/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Used to overwrite gather steps.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class MockUpGatherSteps extends AbstractSbTasklet 
{	
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		//TODO: Retrieve expected number of document for this eBook from execution context
		jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, 5);

		File staticCover = new File("/apps/eBookBuilder/staticContent/coverArt.PNG");

		jobExecutionContext.putString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH, 
				jobExecutionContext.getString(JobExecutionKey.FORMAT_HTML_WRAPPER_DIR));
		jobExecutionContext.putString(
				JobExecutionKey.COVER_ART_PATH, staticCover.getAbsolutePath());
		
		return ExitStatus.COMPLETED;
	}
}
