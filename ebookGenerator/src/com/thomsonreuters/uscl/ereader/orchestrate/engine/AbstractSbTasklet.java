package com.thomsonreuters.uscl.ereader.orchestrate.engine;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Wrapper designed to handle exceptions thrown from step execution business as a STOPPED exit status for the step and job.
 * This is a specific requirement for eReader.
 */
public abstract class AbstractSbTasklet implements Tasklet {
	private static final Logger log = Logger.getLogger(AbstractSbTasklet.class);
	
	public static final ExitStatus EXIT_STATUS_WAITING = new ExitStatus("WAITING", "Job waiting for an automatic restart.");
	
	/**
	 * Implement this method in the concrete subclass.
	 * @return the transition name for the step in the form of an ExitStatus.
	 * Return ExitStatus.COMPLETED for a normal finish.
	 * Return ExitStatus.STOPPED to stop the step and the job.  Returning STOPPED will automatically set the step status to BatchStatus.STOPPED.
	 * Returning a custom ExitStatus will always result in the step BatchStatus being set to BatchStatus.COMPLETED.
	 */
	public abstract ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception;
	
	
	/**
	 * Wrapper around the user implemented task logic that hides the repeat and transition calculations away.
	 */
	public final RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();

		log.debug("Step: " + stepContext.getJobName() + "." + stepContext.getStepName());
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		
		// Delegate to user defined step logic
		ExitStatus stepTransition = executeStep(contribution, chunkContext);

Thread.sleep(5000); // DEBUG: TESTING  pretend we have something to do

		// Set the step execution exit status (transition) name to what was returned from executeStep() in the subclass
		stepExecution.setExitStatus(stepTransition);
		
		// During normal processing of a restarted job, any step with a
		// status of 'COMPLETED', meaning it has already been completed successfully, will be skipped.
		// Setting allow-start-if-complete to "true" overrides this so that the step will always run.
		// An infinite loop occurs if in step 'foo' status is COMPLETED, the step exit status is STOPPED, and you restart again in step 'foo'.
		if (ExitStatus.STOPPED.getExitCode().equals(stepTransition.getExitCode())) {
			stepExecution.setStatus(BatchStatus.STOPPED);
		}
		return RepeatStatus.FINISHED;
	}
}
