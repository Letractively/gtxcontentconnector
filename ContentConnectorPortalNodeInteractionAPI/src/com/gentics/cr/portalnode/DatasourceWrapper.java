package com.gentics.cr.portalnode;

import com.gentics.cr.portalnode.exception.ExpressionParserException;
import com.gentics.cr.portalnode.expressions.Expression;
/**
 * Wrapper for datasources, so it is possible to access methods that otherwise
 * would need the Gentics API.
 * @author bigbear3001
 *
 */
public class DatasourceWrapper {
  /**
   * variable where the wrapped Datasource is stored in.
   */
  private Datasource wrappedDatasource;

  /**
   * constructor to initialize the datasource wrapper with a given datasource.
   * @param datasource Datasource you want to access methods of
   */
  public DatasourceWrapper(final Datasource datasource) {
    wrappedDatasource = datasource;
  }

  /**
   * Create a DatasourceFilter with the given Expression.
   * @param expression Expression descriping the objects to load
   * @return wrapped DatasourceFilter.
   * @throws ExpressionParserException when the Expression cannot be parsed
   */
  public final DatasourceFilter
      createDatasourceFilter(final Expression expression)
      throws ExpressionParserException {
    try {
      return (DatasourceFilter) wrappedDatasource
        .createDatasourceFilter(expression);
    } catch (com.gentics.api.lib.expressionparser.ExpressionParserException e) {
      throw (ExpressionParserException) e;
    }
  }
}
