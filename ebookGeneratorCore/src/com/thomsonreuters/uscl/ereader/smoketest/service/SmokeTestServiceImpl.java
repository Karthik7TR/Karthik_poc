/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.smoketest.service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.smoketest.dao.SmokeTestDao;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;

/**
 * Service that returns Server statuses
 *
 */
public class SmokeTestServiceImpl implements SmokeTestService
{
	//private static final Logger log = LogManager.getLogger(SmokeTestServiceImpl.class);
	public static final File APPSERVER_TOMCAT_DIR = new File("/appserver/tomcat");
	
	private SmokeTestDao dao;
	@Resource(name = "dataSource")
	private BasicDataSource basicDataSource;
	
	private static final int TIME_OUT = 3000; // In milliseconds
	
	private static final String[] qedManagerServers = {"c111zmxctasux", "c111fesctasux"};
	private static final String[] qedGeneratorServers = {"c111trvctasux", "c111ermctasux", "c111udzctasux"};
	
	private static final String[] prodManagerServers = {"c111hzactaspf", "c111yqpctaspf"};
	private static final String[] prodGeneratorServers = {"c111pjdctaspf", "c111rgfctaspf", "c111wkxctaspf", "c111gmkctaspf"};
	
	public List<SmokeTest> getCIServerStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		statuses.add(getServerStatus("c111jffctasdx"));
		
		return statuses;
	}
	
	public List<SmokeTest> getCIApplicationStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		statuses.add(getApplicationStatus("Manager", "http://c111jffctasdx:9007/ebookManager"));
		statuses.add(getApplicationStatus("Generator", "http://c111jffctasdx:9002/ebookGenerator"));
		statuses.add(getApplicationStatus("Gatherer", "http://c111jffctasdx:9001/ebookGatherer"));
		
		return statuses;
	}
	
	public List<SmokeTest> getTestServerStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		statuses.add(getServerStatus("c111heyctasqx"));
		statuses.add(getServerStatus("c111gvvctasqx"));
		
		return statuses;
	}
	
	public List<SmokeTest> getTestApplicationStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		statuses.add(getApplicationStatus("Manager", "http://c111heyctasqx:9003/ebookManager"));
		statuses.add(getApplicationStatus("Generator", "http://c111gvvctasqx:9002/ebookGenerator"));
		statuses.add(getApplicationStatus("Gatherer", "http://c111gvvctasqx:9001/ebookGatherer"));
		
		return statuses;
	}
	
	public List<SmokeTest> getQAServerStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		// List of eBook Manager Servers
		for(String server: qedManagerServers) {
			statuses.add(getServerStatus(server));
		}
		
		// List of eBook Generator Servers
		for(String server: qedGeneratorServers) {
			statuses.add(getServerStatus(server));
		}
		
		return statuses;
	}
	
	public List<SmokeTest> getQAApplicationStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		// List of eBook Manager Servers
		for(String server: qedManagerServers) {
			statuses.add(getApplicationStatus("Manager", String.format("http://%s:9001/ebookManager", server)));
		}
		
		// List of eBook Generator Servers
		for(String server: qedGeneratorServers) {
			statuses.add(getApplicationStatus("Gatherer", String.format("http://%s:9001/ebookGatherer", server)));
			statuses.add(getApplicationStatus("Generator", String.format("http://%s:9002/ebookGenerator", server)));
		}
		
		statuses.add(getApplicationStatus("Manager", "http://qa.ebookmanager.uslf.int.westgroup.com/ebookManager"));
		statuses.add(getApplicationStatus("Gatherer", "http://qa.ebookgatherer.uslf.int.westgroup.com/ebookGatherer"));
		statuses.add(getApplicationStatus("Generator", "http://qa.ebookgenerator.uslf.int.westgroup.com/ebookGenerator"));
		
		return statuses;
	}
	
	public List<SmokeTest> getLowerEnvDatabaseServerStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		// Lower environment Database servers
		statuses.add(getServerStatus("c111wuyctdbux"));
		statuses.add(getServerStatus("c111cujctdbux"));
		
		return statuses;
	}
	
	public List<SmokeTest> getProdServerStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		// List of eBook Manager Servers
		for(String server: prodManagerServers) {
			statuses.add(getServerStatus(server));
		}
		
		// List of eBook Generator Servers
		for(String server: prodGeneratorServers) {
			statuses.add(getServerStatus(server));
		}

		return statuses;
	}
	
	public List<SmokeTest> getProdApplicationStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		// List of eBook Manager Servers
		for(String server: prodManagerServers) {
			statuses.add(getApplicationStatus("Manager", String.format("http://%s:9001/ebookManager", server)));
		}
		
		// List of eBook Generator Servers
		for(String server: prodGeneratorServers) {
			statuses.add(getApplicationStatus("Gatherer", String.format("http://%s:9001/ebookGatherer", server)));
			statuses.add(getApplicationStatus("Generator", String.format("http://%s:9002/ebookGenerator", server)));
		}
		
		statuses.add(getApplicationStatus("Manager", "http://ebookmanager.uslf.int.westgroup.com/ebookManager"));
		statuses.add(getApplicationStatus("Gatherer", "http://ebookgatherer.uslf.int.westgroup.com/ebookGatherer"));
		statuses.add(getApplicationStatus("Generator", "http://ebookgenerator.uslf.int.westgroup.com/ebookGenerator"));
		
		return statuses;
	}
	
	public List<SmokeTest> getProdDatabaseServerStatuses() {
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		
		// Prod Database servers
		statuses.add(getServerStatus("c111fwnctdbpf"));
		statuses.add(getServerStatus("c111gkjctdbpf"));
		
		return statuses;
	}
	
	public List<String> getRunningApplications() {
		List<String> appNames = null;
		
		
		try {
			AppFilenameFilter filter = new AppFilenameFilter();
			if (APPSERVER_TOMCAT_DIR.exists()) {
				appNames = new ArrayList<String>(Arrays.asList(APPSERVER_TOMCAT_DIR.list(filter)));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return appNames;
	}
	
	public SmokeTest getApplicationStatus(String appName, String url) {
		SmokeTest serverStatus = new SmokeTest(); 
		serverStatus.setName(appName);
		serverStatus.setAddress(url);
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(TIME_OUT);
			connection.setReadTimeout(TIME_OUT);
			serverStatus.setIsRunning(connection.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch(Exception e) {
			e.printStackTrace();
			serverStatus.setIsRunning(false);
		}
		
		return serverStatus;
	}
	
	@Transactional(readOnly=true)
	public SmokeTest testConnection() {
		SmokeTest status = new SmokeTest();
		status.setName("Database Connection");
		status.setAddress(String.format("numActive=%d, numIdle=%d, maxActive=%d, maxWait=%d",
				basicDataSource.getNumActive(), basicDataSource.getNumIdle(),
				basicDataSource.getMaxActive(), basicDataSource.getMaxWait()));
		status.setIsRunning(dao.testConnection());
		
		return status;
	}
	
	private SmokeTest getServerStatus(String serverName) {
		SmokeTest serverStatus = new SmokeTest(); 
		try {
			InetAddress address = InetAddress.getByName(serverName);
			serverStatus.setName(address.getHostName());
			serverStatus.setAddress(address.getHostAddress());
			serverStatus.setIsRunning(address.isReachable(TIME_OUT));
	     }
	     catch (UnknownHostException e) {
	    	 e.printStackTrace();
	    	 serverStatus.setIsRunning(false);
	     }
	     catch (IOException e) {
	    	 e.printStackTrace();
	    	 serverStatus.setIsRunning(false);
	     }
		
		return serverStatus;
	}
	
	@Required
	public void setSmokeTestDao(SmokeTestDao dao) {
		this.dao = dao;
	}
}
