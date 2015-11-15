# Important Changes #
  * Have a look at [Changelog\_1111](Changelog_1111.md)
  * 1.11.3 Transformers where moved from contentconnector-lucene to contentconnector-core
  * 1.11.24 Removed deprecated method CRResolvableBean#getMother, method used a non API return type

# Configuration Changes #

# Features #
  * 1.11.4 Implement simple filtering for the StaticObjectHolderRequestProcessor
  * 1.11.5 DirectoryScanner now works with sub directories
  * 1.11.6 the nagios servlet now has the option to pass the idx parameter with a wildcard `*` to see all index states with one call
  * 1.11.14 Synonyms, you can now Index Synonyms (SynonymIndexExtentsion) and add them to search query (SynonymQueryParser), for configuration and further information see the wiki page

# Bugfixes #
  * 1.11.3 IndexAccessorFactory can now be destroyed
  * 1.11.9 catch OutOfMemory erros completly in LuceneIndexUpdateCheckers and release IndexReader with write permissions to free the index
  * 1.11.10 added finalizers for CRSearcher and LuceneRequestProcessor to be able to shutdown the lucene implmentation correctly
  * 1.11.12 DefaultIndexAccessor - reduced memory and cpu usage by reducing unused threads per index accessor
  * 1.11.12 RestServlet - output the runtime and url also for requests that caused an error
  * 1.11.13 IndexLocation.createAllCRIndexJobs: Now Index-Jobs will be created in alphabetical order

# Other #
  * 1.11.25 Test Release with Jenkins and Maven Release Plugin

