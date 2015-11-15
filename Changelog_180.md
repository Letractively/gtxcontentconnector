# Important Changes #
  * Now per default no stop words are used by the lucene StandardAnalyzer.

# Configuration changes #

# Features #
  * Added possibility to create custom implementations of the HTTPClientRequestProcessor. Just extend the AbstractHTTPClientRequestProcessor and add custom request parameters.
  * Added com.gentics.cr.lucene.analysis.KeywordListAnalyzer
  * The Did You Mean Index will now be updated when the reopen file of the source index is updated. This can be configured with
```
rp.1.dymreopenupdate=true
```
  * The Autocomplete Index will now be updated when the reopen file of the source index is updated. This can be configured with
```
rp.1.autocompletereopenupdate=true
```
  * Now marking failed index jobs and display the error message in the indexer servlet.
  * You can now configure the query parser to not convert wildcardqueries to lower case. This ios usefull if you index a column with the WhitespaceAnalyzer which doesn't convert the terms to lowercase when indexing.
```
rp.1.queryparser.lowercaseexpandedterms=false
```

# Bugfixes #
  * Fixed display of memory consumption in the indexer servlet
  * Fixed debug output when a configuration file in a config subfolder would not be found


