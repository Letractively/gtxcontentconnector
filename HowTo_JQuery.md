# You want to access via jQuery? #
Nothing is simpler than that. First you have to get all the dependencies you need:
  * The latest jQuery [release](http://docs.jquery.com/Downloading_jQuery)
  * [Gentics Portal.Node SDK](http://www.gentics.com/Content.Node/products/portalnodesdk/gentics_portalnode_4_sdk_ueberblick.php) (here short GPN SDK)
  * Gentics Portal.Node SDK Licence Key

## Preparing ##
First you have to set up Gentics Portal.Node SDK. Use the [InstallationGuide](http://www.gentics.com/help/topic/com.gentics.portalnode.sdk.doc/misc/doc/sdkguide/xhtml/sdkguide-installation.html) if you need any help.

Then you can set up your WebProject.
Click File->New->Project in your GPN SDK and select "Dynamic Web Project". Go to the next page and enter a project name (We will use "CCR\_Samples" here).

[![](http://farm3.static.flickr.com/2743/4151381742_e13d239bc5_t.jpg)](http://www.flickr.com/photos/27618206@N05/4151381742/sizes/o/)
[![](http://farm3.static.flickr.com/2612/4150622755_7f8783bfc2_t.jpg)](http://www.flickr.com/photos/27618206@N05/4150622755/sizes/o/)
[![](http://farm3.static.flickr.com/2572/4151381794_272a109ccf_t.jpg)](http://www.flickr.com/photos/27618206@N05/4151381794/sizes/o/)
[![](http://farm3.static.flickr.com/2801/4151381848_7f341e7142_t.jpg)](http://www.flickr.com/photos/27618206@N05/4151381848/sizes/o/)


### Fix two small Bugs ###
Unfortunately there is a small issue in the delivered sample project, so you have to make a change in the project settings.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=w4T3f-VwxBs' target='_blank'><img src='http://img.youtube.com/vi/w4T3f-VwxBs/0.jpg' width='425' height=344 /></a>

And you have to add the following [missing library](http://code.google.com/p/gtxcontentconnector/source/browse/branches/stable/Servlets/WebContent/WEB-INF/lib/ezmorph-1.0.jar) to the project 'GEP ContentRepository/WebContent/WEB-INF/lib'

## The Coding ##
Now we can get going:

Navigate to your newly created project and into the /WebContent/ folder. There we can create our HTML-File and name it index.html:

```
<html>
  <head>
    <title>My First Content Connector Example</title>
    <script src="jquery.js" type="text/javascript"></script> 
  </head>
  <body>
   <script type="text/javascript">
   var custom = {};
   custom.ccrlocation="/ContentRepository/ccr";
   $(function() {
   	$('#update-target a').click(function() {
   	  	$.getJSON(custom.ccrlocation+"?   filter=object.obj_type==10007&attributes=contentid&attributes=name&attributes=filename&sor   ting=contentid:asc&type=json&count=30",
   	  	function(data){
   	  	  $('<p></p>').html('status: '+data.status).appendTo('#update-target ol');
             $.each(data.Objects, function(i,item){
               
               $('<li></li>')
   		            .html(i+' '+item.attributes.name+' -    '+item.attributes.filename+' - '+item.attributes.contentid)
   		            .appendTo('#update-target ol');
               
             });
           }); //close $.ajax(
       }); //close click(
   }); //close $(
   </script>
   <div id='update-target'><a href="#">Click here to load data</a>
   <ol></ol>
   </div>
  </body>
</html>
```

After placing the jquery.js in the same folder, you are ready to test the page in your browser.

## The Testing ##
Activate the "J2EE-Perspective" with Window->Open Perspective->Other and select "Java EE". There you should see a Servers View where you have predefined Servers. We will use "Gentics Enterprise Portal". You can drag&drop your Dynamic Web Project from the Project Explorer to the Server.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=PSfDRFF9JvQ' target='_blank'><img src='http://img.youtube.com/vi/PSfDRFF9JvQ/0.jpg' width='425' height=344 /></a>

Now that the Project is deployed, you are ready to start the server with a right click on the instance which brings up a context menu. Now select "Start" and on it goes.

If you have problems on a slow machine and your server is abborting the startup you may increase the allowed startup time of the server:

<a href='http://www.youtube.com/watch?feature=player_embedded&v=mWAlZ0qMzN8' target='_blank'><img src='http://img.youtube.com/vi/mWAlZ0qMzN8/0.jpg' width='425' height=344 /></a>

After the server has started you should be able to access your example via http://localhost:42881/Example.

## The Playing around ##
Edit this code and play around. Try to add additional attributes to the json-url (e.g. ...&attributes=content&attributes=updatetimestamp) and make the output more beautiful ;)

Visit [Usage](Usage.md) for more information.

## You want more? ##
View [More examples](http://code.google.com/p/gtxcontentconnector/source/browse/#svn%2Fbranches%2Farchive%2Ftrunk_20.04.2010%2FExamples%2FExamples)