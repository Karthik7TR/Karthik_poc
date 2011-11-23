package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.tasklet.ereader;


import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

public class PackageForBermudaTasklet extends AbstractSbTasklet {
	private static final Logger log = Logger.getLogger(PackageForBermudaTasklet.class);

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		
		// CASE 5: Job is made to "wait" in a step (a user defined exit code)
		// return new ExitStatus(Constants.EXIT_STATUS_CODE_WAITING, "Job waiting for an automatic restart.");
		
//		// CASE 4: Step implementation throws an exception = FAILURE
//		if (this != null) {
//			throw new Exception("BOGUS packageForBermuda problem ("+System.currentTimeMillis()+")");
//		}
		
		// CASE 3: Step is stopped programmatically from within step implementation
		//stepExecution.setStatus(BatchStatus.STOPPED);
		//return new ExitStatus(ExitStatus.STOPPED.getExitCode(), "The step was stopped for testing purposes.");
		
		// CASE 2: Stop the Job using the jobOperator.stop
		
		// CASE 1: Happy path
		return ExitStatus.COMPLETED;
	}
}
