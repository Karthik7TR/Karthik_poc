package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;


public interface EngineDao {
	
	/**
	 * Returns the total number of currently executing jobs, i.e. jobs that have a batch status of BatchStatus.STARTED|STARTING.
	 * @return the number of currently executing jobs as known by the job repository.
	 */
	public int getRunningJobExecutionCount();

}
