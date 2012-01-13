/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.IllegalStateException;

import org.apache.log4j.Logger;
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
	private static final Logger log = Logger.getLogger(InitializeTask.class);
	static final String BOOK_FILE_TYPE_SUFFIX = ".gz";

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
		String dynamicPath = String.format("%s/%s/%d", new SimpleDateFormat("yyyyMMdd").format(new Date()), titleId, jobInstance.getId());
		File workDirectory = new File(rootWorkDirectory, dynamicPath);
		workDirectory.mkdirs();
		log.debug("workDirectory: " + workDirectory.getAbsolutePath());
		if (!workDirectory.exists()) {
			throw new IllegalStateException("Expected work directory was not created in the filesystem: " + workDirectory.getAbsolutePath());
		}
		
		// Create gather directories
		File gatherDirectory = new File(workDirectory, "Gather");
		File docsDirectory = new File(gatherDirectory, "Docs");
		File imagesDirectory = new File(gatherDirectory, "Images");
		File staticImagesDirectory = new File(imagesDirectory, "Static");
		File dynamicImagesDirectory = new File(imagesDirectory, "Dynamic");
		File assembleDirectory = new File(workDirectory, "Assemble");
		File assembledTitleDirectory = new File(assembleDirectory, titleId);
		gatherDirectory.mkdir();
		docsDirectory.mkdir();
		imagesDirectory.mkdir();		// Root directory for images
		staticImagesDirectory.mkdir();	// Standard filesystem images
		dynamicImagesDirectory.mkdir();	// Images from the Image Vertical REST service
		assembleDirectory.mkdir();
		assembledTitleDirectory.mkdir();
		
		// Create format directories
		File formatDirectory = new File(workDirectory, "Format");
		File transformedDirectory = new File(formatDirectory, "Transformed");
		File htmlWrapperDirectory = new File(formatDirectory, "HTMLWrapper");
		formatDirectory.mkdir();
		transformedDirectory.mkdir();
		htmlWrapperDirectory.mkdir();
		//File htmlDirectory = new File(formatDirectory, "HTML");
		
		// Create the absolute path to the final e-book artifact - a GNU ZIP file
		// "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
		File ebookFile = new File(workDirectory, titleId + BOOK_FILE_TYPE_SUFFIX);
		File titleXml = new File(assembledTitleDirectory, "title.xml");
		
		// File containing image GUID's one per line
		File imageGuidsFile = new File(gatherDirectory, "image-guids.txt");
		
		// Place data on the JobExecutionContext for use in later steps
		jobExecutionContext.putString(JobExecutionKey.EBOOK_DIRECTORY_PATH, assembledTitleDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FILE_PATH, ebookFile.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_GATHER_DOCS_PATH, docsDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_GATHER_IMAGE_DIR_PATH, imagesDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_GATHER_IMAGE_STATIC_DIR_PATH, staticImagesDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_GATHER_IMAGE_DYNAMIC_DIR_PATH, dynamicImagesDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_GATHER_IMAGE_GUIDS_FILE_PATH, imageGuidsFile.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FORMAT_TRANSFORMED_PATH, transformedDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.EBOOK_FORMAT_HTML_WRAPPER_PATH, htmlWrapperDirectory.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.TITLE_XML_PATH, titleXml.getAbsolutePath());

		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setRootWorkDirectory(File rootDir) {
		this.rootWorkDirectory = rootDir;
	}
}
