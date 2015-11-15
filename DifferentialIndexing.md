This page describes how you can enable differential indexing with your search installation.

# Configuration #

Now a updatetimestamp - attribute has to be configured to enable differential indexing.

This attribute will be used to check for changes. Every time the attribute of the fetched objects (from the content repository) is not equal to the according object in the index, the object will be reindexed.

```
# Enables the differential indexing and uses the attribute set in this option
index.DEFAULT.CR.PAGES.updateattribute=updatetimestamp
```

This has to be configured for every CR that should have differential indexing enabled.