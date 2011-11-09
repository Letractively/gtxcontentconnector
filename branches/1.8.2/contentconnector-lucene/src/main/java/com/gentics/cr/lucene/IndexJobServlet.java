package com.gentics.cr.lucene;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gentics.cr.CRConfigUtil;
import com.gentics.cr.lucene.information.SpecialDirectoryRegistry;
import com.gentics.cr.monitoring.MonitorFactory;
import com.gentics.cr.servlet.VelocityServlet;
import com.gentics.cr.util.indexing.AbstractUpdateCheckerJob;
import com.gentics.cr.util.indexing.IndexController;
import com.gentics.cr.util.indexing.IndexJobQueue;
import com.gentics.cr.util.indexing.IndexLocation;




/**
 * @author Christopher Supnig
 */
public class IndexJobServlet extends VelocityServlet {

	private static final String NAGIOS_PARAM = "nagios";
	private static final long serialVersionUID = 0002L;
	private Logger log = Logger.getLogger(IndexJobServlet.class);
	protected IndexController indexer;

	public void init(final ServletConfig config) throws ServletException {

		super.init(config);
		this.indexer = initIndexController(config);

	}
	
	/**
	 * implemented as own method to change executed context.
	 * 
	 * @param config
	 * @return indexController
	 */
	public IndexController initIndexController(final ServletConfig config) {
		return new IndexController(config.getServletName());
	}

	@Override
	public final void destroy() {
	if (indexer != null) {
			indexer.stop();
		}
	}


	public void doService(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		this.log.debug("Request:" + request.getQueryString());
		String nagString = request.getParameter(NAGIOS_PARAM);
		boolean doNag = Boolean.parseBoolean(nagString);
		// starttime
		long s = new Date().getTime();
		// get the objects

		String action = getAction(request);
		String index = request.getParameter("idx");

		if (doNag) {
			response.setContentType("text/plain");
			Hashtable<String, IndexLocation> indexTable = indexer.getIndexes();
			for (Entry<String, IndexLocation> e : indexTable.entrySet()) {
				if (e.getKey().equalsIgnoreCase(index)) {
					IndexLocation loc = e.getValue();
					IndexJobQueue queue = loc.getQueue();
					if (queue != null && queue.isRunning()) {
						response.getWriter().write("WorkerThread:OK\n");
					} else {
						response.getWriter().write("WorkerThread:NOK\n");
					}
					response.getWriter().write("ObjectsInIndex:" + loc.getDocCount()
							+ "\n");
					AbstractUpdateCheckerJob j = queue.getCurrentJob();
					if (j != null) {
						response.getWriter().write("CurrentJobObjectsToIndex:"
								+ j.getObjectsToIndex() + "\n");
					}
				}
			}
			skipRenderingVelocity();
		} else {
			response.setContentType("text/html");
			Hashtable<String, IndexLocation> indexTable = indexer.getIndexes();
			
			setTemplateVariables(request);
			
			for (Entry<String, IndexLocation> e : indexTable.entrySet()) {
			IndexLocation loc = e.getValue();
				IndexJobQueue queue = loc.getQueue();
				Hashtable<String, CRConfigUtil> map = loc.getCRMap();
				if (e.getKey().equalsIgnoreCase(index)) {
					if ("stopWorker".equalsIgnoreCase(action)) {
						queue.pauseWorker();
					}
					if ("startWorker".equalsIgnoreCase(action)) {
						queue.resumeWorker();
					}
					if ("clear".equalsIgnoreCase(action))	{
						loc.createClearJob();
					}
					if ("optimize".equalsIgnoreCase(action))	{
						loc.createOptimizeJob();
					}
					if ("addJob".equalsIgnoreCase(action)) {
						String cr = request.getParameter("cr");
						if ("all".equalsIgnoreCase(cr)) {
							loc.createAllCRIndexJobs();
						} else {
							if (cr != null) {
								CRConfigUtil crc = map.get(cr);
								loc.createCRIndexJob(crc, map);
							}
						}
					}
				}
			}
		}
		render(response);
		// endtime
		long e = new Date().getTime();
		this.log.info("Executiontime for getting Status " + (e - s));
	}
	
	/**
     * set variables for velocity template
     */
	protected void setTemplateVariables(HttpServletRequest request) {
		Hashtable<String, IndexLocation> indexTable = indexer.getIndexes();
		String nc = "&t=" + System.currentTimeMillis();
		String selectedIndex = request.getParameter("index");
		Long totalMemory = Runtime.getRuntime().totalMemory();
		Long freeMemory = Runtime.getRuntime().freeMemory();
		Long maxMemory = Runtime.getRuntime().maxMemory();
		
		setTemplateVariable("specialDirs", SpecialDirectoryRegistry
				.getInstance().getSpecialDirectories()); 
		setTemplateVariable("indexes", indexTable.entrySet());
		setTemplateVariable("nc", nc);
		setTemplateVariable("selectedIndex", selectedIndex);
		setTemplateVariable("report", MonitorFactory.getSimpleReport());
		setTemplateVariable("action", getAction(request));
		setTemplateVariable("maxmemory", maxMemory);
		setTemplateVariable("totalmemory", totalMemory);
		setTemplateVariable("freememory", freeMemory);
		setTemplateVariable("usedmemory", totalMemory - freeMemory);
	}
	
	protected String getAction(HttpServletRequest request) {
		return request.getParameter("action");
	}

}