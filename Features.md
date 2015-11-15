This articles describes a list of features

# General Functionality #
  * retrieve objects and attributes
  * get contents (html, css, binaries)
  * get navigation structures
  * search
  * caching

# REST API #
  * get content in xml, php serialized array, json, java xml, java binary
  * retrieve navigation structure (tree)
  * search

## methods ##

### configuration ###
applicationsrule: rule
rule to filter queries and loading in CR based servlet.

### load specific objects and attributes ###
requires a REST-Servlet, configured for CR.

parameter: contentid
returns xml without attributes

parameter: attributes=name
returns cc-xml with given attributes

parameter: type=php|xml|javaxml|javabin|json
javaxml: returns vector of CRResolvableBeans, requires client lib.
javabin: see javaxml
json: structure see cc-xml


### load contents ###
requires CRServlet, configured for CR.

configuration: urltemplate
a velocity template to replace plink (url placeholder)

configuration: velocity
content can be rendered in velocity after fetching

parameter: contentid
returns content (for pages), binary (for files). optional.

paramter: filter
returns objects filtered by given rule.

parameter: sorting=key:order
sort by attribute "key", order "asc" or "desc"

pathinfo:
pathinfo will be used to load contents by path. optional.



### search in cr ###
requires a REST-Servlet, configured for CR.

parameter: filter=rule
returns cc-xml without attributes

parameter: count, start
for paging

parameter: sort
see above


### search via lucene ###
requires a REST-Servlet, configured for Lucene.

paramter: filter=lucene rule
returns cc-xml without attributes

parameter: count, start
see above

configuration: highlighter


### navigation structure ###
requires Nav-Servlet, configure for CR.

parameter: filter
see above

parameter: childrule
will be appended to filter to determine children

returns:
hierarchical structure of the objects instead of list.


# Java API #
present content and navigation for servlets or portlets
retrieve objects and attributes
search
requires client library.

## methods ##
See above REST-API

NavObject:
parameters: conf, rootobject, vtltemplate, contextobjects
method: render


## configuration ##
requestprocessormerger:
enrich lucene search result with attributes from a CR.



# JSR-286 Portlets #
including jboss, liferay and gentics deployment descriptor.
contentportlet, navigationportlet.

## contentportlet ##
parameters: contentid, p.maxwidth, p.maxheight
listens to "GENTICS\_CONTENT" event, stores contentid to portletsession and displays content.

## navigationportlet ##
see Java API, NavObject

fires "GENTICS\_CONTENT" event when navigation element was clicked.



# Indexer #
servlet to (start, stop) manage indexing.

## configuration ##
indexpath, datasource, rule, indexedAttributes, containedAttributes (required for highlighting)

transformer:
to index html, pdf, word, etc.
ignore no comment sections.
languageidentifier.

## methods ##
add index job for CRs.