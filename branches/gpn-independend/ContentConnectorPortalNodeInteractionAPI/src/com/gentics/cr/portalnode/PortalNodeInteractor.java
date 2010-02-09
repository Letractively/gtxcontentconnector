package com.gentics.cr.portalnode;

import com.gentics.cr.portalnode.Datasource;
import com.gentics.api.portalnode.portlet.GenticsPortlet;
import com.gentics.api.portalnode.templateengine.TemplateProcessor;
import com.gentics.portalnode.portal.Portal;


/**
 * 
 * Last changed: $Date: 2009-06-26 15:48:16 +0200 (Fr, 26 Jun 2009) $
 * @version $Revision: 105 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class PortalNodeInteractor {

	/**
	 * Get a configured Instance of Datasource from a running Portal.Node
	 * Instance.
	 * @param key datasource identifyer e.g.: ccr, pcr,...
	 * @return
	 */
	public static Datasource getPortalnodeDatasource(String key)
	{
		//TODO: remove reference to non-api class Portal,
		//		maybe we can handle this over the PortalPropertyResolver
		//		and the path "portal.portal.createDatasource(key)
		return (Datasource) (Portal.getCurrentPortal().createDatasource(key));
	}
	
	/**
	 * Get a TemplateProcessor from a running Portal.Node Instance
	 * @param portlet
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static TemplateProcessor getPortletTemplateProcessor(GenticsPortlet portlet)
	{
		//TODO Get not depricated method from GTX-DEV
		return(portlet.getTemplateProcessor(null, null));
	}
}
