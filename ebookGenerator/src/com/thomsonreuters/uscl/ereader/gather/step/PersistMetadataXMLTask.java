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
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;

import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step persists the Novus Metadata xml to DB.
 * 
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
public class PersistMetadataXMLTask extends AbstractSbTasklet {
	// TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger
			.getLogger(PersistMetadataXMLTask.class);
	private DocMetadataService docMetadataService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);

		String titleId = jobParams.getString(JobParameterKey.TITLE_ID);
		Long jobId = jobInstance.getId();

//		String docCollectionName = jobParams
//				.getString(JobParameterKey.DOC_COLLECTION_NAME); 
		String docCollectionName = null; 
		String tocSequenceNum = null; 

		File metaDataDirectory = new File(getRequiredStringProperty(
				jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));

		// TODO: Set value below based on execution context value

		int numDocsMetaDataRun = 0;

		// recursively read the directory for parsing the document metadata

		if (metaDataDirectory.isDirectory()) {

			File allFiles[] = metaDataDirectory.listFiles();
			for (File metadataFile : allFiles) {
				String fileName =  metadataFile.getName();
				if (fileName.lastIndexOf("-") > -1)
				{
					tocSequenceNum = fileName.substring(0, fileName.indexOf("-")); 

					docCollectionName = fileName.substring(fileName.indexOf("-")+1, fileName.lastIndexOf("-")); 
				}
				docMetadataService.parseAndStoreDocMetadata(titleId,
						jobId.intValue(), docCollectionName, metadataFile, tocSequenceNum);
				numDocsMetaDataRun++;
			}
			// TODO: Improve metrics
			LOG.debug("Persisted " + numDocsMetaDataRun
					+ " Metadata XML files from "
					+ metaDataDirectory.getAbsolutePath());
		}
		// TODO: add else?
		return ExitStatus.COMPLETED;
	}

	@Required
	public void setDocMetadataService(DocMetadataService docMetadataSvc) {
		this.docMetadataService = docMetadataSvc;
	}

}
