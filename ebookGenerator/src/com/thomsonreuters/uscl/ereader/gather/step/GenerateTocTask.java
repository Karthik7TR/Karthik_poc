/*
* Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.IllegalStateException;

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
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter.NortFilenameFilter;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusNortFileParser;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.service.NovusNortFileService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Create TOC file from NORT files created by Codes Workbench
 * 
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class GenerateTocTask  extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GenerateTocTask.class);
	private PublishingStatsService publishingStatsService;
	
	private NovusNortFileService novusNortFileService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		GatherResponse gatherResponse = null;
		String publishStatus = "Completed";
			
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		File rootCodesWorkbenchLandingStrip = new File(jobExecutionContext.getString(JobExecutionKey.CODES_WORKBENCH_ROOT_LANDING_STRIP_DIR));
		File tocFile = new File(jobExecutionContext.getString(JobExecutionKey.GATHER_TOC_FILE));
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		
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
					throw new IllegalStateException("Expected Codes Workbench content direction does not exist: " + 
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
        			rootNodes.add(parser.parseDocument(nortFile));
        		}
        	}
        	
			if(bookDefinition.getSourceType().equals(SourceType.FILE) && rootNodes.size() > 0)
			{
				gatherResponse = novusNortFileService.findTableOfContents(rootNodes, tocFile, cutoffDate, 
						excludeDocuments, renameTocEntries);
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
        }
        catch (Exception e)
        {
        	publishStatus = "Failed";
        	throw (e);
        }
        finally 
        {
        	jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus("generateTocFromNortFile : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERATETOC);
        }
		
		return ExitStatus.COMPLETED;
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
}
