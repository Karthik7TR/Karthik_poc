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
import com.thomsonreuters.uscl.ereader.format.service.TitleMetadataAnchorUpdateService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Step that updates all the anchor references to the proper format.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TitleMetadataAnchorUpdate extends AbstractSbTasklet {
	
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(TitleMetadataAnchorUpdate.class);
	private TitleMetadataAnchorUpdateService anchorUpdateService;
	
	public void setanchorUpdateService(TitleMetadataAnchorUpdateService anchorUpdateService)
	{
		this.anchorUpdateService = anchorUpdateService;
	}
	
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) 
			throws Exception 
	{
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		String srcTitleXMLFileName = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.INTERMEDIATE_TITLE_XML_FILE);
		String titleXMLFileName = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.TITLE_XML_FILE);
		String docToTocFileName = 
				getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE);
		
		File srcTitleXML = new File(srcTitleXMLFileName);
		File titleXML = new File(titleXMLFileName);
		File docToToc = new File(docToTocFileName);
		
		long startTime = System.currentTimeMillis();
		anchorUpdateService.updateAnchors(srcTitleXML, titleXML, docToToc);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		LOG.debug("Transformed anchors in " + titleXMLFileName + " file in " + elapsedTime + " milliseconds");
		
		return ExitStatus.COMPLETED;		
	}

}
