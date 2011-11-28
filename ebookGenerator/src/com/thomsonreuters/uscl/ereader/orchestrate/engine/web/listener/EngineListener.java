package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EngineListener implements ServletContextListener {
	//private static final Logger log = Logger.getLogger(EngineListener.class);
	
	/**
	 * Servlet container life-cycle method called when the web container is shut down.
	 * Used to terminate the Quartz scheduler which is started from the QuartzManager Spring bean.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
//		ServletContext servletContext = sce.getServletContext();
//		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
	}
}
