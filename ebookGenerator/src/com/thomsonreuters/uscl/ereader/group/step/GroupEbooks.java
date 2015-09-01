package com.thomsonreuters.uscl.ereader.group.step;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.format.FormatConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class GroupEbooks extends AbstractSbTasklet {

	private static final Logger LOG = Logger.getLogger(GroupEbooks.class);
	private ProviewClient proviewClient;
	private PublishingStatsService publishingStatsService;
	private GroupService groupService;
	// retry parameters
    private int baseRetryInterval = 10000; // in ms    
	private int maxNumberOfRetries = 3;
    // used to compute a multiplier for successive retries
    private int retryIntervalMultiplierBase = 5;
    // hard limit on the computed interval
    private int maxRetryIntervalLimit = 300 * 1000; // 5 minutes
	
	
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
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);		

		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String versionNumber = FormatConstants.VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
		
		long startTime = System.currentTimeMillis();
		LOG.debug("Publishing eBook [" + fullyQualifiedTitleId+ "] to Proview.");
		String publishStatus =  "Completed";
		
		DocumentTypeCode documentTypeCode = bookDefinition.getDocumentTypeCodes();
		Long groupVersion = new Long(1);

		try 
		{	
			if(bookDefinition.isSplitBook()){
				
				String majorVersion = versionNumber;
				if(StringUtils.contains(versionNumber, '.')){
					majorVersion = StringUtils.substringBefore(versionNumber, ".");
				}
				Long lastGroupVersionSubmitted = getGroupVersionByBookDefinition(bookDefinition.getEbookDefinitionId());
				GroupDefinition groupDefinition = new GroupDefinition();
				String groupId = groupService.getGroupId(bookDefinition);
				String groupName = groupService.getGroupName(documentTypeCode, bookDefinition.getEbookNames());
				List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
				
				if (lastGroupVersionSubmitted != null){					
					String proviewResponse = getGroupInfoByVersion(groupId, lastGroupVersionSubmitted);
					//Always create new subgroup for the latest major version ;
					subGroupInfoList.add(groupService.getSubGroupInfo(jobInstance,majorVersion));
					//Add subgroups from Proview previous versions
					if(proviewResponse != null){
						subGroupInfoList.addAll(groupService.getSubGroupsFromProviewResponse(proviewResponse, majorVersion));
					}
					groupDefinition.setGroupId(groupId);
					groupDefinition.setHeadTitle(fullyQualifiedTitleId+"/"+majorVersion);
					groupDefinition.setName(groupName);
					groupDefinition.setType("standard");
					//Increment the group version if it exists in Proview.
					groupVersion = lastGroupVersionSubmitted +1;
					groupDefinition.setGroupVersion(FormatConstants.VERSION_NUMBER_PREFIX+groupVersion.toString());
					groupDefinition.setSubGroupInfoList(subGroupInfoList);
				}					
				else{
						
					groupDefinition.setGroupId(groupId);
					groupDefinition.setGroupVersion(FormatConstants.VERSION_NUMBER_PREFIX+String.valueOf(groupVersion));
					groupDefinition.setHeadTitle(fullyQualifiedTitleId+"/"+majorVersion);
					groupDefinition.setName(groupName);
					groupDefinition.setType("standard");
					subGroupInfoList.add(groupService.getSubGroupInfo(jobInstance,majorVersion));
					groupDefinition.setSubGroupInfoList(subGroupInfoList);
					
				}	
				//Create group with retry logic
				/**
				 * Creategroup should be the last line before catching exception
				 * so it can be assumed that group was not created if an exception is thrown before this step
				 * it is safe to insert group version as null in publishing stats
				 */
				createGroup(groupDefinition);
				
				
			}
		} 
		catch (Exception e) 
		{
			groupVersion = null;
			publishStatus =  "Failed";
			throw(e);
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
	
	public void createGroup(GroupDefinition groupDefinition) throws ProviewException {
		boolean retryRequest = true;

		int retryCount = 0;
		String errorMsg = "";
		do {
			try {
				proviewClient.createGroup(groupDefinition);
				retryRequest = false;
			} catch (ProviewRuntimeException ex) {
				errorMsg = ex.getMessage();
				if (errorMsg.startsWith("400") && errorMsg.contains("This Title does not exist")){
					// retry a retriable request
					int computedRetryInterval = computeRetryInterval(retryCount);

					LOG.warn("Retriable status received: waiting " + computedRetryInterval + "ms (retryCount: "
							+ retryCount +")");

					retryRequest = true;
					retryCount++;

					try {
						Thread.sleep(computedRetryInterval);
					} catch (InterruptedException e) {
						LOG.error("InterruptedException during HTTP retry", e);
					};
				}else {
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
	
	/**
     * Compute an interval that grows somewhat randomly with each retry attempt.
     * 
     * @param retryCount
     * @return
     */
    protected int computeRetryInterval(int retryCount) {
        int randomnessMultiplier = (int) Math.pow(retryIntervalMultiplierBase, Math.max(0, retryCount - 1));
        int randomnessInterval = (int) ((Math.random() - 0.5) * 2 * getBaseRetryInterval() * randomnessMultiplier);
        int multiplier = (int) Math.pow(retryIntervalMultiplierBase, retryCount);
        int interval = Math.max(getBaseRetryInterval(), (getBaseRetryInterval() * multiplier) + randomnessInterval);
        return Math.min(interval, maxRetryIntervalLimit);
    }
	
	public int getMaxNumberOfRetries() {
        return this.maxNumberOfRetries;
    }	
	
	public int getBaseRetryInterval() {
		return baseRetryInterval;
	}

	public void setBaseRetryInterval(int baseRetryInterval) {
		this.baseRetryInterval = baseRetryInterval;
	}
	
	public Long getGroupVersionByBookDefinition(Long bookDefinitionId){
		return publishingStatsService.getMaxGroupVersionById(bookDefinitionId);
	}
	
	public String getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException {
		String response = null;
		do {
			try {
				response = proviewClient.getProviewGroupInfo(groupId, FormatConstants.VERSION_NUMBER_PREFIX
						+ groupVersion.toString());
				return response;
			} catch (ProviewRuntimeException ex) {
				if (ex.getMessage().startsWith("400") && ex.toString().contains("No such group id and version exist")) {
					// go down the version by one if the current version is
					// deleted in Proview
					groupVersion = groupVersion - 1;
				} else {
					throw new ProviewRuntimeException(ex.getMessage());
				}
			}
		} while (groupVersion > 0);
		return response;
	}
	
	public void getGroupDefinition(InputStream is){
		
	}
	
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}

