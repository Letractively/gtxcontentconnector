This article describes how to set up stemming in your lucene search.

# Introduction #

In linguistic morphology, stemming is the process for reducing inflected (or sometimes derived) words to their stem, base or root form – generally a written word form. The stem need not be identical to the morphological root of the word; it is usually sufficient that related words map to the same stem, even if this stem is not in itself a valid root. The algorithm has been a long-standing problem in computer science; the first paper on the subject was published in 1968. The process of stemming, often called conflation, is useful in search engines for query expansion or indexing and other natural language processing problems.

A stemmer for English, for example, should identify the string "cats" (and possibly "catlike", "catty" etc.) as based on the root "cat", and "stemmer", "stemming", "stemmed" as based on "stem". A stemming algorithm reduces the words "fishing", "fished", "fish", and "fisher" to the root word, "fish".

# Available stemmers #
The following stemmers are available by default and can be configured for any desired field:
  * Danish
  * Dutch
  * English
  * Finnish
  * French
  * German
  * German2
  * Hungarian
  * Italian
  * Kp
  * Lovins
  * Norwegian
  * Porter
  * Portuguese
  * Romanian
  * Russian
  * Spanish
  * Swedish
  * Turkish

# Configuration #

Have a look at IndexPerFieldAnalyzer in order to know the basic configuration possibilities.

A Snowball stemmer is defined as follows:

```
content.analyzerclass=com.gentics.cr.lucene.analysis.WrappedSnowballAnalyzer
content.fieldname=content
content.stemmername=English
```

This would initialize an english snowball stemmer. You can change English for any other snowball stemmer available.

# Additional Info #
  * [How to create a new Stemmer](http://e-mats.org/2009/05/modifying-a-lucene-snowball-stemmer/)

There are more stemming implementations that do not use the snowball classes e.g.:
  * Arabic
  * Brazilian

To use these you have to configure their classes like follows
```
content_b.analyzerclass=org.apache.lucene.analysis.br.BrazilianAnalyzer
content_b.fieldname=content_b
content_a.analyzerclass=org.apache.lucene.analysis.ar.ArabicAnalyzer
content_arfieldname=content_a
```