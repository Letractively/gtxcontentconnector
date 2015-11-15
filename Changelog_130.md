# Configuration changes #
  * The Query Parser can now be configured :
```
rp.1.queryparser.class=com.gentics.cr.lucene.search.query.CRComplexPhraseQueryParser
```


# Features #
  * Lucene Update 3.0.1 => 3.0.2
  * Added ComplexPhraseQueryParser capability to support wildcards in phrases e.g. "The Movie`*`"
  * Added Feature for References in Configuration Files, now we can maintain all ds urls in one file. Referencing properties in the same Configuration file is not yet supported.
```
#loads path.to.property.B from anotherpropertyfilename.properties
path.to.property.A=&{anotherpropertyfilename.path.to.property.B}
```
  * MergeTransformer can now add static strings
```
...transformer.1.sourceattributes=directory,"/",filename
...transformer.1.targetattribute=path
...transformer.1.rule=1==1
...transformer.1.transformerclass=com.gentics.cr.lucene.indexer.transformer.MergeTransformer

```
  * The renderers (ContentRepository) can now contain deployable objects. This is now used in the VelocityContentRepository. In connection with the default RESTServlet, you can now access the Request and the session in the velocity context.
```
${request.parameters.test} #<= fetches the parameter test (smart as string or array depending on the size)
${request.parameters.get("test")} #<= same as above
${request.parameterMap} #<= default servlet parameterMap
${request.parameterMap.test} #<= parameter test as array
```
  * The indexer servlet now gives information about the optimization status of the indexes managed by the servlet. Furthermore a optimize job can now be started through the servlet.

# Bugfixes #
  * Fixed: [Issue 51](https://code.google.com/p/gtxcontentconnector/issues/detail?id=51):       Deadlock after broken DB connection
  * Fixed: [Issue 50](https://code.google.com/p/gtxcontentconnector/issues/detail?id=50):       Lucene DefaultMultiIndexAccessor throws NPE
  * Fixed: [Issue 49](https://code.google.com/p/gtxcontentconnector/issues/detail?id=49):       Start/Stop via IndexerServlet
  * Fixed: [Issue 45](https://code.google.com/p/gtxcontentconnector/issues/detail?id=45):       Transformer "LanguageIdentifyer" doesn't work