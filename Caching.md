This article describes how caching works with the Gentics Content Connector

# Caching #

Caching of objects and in Gentics Content Connector is done by the Gentics Portal Connector datasource.

Additionally rendered contents and plinks are cached in the crcontent cache.
TODO: describe this in more detail.


# Configuraiton #

Example:
```
rp.1.ds.cache=true
rp.1.ds.cache.foreignlinkattributes=true
rp.1.ds.cache.syncchecking=true
```

Explained:
```
#With this parameter, the datasource cache can be switched on. Datasource cache will increase performance when accessing objects coming from this datasource.	DEFAULT: false
cache=false

#When enabling the datasource cache for datasources that are filled by Gentics Content.Node;, you should set this to true . When syncchecking is enabled, Gentics Portal.Node checks frequently for an updated contentrepository and will clear all relevant caches.	DEFAULT: false
cache.syncchecking=false

#Interval in seconds for background checks whether the updatetimestamp of a Content Repository has changed (when cache.syncchecking is enabled).	DEFAULT: 10
cache.syncchecking.interval=10	

#When the datasource parameters cache and cache.syncchecking are enabled for this datasource, setting this parameter to true will modify the behaviour of clearing the caches when datasource modifications are detected: By default, all datasource caches (queries, objects, attributes) are cleared, but with differential syncchecking, only the modified objects (and their attributes) are removed from the cache (together with all query results).	DEFAULT: true
cache.syncchecking.differential=true	 

#When caching for this datasource is activated, all attributes would be cached. With this setting, the caching for the attribute attributename can be deactivated.	DEFAULT true
cache.attribute.[attributename]=true

#With this parameter, the cache region for attribute attributename can be defined. This can be used to have better control of how specific attributes will be cached.	 DEFAULT gentics-portal-contentrepository-atts	 
cache.attribute.[attributename].region=gentics-portal-contentrepository-atts	 

#Setting this parameter to true will enable the caching also for foreign link attributes (when the cache is enabled at all). This should not be done for writeable datasources, because the cache for foreign linked attributes can not be dirted correctly and this will most likely lead to inconsistent cache data. If you use caching for a datasource filled from Gentics Content.Node; and have cache.syncchecking set to true, you should also set cache.foreignlinkattributes to true.	DEFAULT:false
cache.foreignlinkattributes=false	
```

It is possible to deactivate the crcontent cache with setting the crcontentcache property of the RequestPocessor to false, default is true:
```
#deactivate the content cache of the request processor
rp.1.crcontentcache=false
```

# Clearing the cache #

The cache can either be cleared directly using Java and the class CRDatabaseFactory, or you can configure a cache clear servlet, that provides this functionality.

```
	<servlet>
		<servlet-name>clearcache</servlet-name>
		<servlet-class>com.gentics.cr.servlet.system.ClearCacheServlet</servlet-class>
	</servlet>
```

You have to configure the datasource for the servlet that should be affected by the cache clear:

```
#Datasource
ds-handle.type=jdbc
ds-handle.driverClass=com.mysql.jdbc.Driver
ds-handle.url=jdbc:mysql://localhost:3306/lucene_datasource?user=node
ds.cache=true
ds.cache.foreignlinkattributes=true
ds.cache.syncchecking=true
dblazyinit=true
```

There are two possible modes.

Clear the cache for one item:

`/servlet-sample/clearcache?contentid=10007.123`

Clear the complete cache for the datasource

`/servlet-sample/clearcache?all=true`