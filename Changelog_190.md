# Important Changes #

  * The CRQueryParser now applies the wordmatch request parameter only to the searched attributes given in the constructor. This way we prevent the QuerParser from adding wildcards to category limiters.
    * see also
      * CRRequest#WORDMATCH\_KEY
      * CRQueryParser#addWildcardsForWordmatchParameter(String)
  * Added new interface: IndexExtension.
    * The new IndexExtensions can be used to manage additional indexes along with the main source index (e.g.: autcomplete- or didyoumean-index)
    * added support for IndexExtensions to IndexLocation - it can now manage multiple extensions
      * the extension can be controlled via the IndexerServlet
  * The autocompleter can now be used as IndexExtension (with the class AutocompleteIndexExtension)
    * the improved autcompleter provides a strict separation of indexing jobs and search jobs
    * all implementations of the autocompleter should consider using the new extension due to significant performance gains
    * future versions of the contenconnector will not support the management of the autocomplete-indexing via the Autocompleter-class anymore

# Configuration changes #

  * If you set didyoumean\_activatelimit to -1 didyoumean is always axecuted regardless of the count of the results comming back
```
rp.1.didyoumean_activatelimit = -1
```

# Features #

  * added posibility to add a frame template arround the rendered code of the VelocityContentRepository
```
 #this can be used to integrate a design in the responce
 #the placeholder [Searchresult] is replaced by the rendered template content
 cr.velocity.frame=http://mydomain/searchresults.htm
 cr.velocity.frameplaceholder=[Searchresult]
```
  * added the JsonRequestProcessor in contentconnector-json
```
 rp.1.rpClass=com.gentics.cr.rest.json.JSONRequestProcessor
 rp.1.objects={objects:[\
				{name: "first object"},\
				{name: "second object"},\
				{name: "third object"}\
			]}
```

  * Index Extensions:
    * add multiple extensions to one indexer
      * the extensions provide a list of supported jobs
      * jobs can be added to the job queue of a IndexLocation
```
# NEW KEYS for indexer.properties
# use the extensions-key to store the configuration for the extension  
index.<INDEX_NAME>.extensions
# The class name of the extension (required)
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.class=com.gentics.cr.lucene.autocomplete.AutocompleteIndexExtension
```

  * Autocompleter (AutocompleteIndexExtension):
    * the extensions provides two jobs:
      * the reIndex job is used to reIndex the the main index (it can be configured to be started always after a main index job is finished)
      * the delete job is used to clear all files in the main index
      * the jobs can be controlled via the indexer servlet

Porperties for the AutocompleteIndexExtension in the indexer.properties
```
# the name of the autocompleter class
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.class=com.gentics.cr.lucene.autocomplete.AutocompleteIndexExtension
# set this property to true when the the autcomplete index should be re-indexed after the main index finished indexing
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.reindexOnCRIndexFinished=true
# the cofinguration keys for the autocomplete index-location
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.autocompletelocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.autocompletelocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/autocomplete
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.autocompletelocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/autocompleteanalyzer.properties
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.autocompletelocation.reopencheck=timestamp
# the cofinguration keys for the source index-location
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.srcindexlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.srcindexlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/index
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.srcindexlocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties
index.<INDEX_NAME>.extensions.<EXTENSION_NAME>.srcindexlocation.reopencheck=timestamp
```
Properties for the autocompleter.properties
```
# set this property to true to use the new AutocompleteIndexExtension (default: false)
rp.1.useAutocompleteIndexer=true
# the configuration for the autocomplete-location
rp.1.rpClass=com.gentics.cr.lucene.autocomplete.AutocompleteRequestProcessor
rp.1.autocompletelocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
rp.1.autocompletelocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/autocomplete
rp.1.autocompletelocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/autocompleteanalyzer.properties
rp.1.autocompletelocation.reopencheck=timestamp
# the configuration properties for the source index location (srcindexlocation) are not needed 
# when useAutocompleteIndexer == true
```
# Bugfixes #




