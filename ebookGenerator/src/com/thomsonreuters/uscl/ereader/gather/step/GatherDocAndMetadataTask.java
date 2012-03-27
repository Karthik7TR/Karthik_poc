/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step persists the Novus Metadata xml to DB.
 * 
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class GatherDocAndMetadataTask extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GatherDocAndMetadataTask.class);
	private DocMetaDataGuidParserService docMetaDataParserService;
	private GatherService gatherService;
	private PublishingStatsService publishingStatsService;

	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		File tocFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
		File docsDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR));
		File docsMetadataDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));
		File docsGuidsFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE));
		String docCollectionName = jobParams.getString(JobParameterKey.DOC_COLLECTION_NAME);
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

		docMetaDataParserService.generateDocGuidList(tocFile, docsGuidsFile);
       
		List<String> docGuids = readDocGuidsFromTextFile(docsGuidsFile);
    	
		GatherDocRequest gatherDocRequest = new GatherDocRequest(docGuids, docCollectionName, docsDir, docsMetadataDir);
		GatherResponse gatherResponse = gatherService.getDoc(gatherDocRequest);
		LOG.debug(gatherResponse);

		PublishingStats jobstatsDoc = new PublishingStats();
		jobstatsDoc.setJobInstanceId(jobInstance);
		jobstatsDoc.setGatherDocRetrievedCount(gatherResponse.getDocCount());
		jobstatsDoc.setGatherDocExpectedCount(gatherResponse.getNodeCount());
		jobstatsDoc.setGatherDocRetryCount(gatherResponse.getRetryCount());
		jobstatsDoc.setPublishStatus(gatherResponse.getPublishStatus());
       
		publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GATHERDOC);

		
		if (gatherResponse.getErrorCode() != 0 ) {
		
			GatherException gatherException = new GatherException(
					gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
			throw gatherException;
		}
		return ExitStatus.COMPLETED;      
	}

	@Required
	public void setDocMetadataGuidParserService(DocMetaDataGuidParserService docMetadataSvc) {
		this.docMetaDataParserService = docMetadataSvc;
	}
	@Required
	public void setGatherService(GatherService service) {
		this.gatherService = service;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	/**
	 * Reads the contents of a text file and return the guid before the first comma
	 * as an element in the returned list.
	 * The file is assumed to already exist.
	 * @file textFile the text file to process
	 * @return a list of text strings, representing each file of the specified file
	 */
	public static List<String> readDocGuidsFromTextFile(File textFile) throws IOException {
		List<String> lineList = new ArrayList<String>();
		FileReader fileReader = new FileReader(textFile);
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					int i = textLine.indexOf(",");
					if (i != -1){
					textLine = textLine.substring(0, textLine.indexOf(","));
					lineList.add(textLine.trim());
					}
				}
			}
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return lineList;
	}
}
