package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;


/**
 * Encapsulates the maximum number of concurrent events that are currently allowed for some operation.
 */
public interface ThrottleConfig {
	
	/**
	 * Returns the maximum number of concurrent events for the given point in time.
	 */
	public int getMaximum();

}
