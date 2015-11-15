This article describes how to set up boosting in your lucene search.

# Introduction #

Boosting can be used to give special documents and fields a greater relevance in order to bring them higher up in the results once the document is found.

The default boost factor is always 1.0.

# Query Boosting #
You can add a boosting to query fields using the lucene query syntax.

The following example will boost the term jakarta.
```
content:(jakarta^4 apache)
```

# Field Boosting #

You can define severyl fields in the indexer configuration, that should be boosted.

```
# The fields/attributes that should be boosted with the value that is stated after the "^"
index.DEFAULT.CR.PAGES.BoostedAttributes=name^10.0,content^5.0
```

# Document Boosting #

You can also define an attribute, that will contain the boost factor for each document.

```
# The field that contains the boostvalue for the current object. 
# 1.0 is default if this attribute is not set or not present.
index.DEFAULT.CR.PAGES.boostAttribute=searchboost
```

# Boosting recent entries #

With the CRRecencyBoostingQueryParser it's possible to boost entries in a specific time-range.

Necessary for this option is one timestamp field, for example boosttimestamp.

With translator, you can define of each part this timestamp, if your fields are different named.
Example:
```
index.XXX.CR.PAGES.transformer.4.rule=true==true
index.XXX.CR.PAGES.transformer.4.sourceattribute=field_source
index.XXX.CR.PAGES.transformer.4.targetattribute=targetTimeStamp
index.XXX.CR.PAGES.transformer.4.transformerclass=com.gentics.cr.lucene.indexer.transformer.other.CopyValueTransformer
```

In order to boost this field, you have to config the search.properties file.
Firstly, the query parser have to be configs like this:
```
rp.1.queryparser.class=com.gentics.cr.lucene.search.query.CRRecencyBoostingQueryParser
rp.1.queryparser.boostAttribute=updatetimestamp
rp.1.queryparser.timerange=4320000
rp.1.queryparser.multiplicatorBoost=5
```
All entries between today and today - timerange gets a boost.
Newer entries gets a better boost then older entries, which are also in the timerange.

All entries later then the timerange don't get a boost.


Highlighting

For a correct highlighting we need an own query parser for this, because it isn't implemented in lucene to highlight own custom score queries like the RecencyBoostingQuery.
So we have to define a highlightqueryparser, e.g.:
```
rp.1.highlightqueryparser.class=org.apache.lucene.queryParser.QueryParser
```