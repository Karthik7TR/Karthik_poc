/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;

/**
 * A View Data Object (VDO) wrapper around a Spring Batch JobExecution object (Decorator/VDO patterns).
 * Exists to provide convenience methods to expose complex calculated data and values which would otherwise be very
 * messy to calculate directly within the JSP. 
 */
public class JobExecutionVdo {
	//private static final Logger log = Logger.getLogger(JobExecutionVdo.class);
	private static final Comparator<StepExecution> STEP_START_TIME_COMPARATOR = new StepStartTimeComparator();
	/** Comparator to sort lists of properties into ascending key order */
	private static final Comparator<Map.Entry<String,?>> MAP_ENTRY_KEY_COMPARATOR = new MapEntryKeyComparator();
	
	private JobExecution jobExecution;
	private EbookAudit bookInfo;
	/**
	 * @param jobExecution the Spring Batch job execution object.
	 * May be null, but no null checks are made in the convenience methods that use it so clients must first check
	 * that the jobExecution property is not null.
	 * @param bookInfo book data needed for presentation that is associated with this instance.
	 */
	public JobExecutionVdo(JobExecution jobExecution, EbookAudit bookInfo) {
		this.jobExecution = jobExecution;
		this.bookInfo = bookInfo;
	}
	public JobExecution getJobExecution() {
		return jobExecution;
	}
	public EbookAudit getBookInfo() {
		return bookInfo;
	}
	/**
	 * Get the job execution steps in descending start time order.
	 */
	public List<StepExecution> getSteps() {
		Collection<StepExecution> stepColl = jobExecution.getStepExecutions();
		List<StepExecution> stepList = new ArrayList<StepExecution>(stepColl);
		Collections.sort(stepList, STEP_START_TIME_COMPARATOR);
		return stepList;
	}
	
	/**
	 * Used to determine if the "Restart" button should be displayed on the details page.
	 * @return true if the job is restartable by verifying the Spring Batch rules.
	 */
	public boolean isRestartable() {
		BatchStatus batchStatus = jobExecution.getStatus();
		return ((BatchStatus.STOPPED == batchStatus) ||
				(BatchStatus.FAILED == batchStatus));
	}
	
	public boolean isStoppable() {
		return (BatchStatus.STARTED == jobExecution.getStatus());
	}
	
	public String getDuration() {
		String duration = null;
		if (jobExecution != null) {
			duration = JobSummary.getExecutionDuration(JobSummary.getExecutionDuration(jobExecution.getStartTime(), jobExecution.getEndTime()));
		}
		return duration;
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
		Collections.sort(mapEntryList, MAP_ENTRY_KEY_COMPARATOR);
		return mapEntryList;
	}
}
