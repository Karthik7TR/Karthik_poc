package com.thomsonreuters.uscl.ereader.group.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.FormatConstants;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class GroupEbooks extends AbstractSbTasklet {

	private static final Logger LOG = Logger.getLogger(GroupEbooks.class);
	
	private PublishingStatsService publishingStatsService;
	private GroupService groupService;
	
	
	
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
				if(groupDefinition != null) {
					groupService.createGroup(groupDefinition);
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

