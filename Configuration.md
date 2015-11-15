# Configuration #
The following page describes how to configure an instance of the Gentics Content Connector.

## Configuration Folder ##

The application you want to use / deploy the contentconnector to, should be started with the following JAVA\_OPTS
```
-Dcom.gentics.portalnode.confpath="path/to/your/configuration/folder"
```

## Property File ##

Place a property file with the name of the instance (usually the servlet name) in the configuration folder.

A typical configuration file could look like this:

```
#RequestProcessor1 gets the elements from Contentrepository
rp.1.rpClass=com.gentics.cr.CRRequestProcessor
#Datasource
rp.1.ds-handle.type=jdbc
rp.1.ds-handle.driverClass=com.mysql.jdbc.Driver
rp.1.ds-handle.url=jdbc:mysql://localhost:3306/lucene_datasource?user=node
rp.1.ds.cache=true
rp.1.ds.cache.foreignlinkattributes=true
rp.1.ds.cache.syncchecking=true
#RequestProcessor
rp.1.response-charset=UTF-8
rp.1.binarytype=10008
rp.1.foldertype=10002
rp.1.pagetype=10007
```

It is also possible to connect to a database.

```
# configure the SQL Processor to search in mysql-db
rp.1.rpClass=com.gentics.cr.SQLRequestProcessor
rp.1.ds-handle.type=jdbc
rp.1.ds-handle.driverClass=com.mysql.jdbc.Driver
rp.1.ds-handle.url=jdbc:mysql://localhost:3306/DBNAME?user=USER&password=SECRET

# SQL Options
rp.1.ds.table=TABLENAME
rp.1.ds.columns=TABLECOLUMNS
rp.1.ds.idcolumn=TABLEID / PRIMARY
```

## Example Files ##
Download the latest [Sample Configuration](http://archiva.gentics.com:8081/archiva/browse/com.gentics/contentconnector-config-sample/) here and extract the ZIP file to a folder of your liking. Then link the com.gentics.portalnode.confpath to the "config" folder that is contained in the ZIP as described in **Configuration Folder**.

The **config** folder contains the following resources:

The **rest** folder contains several configuration files that will expect a contentrepository running on a mysql on your local machine on port 3306. The database should be called lucene\_datasource with a user node that can access it.

The **template** contains the velocity templates for our search response page.

The **index** directory will contain the index, once the indexer did its job.

The **cache.ccf** file can be used to configure the JCS cache. Have a look at [the JCS homepage](http://commons.apache.org/jcs/) for further information.

The **nodelog.properties** file contains the configuration for Log4J.