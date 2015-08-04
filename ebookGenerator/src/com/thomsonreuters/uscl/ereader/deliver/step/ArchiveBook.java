/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.step;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This class is responsible for archiving the created book artifact.
 * Only performed if this is the production ("prod") environment.
 * The last delivered major and minor number version of the file is archived.
 */
public class ArchiveBook extends AbstractSbTasklet {

	private static final Logger log = Logger.getLogger(ArchiveBook.class);
	public static final String MAJOR_ARCHIVE_DIR = "major";
	public static final String MINOR_ARCHIVE_DIR = "minor";

	private String environmentName;
	private File archiveBaseDirectory;
	private PublishingStatsService publishingStatsService;
	private DocMetadataService docMetadataService;

	public DocMetadataService getDocMetadataService() {
		return docMetadataService;
	}

	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobInstance jobInstance = getJobInstance(chunkContext);
		String publishStatus = "Completed";
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobInstance.getId());
		try {
			// We only archive in the production environment
			if (CoreConstants.PROD_ENVIRONMENT_NAME.equalsIgnoreCase(environmentName)) {
				ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
				JobParameters jobParameters = getJobParameters(chunkContext);
				String bookVersion = jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
				
				BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);	
	
				// Calculate and create the target archive directory
				File archiveDirectory = (bookVersion.endsWith(".0")) ?
												new File(archiveBaseDirectory, MAJOR_ARCHIVE_DIR) :
												new File(archiveBaseDirectory, MINOR_ARCHIVE_DIR);
				if (!archiveDirectory.exists()) {
					archiveDirectory.mkdirs();
				}
				
				if(bookDefinition.isSplitBook()){
					File workDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.WORK_DIRECTORY));
					if (workDirectory == null || !workDirectory.isDirectory()) {
						throw new IOException("workDirectory must not be null and must be a directory.");
					}
					List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(jobInstance.getId());
					for (String splitTitleId : splitTitles) {
						splitTitleId = StringUtils.substringAfterLast(splitTitleId, "/");
						File sourceFilename = new File(workDirectory, splitTitleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
						if(sourceFilename == null || !sourceFilename.exists()){
							throw new IOException("eBook must not be null and should exists.");
						}
						archiveBook(jobExecutionContext, archiveDirectory,sourceFilename.getAbsolutePath());
					}
				}
				else{
					String sourceFilename = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE);
					archiveBook(jobExecutionContext, archiveDirectory,sourceFilename);
				}				
				
			}
		} 
		catch (Exception e) 
		{
			publishStatus = "Failed";
			log.error("Failed to archive ebook file", e);
			throw e;
		}
		finally 
		{
			jobstats.setPublishStatus("archiveBook: " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);	
		}
		return ExitStatus.COMPLETED;
	}
	
	private void archiveBook(final ExecutionContext jobExecutionContext, final File archiveDirectory, String sourceFilename) throws IOException{
		// Copy the ebook artifact file to the archive directory		
		File sourceFile = new File(sourceFilename);
		String targetBasename = sourceFile.getName();
		File targetFile = new File(archiveDirectory, targetBasename);
		copyFile(sourceFile, targetFile);
	}
	
	private void copyFile(File source, File target) throws IOException {
		log.debug(String.format("Archive copying %s to %s", source.getAbsolutePath(), target.getAbsolutePath()));
		ImageServiceImpl.copyFile(source, target);
	}
	@Required
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	@Required
	public void setArchiveBaseDirectory(File archiveBaseDirectory) {
		this.archiveBaseDirectory = archiveBaseDirectory;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
