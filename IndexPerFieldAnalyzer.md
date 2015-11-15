This article describes how an analyzer can be configured per field in the index

# Introduction #

In order to enable this feature you have to add the following line to your indexer/searcher configuration. You have to specify a configuration file where each field that you want to customize is configured.

`index.1.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties`


# Default Behavior #

For each field that is not configured in the analyzer configuration file, a org.apache.lucene.analysis.standard.StandardAnalyzer will be used.

# Configuration file #

The configuration syntax is as follows:
```
<unique_name>.analyzerclass=<analyzerclass>
<unique_name>.fieldname=<fieldname>
```

for example:
```
name.analyzerclass=com.gentics.cr.test.lucene.analysis.TestAnalyzer
name.fieldname=name
```

You can specify more than those parameters. They will be available in the analyzer if the analyzer supports a GenericConfiguration object.

# Writing your own analyzer #

You can easily write and configure your own analyzer. Here we will show you, how to wrap the Snowball stemmer in order to use a configured language.

```
package com.gentics.cr.lucene.analysis;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;

import com.gentics.cr.configuration.GenericConfiguration;

public class WrappedSnowballAnalyzer extends SnowballAnalyzer {
	private static final String STEMMER_NAME_KEY = "stemmername";
	/**
	 * Creates a SnowballAnalyzer with the configured stemmer name
	 * @param config 
	 */
	public WrappedSnowballAnalyzer(GenericConfiguration config) {
		super(Version.LUCENE_CURRENT, config.getString(STEMMER_NAME_KEY));
	}

}

```

The configuration for this analyzer would look like this:

```
content.analyzerclass=com.gentics.cr.lucene.analysis.WrappedSnowballAnalyzer
content.fieldname=content
content.stemmername=English
```