The article describes how to increase the performance of the embedded [Apache Lucene](http://lucene.apache.org)

# Introduction #

The following page should give you some basic understanding on how to increase search and indexing performance.

# Indexing Performance #

If you have sufficient system memory available you can increase the number of objects fetched at a time from the requests processor:

```
#How many items should be processed at once. Decrease this number if you have memory problems.
index.DEFAULT.batchsize=10
#Specific batch size for the configuration block (PAGES) decrease this value if you have memory problems. (overwrites index.DEFAULT.batchsize)
index.DEFAULT.CR.PAGES.batchsize=100
```

# Search Performance #

Check your queries!

Rule of thumb: The shorter a query - the faster it is.
This applies only for the standard operations.

Operations like fuzzy and wild card search take additional time.

Also the highlighting of terms in the results takes some time.

# External Articles #

Have a look on the following articles to better understand how lucene works:
  * http://onjava.com/pub/a/onjava/2003/03/05/lucene.html?page=1