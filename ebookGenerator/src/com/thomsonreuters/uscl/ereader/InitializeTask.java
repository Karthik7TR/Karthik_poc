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

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for
 * use in later steps.
 * This includes various file system path calculations based on the JobParameters used to run the job.
 *
 */
public class InitializeTask extends AbstractSbTasklet {
	private static final Logger log = Logger.getLogger(InitializeTask.class);
	static final String BOOK_FILE_TYPE_SUFFIX = ".gz";
	private static final String DE_DUPPING_ANCHOR_FILE = "eBG_deDupping_anchors.txt";

	private File rootWorkDirectory; // "/nas/ebookbuilder/data"
	private String environmentName;
	private PublishingStatsService publishingStatsService;
	private EBookAuditService eBookAuditService;
	private BookDefinitionService bookDefnService;
	
	@Override
	public ExitStatus executeStep(StepContribution contribution,
								  ChunkContext chunkContext) throws Exception 
	{
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		JobInstance jobInstance = jobExecution.getJobInstance();
		JobParameters jobParams = jobInstance.getJobParameters();
		String publishStatus = "Failed";
		try 
		{
						
			// get ebookDefinition
			BookDefinition bookDefinition = bookDefnService.findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID));
			String titleId = bookDefinition.getTitleId();
			
			// Place data on the JobExecutionContext for use in later steps
			jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITON, bookDefinition);
	
			log.info("titleId (Fully Qualified): " + bookDefinition.getTitleId());
			log.info("hostname: " + jobParams.getString(JobParameterKey.HOST_NAME));
			log.info("TOC Collection: " + bookDefinition.getTocCollectionName());
			log.info("TOC Root Guid: " + bookDefinition.getRootTocGuid());
			log.info("NORT Domain: " + bookDefinition.getNortDomain());
			log.info("NORT Filter: " + bookDefinition.getNortFilterView());		
	
			// Create the work directory for the ebook and create the physical directory in the filesystem
			// "<yyyyMMdd>/<titleId>/<jobInstanceId>"
			// Sample: "/apps/eBookBuilder/prod/data/20120131/FRCP/356"
			String dynamicPath = String.format("%s/%s/%s/%s/%d", environmentName, CoreConstants.DATA_DIR,
					new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date()), titleId, jobInstance.getId());
			File workDirectory = new File(rootWorkDirectory, dynamicPath);
			workDirectory.mkdirs();
			
			log.info("workDirectory: " + workDirectory.getAbsolutePath());
			
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
			File docsMissingGuidsFile = new File(gatherDirectory, "Docs_doc_missing_guids.txt");
			
			// Image directories and files
			File imageRootDirectory = new File(gatherDirectory, "Images");
			File imageDynamicDirectory = new File(imageRootDirectory, "Dynamic");
			File imageStaticDirectory = new File(imageRootDirectory, "Static");
			File imageDynamicGuidsFile = new File(gatherDirectory, "dynamic-image-guids.txt");
			File imageStaticManifestFile = new File(gatherDirectory, "static-image-manifest.txt");
			File imageMissingGuidsFile = new File(imageRootDirectory, "missing_image_guids.txt");
			
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
			File formatImageMetadataDirectory = new File(formatDirectory, "ImageMetadata");
			File postTransformDirectory = new File(formatDirectory, "PostTransform");
			File createdLinksTransformDirectory = new File(formatDirectory, "CreatedLinksTransform");
			File fixedTransformDirectory = new File(formatDirectory, "FixedTransform");
			File htmlWrapperDirectory = new File(formatDirectory, "HTMLWrapper");
			File frontMatterHTMLDiretory = new File(formatDirectory, "FrontMatterHTML");
			formatDirectory.mkdir();
			transformedDirectory.mkdir();
			formatImageMetadataDirectory.mkdir();
			postTransformDirectory.mkdir();
			createdLinksTransformDirectory.mkdir();
			fixedTransformDirectory.mkdir();
			htmlWrapperDirectory.mkdir();
			frontMatterHTMLDiretory.mkdir();
			File imageToDocumentManifestFile = new File(formatDirectory, "doc-to-image-manifest.txt");
			
			File deDuppingAnchorFile = new File(formatDirectory, DE_DUPPING_ANCHOR_FILE);
			
			if (deDuppingAnchorFile.exists())
			{
				deDuppingAnchorFile.delete();			
			}
			
			deDuppingAnchorFile.createNewFile();
			
			//File htmlDirectory = new File(formatDirectory, "HTML");
			
			// Create the absolute path to the final e-book artifact - a GNU ZIP file
			// "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
			File ebookFile = new File(workDirectory, titleId + BOOK_FILE_TYPE_SUFFIX);
			File titleXmlFile = new File(assembledTitleDirectory, "title.xml");
			
			jobExecutionContext.putString(
					JobExecutionKey.DEDUPPING_FILE, deDuppingAnchorFile.getAbsolutePath());
			
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
			
			jobExecutionContext.putString(
					JobExecutionKey.DOCS_MISSING_GUIDS_FILE, docsMissingGuidsFile.getAbsolutePath());
			
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
					JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE, imageToDocumentManifestFile.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.IMAGE_MISSING_GUIDS_FILE, imageMissingGuidsFile.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_TRANSFORMED_DIR, transformedDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_IMAGE_METADATA_DIR, formatImageMetadataDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_POST_TRANSFORM_DIR, postTransformDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_CREATED_DIR, createdLinksTransformDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR, fixedTransformDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_HTML_WRAPPER_DIR, htmlWrapperDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR, frontMatterHTMLDiretory.getAbsolutePath());
			
			jobExecutionContext.putString(
					JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR, assembleDocumentsDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.ASSEMBLE_ASSETS_DIR, assembleAssetsDirectory.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.ASSEMBLE_ARTWORK_DIR, assembleArtworkDirectory.getAbsolutePath());
			jobExecutionContext.putString(JobExecutionKey.TITLE_XML_FILE, titleXmlFile.getAbsolutePath());
	
			log.info("Image Service URL: " + System.getProperty("image.vertical.context.url") );
			log.info("Proview Domain URL: " + System.getProperty("proview.domain") );
			publishStatus="Completed";
			
		}
		catch (Exception e)
		{
			throw (e);
		}
		finally 
		{
            PublishingStats pubStats = new PublishingStats();
			
			// TODO: replace with call to job queue?
			Date rightNow = new Date();
			Long ebookDefId = jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID);
			pubStats.setEbookDefId(ebookDefId);
			Long auditId = eBookAuditService.findEbookAuditByEbookDefId(ebookDefId);
			pubStats.setAuditId(auditId);
			pubStats.setBookVersionSubmitted(jobParams.getString(JobParameterKey.BOOK_VERSION_SUBMITTED));
			pubStats.setJobHostName(jobParams.getString(JobParameterKey.HOST_NAME));
			pubStats.setJobInstanceId(Long.valueOf(jobInstance.getId().toString()));
			pubStats.setJobSubmitterName(jobParams.getString(JobParameterKey.USER_NAME));
			pubStats.setJobSubmitTimestamp(jobParams.getDate(JobParameterKey.TIMESTAMP)); 
			pubStats.setPublishStatus("InitializeTask: " + publishStatus);
			pubStats.setPublishStartTimestamp(rightNow); 
			pubStats.setLastUpdated(rightNow);
			
			publishingStatsService.savePublishingStats(pubStats);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setRootWorkDirectory(File rootDir) {
		this.rootWorkDirectory = rootDir;
	}
	@Required
	public void setEnvironmentName(String envName) {
		this.environmentName = envName;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	@Required
	public void setEbookAuditService(EBookAuditService eBookAuditService) {
		this.eBookAuditService = eBookAuditService;
	}
	@Required
	public void setBookDefnService(BookDefinitionService bookDefnService) {
		this.bookDefnService = bookDefnService;
	}
}
