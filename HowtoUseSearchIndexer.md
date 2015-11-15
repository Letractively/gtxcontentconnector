# How to use the Gentics Search Web app #

The Gentics search web app is based on the Gentics Content Connector and Apache Lucene.

## Components ##
The Gentics Content Connector includes also these components which are needed to provide the search functionality
  * Apache Lucene
  * Gentics Content Connector
  * 2 servlets (for search and auto suggestions)

## Infrastructure ##
[Read the article about how to install a good search and indexer infrastructure.](http://code.google.com/p/gtxcontentconnector/wiki/PossibleSearchInfrastructure)

## Configuration ##
[Read more about the configuration of the various functions of the search and the indexer.](http://code.google.com/p/gtxcontentconnector/wiki/Configuration)

## Indexer ##
The search contains of two main functionalities (the searcher and the indexer). The indexer might run on the CMS server and creates
one or many indexes. These indexes are needed by the searcher. Calculate a minimum of 1 GB RAM for the web app and it's good to put it
on a separate servlet container (i.e. Tomcat). Deploy the indexer.war there.

The indexer also provides an HTML interface where several indexing and searching statistics can be displayed. Don't forget to secure this. The additional indexes for Didyoumean-functionality and Autosuggestion-functionality are also created by the indexer.

/indexer/indexer?action=show&t=1309889140650

## Searcher ##

The searcher can be requested via REST (Web service) and gets back JSON objects or serialized java / php objects. The searcher has to have
access to the indexes in the file system. These indexes can also be synchronized between servers if you have several frontends which access the search.

Deploy the searcher webapp on any frontend or in some environments it makes a lot of sense to have a central search server and that all frontends only request from this central search server the search results.

Create quick-search-form, advanced search-form and search results on your frontend. Think about I18N (internationalisation).
If the search form is empty, show error message. If the search term is < 3 characters, show error message.

If this is ok, then send the REST-request:

/searcher/search?filter=content:searchterm

Based on the node\_id you can limit to single projects or you create one index per project.

/searcher/search?filter=content:searchterm+AND+node\_id:3

There are several other search parameters to limit for example based on the path or on the file format (mimetype)

/searcher/search?filter=content:searchterm+AND+node\_id:3+AND+mimetype:**pdf**

Use the Apache Lucene Query Language to create more complex search queries:
  * [Lucene Query Syntax](http://lucene.apache.org/java/3_0_0/queryparsersyntax.html)
  * Fuzzysearch add searchterm~
  * Wildcard search **searchterm**
  * searchterm1 AND searchterm2
  * searchterm1 OR searchterm2
  * searchterm1 NOT searchterm2

If you put this in the search request, don't forget to encode it.

Limit the search results based on attribute "languagecode". Apache Lucene also provides a plugin for language guessing.

A lot of search parameters can be defined in the search-configuration-properties files. [So, read the configuration section]

You have to create another request based on jQuery for providing the autosuggestion:

/searcher/autocomplete?filter=un

Have a look at the sample search app which will be delivered with the package you can download.

Use <!--noindexstart--> content not indexed <!--noindexend--> to prevent the content between these tags from being indexed.

### Further features of the search ###
  * multi site support (with several configurations)
  * filter for languages (or use language guessing)
  * autosuggestion (don't forget to limit this per project = Node)
  * binary content indexing (based on plugin languagetransformer)
  * wildcard search
  * fuzzy search ~
  * did you mean?
  * stopwords for languages
  * boosting of articles

### Examples ###
[UNIQA Search](http://www.uniqa.at/uniqaat/cms/search.de.xhtml)

[Advantageaustria Search](http://www.advantageaustria.org/us/zentral/system/searchresult.en.html?query=opportunities)

[Unternehmersearchportal Suche](https://www.usp.gv.at/Portal.Node/usp/public?gentics.am=search.searchresults_teaser&gentics.pb=search&p.key=1332280612186&gentics.ts=1332280612&p.search.search.searchresults_teaser.c.2.1.search-trigger-button.btn0.v=Suche)