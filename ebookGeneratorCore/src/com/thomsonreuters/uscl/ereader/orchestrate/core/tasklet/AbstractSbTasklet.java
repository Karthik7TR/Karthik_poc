/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * Wrapper designed to handle exceptions thrown from step execution business as a STOPPED exit status for the step and job.
 * This is a specific requirement for eReader.
 */
public abstract class AbstractSbTasklet implements Tasklet {
	private static final Logger LOG = Logger.getLogger(AbstractSbTasklet.class);
	public static final String EBOOK_DEFINITON = "bookDefn";
	private String missingDocFile = "";
		
	
	public void setMissingDocFile(final String missingDocFile) {
		this.missingDocFile = missingDocFile;
	}


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
        try {
            
        ExitStatus stepTransition = executeStep(contribution, chunkContext);

        // Set the step execution exit status (transition) name to what was returned from executeStep() in the subclass
        stepExecution.setExitStatus(stepTransition);
        } catch (Exception e){
        	sendNotification(chunkContext,e.getMessage());
            throw e;
        }

        //re-initialize missingDocFile 
        missingDocFile = "";
		return RepeatStatus.FINISHED;
	}
	
    private void sendNotification(ChunkContext chunkContext, String bodyMessage) {
        
        JobParameters param = getJobParameters(chunkContext);
        ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(EBOOK_DEFINITON);
        String emailId = param.getString(JobParameterKey.USER_EMAIL);
        emailId="Thomson.eBookGenerator-Dev@thomsonreuters.com"; // need to be updated 
        String subject = bookDefinition.getTitleId() + "  " + bookDefinition.getProviewDisplayName();
        if ("".equals(missingDocFile))
        {
           EmailNotification.send(emailId, subject, bodyMessage);
        }
        else {
        	List<String> fileList = new ArrayList<String>();
        	EmailNotification.sendWithAttachment(emailId, subject, bodyMessage, fileList);
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
}
