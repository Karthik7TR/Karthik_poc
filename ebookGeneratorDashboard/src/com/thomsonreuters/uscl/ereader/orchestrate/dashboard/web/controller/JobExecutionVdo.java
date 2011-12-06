package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

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
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;


/**
 * A View Data Object (VDO) wrapper around a Spring Batch JobExecution object (Decorator/VDO patterns).
 * Exists to provide convenience methods to expose complex calculated data and values which would otherwise be very
 * messy to calculate directly within the JSP. 
 */
public class JobExecutionVdo {
	//private static final Logger log = Logger.getLogger(JobExecutionVdo.class);
	private static final Comparator<StepExecution> stepStartTimeComparator = new StepStartTimeComparator();
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
	public String getBookCode() {
		return jobExecution.getJobInstance().getJobParameters().getString(EngineConstants.JOB_PARAM_BOOK_CODE);
	}
	public String getBookTitle() {
		return jobExecution.getJobInstance().getJobParameters().getString(EngineConstants.JOB_PARAM_BOOK_TITLE);
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
		return ((BatchStatus.STOPPED == jobExecution.getStatus()) ||
				(BatchStatus.FAILED == jobExecution.getStatus()));
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
	
	public BatchAndExitStatus getLastStepBatchAndExitStatus() {
		BatchStatus batchStatus = null;
		ExitStatus exitStatus = null;
		Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
		int size = stepExecutions.size();
		if (size > 0) {
			StepExecution[] stepExecutionArray = new StepExecution[size];
			stepExecutions.toArray(stepExecutionArray);
			batchStatus = stepExecutionArray[size-1].getStatus();
			exitStatus = stepExecutionArray[size-1].getExitStatus();
		}
		return new BatchAndExitStatus(batchStatus, exitStatus);
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
	
	public List<Map.Entry<String,JobParameter>> getJobParameterList() {
		List<Map.Entry<String,JobParameter>> parameterList = new ArrayList<Map.Entry<String,JobParameter>>();
		if (jobExecution != null) {
			JobParameters jobParameters = jobExecution.getJobInstance().getJobParameters();
			Map<String,JobParameter> jobParameterMap = jobParameters.getParameters();
			Set<Map.Entry<String,JobParameter>> entrySet = jobParameterMap.entrySet();
			// Convert the parameters to a list for presentation
			Iterator<Map.Entry<String,JobParameter>> entryIterator = entrySet.iterator();
			while(entryIterator.hasNext()) {
				parameterList.add(entryIterator.next());
			}
		}	
		return parameterList;
	}
	
	public List<Map.Entry<String,Object>> getJobExecutionContextList() {
		List<Map.Entry<String,Object>> list = new ArrayList<Map.Entry<String,Object>>();
		if (jobExecution != null) {
			ExecutionContext execContext = jobExecution.getExecutionContext();
			Set<Map.Entry<String,Object>> entrySet = execContext.entrySet();
			Iterator<Map.Entry<String,Object>> entryIterator = entrySet.iterator();
			while(entryIterator.hasNext()) {
				list.add(entryIterator.next());
			}
		}
		return list;
	}
}

