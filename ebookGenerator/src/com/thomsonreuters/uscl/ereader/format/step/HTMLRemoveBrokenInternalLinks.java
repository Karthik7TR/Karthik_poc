/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
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
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLRemoveBrokenInternalLinksService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step transforms the HTML generated by the transformation process into ProView acceptable HTML.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLRemoveBrokenInternalLinks extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(HTMLRemoveBrokenInternalLinks.class);
	private HTMLRemoveBrokenInternalLinksService transformerUnlinkService;
	private PublishingStatsService publishingStatsService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		
		String titleId = bookDefinition.getTitleId();
		Long jobId = jobInstance.getId();

		String transformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_CREATED_DIR);
		String postTransformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR);

		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
		String username = jobParams.getString(JobParameterKey.USER_NAME);
		String envName = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
		Collection<InternetAddress> emailRecipients = coreService.getEmailRecipientsByUsername(username);
				
		File transformDir = new File(transformDirectory);
		File postTransformDir = new File(postTransformDirectory);
		
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobId);
	    String stepStatus = "Completed";
		try {
			long startTime = System.currentTimeMillis();
			int numDocsTransformed = 
					transformerUnlinkService.transformHTML(transformDir, postTransformDir,titleId, jobId, envName, emailRecipients);
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
			jobstats.setPublishStatus("formatHTMLRemoveBrokenInternalLinks : " + stepStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}

	@Required
	public void setTransformerUnlinkService(HTMLRemoveBrokenInternalLinksService transformerUnlinkService) {
		this.transformerUnlinkService = transformerUnlinkService;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
