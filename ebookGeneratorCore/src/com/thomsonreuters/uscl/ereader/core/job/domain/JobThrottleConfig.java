package com.thomsonreuters.uscl.ereader.core.job.domain;

public interface JobThrottleConfig {
	
	public int getCoreThreadPoolSize();
	public boolean isStepThrottleEnabled();
	public String getThrottleStepName();
	public int getThrotttleStepMaxJobs();
	
	public void setCoreThreadPoolSize(int coreThreadPoolSize);
	public void setStepThrottleEnabled(boolean enabled);
	public void setThrottleStepName(String throttleStepName);
	public void setThrotttleStepMaxJobs(int throtttleStepMaxJobs);
}
