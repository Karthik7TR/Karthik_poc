/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.XMLImageParserService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step generates the Image GUID list file that is used to retrieve the images referenced
 * by any documents within this eBook.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class ParseImageGUIDList extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = LogManager.getLogger(ParseImageGUIDList.class);
	private PublishingStatsService publishingStatsService;
	
	private XMLImageParserService xmlImageParserService;

	public void setxmlImageParserService(XMLImageParserService xmlImageParserService) 
	{
		this.xmlImageParserService = xmlImageParserService;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
				
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		
		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);
		String imgGuidListFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_GUIDS_FILE);
		String imgDocMapFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE);

		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
		
		File xmlDir = new File(xmlDirectory);
		File imgGuidList = new File(imgGuidListFile);
		File imgDocMap = new File(imgDocMapFile);
		
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobInstance);
	    String publishingStatus = "Completed";
	    
		try {
			long startTime = System.currentTimeMillis();
			int numDocsParsed = xmlImageParserService.generateImageList(xmlDir, imgGuidList, imgDocMap);
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			
			if (numDocsParsed != numDocsInTOC)
			{
				String message = "The number of documents wrapped by the HTMLWrapper Service did " +
						"not match the number of documents retrieved from the eBook TOC. Wrapped " + 
						numDocsParsed + " documents while the eBook TOC had " + numDocsInTOC + " documents.";
				LOG.error(message);
				throw new EBookFormatException(message);
			}
			
			LOG.debug("Generate Image Guid list in " + elapsedTime + " milliseconds from " + 
					+ numDocsParsed + " xml documents.");
			
		} catch (Exception e) {
			publishingStatus = "Failed";
			throw e;
		} finally {
			jobstats.setPublishStatus("parseImageGuids : " + publishingStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
