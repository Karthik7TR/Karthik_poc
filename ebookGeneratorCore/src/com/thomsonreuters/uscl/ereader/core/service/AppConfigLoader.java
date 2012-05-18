package com.thomsonreuters.uscl.ereader.core.service;

public interface AppConfigLoader {

	/**
	 * Read and synchronize the web application configuration from the database.
	 */
	public void loadApplicationConfiguration() throws Exception;
	
}
