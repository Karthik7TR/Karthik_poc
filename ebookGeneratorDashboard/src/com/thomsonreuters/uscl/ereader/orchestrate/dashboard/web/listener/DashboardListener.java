package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DashboardListener implements ServletContextListener {
	//private static final Logger log = Logger.getLogger(DashboardListener.class);
	
	/**
	 * Servlet container lifecycle method called when the web container is shut down.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

	}
}
