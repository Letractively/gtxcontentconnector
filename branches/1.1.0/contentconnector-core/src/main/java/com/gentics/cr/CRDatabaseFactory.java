package com.gentics.cr;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.gentics.api.lib.datasource.Datasource;
import com.gentics.api.portalnode.connector.PortalConnectorFactory;
import com.gentics.cr.portalnode.PortalNodeInteractor;
/**
 * 
 * Last changed: $Date: 2010-04-01 15:24:02 +0200 (Do, 01 Apr 2010) $
 * @version $Revision: 541 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class CRDatabaseFactory {
	//Static Members
	private static Logger log = Logger.getLogger(CRDatabaseFactory.class);
	private static CRDatabaseFactory instance;
	
	//Instance Members
	private long dbcount = 0;
	
	private static CRDatabaseFactory getInstance()
	{
		if(instance==null)instance=new CRDatabaseFactory();
		return instance;
	}
	
	/**
	 * Release Datasource instance
	 * @param ds
	 */
	public static void releaseDatasource(Datasource ds)
	{
		if(ds!=null)
		{
			getInstance().releaseDS();
			ds = null;
		}
	}
	
	private synchronized void releaseDS()
	{
		dbcount--;
	}
	
	private synchronized void accquireDS()
	{
		dbcount++;
	}
	
	private synchronized boolean destroyFactory()
	{
		if(dbcount<=0)
		{
			PortalConnectorFactory.destroy();
			log.debug("Factory, resources and threads have been closed.");
			return true;
		}
		log.error("There are still unreleased datasources => could not destroy the factory");
		return false;
	}
	
	/**
	 * Destroys the Factory and releases all resources and stops threads if there are no more datasources that were not released
	 * @return true if there were no unreleased datasources and the factory was destroyed
	 */
	public static boolean destroy()
	{
		return getInstance().destroyFactory();
	}
	
	/**
	 * Gets a Datasource instance that ins configured within the given requestProcessorConfig
	 * @param requestProcessorConfig containing the datasource config
	 * @return Datasource if correctly configured, otherwise null
	 */
	public static Datasource getDatasource(CRConfigUtil requestProcessorConfig)
	{
		Datasource ds = null;
		Properties ds_handle = requestProcessorConfig.getDatasourceHandleProperties();
		Properties ds_props = requestProcessorConfig.getDatasourceProperties();
		if(ds_handle!=null && ds_handle.size()!=0)
		{
			if(ds_handle.containsKey("portalnodedb"))
			{
				String key = (String)ds_handle.get("portalnodedb");
				ds = PortalNodeInteractor.getPortalnodeDatasource(key);
			}
			else if(ds_props!=null && ds_props.size()!=0)
			{
				ds = PortalConnectorFactory.createWriteableDatasource(ds_handle,ds_props);
			}
			else
			{
				ds = PortalConnectorFactory.createWriteableDatasource(ds_handle);
			}	
			log.debug("Datasource created for "+requestProcessorConfig.getName());
			if(ds!=null)
			{
				getInstance().accquireDS();
			}
		}
		else
		{
			log.debug("No Datasource created for"+requestProcessorConfig.getName());
		}
		return(ds);
	}
}