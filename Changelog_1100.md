# Important Changes #

# Configuration changes #

# Features #
  * the velocity tools are now available in the VelocityContentRepository via $tools so now you can use the following code to prevent xss in your search template
```
$tools.esc.html($yourquery)
```
  * You now have the possibility in the LuceneRequestProcessor to configure the handling of the MaxClauseCount exception. Therefore you have the possiblity to give your users a hint that they should use a longer searchterm. See example in search.properties (as always commented out per default):
```
#throw an exception if the maxclausecount is reached and catched, this is useful for handling in the ContentRepository#respondWithError()
rp.1.failOnMaxClauses=true
```
  * Now it is possible to deactivate the crcontent cache with setting the crcontentcache property of the RequestPocessor to false, default is true:
```
#deactivate the content cache of the request processor
rp.1.crcontentcache=false
```

# Bugfixes #


