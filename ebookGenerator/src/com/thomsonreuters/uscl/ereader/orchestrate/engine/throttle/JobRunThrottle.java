package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

/**
 * Stores the number of currently executing jobs.
 */
@Component
public class JobRunThrottle implements Throttle {
	
	@Resource(name="jobRunThrottleConfig")
	private ThrottleConfig throttleConfig;
	@Autowired
	private EngineDao dao;
	
	public JobRunThrottle() {
		setThrottleConfig(throttleConfig);
	}
	
	@Override
	public boolean isAtMaximum() {
		return (getCount() >= throttleConfig.getMaximum());
	}
	
	/**
	 * Returns the number of currently executing jobs.
	 */
	@Override
	public int getCount() {
		return dao.getRunningJobExecutionCount();
	}

	@Override
	public void setCount(int count) {
		throw new RuntimeException("Not allowed to explicitly set a count of currently executing jobs");
	}
	
	@Override
	public ThrottleConfig getThrottleConfig() {
		return throttleConfig;
	}
	@Override
	public void setThrottleConfig(ThrottleConfig config) {
		this.throttleConfig = config;
	}
}
