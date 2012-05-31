/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * Wrapper designed to handle exceptions thrown from step execution business as a STOPPED exit status for the step and job.
 * This is a specific requirement for eReader.
 */
public abstract class AbstractSbTasklet implements Tasklet {
	private static final Logger LOG = Logger.getLogger(AbstractSbTasklet.class);
	public static final String EBOOK_DEFINITON = "bookDefn";
	public static final String IMAGE_MISSING_GUIDS_FILE = "imageMissingGuidsFile";
	public static final String DOCS_MISSING_GUIDS_FILE = "docsMissingGuidsFile";
	
	private OutageService outageService;
	

	/**
	 * Implement this method in the concrete subclass.
	 * @return the transition name for the step in the form of an ExitStatus.
	 * Return ExitStatus.COMPLETED for a normal finish.
	 * Returning a custom ExitStatus will always result in the step BatchStatus being set to BatchStatus.COMPLETED.
	 */
	public abstract ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception;
	
	
	/**
	 * Wrapper around the user implemented task logic that hides the repeat and transition calculations away.
	 */
	public final RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		StepContext stepContext = chunkContext.getStepContext();
		LOG.debug("Step: " + stepContext.getJobName() + "." + stepContext.getStepName());
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		long jobInstanceId = jobExecution.getJobInstance().getId();
		long jobExecutionId = stepExecution.getJobExecutionId();
		ExitStatus stepExitStatus = null;
		try {
        	// Check if a planned outage has come into effect, if so, fail this step right at the start
			// with an exit message indicating the interval of the outage.
        	PlannedOutage plannedOutage = outageService.processPlannedOutages();
        	if (plannedOutage != null) {
        		LOG.debug("Failing job step at start due to planned outage: " + plannedOutage);
        		SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
        		stepExitStatus = new ExitStatus(ExitStatus.FAILED.getExitCode(),
        							 String.format("Planned service outage in effect from %s to %s",
        							 sdf.format(plannedOutage.getStartTime()), sdf.format(plannedOutage.getEndTime())));
        	} else {
        		// Execute user defined step logic
        		stepExitStatus = executeStep(contribution, chunkContext);
        	}
        	// Set the step execution exit status (transition) name to what was returned from executeStep() in the subclass
        	stepExecution.setExitStatus(stepExitStatus);
        
        } catch (Exception e){
        	String stackTrace = getStackTrace(e);

        	stackTrace = "Error Message : " + e.getMessage() + "\nStack Trace is " + stackTrace; 
        	sendNotification(chunkContext,stackTrace,jobInstanceId,jobExecutionId);
            throw e;
        }

       return RepeatStatus.FINISHED;
	}
	
    private void sendNotification(ChunkContext chunkContext, String bodyMessage,long jobInstanceId,long jobExecutionId) {
        
        ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(EBOOK_DEFINITON);
		List<String> fileList = new ArrayList<String>();
		String subject;
		String failedJobInfo; 
		String emailAdd;
		String userEmailId = jobParams.getString(JobParameterKey.USER_EMAIL);
        String jobOwnerGroupEmail = jobParams.getString(JobParameterKey.JOB_OWNERS_GROUP_EMAIL);
        
        String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

        failedJobInfo = "eBook Publishing Failure:  " + jobEnvironment +"  " +bookDefinition.getFullyQualifiedTitleId() +"  "+bookDefinition.getProviewDisplayName() +"  "+jobInstanceId +"  "+jobExecutionId  ; 
        bodyMessage = failedJobInfo + "  \n"+ bodyMessage;
        subject = failedJobInfo;
        
        String imgGuidsFile = jobExecutionContext.getString(IMAGE_MISSING_GUIDS_FILE);
        
        /*** if job owner has entered email address to notify jobs outcome send email to that address else use default email address.**/
        if(userEmailId != null &&  !userEmailId.isEmpty())
        {
        	emailAdd= userEmailId;	
        }else{
        	emailAdd = jobOwnerGroupEmail;
        }
        
        if (getFileSize(imgGuidsFile) > 0 )
        {
        	fileList.add(imgGuidsFile);
        }
        
        String missingGuidsFile = jobExecutionContext.getString(DOCS_MISSING_GUIDS_FILE);
        
        if (getFileSize(missingGuidsFile) > 0 )
        {
        	fileList.add(missingGuidsFile);
        }
        
        if (fileList.size() > 0)
        {
        	EmailNotification.sendWithAttachment(emailAdd, subject, bodyMessage.toString(), fileList);
           
        }
        else {
        	EmailNotification.send(emailAdd, subject, bodyMessage.toString());
        }
        
    }

	
	/**
	 * Retrieves the JobParameters from the Spring Batch ChunkContext.
	 * 
	 * <p>Job Parameters are set at job initialization time, and are immutable afterwards.</p>
	 * @param chunkContext the Spring Batch context exposed to the step implementor (to retrieve Job Parameters from)
	 * @return the Job Parameters
	 */
	protected static JobParameters getJobParameters(ChunkContext chunkContext) {
		return chunkContext.getStepContext().getStepExecution().getJobParameters();
	}
	
	/**
	 * Retrieves the Job ExecutionContext from the Spring Batch ChunkContext.
	 * 
	 * <p>Job Execution Context is where we put information for later steps to use. Each value added to the Job Execution Context is< mutable.</p>
	 * @param chunkContext the Spring Batch context exposed to the step implementor (to retrieve Job Execution Context from)
	 * @return the Job Execution context
	 */
	protected static ExecutionContext getJobExecutionContext(ChunkContext chunkContext) {
		return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
	}
	
	protected static JobInstance getJobInstance(ChunkContext chunkContext) {
		return chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance();
	}
	
	/**
	 * Retrieves a String property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
	 * 
	 * @param executionContext The ExecutionContext that contains the property to retrieve.
	 * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
	 *   
	 * @return the String value corresponding to the provided key.
	 */
	protected static String getRequiredStringProperty(final ExecutionContext executionContext, final String propertyKey) {
		assertPropertyExists(executionContext, propertyKey);
		return executionContext.getString(propertyKey);
	}
	
	/**
	 * Retrieves a int property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
	 * 
	 * @param executionContext The ExecutionContext that contains the property to retrieve.
	 * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
	 *  
	 * @return the int value corresponding to the provided key.
	 */
	protected static int getRequiredIntProperty(final ExecutionContext executionContext, final String propertyKey) {
		assertPropertyExists(executionContext, propertyKey);
		return executionContext.getInt(propertyKey);
	}
	
	/**
	 * Retrieves a long property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
	 * 
	 * @param executionContext The ExecutionContext that contains the property to retrieve.
	 * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
	 *  
	 * @return the long value corresponding to the provided key.
	 */
	protected static long getRequiredLongProperty(final ExecutionContext executionContext, final String propertyKey) {
		assertPropertyExists(executionContext, propertyKey);
		return executionContext.getLong(propertyKey);
	}
	
	/**
	 * Retrieves a double property from the provided ExecutionContext. An existence check is performed prior to attempting type-safe retrieval using the Spring Batch API.
	 * 
	 * @param executionContext The ExecutionContext that contains the property to retrieve.
	 * @param propertyKey the name of the property.  The key used as an argument to this method should be exposed via the JobExecutionKey class. If deviation is necessary, talk to Chris, Tom and Nirupam.
	 *  
	 * @return the double value corresponding to the provided key.
	 */
	protected static double getRequiredDoubleProperty(final ExecutionContext executionContext, final String propertyKey) {
		assertPropertyExists(executionContext, propertyKey);
		return executionContext.getDouble(propertyKey);
	}
	
	private static void assertPropertyExists(final ExecutionContext executionContext, final String propertyKey) {
		if (!executionContext.containsKey(propertyKey)) {
			throw new IllegalArgumentException("The required property '" + propertyKey + "' was not present in the execution context, but should have been. " +
					"This is considered a programming error, please contact development. The job cannot continue until the '" + propertyKey + "' property is present.");
		}
	}
	
	/**
	 * @param aThrowable
	 * @return string corresponding to the provided exception's stack trace.
	 */
	private String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }
	
	 /**
	  * @param filename
	  * @return a long value of file length
	 */
	private long getFileSize(final String filename) {

		   File file = new File(filename);
		   if (!file.exists() || !file.isFile()) {
			  return -1;
		   }
		   return file.length();
	}
	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
}
