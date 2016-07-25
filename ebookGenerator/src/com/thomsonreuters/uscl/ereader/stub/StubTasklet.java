/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.stub;

import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

public class StubTasklet extends AbstractSbTasklet {
//	private static final Logger log = LogManager.getLogger(StubTasklet.class);

	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();
//		Map<String, Object> jobParams = stepContext.getJobParameters();
		StepExecution stepExecution = stepContext.getStepExecution();
		ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		
		// Add an execution context key/value pair
		Date date = new Date();
		long time = date.getTime();
		jobExecutionContext.put("jec_key-"+time, time);
		stepExecutionContext.put("sec_key-"+time, time);
		
//		if (time > 0) throw new RuntimeException("Some bogus problem occured on " + date);  // TESTING
		
		return ExitStatus.COMPLETED;
	}
}
