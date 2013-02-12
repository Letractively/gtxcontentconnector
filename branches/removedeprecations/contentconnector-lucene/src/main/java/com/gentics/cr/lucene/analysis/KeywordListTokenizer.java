package com.gentics.cr.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

/**
 * Emits the entire input as a single token.
 */
public final class KeywordListTokenizer extends CharTokenizer {

	/**
	 * Constructor.
	 * @param input input
	 */
	public KeywordListTokenizer(final Version version, final Reader input) {
		super(version, input);
	}

	/**
	 * Constructor.
	 * @param source source
	 * @param input input
	 */
	public KeywordListTokenizer(final Version version, final AttributeSource source, final Reader input) {
		super(version, source, input);
	}

	/**
	 * Constructor.
	 * @param factory factory.
	 * @param input input
	 */
	public KeywordListTokenizer(final Version version, final AttributeFactory factory, final Reader input) {
		super(version, factory, input);
	}

	@Override
	protected boolean isTokenChar(final int c) {
		return !isSeparator(c);
	}

	/**
	 * isSeperator.
	 * @param c character
	 * @return true if seperator.
	 */
	private boolean isSeparator(final int c) {
		return '\n' == ((char) c);
	}
}
