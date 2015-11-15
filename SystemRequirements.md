This page will give some facts about the required system configuration

# General Requirements #

The framework can be used in web applications as well as in other environments.

It requires a JRE to work.

The amount of Memory and CPU power needed strongly depends on the implementation and the amount of data you want to work with.

# Plugins and Transformer #

ATTENTION:
The Transformer com.gentics.cr.lucene.indexer.transformer.lang.LanguageIdentifyer will significantly increase the amount of needed memory.

# Indexing with lucene #
When you have a great amout of data that should be indexed, please keep in mind that is is somewhat costly in terms of cpu and memory usage. You might want to provide a dedicated search/index server.