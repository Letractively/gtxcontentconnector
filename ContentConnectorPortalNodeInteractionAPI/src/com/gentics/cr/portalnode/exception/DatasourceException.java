package com.gentics.cr.portalnode.exception;

/**
 * Wrapping class for DatasourceAxception generated from Gentics datasources.
 * @author bigbear3001
 *
 */
public class DatasourceException extends
    com.gentics.api.lib.datasource.DatasourceException {

  /**
   * generated serialVersionUID.
   */
  private static final long serialVersionUID = -7086611233392190803L;

  /**
   * Wrapping constructor for DatasourceExcpetion.
   * @param message Message describing the cause of the Exception
   */
  public DatasourceException(final String message) {
    super(message);
  }

}
