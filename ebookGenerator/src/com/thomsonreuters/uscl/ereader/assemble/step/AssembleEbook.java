/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.step;
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
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Step responsible for assembling an eBook.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class AssembleEbook extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(AssembleEbook.class);
	private EBookAssemblyService eBookAssemblyService;

	public void seteBookAssemblyService(EBookAssemblyService eBookAssemblyService) {
		this.eBookAssemblyService = eBookAssemblyService;
	}

	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		String eBookDirectoryPath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_DIRECTORY_PATH);
		String eBookFilePath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE_PATH);
		
		File eBookDirectory = new File(eBookDirectoryPath);
		File eBookFile = new File(eBookFilePath);
		
		long startTime = System.currentTimeMillis();
		eBookAssemblyService.assembleEBook(eBookDirectory, eBookFile);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Consider defining the time spent in assembly as a JODA-Time interval.
		LOG.debug("Assembled eBook in " + elapsedTime + " milliseconds");
		
		return ExitStatus.COMPLETED;
	}
}

