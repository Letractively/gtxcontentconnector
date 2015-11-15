This article describes how to use the TwitterJSONRequestProcessor

# Introduction #

With the TwitterJSONRequestProcessor you can easily access the Twitter API and return search results as Resolvables known to the Gentics Content Connector and Gentics Portals.

# Details #

Checkout the contentconnector-social-networking project and add it as dependency to your project. It contains the class TwitterJSONRequestProcesser that you can configure as your RequestProcessor

```
#RequestProcessor2 gets the elements from Contentrepository
rp.1.rpClass=com.gentics.cr.sn.TwitterJSONRequestProcessor
```

By default this class contacts the JSON Twitter API with the provided search terms and returns the found tweets as Resolvables.