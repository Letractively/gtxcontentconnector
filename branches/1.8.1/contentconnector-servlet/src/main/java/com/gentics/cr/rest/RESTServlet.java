package com.gentics.cr.rest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gentics.api.lib.resolving.Resolvable;
import com.gentics.cr.CRServletConfig;
import com.gentics.cr.monitoring.MonitorFactory;
import com.gentics.cr.monitoring.UseCase;
import com.gentics.cr.util.CRRequestBuilder;
import com.gentics.cr.util.HttpSessionWrapper;
import com.gentics.cr.util.RequestBeanWrapper;
import com.gentics.cr.util.response.ServletResponseTypeSetter;


/**
 * @author haymo
 * 
 * Used to render the Rest xml.
 * 
 */
public class RESTServlet extends HttpServlet {

  private static final long serialVersionUID = 0002L;
  /**
   * Log4j logger for debug and error messages.
   */
  private static Logger log = Logger.getLogger(RESTServlet.class);
  /**
   * Configuration for the Servlet.
   */
  private CRServletConfig crConf;
  private RESTSimpleContainer container;
  
  
  public void init(ServletConfig config) throws ServletException {

    super.init(config);
    crConf = new CRServletConfig(config);
    container = new RESTSimpleContainer(crConf);

  }
  
  @Override
  public void destroy()
  {
    if(this.container!=null)this.container.finalize();
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
    UseCase uc = MonitorFactory.startUseCase("RESTServlet(" + request.getServletPath() + ")");
    log.debug("Request:" + request.getQueryString());
    
    // starttime
    long s = new Date().getTime();
    // get the objects
    
    HashMap<String,Resolvable> objects = new HashMap<String,Resolvable>();
    objects.put("request", new RequestBeanWrapper(request));
    objects.put("session", new HttpSessionWrapper(request.getSession()));
    CRRequestBuilder rB = new CRRequestBuilder(request, crConf);
    //response.setContentType(rB.getContentRepository(this.crConf.getEncoding()).getContentType()+"; charset="+this.crConf.getEncoding());
    container.processService(rB, objects, response.getOutputStream(), new ServletResponseTypeSetter(response));
    response.getOutputStream().flush();
    response.getOutputStream().close();
    // endtime
    long e = new Date().getTime();
    if(log.isInfoEnabled()) {
      StringBuilder requestID = new StringBuilder();
      requestID.append(request.getRequestURI());
      if(request.getQueryString() != null) {
        requestID.append('?');
        requestID.append(request.getQueryString());
      }
      log.info("Executiontime for " + requestID + ":" + (e - s));
    }
    uc.stop();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doService(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doService(request, response);
  }

}