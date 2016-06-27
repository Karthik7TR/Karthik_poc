package com.thomsonreuters.uscl.ereader.group.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.format.FormatConstants;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

public class GroupEbooks extends AbstractSbTasklet {

	private static final Logger LOG = Logger.getLogger(GroupEbooks.class);
	
	private PublishingStatsService publishingStatsService;
	private GroupService groupService;

	private int maxNumberOfRetries = 3;
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
	
	
	
	public GroupService getGroupService() {
		return groupService;
	}

	@Required
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParameters = getJobParameters(chunkContext);
		
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);		

		String versionNumber = FormatConstants.VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		long startTime = System.currentTimeMillis();
		LOG.debug("Publishing eBook [" + fullyQualifiedTitleId+ "] to Proview.");
		String publishStatus =  "Completed";
		
		
		Long groupVersion = new Long(0);

		try 
		{	
						
			if (!StringUtils.isEmpty(bookDefinition.getGroupName())) {
				List<String> splitTitles = null;
				if (bookDefinition.isSplitBook()) {
					String splitNodeInfoFile = getRequiredStringProperty(jobExecutionContext,
							JobExecutionKey.SPLIT_NODE_INFO_FILE);
					splitTitles = readSplitNodeInforFile(splitNodeInfoFile, fullyQualifiedTitleId);
				}				
				GroupDefinition groupDefinition = groupService.createGroupDefinition(bookDefinition, versionNumber, splitTitles);
				GroupDefinition previousGroupDefinition = groupService.getLastGroup(bookDefinition);
				if(!groupDefinition.isSimilarGroup(previousGroupDefinition)) {
					createGroupWithRetry(groupDefinition);
					groupVersion = groupDefinition.getGroupVersion();
				}
			}
			else if (publishingStatsService.hasBeenGrouped(bookDefinition.getEbookDefinitionId())){
				groupService.removeAllPreviousGroups(bookDefinition);
			}
		} 
		catch (Exception e) 
		{
			groupVersion = null;
			//For pilot books we will not fail the job. 
			if(bookDefinition.getPilotBooks() != null && bookDefinition.getPilotBooks().size() > 0){
				LOG.error("GroupEbooks failed : " + e.getStackTrace());
				sendEmailNotification(chunkContext);
			}
			else{
				publishStatus =  "Failed";
				throw(e);
			}
		}
		finally
		{
		    PublishingStats jobstats = new PublishingStats();
		    jobstats.setJobInstanceId(jobInstance);
		    jobstats.setPublishStatus("GroupEBook : " + publishStatus);
		    jobstats.setGroupVersion(groupVersion);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GROUPEBOOK);
		}
		
		long processingTime = System.currentTimeMillis() - startTime;
		LOG.debug("Publishing complete. Time elapsed: " + processingTime + "ms");

      
		return ExitStatus.COMPLETED;
	}
	
	protected void sendEmailNotification(ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);

		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();

		long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getId();
		long jobExecutionId = stepExecution.getJobExecutionId();

		String username = jobParams.getString(JobParameterKey.USER_NAME);
		Collection<InternetAddress> recipients = coreService.getEmailRecipientsByUsername(username);
		
		String environment  = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(EBOOK_DEFINITON);
        
		String subject = "eBook Failed to create group - " + bookDefinition.getFullyQualifiedTitleId();
        String body =  String.format("%s\t\nProview Display Name: %s \t\nTitle ID: %s \t\nJob Instance ID: %d \t\nJob Execution ID: %d \t\nEnvironment: %s \t\n",
        					subject, bookDefinition.getProviewDisplayName(), bookDefinition.getFullyQualifiedTitleId(),
        					jobInstanceId, jobExecutionId, environment);      

        EmailNotification.send(recipients, subject, body);
	}
	
	protected void createGroupWithRetry(GroupDefinition groupDefinition) {
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
				groupService.createGroup(groupDefinition);
				retryRequest = false;
			} catch (ProviewException ex) {
				errorMsg = ex.getMessage();
				if (errorMsg.equalsIgnoreCase(CoreConstants.NO_TITLE_IN_PROVIEW)){
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
				else if (errorMsg.equalsIgnoreCase(CoreConstants.GROUP_AND_VERSION_EXISTS)) {
					retryRequest = true;
					retryCount++;
					Long groupVersion = groupDefinition.getGroupVersion() + 1;
					LOG.warn("Incrementing group version "+groupVersion);
					groupDefinition.setGroupVersion(groupVersion);
				}
				else {
					throw new ProviewRuntimeException(errorMsg);
				}
			}
		} while (retryRequest && retryCount < getMaxNumberOfRetries());
		if (retryRequest && retryCount == getMaxNumberOfRetries()) {
			throw new ProviewRuntimeException(
					"Tried 3 times to create group and not succeeded. Proview might be down "
					+ "or still in the process of loading parts of the book. Please try again later. ");
		}

	}
	
	
	
	/*
	 * Reads the file at Format\splitEbook\splitNodeInfo.txt and gets the split titles
	 */
	protected List<String> readSplitNodeInforFile(final String splitNodeInfoFilePath, String fullyQualifiedTitleId) {
		
		 File splitNodeInfoFile = new File(splitNodeInfoFilePath);
		List<String> splitTitles = new ArrayList<String>();
		splitTitles.add(fullyQualifiedTitleId);
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(splitNodeInfoFile));

			while ((line = stream.readLine()) != null) {
				
				String[] splitted = line.split("\\|");	
				splitTitles.add(splitted[1]);

			}
		} catch (IOException iox) {
			throw new RuntimeException("Unable to find File : " + splitNodeInfoFile.getAbsolutePath() + " " + iox);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException("An IOException occurred while closing a file ", e);
				}
			}
		}
		return splitTitles;
	}
	
	
    
    
	
	
	
	public Long getGroupVersionByBookDefinition(Long bookDefinitionId){
		return publishingStatsService.getMaxGroupVersionById(bookDefinitionId);
	}
	
	
	
	public void getGroupDefinition(InputStream is){
		
	}	
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}

