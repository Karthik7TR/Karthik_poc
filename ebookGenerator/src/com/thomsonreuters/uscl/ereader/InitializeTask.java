/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for
 * use in later steps.
 * This includes various file system path calculations based on the JobParameters used to run the job.
 *
 */
public class InitializeTask extends AbstractSbTasklet {

	@Override
	public ExitStatus executeStep(StepContribution contribution,
								  ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();
		Map<String, Object> jobParams = stepContext.getJobParameters();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		
		// Create the root directory for the ebook
		// TODO: STUB: calculate ebook dir path from job params
		//File rootEbookDirPath = new File ("/nas/ebookbuilder/data/YYYYMMDD/ebookId/jobId");
		File rootEbookDirPath = new File ("/nas/ebookbuilder/data/samplebook");  // STUB
		rootEbookDirPath.mkdirs();
		
		// Create the absolute path to the final ebook artifact - a GNU Zip file
		// TODO: <titleid>.gz file basename is a function of job params, sample: "FL_2011_LOCAL.gz"
		String bookCode = "TODO_BOOK_TITLE_ID";
		File ebookFilePath = new File(rootEbookDirPath, bookCode + ".gz");
		
		// Place data on the JobExecutionContext for use in later steps
		jobExecutionContext.putString(JobExecutionKey.EBOOK_DIRECTORY_PATH, rootEbookDirPath.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FILE_PATH, ebookFilePath.getAbsolutePath());

		return ExitStatus.COMPLETED;
	}

}
