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
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
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
	private static final Logger LOG = Logger.getLogger(AddHTMLWrapper.class);
	private HTMLWrapperService htmlWrapperService;
	
	private BookDefinitionService bookDefnService;
	
	
	public void sethtmlWrapperService(HTMLWrapperService htmlWrapperService) 
	{
		this.htmlWrapperService = htmlWrapperService;
	}
	
	@Required
	public void setBookDefnService(BookDefinitionService bookDefnService) {
		this.bookDefnService = bookDefnService;
	}
	
	
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		String postTransformDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR);
		String htmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_HTML_WRAPPER_DIR);
		String docToTocFileName = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE);
		//TODO: Retrieve expected number of document for this eBook from execution context
		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
					
		JobParameters jobParams = getJobParameters(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		BookDefinition bookDefinition = bookDefnService.findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID));		
		String titleId = bookDefinition.getTitleId();
		boolean keyciteToplineFlag = bookDefinition.getKeyciteToplineFlag();
		
		Long jobId = jobInstance.getId();
		
				
		File postTransformDir = new File(postTransformDirectory);
		File htmlDir = new File(htmlDirectory);
		File docToTocFile = new File(docToTocFileName);
		
		long startTime = System.currentTimeMillis();
		int numDocsWrapped = htmlWrapperService.addHTMLWrappers(postTransformDir, htmlDir, docToTocFile, titleId, jobId, keyciteToplineFlag);
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
		LOG.debug("Added HTML and ProView document wrappers to " + numDocsWrapped + " documents in " + 
				elapsedTime + " milliseconds");
		
		return ExitStatus.COMPLETED;
	}

}
