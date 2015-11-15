This articles describes the deployment structure for the Apache Lucene Webapp.

# Details #
## Webapp ##

> `<tomcathome>/webapps/<CCWebapp>/`<br />
> `<tomcathome>/webapps/<CCWebapp.war>`

**CCWebapp**

`/WEB-INF/web.xml`
> contains the config for search- and indexerservlet

> indexerconfig example
```
<servlet>
  <description>
    Gentics .Node ContentConnector Indexer Application
  </description>
  <display-name>indexer</display-name>

  <servlet-name>indexer</servlet-name>
  <servlet-class>com.gentics.cr.lucene.IndexJobServlet</servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
  <servlet-name>indexer</servlet-name>
  <url-pattern>/indexer/*</url-pattern>
</servlet-mapping>
```

> searchconfig example
```
<servlet>
  <servlet-name>search</servlet-name>
  <servlet-class>com.gentics.cr.rest.RESTServlet</servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
  <servlet-name>search</servlet-name>
  <url-pattern>/search/*</url-pattern>
</servlet-mapping>
```

`/WEB-INF/lib/`
> contains Lucene Libs, Transformator Libs and CC Libs

## Config ##

> `<tomcathome>/conf/gentics/rest/`
> > indexer.properties, analyzer.properties and search.properties


> `<tomcathome>/conf/gentics/nodelog.properties`

> `<tomcathome>/conf/gentics/cache.ccf`

## Indexer Files ##
> Directory for the indexerfiles (configured in indexer.properties)