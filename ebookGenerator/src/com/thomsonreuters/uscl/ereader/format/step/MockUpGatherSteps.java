/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
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
		
		String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);
		//TODO: Retrieve expected number of document for this eBook from execution context
		jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, 5);
		
		LOG.debug("Moving sample content into gather...");
		
		//TODO: Remove stubbed XML files copy
		moveSampleDocsToGather(new File(xmlDirectory));
		
		LOG.info("Moved all sample content into appropriate gather paths.");
		
		return ExitStatus.COMPLETED;
	}

	//TODO: Remove stub method that moves XML files to Gather Directory
	public void moveSampleDocsToGather(File targetBookDir) throws IOException
	{
		File sourceDir = 
				//new File("C:\\nas\\AnalyticalSample1\\XML");
				//new File("C:\\nas\\AnalyticalSample2\\XML"); 
				//new File("C:\\nas\\CRSample\\XML");  
				//new File("C:\\nas\\SCSample\\XML");
				new File("C:\\nas\\DocsWithImages\\XML");
		
		if (sourceDir.exists())
		{
			File[] xmlFileList = sourceDir.listFiles();
			for (File xml : xmlFileList)
			{
				FileUtils.copyFile(xml, new File(targetBookDir, xml.getName()));
			}
		}
	}
}
