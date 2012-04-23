/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

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
	private Ssh ssh;


	private EmailNotification emailNotification;
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
	 */
	@Override
	public void stopServer(String serverNames,String userName,String password,String appNames,String emailGroup){

		log.debug("In ServerAccessServiceImpl serverNames = "+serverNames +" userName = "+userName +" password = "+password + " appNames = " +appNames +" emailGroup = " +emailGroup);
		   killServerInstances(serverNames, userName, password, appNames);	
	       notifyJobOwner(emailGroup);
	       updateJobsInProgress();

	}
	
	
	/**
	 * Iterate over list of servers.In Production & QA environment multiple server instances could be running. 
	 * 
	 * @param serverNames ',' separated server names.
	 * @param userName
	 * @param password
	 * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
	 */
	private void killServerInstances(String serverNames,String userName,String password,String appNames) {
		
		String[] targetServerArray =  StringUtils.commaDelimitedListToStringArray(serverNames);
		String[] appNamesArray =  StringUtils.commaDelimitedListToStringArray(appNames);
        String cmd = null;
        for (String serverName : targetServerArray) {
    	   for (String applicationName : appNamesArray) {

   			cmd = "for i in `ls /appserver/tomcat/"+applicationName+"_*[^X]/bin/stopServer.sh`; do $i; done";
   			try {
   				execute(serverName, userName, password, cmd);
   			} catch (Exception e) {
   				e.printStackTrace();
   				
   			}
   			
   			log.info("Stopped server " + serverName);

    	   }
       }

	}
	
	
	/**
	 * update jobs job exit status from 'undefined' to failed.
	 * 
	 */
	private void updateJobsInProgress(){
		jobCleanupService.cleanUpDeadJobs();		
	}
	
	
	/**
	 * Email notification is sent to user group regarding server shutdown with list of jobs in progress. 
	 * These jobs will have to be re-submitted.
	 * @param emailGroup
	 */
	private  void notifyJobOwner(String emailGroup){

		ArrayList<String> jobListInfo = jobCleanupService.findListOfDeadJobs();
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
			subject = "Server is being shut down , please resubmit these jobs";
			emailAddress = emailGroup;
			emailBody = emailBodySB.toString();
			
		}else{
			emailBodySB.append("Server is being shut down");
			subject = "Server is being shut down <EOM>";
			emailAddress = emailGroup;
			emailBody = emailBodySB.toString();

		}
		log.debug("Notification email address : " + emailAddress);
		log.debug("Notification email subject : " + subject);
		log.debug("Notification email body : " + emailBody);

		emailNotification.send(emailAddress, subject, emailBody);
	}
	
	
	private void execute(String server, String user, String password, String cmd) throws Exception
	{
		log.debug("Starting " + cmd + "...");

		log.debug("Server: " + server);
		log.debug("User: " + user);
		log.debug("Pass: " + password);
		
		String retValue =	Ssh.executeCommand(server, user, password, cmd);						

		log.debug("Execute Command " + retValue + " value ");

	}
	

	@Required
	public void setEmailNotification(EmailNotification emailNotification) {
		this.emailNotification = emailNotification;
	}
	
	@Required
	public void setJobCleanupService(JobCleanupService jobCleanupService) {
		this.jobCleanupService = jobCleanupService;
	}	
	@Required
	public void setSsh(Ssh ssh) {
		this.ssh = ssh;
	}
}
