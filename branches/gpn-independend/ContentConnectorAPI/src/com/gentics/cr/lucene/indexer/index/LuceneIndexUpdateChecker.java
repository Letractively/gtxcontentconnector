package com.gentics.cr.lucene.indexer.index;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

import com.gentics.api.lib.resolving.Resolvable;
import com.gentics.cr.lucene.indexaccessor.IndexAccessor;
import com.gentics.cr.util.indexing.IndexUpdateChecker;

/**
 * 
 * Lucene Implementation of IndexUpdateChecker
 * Walks an Index and compares Identifyer/Timestamp pairs to the Objects in the Index
 * 
 * Last changed: $Date: 2009-09-02 17:57:48 +0200 (Mi, 02 Sep 2009) $
 * @version $Revision: 180 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class LuceneIndexUpdateChecker extends IndexUpdateChecker{

	LuceneIndexLocation indexLocation;
	IndexReader reader;
	IndexAccessor indexAccessor;
	LinkedHashMap<String,Integer> docs;
	Iterator<String> docIT;
	/**
	 * Initializes the Lucene Implementation of {@link IndexUpdateChecker}.
	 * @param indexLocation
	 * @param termKey - Key under wich the termValue is stored in the Index e.g. CRID
	 * @param termValue - Value wich to use for iteration e.g. CRID_1
	 * @param idAttribute - ID-Attribute key that will be used for Identifyer comparison. This has to represent the field where the identifyer in the method {@link #checkUpToDate(String, int)} is present.
	 * @throws IOException 
	 */
	public LuceneIndexUpdateChecker(LuceneIndexLocation indexLocation,String termKey, String termValue,String idAttribute) throws IOException
	{
		this.indexLocation = indexLocation;
		indexAccessor = indexLocation.getAccessor();
		reader = indexAccessor.getReader(true);
		
		TermDocs termDocs = reader.termDocs(new Term(termKey,termValue));
		
		docs = fetchSortedDocs(termDocs, reader, idAttribute);
		docIT = docs.keySet().iterator();
		
		//TODO CONTINUE HERE PREPARE TO USE ITERATOR IN CHECK METHOD
		
	}
	
	@Override
	protected boolean checkUpToDate(String identifyer, int timestamp, Resolvable object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteStaleObjects() {
		// TODO Auto-generated method stub
		
	}
	
	private LinkedHashMap<String,Integer> fetchSortedDocs(TermDocs termDocs, IndexReader reader, String idAttribute) throws IOException
	{
		LinkedHashMap<String,Integer> tmp = new LinkedHashMap<String,Integer>();
		boolean finish=!termDocs.next();
		
		while(!finish)
		{
			Document doc = reader.document(termDocs.doc());
			String docID = doc.get(idAttribute);
			tmp.put(docID, termDocs.doc());
			finish=!termDocs.next();
		}
		
		LinkedHashMap<String,Integer> ret = new LinkedHashMap<String,Integer>(tmp.size());
		Vector<String> v = new Vector<String>(tmp.keySet());
		Collections.sort(v);
		for(String id:v)
		{
			ret.put(id, tmp.get(id));
		}
		return ret;
	}

}