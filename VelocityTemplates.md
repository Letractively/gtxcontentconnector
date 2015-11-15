Short Overview about the templating in ContentConnector with velocity templates.

# Create Velocity Templates #

After each request (like searching for a term or conditions) you'll get a velocity-result.
Create a normal template for your application and add your velocity-code.

Example:
```
<form action="velocity" method="post">
	Query: <input type="text" name="filter" id="input_search"/> <input type="submit" name="submit" value="Go"/>
</form>
```

After sending an request to the velocity-template, you have a lot variables available.

**resolvables** 	returns collection
e.g.:
```
#if($resolvables && $resolvables.size()>0 && $resolvables.get(0).contentid == "10001")
##$metaresolvable contains search result meta information like total hits etc.; first resolvable in collection, can be configured in lucene properties
#set($metaresolvable=$resolvables.remove(0))
#end
```

notice: in velocity you have for the collection all implemented methodes available.


**tools**			returns com.gentics.cr.util.velocity.VelocityTools Object
**esc**			returns org.apache.velocity.tools.generic.EscapeTool Object
**encoding** 		returns the encoding-strin, e.g.: utf8

If you'll get an Error-Page, followed variables are available:
**exception**
**debug**

**Further code examples:**

```
#set($start = ($metaresolvable.start + 1))
#set($end = ($metaresolvable.start + $metaresolvable.count))
#if($end && $end > $metaresolvable.totalhits)
	#set($end = $metaresolvable.totalhits)
#end

#if($metaresolvable.totalhits && $metaresolvable.totalhits > 0)
<p id="result_report">
      Showing $start - $end of $metaresolvable.totalhits results
</p>
#else
```

Iterating over the resolveables
```
#foreach($resolvable in $resolvables)
	<h4><a class="title highlight" href="content$resolvable.pub_dir$resolvable.filename">$resolvable.name</a></h4>
	#set($content = "$!{resolvable.content}$!{resolvable.binarycontent}")
##if you are limiting the output size, be sure to not cut html-tags in two -> and configure the highlighter to have max the output you want
	#if (false && $content.length() > 1300)
		#set($content = $content.substring(0,1300)+"...")
	#end
	<p class="description">$!content
	<br />
	<a title="$resolvable.name" class="title" href="content$resolvable.pub_dir$resolvable.filename">content$resolvable.pub_dir$resolvable.filename</a>$!{size}
	</p>
#end
```