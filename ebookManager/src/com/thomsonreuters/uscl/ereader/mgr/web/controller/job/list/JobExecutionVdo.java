/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Period;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobParameterKey;


/**
 * A View Data Object (VDO) wrapper around a Spring Batch JobExecution object (Decorator/VDO patterns).
 * Exists to provide convenience methods to expose complex calculated data and values which would otherwise be very
 * messy to calculate directly within the JSP. 
 */
public class JobExecutionVdo {
	//private static final Logger log = Logger.getLogger(JobExecutionVdo.class);
	private static final Comparator<StepExecution> stepStartTimeComparator = new StepStartTimeComparator();
	/** Comparator to sort lists of properties into ascending key order */
	private static final Comparator<Map.Entry<String,?>> mapEntryKeyComparator = new MapEntryKeyComparator();
	private JobExecution jobExecution;
	
	/**
	 * @param jobExecution may be null
	 * @param jobExecutions the list of executions that have run as the above job instance.
	 */
	public JobExecutionVdo(JobExecution jobExecution) {
		this.jobExecution = jobExecution;
	}
	public JobExecution getJobExecution() {
		return jobExecution;
	}
	public Long getMajorVersion() {
		return jobExecution.getJobInstance().getJobParameters().getLong(JobParameterKey.MAJOR_VERSION);
	}
	public String getTitleId() {
		return jobExecution.getJobInstance().getJobParameters().getString(JobParameterKey.TITLE_ID);
	}
	public String getFullyQualifiedTitleId() {
		return jobExecution.getJobInstance().getJobParameters().getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
	}
	public String getBookName() {
		return jobExecution.getJobInstance().getJobParameters().getString(JobParameterKey.BOOK_NAME);
	}
	/**
	 * Get the job execution steps in descending start time order.
	 */
	public List<StepExecution> getSteps() {
		Collection<StepExecution> stepColl = jobExecution.getStepExecutions();
		List<StepExecution> stepList = new ArrayList<StepExecution>(stepColl);
		Collections.sort(stepList, stepStartTimeComparator);
		return stepList;
	}
	
	/**
	 * Used to determine if the "Restart" button should be displayed on the details page.
	 * @return true if the job is restartable by Spring Batch definition.
	 */
	public boolean isRestartable() {
		if (jobExecution == null) {
			return false;
		}
		BatchStatus batchStatus = jobExecution.getStatus();
		return ((BatchStatus.STOPPED == batchStatus) ||
				(BatchStatus.FAILED == batchStatus));
	}
	
	public boolean isStoppable() {
		return (BatchStatus.STARTED == jobExecution.getStatus());
	}

	/**
	 * The delta between the job start time and ending time.  If the job is running
	 * then the run time to present is calculated.
	 * @return the integer execution time total if finished, or current if still running
	 */
	public long getExecutionDurationMs() {
		long ms = -1;
		if (jobExecution != null) {
			ms = getExecutionDurationMs(jobExecution.getStartTime(), jobExecution.getEndTime());
		}
		return ms;
	}
	
	public static long getExecutionDurationMs(Date startTime, Date endTime) {		
		long execTimeMs = -1;
		if (startTime != null) {
			if (endTime == null) {
				endTime = new Date();
			}
			execTimeMs = endTime.getTime() - startTime.getTime();
		}
		return execTimeMs;
	}
	
	/**
	 * @return  the execution time period in the fomat mm:hh:ss.SSS
	 */
	public String getExecutionDuration() {
		return getExecutionDuration(getExecutionDurationMs());
	}
	
	public static String getExecutionDuration(long durationMs) {
		StringBuffer periodString = new StringBuffer();
		if (durationMs > -1) {
			Period period = new Period(durationMs);
			periodString.append((period.getHours() < 10) ? "0" : "");
			periodString.append(period.getHours());
			periodString.append(":");
			periodString.append((period.getMinutes() < 10) ? "0" : "");
			periodString.append(period.getMinutes());
			periodString.append(":");
			periodString.append((period.getSeconds() < 10) ? "0" : "");
			periodString.append(period.getSeconds());
			periodString.append(".");
			periodString.append((period.getMillis() < 10) ? "0" : "");
			periodString.append((period.getMillis() < 100) ? "0" : "");
			periodString.append(period.getMillis());
		}
		return periodString.toString();
	}
	
	/**
	 * Creates a sorted list of job launch parameter map entries fetched from the current JobExecution.
	 * This list of key/value pairs is then presented on the JSP. 
	 * @return a sorted list of job parameter map entries, possibly empty, never null
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<Map.Entry<String,?>> getJobParameterMapEntryList() {
		if (jobExecution != null) {
			JobParameters jobParameters = jobExecution.getJobInstance().getJobParameters();
			Map<String,JobParameter> jobParameterMap = jobParameters.getParameters();
			Set entrySet = jobParameterMap.entrySet();
			return createMapEntryList(entrySet);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * Creates a sorted list of job execution context map entries from the current JobExecution.
	 * 	 * This list of key/value pairs is then presented on the JSP. 
	 * @return a sorted list of job execution context map entries, possibly empty, never null
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<Map.Entry<String,?>> getJobExecutionContextMapEntryList() {
		if (jobExecution != null) {
			ExecutionContext execContext = jobExecution.getExecutionContext();
			Set entrySet = execContext.entrySet();
			return createMapEntryList(entrySet);
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * Create a ascending key sorted list of map entries from the specified map.
	 * @param map the map whose entries will be extracted into a list sorted by key in ascending order. 
	 * @return a list of map entries sorted by key.
	 */
	private List<Map.Entry<String,?>> createMapEntryList(Set<Map.Entry<String,?>> entrySet) {
		List<Map.Entry<String,?>> mapEntryList = new ArrayList<Map.Entry<String,?>>();
		Iterator<Map.Entry<String,?>> entryIterator = entrySet.iterator();
		while(entryIterator.hasNext()) {
			mapEntryList.add(entryIterator.next());
		}
		Collections.sort(mapEntryList, mapEntryKeyComparator);
		return mapEntryList;
	}
}
