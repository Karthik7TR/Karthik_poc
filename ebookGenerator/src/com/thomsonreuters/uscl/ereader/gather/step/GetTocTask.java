/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * 
 * @author U0105927
 *
 */
public class GetTocTask  extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(PersistMetadataXMLTask.class);
	private GatherService gatherService;
	private PublishingStatsService publishingStatsService;
	private BookDefinitionService bookDefinitionService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		GatherResponse gatherResponse = null;
		String publishStatus = "Completed";
			
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		File tocFile = new File(jobExecutionContext.getString(JobExecutionKey.GATHER_TOC_FILE));
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);

		// TOC
		String tocCollectionName = bookDefinition.getTocCollectionName(); 
		String tocRootGuid = bookDefinition.getRootTocGuid();
		// NORT
		String nortDomainName = bookDefinition.getNortDomain();
		String nortExpressionFilter = bookDefinition.getNortFilterView();
		ArrayList<ExcludeDocument> excludeDocuments = (ArrayList<ExcludeDocument>) bookDefinition.getExcludeDocuments();
		ArrayList<RenameTocEntry> renameTocEntries = (ArrayList<RenameTocEntry>) bookDefinition.getRenameTocEntries();
		
		
		List<String> splitTocGuidList = null;		
		if (bookDefinition.isSplitBook()) {

			List<SplitDocument> splitDocuments = bookDefinition.getSplitDocumentsAsList();
			splitTocGuidList = new ArrayList<String>();

			for (SplitDocument splitDocument : splitDocuments) {
				splitTocGuidList.add(splitDocument.getTocGuid());
			}			
		}
		Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();	
		
		Date nortCutoffDate = null;
		
		if (bookDefinition.getPublishCutoffDate() != null) {
			
			nortCutoffDate = (Date)(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateFormatUtils.ISO_DATETIME_FORMAT
					.format(bookDefinition.getPublishCutoffDate()).replace("T", " ")));			
		}
		PublishingStats jobstats = new PublishingStats();
        try 
        {
			if(tocCollectionName != null) // TOC
			{
			GatherTocRequest gatherTocRequest = new GatherTocRequest(tocRootGuid, tocCollectionName, tocFile, excludeDocuments, 
					renameTocEntries, bookDefinition.isFinalStage(), splitTocGuidList, thresholdValue);
			LOG.debug(gatherTocRequest);
		
			gatherResponse = gatherService.getToc(gatherTocRequest);
			}
			else if(nortDomainName != null) // NORT
			{
	//			GatherNortRequest gatherNortRequest = new GatherNortRequest(nortDomainName, nortExpressionFilter, tocFile, nortCutoffDate, jobInstance);
				GatherNortRequest gatherNortRequest = new GatherNortRequest(nortDomainName, nortExpressionFilter, tocFile, 
						nortCutoffDate, excludeDocuments, renameTocEntries, bookDefinition.isFinalStage(), bookDefinition.getUseReloadContent(), splitTocGuidList, thresholdValue);
				LOG.debug(gatherNortRequest);
		
				gatherResponse = gatherService.getNort(gatherNortRequest);
			}
			else
			{
				String errorMessage = "Neither tocCollectionName nor nortDomainName were defined for eBook" ;
				LOG.error(errorMessage);
				gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, errorMessage, 0,0,0,"TOC STEP FAILED UNDEFINED KEY");
			}
			jobstats.setGatherTocDocCount(gatherResponse.getDocCount());
            jobstats.setGatherTocNodeCount(gatherResponse.getNodeCount());
            jobstats.setGatherTocSkippedCount(gatherResponse.getSkipCount());
            jobstats.setGatherTocRetryCount(gatherResponse.getRetryCount());
            
            // TODO: update doc count used in Job Execution Context
    		
    		LOG.debug(gatherResponse);
    		if (gatherResponse.getErrorCode() != 0 ) {
    			GatherException gatherException = new GatherException(
    					gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
    			throw gatherException;
    		}
    		
    		//Error out if the splitdoes not exist
    		if (bookDefinition.isSplitBook() && gatherResponse.getSplitTocGuidList() != null && gatherResponse.getSplitTocGuidList().size() > 0){
				StringBuffer errorMessageBuffer = new StringBuffer("TOC/NORT guid provided for the split does not exist.") ;
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
    		
    		if(bookDefinition.isSplitBook() && bookDefinition.isSplitTypeAuto()){
    			Integer tocNodeCount = gatherResponse.getNodeCount();
				if (tocNodeCount < thresholdValue){
	    			StringBuffer eMessage = new StringBuffer("Cannot split the book into parts as node count"+tocNodeCount+" is less than threshold value "+thresholdValue);
	    			throw new  RuntimeException(eMessage.toString());
				}
				else if(gatherResponse.isFindSplitsAgain()){
						bookDefinitionService.deleteSplitDocuments(bookDefinition.getEbookDefinitionId());
				}
    		}
        }
        catch (Exception e)
        {
        	publishStatus = "Failed";
        	throw (e);
        }
        finally 
        {
        	jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus("getToc : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GATHERTOC);
        }
		
		return ExitStatus.COMPLETED;
	}

	@Required
	public void setGatherService(GatherService gatherService) {
		this.gatherService = gatherService;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	
	@Required
	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}
}
