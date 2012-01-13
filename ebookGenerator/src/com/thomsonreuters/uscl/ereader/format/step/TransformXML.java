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

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.assemble.step.AssembleEbook;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.TransformerService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step transforms the Novus extracted XML documents into HTML.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TransformXML extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(AssembleEbook.class);
	private TransformerService transformerService;

	public void settransformerService(TransformerService transformerService) 
	{
		this.transformerService = transformerService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		
		String titleId = jobParams.getString(JobParameterKey.TITLE_ID);
		Long jobId = jobInstance.getId();

		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);
		String transformDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORMED_DIR);
		//TODO: Set value below based on execution context value
		int numDocsInTOC = 0; 
		
		File xmlDir = new File(xmlDirectory);
		File transformDir = new File(transformDirectory);
		
		long startTime = System.currentTimeMillis();
		int numDocsTransformed = transformerService.transformXMLDocuments(xmlDir, transformDir, titleId, jobId);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Add check to make sure number of documents that were transformed equals number of documents
		//retrieved from Novus
		if (numDocsTransformed == 0)
		{
			String message = "The number of documents transformed did not match the number " +
					"of documents retrieved from the eBook TOC. Transformed " + numDocsTransformed + 
					" documents while the eBook TOC had " + numDocsInTOC + " documents.";
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		
		//TODO: Improve metrics
		LOG.debug("Transformed " + numDocsTransformed + " XML files in " + elapsedTime + " milliseconds");
		
		return ExitStatus.COMPLETED;
	}

}
