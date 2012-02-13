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
		String dynamicPath = "data/" + String.format("%s/%s/%d", 
				new SimpleDateFormat("yyyyMMdd").format(new Date()), titleId, jobInstance.getId());
		File workDirectory = new File(rootWorkDirectory, dynamicPath);
		workDirectory.mkdirs();
		log.debug("workDirectory: " + workDirectory.getAbsolutePath());
		if (!workDirectory.exists()) {
			throw new IllegalStateException("Expected work directory was not created in the filesystem: " + 
					workDirectory.getAbsolutePath());
		}
		
		// Create gather directories
		File gatherDirectory = new File(workDirectory, "Gather");
		File docsDirectory = new File(gatherDirectory, "Docs");
		File tocDirectory = new File(gatherDirectory, "Toc");
		File tocFile = new File(tocDirectory, "toc.xml");
		File docsMetadataDirectory = new File(docsDirectory, "Metadata");
		File docsGuidsFile = new File(gatherDirectory, "docs-guids.txt");		
		
		// Image directories and files
		File imageRootDirectory = new File(gatherDirectory, "Images");
		File imageDynamicDirectory = new File(imageRootDirectory, "Dynamic");
		File imageStaticDirectory = new File(imageRootDirectory, "Static");
		File imageDynamicGuidsFile = new File(gatherDirectory, "dynamic-image-guids.txt");
		File imageStaticManifestFile = new File(gatherDirectory, "static-image-manifest.txt");
		
		File assembleDirectory = new File(workDirectory, "Assemble");
		File assembledTitleDirectory = new File(assembleDirectory, titleId);
		File assembleDocumentsDirectory = new File(assembledTitleDirectory, "documents");
		File assembleAssetsDirectory = new File(assembledTitleDirectory, "assets");
		File assembleArtworkDirectory = new File(assembledTitleDirectory, "artwork");
		
		// Create required directories
		gatherDirectory.mkdir();
		docsDirectory.mkdir();
		docsMetadataDirectory.mkdir();
		tocDirectory.mkdir();
		imageRootDirectory.mkdir();		// Root directory for images
		imageStaticDirectory.mkdir();	// where the copied static images go
		imageDynamicDirectory.mkdir();	// where images from the Image Vertical REST service go
		assembleDirectory.mkdir();
		assembledTitleDirectory.mkdir();
		assembleDocumentsDirectory.mkdir();
		assembleAssetsDirectory.mkdir();
		assembleArtworkDirectory.mkdir();
		
		// Create format directories
		File formatDirectory = new File(workDirectory, "Format");
		File transformedDirectory = new File(formatDirectory, "Transformed");
		File postTransformDirectory = new File(formatDirectory, "PostTransform");
		File htmlWrapperDirectory = new File(formatDirectory, "HTMLWrapper");
		formatDirectory.mkdir();
		transformedDirectory.mkdir();
		postTransformDirectory.mkdir();
		htmlWrapperDirectory.mkdir();
		File intermediateTitleXMLFile = new File(formatDirectory, "IntermediateTitle.xml");
		
		//File htmlDirectory = new File(formatDirectory, "HTML");
		
		// Create the absolute path to the final e-book artifact - a GNU ZIP file
		// "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
		File ebookFile = new File(workDirectory, titleId + BOOK_FILE_TYPE_SUFFIX);
		File titleXmlFile = new File(assembledTitleDirectory, "title.xml");
		
		// File containing image GUID's one per line
		
		// Place data on the JobExecutionContext for use in later steps
		jobExecutionContext.putString(
				JobExecutionKey.EBOOK_DIRECTORY, assembledTitleDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.EBOOK_FILE, ebookFile.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.GATHER_DIR, gatherDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.GATHER_DOCS_DIR, docsDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.GATHER_DOCS_METADATA_DIR, docsMetadataDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.GATHER_TOC_DIR, tocDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.GATHER_TOC_FILE, tocFile.getAbsolutePath());
		
		jobExecutionContext.putString(
				JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE, docsGuidsFile.getAbsolutePath());		
		
		// Images - static and dynamic directories and files
		jobExecutionContext.putString(
				JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR, imageDynamicDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.IMAGE_DYNAMIC_GUIDS_FILE, imageDynamicGuidsFile.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.IMAGE_ROOT_DIR, imageRootDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.IMAGE_STATIC_DEST_DIR, imageStaticDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.IMAGE_STATIC_MANIFEST_FILE, imageStaticManifestFile.getAbsolutePath());
		
		jobExecutionContext.putString(
				JobExecutionKey.FORMAT_TRANSFORMED_DIR, transformedDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.FORMAT_POST_TRANSFORM_DIR, postTransformDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.FORMAT_HTML_WRAPPER_DIR, htmlWrapperDirectory.getAbsolutePath());
		
		jobExecutionContext.putString(
				JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR, assembleDocumentsDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.ASSEMBLE_ASSETS_DIR, assembleAssetsDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.ASSEMBLE_ARTWORK_DIR, assembleArtworkDirectory.getAbsolutePath());
		jobExecutionContext.putString(
				JobExecutionKey.INTERMEDIATE_TITLE_XML_FILE, intermediateTitleXMLFile.getAbsolutePath());
		jobExecutionContext.putString(JobExecutionKey.TITLE_XML_FILE, titleXmlFile.getAbsolutePath());

		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setRootWorkDirectory(File rootDir) {
		this.rootWorkDirectory = rootDir;
	}
}
