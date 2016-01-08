/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.step;

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
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step persists the Novus Metadata xml to DB.
 * 
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
public class PersistMetadataXMLTask extends AbstractSbTasklet {
	// TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger
			.getLogger(PersistMetadataXMLTask.class);
	private DocMetadataService docMetadataService;
	private PublishingStatsService publishingStatsService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext)  throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);	

		String titleId = bookDefinition.getTitleId();
		Long jobInstanceId = jobInstance.getId();

		String docCollectionName = null; 

		File metaDataDirectory = new File(getRequiredStringProperty(
				jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));

		// TODO: Set value below based on execution context value

		int numDocsMetaDataRun = 0;

		String publishStatus =  "Completed";
		try 
		{
			// recursively read the directory for parsing the document metadata
			if (metaDataDirectory.isDirectory()) 
			{
				File allFiles[] = metaDataDirectory.listFiles();
				for (File metadataFile : allFiles) 
				{
					String fileName =  metadataFile.getName();
					if (fileName.lastIndexOf("-") > -1)
					{
						int startIndex = fileName.indexOf("-")+1;
						docCollectionName = fileName.substring(startIndex, fileName.indexOf("-", startIndex)); 
					}
					docMetadataService.parseAndStoreDocMetadata(titleId,
						        jobInstanceId, docCollectionName, metadataFile);				     
					    numDocsMetaDataRun++;
					
				}
			}
		}
		catch(Exception e)
		{
			publishStatus =  "Failed " + e.getMessage();
			throw(e);
		}
		finally 
		{
			int dupDocCount = docMetadataService.updateProviewFamilyUUIDDedupFields(jobInstanceId);
			updateTitleDocCountStats(jobInstanceId,dupDocCount,publishStatus);
		}
		LOG.debug("Persisted " + numDocsMetaDataRun
					+ " Metadata XML files from "
					+ metaDataDirectory.getAbsolutePath());
		
		return ExitStatus.COMPLETED;
	}

	private void updateTitleDocCountStats(Long jobId,int titleDupDocCount, String status) 
	{
		
		PublishingStats jobstatsFormat = new PublishingStats();
		jobstatsFormat.setJobInstanceId(jobId);
		jobstatsFormat.setTitleDupDocCount(titleDupDocCount);
		jobstatsFormat.setPublishStatus("persistMetadata : " +  status);
		
		LOG.debug("titleDupDocCount ="+titleDupDocCount);

		publishingStatsService.updatePublishingStats(jobstatsFormat, StatsUpdateTypeEnum.TITLEDUPDOCCOUNT);
		
	}

	@Required
	public void setDocMetadataService(DocMetadataService docMetadataSvc) {
		this.docMetadataService = docMetadataSvc;
	}
	
	@Required
	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

}
