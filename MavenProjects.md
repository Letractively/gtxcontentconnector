This is the list of maven artifacts in the Gentics Content Connector project. Here you can find what they can be used for

# POM #
```
  <dependency>
      <groupId>com.gentics</groupId>
      <artifactId>[Artifact ID]</artifactId>
      <version>[Version]</version>
    </dependency>
```

# Example dependency #

```
  <dependency>
      <groupId>com.gentics</groupId>
      <artifactId>contentconnector-core</artifactId>
      <version>1.12.0</version>
  </dependency>
```

# Artifact IDs #

  * contentconnector-apihelper
  * contentconnector-config-sample
  * contentconnector-core
  * contentconnector-http
  * contentconnector-json
  * contentconnector-jtidy
  * contentconnector-language
  * contentconnector-lucene: artifact with all lucene specific parts. can be used for indexing and/or searching a lucene index in the same way you use the other request processors.
  * contentconnector-lucene-autocomplete
  * contentconnector-poi-transformer
  * contentconnector-portlet
  * contentconnector-rss
  * contentconnector-servlet: contains the restfull servlets used to access repositories over http. not necessary if you only use the java api to get objects from the repositories.
  * contentconnector-servlet-sample
  * contentconnector-social-networking

# Maven Settings #
In order to retrieve all dependencies (Gentics Portal.Node) you have to have a valid license key. It has to be referenced in your settings.xml. For further Information visit https://maven.gentics.com/