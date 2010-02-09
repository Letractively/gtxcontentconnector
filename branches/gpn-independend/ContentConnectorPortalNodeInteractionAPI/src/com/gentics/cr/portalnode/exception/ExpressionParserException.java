package com.gentics.cr.portalnode.exception;

/**
 * Wrapping class for ExpressionParserExcpetion generated from Gentics
 * datasources.
 * @author bigbear3001
 *
 */
public abstract class ExpressionParserException extends
    com.gentics.api.lib.expressionparser.ExpressionParserException {

  /**
   * generated serialVersionUID.
   */
  private static final long serialVersionUID = -6620642065227606448L;

  /**
   * Wrapping constructor for ExpressionParserExcpetion.
   * @param message Message describing the cause of the Exception
   */
  public ExpressionParserException(final String message) {
    super(message);
  }

  @Override
  protected abstract String getGeneralExceptionMessagePrefix();

}
