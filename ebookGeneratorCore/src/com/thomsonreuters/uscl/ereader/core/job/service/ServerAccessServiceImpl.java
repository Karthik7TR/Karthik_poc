/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.thomsonreuters.uscl.ereader.util.EBookServerException;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import com.thomsonreuters.uscl.ereader.util.Ssh;


/**
 * Stops all the generator and gather instances from server, 
 * notifies user group about server shutdown. 
 * update all the unfinished jobs to failed exit status.
 * 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public class ServerAccessServiceImpl implements ServerAccessService {

	private static final Logger log = Logger.getLogger(ServerAccessServiceImpl.class);

	private JobCleanupService jobCleanupService;
	
	/**
	 * Stops all the generator and gather instances from server, 
	 * notifies user group about server shutdown. 
	 * update all the unfinished jobs to failed exit status.
	 *  
	 * @param serverNames ',' separated server names.
	 * @param userName
	 * @param password
	 * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
	 * @param emailGroup
	 * @throws EBookServerException 
	 */
	@Override
	public void stopServer(String serverNames,String userName,String password,String appNames,String emailGroup) throws EBookServerException{

		log.debug("In ServerAccessServiceImpl serverNames = "+serverNames +" userName = "+userName +" password = "+password + " appNames = " +appNames +" emailGroup = " +emailGroup);
			if(serverNames == null || serverNames.isEmpty()){
				
				throw new EBookServerException("Failed to kill server(s) as server name is empty.");
				
			}
			
			if(appNames == null || appNames.isEmpty()){
				
				throw new EBookServerException("Failed to kill server(s) as application name is empty.");
				
			}

			if(emailGroup == null || emailGroup.isEmpty()){
				
				throw new EBookServerException("Failed to notify end users as emailGroup is empty.");
			}
			
		   killServerInstances(serverNames, userName, password, appNames);	
		   notifyJobOwnerOnServerShutdown(emailGroup);
	       updateJobsInProgress();

	}
	
	
	/**
	 * Iterate over list of servers.In Production & QA environment multiple server instances could be running. 
	 * 
	 * @param serverNames ',' separated server names.
	 * @param userName
	 * @param password
	 * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
	 * @throws EBookServerException 
	 */
	private void killServerInstances(String serverNames,String userName,String password,String appNames) throws EBookServerException {
		

		String[] targetServerArray =  StringUtils.commaDelimitedListToStringArray(serverNames);
		String[] appNamesArray =  StringUtils.commaDelimitedListToStringArray(appNames);
        String cmd = null;
        for (String serverName : targetServerArray) {
    	   for (String applicationName : appNamesArray) {

   			cmd = "for i in `ls /appserver/tomcat/"+applicationName+"_*[^X]/bin/stopServer.sh`; do $i; done";
   		
   				execute(serverName, userName, password, cmd);
   			
   			log.info("Stopped server " + serverName);

    	   }
       }

	}
	
	
	/**
	 * update jobs job exit status from 'undefined' to failed.
	 * @throws EBookServerException 
	 * 
	 */
	private void updateJobsInProgress() throws EBookServerException{
		jobCleanupService.cleanUpDeadJobs();		
	}
	
	/**
	 * sends email notification on startup only if any of the jobs were updated. 
	 * @param serverName
	 * @param emailGroup
	 * @throws EBookServerException 
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void notifyJobOwnerOnServerStartup(String serverName,String emailGroup) throws EBookServerException{
		
		List<String> jobListInfo ;
			jobListInfo = jobCleanupService.findListOfDeadJobsByServerName(serverName);
		String subject ;
		String emailAddress ;
		String emailBody ;
		StringBuffer emailBodySB = new StringBuffer();

		
		if (jobListInfo != null && jobListInfo.size()> 0) {
			
			if(emailGroup == null || emailGroup.isEmpty()){
				
				throw new EBookServerException("Failed to notify end users as emailGroup is empty.");
			}

			emailBodySB.append("Please resubmit these jobs.");
			emailBodySB.append("\n");
			for (String string : jobListInfo) {
				emailBodySB.append(string);
				emailBodySB.append("\n");
			}
			subject = "Server is down, please resubmit these jobs";
			emailAddress = emailGroup;
			emailBody = emailBodySB.toString();
			log.debug("Notification email address : " + emailAddress);
			log.debug("Notification email subject : " + subject);
			log.debug("Notification email body : " + emailBody);

			EmailNotification.send(emailAddress, subject, emailBody);

			
		}
	}
	
	/**
	 * Email notification is sent to user group regarding server shutdown with list of jobs in progress. 
	 * These jobs will have to be re-submitted.
	 * @param emailGroup
	 * @throws EBookServerException 
	 */
	private void notifyJobOwnerOnServerShutdown(String emailGroup) throws EBookServerException {

		List<String> jobListInfo = jobCleanupService.findListOfDeadJobs();
		String subject ;
		String emailAddress ;
		String emailBody ;
		StringBuffer emailBodySB = new StringBuffer();

		
		if (jobListInfo != null && jobListInfo.size()> 0) {
			emailBodySB.append("Please resubmit these jobs.");
			emailBodySB.append("\n");
			for (String string : jobListInfo) {
				emailBodySB.append(string);
				emailBodySB.append("\n");
			}
			subject = "Server is down , please resubmit these jobs";
			emailAddress = emailGroup;
			emailBody = emailBodySB.toString();
			
		}else{
			emailBodySB.append("Server is down");
			subject = "Server is down <EOM>";
			emailAddress = emailGroup;
			emailBody = emailBodySB.toString();

		}
		log.debug("Notification email address : " + emailAddress);
		log.debug("Notification email subject : " + subject);
		log.debug("Notification email body : " + emailBody);

		EmailNotification.send(emailAddress, subject, emailBody);
	}
	
	
	private void execute(String server, String user, String password, String cmd) throws EBookServerException
	{
		log.debug("Starting " + cmd + "...");

		log.debug("Server: " + server);
		log.debug("User: " + user);
		log.debug("Pass: " + password);
		
		String retValue =	Ssh.executeCommand(server, user, password, cmd);						

		log.debug("Execute Command " + retValue + " value ");

	}
	
	@Required
	public void setJobCleanupService(JobCleanupService jobCleanupService) {
		this.jobCleanupService = jobCleanupService;
	}
}
