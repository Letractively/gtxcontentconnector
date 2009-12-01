package com.gentics.cr.lucene.indexaccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;

class WarmingIndexAccessor extends DefaultIndexAccessor {

  private Query warmQuery;
  // look into making a Set again
  private List<IndexSearcher> retiredSearchers;
    
  /**
   * Create new instance
   * @param dir
   * @param analyzer
   * @param warmQuery
   */
  public WarmingIndexAccessor(Directory dir, Analyzer analyzer, Query warmQuery) {
	  super(dir, analyzer);

	    this.warmQuery = warmQuery;
	    retiredSearchers = new ArrayList<IndexSearcher>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.mhs.indexaccessor.IndexAccessor#close()
   */
  public synchronized void close() {

    if (closed) {
      return;
    }
    closed = true;
    while ((readingReaderUseCount > 0) || (searcherUseCount > 0) || (writingReaderUseCount > 0)
        || (writerUseCount > 0) || (numReopening > 0)) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }

    closeCachedReadingReader();
    closeCachedWritingReader();
    closeCachedWriter();

    // System.out.println("wait for warming s's:" + numSearchersForRetirment);

    retireSearchers();
    while (numSearchersForRetirment > 0) {

      try {
        wait();
      } catch (InterruptedException e) {
      }
      retireSearchers();
    }

    closeCachedSearchers();
    shutdownAndAwaitTermination(pool);

    // int i = 0;
    // for (IndexSearcher s : createdSearchers) {
    // System.out.println(i++ + ":" + s.getIndexReader().refCount + " :" +
    // s.getIndexReader());
    // }
  }

  /**
   * Reopens all of the Searchers in the Searcher cache. This method is invoked
   * in a synchronized context.
   */
  protected void reopenCachedSearchers() {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("reopening cached searchers (" + cachedSearchers.size() + "):"
          + Thread.currentThread().getId());
    }
    Set<Similarity> keys = cachedSearchers.keySet();
    for (Similarity key : keys) {
      IndexSearcher searcher = cachedSearchers.get(key);
      try {
        IndexReader oldReader = searcher.getIndexReader();
        IndexSearcher oldSearcher = searcher;
        IndexReader newReader = oldReader.reopen();

        if (newReader != oldReader) {

          retireSearchers();
          IndexSearcher newSearcher = new IndexSearcher(newReader);
          createdSearchers.add(newSearcher);
          newSearcher.setSimilarity(oldSearcher.getSimilarity());
          SearcherWarmer warmer = new SearcherWarmer(oldSearcher, newSearcher);
          numSearchersForRetirment++;
          numSearchersForRetirment++;
          pool.execute(warmer);

        }

      } catch (IOException e) {
        logger.log(Level.SEVERE, "error reopening cached Searcher", e);
      }
    }

  }

  private void retireSearchers() {

    Iterator<IndexSearcher> it = retiredSearchers.iterator();

    while (it.hasNext()) {

      IndexSearcher s = it.next();
      if (logger.isLoggable(Level.FINE)) {
        logger.fine("closing retired searcher:" + s.getIndexReader());
      }

      try {
        s.getIndexReader().close();
      } catch (IOException e) {
        logger.log(Level.SEVERE, "error closing cached Searcher", e);
      }
      numSearchersForRetirment--;
    }
    retiredSearchers.clear();

  }

  /**
   * 
   * Last changed: $Date: 2009-09-02 17:57:48 +0200 (Mi, 02 Sep 2009) $
   * @version $Revision: 180 $
   * @author $Author: supnig@constantinopel.at $
   *
   */
  public class SearcherWarmer implements Runnable {
    private IndexSearcher searcher;
    private IndexSearcher oldSearcher;
    /**
     * Create new instance
     * @param oldSearcher
     * @param searcher
     */
    public SearcherWarmer(IndexSearcher oldSearcher, IndexSearcher searcher) {
      this.searcher = searcher;
      this.oldSearcher = oldSearcher;

    }

    public void run() {
      if (logger.isLoggable(Level.FINE)) {
        logger.fine("warming up searcher...");
      }
      try {
        
          searcher.search(warmQuery,new HitCollector() {
              public void collect(int doc, float score) {
                  //do nothing
                }
              });
        
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      if (logger.isLoggable(Level.FINE)) {
        logger.fine("warming done");
      }

      synchronized (WarmingIndexAccessor.this) {
        retiredSearchers.add(cachedSearchers.put(searcher.getSimilarity(), searcher));
        retiredSearchers.add(oldSearcher);

        WarmingIndexAccessor.this.notifyAll();
      }
    }
  }

}
