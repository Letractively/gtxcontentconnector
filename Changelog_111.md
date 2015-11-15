#This Page describes the major changes from Verstion 1.1.0 to 1.1.1

# Configuration changes #

## Differential Indexing ##

Now a updateattribute has to be configured to enable differential indexing.

```
# Enables the differential indexing and uses the attribute set in this option
index.DEFAULT.CR.PAGES.updateattribute=updatetimestamp
```

## Index Optimization ##
We replaced the following configuration option
```
#The following line sets the index job to not optimize the index
index.DEFAULT.CR.FILES.ignoreoptimize=true
```

with the following options. They give more control over the optimization process and do not mislead the user.

```
#The following line would set the index job to execute an optimize command on the index after each run (time an memory consuming)
index.DEFAULT.CR.FILES.optimize=true
#The following line would set the index job to execute an optimize command on the index after each run using a max segement rule => only optimizes when more than max segments are present (not as time and memory consuming) (the line above should be commented out)
index.DEFAULT.CR.FILES.maxsegments=10
```

# Features #

  * FileSystemRequestProcessor (not yet finished) which allows to get and index contents from a directory in the file system. At the moment you can use the attributes "binarycontent", "obj\_type" of the resolvables
  * simple performance montoring. we are still working on this.
  * VectorBolder which uses search term vectors stored when indexing, so its faster than the old PhraseBolder.
```
#Configure the highlighters (sample for VectorBolder)
#Vector Bolder needs Vectors stored in the index so check your indexer.properties
#if index.DEFAULT.CR.FILES.storeVectors is set to true (default)
rp.1.highlighter.1.class=com.gentics.cr.lucene.search.highlight.VectorBolder
#Set the attribute that should be highlighted
rp.1.highlighter.1.attribute=content
#Set the rule that defines the objects that should be highlighted with this highlighter
rp.1.highlighter.1.rule=1==1
#Set the max number of fragments that should be present in the highlighted text
rp.1.highlighter.1.fragments=5
#Set the max number of characters a fragment should contain
rp.1.highlighter.1.fragmentsize=100
#Set the tags that should be used for highlighting
rp.1.highlighter.1.highlightprefix=<b>
rp.1.highlighter.1.highlightpostfix=</b>
#Set the seperator that should be used between the fragments
rp.1.highlighter.1.fragmentseperator=...
```
  * SearchTerm Vectors are now stored by default when indexing (for faster search and VectorBolder)
```
#Makes searches faster, required for VectorBolder, needs more space on disk (about double the space as without Vector), default is true
index.DEFAULT.CR.FILES.storeVectors=true
```
  * You can now configure a custom collector. e.g. for checking permissions.
```
#configure a custom collector (not included in content connector)
#in this case it is a collector to check the permissions of the search results
rp.1.collectorClass=org.apache.lucene.search.PermissionsCollector
#all properties used in rp.1.collector are put into the collector config
#here we have a sample config with another custom class and an additional datasource for the permissions
rp.1.collector.permissionCheckerClass=your.company.PermissionChecker
rp.1.collector.permissionChecker.ds.ds-handle.type=jdbc
rp.1.collector.permissionChecker.ds.ds-handle.driverClass=com.mysql.jdbc.Driver
rp.1.collector.permissionChecker.ds.ds-handle.url=jdbc:mysql://127.0.0.1:3306/permissiondb?user=x&password=y
```

  * You can now use and configure the did-you-mean functionality
```
# Did you mean functionality
# Enables the did you mean functionality
rp.1.didyoumean=true
# Sets the minimum score that works as a threshold. For results below that score a did you mean suggestion will be computed
rp.1.didyoumeanminscore=0.01
# Enables or disables the bestquery calculation
rp.1.didyoumeanbestquery=false
# Sets the fields that are being used to suggest the best terms
rp.1.didyoumeanfields=content,teaser_title,teaser_text
# Configures the location where the didyoumeanindex will be stored
rp.1.didyoumeanlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
rp.1.didyoumeanlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/didyoumean
rp.1.didyoumeanlocation.reopencheck=true
# Configures the location of the index for that the didyoumeanindex will be generated
rp.1.srcindexlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
rp.1.srcindexlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/index
rp.1.srcindexlocation.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties
rp.1.srcindexlocation.reopencheck=true
```

  * You can now use boosting on field level in the indexer
```
# The fields/attributes that should be boosted with the value that is stated after the "^"
index.DEFAULT.CR.PAGES.BoostedAttributes=name^10.0,content^5.0
```

# Bugfixes #

  * [issue #37](https://code.google.com/p/gtxcontentconnector/issues/detail?id=#37) is fixed, this caused a not stopable jvm when using RPMergerRequestProcessor
  * various bugfixes that where not reported as an issue, mainly bugfixes to be able to stop the jvm correctly, also now the background threads are demon threads