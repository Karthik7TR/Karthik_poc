/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		GatherResponse gatherResponse = null;
			
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		File tocFile = new File(jobExecutionContext.getString(JobExecutionKey.GATHER_TOC_FILE));
		Long jobInstance = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

		// TOC
		String tocCollectionName = jobParams.getString(JobParameterKey.TOC_COLLECTION_NAME); 
		String tocRootGuid = jobParams.getString(JobParameterKey.ROOT_TOC_GUID);
		// NORT
		String nortDomainName = jobParams.getString(JobParameterKey.NORT_DOMAIN); 
		String nortExpressionFilter = jobParams.getString(JobParameterKey.NORT_FILTER_VIEW);
		
		Date nortCutoffDate = null;
		
		if (jobParams.getString(JobParameterKey.PUB_CUTOFF_DATE) != null) {
			nortCutoffDate = (Date)(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jobParams.getString(JobParameterKey.PUB_CUTOFF_DATE).replace("T", " ")));
		}

		if(tocCollectionName != null) // TOC
		{
		GatherTocRequest gatherTocRequest = new GatherTocRequest(tocRootGuid, tocCollectionName, tocFile);
		LOG.debug(gatherTocRequest);
	
		gatherResponse = gatherService.getToc(gatherTocRequest);
		}
		else if(nortDomainName != null) // NORT
		{
//			GatherNortRequest gatherNortRequest = new GatherNortRequest(nortDomainName, nortExpressionFilter, tocFile, nortCutoffDate, jobInstance);
			GatherNortRequest gatherNortRequest = new GatherNortRequest(nortDomainName, nortExpressionFilter, tocFile, nortCutoffDate);
			LOG.debug(gatherNortRequest);
	
			gatherResponse = gatherService.getNort(gatherNortRequest);
		}
		else
		{
			String errorMessage = "Neither tocCollectionName nor nortDomainName were defined for eBook" ;
			LOG.error(errorMessage);
			gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, errorMessage, 0,0,0,"TOC STEP FAILED UNDEFINED KEY");
		}
		
        PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobInstance);
        jobstats.setGatherTocDocCount(gatherResponse.getDocCount());
        jobstats.setGatherTocNodeCount(gatherResponse.getNodeCount());
        jobstats.setGatherTocSkippedCount(gatherResponse.getSkipCount());
        jobstats.setGatherTocRetryCount(gatherResponse.getRetryCount());
        jobstats.setPublishStatus(gatherResponse.getPublishStatus());
       
		publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GATHERTOC);
		
		// TODO: update doc count used in Job Execution Context
		
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
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
