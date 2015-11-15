# Important Changes #
  * To achieve stemming you now have to configure the correct analyzer for the fields you want to stemm in the analyzer.properties

# Configuration changes #

# Features #
  * Added a new transformer for XLSX documents that works better with large files (>=30 MB)
```
# XLSX
index.DEFAULT.CR.FILES.transformer.4.attribute=binarycontent
index.DEFAULT.CR.FILES.transformer.4.rule=object.obj_type==10008
index.DEFAULT.CR.FILES.transformer.4.transformerclass=com.gentics.cr.lucene.indexer.transformer.xlsx.XLSXContentTransformer
```


# Bugfixes #
  * Fixed debug output in indexer servlet
  * Fixed a bug where a UnsupportedOperationException would occure when using DYM and complex queries

