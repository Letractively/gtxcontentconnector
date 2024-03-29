package com.gentics.cr;

import com.gentics.cr.configuration.EnvironmentConfiguration;
import com.gentics.cr.plink.PlinkProcessor;

public class HSQLTestConfigFactory {
	
	private static final String CR_1_PREFIX = "RP.1.";
	
	/**
	 * This method generates a DB configuration for an in memory HSQL.
	 * A default empty repository will be generated.
	 * Default name: mytestdatasource
	 * @return
	 */
	public static final CRConfigUtil getDefaultHSQLConfiguration() {
		return getDefaultHSQLConfiguration("mytestdatasource");
	}
	
	/**
	 * This method generates a DB configuration for an in memory HSQL.
	 * A default empty repository will be generated.
	 * @param dsName name of the datasource
	 * @return
	 */
	public static final CRConfigUtil getDefaultHSQLConfiguration(String dsName) {
		EnvironmentConfiguration.setConfigPath(HSQLTestConfigFactory.class.getResource("conf/gentics").getPath());
		EnvironmentConfiguration.loadEnvironmentProperties();

		CRConfigUtil config = new CRConfigUtil();
		config.set(CR_1_PREFIX + RequestProcessor.CONTENTCACHE_KEY, "false");
		config.set(CR_1_PREFIX + PlinkProcessor.PLINK_CACHE_ACTIVATION_KEY, "false");
		config.set(CR_1_PREFIX + "ds-handle.url", "jdbc:hsqldb:mem:" + dsName);
		config.set(CR_1_PREFIX + "ds-handle.driverClass", "org.hsqldb.jdbcDriver");
		config.set(CR_1_PREFIX + "ds-handle.shutDownCommand", "SHUTDOWN");
		config.set(CR_1_PREFIX + "ds-handle.type", "jdbc");
		config.set(CR_1_PREFIX + "ds.sanitycheck2", "true");
		config.set(CR_1_PREFIX + "ds.autorepair2", "true");
		config.set(CR_1_PREFIX + "ds.sanitycheck", "false");
		return config;
	}

}
