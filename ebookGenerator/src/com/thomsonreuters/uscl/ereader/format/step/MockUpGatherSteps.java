/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Used to overwrite gather steps.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class MockUpGatherSteps extends AbstractSbTasklet 
{
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(ParseImageGUIDList.class);
	
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		
		String titleId = jobParams.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
		
		String sampleBook = null;
		
		if (titleId.equalsIgnoreCase("uscl/cr/al_2012_federal"))
		{
			sampleBook = "AnalyticalSample1";
		}
		else if (titleId.equalsIgnoreCase("uscl/cr/al_2012_state"))
		{
			sampleBook = "AnalyticalSample2";
		}
		else if (titleId.equalsIgnoreCase("uscl/an/FRCP"))
		{
			sampleBook = "CRSample";
		}
		else if (titleId.equalsIgnoreCase("uscl/an/FSLP"))
		{
			sampleBook = "SCSample";
		}
		else if (titleId.equalsIgnoreCase("uscl/an/IMPH"))
		{
			sampleBook = "DocsWithImages";
		}
		
		//TODO: Retrieve expected number of document for this eBook from execution context
		jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, 5);
		
		if (sampleBook != null)
		{
			LOG.info("Modifying Gather Execution Keys to point to " + sampleBook + " sample directory...");
			
			File staticGatherDir = 
					new File("/nas/" + sampleBook + "/Gather");
			
			File staticTocSample = new File(staticGatherDir, "toc.xml");
			File staticDocsDirectory = new File(staticGatherDir, "Docs");
			
			File staticCover = new File("/nas/CoverArt/coverArt.PNG");
			File cssFile = new File("/nas/CSS/document.css");
			
			//TODO: Remove execution key modification
			jobExecutionContext.putString(
					JobExecutionKey.GATHER_DIR, staticGatherDir.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.GATHER_TOC_FILE, staticTocSample.getAbsolutePath());
			jobExecutionContext.putString(
					JobExecutionKey.GATHER_DOCS_DIR, staticDocsDirectory.getAbsolutePath());
			jobExecutionContext.putString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH, 
					jobExecutionContext.getString(JobExecutionKey.FORMAT_HTML_WRAPPER_DIR));
			jobExecutionContext.putString(
					JobExecutionKey.COVER_ART_PATH, staticCover.getAbsolutePath());
			jobExecutionContext.putString(JobExecutionKey.DOCUMENT_CSS_FILE, cssFile.getAbsolutePath());
			
			LOG.info("Gather Execution Keys modified to point to " + sampleBook + " directory.");
		}
		return ExitStatus.COMPLETED;
	}
}
