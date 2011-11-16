package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.tasklet.play;


import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class PlayTasklet implements Tasklet {
	private static final Logger log = Logger.getLogger(PlayTasklet.class);

	private String mesg;
	public PlayTasklet(String mesg) {
		this.mesg = mesg;
	}
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		StepContext stepContext = chunkContext.getStepContext();

		Map<String, Object> jobParams = stepContext.getJobParameters();
log.debug(mesg + " params: " + jobParams);
		StepExecution stepExecution = stepContext.getStepExecution();
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		
		// Add an execution context key/value pair
		Date date = new Date();
		long time = date.getTime();
		jobExecutionContext.put("jec_key-"+time, time);
		stepExecutionContext.put("sec_key-"+time, time);
		
		Thread.sleep(30000);  // pretend we have a lot of work to do...
		
		//if (time > 0) throw new RuntimeException("Some bogus problem occured on " + date);  // TESTING
		
		return RepeatStatus.FINISHED;
	}
}
