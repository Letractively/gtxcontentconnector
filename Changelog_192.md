# Introduction #
New DidyoumeanIndexExtension to provide a separation of indexing jobs and search tasks in the didyoumean functionality of the contenconnector-lucene

# Important Changes #
  * DidyoumeanIndexExtension:
    * added the didyoumean index extension to provide clear separation between indexing jobs and search task concerning the didyoumean functionality of the contentconnector-lucene
    * the extensions provides a reIndex-job - it is used to reIndex the the didyoumean index, and a clear-Job which is used to delete all entries from the didyoumean-index.
    * the reIndex-job can be configured to be started always after a main index job is finished or it can be controlled via the indexer servlet - the clear-job has to be started manually

# Configuration changes #
  * To configure the new DidyoumeanIndexExtension following additional parameters need to be added to the indexer.properties file:
```
# the class name of the DidyoumeanIndexExtension
index.<INDEX_NAME>.extensions.DIDYOUMEAN.class=com.gentics.cr.lucene.didyoumean.DidyoumeanIndexExtension
# location of the dym index
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/didyoumean
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanlocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzerdidyoumean.properties
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanlocation.reopencheck=timestamp
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanlocation.writereopenfile=true

# location of the source index. Optional - if not provided the main LuceneIndexLocation is used by default 
index.<INDEX_NAME>.extensions.DIDYOUMEAN.srcindexlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
index.<INDEX_NAME>.extensions.DIDYOUMEAN.srcindexlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/index
index.<INDEX_NAME>.extensions.DIDYOUMEAN.srcindexlocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties
index.<INDEX_NAME>.extensions.DIDYOUMEAN.srcindexlocation.reopencheck=timestamp

# Fields, that will be used to find the suggestions. Default: "All"
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanfields=content,binarycontent,name,text,titel
# Sets the the worddistancescore that has to be reached
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanmindistancescore=0.75
# Sets the minimal frequecy the suggested term must have
index.<INDEX_NAME>.extensions.DIDYOUMEAN.didyoumeanmindocfreq=5

# if set to true the reindex-job is started everytime a main index job is finished (default false)
index.<INDEX_NAME>.extensions.DIDYOUMEAN.reindexOnCRIndexFinished=true
```
  * Following changes need to be applied to the search.properties to activate the use of the DidyoumeanIndexExtension
```
# set to true if you want to use the new IndexExtension (default: false)
rp.1.didyoumeanUseIndexExtension=true

 ...

# you don't need to configure the source IndexLocation (rp.1.srcindexlocation) 

...

# location of the dym index
rp.1.didyoumeanlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
rp.1.didyoumeanlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/didyoumean
rp.1.didyoumeanlocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzerdidyoumean.properties
# set the reopencheck attribute accordingly to the configuration in the indexer.properties (true/false or "timestamp")
rp.1.didyoumeanlocation.reopencheck=timestamp

 ...

# the values of the following properties need to be the same as the values of their corresponding 
# properties in the indexer.porperties to provide correct suggestions
rp.1.didyoumeanfields=content,binarycontent,name,text,titel
rp.1.didyoumeanmindistancescore=0.75
rp.1.didyoumeanmindocfreq=5
```