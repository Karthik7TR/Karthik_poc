/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.step;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
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

	private static final Logger LOG = Logger.getLogger(DeliverToProview.class);
	private static final String VERSION_NUMBER_PREFIX = "v";
	private ProviewClient proviewClient;
	private PublishingStatsService publishingStatsService;
	private DocMetadataService docMetadataService;

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
						throw new IOException("eBook must not be null and should exists.");
					}
					proviewClient.publishTitle(fullyQualifiedTitleId, versionNumber, eBook);
				}
			}
			else{
				proviewClient.publishTitle(fullyQualifiedTitleId, versionNumber, eBook);
			}
		} 
		catch (Exception e) 
		{
			publishStatus =  "Failed";
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
	
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
