# Introduction #
Bug fixes for the DidyoumeanIndexExtension, AutocompleteIndexExtension and DefaultIndexAccessor

# Bugs fixed #
  * fixed: DefaultIndexAccessor opens a IndexWriter and therefore produces a NativeFSLock everytime a reopencheck is perfomed
  * fixed: in Enviroments with multiple IndexLocations the DidyoumeanIndexExtension and AutocompleteIndexExtension would start reindex-Jobs everytime a IndexLocation fires a IndexFinished Event. Both IndexExtensions now only start their Jobs if the IndexLocation to which they extend fired the event.