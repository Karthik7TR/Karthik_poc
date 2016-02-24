/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.TransformerService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step transforms the Novus extracted XML documents into HTML.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TransformXML extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(TransformXML.class);
	private TransformerService transformerService;
	private PublishingStatsService publishingStatsService;
	

	public void settransformerService(TransformerService transformerService) 
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

		String preprocessDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_PREPROCESS_DIR);
		String metadataDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR);
		String transformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORMED_DIR);
		String imgMetadataDirectory =
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_IMAGE_METADATA_DIR);

		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
		
		File preprocessDir = new File(preprocessDirectory);
		File metadataDir = new File(metadataDirectory);
		File transformDir = new File(transformDirectory);
		File imgMetadataDir = new File(imgMetadataDirectory);
		
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobId);
		String stepStatus = "Completed";
		
		File staticContentDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.STATIC_CONTENT_DIR));			 
	    
		try {
			long startTime = System.currentTimeMillis();
			int numDocsTransformed = transformerService.transformXMLDocuments(
					preprocessDir, metadataDir, imgMetadataDir, transformDir, titleId, jobId, bookDefinition.getIncludeAnnotations(), staticContentDir);
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			
			if (numDocsTransformed != numDocsInTOC)
			{
				String message = "The number of documents transformed did not match the number " +
						"of documents retrieved from the eBook TOC. Transformed " + numDocsTransformed + 
						" documents while the eBook TOC had " + numDocsInTOC + " documents.";
				LOG.error(message);
				throw new EBookFormatException(message);
			}
			
			LOG.debug("Transformed " + numDocsTransformed + " XML files in " + elapsedTime + " milliseconds");
		} catch (Exception e) {
			stepStatus= "Failed";
			throw e;
		} finally {
			jobstats.setPublishStatus("formatTransformXML : " + stepStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

}
