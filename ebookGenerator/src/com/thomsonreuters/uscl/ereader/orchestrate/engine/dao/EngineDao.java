/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
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
	
	/**
	 * Job Throttling: Get day of week 1=SUN..7=SAT to schedule name, like "WEEKEND" or "WEEKDAY" for
	 * each day of the week.
	 * @return a list of the 7 days of the week, to the max concurrent jobs schedule to be applied to it for job launch throttling.
	 */
	public List<ScheduleDayOfWeek> findDayOfWeekSchedule();

}
