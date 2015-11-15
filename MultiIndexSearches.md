This article describes how you can set up your search engine to search in multiple index directories

# Configuration #

The following configuration would search in 3 index directories.
```
#ContentRepository specific config
#RequestProcessor1 gets the elements from LuceneIndex
rp.1.rpClass=com.gentics.cr.lucene.search.LuceneRequestProcessor
#file system location of index
rp.1.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties
rp.1.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/index
rp.1.indexLocations.1.path=${com.gentics.portalnode.confpath}/index_1/index
rp.1.indexLocations.2.path=${com.gentics.portalnode.confpath}/index_2/index
rp.1.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneMultiIndexLocation
#check if index should be reopened each search
rp.1.reopencheck=true
#idattribute: attribute that contains the identifyer in the lucene index
rp.1.idattribute=contentid
#Searched attributes: default attribute that is being searched if no other is specified
rp.1.searchedattributes=content
#Searchcount: number of hits that is returned if no other specified
rp.1.searchcount=30
#Score attribute: under which the score of each hit can be requested
rp.1.scoreattribute=score
#getstoredattributes: if this is set to true, all stored attributes are returned
rp.1.getstoredattributes=true
#highlighters
rp.1.highlighter.1.class=com.gentics.cr.lucene.search.highlight.PhraseBolder
rp.1.highlighter.1.attribute=content
rp.1.highlighter.1.rule=1==1
rp.1.highlighter.1.fragments=5
rp.1.highlighter.1.fragmentsize=10
rp.1.highlighter.1.highlightprefix=<b>
rp.1.highlighter.1.highlightpostfix=</b>
rp.1.highlighter.1.fragmentseperator=...
```

# Notes #
The LuceneMultiIndexLocation is not able to write to the index.

The performance of multi index searches is slightly below the normal search performance when using a single index.

You should use the same analyzers for searching as for indexing