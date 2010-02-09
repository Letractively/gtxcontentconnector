package com.gentics.cr.portalnode;

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
}
