/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * Stops all the generator and gather instances from server, 
 * notifies user group about server shutdown. 
 * update all the unfinished jobs to failed exit status.
 * 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public interface ServerAccessService {

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
	public void stopServer(String serverNames,String userName,String password,String appNames,String emailGroup);
	/**
	 * Sends sends email notification on startup only if any of the jobs were updated. 
	 * @param serverName
	 * @param emailGroup
	 */
	public void notifyJobOwnerOnServerStartup(String serverName, String emailGroup);

}
