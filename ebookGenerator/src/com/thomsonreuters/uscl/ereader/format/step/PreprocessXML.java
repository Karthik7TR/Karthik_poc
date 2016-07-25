/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
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
import com.thomsonreuters.uscl.ereader.format.service.XMLPreprocessService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step transforms the Novus extracted XML documents by adding additional mark ups and content 
 * 
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class PreprocessXML extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = LogManager.getLogger(PreprocessXML.class);
	private XMLPreprocessService preprocessService;
	private PublishingStatsService publishingStatsService;
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);	

		Long jobId = jobInstance.getId();

		String xmlDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);
		String preprocessDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_PREPROCESS_DIR);

		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
		
		File xmlDir = new File(xmlDirectory);
		File preprocessDir = new File(preprocessDirectory);
		
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobId);
		String stepStatus = "Completed";
	    
		try {
			long startTime = System.currentTimeMillis();
			int numDocsTransformed = preprocessService.transformXML(
					xmlDir, preprocessDir, bookDefinition.isFinalStage(), bookDefinition.getDocumentCopyrights(), bookDefinition.getDocumentCurrencies());
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			
			if (numDocsTransformed != numDocsInTOC)
			{
				String message = "The number of documents preprocessed did not match the number " +
						"of documents retrieved from the eBook TOC. Preprocessed " + numDocsTransformed + 
						" documents while the eBook TOC had " + numDocsInTOC + " documents.";
				LOG.error(message);
				throw new EBookFormatException(message);
			}
			
			LOG.debug("Preprocessed " + numDocsTransformed + " XML files in " + elapsedTime + " milliseconds");
		} catch (Exception e) {
			stepStatus= "Failed";
			throw e;
		} finally {
			jobstats.setPublishStatus("formatPreprocessXML : " + stepStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	
	@Required
	public void setPreprocessService(XMLPreprocessService preprocessService) 
	{
		this.preprocessService = preprocessService;
	}

}
