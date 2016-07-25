/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.util.List;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLTransformerService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step transforms the HTML generated by the transformation process into ProView acceptable HTML.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLPostTransform extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = LogManager.getLogger(HTMLPostTransform.class);
	private HTMLTransformerService transformerService;
	private PublishingStatsService publishingStatsService;

	public void settransformerService(HTMLTransformerService transformerService) 
	{
		this.transformerService = transformerService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
		
		String titleId = bookDefinition.getTitleId();
		Long jobId = jobInstance.getId();
		JobParameters jobParams = getJobParameters(chunkContext);

		String version = jobParams.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
		String transformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORMED_DIR);
		String postTransformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_POST_TRANSFORM_DIR);
		String staticImagePath = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_MANIFEST_FILE);
		String docsGuid = getRequiredStringProperty(jobExecutionContext,JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE);
		
		String deDupping = getRequiredStringProperty(jobExecutionContext,JobExecutionKey.DEDUPPING_FILE);

		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT); 
		
		List<TableViewer> tableViewers = bookDefinition.getTableViewers();
		
		File transformDir = new File(transformDirectory);
		File postTransformDir = new File(postTransformDirectory);
		File staticImgFile = new File(staticImagePath);
		File docsGuidFile = new File(docsGuid);
		File deDuppingFile = new File(deDupping);
		
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobId);
	    String stepStatus = "Completed";
	    
		try {
			long startTime = System.currentTimeMillis();
			int numDocsTransformed = 
					transformerService.transformHTML(transformDir, postTransformDir, staticImgFile, tableViewers, 
							titleId, jobId, null, docsGuidFile, deDuppingFile, bookDefinition.isInsStyleFlag(), 
							bookDefinition.isDelStyleFlag(), bookDefinition.isRemoveEditorNoteHeadFlag(),version);
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			
			if (numDocsTransformed != numDocsInTOC)
			{
				String message = "The number of post transformed documents did not match the number " +
						"of documents retrieved from the eBook TOC. Transformed " + numDocsTransformed + 
						" documents while the eBook TOC had " + numDocsInTOC + " documents.";
				LOG.error(message);
				throw new EBookFormatException(message);
			}
			
			LOG.debug("Transformed " + numDocsTransformed + " HTML files in " + elapsedTime + " milliseconds");
		} catch (Exception e) {
			stepStatus = "Failed";
			throw e;
		} finally {
			jobstats.setPublishStatus("formatHTMLTransformer : " + stepStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}

		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
