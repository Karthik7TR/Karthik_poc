package com.thomsonreuters.uscl.ereader.orchestrate.core;


/**
 * A utility to send JMS messages to the Spring Batch Engine web application (sbEngine) requesting it to start jobs.
 * Exceptions from these methods are related to interaction (placing messages on) with a JMS queue,
 * not exceptions from job operations themselves which are carried out from the remote sbEngine web application.
 */
public interface JobControl {
	
	/**
	 * Sends a JMS message to the sbEngine to launch a job with the specified set of job parameters at
	 * default normal priority.  This job request is placed in the normal processing queue and is subject to
	 * the throttling of jobs.
	 * @param jobName job definition name defined in the Spring Batch engine
	 * @throws Exception on failure to send launch message to engine.
	 */
	public void startJob(String jobName) throws Exception;

	/**
	 * Sends a JMS message to the sbEngine to launch a job with the specified set of job parameters at
	 * default normal priority.  This job request is placed in the normal processing queue and is subject to
	 * the throttling of jobs.	 * @param jobName job definition name defined in the Spring Batch engine
	 * @param priority thread priority (1-10) 1=lowest, 10=highest, see Thread class. May be null to default to normal (5) priority.
	 * @throws Exception on failure to send launch message to the engine.
	 */
	public void startJob(String jobName, Integer priority) throws Exception;
	
	/**
	 * Sends a JMS message to the sbEngine to launch a job immediately without being subject to the job throttling
	 * constraints of the engine.
	 * @param jobName job definition name defined in the Spring Batch engine
	 * @param params become the job parameters used to launch the job
	 * @throws Exception on failure to send launch message to engine.
	 */
	public void startJobImmediately(String jobName) throws Exception;
}
