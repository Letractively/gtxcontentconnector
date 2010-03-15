package com.gentics.cr.util.response;

/**
 * 
 * Last changed: $Date$
 * @version $Revision$
 * @author $Author$
 *
 */
public interface IResponseTypeSetter {

	/**
	 * Sets the contenttype to a given response. This may be a HttpServletResponse, a PortletResponse,...
	 * @param type
	 */
	public void setContentType(String type);
}
