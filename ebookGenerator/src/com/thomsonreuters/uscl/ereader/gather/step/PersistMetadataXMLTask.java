/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

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

import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step persists the Novus Metadata xml to DB.
 * 
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class PersistMetadataXMLTask extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(PersistMetadataXMLTask.class);
	private DocMetadataService docMetaDataSvc;

	public void setDocMetadataService(DocMetadataService docMetaDataSvc) 
	{
		this.docMetaDataSvc = docMetaDataSvc;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		
		String titleId = jobParams.getString(JobParameterKey.TITLE_ID);
		Long jobId = jobInstance.getId();

		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_GATHER_DOCS_PATH);

		//TODO: Set value below based on execution context value
		int numDocsInTOC = 0; 
		
		int numDocsMetaDataRun = 0;
		
		File xmlDir = new File(xmlDirectory + "/metadata");
		
		long startTime = System.currentTimeMillis();
		
		//recursively read the directory for parsing the document metadata
				
	        if (xmlDir.isDirectory()){

	            File allFiles[] = xmlDir.listFiles();
	            for(File metadataFile : allFiles){
	                docMetaDataSvc.parseAndStoreDocMetadata(titleId, jobId.intValue(), metadataFile);
	                numDocsMetaDataRun++;
	            }
		
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Add check to make sure number of documents that were transformed equals number of documents
		//retrieved from Novus
		if (numDocsMetaDataRun != numDocsInTOC)
		{
			String message = "The number of documents for which metadata was extracted did not match the number " +
					"of documents retrieved from the eBook TOC. Transformed " + numDocsMetaDataRun + 
					" documents while the eBook TOC had " + numDocsInTOC + " documents.";
			LOG.error(message);
			throw new Exception(message);
		}
		
		//TODO: Improve metrics
		LOG.debug("Persisted all Metadata XML files in " + elapsedTime + " milliseconds");
		

	}
			return ExitStatus.COMPLETED;	        
	}

}
