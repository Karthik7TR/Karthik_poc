/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.step;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This class is responsible for delivering a generated eBook to ProView.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class DeliverToProview extends AbstractSbTasklet {

	private static final Logger LOG = LogManager.getLogger(DeliverToProview.class);
	private static final String VERSION_NUMBER_PREFIX = "v";
	private ProviewClient proviewClient;
	private PublishingStatsService publishingStatsService;
	private DocMetadataService docMetadataService;
	private int maxNumberOfRetries = 2;
	private int sleepTimeInMinutes = 15;
	private int baseSleepTimeInMinutes=2;
	
	public void setSleepTimeInMinutes(int sleepTimeInMinutes) {
		this.sleepTimeInMinutes = sleepTimeInMinutes;
	}
	
	public void setBaseSleepTimeInMinutes(int baseSleepTimeInMinutes) {
		this.baseSleepTimeInMinutes = baseSleepTimeInMinutes;
	}
    
    public int getMaxNumberOfRetries() {
        return this.maxNumberOfRetries;
    }

	public DocMetadataService getDocMetadataService() {
		return docMetadataService;
	}

	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParameters = getJobParameters(chunkContext);
		
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);		

		File eBook = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE));
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String versionNumber = VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
		
		long startTime = System.currentTimeMillis();
		LOG.debug("Publishing eBook [" + fullyQualifiedTitleId+ "] to Proview.");
		String publishStatus =  "Completed";
		List<String> successfullyPublishisedList = new ArrayList<String>();
		try 
		{	
			if(bookDefinition.isSplitBook()){
				File workDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.WORK_DIRECTORY));
				if (workDirectory == null || !workDirectory.isDirectory()) {
					throw new IOException("workDirectory must not be null and must be a directory.");
				}
				List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(jobInstance);
				for (String splitTitleId : splitTitles) {
					
					fullyQualifiedTitleId = splitTitleId;
					splitTitleId = StringUtils.substringAfterLast(splitTitleId, "/");
					eBook = new File(workDirectory, splitTitleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
					if(eBook == null || !eBook.exists()){
						throw new IOException("eBook cannot be null and contact ebook support for furthur analysis.");
					}
					proviewClient.publishTitle(fullyQualifiedTitleId, versionNumber, eBook);					
					successfullyPublishisedList.add(fullyQualifiedTitleId);
				}
			}
			else{
				proviewClient.publishTitle(fullyQualifiedTitleId, versionNumber, eBook);
			}
		} 
		catch (Exception e) 
		{
			publishStatus =  "Failed";
			//Remove parts of the book when there is a failure
			for(String splitTitle : successfullyPublishisedList ){
				removeGroupWithRetry(splitTitle, versionNumber);
			}
			throw(e);
		}
		finally
		{
		    PublishingStats jobstats = new PublishingStats();
		    jobstats.setJobInstanceId(jobInstance);
		    jobstats.setPublishStatus("deliverEBook : " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.FINALPUBLISH);
		}
		
		long processingTime = System.currentTimeMillis() - startTime;
		LOG.debug("Publishing complete. Time elapsed: " + processingTime + "ms");

      
		return ExitStatus.COMPLETED;
	}
	
	protected void removeGroupWithRetry(String splitTitle, String versionNumber) {
		boolean retryRequest = true;

		//Most of the books should finish in two minutes
		try{
		TimeUnit.MINUTES.sleep(baseSleepTimeInMinutes);
		} catch (InterruptedException e) {
			LOG.error("InterruptedException during HTTP retry", e);
		};
		
		int retryCount = 0;
		String errorMsg = "";
		do {
			try {
				String response = proviewClient.removeTitle(splitTitle, versionNumber);
				if(response.contains("200")){
					proviewClient.deleteTitle(splitTitle, versionNumber);
				}
				retryRequest = false;
			} catch (ProviewException ex) {
				errorMsg = ex.getMessage();
				if (errorMsg.equalsIgnoreCase(CoreConstants.TTILE_IN_QUEUE)){
					// retry a retriable request					

					LOG.warn("Retriable status received: waiting " + sleepTimeInMinutes + "minutes (retryCount: "
							+ retryCount +")");

					retryRequest = true;
					retryCount++;

					try {
						TimeUnit.MINUTES.sleep(sleepTimeInMinutes);
					} catch (InterruptedException e) {
						LOG.error("InterruptedException during HTTP retry", e);
					};
				}
				else {
					throw new ProviewRuntimeException(errorMsg);
				}
			}
		} while (retryRequest && retryCount < getMaxNumberOfRetries());
		if (retryRequest && retryCount == getMaxNumberOfRetries()) {
			throw new ProviewRuntimeException(
					"Tried 3 times to remove part of the split title. Proview might be down "
					+ "or still in the process of loading the book. Please try again later. ");
		}

	}
	
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
