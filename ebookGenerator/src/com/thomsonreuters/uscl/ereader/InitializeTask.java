/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for
 * use in later steps.
 * This includes various file system path calculations based on the JobParameters used to run the job.
 *
 */
public class InitializeTask extends AbstractSbTasklet {
	
	public static final String BOOK_FILE_TYPE_SUFFIX = ".gz";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private File rootWorkDirectory; // "/nas/ebookbuilder/data"
	
	@Override
	public ExitStatus executeStep(StepContribution contribution,
								  ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		JobInstance jobInstance = jobExecution.getJobInstance();
		JobParameters jobParams = jobInstance.getJobParameters();
		
		String titleId = jobParams.getString(JobParameterKey.TITLE_ID);
		
		// Create the work directory for the ebook and create the physical directory in the filesystem
		// "<yyyyMMdd>/<titleId>/<jobInstanceId>"
		// Sample: "/nas/ebookbuilder/data/20120131/FRCP/356"
		String dynamicPath = String.format("%s/%s/%d", DATE_FORMAT.format(new Date()), titleId, jobInstance.getId());
		File workDirectory = new File(rootWorkDirectory, dynamicPath);
		workDirectory.mkdirs();
		
		//Create gather directories
		File gatherDirectory = new File(workDirectory, "Gather");
		File docsDirectory = new File(gatherDirectory, "Docs");

		//Create format directories
		File formatDirectory = new File(workDirectory, "Format");
		File transformedDirectory = new File(formatDirectory, "Transformed");
		File htmlWrapperDirectory = new File(formatDirectory, "HTMLWrapper");
		//File htmlDirectory = new File(formatDirectory, "HTML");
		
		// Create the absolute path to the final e-book artifact - a GNU ZIP file
		// "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
		File ebookFile = new File(workDirectory, titleId + BOOK_FILE_TYPE_SUFFIX);
		
		// Place data on the JobExecutionContext for use in later steps
		jobExecutionContext.putString(JobExecutionKey.EBOOK_DIRECTORY_PATH, workDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FILE_PATH, ebookFile.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_GATHER_DOCS_PATH, docsDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FORMAT_TRANSFORMED_PATH, transformedDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FORMAT_HTML_WRAPPER_PATH, htmlWrapperDirectory.getAbsolutePath());

		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setRootWorkDirectory(File rootDir) {
		this.rootWorkDirectory = rootDir;
	}
}
