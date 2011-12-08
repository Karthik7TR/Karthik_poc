/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;


/**
 * Client-side utility to send job run request messages to either the HIGH or NORMAL priority queue
 * monitored by the engine web application requesting it to start jobs.
 * Exceptions from these methods are related to interaction (placing messages on) with a queue,
 * not exceptions from job run operations themselves which are carried out from the remote engine web application.
 */
public interface JobRunner {
	
	/**
	 * Place a job start request on the normal priority job request queue.
	 * This job request is subject to the throttling of jobs.
	 * @param jobName job definition name defined in the Spring Batch engine
	 * @throws Exception on failure to send launch message to the engine.
	 */
	public void enqueueNormalPriorityJobRunRequest(JobRunRequest jobRunRequest) throws Exception;

	/**
	 * Place a job start request on the high priority job request queue.
	 * This means that this job would start before jobs on the normal priority queue.
	 * This job request is subject to the throttling of jobs.
	 * @param jobName job definition name defined in the Spring Batch engine
	 * @throws Exception on failure to send launch message to the engine.
	 */
	public void enqueueHighPriorityJobRunRequest(JobRunRequest jobRunRequest) throws Exception;
}
