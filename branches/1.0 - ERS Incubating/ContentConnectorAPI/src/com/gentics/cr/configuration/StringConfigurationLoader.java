package com.gentics.cr.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Properties;


/**
 * 
 * Last changed: $Date: 2009-06-22 17:49:58 +0200 (Mo, 22 Jun 2009) $
 * @version $Revision: 95 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class StringConfigurationLoader extends GenericConfiguration{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7725953433422855180L;
	
	/**
	 * Loads the configuration from a String formated like a java properties file
	 * @param configstring
	 * @throws IOException
	 */
	public void load(String configstring) throws IOException
	{
		if(configstring!=null)
		{
			Properties props = new Properties();
			StringReader sr = new StringReader(configstring);
			props.load(sr);
			for(Entry<Object,Object> e : props.entrySet())
			{
				this.set((String)e.getKey(),(String)e.getValue());
			}
		}
	}

}
