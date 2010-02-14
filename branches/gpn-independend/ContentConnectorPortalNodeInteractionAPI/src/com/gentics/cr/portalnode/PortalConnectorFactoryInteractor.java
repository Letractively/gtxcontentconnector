package com.gentics.cr.portalnode;

import java.util.Properties;

import com.gentics.api.portalnode.connector.PortalConnectorFactory;
import com.gentics.cr.portalnode.exception.DatasourceNotAvailableException;
import com.gentics.cr.portalnode.exception.ParserException;
import com.gentics.cr.portalnode.expressions.Expression;

/**
 * Provides access to the final {@link PortalConnectorFactory}.
 * All method calls are delegated to the {@link PortalConnectorFactory}.
 * @author bigbear3001
 *
 */
public final class PortalConnectorFactoryInteractor {

  /**
   * private constructor to ensure nobody creates an instance of this object.
   * all methods are static
   */
  private PortalConnectorFactoryInteractor() { }
  /**
   * calls the method {@link PortalConnectoryFactory#destroy()}.
   * This should be called when your application  uses no more datasource from
   * Gentics Portal.Node. So Portal.Node can close all handles to the database.
   */
  public static void destroy() {
    PortalConnectorFactory.destroy();
  }
  /**
   * creates a {@link WriteableDatasource} with the given properties and the
   * given handle properties. See the Gentics Portal.Node help for more
   * information.
   * @param dsHandleProps properties for the handle (e.g. datasource connection)
   * @param dsProps properties for the datasource (e.g. caching)
   * @return datasource for the given connection
   * @see http://tinyurl.com/portalnodedatasourceconfigurat
   */
  public static Datasource
    createWriteableDatasource(final Properties dsHandleProps,
      final Properties dsProps) {
    return (Datasource) PortalConnectorFactory
      .createWriteableDatasource(dsHandleProps, dsProps);
  }
  /**
   * creates a {@link WriteableDatasource} with the given properties and the
   * given handle properties. See the Gentics Portal.Node help for more
   * information.
   * @param dsHandleProps properties for the handle (e.g. datasource connection)
   * @return datasource for the given connection
   * @see http://tinyurl.com/portalnodedatasourceconfigurat
   */
  public static Datasource
    createWriteableDatasource(final Properties dsHandleProps) {
    return (Datasource) PortalConnectorFactory
      .createWriteableDatasource(dsHandleProps);
  }
  /**
   * creates an Expression from a given rule.
   * @param rule String with the rule
   * @return Expression generated from the rule
   * @throws ParserException in case the Expression cannot be generated
   */
  public static Expression createExpression(final String rule)
      throws ParserException {
    try {
      return (Expression) PortalConnectorFactory.createExpression(rule);
    } catch (com.gentics.api.lib.exception.ParserException e) {
      throw (ParserException) e;
    }
  }

  /**
   * Get a specific content object from the {@link Datasource}.
   * @param contentid String identifying the object
   * @param datasource {@link Datasource} to get the object from
   * @return {@link Resolvable} with the given identifier
   * @throws DatasourceNotAvailableException if the {@link Datasource} is not
   * available.
   */
  public static Resolvable getContentObject(final String contentid,
      final Datasource datasource) throws DatasourceNotAvailableException {
    try {
      return (Resolvable) PortalConnectorFactory.getContentObject(contentid,
        datasource);
    } catch (com.gentics.api.lib.datasource.DatasourceNotAvailableException e) {
      throw (DatasourceNotAvailableException) e;
    }
  }
}
