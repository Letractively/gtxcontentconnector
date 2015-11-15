# Important Changes #
  * Removed WrappedSnowballAnalyzer as this will no longer be supported by Lucene. Use the language specific analyzers provided by lucene instead.

# Configuration changes #

# Features #
  * Now using Lucene Version 3.1 (Released in March 2011)
  * The DYM-Provider (Did you Mean index) now checks for an existing lock on startup (when it has not been ended with a clean shut down) and tries to remove it.
  * The Autocompleter now checks for an existing lock on startup (when it has not been ended with a clean shut down) and tries to remove it.
  * The indexer servlet can now display special indexes, such as DYM and autocomplete.
  * Now the locking state is displayed for each index.
  * A CustomPatternAnalyzer has been added. This analyzer splits on a configured pattern. analyzer.properties
```
TAGS.analyzerclass=com.gentics.cr.lucene.analysis.CustomPatternAnalyzer
TAGS.fieldname=tags
TAGS.pattern=someregex
```

# Bugfixes #
  * fixed backwards compatibility with Java 1.5
  * fixed indexing problems with SQLRequestProcessor when using views with extra column labels
  * Windows (CP-1252) encoded powerpoint files containing special characters will now correctly be indexed.


