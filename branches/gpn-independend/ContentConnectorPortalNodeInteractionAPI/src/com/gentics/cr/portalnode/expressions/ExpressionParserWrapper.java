package com.gentics.cr.portalnode.expressions;

import com.gentics.api.lib.expressionparser.ExpressionParser;
import com.gentics.cr.portalnode.exception.ParserException;

/**
 * Wrapper class for the ExpressionParser contained in Gentics Portal.Node.
 * @author bigbear3001
 * TODO: i'm sure there are better ways than to wrap a singleton multiple times.
 * I only don't know them yet. Feel free to make it better.
 */
public final class ExpressionParserWrapper {

  /**
   * Variable holding the wrapped singleton of the ExpressionParser.
   */
  private ExpressionParser wrappedExpressionParser = null;

  /**
   * private constructor so nobody can get a new instance.
   */
  private ExpressionParserWrapper() { }

  /**
   * private constructor for generating a new wrapper enclosing the singleton.
   * @param expressionParser the singleton to wrap
   * @see http://tinyurl.com/portalnodeexpressionparserinst
   */
  private ExpressionParserWrapper(final ExpressionParser expressionParser) {
    this.wrappedExpressionParser = expressionParser;
  }

  /**
   * Wraps the {@link ExpressionParser#getInstance()} method.
   * @return a new ExpressionParserWrapper enclosing the singleton of the
   * {@link ExpressionParser}
   */
  public static ExpressionParserWrapper getInstance() {
    return new ExpressionParserWrapper(ExpressionParser.getInstance());
  }
  /**
   * Parses the given Method into an Expression.
   * @param rule Gentics Portal.Node compatible Expression
   * @return wrapped Expression genrated from the Expression
   * @throws ParserException when there occured errors parsing the Expression
   */
  public Expression parse(final String rule) throws ParserException {
    if (this.wrappedExpressionParser == null) {
      this.wrappedExpressionParser = ExpressionParser.getInstance();
    }
    try {
      return (Expression) this.wrappedExpressionParser.parse(rule);
    } catch (com.gentics.api.lib.exception.ParserException e) {
      throw (ParserException) e;
    }
  }
}
