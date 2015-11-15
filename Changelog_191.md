# Introduction #
bugfix release for 'parse\_query'-Feature
  * changes in LuceneRequestProcessor caused an ClassNotFoundException in AbstractHTTPClientRequestProcessor (during deserialization of search result)

# Important Changes #
> see Bugfixes


# Configuration changes #
  * To return the parsedQuery with LuceneRequestProcessor
```
rp.1.showparsedquery = true
```

# Bugfixes #
  * the LuceneRequestProcessor only returns parsed\_query if explicit
configured - if no configuration is done version 1.9.1 has same behavior as 1.8.0 in this case