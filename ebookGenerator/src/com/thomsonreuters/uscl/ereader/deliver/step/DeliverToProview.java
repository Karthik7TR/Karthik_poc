/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This class is responsible for delivering a generated eBook to ProView.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class DeliverToProview extends AbstractSbTasklet {

	private static final Logger LOG = Logger.getLogger(DeliverToProview.class);
	private static final String VERSION_NUMBER_PREFIX = "v";
	private ProviewClient proviewClient;
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParameters = getJobParameters(chunkContext);
		
		File eBook = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE_PATH));
		String fullyQualifiedTitleId = jobParameters.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
		String versionNumber = VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.MAJOR_VERSION);
		
		long startTime = System.currentTimeMillis();
		LOG.debug("Publishing eBook [" + fullyQualifiedTitleId+ "] to Proview.");
		
		proviewClient.publishTitle(fullyQualifiedTitleId, versionNumber, eBook);
		
		long processingTime = System.currentTimeMillis() - startTime;
		LOG.debug("Publishing complete. Time elapsed: " + processingTime + "ms");
		
		return ExitStatus.COMPLETED;
	}
	
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
}
