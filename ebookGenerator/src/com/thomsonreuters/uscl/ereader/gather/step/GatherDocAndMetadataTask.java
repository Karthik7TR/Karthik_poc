/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

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

	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExitStatus taskExitStatus = ExitStatus.COMPLETED;		
		
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		File tocFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
		File docsDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR));
		File docsMetadataDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));
		File docsGuidsFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE));
		String docCollectionName = jobParams.getString(JobParameterKey.DOC_COLLECTION_NAME);
		
	   
    	docMetaDataParserService.generateDocGuidList(tocFile, docsGuidsFile);
    	List<String> docGuids = GatherImageVerticalImagesTask.readLinesFromTextFile(docsGuidsFile);
    	
		GatherDocRequest gatherDocRequest = new GatherDocRequest(docGuids, docCollectionName, docsDir, docsMetadataDir);
		GatherResponse gatherResponse = gatherService.getDoc(gatherDocRequest);
		LOG.debug(gatherResponse);
		if (gatherResponse.getErrorCode() != 0 ) {
			taskExitStatus =  ExitStatus.FAILED;
		}
	  
		return taskExitStatus;        
	}
	@Required
	public void setDocMetadataGuidParserService(DocMetaDataGuidParserService docMetadataSvc) {
		this.docMetaDataParserService = docMetadataSvc;
	}
	@Required
	public void setGatherService(GatherService service) {
		this.gatherService = service;
	}
}
