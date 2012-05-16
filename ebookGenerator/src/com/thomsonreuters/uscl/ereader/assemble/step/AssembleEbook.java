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
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Step responsible for assembling an eBook.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class AssembleEbook extends AbstractSbTasklet {
	//TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(AssembleEbook.class);
	private EBookAssemblyService eBookAssemblyService;
	private PublishingStatsService publishingStatsService;

	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		Long jobInstanceId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

		String eBookDirectoryPath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_DIRECTORY);
		String eBookFilePath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE);
		
		File eBookDirectory = new File(eBookDirectoryPath);
		File eBookFile = new File(eBookFilePath);
		
		long startTime = System.currentTimeMillis();
		
		eBookAssemblyService.assembleEBook(eBookDirectory, eBookFile);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		//TODO: Consider defining the time spent in assembly as a JODA-Time interval.
		LOG.debug("Assembled eBook in " + elapsedTime + " milliseconds");
		double gzipLength = eBookFile.length();
		
		/**
		 * save book size in bytes so that we would not lose any quantity in unit conversion and rounding. 
		 * We can convert bytes to KB/ MB while displaying in UI 
		 */
		
		double  gzipeSizeInMB = (double)gzipLength/(1024*1024);
		double gzipeSizeInKB = (int)gzipLength/1024;
		updateAssembleEbookStats(gzipLength,jobInstanceId,eBookDirectoryPath );
		
		
		return ExitStatus.COMPLETED;
	}
	
	private void updateAssembleEbookStats(double gzipeSize,Long jobId,String eBookDirectoryPath) {

		File docFile = new File(eBookDirectoryPath);
		File finalDocDir = new File(docFile,"documents");
		File finalPdfImageDir = new File(docFile,"assets");
		 
		String documentsPath = finalDocDir.getAbsolutePath();
		String pdfImagePath = finalPdfImageDir.getAbsolutePath();
		
		double largestDocuent = eBookAssemblyService.getLargestContent(documentsPath ,".html");
		double largestPdf = eBookAssemblyService.getLargestContent(pdfImagePath,".pdf");
		double largestImage = eBookAssemblyService.getLargestContent(pdfImagePath,".png,.jpeg,.gif");

		
		PublishingStats jobstatsFormat = new PublishingStats();
		jobstatsFormat.setJobInstanceId(jobId);
		jobstatsFormat.setLargestDocSize((int)largestDocuent);
		jobstatsFormat.setLargestImageSize((int)largestImage);
		jobstatsFormat.setLargestPdfSize((int)largestPdf);
		jobstatsFormat.setBookSize((int)gzipeSize);
		
		
		LOG.debug("largestDocuent ="+largestDocuent);
		LOG.debug("largestPdf ="+largestPdf);
		LOG.debug("largestImage ="+largestImage);
		LOG.debug("gzipeSize ="+gzipeSize);
		
		publishingStatsService.updatePublishingStats(jobstatsFormat, StatsUpdateTypeEnum.ASSEMBLEDOC);
		
	}
	
	@Required
	public void seteBookAssemblyService(EBookAssemblyService eBookAssemblyService) {
		this.eBookAssemblyService = eBookAssemblyService;
	}
	@Required
	public void setPublishingStatsService(
			
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	
}

