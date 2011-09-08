package com.gentis.cr.lucene.search.query.mocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class SimpleLucene {

	public static final String CONTENT_ATTRIBUTE = "content";

	Version luceneVersion = Version.LUCENE_31;
	
	IndexSearcher searcher;
	
	
	RAMDirectory index;
	
	public SimpleLucene() throws CorruptIndexException, IOException {
		index = new RAMDirectory();
	}
	
	public void add(Document document) throws CorruptIndexException, IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(luceneVersion, new StandardAnalyzer(luceneVersion));
		IndexWriter writer = new IndexWriter(index, indexWriterConfig);
		writer.addDocument(document);
		writer.optimize();
		writer.close();
	}
	
	public Document add(String text) throws CorruptIndexException, IOException {
		Document document = new Document();
		document.add(new Field(SimpleLucene.CONTENT_ATTRIBUTE, text, Field.Store.YES, Field.Index.ANALYZED));
		add(document);
		return document;
	}

	public Document add(Map<String, String> fields) throws CorruptIndexException, IOException {
		Document document = new Document();
		for(String fieldName : fields.keySet()) {
			document.add(new Field(fieldName, fields.get(fieldName), Field.Store.YES, Field.Index.ANALYZED));
		}
		add(document);
		return document;
	}
	
	public Document add(String...fields) throws CorruptIndexException, IOException {
		Document document = new Document();
		for(String field : fields) {
			String name = field.replaceAll(":.*", "");
			String value = field.substring(name.length() + 1);
			document.add(new Field(name, value, Field.Store.YES, Field.Index.ANALYZED));
		}
		add(document);
		return document;
	}
	
	public Collection<Document> find(String luceneQuery) throws ParseException, IOException {
		initSearcher();
		QueryParser queryParser = new QueryParser(luceneVersion, CONTENT_ATTRIBUTE, new StandardAnalyzer(luceneVersion));
		Query query = queryParser.parse(luceneQuery);
		TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
		ArrayList<Document> result = new ArrayList<Document>(); 
		for(ScoreDoc document : hits.scoreDocs) {
			result.add(searcher.doc(document.doc));
		}
		return result;
	}

	private void initSearcher() throws CorruptIndexException, IOException {
		if(searcher == null) {
			searcher = new IndexSearcher(index);
		}
	}


}
