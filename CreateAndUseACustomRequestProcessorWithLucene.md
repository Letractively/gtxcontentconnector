This article describes how to create and use a custom RequestProcessor and use it with the indexer to index custom datasources.

# Introduction #

The Apache Lucene indexer in this framework uses different instances of the RequestProcessor class to access all datasources that need to be indexed. It queries them with the configured rule periodically and expects them to return all the objects that should be contained in the index (for this datasource. e.g. matching the provided rule). When using differential indexing, it is important that the RequestProcessor always returns all objects (otherwise the missing objects would be removed from the index).


# Creating the RequestProcessor #

Extend the existing RequestProcessor class to create an abstraction for your own datasource.

Here we created a dummy RequestProcessor that returns dummy objects. These objects can also be loaded from a database or generated from a XML-File, ...

```
package com.gentics.cr;

import java.util.ArrayList;
import java.util.Collection;

import com.gentics.cr.exceptions.CRException;
/**
 * Dummy Request Processor is a simple Template 
 * for new RequestProcessor classes.
 * @author Christopher
 *
 */
public class DummyRequestProcessor extends RequestProcessor {
	/**
	 * Key to load from the configuration.
	 */
	private static final String PREFIX_KEY = "prefix";
	/**
	 * Custom prefix that will be added to all values.
	 */
	private String customPrefix = "";
	/**
	 * Constructor.
	 * @param config configuration passed by the system.
	 * @throws CRException exception thrown on error
	 */
	public DummyRequestProcessor(final CRConfig config) throws CRException {
		super(config);
		//SETUP THE REQUEST PROCESSOR AND LOAD THE CONFIGURATION
		//HERE YOU COULD LOAD DB-HANDLE INFORMATION OR PATHS TO FILES,...
		customPrefix = (String) config.get(PREFIX_KEY);
	}

	@Override
	public void finalize() {
		//CLOSE ALL DATABASE CONNECTIONS HERE
	}

	/**
	 * Method that gets called for retrieving objects.
	 * @param request request object, containig the url, count, size,...
	 * @param doNavigation can be ignored.
	 * @throws CRException exception that gets thrown on error.
	 * @return complete list of objects requested.
	 */
	public final Collection<CRResolvableBean> getObjects(
			final CRRequest request,
			final boolean doNavigation) throws CRException {
		Collection<CRResolvableBean> response 
				= new ArrayList<CRResolvableBean>();
		//The Request filter could be a SQL statement, a XPATH Query
		//or anything the like.
		String requestFilter = request.getRequestFilter();
		//DO FANCY STUFF TO RETRIEVE AND/OR CREATE OBJECTS
		for (int i = 0; i < 10; i++) {
			CRResolvableBean bean = new CRResolvableBean();
			String[] requestedAttributes = request.getAttributeArray();
			
			for (String attribute : requestedAttributes) {
				bean.set(attribute, customPrefix + "VALUE" + i + "_FOR_"
						+ attribute);
			}
			//CHECK IF BEAN MATCHES THE CONFIGURED RULE
			// IN THIS CASE IT ALWAYS DOES IF THE FILTER IS NOT NULL
			if (requestFilter != null) {
				response.add(bean);
			}
		}
		
		return response;
	}

}

```

## The Request Object ##

The Request Object passed to the method getObjects contains all request specific information that can be used to fetch the queried objects.


Important parameters:
  * FilterRule (getFilterRule): This can be a SQL statement, Content Repository Rule or anything like that. All returned objects have to match that rule.
  * AttributeArray (getAttributeArray): This array contains the attributes that should be contained in the result (if they exist).
  * Start (getStart): This is a number and states the number of the object, the result should be start with e.g. (in MySQL) LIMIT **10** , 10
  * Count (getCount): This is a number and state the number of objects the result should be limited to e.g. (in MySQL) LIMIT 10, **10**

# Configuration #

The new RequestPrcessor can then be configured in the according indexer.properties file as a new CR.

```
#-------------------------------------------------------------------------------------------------
# ContentRepository specific config for Dummy
#-------------------------------------------------------------------------------------------------
# The RequestProcessor that should be used to fetch the objects 
index.DEFAULT.CR.DUMMY.rp.1.rpClass=com.gentics.cr.DummyRequestProcessor

# Configuration of the custom RequestProcessor
index.DEFAULT.CR.DUMMY.rp.1.prefix=MyCustomPrefix

# The Rule that is used to fetch the objects
index.DEFAULT.CR.DUMMY.rule=object.obj_type=="DUMMY"

# The fields/attributes that should be fetched with the object
index.DEFAULT.CR.DUMMY.indexedAttributes=name,edittimestamp,binarycontent,publishtimestamp,mimetype,folder_id,node_id

# The fields/attributes that should also be stored in the index
index.DEFAULT.CR.DUMMY.containedAttributes=name,binarycontent

# The fields/attributes that should be boosted with the value that is stated after the "^"
index.DEFAULT.CR.DUMMY.BoostedAttributes=name^10.0,content^5.0

# The attribute/field that can be used as unique ID-Attribute
index.DEFAULT.CR.DUMMY.idattribute=contentid

# Enables the differential indexing and uses the attribute set in this option
index.DEFAULT.CR.DUMMY.updateattribute=updatetimestamp

# Specific batch size for the configuration block (DUMMY) decrease this value if you have memory 
# problems. (overwrites index.DEFAULT.batchsize)
index.DEFAULT.CR.DUMMY.batchsize=5

# Configure this index part to check every 6hours. note this should be a
# multiple of index.DEFAULT.interval
#index.DEFAUL.CR.DUMMY.interval=21600

# The following line would set the index job to execute an optimize command on the index after 
# each run (time an memory consuming)
#index.DEFAULT.CR.DUMMY.optimize=true

# The following line would set the index job to execute an optimize command on the index after 
# each run using a max segement rule => only optimizes when more than max segments are present 
# (not as time and memory consuming) (the line above should be commented out)
#index.DEFAULT.CR.DUMMY.maxsegments=10

# Makes searches faster, required for VectorBolder, needs more space on disk (about double the 
# space as without Vector), default is true
index.DEFAULT.CR.DUMMY.storeVectors=true
```