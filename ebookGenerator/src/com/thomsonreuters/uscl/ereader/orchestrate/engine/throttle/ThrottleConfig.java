package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;


/**
 * Encapsulates the maximum number of concurrent events that are currently allowed for some operation.
 */
public interface ThrottleConfig {
	
	public int getMaximum();
	public void setMaximum(int max);

}
