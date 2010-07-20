package com.gentics.cr.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;

import com.gentics.cr.CRConfig;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.lucene.LuceneVersion;
import com.gentics.cr.lucene.indexaccessor.IndexAccessor;
import com.gentics.cr.lucene.indexer.index.LuceneAnalyzerFactory;
import com.gentics.cr.lucene.indexer.index.LuceneIndexLocation;
/**
 * 
 * Last changed: $Date: 2010-04-01 15:25:54 +0200 (Do, 01 Apr 2010) $
 * @version $Revision: 545 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class CRSearcher {
	
	protected static Logger log = Logger.getLogger(CRSearcher.class);
	protected static Logger log_explain = Logger.getLogger(CRSearcher.class);
	
	protected static final String INDEX_LOCATION_KEY = "indexLocation";
	protected static final String COMPUTE_SCORES_KEY = "computescores";
	protected static final String STEMMING_KEY = "STEMMING";
	protected static final String STEMMER_NAME_KEY = "STEMMERNAME";
	
	protected CRConfig config;
	private boolean computescores = true;
	
	/**
	 * Create new instance of CRSearcher
	 * @param config
	 */
	public CRSearcher(CRConfig config) {
		this.config = config;
		String s_cs = config.getString(COMPUTE_SCORES_KEY);
		if(s_cs!=null && !"".equals(s_cs))
		{
			computescores = Boolean.parseBoolean(s_cs);
		}
	}
	
	
	public HashMap<String,Object> search(String query,String[] searchedAttributes,int count,int start,boolean explain) throws IOException{
		return search(query,searchedAttributes,count,start,explain,null);
	}
	
	/**
	 * Creates a Sort object for the Sort collector. The general syntax for sort properties is [property][:asc|:desc] where the postfix determines the sortorder. If neither :asc nor :desc is given, the sorting will be done ascending for this property.
	 * @param sorting
	 * @return
	 */
	private Sort createSort(String[] sorting)
	{
		Sort ret = null;
		ArrayList<SortField> sf = new ArrayList<SortField>();
		for(String s:sorting)
		{
			// split attribute on :. First element is attribute name the
			// second is the direction
			String[] sort = s.split(":");

			if (sort[0] != null) {
				boolean reverse;
				if ("desc".equals(sort[1].toLowerCase())) {
					reverse = true;
				} else {
					reverse = false;
				}
				sf.add(new SortField(sort[0],Locale.getDefault(),reverse));
			}
			
		}
		ret = new Sort(sf.toArray(new SortField[]{}));
		
		return ret;
	}
	
	/**
	 * Create the appropriate collector
	 * @param hits
	 * @param sorting
	 * @return
	 * @throws IOException 
	 */
	private TopDocsCollector<?> createCollector(int hits, String[] sorting, boolean computescores) throws IOException
	{
		TopDocsCollector<?> coll = null;
		
		if(sorting!=null)
		{
			coll=TopFieldCollector.create(createSort(sorting), hits, true, computescores, computescores, computescores);
		}
		
		if(coll==null)
		{
			coll=TopScoreDocCollector.create(hits, true);
		}
		
		return coll;
	}
	
	/**
	 * Search in lucene index
	 * @param query query string
	 * @param searchedAttributes
	 * @param count - max number of results that are to be returned
	 * @param start - the start number of the page e.g. if start = 50 and count = 10 you will get the elements 50 - 60
	 * @param explain - if set to true the searcher will add extra explain output to the logger com.gentics.cr.lucene.searchCRSearcher.explain
	 * @param sorting - this argument takes the sorting array that can look like this: ["contentid:asc","name:desc"]
	 * @return HashMap<String,Object with two entries. Entry "query" contains the paresed query and entry "result" contains a Collection of result documents.
	 * @throws IOException 
	 */
	public HashMap<String,Object> search(String query,String[] searchedAttributes,int count,int start,boolean explain,String[] sorting) throws IOException{
		
			
		Searcher searcher;
		Analyzer analyzer;
		//Collect count+start hits
		int hits = count+start;
		TopDocsCollector<?> collector = createCollector(hits,sorting,computescores);
	
		LuceneIndexLocation idsLocation = LuceneIndexLocation.getIndexLocation(this.config);
		
		IndexAccessor indexAccessor = idsLocation.getAccessor();
		searcher = indexAccessor.getPrioritizedSearcher();
		HashMap<String,Object> result = null;
		try {	
			
			analyzer = LuceneAnalyzerFactory.createAnalyzer((GenericConfiguration)this.config);
						
			if(searchedAttributes!=null && searchedAttributes.length>0)
			{
				QueryParser parser = new QueryParser(LuceneVersion.getVersion(),searchedAttributes[0], analyzer);
				parser.setAllowLeadingWildcard(true);
				query = replaceBooleanMnoGoSearchQuery(query);
				
				if (searchedAttributes.length > 1) {
					query = addMultipleSearchedAttributes(query,searchedAttributes);
				}
				Query parsedQuery = parser.parse(query);
				result = new HashMap<String,Object>(2);
				result.put("query", parsedQuery);
				LinkedHashMap<Document,Float> coll = runSearch(collector,searcher,parsedQuery,explain,count,start);
				result.put("result", coll);
				result.put("hits", collector.getTotalHits());
				int size=0;
				if(coll!=null)size=coll.size();
				log.debug("Fetched "+size+" objects with query: "+query);
				
			}
			
		} catch (Exception e) {
			log.error("Error getting the results.",e);
			result=null;
		}
		finally{
			indexAccessor.release(searcher);
		}
		return(result);
	}
	
	/**
	 * parse given query and prepare it to search in multiple attributes with lucene, only words are replaced that are not one of AND, OR, NOT and do not contain a ":" char.
	 * @param query String with query to parse
	 * @param searchedAttributes Array of attributes to search in.
	 * @return parsed query, in case no searchedAttributes are given the original query is given back.
	 */
	private String addMultipleSearchedAttributes(String query,
			String[] searchedAttributes) {
		StringBuffer new_query;
		String replacement = "";
		String seperatorCharacterClass = " \\(\\)";
		for(String attribute:searchedAttributes) {
			if(replacement.length()>0)replacement += " OR ";
			replacement += attribute+":$2";
		}
		if(replacement.length()>0){
			replacement = "("+replacement+")";
			Pattern valuePattern = Pattern.compile("(["+seperatorCharacterClass+"]*)([^"+seperatorCharacterClass+"]+)(["+seperatorCharacterClass+"]*)");
			Matcher valueMatcher = valuePattern.matcher(query);
			new_query = new StringBuffer();
			while(valueMatcher.find()){
				String charsBeforeValue = valueMatcher.group(1);
				String value = valueMatcher.group(2);
				String charsAfterValue = valueMatcher.group(3);
				if(!"AND".equalsIgnoreCase(value)
					&& !"OR".equalsIgnoreCase(value)
					&& !"NOT".equalsIgnoreCase(value)
					&& !value.contains(":")
				) {
					valueMatcher.appendReplacement(new_query, charsBeforeValue+replacement+charsAfterValue);
				}
			}
			valueMatcher.appendTail(new_query);
		} else {
			return query;
		}
		return new_query.toString();
	}

	/**
	 * Helper method to replace search parameters from boolean mnoGoSearch query into their lucene compatible parameters
	 * @param mnoGoSearchQuery
	 * @return
	 */
	
	private String replaceBooleanMnoGoSearchQuery(String mnoGoSearchQuery){
		String luceneQuery = mnoGoSearchQuery.replace("|", "OR").replace("&", "AND").replace('\'', '"');
		luceneQuery = luceneQuery.replaceAll(" ~([a-zA-Z0-9üöäÜÖÄß]+)", " NOT $1");
		return luceneQuery;
	}

	/**
	 * Run a Search against the lucene index
	 * @param searcher
	 * @param parsedQuery
	 * @param count
	 * @return ArrayList of results
	 */
	private LinkedHashMap<Document,Float> runSearch(TopDocsCollector<?> collector, Searcher searcher, Query parsedQuery,boolean explain,int count, int start) {
		try {
		    
			
			
		    searcher.search(parsedQuery, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    LinkedHashMap<Document,Float> result = new LinkedHashMap<Document,Float>(hits.length);
		    
		    //Calculate the number of documents to be fetched
		    int num = Math.min(hits.length - start, count);
		    for(int i = 0 ; i < num ; i++) {
		    	Document doc = searcher.doc(hits[start+i].doc);
		    	result.put(doc,hits[start+i].score);
		    	if(explain)
		    	{
		    		Explanation ex = searcher.explain(parsedQuery, hits[start+i].doc);
		    		log_explain.debug("Explanation for "+doc.toString()+" - "+ex.toString());
		    	}
			}
		    log.debug("Fetched Document "+start+" to "+(start+num)+" of "+collector.getTotalHits()+" found Documents");
			
			return(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return(null);
	}
	
	public void finalize()
	{
		LuceneIndexLocation idsLocation = LuceneIndexLocation.getIndexLocation(this.config);
		idsLocation.finalize();
	}
}