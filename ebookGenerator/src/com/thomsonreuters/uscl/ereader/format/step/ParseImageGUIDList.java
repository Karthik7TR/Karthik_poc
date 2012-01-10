/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.XMLImageParserService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step generates the Image GUID list file that is used to retrieve the images referenced
 * by any documents within this eBook.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class ParseImageGUIDList extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(ParseImageGUIDList.class);
	
	private XMLImageParserService xmlImageParserService;

	public void setxmlImageParserService(XMLImageParserService xmlImageParserService) 
	{
		this.xmlImageParserService = xmlImageParserService;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_GATHER_DOCS_PATH);
		String imgGuidListFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_GATHER_IMAGE_GUIDS_FILE_PATH);
		//TODO: Retrieve expected number of document for this eBook from execution context
		int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
		
		File xmlDir = new File(xmlDirectory);
		File imgGuidList = new File(imgGuidListFile);
		
		long startTime = System.currentTimeMillis();
		int numDocsParsed = xmlImageParserService.generateImageList(xmlDir, imgGuidList);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Update to check value is equal to execution context value (numDocsInTOC)
		if (numDocsParsed == 0)
		{
			String message = "The number of documents wrapped by the HTMLWrapper Service did " +
					"not match the number of documents retrieved from the eBook TOC. Wrapped " + 
					numDocsParsed + " documents while the eBook TOC had " + numDocsInTOC + " documents.";
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		
		//TODO: Improve metrics
		LOG.debug("Generate Image Guid list in " + elapsedTime + " milliseconds.");
		
		return ExitStatus.COMPLETED;
	}
}
