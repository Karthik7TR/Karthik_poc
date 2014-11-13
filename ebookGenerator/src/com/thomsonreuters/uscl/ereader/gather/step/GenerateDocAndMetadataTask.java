/*
* Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.service.NovusDocFileService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Splits metadata and docbody from Document XML file from Codes Workbench
 * 
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class GenerateDocAndMetadataTask extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GenerateDocAndMetadataTask.class);
	private DocMetaDataGuidParserService docMetaDataParserService;
	private PublishingStatsService publishingStatsService;
	private NovusDocFileService novusDocFileService;
	
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		String publishStatus = "Completed";
				
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		
		File tocFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
		File docsDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR));
		File docsMetadataDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));
		File docsGuidsFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE));
		File rootCodesWorkbenchLandingStrip = new File(jobExecutionContext.getString(JobExecutionKey.CODES_WORKBENCH_ROOT_LANDING_STRIP_DIR));
		
		String cwbBookName = bookDefinition.getCwbBookName();
		List<NortFileLocation> fileLocations = bookDefinition.getNortFileLocations();
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		PublishingStats jobstatsDoc = new PublishingStats();
		jobstatsDoc.setJobInstanceId(jobInstance);
		
		try 
		{
			docMetaDataParserService.generateDocGuidList(tocFile, docsGuidsFile);
	       
			HashMap<String, Integer> docGuidsMap = readDocGuidsFromTextFile(docsGuidsFile);
			
			GatherResponse gatherResponse = novusDocFileService.fetchDocuments(docGuidsMap, rootCodesWorkbenchLandingStrip,
					cwbBookName, fileLocations, docsDir, docsMetadataDir);
			LOG.debug(gatherResponse);	
			
			jobstatsDoc.setGatherDocRetrievedCount(gatherResponse.getDocCount());
			jobstatsDoc.setGatherDocExpectedCount(gatherResponse.getNodeCount());
			jobstatsDoc.setGatherMetaRetrievedCount(gatherResponse.getDocCount2());
			jobstatsDoc.setGatherMetaExpectedCount(gatherResponse.getNodeCount());
			jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, gatherResponse.getDocCount());
			
			if (gatherResponse.getErrorCode() != 0 ) {
			
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
			jobstatsDoc.setPublishStatus("generateDocAndMetadata : " + publishStatus);		       
			publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GENERATEDOC);
		}
		
		return ExitStatus.COMPLETED;      
	}
	
	/**
	 * Reads the contents of a text file and return the guid before the first comma
	 * as an element in the returned HashMap.
	 * The file is assumed to already exist.
	 * @file textFile the text file to process
	 * @return a HashMap of text strings, representing each file of the specified file and count
	 */
	private HashMap<String, Integer> readDocGuidsFromTextFile(File textFile) throws IOException 
	{
		HashMap<String, Integer> lineMap = new HashMap<String, Integer>();
		
		try (FileReader fileReader = new FileReader(textFile);
				BufferedReader reader = new BufferedReader(fileReader)) {
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					int i = textLine.indexOf(",");
					if (i != -1){
						textLine = textLine.substring(0, textLine.indexOf(",")).trim();
						Integer count = lineMap.get(textLine);
						if(count != null && count > 0) {
							count++;
						} else {
							count = 1;
						}
						lineMap.put(textLine, count);
					}
				}
			}
		}
		
		return lineMap;
	}

	@Required
	public void setDocMetadataGuidParserService(DocMetaDataGuidParserService docMetadataSvc) 
	{
		this.docMetaDataParserService = docMetadataSvc;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) 
	{
		this.publishingStatsService = publishingStatsService;
	}
	@Required
	public void setNovusDocFileService(NovusDocFileService novusDocFileService) 
	{
		this.novusDocFileService = novusDocFileService;
	}

}
