package com.thomsonreuters.uscl.ereader.orchestrate.core;


/**
 * A utility to send JMS messages to the Spring Batch Engine web application (sbEngine) requesting it to start jobs.
 * Exceptions from these methods are related to interaction (placing messages on) with a JMS queue,
 * not exceptions from job operations themselves which are carried out from the remote engine web application.
 */
public interface JobControl {
	
	/**
	 * Place a job start request on the normal priority job request queue.
	 * This job request is subject to the throttling of jobs.
	 * @param jobName job definition name defined in the Spring Batch engine
	 * @throws Exception on failure to send launch message to the engine.
	 */
	public void enqueueNormalPriorityJobRunRequest(String jobName, int threadPriority) throws Exception;

	/**
	 * Place a job start request on the high priority job request queue.
	 * This means that this job would start before jobs on the normal priority queue.
	 * This job request is subject to the throttling of jobs.
	 * @param jobName job definition name defined in the Spring Batch engine
	 * @throws Exception on failure to send launch message to the engine.
	 */
	public void enqueueHighPriorityJobRunRequest(String jobName, int threadPriority) throws Exception;
}
