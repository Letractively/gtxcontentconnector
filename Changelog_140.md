# Configuration changes #

# Features #
  * Updated Lucene from 3.0.2 to 3.0.3
  * A new version of the VectorBolder (WhitespaceVectorBolder) has been added. FastVectorHighlighter does not take word boundaries into consideration when building fragments, so that in most cases the first and last word of a fragment are truncated. This makes the highlights less legible than they should be. This new WhitespaceVectorBolder resolves this by expanding the start and end boundaries of the fragment to the first whitespace character on either side of the fragment, or the beginning or end of the source text, whichever comes first. This significantly improves legibility, at the cost of returning a slightly larger number of characters than specified for the fragment size. This will not work for CJK.
```
##Configure the highlighters (sample for VectorBolder)
##Vector Bolder needs Vectors stored in the index so check your indexer.properties
##if index.DEFAULT.CR.FILES.storeVectors is set to true (default)
rp.1.highlighter.1.class=com.gentics.cr.lucene.search.highlight.WhitespaceVectorBolder
##Set the attribute that should be highlighted
rp.1.highlighter.1.attribute=content
##Set the rule that defines the objects that should be highlighted with this highlighter
rp.1.highlighter.1.rule=1==1
##Set the max number of fragments that should be present in the highlighted text
rp.1.highlighter.1.fragments=5
##Set the max number of words a fragment should contain
rp.1.highlighter.1.fragmentsize=100
##Set the tags that should be used for highlighting
rp.1.highlighter.1.highlightprefix=<b>
rp.1.highlighter.1.highlightpostfix=</b>
##Set the seperator that should be used between the fragments
rp.1.highlighter.1.fragmentseperator=...
```

# Bugfixes #
  * Fixed [Issue 54](https://code.google.com/p/gtxcontentconnector/issues/detail?id=54)
  * Fixed [Issue 55](https://code.google.com/p/gtxcontentconnector/issues/detail?id=55) - Now the Following file extensions and mimetypes are supported for now
```
#EXTENSIONS
gif=gif
png=png
jpg=jpg
jpeg=jpg
bmp=bmp
tif=tiff
tiff=tiff

#MIMETYPES
image/gif=gif
image/png=png
image/jpeg=jpg
image/pjpeg=jpg
image/bmp=bmp
image/x-windows-bmp=bmp
image/tiff=tiff
image/x-tiff=tiff
```