/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLCreateNamedAnchorsInternalLinksService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step transforms the HTML generated by the transformation process into ProView acceptable HTML.
 * by creating named anchors where they are currently ids in divs or spans.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLCreateNamedAnchorsInternalLinks extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(HTMLCreateNamedAnchorsInternalLinks.class);
	private HTMLCreateNamedAnchorsInternalLinksService transformerCreateAnchorService;

	public void setTransformerCreateAnchorService(HTMLCreateNamedAnchorsInternalLinksService transformerCreateAnchorService) 
	{
		this.transformerCreateAnchorService = transformerCreateAnchorService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		
		String titleId = bookDefinition.getTitleId();
		Long jobId = jobInstance.getId();

		String transformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_POST_TRANSFORM_DIR);
		String postTransformDirectory = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_CREATED_DIR);
		//TODO: Set value below based on execution context value
		int numDocsInTOC = 0; 
				
		File transformDir = new File(transformDirectory);
		File postTransformDir = new File(postTransformDirectory);
		
		long startTime = System.currentTimeMillis();
		int numDocsTransformed = 
				transformerCreateAnchorService.transformHTML(transformDir, postTransformDir,titleId, jobId);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Add check to make sure number of documents that were transformed equals number of documents
		//retrieved from Novus
		if (numDocsTransformed == 0)
		{
			String message = "The number of post transformed documents did not match the number " +
					"of documents retrieved from the eBook TOC. Transformed " + numDocsTransformed + 
					" documents while the eBook TOC had " + numDocsInTOC + " documents.";
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		
		//TODO: Improve metrics
		LOG.debug("Transformed " + numDocsTransformed + " HTML files in " + elapsedTime + " milliseconds");
		
//		return ExitStatus.FAILED; // TODO: Remove after testing. KG

		return ExitStatus.COMPLETED;
	}
}
