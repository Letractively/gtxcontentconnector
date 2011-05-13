package com.gentics.cr.lucene.didyoumean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.CustomSpellChecker;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.Directory;

import com.gentics.cr.CRConfig;
import com.gentics.cr.CRConfigUtil;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.events.Event;
import com.gentics.cr.events.EventManager;
import com.gentics.cr.events.IEventReceiver;
import com.gentics.cr.lucene.events.IndexingFinishedEvent;
import com.gentics.cr.lucene.indexer.index.LuceneIndexLocation;
import com.gentics.cr.lucene.information.SpecialDirectoryRegistry;

/**
 * This class can be used to build an autocomplete index over an existing lucene index.
 * 
 * Last changed: $Date: 2010-04-01 15:20:21 +0200 (Do, 01 Apr 2010) $
 * @version $Revision: 528 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class DidYouMeanProvider implements IEventReceiver{

  protected static final Logger log = Logger.getLogger(DidYouMeanProvider.class);
  private Directory source;
  private Directory didyoumeanLocation;
  
  
  
  private static final String SOURCE_INDEX_KEY="srcindexlocation";
  
  private static final String DIDYOUMEAN_INDEY_KEY="didyoumeanlocation";
  
  private static final String DIDYOUMEAN_FIELD_KEY="didyoumeanfields";
  
  private static final String DIDYOUMEAN_MIN_DISTANCESCORE="didyoumeanmindistancescore";
  
  private static final String DIDYOUMEAN_MIN_DOCFREQ="didyoumeanmindocfreq";
  
  /**
   * Configuration key to activate the didyoumean feature for terms that are in
   * the index but have a low result size.
   */
  private static final String DIDYOUMEAN_EXISTINGTERMS_KEY =
    "didyoumean_forexisitingterms";
  

  private String didyoumeanfield = "all";
  
  private CustomSpellChecker spellchecker=null;
  
  private boolean all = false;
  
  /**
   * Mark if we should provide the didyoumean feature for existing terms (with
   * low result count).
   */
  private boolean checkForExistingTerms = false;
  
  private Collection<String> dym_fields = null;
  
  public DidYouMeanProvider(CRConfig config)
  {
    GenericConfiguration src_conf = (GenericConfiguration)config.get(SOURCE_INDEX_KEY);
    GenericConfiguration auto_conf = (GenericConfiguration)config.get(DIDYOUMEAN_INDEY_KEY);
    source = LuceneIndexLocation.createDirectory(new CRConfigUtil(src_conf,"SOURCE_INDEX_KEY"));
    didyoumeanLocation = LuceneIndexLocation.createDirectory(new CRConfigUtil(auto_conf,DIDYOUMEAN_INDEY_KEY));
    SpecialDirectoryRegistry.getInstance().register(didyoumeanLocation);
    didyoumeanfield = config.getString(DIDYOUMEAN_FIELD_KEY, didyoumeanfield);

    checkForExistingTerms =
      config.getBoolean(DIDYOUMEAN_EXISTINGTERMS_KEY, checkForExistingTerms);
    
    Float minDScore = config.getFloat(DIDYOUMEAN_MIN_DISTANCESCORE,
    		(float) 0.0);
    Integer minDFreq = config.getInteger(DIDYOUMEAN_MIN_DOCFREQ, 0);
    
    
    //FETCH DYM FIELDS
    if (this.didyoumeanfield.equalsIgnoreCase("ALL")) {
      all = true;
    } else if (this.didyoumeanfield.contains(",")) {
      String[] arr = this.didyoumeanfield.split(",");
      dym_fields = new ArrayList<String>(Arrays.asList(arr));
    } else {
      dym_fields = new ArrayList<String>(1);
      dym_fields.add(this.didyoumeanfield);
    }
    
    try {
    	//CHECK FOR EXISTING LOCK AND REMOVE IT
    	synchronized (this) {
	    	try {
				if (IndexWriter.isLocked(didyoumeanLocation)) {
					IndexWriter.unlock(didyoumeanLocation);
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
    	}
      spellchecker = new CustomSpellChecker(didyoumeanLocation,
    		  minDScore, minDFreq);
      reIndex();
    } catch (IOException e) {
      log.error("Could not create didyoumean index.", e);
    }
    EventManager.getInstance().register(this);
  }
  
  
  public void processEvent(Event event) {
    if(IndexingFinishedEvent.INDEXING_FINISHED_EVENT_TYPE.equals(event.getType())) {
      try {
        reIndex();
      } catch(IOException e) {
        log.error("Could not reindex didyoumean index.", e);
      }
    }
  }
  
  public CustomSpellChecker getInitializedSpellchecker() {
    return this.spellchecker;
  }

  /**
   * TODO javadoc.
   * @param termlist TODO javadoc
   * @param count TODO javadoc
   * @param reader TODO javadoc
   * @return TODO javadoc
   */
  public Map<String,String[]> getSuggestions(Set<Term> termlist,int count,IndexReader reader)
  {
    Map<String,String[]> result = new LinkedHashMap<String,String[]>();
    Set<String> uniquetermset = new HashSet<String>();
    for (Term t : termlist) {
      if(all) {
        uniquetermset.add(t.text());
      } else {
        //ONLY ADD TERM IF IT COMES FROM A DYM FIELD
        if(dym_fields.contains(t.field())) {
          uniquetermset.add(t.text());
        }
      }
    }
    log.debug("Will use the following fields for dym: "+dym_fields.toString());
    for(String term : uniquetermset) {
      try {
        if(checkForExistingTerms || !this.spellchecker.exist(term)) {
          String[] ts = this.spellchecker.suggestSimilar(term, count, reader,
              didyoumeanfield, true);
          if (ts!=null && ts.length>0) {
            result.put(term, ts);
          }
        }
      } catch(IOException ex) {
        log.error("Could not suggest terms",ex);
      }
    }
    return result;
  }

  private void reIndex() throws IOException
  {
    // build a dictionary (from the spell package) 
    log.debug("Starting to reindex didyoumean index.");
    IndexReader sourceReader = IndexReader.open(source);
    Collection<String> fields = null;
    if (all) {
      fields = sourceReader.getFieldNames(IndexReader.FieldOption.ALL);
    } else {
      fields = dym_fields;
    }
    try {
      for (String fieldname : fields) {
        LuceneDictionary dict = new LuceneDictionary(sourceReader, fieldname); 
        spellchecker.indexDictionary(dict);
      }
    } finally {
      sourceReader.close(); 
    }
    log.debug("Finished reindexing didyoumean index.");
  }

  public void finalize() {
	SpecialDirectoryRegistry.getInstance().unregister(didyoumeanLocation);
    EventManager.getInstance().unregister(this);
  }

}
