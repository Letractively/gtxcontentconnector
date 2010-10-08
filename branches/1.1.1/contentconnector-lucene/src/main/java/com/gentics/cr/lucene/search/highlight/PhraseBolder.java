package com.gentics.cr.lucene.search.highlight;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenGroup;

import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.lucene.indexer.index.LuceneAnalyzerFactory;
import com.gentics.cr.monitoring.MonitorFactory;
import com.gentics.cr.monitoring.UseCase;
/**
 * 
 * Last changed: $Date: 2009-06-26 15:48:16 +0200 (Fr, 26 Jun 2009) $
 * @version $Revision: 105 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class PhraseBolder extends ContentHighlighter implements Formatter {

  private Analyzer analyzer=null;
  private int numMaxFragments=3;
  private int fragmentSize=100;

  private String highlightPrefix="";
  private String highlightPostfix="";
  private String fragmentSeperator="";

  /**
   * number of fragments we should return. (maximum)
   */
  private static final String NUM_MAX_FRAGMENTS_KEY = "fragments";

  /**
   * size of fragments in words.
   */
  private static final String NUM_FRAGMENT_SIZE_KEY = "fragmentsize";
  /**
   * prefix for highlighted text.
   */
  private static final String PHRASE_PREFIX_KEY = "highlightprefix";
  /**
   * postfix for highlighted text.
   */
  private static final String PHRASE_POSTFIX_KEY = "highlightpostfix";
  /**
   * Configuration Key for fragment seperator.
   */
  private static final String FRAGMENT_SEPERATOR_KEY = "fragmentseperator";

  /**
   * regex to remove text from fragments (e.g. leading commas and spaces)
   */
  private static final String REMOVE_TEXT_FROM_FRAGMENT_REGEX = "^[\\r\\n),.: -]*";

  /**
   * Create new Instance of PhraseBolder.
   * @param config
   */
  public PhraseBolder(GenericConfiguration config) {
    super(config);
    analyzer = LuceneAnalyzerFactory.createAnalyzer(config);

    highlightPrefix = (String)config.get(PHRASE_PREFIX_KEY);
    if(highlightPrefix==null)highlightPrefix="<b>";
    highlightPostfix = (String)config.get(PHRASE_POSTFIX_KEY);
    if(highlightPostfix==null)highlightPostfix="</b>";
    fragmentSeperator = (String)config.get(FRAGMENT_SEPERATOR_KEY);
    if(fragmentSeperator==null)fragmentSeperator=" ... ";
    
    String nmF = (String)config.get(NUM_MAX_FRAGMENTS_KEY);
    if(nmF!=null)
    {
      try
      {
        int i = Integer.parseInt(nmF);
        numMaxFragments = i;
      }catch(NumberFormatException e)
      {
        log.error("The configured count of fragments for this ContentHighlighter is not a number");
      }
    }
    String nFS = (String)config.get(NUM_FRAGMENT_SIZE_KEY);
    if(nFS!=null)
    {
      try
      {
        int i = Integer.parseInt(nFS);
        fragmentSize = i;
      }catch(NumberFormatException e)
      {
        log.error("The configured size for fragments for this ContentHighlighter is not a number");
      }
    }
  }

  /**
   * Highlights Terms by enclosing them with &lt;b&gt;term&lt;/b&gt;
   * @param originalTermText 
   * @param tokenGroup 
   * @return 
   */
  public String highlightTerm(String originalTermText, TokenGroup tokenGroup) {
    UseCase uc = MonitorFactory.startUseCase(
    "Highlight.PhraseBolder.highlightTerm()");
    if (tokenGroup.getTotalScore() <= 0) {
      uc.stop();
      return originalTermText;
    }
    uc.stop();
    return highlightPrefix + originalTermText + highlightPostfix;

  }


  /**
   * @param attribute 
   * @param parsedQuery 
   * @return 
   * 
   */
  public String highlight(String attribute, Query parsedQuery) {
    UseCase uc = MonitorFactory.startUseCase(
        "Highlight.PhraseBolder.highlight()");
    String result = "";
    if (attribute != null && parsedQuery != null) {
      Highlighter highlighter =
        new Highlighter(this, new QueryScorer(parsedQuery));
      highlighter.setTextFragmenter(new WordCountFragmenter(fragmentSize));

      TokenStream tokenStream = analyzer.tokenStream(
          this.getHighlightAttribute(), new StringReader(attribute));
      try {
        UseCase ucFragments = MonitorFactory.startUseCase(
        "Highlight.PhraseBolder.highlight()#getFragments");
        TextFragment[] frags = highlighter.getBestTextFragments(tokenStream,
            attribute, true, numMaxFragments);
        ucFragments.stop();
        boolean first = true;
        for (TextFragment frag : frags) {
          if (!first) {
            result += fragmentSeperator;
          } else {
            first = false;
          }
          String fragment = frag.toString()
              .replaceAll(REMOVE_TEXT_FROM_FRAGMENT_REGEX, "");
          result += fragment;
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (InvalidTokenOffsetsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    uc.stop();
    return result;
  }

}