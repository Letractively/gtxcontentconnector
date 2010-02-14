package com.gentics.cr.portalnode.exception;

/**
 * Wrapping class for DatasourceNotAvailableException generated from Gentics datasources.
 * @author bigbear3001
 *
 */
public class DatasourceNotAvailableException extends
    com.gentics.api.lib.datasource.DatasourceNotAvailableException {

  /**
   * generated serialVersionUID.
   */
  private static final long serialVersionUID = -7086611233392190803L;

  /**
   * Wrapping constructor for DatasourceNotAvailableExcpetion.
   * @param message Message describing the cause of the Exception
   */
  public DatasourceNotAvailableException(final String message) {
    super(message);
  }

}
