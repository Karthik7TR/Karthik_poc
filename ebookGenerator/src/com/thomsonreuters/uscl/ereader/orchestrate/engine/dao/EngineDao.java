package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import java.util.List;

import org.springframework.batch.core.JobParameters;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.ScheduleCurvePoint;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.ScheduleDayOfWeek;


public interface EngineDao {
	
	/**
	 * Returns the total number of currently executing jobs, i.e. jobs that have a batch status of BatchStatus.STARTED|STARTING.
	 * @return the number of currently executing jobs as known by the job repository.
	 */
	public int getRunningJobExecutionCount();
	
	/**
	 * Read all the data from the curve table that defines the function curves for the job launch throttle.
	 */
	public List<ScheduleCurvePoint> findAllThrottleScheduleCurvePoints();
	
	public List<ScheduleDayOfWeek> findDayOfWeekSchedule();
	
	/**
	 * Load the launch parameters for a specific job from the database.
	 * @param jobName job whose run parameters we want
	 * @return a map of key=JobParameter
	 */
	public JobParameters loadJobParameters(String jobName);

}
