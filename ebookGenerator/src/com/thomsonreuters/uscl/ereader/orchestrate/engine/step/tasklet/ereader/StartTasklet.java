package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.tasklet.ereader;


import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.AbstractSbTasklet;

public class StartTasklet extends AbstractSbTasklet {
	private static final Logger log = Logger.getLogger(StartTasklet.class);

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
//		StepContext stepContext = chunkContext.getStepContext();
//
//		Map<String, Object> jobParameters = stepContext.getJobParameters();
//		StepExecution stepExecution = stepContext.getStepExecution();
//		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
//		JobExecution jobExecution = stepExecution.getJobExecution();
//		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();


		return ExitStatus.COMPLETED;
		
	}
}
