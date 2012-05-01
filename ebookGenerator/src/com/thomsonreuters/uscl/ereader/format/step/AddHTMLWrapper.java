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
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLWrapperService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step adds a static predefined HTML header and footer and any ProView specific document wrappers.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class AddHTMLWrapper extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(AddHTMLWrapper.class);
	private HTMLWrapperService htmlWrapperService;
	
	private BookDefinitionService bookDefnService;
	private PublishingStatsService publishingStatsService;
	
	public void sethtmlWrapperService(HTMLWrapperService htmlWrapperService) 
	{
		this.htmlWrapperService = htmlWrapperService;
	}
	
	@Required
	public void setBookDefnService(BookDefinitionService bookDefnService) {
		this.bookDefnService = bookDefnService;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		String postTransformDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR);
		String htmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_HTML_WRAPPER_DIR);
		String docToTocFileName = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE);

		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
					
		JobParameters jobParams = getJobParameters(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		BookDefinition bookDefinition = bookDefnService.findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID));		
		String titleId = bookDefinition.getTitleId();
		boolean keyciteToplineFlag = bookDefinition.getKeyciteToplineFlag();
		
		Long jobId = jobInstance.getId();
		
				
		File postTransformDir = new File(postTransformDirectory);
		File htmlDir = new File(htmlDirectory);
		File docToTocFile = new File(docToTocFileName);
		
		long startTime = System.currentTimeMillis();
		
		String stepStatus =  "All Format Steps Completed";
		int numDocsWrapped = -1;
		try
		{
			numDocsWrapped = htmlWrapperService.addHTMLWrappers(postTransformDir, htmlDir, docToTocFile, titleId, jobId, keyciteToplineFlag);
		}
		catch (EBookFormatException e)
		{
			stepStatus = "Failed in Wrapper Format Step";
			throw e;
		}
		finally
		{
			  PublishingStats jobstats = new PublishingStats();
		      jobstats.setJobInstanceId(jobId);
		      jobstats.setFormatDocCount(numDocsWrapped);
		      jobstats.setPublishStatus(stepStatus);
			  publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.FORMATDOC);
		}
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		if (numDocsWrapped != numDocsInTOC)
		{
			String message = "The number of documents wrapped by the HTMLWrapper Service did " +
					"not match the number of documents retrieved from the eBook TOC. Wrapped " + 
					numDocsWrapped + " documents while the eBook TOC had " + numDocsInTOC + " documents.";
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		
		LOG.debug("Added HTML and ProView document wrappers to " + numDocsWrapped + " documents in " + 
				elapsedTime + " milliseconds");
		
		jobExecutionContext.putString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH, 
				jobExecutionContext.getString(JobExecutionKey.FORMAT_HTML_WRAPPER_DIR));
		
		return ExitStatus.COMPLETED;
	}

}
