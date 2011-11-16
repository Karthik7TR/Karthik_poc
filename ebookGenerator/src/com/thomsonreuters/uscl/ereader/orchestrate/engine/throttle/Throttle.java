package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

public interface Throttle {

	/**
	 * Returns true if we are in a petal-to-the-metal condition, i.e. we are at the maximum number of actions.
	 * Example: The max number of concurrent batch jobs has been reached, in which case this method will return true. 
	 * @return true if we are at the state maximum for a given action.
	 */
	public boolean isAtMaximum();
	
	public int getCount();
	public void setCount(int count);
	
	public ThrottleConfig getThrottleConfig();
	public void setThrottleConfig(ThrottleConfig config);
}
