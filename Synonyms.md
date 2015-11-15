# Synonyms #

The Synonyms Extension provides the possibility to index synonyms and integrate it in the users search query.

A synonym in the synonym index consists of 2 terms:
the descriptor: the word in the search query
the synonym: the word which will be added if the descriptor is in the search query

For example a deskriptor could be "CCR" and the Synonym can be "Content-Connector". So if the search-Query contains "CCR", "Content-Connector" will automatically be added to the query.

If the query is **"name:CCR"** it will be expanded to **"name:CCR OR name:Content-Connector"**

## Indexer Configuration ##

In the indexer-configuration you have to configure from where the synonyms are loaded and where they are stored.

```
index.DEFAULT.extensions.SYN.class=com.gentics.cr.lucene.synonyms.SynonymIndexExtension
## set this property to true when the the synonym index should be re-indexed after the main index finished indexing
index.DEFAULT.extensions.SYN.reindexOnCRIndexFinished=true
## set the RequestProcessor which provides the data for the synonym index
index.DEFAULT.extensions.SYN.rp.1.rpClass=com.gentics.cr.lucene.synonyms.DummySynonymRequestProcessor
## name of the descriptor field in resultSet of the RequestProcessor
index.DEFAULT.extensions.SYN.descriptorColumnName=Deskriptor
## name of the synonym field in resultSet of the RequestProcessor
index.DEFAULT.extensions.SYN.synonymColumnName=Synonym
## Class and Path of the indexLocation where the Synonym Index will be stored
index.DEFAULT.extensions.SYN.synonymlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
index.DEFAULT.extensions.SYN.synonymlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/sym
```


## Search Configuration ##

In the search configuration you have to specify the queryparser, if you want to use synonyms. You also have to configure the path of the synonym-Index.

```
rp.1.queryparser.class=com.gentics.cr.lucene.search.query.SynonymQueryParser
## indexLocation Class and path to the synonym-Index
rp.1.queryparser.synonymlocation.indexLocationClass=com.gentics.cr.lucene.indexer.index.LuceneSingleIndexLocation
rp.1.queryparser.synonymlocation.indexLocations.0.path=${com.gentics.portalnode.confpath}/index/sym
## Possibility to set another QueryParser which will be called as super QueryParser
#rp.1.queryparser.queryparser = com.gentics.cr.lucene.OtherQueryParser

```