/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

@Deprecated
// Deprecated in favor of the use of the JobThrottleConfig which is populated via the Manager Administration:Job Throttle Configuration
public interface JobStartupThrottleDao {
	
	
	/**
	 * Returns throttle limit number for step passed on.
	 * @param throttlStep
	 * @return
	 */
//	public int getThrottleLimitForExecutionStep(String throttleStep);
	
	/**
	 * Returns throttle limit number for passed on step and military time in number like 1.2.3....24
	 * need to implement this method once we decide to implement throttle based on bell curve. 
	 * @param miletoryTime
	 * @param throttleStep
	 * @return
	 */
//	public int getThrottleLimitForCurrentTimeAndExecutionStep(int militaryTime,String throttleStep);

}
