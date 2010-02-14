package com.gentics.cr.portalnode;

import com.gentics.api.lib.expressionparser.filtergenerator.DatasourceFilter;
import com.gentics.cr.portalnode.exception.FilterGeneratorException;

/**
 * Interface to wrap the DatasourceFilter from Gentics Portal.Node.
 * @author bigbear3001
 *
 */
public class DatasourceFilterWrapper {

  /**
   * Wrapped {@link DatasourceFilter}.
   */
  private DatasourceFilter datasourceFilter;

  /**
   * Create a DatasourceFilterWrapper with the given DatasourceFilter.
   * @param wrappedDatasourcFilter DatasourceFilter to wrap in
   */
  public DatasourceFilterWrapper(final DatasourceFilter wrappedDatasourcFilter)
  {
    datasourceFilter = wrappedDatasourcFilter;
  }

  /**
   * Add a resolvable to the environment the expression is executed in.
   * @param key {@link String} identifier for accessing the object in the
   * Expression
   * @param resolvable {@link Resolvable} to deploy
   * @throws FilterGeneratorException when the resolvable cannot be added to the
   * context
   */
  public final void addBaseResolvable(final String key,
      final Resolvable resolvable) throws
      FilterGeneratorException {
    try {
      datasourceFilter.addBaseResolvable(key, resolvable);
    } catch (com.gentics.api.lib.expressionparser.filtergenerator.
        FilterGeneratorException e) {
      throw (FilterGeneratorException) e;
    }
  }
}
