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

	public void setTransformerService(TransformerService transService) 
	{
		this.transformerService = transService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_GATHER_DOCS_PATH);
		String transformDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FORMAT_TRANSFORMED_PATH);
		
		File xmlDir = new File(xmlDirectory);
		File transformDir = new File(transformDirectory);
		
		long startTime = System.currentTimeMillis();
		transformerService.transformXMLDocuments(xmlDir, transformDir);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Improve metrics
		LOG.debug("Transformed all XML files in " + elapsedTime + " milliseconds");
		
		return ExitStatus.COMPLETED;
	}

}
