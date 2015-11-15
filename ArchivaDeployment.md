# Requirements #
  * SVN Client
  * Maven
  * Archiva Server and Permission to Deploy


# Details #

To deploy a version of Gentics ContentConnector to your own archiva repository follow these steps:
  1. Checkout the code from the SVN
```
svn checkout https://gtxcontentconnector.googlecode.com/svn/branches/1.11.0
```
  1. Package all project of the gentics contentconnector
```
mvn package
```
  1. ensure that the unit tests were successfully
  1. add the following code to the main pom.xml of the contentconnector. if you don't alow anonymous deployment on your archiva server, rember to add the username and password for the server in your settings.xml. also take a look at http://archiva.apache.org/docs/1.3.1/userguide/deploy.html
```
	<distributionManagement>
		<repository>
			<id>myrepository.releases</id>
			<name>releases</name>
			<url>http://127.0.0.1:8080/archiva/repository/myrepository.releases.path/</url>
		</repository>
	</distributionManagement>
```
  1. deploy the prebuilt artifacts to archiva
```
mvn deploy
```