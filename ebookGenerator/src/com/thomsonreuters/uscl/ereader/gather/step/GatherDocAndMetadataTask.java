/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step persists the Novus Metadata xml to DB.
 * 
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class GatherDocAndMetadataTask extends AbstractSbTasklet
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GatherDocAndMetadataTask.class);
	private DocMetaDataGuidParserService docMetaDataParserSvc;
	private GatherService gatherService;

	public void setDocMetadataGuidParserService(DocMetaDataGuidParserService docMetaDataSvc) 
	{
		this.docMetaDataParserSvc = docMetaDataParserSvc;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception 
	{
		ExitStatus taskExitStatus = ExitStatus.COMPLETED;		
		
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		String tocDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE);
		String dynamicGuidDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE);

		
		File tocDir = new File(tocDirectory);
		File docsGuidsDir = new File(dynamicGuidDirectory);
		
		long startTime = System.currentTimeMillis();
		
		//read the directory for parsing the toc xml file
				
	        if (tocDir.isDirectory()){
	        	
	        	docMetaDataParserSvc.generateDocGuidList(tocDir, docsGuidsDir);
	        	
/*	    		GatherDocRequest gatherDocRequest = new GatherDocRequest(tocRootGuid,tocCollectionName,destinationFile);
	    		LOG.debug(gatherDocRequest);
	    		GatherResponse gatherResponse = gatherService.getDoc(gatherDocRequest);
	    		if(gatherResponse.getErrorCode() != 0 ){
	    			
	    			taskExitStatus =  ExitStatus.FAILED;
	    		}*/
	    		
	    		return taskExitStatus ;		        	

	            }
		
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Add check to make sure number of documents that were persisted equals number of documents
		//retrieved from TOC

		
		//TODO: Improve metrics
		LOG.debug("Retrieve Document and metadata XML files in " + elapsedTime + " milliseconds");
		
			return ExitStatus.COMPLETED;	        
	}

}
