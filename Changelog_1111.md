# Important Changes #
  * We now use a maven multi module projet to reduce complexity.
  * group ids changed to com.gentics
  * We changed the default behaviour for returning the bestqueries for didyoumean results. The default setting is now to not return them, because serializing them would lead to exceptions when using type JSON/PHP/XML. You can enable this by adding the following line to your search config
```
# Enables or disables the bestquery calculation (1 Result)
rp.1.didyoumeanbestquery=true
```
# Configuration changes #

# Features #
  * We can now configura a LockFactory for lucene indexes. Take care, this factory has to be configured for index extensions as well!
```
#LockFactory configuration.
index.DEFAULT.lockFactoryClass=org.apache.lucene.store.SimpleFSLockFactory

# Be sure to configure the lock factory for all index extensions. eg.:
index.DEFAULT.extensions.AUTOCOMPLETE.srcindexlocation.lockFactoryClass=org.apache.lucene.store.SimpleFSLockFactory
```
  * Introduced a FastContentRenderer that uses the PLinkOutputStream to replace Plinks and therefore is much faster than the common ContentRenderer.
  * It is now possible to boost complete documents when indexing. An attribute name can be configured and the indexer will then use the value behind this attribute to set the boost value for the document.

```
# The field that contains the boostvalue for the current object. 1.0 is default if this attribute is not set or not present.
index.DEFAULT.CR.PAGES.boostAttribute=searchboost
```
  * Introduced a cache clear servlet, that can be used to clear datasource caches after one or more items have been changed.
  * You can now configure lazyinit for the databases. If this is set to true, the datasource will not be initialized while loading the configuration file, but on the first request. This can be useful if the database relies on JNDI objects that are not available at startup.
```
rp.1.dblazyinit=true # Default = false
```
  * You can now fetch update timestamp of the youngest Element with a rule (also supports tree structures).
```
#add this to your configuration. you can the use it with &type=YOUNGEST
cr.youngest=com.gentics.cr.rest.misc.YoungestTimestampContentRepository
```
  * Added methods to clear the datasource cache to the CRDatabaseFactory. Both for single items and for the whole datasource.
```
/**
	 * Clears all cache contents for the object with the given contentid.
	 * @param datasource datasource object on that the cache clear should
	 * 		be performed on.
	 * @param contentId id of the object that should be removed from the cache.
	 */
	public static void clearCache(final Datasource datasource, final String contentId) {
		PortalConnectorHelper.clearCache(datasource, contentId);
	}

	/**
	 * Clears all cache contents for the given datasource.
	 * @param datasource datasource object on that the cache clear should
	 * 		be performed on.
	 */
	public static void clearCache(final Datasource datasource) {
		PortalConnectorHelper.clearCache(datasource);
	}
```
  * Introduced new feature (still experimental): faceted search
    * it is now possible to enrich search results with a result count in certain configurable categories (and subcategories)
    * a taxonomy is created by the indexer, the taxonomy is a special index which contains all the categories and subcategories used in the main index
    * the taxonomy is used by the searcher to create the category count
    * the output of the category count is returned in the MetaResolveable
    * TODO: category drilldown: narrow the search result to one category (with all subcategories)
    * Further information: [Apache Lucene Faceting module Ticket in Apache Lucene JIRA](https://issues.apache.org/jira/browse/LUCENE-3079) & [Apache Lucene Contrib-Facet JavaDoc](http://lucene.apache.org/core/old_versioned_docs/versions/3_5_0/api/contrib-facet/index.html)
    * Configuration:
```
# To enable the faceted search:

# in the indexer.properties
index.DEFAULT.facet=true

# in the search.properties
rp.1.facet=true

## a complete configuration example is available in the config-samples
```

# Bugfixes #


