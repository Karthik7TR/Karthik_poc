/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.assemble.step.AssembleEbook;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLWrapperService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step adds a static predefined HTML header and footer and any ProView specific document wrappers.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class AddHTMLWrapper extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(AssembleEbook.class);
	private HTMLWrapperService htmlWrapperService;

	public void sethtmlWrapperService(HTMLWrapperService htmlWrapperService) 
	{
		this.htmlWrapperService = htmlWrapperService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		
		String transformDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FORMAT_TRANSFORMED_PATH);
		String htmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FORMAT_HTML_WRAPPER_PATH);
		//TODO: Retrieve expected number of document for this eBook from execution context
		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
				
		File transformDir = new File(transformDirectory);
		File htmlDir = new File(htmlDirectory);
		
		long startTime = System.currentTimeMillis();
		int numDocsWrapped = htmlWrapperService.addHTMLWrappers(transformDir, htmlDir);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Update to check value is equal to execution context value (numDocsInTOC)
		if (numDocsWrapped == 0)
		{
			String message = "The number of documents wrapped by the HTMLWrapper Service did " +
					"not match the number of documents retrieved from the eBook TOC. Wrapped " + 
					numDocsWrapped + " documents while the eBook TOC had " + numDocsInTOC + " documents.";
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		
		//TODO: Improve metrics
		LOG.debug("Added HTML and ProView document wrappers in " + elapsedTime + " milliseconds");
		
		return ExitStatus.COMPLETED;
	}

}
