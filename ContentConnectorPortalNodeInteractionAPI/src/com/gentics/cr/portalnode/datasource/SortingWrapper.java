package com.gentics.cr.portalnode.datasource;

import com.gentics.api.lib.datasource.Datasource.Sorting;

/**
 * Wrapper class for {@link Sorting} from Gentics Portal.Node.
 * @author bigbear3001
 *
 */
public class SortingWrapper {

  /**
   * Wrapped {@link Sorting} used to sort Datasources.
   */
  private Sorting sorting;

  /**
   * Wrap an existing {@link Sorting}.
   * @param wrappedSorting {@link Sorting} to wrap
   */
  public SortingWrapper(final Sorting wrappedSorting) {
    sorting = wrappedSorting;
  }

  /**
   * Create wrapped {@link Sorting}.
   * @param attributeName Name of the Attribute to sort the objects by.
   * @param sortorder Order to sort the objects, use one of
   * {@link DatasourceWrapper#SORTORDER_ASC}
   * {@link DatasourceWrapper#SORTORDER_DESC}
   * {@link DatasourceWrapper#SORTORDER_NONE}
   */
  public SortingWrapper(final String attributeName, final int sortorder) {
    sorting = new Sorting(attributeName, sortorder);
  }

  /**
   * Get the wrapped {@link Sorting}. You must include Gentics Portal.Node API
   * to use this method.
   * @return wrapped {@link Sorting}.
   */
  public final Sorting getWrapperSorting() {
    return sorting;
  }

}
