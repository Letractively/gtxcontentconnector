package com.gentics.cr;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.gentics.api.lib.datasource.Datasource;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.plink.PathResolver;
import com.gentics.cr.template.ITemplateManager;
import com.gentics.cr.template.VelocityTemplateManagerFactory;
import com.gentics.cr.util.CRUtil;

/**
 * 
 * Last changed: $Date$
 * @version $Revision$
 * @author $Author$
 *
 */
public class CRConfigUtil extends GenericConfiguration implements CRConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1599393624385876283L;

	/**
	 * Key under which the RequestProcessorConfigs are stored
	 */
	private static final String REQUEST_PROCESSOR_KEY = "RP";
	
	private static Logger log = Logger.getLogger(CRConfigUtil.class);

	private String name = null;
	

	/**
	 * Create new instance of CRConfigUtil
	 */
	public CRConfigUtil() {
		
	}
	
	/**
	 * Create new instace of CRConfig util from GenericConfiguration
	 * @param conf
	 * @param name
	 */
	public CRConfigUtil(GenericConfiguration conf, String name)
	{
		this.name=name;
		this.properties = conf.getProperties();
		this.subconfigs = conf.getSubConfigs();
	}
	
	
	
	/**
	 * Init Datasource with Handle Properties and Datasource Properties
	 * @param hp Handle Properties
	 * @param dp Datasource Properties
	 */
	public void initDS() {
		
		
		log.debug("Creating Datasource for " + this.getName());

		// Init datasources so that the connections can be initialized
		// The initialized handles will be cached and then returned when 
		// the datasource is fetched again
		getDatasource();
		
		for(int i = 1; i <= getRequestProcessorSize(); i++){
			CRConfigUtil requestProcessorConfig = getRequestProcessorConfig(i);
			requestProcessorConfig.initDS();
			
			
		}
		
		
		

	}
	
	
	/**
	 * Set Name of Config
	 * @param name
	 */
	public void setName(String name) {
		log.debug("Set name: "+ name);
		this.name = name;
	}
	
	/**
	 * Gets the configs name
	 * @return name as string
	 */
	public String getName() {
		return this.name;
	}
	
	
	private static final String BINARY_TYPE_KEY="BINARYTYPE";
	/**
	 * Gets the applications binary type
	 * @return 
	 */
	public String getBinaryType()
	{
		return((String)this.get(BINARY_TYPE_KEY));
	}
	
	/**
	 * Sets the applications binary type
	 * @param type
	 */
	public void setBinaryType(String type)
	{
		this.set(BINARY_TYPE_KEY, type);
	}
	
	private static final String FOLDER_TYPE_KEY="FOLDERTYPE";
	/**
	 * Gets the applications folder type
	 * @return type
	 */
	public String getFolderType()
	{
		return((String)this.get(FOLDER_TYPE_KEY));
	}
	
	/**
	 * Sets the applications folder type
	 * @param type
	 */
	public void setFolderType(String type)
	{
		this.set(FOLDER_TYPE_KEY, type);
	}
	
	private static final String PAGE_TYPE_KEY="PAGETYPE";
	/**
	 * Gets the applications page type
	 * @return type
	 */
	public String getPageType()
	{
		return((String)this.get(PAGE_TYPE_KEY));
	}
	
	/**
	 * Sets the applications page type
	 * @param type
	 */
	public void setPageType(String type)
	{
		this.set(PAGE_TYPE_KEY, type);
	}
	
	private static final String APPRULE_KEY="APPLICATIONRULE";
	/**
	 * Gets the application rule
	 * @return rule
	 */
	public String getApplicationRule()
	{
		return((String)this.get(APPRULE_KEY));
	}
	/**
	 * Sets the application rule
	 * @param rule
	 */
	public void setApplicationRule(String rule)
	{
		this.set(APPRULE_KEY, rule);
	}

	/**
	 * Alias for getProperties Method
	 * @return flat properties as Properties Class
	 */
	public Properties getProps()
	{
		return(this.getProperties());
	}

	private static final String PLINK_TEMPLATE_KEY="PLINKTEMPLATE";
	
	/**
	 * Sets the applications PLink Template
	 * @param template
	 */
	public void setPlinkTemplate(String template) {
		this.set(PLINK_TEMPLATE_KEY, template);
	}

	/**
	 * Gets the applications PLink Template
	 * @return PLink template as string. Defaults to "$url" 
	 */
	public String getPlinkTemplate() {
		String tmp = null;
		tmp = (String)this.get(PLINK_TEMPLATE_KEY);
		if (tmp != null && !"".equals(tmp)) {
			return tmp;
		}
		log.warn("No plinktemplate set. Using default template '$url'.");
		return "$url";
	}

	/**
	 * Gets the Datasource of this config
	 * 		- only RequestProcessor configs have Datasources set
	 * When a Datasource is requested it has to be released after usage using CRDatabaseFactory.releaseDatasource(ds); where ds is the Datasource instance.
	 * @return Datasource or null if config has no Datasource
	 */
	public Datasource getDatasource() {
		return CRDatabaseFactory.getDatasource(this);
	}

	/**
	 * Gets the PathResolver of this config
	 * 		- only RequestProcessor configs have PathResolvers set
	 * @return PathResolver or null if config has no PathResolver
	 */
	public PathResolver getPathResolver() {
		PathResolver pathResolver = new PathResolver(this, this.getApplicationRule());
		if (pathResolver != null) 
		{
			log.debug("Loaded Pathresolver for " + this.getName());
		} 
		else 
		{
			log.error("Could not initialize Pathresolver for "+ this.getName());
		}
		return pathResolver;
	}

	/**
	 * Default to String method
	 * @return Class name and Config name
	 */
	public String toString() {
		return this.getClass().getName() + ":" + this.getName();
	}

	private static final String ENCODING_KEY = "RESPONSE-CHARSET";
	
	/**
	 * Sets the response encoding of the application
	 * @param response_encoding
	 */
	public void setEncoding(String response_encoding) {
		this.set(ENCODING_KEY, response_encoding);
	}

	/**
	 * Gets the applications configured Encoding
	 * @return Encoding as String, defaults to "utf-8"
	 */
	public String getEncoding() {
		String enc = (String)this.get(ENCODING_KEY);
		if(enc!=null && !"".equals(enc))
		{
			return(enc);
		}
		return("utf-8");
	}
	
	private static final String RP_CLASS_KEY = "RPCLASS";
	
	/**
	 * Sets the RequestProcessor class used by this RP instance
	 * 		- only RP configs have RP classes
	 * @param requestProcessorClass
	 */
	public void setRequestProcessorClass(String requestProcessorClass) {
		this.set(RP_CLASS_KEY, requestProcessorClass);
	}

	/**
	 *  Gets the RequestProcessor class used by this RP instance
	 * 		- only RP configs have RP classes
	 * @return class name as string or null if not set
	 */
	public String getRequestProcessorClass() {
		return (String)this.get(RP_CLASS_KEY);
	}
	
	private static final String OBJECT_PERMISSION_ATTR_KEY = "OBJECTPERMISSIONATTRIBUTE";
	
	/**
	 * Sets the applications objectperissionattribute
	 * @param objectpermissionattribute
	 */
	public void setObjectPermissionAttribute(String objectpermissionattribute) {
		this.set(OBJECT_PERMISSION_ATTR_KEY, objectpermissionattribute);
	}
	
	/**
	 * Gets the applications objectperissionattribute
	 * @return objectpermissionattribute
	 */
	public String getObjectPermissionAttribute() {
		return (String)this.get(OBJECT_PERMISSION_ATTR_KEY);
	}
	
	private static final String USER_PERMISSION_ATTR_KEY ="USERPERMISSIONATTRIBUTE";
	/**
	 * Sets the applications userperissionattribute
	 * @param userpermissionattribute
	 */
	public void setUserPermissionAttribute(String userpermissionattribute) {
		this.set(USER_PERMISSION_ATTR_KEY, userpermissionattribute);
	}
	
	/**
	 * Gets the applications userpermissionattribute
	 * @return userpermissionattribute
	 */
	public String getUserPermissionAttribute() {
		return (String)this.get(USER_PERMISSION_ATTR_KEY);
	}

	private static final String XML_URL_KEY ="XMLURL";
	/**
	 * Sets the applications XML url
	 * @param xmlUrl
	 */
	public void setXmlUrl(String xmlUrl) {
		this.set(XML_URL_KEY, xmlUrl);
	}
	/**
	 * Gets the applications XML url
	 * @return url
	 */
	public String getXmlUrl() {
		return (String)this.get(XML_URL_KEY);
	}
	
	private static final String XSLT_URL_KEY ="XSLTURL";
	/**
	 * Sets the applications Xslt url
	 * @param xsltUrl
	 */
	public void setXsltUrl(String xsltUrl) {
 		this.set(XSLT_URL_KEY, xsltUrl);
	}
	/**
	 * Gets the applications Xslt url
	 * @return url
	 */
	public String getXsltUrl() {
		return (String)this.get(XSLT_URL_KEY);
	}
	
	private static final String CONTENTID_REGEX_KEY ="CONTENTID_REGEX";
	/**
	 * Sets the applications Contentid Regex
	 * @param contentid_regex
	 */
	public void setContentidRegex(String contentid_regex) {
		this.set(CONTENTID_REGEX_KEY,contentid_regex);
	}
	/**
	 * Gets the applications Contentid Regex
	 * @return regex
	 */
	public String getContentidRegex() {
		return (String)this.get(CONTENTID_REGEX_KEY);
	}
	
	private static final String FILTERCHAIN_KEY = "FILTERCHAIN";
	/**
	 * Gets the applications filterchain
	 * @return filterchain or null if no filterchain is set
	 */
	public ArrayList<String> getFilterChain(){
		
		return getPropertiesAsSortedCollection(FILTERCHAIN_KEY);
	}
	
	/**
	 * Gets count of requestprocessors configured in this config
	 * @return count
	 */
	public int getRequestProcessorSize(){
		Object obj = get(REQUEST_PROCESSOR_KEY);
		if(obj!=null && obj instanceof GenericConfiguration)
		{
			return ((GenericConfiguration)obj).getSubConfigSize();
		}
		return 0;
	}
	/**
	 * get Configuration of the specified RequestProcessor
	 * @param requestProcessorId Number of the RequestProcessor
	 * @return Configuration of the specified RequestProcessor. null in case something bad happens
	 */
	public CRConfigUtil getRequestProcessorConfig(int requestProcessorId){
		return(getRequestProcessorConfig(""+requestProcessorId));
	}
	
	/**
	 * get Configuration of the specified RequestProcessor
	 * @param requestProcessorId Number of the RequestProcessor
	 * @return Configuration of the specified RequestProcessor. null in case something bad happens
	 */
	public CRConfigUtil getRequestProcessorConfig(String requestProcessorId){
		Object obj = get(REQUEST_PROCESSOR_KEY+"."+requestProcessorId);
		
		if(obj!=null && obj instanceof GenericConfiguration){
			return new CRConfigUtil((GenericConfiguration)obj,this.getName()+"."+REQUEST_PROCESSOR_KEY+"."+requestProcessorId);
		}
		else{
			log.fatal("RequestProcessor"+requestProcessorId+" cannot be found. Maybe your config cannot be initialized correctly.");
			return null;			
		}
	}
	
	/**
	 * Returns a new instance of the requestprocessor configured in the config with the given requestProcessorId
	 * @param requestProcessorId
	 * @return RequestProcessor
	 * @throws CRException
	 */
	public RequestProcessor getNewRequestProcessorInstance(int requestProcessorId) throws CRException
	{
		
		CRConfigUtil requestProcessorConfig = this.getRequestProcessorConfig(requestProcessorId);
		String requestProcessorClass = requestProcessorConfig.getRequestProcessorClass();
		
		log.debug("Instanciate RequestProcessor"+requestProcessorId+" from class "+requestProcessorClass);
		RequestProcessor rp=null;
		try {
			rp = (RequestProcessor) Class.forName(requestProcessorClass).getConstructor(new Class[] {CRConfig.class}).newInstance(requestProcessorConfig);
		} catch (Exception e) {
			throw new CRException(e);
		}
		return(rp);
	}
	
	private static final String PORTALNODE_COMPATIBILITY_MODE_KEY = "PORTALNODECOMPATIBILITY";
	/**
	 * gets if application is running in portalnode compatibility mode
	 * @return true if app is running in portalnode compatibility mode
	 */
	public boolean getPortalNodeCompMode() {
		String pnComp = (String)get(PORTALNODE_COMPATIBILITY_MODE_KEY);
		return Boolean.parseBoolean(pnComp);
	}
	/**
	 * Sets the applications portalnode compatibility mode
	 * @param mode
	 */
	public void setPortalNodeCompMode(String mode) {
		this.set(PORTALNODE_COMPATIBILITY_MODE_KEY, mode);
	}
	
	/**
	 * Gets the configured template manager
	 * @return template manager or null if it is not set
	 */
	public ITemplateManager getTemplateManager() {
		if(!this.getPortalNodeCompMode())
		{
			ITemplateManager tmplManager=null;
			try
			{
				tmplManager = VelocityTemplateManagerFactory.getConfiguredVelocityTemplateManagerInstance(this.getEncoding(),CRUtil.resolveSystemProperties("${com.gentics.portalnode.confpath}/templates/"));
			}catch(Exception e)
			{
				CRException ex = new CRException(e);
				log.error(ex.getMessage()+ex.getStringStackTrace());
			}
			return tmplManager;
		}
		return null;
	}
	
	private static final String SHARED_CACHE_KEY="USESHAREDCACHES";
	/**
	 * @return true if application is to use shared caches
	 */
	public boolean useSharedCache(){
		String pnSC = (String)get(PORTALNODE_COMPATIBILITY_MODE_KEY);
		return Boolean.parseBoolean(pnSC);
		
	}
	/**
	 * Sets if the application should use shared caches
	 * @param sharedCache
	 * @return value of sharedcache after it has been set
	 */
	public boolean useSharedCache(boolean sharedCache){
		this.set(SHARED_CACHE_KEY, Boolean.toString(sharedCache));
		return useSharedCache();
	}
	
	private static final String CONTENTID_URL_KEY = "USECONTENTIDURL";
	
	/**
	 * defindes if to use contentidurl
	 * @return true if using conteitdurl
	 */
	public boolean usesContentidUrl() {
		String pnCU = (String)get(CONTENTID_URL_KEY);
		return Boolean.parseBoolean(pnCU);
	}
	
	private static final String DATASOURCE_PROPS_KEY = "DS";
	
	/**
	 * Get the Datasource Properties for the current config
	 * @return Datasource Properties or null if not set
	 */
	public Properties getDatasourceProperties()
	{
		Object obj = get(DATASOURCE_PROPS_KEY);
		if(obj!=null && obj instanceof GenericConfiguration)
		{
			Properties dsprops = ((GenericConfiguration)obj).getRebuiltPropertyTree();
			Properties ret = new Properties();
			//DATASOURCE PROPERTY KEYS NEED TO BE LOWERCASE IN ORDER TO BE UNDERSTOOD BY THE DRIVER
			//DATASOURCE PROPERTIES NEED TO BE RESOLVED AND REBUILT
			for(Entry<Object,Object> e:dsprops.entrySet())
			{
				String key = (String)e.getKey();
				ret.put(key.toLowerCase(), e.getValue());
			}
			return ret;
		}
		return(null);
	}
	
	private static final String DATASOURCE_HANDLE_KEY = "DS-HANDLE";
	
	/**
	 * Get the Datasource Handle Properties for the current config
	 * @return Datasource Handle Properties or null if not set
	 */
	public Properties getDatasourceHandleProperties()
	{
		Object obj = get(DATASOURCE_HANDLE_KEY);
		if(obj!=null && obj instanceof GenericConfiguration)
		{
			Properties dsprops = ((GenericConfiguration)obj).getRebuiltPropertyTree();
			Properties ret = new Properties();
			//DATASOURCE PROPERTY KEYS NEED TO BE LOWERCASE IN ORDER TO BE UNDERSTOOD BY THE DRIVER
			for(Entry<Object,Object> e:dsprops.entrySet())
			{
				String key = ((String)e.getKey()).toLowerCase();
				//Driver class has to be spelled "driverClass"
				if("driverclass".equals(key))key="driverClass";
				ret.put(key, e.getValue());
			}
			return ret;
		}
		return(null);
	}
}
