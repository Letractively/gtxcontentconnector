package com.gentics.cr.lucene;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gentics.cr.CRConfigUtil;
import com.gentics.cr.template.FileTemplate;
import com.gentics.cr.template.ITemplate;
import com.gentics.cr.template.ITemplateManager;
import com.gentics.cr.util.indexing.AbstractUpdateCheckerJob;
import com.gentics.cr.util.indexing.IndexController;
import com.gentics.cr.util.indexing.IndexJobQueue;
import com.gentics.cr.util.indexing.IndexLocation;


/**
 * @author haymo
 * Used to render the Rest xml.
 */
public class IndexJobServlet extends HttpServlet {

  private static final String NAGIOS_PARAM = "nagios";
  private static final long serialVersionUID = 0002L;
  private Logger log;
  private IndexController indexer;

  private CRConfigUtil crConf;
  private ITemplateManager vtl;
  private ITemplate tpl;


  public final void init(final ServletConfig config) throws ServletException {

    super.init(config);
    this.log = Logger.getLogger("com.gentics.cr.lucene");
    this.indexer = new IndexController(config.getServletName());

    
    
    this.crConf = new CRConfigUtil();
    this.vtl = crConf.getTemplateManager();
    
	try{
		this.tpl = new FileTemplate(IndexJobServlet.class.getResourceAsStream("indexjobtemplate.vm"));
	}
	catch(Exception ex)
	{
		log.error("FAILED TO LOAD VELOCITY TEMPLATE FROM indexjobtemplate.vm");
	}
  }

  @Override
  public final void destroy() {
    if (indexer != null) {
      indexer.stop();
    }
  }

  /**
   * Wrapper Method for the doGet and doPost Methods
   * 
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  public void doService(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    this.log.debug("Request:" + request.getQueryString());
    String nagString = request.getParameter(NAGIOS_PARAM);
    boolean doNag = Boolean.parseBoolean(nagString);
    // starttime
    long s = new Date().getTime();
    // get the objects

    String action = request.getParameter("action");
    String index = request.getParameter("idx");

    if (doNag) {
      response.setContentType("text/plain");
      Hashtable<String, IndexLocation> indexTable = indexer.getIndexes();
      for (Entry<String, IndexLocation> e : indexTable.entrySet()) {
        if(e.getKey().equalsIgnoreCase(index))
        {
          IndexLocation loc = e.getValue();
          IndexJobQueue queue = loc.getQueue();
          if(queue!=null && queue.isRunning())
          {
            response.getWriter().write("WorkerThread:OK\n");
          }
          else
          {
            response.getWriter().write("WorkerThread:NOK\n");
          }
          response.getWriter().write("ObjectsInIndex:"+loc.getDocCount()+"\n");
          AbstractUpdateCheckerJob j = queue.getCurrentJob();
          if(j!=null)
          {
            response.getWriter().write("CurrentJobObjectsToIndex:"+j.getObjectsToIndex()+"\n");
          }
        }
      }
    } else {
      String nc = "&t="+System.currentTimeMillis();

      response.setContentType("text/html");
      
      Hashtable<String,IndexLocation> indexTable = indexer.getIndexes();
      
      this.vtl.put("indexes", indexTable.entrySet());
      this.vtl.put("nc", nc);
      
      for (Entry<String,IndexLocation> e:indexTable.entrySet()) {
        
    	IndexLocation loc = e.getValue();
        IndexJobQueue queue = loc.getQueue();
        
        Hashtable<String,CRConfigUtil> map = loc.getCRMap();
        
        if(e.getKey().equalsIgnoreCase(index))
        {
          if("stopWorker".equalsIgnoreCase(action))
            queue.stopWorker();
          if("startWorker".equalsIgnoreCase(action))
            queue.startWorker();
          if("addJob".equalsIgnoreCase(action))
          {
            String cr = request.getParameter("cr");
            if("all".equalsIgnoreCase(cr))
              loc.createAllCRIndexJobs();
            else
            {
              if(cr!=null)
              {
                CRConfigUtil crc=map.get(cr);
                loc.createCRIndexJob(crc,map);
              }
            }
          }
        }
      }
    }
    
	try
	{
		String output = this.vtl.render(this.tpl.getKey(), this.tpl.getSource());
		response.getWriter().write(output);
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
    
    response.getWriter().flush();
    response.getWriter().close();
    // endtime
    long e = new Date().getTime();
    this.log.info("Executiontime for getting Status " + (e - s));

  }

  public void doGet(final HttpServletRequest request,
      final HttpServletResponse response) throws ServletException, IOException {
    doService(request, response);
  }

  public void doPost(final HttpServletRequest request,
      final HttpServletResponse response) throws ServletException, IOException {
    doService(request, response);
  }

}