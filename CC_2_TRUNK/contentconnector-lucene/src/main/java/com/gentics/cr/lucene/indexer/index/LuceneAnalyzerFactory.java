package com.gentics.cr.lucene.indexer.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import com.gentics.cr.CRConfigFileLoader;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.lucene.LuceneVersion;
import com.gentics.cr.lucene.analysis.ReverseAnalyzer;
import com.gentics.cr.lucene.indexer.IndexerUtil;

/**
 * TODO javadoc.
 * Last changed: $Date: 2009-07-10 10:49:03 +0200 (Fr, 10 Jul 2009) $
 * @version $Revision: 131 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class LuceneAnalyzerFactory {
	/**
	 * Log4j Logger for error and debug messages.
	 */
	protected static final Logger LOGGER =
		Logger.getLogger(LuceneAnalyzerFactory.class);
	private static final String STEMMING_KEY = "STEMMING";
	private static final String STEMMER_NAME_KEY = "STEMMERNAME";
	private static final String STOP_WORD_FILE_KEY = "STOPWORDFILE";
	private static final String ANALYZER_CONFIG_KEY = "ANALYZERCONFIG";
	private static final String ANALYZER_CLASS_KEY = "ANALYZERCLASS";
	private static final String FIELD_NAME_KEY = "FIELDNAME";
	private static final String REVERSE_ATTRIBUTES_KEY="REVERSEATTRIBUTES";

	public static final String REVERSE_ATTRIBUTE_SUFFIX="_REVERSE";

	/**
	 * TODO javadoc.
	 * @param config TODO javadoc
	 * @return TODO javadoc
	 */
	public static List<String> getReverseAttributes(
			final GenericConfiguration config) {
		GenericConfiguration aconfig = loadAnalyzerConfig(config);
		if (aconfig != null) {
			String reverseAttributeString =
				(String) aconfig.get(REVERSE_ATTRIBUTES_KEY);
			return IndexerUtil.getListFromString(reverseAttributeString, ",");
		}
		return null;
	}

	/**
	 * Creates an analyzer from the given config.
	 * @param config TODO javadoc
	 * @return TODO javadoc
	 */
	public static Analyzer createAnalyzer(final GenericConfiguration config) {
		PerFieldAnalyzerWrapper analyzerWrapper =
			new PerFieldAnalyzerWrapper(createDefaultAnalyzer(config));
		//TODO Cache analyzer instances and do not read file each time
		//Load analyzer config
		GenericConfiguration analyzerConfig = loadAnalyzerConfig(config);
		if (analyzerConfig != null) {
			ArrayList<String> addedRattributes = new ArrayList<String>();
			List<String> reverseAttributes = getReverseAttributes(config);
			Map<String, GenericConfiguration> subconfigs =
				analyzerConfig.getSortedSubconfigs();
			if (subconfigs != null) {
				for (Map.Entry<String, GenericConfiguration> e : subconfigs.entrySet())
				{
					GenericConfiguration analyzerconfig = e.getValue();
					String fieldname = analyzerconfig.getString(FIELD_NAME_KEY);
					String analyzerclass = analyzerconfig.getString(ANALYZER_CLASS_KEY);
					Analyzer a = createAnalyzer(analyzerclass, analyzerconfig);
					analyzerWrapper.addAnalyzer(fieldname, a);
					//ADD REVERSE ANALYZERS
					if (reverseAttributes != null
							&& reverseAttributes.contains(fieldname)) {
						addedRattributes.add(fieldname);
						analyzerWrapper.addAnalyzer(fieldname + REVERSE_ATTRIBUTE_SUFFIX,
								new ReverseAnalyzer(a));
					}
				}
			}
			//ADD ALL NON CONFIGURED REVERSE ANALYZERS
			if (reverseAttributes != null && reverseAttributes.size() > 0) {
				for (String att : reverseAttributes) {
					if (!addedRattributes.contains(att)) {
						analyzerWrapper.addAnalyzer(att + REVERSE_ATTRIBUTE_SUFFIX,
								new ReverseAnalyzer(null));
					}
				}
			}
		}
		return analyzerWrapper;
	}

	/**
	 * TODO javadoc.
	 * @param config TODO javadoc
	 * @return TODO javadoc
	 */
	private static GenericConfiguration loadAnalyzerConfig(
			final GenericConfiguration config) {
		GenericConfiguration analyzerConfig = null;
		String confpath = (String) config.get(ANALYZER_CONFIG_KEY);
		if (confpath != null) {
			analyzerConfig = new GenericConfiguration();
			try {
				CRConfigFileLoader.loadConfiguration(analyzerConfig, confpath, null);
			} catch (IOException e) {
				LOGGER.error("Could not load analyzer configuration from " + confpath, e);
			}
		}
		return analyzerConfig;
	}

	/**
	 * TODO javadoc.
	 * @param analyzerclass TODO javadoc
	 * @param config TODO javadoc
	 * @return TODO javadoc
	 */
	private static Analyzer createAnalyzer(final String analyzerclass,
			final GenericConfiguration config) {
		Analyzer a = null;
		try {
			//First try to create an Analyzer that takes a config object
			a = (Analyzer) Class.forName(analyzerclass).getConstructor(
					new Class[]{GenericConfiguration.class}).newInstance(config);
		} catch (Exception e1) {
			try {
				//IF FIRST FAILS TRY SIMPLE CONSTRUCTOR
				a = (Analyzer) Class.forName(analyzerclass).getConstructor()
					.newInstance();
			} catch (Exception e2) {
				//IF SIMPLE FAILS, PROBABLY DID NOT FIND CONSTRUCTOR,
				//TRYING WITH VERSION ADDED
				try {
					a = (Analyzer) Class.forName(analyzerclass).getConstructor(
							new Class[]{Version.class}).newInstance(
									LuceneVersion.getVersion());
				} catch (Exception e3) {
					LOGGER.error("Could not instantiate Analyzer with class "
							+ analyzerclass, e3);
				}
			}
		}
		return a;
	}

	/**
	 * TODO javadoc.
	 * @param config TODO javadoc
	 * @return TODO javadoc
	 */
	private static Analyzer createDefaultAnalyzer(
			final GenericConfiguration config) {
		//Update/add Documents
		Analyzer analyzer;
		boolean doStemming =
			Boolean.parseBoolean((String) config.get(STEMMING_KEY));
		if (doStemming) {
			analyzer = new SnowballAnalyzer(LuceneVersion.getVersion(),
					(String) config.get(STEMMER_NAME_KEY));
		} else {
			//Load StopWordList
			File stopWordFile = IndexerUtil.getFileFromPath(
					(String) config.get(STOP_WORD_FILE_KEY));
			if (stopWordFile != null) {
				//initialize Analyzer with stop words
				try {
					analyzer =
						new StandardAnalyzer(LuceneVersion.getVersion(), stopWordFile);
				} catch (IOException ex) {
					LOGGER.error("Could not open stop words file. Will create standard "
							+ "analyzer.", ex);
					analyzer = new StandardAnalyzer(LuceneVersion.getVersion());
				}
			} else {
				//if no stop word list exists load fall back
				analyzer = new StandardAnalyzer(LuceneVersion.getVersion());
			}
		}
		return analyzer;
	}
}
