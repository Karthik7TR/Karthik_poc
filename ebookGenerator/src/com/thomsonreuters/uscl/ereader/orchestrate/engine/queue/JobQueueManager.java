/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

public interface JobQueueManager {
	
	/**
	 * Check the high priority job input JMS queue for a message. 
	 * @return the message at the front of the JMS queue, or null if JMS queue is empty.
	 */
	public JobRunRequest getHighPriorityJobRunRequest() throws Exception;

	/**
	 * Check the normal priority job input JMS queue for a message. 
	 * @return the message at the front of the JMS queue, or null if JMS queue is empty.
	 */
	public JobRunRequest getNormalPriorityJobRunRequest() throws Exception;

}
