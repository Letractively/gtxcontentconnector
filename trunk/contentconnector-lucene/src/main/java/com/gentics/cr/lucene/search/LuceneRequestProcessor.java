package com.gentics.cr.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import com.gentics.cr.CRConfig;
import com.gentics.cr.CRError;
import com.gentics.cr.CRRequest;
import com.gentics.cr.CRResolvableBean;
import com.gentics.cr.RequestProcessor;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.exceptions.CRException;
import com.gentics.cr.lucene.LuceneVersion;
import com.gentics.cr.lucene.indexer.index.LuceneAnalyzerFactory;
import com.gentics.cr.lucene.search.highlight.ContentHighlighter;
/**
 * 
 * Last changed: $Date: 2010-04-01 15:25:54 +0200 (Do, 01 Apr 2010) $
 * @version $Revision: 545 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class LuceneRequestProcessor extends RequestProcessor {

  protected static Logger log = Logger.getLogger(LuceneRequestProcessor.class);
  protected static Logger ext_log = Logger.getLogger(LuceneRequestProcessor.class);
  private CRSearcher searcher = null;
  protected String name=null;
  
  private boolean getStoredAttributes = false;
  
  private static final String SCORE_ATTRIBUTE_KEY = "SCOREATTRIBUTE";
  private static final String GET_STORED_ATTRIBUTE_KEY = "GETSTOREDATTRIBUTES";
  
  private Hashtable<String,ContentHighlighter> highlighters;
  
  
  /**
   * Create new instance of LuceneRequestProcessor
   * @param config
   * @throws CRException
   */
  public LuceneRequestProcessor(CRConfig config) throws CRException {
    super(config);
    this.name=config.getName();
    this.searcher = new CRSearcher(config);
    getStoredAttributes = Boolean.parseBoolean((String)config.get(GET_STORED_ATTRIBUTE_KEY));
    highlighters = ContentHighlighter.getTransformerTable((GenericConfiguration)config);
  }
  
  private static final String SEARCH_COUNT_KEY = "SEARCHCOUNT";
  
  private static final String ID_ATTRIBUTE_KEY = "idAttribute";
  
  
  
  /**
   * Key where to find the total hits of the search in the metaresolvable.
   * Metaresolvable has to be enabled => LuceneRequestProcessor.META_RESOLVABLE_KEY
   */
  public static final String META_HITS_KEY = "totalhits";
  
  /**
   * Key where to find the start position of the search in the metaresolvable.
   * Metaresolvable has to be enabled => LuceneRequestProcessor.META_RESOLVABLE_KEY
   */
  public static final String META_START_KEY = "start";
  
  /**
   * TODO
   */
  public static final String META_COUNT_KEY = "count";
  
  /**
   * TODO
   */
  public static final String META_QUERY_KEY = "query";
  
  
  /**
   * Key where to find the query used for highlighting the content. Usually this is the 
   * searchqery without the permissions and meta search informations.
   * If this is not set, the requestFilter (default query) will be used
   */
  public static final String HIGHLIGHT_QUERY_KEY = "highlightquery";
  
  
  @SuppressWarnings("unchecked")
  private static List<Field> toFieldList(List l)
  {
    return((List<Field>) l);
  }
  
  /**
   * This returns a collection of CRResolvableBeans containing the IDATTRIBUTE and all STORED ATTRIBUTES of the Lucene Documents
   * 
   * @param request - CRRequest containing the query in RequestFilter
   * @param doNavigation - if set to true there will be generated explanation output to the explanation logger of CRSearcher
   * @return search result as Collection of CRResolvableBean
   * @throws CRException 
   */
  public Collection<CRResolvableBean> getObjects(CRRequest request,
      boolean doNavigation) throws CRException {
    ArrayList<CRResolvableBean> result = new ArrayList<CRResolvableBean>();
    int count = request.getCount();
    int start = request.getStart();
    //IF COUNT IS NOT SET IN THE REQUEST, USE DEFAULT VALUE LOADED FROM CONFIG
    if(count<=0)
    {  
      String cstring = (String)this.config.get(SEARCH_COUNT_KEY);
      if(cstring!=null)count=new Integer(cstring);
    }
    if(count<=0){
      String message="Default count is lower or equal to 0! This will result in an error. Overthink your config (insert rp.<number>.searchcount=<value> in your properties file)!"; 
      log.error(message);
      throw new CRException(new CRError("Error", message));
    }
    if(start<0){
      String message = "Bad request: start is lower than 0!";
      log.error(message);
      throw new CRException(new CRError("Error", message));
    }
    
    
    String scoreAttribute = (String)config.get(SCORE_ATTRIBUTE_KEY);
    //GET RESULT
    long s1 = System.currentTimeMillis();
    HashMap<String, Object> searchResult  = null;
    try {
      searchResult = this.searcher.search(request.getRequestFilter(),
        getSearchedAttributes(), count, start, doNavigation,
        request.getSortArray(), request);
    } catch (IOException ex) {
      ex.printStackTrace();
      throw new CRException(ex);
    }
    long e1 = System.currentTimeMillis();
    log.debug("Search in Index took " + (e1 - s1) + "ms");
    if (searchResult != null) {
      Query parsedQuery = (Query) searchResult.get("query");

      Object metaKey = request.get(META_RESOLVABLE_KEY);
      if (metaKey != null && (Boolean) metaKey) {
        CRResolvableBean metaBean = new CRResolvableBean();
        metaBean.set(META_HITS_KEY, searchResult.get("hits"));
        metaBean.set(META_START_KEY, start);
        metaBean.set(META_COUNT_KEY, count);
        metaBean.set(META_QUERY_KEY, request.getRequestFilter());
        result.add(metaBean);
      }
      LinkedHashMap<Document, Float> docs =
        objectToLinkedHashMapDocuments(searchResult.get("result"));
      //PARSE HIGHLIGHT QUERY
      Object highlightQuery = request.get(HIGHLIGHT_QUERY_KEY);
      if (highlightQuery != null) {
        Analyzer analyzer = LuceneAnalyzerFactory.createAnalyzer((GenericConfiguration)this.config);
        QueryParser parser = new QueryParser(LuceneVersion.getVersion(),getSearchedAttributes()[0], analyzer);
        try {
          parsedQuery = parser.parse((String)highlightQuery);
        } catch (ParseException e) {
          log.error(e.getMessage());
          e.printStackTrace();
        }
      }
      if (docs != null) {
        String idAttribute = (String) this.config.get(ID_ATTRIBUTE_KEY);
        for (Entry<Document, Float> e : docs.entrySet()) {
          Document doc = e.getKey();
          Float score = e.getValue();
          CRResolvableBean crBean = new CRResolvableBean(doc.get(idAttribute));
          if (getStoredAttributes) {
            for(Field f:toFieldList(doc.getFields()))
            {
              if(f.isStored())
              {
                if(f.isBinary())
                {
                  crBean.set(f.name(), f.getBinaryValue());
                }
                else
                {
                  crBean.set(f.name(), f.stringValue());
                }
              }
            }
          }
          
          if(scoreAttribute!=null && !"".equals(scoreAttribute))
          {
            crBean.set(scoreAttribute, score);
          }
          //IF HIGHLIGHTERS ARE CONFIGURED => DO HIGHLIGHTNING
          if(highlighters!=null)
          {
            long s2 = System.currentTimeMillis();
            for(Entry<String,ContentHighlighter> ch:highlighters.entrySet())
            {
              ContentHighlighter h = ch.getValue();
              String att = ch.getKey();
              //IF crBean matches the highlighters rule => highlight
              if(h.match(crBean))
              { 
                String ret = h.highlight((String)crBean.get(att), parsedQuery);
                crBean.set(att,ret);
              }
            }
            long e2 = System.currentTimeMillis();
            log.debug("Highlighters took "+(e2-s2)+"ms");
          }
          ext_log.debug("Found "+crBean.getContentid()+" with score "+score.toString());
          result.add(crBean);
        }
      }
      /*if(doNavigation)
      {
        //NOT IMPLEMENTED YET, BUT WE DO GENERATE MORE EXPLANATION OUTPUT YET
        //log.error("LUCENEREQUESTPROCESSER CAN NOT YET RETURN A TREE STRUCTURE");
      }*/
    }
    return result;
  }


  /**
   * TODO javadoc.
   * @param obj TODO javadoc
   * @return TODO javadoc
   */
  @SuppressWarnings("unchecked")
  private LinkedHashMap<Document, Float> objectToLinkedHashMapDocuments(
      final Object obj) {
    return (LinkedHashMap<Document, Float>) obj;
  }

  private static final String SEARCHED_ATTRIBUTES_KEY = "searchedAttributes";

  private String[] getSearchedAttributes()
  {
    String sa = (String)this.config.get(SEARCHED_ATTRIBUTES_KEY);
    String[] ret=null;
    if(sa!=null)
    {
      ret = sa.split(",");
    }
    return ret;
  }

  @Override
  public void finalize() {
    if(this.searcher!=null)this.searcher.finalize();
  }

  
}
