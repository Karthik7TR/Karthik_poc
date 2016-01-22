/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jms.IllegalStateException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.time.DateFormatUtils;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter.NortFilenameFilter;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter.NortNodeFilter;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusNortFileParser;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.service.NovusNortFileService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * Create TOC file from NORT files created by Codes Workbench
 * 
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class GenerateTocTask  extends AbstractSbTasklet 
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GenerateTocTask.class);
	private PublishingStatsService publishingStatsService;
	
	private NovusNortFileService novusNortFileService;
	
	private BookDefinitionService bookDefinitionService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		GatherResponse gatherResponse = null;
		String publishStatus = "Completed";
			
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		Long jobInstanceId = jobInstance.getId();
		
		File rootCodesWorkbenchLandingStrip = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.CODES_WORKBENCH_ROOT_LANDING_STRIP_DIR));
		File tocFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
		String tocDir = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_DIR);
		
		String username = jobParams.getString(JobParameterKey.USER_NAME);
		String envName = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
		Collection<InternetAddress> emailRecipients = coreService.getEmailRecipientsByUsername(username);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		
		String cwbBookName = bookDefinition.getCwbBookName();
		List<NortFileLocation> nortFileLocations = (ArrayList<NortFileLocation>) bookDefinition.getNortFileLocations();
		List<ExcludeDocument> excludeDocuments = (ArrayList<ExcludeDocument>) bookDefinition.getExcludeDocuments();
		List<RenameTocEntry> renameTocEntries = (ArrayList<RenameTocEntry>) bookDefinition.getRenameTocEntries();
		
		Date cutoffDate = null;
		
		if (bookDefinition.getPublishCutoffDate() != null) 
		{
			cutoffDate = (Date)(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateFormatUtils.ISO_DATETIME_FORMAT
					.format(bookDefinition.getPublishCutoffDate()).replace("T", " ")));			
		} 
		else 
		{
			cutoffDate = new Date();
		}
		
		PublishingStats jobstats = new PublishingStats();
        try 
        {

        	List<RelationshipNode> rootNodes = new ArrayList<RelationshipNode>();
        	// Get root nodes from NORT files
        	for(NortFileLocation location : nortFileLocations) 
        	{
        		String contentPath = String.format("%s/%s", cwbBookName, location.getLocationName());
        		File contentDirectory = new File(rootCodesWorkbenchLandingStrip, contentPath);
        		if (!contentDirectory.exists()) 
        		{
					throw new IllegalStateException("Expected Codes Workbench content directory does not exist: " + 
							contentDirectory.getAbsolutePath());
				}

        		File[] nortFiles = contentDirectory.listFiles(new NortFilenameFilter());
        		if(nortFiles.length == 0) 
        		{
        			throw new IllegalStateException("Expected Codes Workbench nort file but none exists: " + 
							contentDirectory.getAbsolutePath());
        		} 
        		else if(nortFiles.length > 1) 
        		{
        			throw new IllegalStateException("Too many Codes Workbench nort files exists: " + 
							contentDirectory.getAbsolutePath());
        		}
        		
        		for (File nortFile: nortFiles)
        		{
        			NovusNortFileParser parser = new NovusNortFileParser(cutoffDate);
        			rootNodes.addAll(parser.parseDocument(nortFile));
        		}
        	}
        	
        	// Remove empty nodes
        	NortNodeFilter nodeFilter = new NortNodeFilter(rootNodes);
        	List<RelationshipNode> removedNodes = nodeFilter.filterEmptyNodes();
        	
        	List<RelationshipNode> wlNotificationNodeList = nodeFilter.getWLNotificationNodes();
        	
        	if ( (removedNodes != null && removedNodes.size() > 0) || wlNotificationNodeList.size() > 0)
    		{  
        		int wlNotificationNodeSize = wlNotificationNodeList.size();
        		int removedNodeSize = removedNodes.size();
        		if (removedNodeSize > 0){
        			wlNotificationNodeList.addAll(removedNodes);
        		}
    		// Send notification for empty nodes.
        		String titleId = bookDefinition.getTitleId();
    			File emptyNodeTargetListFile = new File(tocDir, titleId +"_" + jobInstanceId +"_emptyNodeFile.csv");
    			writeEmtpyNodeReport(titleId, jobInstanceId, envName, wlNotificationNodeList, emptyNodeTargetListFile, emailRecipients, wlNotificationNodeSize, removedNodeSize);
    			
    		}
    		
        	boolean deletePreviousSplits = false;
        	
			if(bookDefinition.getSourceType().equals(SourceType.FILE) && rootNodes.size() > 0)
			{
				List<String> splitTocGuidList = new ArrayList<String>();
				List<SplitDocument> splitDocuments = null;
				
				if (bookDefinition.isSplitBook()) {

					splitDocuments = bookDefinition.getSplitDocumentsAsList();					

					for (SplitDocument splitDocument : splitDocuments) {
						splitTocGuidList.add(splitDocument.getTocGuid());
					}
				}
	        	
				Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
				gatherResponse = novusNortFileService.findTableOfContents(rootNodes, tocFile, cutoffDate, 
						excludeDocuments, renameTocEntries, splitTocGuidList, thresholdValue.intValue());
				if (gatherResponse.getSplitTocGuidList() != null && gatherResponse.getSplitTocGuidList().size() > 0){
					if (bookDefinition.isSplitTypeAuto()) {
						deletePreviousSplits = true;
					} 
					else {
						StringBuffer errorMessageBuffer = new StringBuffer("TOC/NORT guid provided for the split does not exist. ") ;
						int i = 1;
						for (String tocGuid : gatherResponse.getSplitTocGuidList()){
							if (i == gatherResponse.getSplitTocGuidList().size()){
								errorMessageBuffer.append(tocGuid);
							}
							else{
								errorMessageBuffer.append(tocGuid+", ");
							}
							i++;
						}
						LOG.error(errorMessageBuffer);
						gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, errorMessageBuffer.toString(), 0,0,0,"GENERATE TOC STEP FAILED NONEXISTENT TOC/NORT GUID");
					}
				}	
				
				//Check for duplicate tocGuids for manual splits
				if (splitDocuments !=null){
					List<String> splitTocGuids = new ArrayList<String>();
					for (SplitDocument splitDocument : splitDocuments) {
						splitTocGuids.add(splitDocument.getTocGuid());
					}
					duplicateTocCheck(splitTocGuids, gatherResponse.getDuplicateTocGuids());
				}
			}
			else
			{
				String errorMessage = "Codes Workbench was not the source type." ;
				LOG.error(errorMessage);
				gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, errorMessage, 0,0,0,"GENERATE TOC STEP FAILED INCORRECT SOURCE TYPE");
			}
			jobstats.setGatherTocDocCount(gatherResponse.getDocCount());
            jobstats.setGatherTocNodeCount(gatherResponse.getNodeCount());
            jobstats.setGatherTocSkippedCount(gatherResponse.getSkipCount());
            jobstats.setGatherTocRetryCount(gatherResponse.getRetryCount());
            
            // TODO: update doc count used in Job Execution Context
    		
    		LOG.debug(gatherResponse);
    		if (gatherResponse.getErrorCode() != 0 ) 
    		{
    			GatherException gatherException = new GatherException(
    					gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
    			throw gatherException;
    		}
    		if(bookDefinition.isSplitBook() && bookDefinition.isSplitTypeAuto()){
    			Integer tocNodeCount = gatherResponse.getNodeCount();
				Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
				if (tocNodeCount < thresholdValue){
	    			StringBuffer eMessage = new StringBuffer("Cannot split the book into parts as node count "+tocNodeCount+" is less than threshold value "+thresholdValue);
	    			throw new  RuntimeException(eMessage.toString());
				}
				else{
					if(novusNortFileService.isFindSplitsAgain() || deletePreviousSplits){
						bookDefinitionService.deleteSplitDocuments(bookDefinition.getEbookDefinitionId());
					}
				}
				//Check for duplicate tocGuids for Auto splits
				duplicateTocCheck(null, gatherResponse.getDuplicateTocGuids());
				
    		}
        }
        catch (Exception e)
        {
        	publishStatus = "Failed";
        	throw (e);
        }
        finally 
        {
        	jobstats.setJobInstanceId(jobInstanceId);
            jobstats.setPublishStatus("generateTocFromNortFile : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERATETOC);
        }
		
		return ExitStatus.COMPLETED;
	}
	
	/**
	 * Thows Excpetion if duplicate Toc exists for split book
	 * @param splitGuidList
	 * @param dupGuidList
	 * @throws Exception
	 */
	public void duplicateTocCheck(List<String> splitGuidList, List<String> dupGuidList) throws Exception {

		if (dupGuidList != null && dupGuidList.size() > 0) {

			StringBuffer eMessage = new StringBuffer("Duplicate TOC guids Found. Cannot split the book. ");

			if (splitGuidList == null) {
				for (String dupTocGuid : dupGuidList) {
					eMessage.append(dupTocGuid + " ");
				}
				throw new RuntimeException(eMessage.toString());
			} else {
				boolean dupFound = false;
				for (String dupTocGuid : dupGuidList) {
					if (splitGuidList.contains(dupTocGuid)) {
						eMessage.append(dupTocGuid + " ");
						dupFound = true;
					}
				}
				if (dupFound) {
					throw new RuntimeException(eMessage.toString());
				}
			}
		}
	}
	
		
	
	protected void writeEmtpyNodeReport(String title, Long jobInstanceId, String envName, List<RelationshipNode> notificationNodes,
			File removedNodesListFile, Collection<InternetAddress> emailRecipients, int wlNotificationNodeSize, int removedNodeSize) throws EBookFormatException 
	{
		try (BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(removedNodesListFile),"UTF-8")))
		{
			writer.write("NORT GUID (NOTE: First character \"N\" needs to change to \"I\" to find node in CWB), Label, TOC Hierarchy");
			writer.newLine();
			
			int i = 0;
			if (wlNotificationNodeSize > 0) {
				writer.write("Bad  \"no WL pubtag\" list ");
				writer.newLine();
			}

			for (RelationshipNode node : notificationNodes) 
			{	

				if (i == wlNotificationNodeSize && removedNodeSize > 0){
					writer.write("Empty nodes removed list ");
					writer.newLine();
				}
				writer.write(node.getNortGuid());
				writer.write(",");
				writer.write(node.getLabel().replaceAll("\\s|,", " "));
				writer.write(",");
				writer.write(node.getTocHierarchy().replaceAll("\\s|,", " "));
				writer.newLine();
				i++;
			}
			writer.flush();
		} 
		catch (IOException e) 
		{
			String errMessage = "Encountered an IO Exception while processing: "
					+ removedNodesListFile.getAbsolutePath();
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		} 

		String subject = String.format( "eBook user Notification for title \"%s\", job: %s, env: %s",
				title, jobInstanceId.toString(), envName);
		String emailBody = "Attached is the file of empty nodes removed and/or bad \"no WL pubtag\" from the TOC in this book. Format is comma seperated list of NORT guids and label.";
		LOG.debug("Notification email recipients : " + emailRecipients);
		LOG.debug("Notification email subject : " + subject);
		LOG.debug("Notification email body : " + emailBody);
		ArrayList<String> filenames = new ArrayList<String>();
		filenames.add(removedNodesListFile.getAbsolutePath());
		EmailNotification.sendWithAttachment(emailRecipients, subject,
				emailBody, filenames);
	}

	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) 
	{
		this.publishingStatsService = publishingStatsService;
	}
	
	@Required
	public void setNovusNortFileService(NovusNortFileService novusNortFileService) 
	{
		this.novusNortFileService = novusNortFileService;
	}
	
	public BookDefinitionService getBookDefinitionService() {
		return bookDefinitionService;
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}
}
