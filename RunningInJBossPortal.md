1. Download JBoss Portal with JBoss AS

2. Deploy war file to server/default/deploy

3. Add to startup
> `rem Setup the com.gentics.portalnode.confpath`

> `set JAVA_OPTS=%JAVA_OPTS% -Dcom.gentics.portalnode.confpath=`

> and place ContentConnecter config (gentics/rest)
> and cache.ccf in the right dirs    (gentics)

4. Add shared libraries to /server/default/lib

  * velocity-dep-1.4
  * velocity-stringresource
  * velocity-tools-1.2
  * gentics-autogenerated-jaxb
  * gentics-portalnode
  * gentics-portalnode-api
  * jcs-1.2.7.9
  * mysql-connector-java-5.1.6-bin
  * commons-dbcp-1.2.1
  * commons-pool-1.2
  * commons-lang-2.1