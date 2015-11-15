# Introduction #

You can request content object from the Content Connector through different ways.

  * via ContentID
  * via Filterrule
  * via Beautiful URL
  * via Lucene Query Language


## request via ContentID ##
You get a result based on a ContentID. You receive one object.

This call delivers an object with the ContentID 90033.305.

/ccr/ccr?contentid=90033.305


## request via Filterrule ##
If you want to request more results based on filters, you can use a filterrule.

How you can create such a rule and what operators you can use, can be read in the following pages:

[Gentics Help Expression Parser](http://www.gentics.com/help/index.jsp?topic=/com.gentics.portalnode.sdk.doc/misc/doc/reference/xhtml/ch04s10.html)

[Gentics Help Rules](http://www.gentics.com/help/topic/com.gentics.portalnode.sdk.doc/misc/doc/reference/xhtml/ref-implementation-rules.html)

This call delivers objects of the type 90033.

/ccr/ccr?filter=object.obj\_type==90033

If you use this component in Gentics Portal.Node as a GenticsPortlet, you can use all portal variables in the rules.

With the following rule you can personalize content for a portal user.

If the content is public or the user's permissions match the content's permissions
((object.public == 1) || (object.permissions CONTAINSONEOF portal.user.systemrole))


## request via Beautiful URL ##
If you request an object via a beautiful URL, the publishing path will be used to find the right object.

In this example the Content Connector Servlet with the name cc\_servlet uses the publishing path /home/images/ to find a file called start.jpg.

Example: http/s://yourserver.com/cc\_servlet/home/images/start.jpg

## Additional parameters ##
**start**
The entry where the request should start with paging. Example start=0 (optional)

**count**
The number of results delivered. Example count=20 (optional)

**sorting**
Object for sorting and the sort order. Example sorting=obj\_typ:desc,firstname:asc (optional)

**attributes**
All attributes which should be in the results. Example attributes=content&attributes=filename (optional)

**mimetypeattribute**
Attribute for defining the Mimetypes of the objects delivered. Example:  mimetypeattribute=mimetype (optional)

## Requesting content objects ##

To get a list of content objects back, getObjects() (function of requestprocessor) will executed in a way that all elements which match a certain rule will be delivered.

They can be loaded with a renderer --> see renderer. This function is in simplecontainer.

## Requesting content ##

To request content, the function getContent() of the request processor is called. This function is in binary container.

## Requesting hierarchies ##

To request hierarchies, getObjects() is called, so that all child elements and their children are loaded. This function is in navigation container.

http://localhost:8080/ccr/nav?rootfilter=object.contentid==10002.278&attributes=name,name_de,name_en,startpage,url,languagecode,node_id,sortorder,permissions&navigation=true&childfilter=object.obj_type==10002

## Request content via Search servlet ##

Using the [Lucene Query Language](http://lucene.apache.org/core/old_versioned_docs/versions/3_0_0/queryparsersyntax.html)

## Content objects in Gentics Content Repository ##
10002 … folders

10007 … pages (html, php, jsp, css, js, xml, text, …)

10008 … binary files (images, files, can be css, js)

## Debugging ##
If your queries are responding with errors, you can add &debug=true as parameter to receive the full stack trace of the exception.

## Renderers ##

Currently we support 4 renderers, they render depending on the type all results and can be chosen via the parameter "type". If you do not select a type, XML is chosen.

Parameter: type

Possible values: XML, PHP, JSON, JavaXML

e.g.: /ccr/ccr?filter=object.obj\_type==90033&type=PHP

If errors occur, please read the page "known errors".

If you can't find any solution, contact us (solution@gentics.com).