/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
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
	private BookDefinitionService bookService;

	public DocMetadataService getDocMetadataService() {
		return docMetadataService;
	}

	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}
	
	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookService = service;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobInstance jobInstance = getJobInstance(chunkContext);
		String publishStatus = "Completed";
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobInstance.getId());
	    ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
	    JobParameters jobParameters = getJobParameters(chunkContext);
		String bookVersion = jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);				
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		try {

			try{
				if (bookDefinition.isSplitBook()) {
					// update table with Node Info
					String splitNodeInfoFile = getRequiredStringProperty(jobExecutionContext,
							JobExecutionKey.SPLIT_NODE_INFO_FILE);
					List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
					readDocImgFile(new File(splitNodeInfoFile), currentsplitNodeList, bookVersion, bookDefinition);

					if (currentsplitNodeList != null || currentsplitNodeList.size() != 0) {
						List<SplitNodeInfo> persistedSplitNodes = bookDefinition.getSplitNodesAsList();

						boolean same = hasChanged(persistedSplitNodes, currentsplitNodeList, bookVersion);
						if (!same) {
							bookService.updateSplitNodeInfoSet(bookDefinition.getEbookDefinitionId(),
									currentsplitNodeList, bookVersion);
						}
					}
				}
			}
			catch(Exception e){
				log.error("Failed to update splitBookInfo", e);
				throw e;
			}
			// We only archive in the production environment
			if (CoreConstants.PROD_ENVIRONMENT_NAME.equalsIgnoreCase(environmentName)) {
	
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
	
	/**
	 * 
	 * @param persistedsplitNodeList
	 * @param currentsplitNodeList
	 * @param currentVersion
	 * @return
	 */
	public boolean hasChanged(List<SplitNodeInfo> persistedsplitNodeList,List<SplitNodeInfo> currentsplitNodeList,String currentVersion){
		
		if (persistedsplitNodeList == null || persistedsplitNodeList.size() == 0) {
			return false;
		} 
		else{
			List<SplitNodeInfo> sameVersionSplitNodes = new ArrayList<SplitNodeInfo>();
			for (SplitNodeInfo splitNodeInfo : persistedsplitNodeList) {
				if (splitNodeInfo.getBookVersionSubmitted().equalsIgnoreCase(currentVersion)) {
					sameVersionSplitNodes.add(splitNodeInfo);
				}
			}
			if(sameVersionSplitNodes.size() == 0){
				return false;
			}
			else if(sameVersionSplitNodes.size() >= 0 && sameVersionSplitNodes.size() != currentsplitNodeList.size() ){
				return false;
			}
			else{				
				for (SplitNodeInfo splitNodeInfo : currentsplitNodeList) {
					if(!sameVersionSplitNodes.contains(splitNodeInfo)){
						return false;
					}
				}
			}
		}
		
		return true;
		
	}
	
	/**
	 * The file contents are in this format bookDefinitionId|TocGuid|splitTitleID
	 * @param docToSplitBook
	 * @param splitNodeInfoList
	 */
	public void readDocImgFile(final File docToSplitBook, List<SplitNodeInfo> splitNodeInfoList, String bookVersion, BookDefinition bookDefinition) {
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(docToSplitBook));

			while ((line = stream.readLine()) != null) {
				
				String[] splitted = line.split("\\|");				
				
				SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
				splitNodeInfo.setBookDefinition(bookDefinition);
				splitNodeInfo.setBookVersionSubmitted(bookVersion);
				String guid = splitted[0];
				if(guid.length()>33){
					guid = StringUtils.substring(guid, 0, 33);
				}
				splitNodeInfo.setSplitNodeGuid(guid);
				splitNodeInfo.setSpitBookTitle(splitted[1]);	
				splitNodeInfoList.add(splitNodeInfo);

			}
		} catch (IOException iox) {
			throw new RuntimeException("Unable to find File : " + docToSplitBook.getAbsolutePath() + " " + iox);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException("An IOException occurred while closing a file ", e);
				}
			}
		}
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
