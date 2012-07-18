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
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.GenerateImageMetadataBlockService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class GenerateImageMetadataFiles extends AbstractSbTasklet {
	
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GenerateImageMetadataFiles.class);
	
	private GenerateImageMetadataBlockService imgMetaBlockService;
	
	private PublishingStatsService publishingStatsService;

	public void setimgMetaBlockService(GenerateImageMetadataBlockService imgMetaBlockService) 
	{
		this.imgMetaBlockService = imgMetaBlockService;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) 
			throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);

		String docToImgFileName = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE);
		String imgMetadataDirName = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_IMAGE_METADATA_DIR);
		
		Long jobId = jobInstance.getId();
		
		//TODO: Retrieve expected number of document for this eBook from execution context
		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
		
		File docToImgFile = new File(docToImgFileName);
		File imgMetadataDir = new File(imgMetadataDirName);

		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobId);
	    String publishStatus = "Completed";
	    
		try{
			long startTime = System.currentTimeMillis();
			int numImgMetaDocsCreated = imgMetaBlockService.generateImageMetadata(docToImgFile, 
					imgMetadataDir, jobId);
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			
			//TODO: Update to check value is equal to execution context value (numDocsInTOC)
			if (numImgMetaDocsCreated == 0)
			{
				String message = "The number of ImageMetadata documents created did " +
						"not match the number of documents retrieved from the eBook TOC. Created " + 
						numImgMetaDocsCreated + " documents while the eBook TOC had " + 
						numDocsInTOC + " documents.";
				LOG.error(message);
				throw new EBookFormatException(message);
			}
			
			//TODO: Improve metrics
			LOG.debug("Created " + numImgMetaDocsCreated + " ImageMetadata documents in " + 
					elapsedTime + " milliseconds");
			
		} catch (EBookFormatException e) {
			publishStatus = "Failed";
			throw e;
		} finally {
			jobstats.setPublishStatus("generateImageMetadataFiles : " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
