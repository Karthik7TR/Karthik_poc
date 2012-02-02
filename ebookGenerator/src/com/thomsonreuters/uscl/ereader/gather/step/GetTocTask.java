/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
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
		
		GatherResponse gatherResponse = null;
			
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		File tocFile = new File(jobExecutionContext.getString(JobExecutionKey.GATHER_TOC_FILE));

		// TOC
		String tocCollectionName = jobParams.getString(JobParameterKey.TOC_COLLECTION_NAME); 
		String tocRootGuid = jobParams.getString(JobParameterKey.ROOT_TOC_GUID);
		if(tocRootGuid != null)
		{
		GatherTocRequest gatherTocRequest = new GatherTocRequest(tocRootGuid, tocCollectionName, tocFile);
		LOG.debug(gatherTocRequest);
	
		gatherResponse = gatherService.getToc(gatherTocRequest);
		}
		
		// NORT
		String nortDomainName = jobParams.getString(JobParameterKey.NORT_DOMAIN); 
		String nortExpressionFilter = jobParams.getString(JobParameterKey.NORT_FILTER_VIEW);
		if(nortDomainName != null)
		{
		GatherNortRequest gatheNortRequest = new GatherNortRequest(nortDomainName, nortExpressionFilter, tocFile);
		LOG.debug(gatheNortRequest);
	
		gatherResponse = gatherService.getNort(gatheNortRequest);
		}
		
		LOG.debug(gatherResponse);
		if (gatherResponse.getErrorCode() != 0 ) {
			GatherException gatherException = new GatherException(
					gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
			throw gatherException;
		}
		
		return ExitStatus.COMPLETED;
	}

	@Required
	public void setGatherService(GatherService gatherService) {
		this.gatherService = gatherService;
	}
}
