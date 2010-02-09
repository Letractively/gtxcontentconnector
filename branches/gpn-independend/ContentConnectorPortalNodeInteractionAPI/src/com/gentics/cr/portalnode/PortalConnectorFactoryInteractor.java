package com.gentics.cr.portalnode;

import java.util.Properties;

import com.gentics.api.portalnode.connector.PortalConnectorFactory;

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
}
