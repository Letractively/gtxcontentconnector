This page describes how you can use nutch to crawl a website and generate a lucene index that can be used by the gentics content connector.


# Requirements #

  * Index (Crawl) Server - (hint: use linux ;))
  * The latest [nutch release](http://www.apache.org/dyn/closer.cgi/lucene/nutch/).


# Setup #
Connect to your linux index server and navigate to a directory where you want to install nutch. Then download the latest release.

```
//Change the url for your needs
wget http://mirror.deri.at/apache/lucene/nutch/nutch-1.0.tar.gz
```

Then you have to extract the archive

```
tar xzf nutch-1.0.tar.gz
```

# Configuration of Nutch #
Editing the nutch configuration is very simple. You only have to follow these small steps:

## Configure the urls to be fetched ##
First you have to create a custom directory that holds the urls you want to index. Navigate to your nutch base directory and do the following:

```
mkdir urls
cd urls
vim nutch
```

Now you should have opened the file nutch in your new urls directory. There you have to specify the entry page where the crawler should start.

e.g. we want to index http://www.gentics.com => contents of nutch
```
http://www.gentics.com
```

Open the file /conf/crawl-urlfilter.txt and edit the filter for the urls you want to allow the crawler to follow.

We only change the filter to allow all subdomains of gentics.com and leave everything else as it is.
```
# accept hosts in MY.DOMAIN.NAME
+^http://([a-z0-9]*\.)gentics.com/

```

## Configure the crawlers identity ##
Now we have to defind the crawlers identity that will be sent to the server as user agent.

Open the file /conf/nutch-site.xml and insert something like the following:
```
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<!-- Put site-specific property overrides in this file. -->

<configuration>
<property>
  <name>http.agent.name</name>
  <value>nutch</value>
  <description>HTTP 'User-Agent' request header. MUST NOT be empty -
  please set this to a single word uniquely related to your organization.

  NOTE: You should also check other related properties:

        http.robots.agents
        http.agent.description
        http.agent.url
        http.agent.email
        http.agent.version

  and set their values appropriately.

  </description>
</property>

<property>
  <name>http.agent.description</name>
  <value>nutch index crawler</value>
  <description>Further description of our bot- this text is used in
  the User-Agent header.  It appears in parenthesis after the agent name.
  </description>
</property>

<property>
  <name>http.agent.url</name>
  <value>your.website</value>
  <description>A URL to advertise in the User-Agent header.  This will
   appear in parenthesis after the agent name. Custom dictates that this
   should be a URL of a page explaining the purpose and behavior of this
   crawler.
  </description>
</property>
<property>
  <name>http.agent.email</name>
  <value>your.email.com</value>
  <description>An email address to advertise in the HTTP 'From' request
   header and User-Agent header. A good practice is to mangle this
   address (e.g. 'info at example dot com') to avoid spamming.
  </description>
</property>
</configuration>

```

## Highlighted content ##
In order to have a highlighted text snipped available you have to set the content attribute to be stored in the index. Unfortunately nutch sets this value hardcoded to false. You have to replace the existing index-basic plugin jar with the one provided [here](http://gtxcontentconnector.googlecode.com/files/index-basic.jar) for download.

You find the existing plugin in the folder plugins/index-basic.

In order to get nutch to use this settings you have to edit conf/nutch-site.xml and add a block like this:

```
<property>
  <name>plugin.includes</name>
  <value>nutch-extensionpoints|protocol-http|urlfilter-regex|parse-(text|html)|index-basic|query-(basic|site|url)</value>
  <description>Regular expression naming plugin directory names to
  include.  Any plugin not matching this expression is excluded.
  In any case you need at least include the nutch-extensionpoints plugin. By
  default Nutch includes crawling just HTML and plain text via HTTP,
  and basic indexing and search plugins.
  </description>
</property>
```

## Multilanguage ##
If you want to search for specific languages you have to enable the language-identifier plugin to add the lang field to the index.

```
<property>
  <name>plugin.includes</name>
  <value>nutch-extensionpoints|protocol-http|language-identifier|urlfilter-regex|parse-(text|html)|index-basic|query-(basic|site|url)</value>
  <description>Regular expression naming plugin directory names to
  include.  Any plugin not matching this expression is excluded.
  In any case you need at least include the nutch-extensionpoints plugin. By
  default Nutch includes crawling just HTML and plain text via HTTP,
  and basic indexing and search plugins.
  </description>
</property>
```

You also have to add the default configuration for the language-identifier to your nutch-default.xml

```
<!-- language-identifier plugin properties -->
	
	<property>
	  <name>lang.ngram.min.length</name>
	  <value>1</value>
	  <description> The minimum size of ngrams to uses to identify
	  language (must be between 1 and lang.ngram.max.length).
	  The larger is the range between lang.ngram.min.length and
	  lang.ngram.max.length, the better is the identification, but
	  the slowest it is.
	  </description>
	</property>
	
	<property>
	  <name>lang.ngram.max.length</name>
	  <value>4</value>
	  <description> The maximum size of ngrams to uses to identify
	  language (must be between lang.ngram.min.length and 4).
	  The larger is the range between lang.ngram.min.length and
	  lang.ngram.max.length, the better is the identification, but
	  the slowest it is.
	  </description>
	</property>
	
	<property>
	  <name>lang.analyze.max.length</name>
	  <value>2048</value>
	  <description> The maximum bytes of data to uses to indentify
	  the language (0 means full content analysis).
	  The larger is this value, the better is the analysis, but the
	  slowest it is.
	  </description>
	</property>
	
	<property>
	  <name>query.lang.boost</name>
	  <value>0.0</value>
	  <description> Used as a boost for lang field in Lucene query.
	  </description>
	</property>
```

# Start crawling #
Once you configured everything you can test your configuration with a shallow depth and a low number of links to follow:

execute the command from your nutch base directory
```
bin/nutch crawl urls -dir your_crawl_dir -depth 3 -topN 50
```

If you are happy with the results returned, you can increase the values. A common depth value for a full crawl would be around 10 and topN can range from hundreds to hundreds of thousands depending on the sources you want to index/crawl.

You can find more infos about how to crawl on [this tutorial](http://wiki.apache.org/nutch/NutchTutorial).

Once the crawling finished, you can find the index in **nutch\_base\_dir**/**your\_crawl\_dir**/index

If you like the outcome you can create a start script that is being executed as cronjob or called by the scheduler of Gentics Content.Node. Your start script could look something like that:

```
rm -r crawl
bin/nutch crawl urls -dir crawl -depth 5 -topN 50
rm -r index
cp -r crawl/index index
touch index/reopen
```

# More #
  * You can find more information about nutch on the [project homepage](http://lucene.apache.org/nutch/).
  * Running [Nutch on Windows](http://wiki.apache.org/nutch/GettingNutchRunningWithWindows)
    * Running Nutch on Windows [without cygwin](http://wiki.apache.org/nutch/Nutch_on_windows_without_cygwin)