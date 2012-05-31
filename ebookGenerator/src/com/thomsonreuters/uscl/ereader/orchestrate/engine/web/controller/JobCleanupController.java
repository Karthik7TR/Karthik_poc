package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;

import com.thomsonreuters.uscl.ereader.core.job.service.JobCleanupService;
import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;

/**
 * Only purpose of this controller is to carry job clean up and notify user group about the jobs which were 
 * affected by server restart. 
 *   
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
@Controller
public class JobCleanupController {
	//private static final Logger log = Logger.getLogger(JobCleanupController.class);
	

	private JobCleanupService jobCleanupService;
	private ServerAccessService serverAccessService; 
	private String emailGroup;


	@PostConstruct
	public void init() throws EBookServerException{
		String hostName = null;	// The host this job running on
		try {
			InetAddress host = InetAddress.getLocalHost();
			hostName = host.getHostName();
		} catch (UnknownHostException uhe) {
			hostName = null;
		}
		serverAccessService.notifyJobOwnerOnServerStartup(hostName, emailGroup);
		jobCleanupService.cleanUpDeadJobsForGivenServer(hostName);
	}
	
	@Required
	public void setJobCleanupService(JobCleanupService jobCleanupService) {
		this.jobCleanupService = jobCleanupService;
	}
	@Required
	public void setServerAccessService(ServerAccessService serverAccessService) {
		this.serverAccessService = serverAccessService;
	}
	@Required
	public void setEmailGroup(String emailGroup) {
		this.emailGroup = emailGroup;
	}	
}
