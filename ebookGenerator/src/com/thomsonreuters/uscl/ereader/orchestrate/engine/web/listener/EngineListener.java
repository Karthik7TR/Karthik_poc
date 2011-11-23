package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class EngineListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(EngineListener.class);
	
	/**
	 * Servlet container lifecycle method called when the web container is shut down.
	 * Used to terminate the Quartz scheduler which is started from the QuartzManager Spring bean.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

//		QuartzManager quartzManager = (QuartzManager) springContext.getBean("quartzManager");
//		try {
//			quartzManager.shutdownQuartzScheduler();
//			log.info("Quartz scheduler has been successfully shutdown.");
//		} catch (SchedulerException e) {
//			log.error("Quartz scheduler failed to properly shutdown.", e);
//		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
	}
}
