This article describes how a Gentics Content Portlet can be deployed in a Liferay portal.

  1. Download Liferay Portal 5.2.3 (comes with tomcat 6.0.18)
  1. Edit the setenv.sh or setenv.bat (depending on your platform) and add
```
      -Dcom.gentics.portalnode.confpath="%CATALINA_HOME%/conf/gentics"
```
  1. Use svn to checkout LiferayConfig from the projects trunk and place the contents of the gentics folder in %CATALINA\_HOME%/conf/gentics
    * You can configure the datasources by editing the files in %CATALINA\_HOME%/conf/gentics/rest
  1. Use svn to checkout LifeRayContentPortlets and ContentConnectorAPI from the projects trunk to a Gentics Portal.Node SDK (or use eclipse instead and download needed jars seperately)
    * Take care about the J2EE dependencies (do not export portlet\_20\_Java14compliant.jar)
    * Build a WAR file from the LifeRayContentPortlets projekt and place it in the deploy folder of Liferay)
  1. start the Liferay server and enjoy gentics content ;)


Additionally you can modify the design of the portlets by editing the template files in /WebContent/WEB-INF/template/ (tree.vm for the navigation portlet and ./content/content.jsp for the content portlet). Other template files can be configured in the portlet.xml file as init parameters for the portlets.