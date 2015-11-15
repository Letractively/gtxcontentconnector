This article describes the search functionality and how to set it up.

# Introduction #

The next few lines describe how to set up a search infrastructure with an index server and a search application.

# Infrastructure #

The infrastructure of a search application can be divided in two important parts: the indexer and the searcher.

![http://farm3.static.flickr.com/2614/4111841413_6f266ca3f6_o_d.png](http://farm3.static.flickr.com/2614/4111841413_6f266ca3f6_o_d.png)

## The Indexer ##

The indexer periodically scans the defined content and builds up an index to speed up searches. The content has to be converted to text before it can be indexed. This means that one has to extract text from files like PDF, DOC, DOCX and so on, before the content of the files is being passed to the indexer.

## The Searcher ##

The searcher runs the searches against the index for the user. It highlights the text snippets in the result and passes the result back to the search application where it can be displayed to the user.

# Index content #

![http://farm3.static.flickr.com/2531/4112482688_3db75f673e_o_d.png](http://farm3.static.flickr.com/2531/4112482688_3db75f673e_o_d.png)

While the structure is still displayed like in the picture above, the configuration has slightly changed.


Here a small example:
```
#file system location of index
index.1.indexLocations.1.path=${com.gentics.portalnode.confpath}/index/index
#index.1.indexLocations.1.path=RAM
#interval in seconds
index.1.interval=5
#job check interval
index.1.checkinterval=5
index.1.periodical=true
#job batch size
index.1.batchsize=10
index.1.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties

#ContentRepository specific config
index.1.CR.1.rp.1.rpClass=com.gentics.cr.CRRequestProcessor
#Datasource
index.1.CR.1.rp.1.ds-handle.type=jdbc
index.1.CR.1.rp.1.ds-handle.driverClass=com.mysql.jdbc.Driver
index.1.CR.1.rp.1.ds-handle.url=jdbc:mysql://127.0.0.1:42006/demoportal_ccr?user=node
#DO NOT USE CACHE FOR INDEXING
index.1.CR.1.rp.1.ds.cache=false
index.1.CR.1.rp.1.ds.cache.foreignlinkattributes=false
index.1.CR.1.rp.1.ds.cache.syncchecking=false
index.1.CR.1.rp.1.response-charset=UTF-8
index.1.CR.1.rp.1.binarytype=10008
index.1.CR.1.rp.1.foldertype=10002
index.1.CR.1.rp.1.pagetype=10007
#index.1.CR.2.ds-handle.portalnodedb=ccr
index.1.CR.1.rule=object.obj_type==10008
#index.1.CR.1.rule=10009==10008
index.1.CR.1.indexedAttributes=name,edittimestamp,publishtimestamp,binarycontent,mimetype,permissions,language
index.1.CR.1.containedAttributes=name,binarycontent,permissions,language
index.1.CR.1.idattribute=contentid
index.1.CR.1.batchsize=5
#Define the content transformer map
#
#PDF
index.1.CR.1.transformer.2.attribute=binarycontent
index.1.CR.1.transformer.2.rule=object.obj_type==10008 AND object.mimetype=="application/pdf"
index.1.CR.1.transformer.2.transformerclass=com.gentics.cr.lucene.indexer.transformer.pdf.PDFContentTransformer
#DOC
index.1.CR.1.transformer.3.attribute=binarycontent
index.1.CR.1.transformer.3.rule=object.obj_type==10008 AND object.mimetype=="application/msword"
index.1.CR.1.transformer.3.transformerclass=com.gentics.cr.lucene.indexer.transformer.doc.DOCContentTransformer
#XLS
index.1.CR.1.transformer.4.attribute=binarycontent
index.1.CR.1.transformer.4.rule=object.obj_type==10008 AND object.mimetype=="application/vnd.ms-excel"
index.1.CR.1.transformer.4.transformerclass=com.gentics.cr.lucene.indexer.transformer.xls.XLSContentTransformer
#PPT
index.1.CR.1.transformer.5.attribute=binarycontent
index.1.CR.1.transformer.5.rule=object.obj_type==10008 AND object.mimetype=="application/vnd.ms-powerpoint"
index.1.CR.1.transformer.5.transformerclass=com.gentics.cr.lucene.indexer.transformer.ppt.PPTContentTransformer
#LANGUAGE
#index.1.CR.1.transformer.6.attribute=binarycontent
#index.1.CR.1.transformer.6.langattribute=language
#index.1.CR.1.transformer.6.rule=object.obj_type==10008
#index.1.CR.1.transformer.6.transformerclass=com.gentics.cr.lucene.indexer.transformer.lang.LanguageIdentifyer

#PERMISSIONS
index.1.CR.1.transformer.7.attribute=permissions
index.1.CR.1.transformer.7.rule=1==1
index.1.CR.1.transformer.7.nullvalue=NULL
index.1.CR.1.transformer.7.transformerclass=com.gentics.cr.lucene.indexer.transformer.multivaluestring.SimpleMVString


#ContentRepository specific config
#ContentRepository specific config
index.1.CR.2.rp.1.rpClass=com.gentics.cr.CRRequestProcessor
#Datasource
index.1.CR.2.rp.1.ds-handle.type=jdbc
index.1.CR.2.rp.1.ds-handle.driverClass=com.mysql.jdbc.Driver
index.1.CR.2.rp.1.ds-handle.url=jdbc:mysql://127.0.0.1:42006/demoportal_ccr?user=node
#DO NOT USE CACHE FOR INDEXING
index.1.CR.2.rp.1.ds.cache=false
index.1.CR.2.rp.1.ds.cache.foreignlinkattributes=false
index.1.CR.2.rp.1.ds.cache.syncchecking=false
index.1.CR.2.rp.1.response-charset=UTF-8
index.1.CR.2.rp.1.binarytype=10008
index.1.CR.2.rp.1.foldertype=10002
index.1.CR.2.rp.1.pagetype=10007
#index.1.CR.2.ds-handle.portalnodedb=ccr
#index.1.CR.2.rule=object.contentid == "10007.1033"
index.1.CR.2.rule=object.obj_type==10007
index.1.CR.2.indexedAttributes=name,edittimestamp,publishtimestamp,content,mimetype,permissions,public,language
index.1.CR.2.containedAttributes=name,content,permissions,public,language
index.1.CR.2.idattribute=contentid
index.1.CR.2.batchsize=100
#Define the content transformer map

#STRIPPER
index.1.CR.2.transformer.1.attribute=content
index.1.CR.2.transformer.1.rule=object.obj_type==10007
index.1.CR.2.transformer.1.transformerclass=com.gentics.cr.lucene.indexer.transformer.RegexReplacer
#HTML
index.1.CR.2.transformer.2.attribute=content
index.1.CR.2.transformer.2.rule=object.obj_type==10007
index.1.CR.2.transformer.2.transformerclass=com.gentics.cr.lucene.indexer.transformer.html.HTMLContentTransformer
#PERMISSIONS
index.1.CR.2.transformer.6.attribute=permissions
index.1.CR.2.transformer.6.rule=1==1
index.1.CR.2.transformer.6.nullvalue=NULL
index.1.CR.2.transformer.6.transformerclass=com.gentics.cr.lucene.indexer.transformer.multivaluestring.SimpleMVString
#LANGUAGE
#index.1.CR.2.transformer.7.attribute=content
#index.1.CR.2.transformer.7.langattribute=language
#index.1.CR.2.transformer.7.rule=object.obj_type==10007
#index.1.CR.2.transformer.7.transformerclass=com.gentics.cr.lucene.indexer.transformer.lang.LanguageIdentifyer

```

  1. All objects that match the index rule (config) are loaded from the content repository with their CONTENTID and UPDATETIMESTAMP. Then they are compared to the objects in the index. If needed the object in the index will be updated, new objects will be added and objects that are no longer in the repository will be deleted.
  1. A chain of configured transformers will now process the collection of resolvables in order to prepare the content for the indexing process. E.g.: Extracting text from a PDF-Binary or stripping HTML tags,... For each resolvable the configured transformer rule is being executed in order to determine if the transformer should process the current resolvable.
  1. Then the converted objects are being written to the index file (can be configured to a file system location or to a RAM-directory).

# Basic search #

![http://farm3.static.flickr.com/2667/4112482644_4929b80e12_o_d.png](http://farm3.static.flickr.com/2667/4112482644_4929b80e12_o_d.png)

  1. A search is being run against the index. For a detailed description on the lucene query language, have a look at: [Querylanguage](http://lucene.apache.org/java/2_0_0/queryparsersyntax.html)
  1. The configured number of results is being returned with the contained attributes and an additional score attribute that describes the importance of the according object. Anyhow the score value does not describe the quality of the according object. [LuceneScore](http://wiki.apache.org/lucene-java/LuceneFAQ#Can_I_filter_by_score.3F) [ScoreCalculation](http://jayant7k.blogspot.com/2006/07/document-scoringcalculating-relevance_08.html)
  1. The collection of resolvables is then being highlighted in order to get a text snipped around the searched terms. There are several ways how highlighting can be done, therefore more than one highlighter can be configured.
  1. Now the result can be displayed to the user.

```
#ContentRepository specific config
#RequestProcessor1 gets the elements from LuceneIndex
rp.1.rpClass=com.gentics.cr.lucene.search.LuceneRequestProcessor
#file system location of index
rp.1.analyzerconfig=${com.gentics.portalnode.confpath}/rest/analyzer.properties
rp.1.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/index
rp.1.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
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

# Enrich searched objects #

![http://farm3.static.flickr.com/2773/4111717305_c6408d11c5_o_d.png](http://farm3.static.flickr.com/2773/4111717305_c6408d11c5_o_d.png)

If you do not want to store much data in the index, you can enrich searched objects by additional attributes from another datasource (e.g. Content Repository). This can be done using the RPMergerRequestProcessor.

  1. Like in the basic search, the query agains the index is being executed and returnes the found objects.
  1. The found objects are then looked up in the second configured datasource.
  1. Attributes that were not stored in the index are now being loaded from the second datasource if the objects (per default matched via CONTENTID) does exist there as well.


# Additional Info #
  * [LuceneLuke](http://code.google.com/p/luke/)
  * [LuceneHome](http://lucene.apache.org/)
  * [LuceneFAQ](http://wiki.apache.org/lucene-java/LuceneFAQ)
  * IndexPerFieldAnalyzer
  * MultiIndexSearches