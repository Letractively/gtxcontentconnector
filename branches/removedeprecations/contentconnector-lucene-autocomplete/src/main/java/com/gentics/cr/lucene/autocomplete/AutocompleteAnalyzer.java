package com.gentics.cr.lucene.autocomplete;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;

import com.gentics.cr.lucene.LuceneVersion;

public class AutocompleteAnalyzer extends Analyzer {

	private static final String[] ENGLISH_STOP_WORDS = { "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "i", "if", "in",
			"into", "is", "no", "not", "of", "on", "or", "s", "such", "t", "that", "the", "their", "then", "there", "these", "they",
			"this", "to", "was", "will", "with" };

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new StandardTokenizer(LuceneVersion.getVersion(), reader);
		result = new StandardFilter(LuceneVersion.getVersion(), result);
		result = new LowerCaseFilter(LuceneVersion.getVersion(), result);
		result = new ASCIIFoldingFilter(result);
		Collection<String> list = Arrays.asList(ENGLISH_STOP_WORDS);
		CharArraySet set = new CharArraySet(LuceneVersion.getVersion(), list, false);
		result = new StopFilter(LuceneVersion.getVersion(), result, set);
		result = new EdgeNGramTokenFilter(result, Side.FRONT, 1, 20);
		return result;
	}

}
