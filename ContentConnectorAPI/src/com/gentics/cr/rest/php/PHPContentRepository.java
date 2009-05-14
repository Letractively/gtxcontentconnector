package com.gentics.cr.rest.php;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.gentics.cr.CRException;
import com.gentics.cr.CRResolvableBean;
import com.gentics.cr.rest.ContentRepository;

/**
 * @author haymo
 * 
 * Implementaion of Json rappresentation for a REST contentrepositroy.
 * 
 */
public class PHPContentRepository extends ContentRepository {

	private static final long serialVersionUID = 0005L;

	private PHPSerializer PHPSerializer;
	private HashMap<String,Object> cr;

	public PHPContentRepository(String[] attr) {

		super(attr);
		this.setResponseEncoding("UTF-8");
		PHPSerializer = new PHPSerializer(this.getResponseEncoding());
		cr = new HashMap<String,Object>();
		
	}
	
	public PHPContentRepository(String[] attr, String encoding) {

		super(attr);
		this.setResponseEncoding(encoding);
		PHPSerializer = new PHPSerializer(this.getResponseEncoding());
		cr = new HashMap<String,Object>();
		
	}
	
	public PHPContentRepository(String[] attr, String encoding, String[] options) {

		super(attr,encoding,options);
		//this.setResponseEncoding(encoding);
		PHPSerializer = new PHPSerializer(this.getResponseEncoding());
		cr = new HashMap<String,Object>();
		
	}

	public String getContentType() {
		return "application/serialized_PHP_variable";
		//return("text/plain");
	}
	
	public void respondWithError(OutputStream stream,CRException ex, boolean isDebug)
	{
		this.cr.clear();
		this.cr.put("status","error");

		HashMap<String,Object> error = new HashMap<String,Object>();
		error.put("type",ex.getType());
		error.put("message",ex.getMessage());
		
		if(isDebug)
		{
			error.put("stacktrace",ex.getStringStackTrace());
		}
		
		this.cr.put("Error",error);
		
		try {
			stream.write((this.PHPSerializer.serialize(this.cr)).getBytes());
		} catch (IOException e) {
			;
		}
	}

	public void toStream(OutputStream stream) throws CRException{
		if(this.resolvableColl.isEmpty())
		{
			//No Data Found
			
			throw new CRException("NoDataFound","Data could not be found.");
		}
		else
		{
			this.cr.put("status","ok");
			for (Iterator<CRResolvableBean> it = this.resolvableColl.iterator(); it.hasNext();) {
	
				CRResolvableBean crBean =  it.next();
	
				HashMap<String,Object> objElement = processElement(crBean);
	
				this.cr.put(crBean.getContentid(), objElement);
			}
		}

		try {
			OutputStreamWriter wr = new OutputStreamWriter(stream, this.getResponseEncoding());
			//TODO auch bei den anderen repositories den output charset übergeben
			//TODO output charset über web.xml konfigurierbar machen
			wr.write(this.PHPSerializer.serialize(this.cr));
			
			wr.flush();
			wr.close();
			//stream.write((this.PHPSerializer.serialize(this.cr)).getBytes());
		} catch (IOException e) {
			;
		}

	}

	
	@SuppressWarnings("unchecked")
	private HashMap<String,Object> processElement(CRResolvableBean crBean) {
		HashMap<String,Object> objElement = new HashMap<String,Object>();

		objElement.put("contentid", "" + crBean.getContentid());
		objElement.put("obj_id", "" + crBean.getObj_id());
		objElement.put("obj_type", "" + crBean.getObj_type());
		objElement.put("mother_id",
				((crBean.getMother_id() == null) ? "" : ""
						+ crBean.getMother_id()));
		objElement.put("mother_type",
				((crBean.getMother_type() == null) ? "" : ""
						+ crBean.getMother_type()));

		if (crBean.getAttrMap() != null && (!crBean.getAttrMap().isEmpty())) {
			HashMap<String,Object> attribContainer = new HashMap<String,Object>();
			Iterator<String> bit = crBean.getAttrMap().keySet().iterator();
			while (bit.hasNext()) {

				String entry =  bit.next();
				// Element attrElement = doc.createElement(entry);
				Object bValue=crBean.getAttrMap().get(entry);
				
				if(bValue!=null)
				{
					if((!entry.equals("binarycontent")) &&(bValue.getClass().isArray()||bValue.getClass()==ArrayList.class))
					{
						Object[] value;
						if(bValue.getClass()==ArrayList.class)
							value=((ArrayList<Object>)bValue).toArray();
						else
							value=(Object[])bValue;
						
						attribContainer.put(entry, value);
					}
					else
					{
						String value="";
						if(entry.equals("binarycontent"))
						{
							try
							{
								value=new String((byte[])bValue);
							}
							catch(ClassCastException x)
							{
								try {
									value=new String(getBytes(bValue));
								} catch (IOException e) {
									value=bValue.toString();
									e.printStackTrace();
								}
							}
							//TODO return proper binary content
							//value=(String) bValue.toString();
						}
						else
						{
							if(bValue.getClass()==String.class)
							{
								
								value=(String)bValue;
							}
							else
							{
								value=bValue.toString();
//								try {
//									value = new String(getBytes(bValue));
//									
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							}
							
	//						if(bValue.getClass()==String.class)
	//						{
	//							value=(String) bValue;
	//						}
	//						else
	//						{
	//							value=(String) bValue.toString();
	//						}
							
						}
						attribContainer.put(entry, value);
					}
				}
				/* 
				if (value == null) {
					value = "";
				}*/
				
			}
			objElement.put("attributes",attribContainer);
		}
		if(crBean.getChildRepository().size()>0)
		{
			HashMap<String,HashMap<String,Object>> childContainer = new HashMap<String,HashMap<String,Object>>();
			
			for (Iterator<CRResolvableBean> it = crBean.getChildRepository().iterator(); it.hasNext();) {
				
				CRResolvableBean chBean = it.next();
	
				HashMap<String,Object> chElement = processElement(chBean);
				childContainer.put(chBean.getContentid(),chElement);
			}
			//Text t = doc.createCDATASection("Count: "+crBean.getChildRepository().size());
			//childContainer.appendChild(t);
			
			objElement.put("children",childContainer);
		}
		return objElement;
	}
}
