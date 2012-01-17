/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * 
 * @author U0105927
 *
 */
public class GetTocTask  extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(PersistMetadataXMLTask.class);
	private GatherService gatherService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		ExitStatus taskExitStatus = ExitStatus.COMPLETED;
		
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		String tocCollectionName = jobExecutionContext.getString(JobParameterKey.TOC_COLLECTION_NAME);
		String tocRootGuid = jobExecutionContext.getString(JobParameterKey.ROOT_TOC_GUID);

		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);

		File destinationFile = new File(xmlDirectory);
		GatherTocRequest gatherTocRequest = new GatherTocRequest(tocRootGuid,tocCollectionName,destinationFile);
		GatherResponse gatherResponse = gatherService.getToc(gatherTocRequest);
		if(gatherResponse.getErrorCode() == 0 ){
			
			taskExitStatus =  ExitStatus.FAILED;
		}
		
		return taskExitStatus ;	
	}

}
