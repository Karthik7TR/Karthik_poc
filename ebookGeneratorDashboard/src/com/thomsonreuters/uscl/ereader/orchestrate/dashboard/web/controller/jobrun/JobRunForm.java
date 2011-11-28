package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobrun;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JobRunForm {
	public static final String FORM_NAME = "jobRunForm";
	
	private String jobName;		// Job name to be launched
	private int threadPriority;	// 1..10, 1=MIN, 5=NORMAL, 10=MAX
	private boolean highPriorityJob;	// if true, job request will be placed on the high priority run queue

	public String getJobName() {
		return jobName;
	}
	public int getThreadPriority() {
		return threadPriority;
	}
	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}
	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
