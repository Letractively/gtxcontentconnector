# Gentics Content Connector and Gentics Search (based on Apache Lucene) #

## Installation and Setup ##
Download the [Sample Webapp](http://archiva.gentics.com:8081/archiva/browse/com.gentics/contentconnector-servlet-sample) and the [Sample Configuration](http://archiva.gentics.com:8081/archiva/browse/com.gentics/contentconnector-config-sample/).

Deploy the Sample Webapp to an application server of your liking and start it with the -Dcom.gentics.portalnode.confpath set to the extracted config directory of the Sample Configuration.

For some more information on how to configure the application, have a look at [Configuration](Configuration.md).

Once the server has started, point your browser to **/servlet-sample/** on it (in most cases this is http://localhost:8080/servlet-sample/).

## Getting Started ##

This web app gives access to the content objects in the Gentics Content Repository and offers you the full functionality of the world-famous search framework Apache Lucene enhanced with a lot of functionality.

Find important information on this topic here:
[Startpage of Wiki](http://code.google.com/p/gtxcontentconnector/wiki/A_STARTPAGE_WIKI)

  * for Content Connector - use expression parser filter rules
  * for Search - use lucene query language
  * for Flat Tables - use SQL

How to use Gentics Content Connector?
[Usage of CCR](http://code.google.com/p/gtxcontentconnector/wiki/Usage)

How to use Gentics Search?
[Usage of Search and Indexer](http://code.google.com/p/gtxcontentconnector/wiki/HowtoUseSearchIndexer)

  * 10002 … folders
  * 10007 … pages (html, php, jsp, css, js, xml, text, …)
  * 10008 … binary files (images, files, can be css, js)

### Examples ###

Request all images (LIKE)

/servlet-sample/ccr?filter=object.url%20LIKE%20%22%jpg%22

Request binary content
To get a binary-content-object request an 10008-object (files, images, not pages) i.e. as json. Request the “obj\_type” and “mimetype” as attributes. Before you display the binary content make an additional request for the binary-content to the content-servlet. Send the mime type in the header and then display the binary content.

Request the content of a binary file via content servlet (for reducing load of json-encoding).

/servlet-sample/content?contentid=10008.93

If the content displayed by the content servlet contains plinks, they will be rendered with the configured plink template. This allows for application specific beautiful URLs.


### Structure of Gentics Content Connector and the sample webapp ###
Each servlet in the Gentics Content Connector has its own special function. For each servlet a properties file exists in the configuration ... /conf/gentics/rest/. Servlets are configured in the web.xml

The Sample Webapp contains the following servlets:

  * **/servlet-sample/** or **/servlet-sample/velocity** - the welocme servlet, also a fully functional search with results, paging, dym and autocompletion
  * **/servlet-sample/indexer** - it will load on startup and contains the index functionality for the lucene search
  * **/servlet-sample/search** - the default search servlet. it can return xml, json and serialized php
  * **/servlet-sample/autocomplete** - the autocomplete servlet that is being used for the autocompletion in the welcome servlet
  * **/servlet-sample/content** - can deliver content and renders the plinks so that they point to the content portlet itself
  * **/servlet-sample/ccr** - can deliver lists / results from the contentrepository in xml, json, and serialized php

Configuration property files:
You can add additional servlets by configuring them in the web.xml and adding a configuration file with the servlet name + .properties as file name.
If the servlet name is “ccr”, the properties file has to be called ccr.properties

For additional information on configuration have a look at the [Wiki Page](Configuration.md).

Deploy it on an application server.

The Gentics Portal Connector (this is the raw Java API to the Gentics Content Repository) is a commercial library of the Gentics Content Connector.