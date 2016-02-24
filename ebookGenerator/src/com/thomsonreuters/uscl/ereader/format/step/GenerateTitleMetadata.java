/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This class is responsible for generating title metadata based on information taken from the Job Parameters, Execution Context, and the file-system.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class GenerateTitleMetadata extends AbstractSbTasklet {

	private static final Logger LOG = Logger.getLogger(GenerateTitleMetadata.class);
	private static final String VERSION_NUMBER_PREFIX = "v";
	private TitleMetadataService titleMetadataService;
	
	/**
	 * To update publishingStatsService table.
	 */
	private PublishingStatsService publishingStatsService;
		
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = getJobParameters(chunkContext);
		Long jobId = getJobInstance(chunkContext).getId();
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
		
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String versionNumber = VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
		
		TitleMetadata titleMetadata = new TitleMetadata(fullyQualifiedTitleId, versionNumber, bookDefinition.getProviewFeatures(), 
				bookDefinition.getKeyWords(), bookDefinition.getAuthors(), bookDefinition.getIsPilotBook(), bookDefinition.getIsbnNormalized());
		String materialId = bookDefinition.getMaterialId();
		
		//TODO: verify that default of 1234 for material id is valid.
		titleMetadata.setMaterialId(StringUtils.isNotBlank(materialId) ? materialId : "1234");
		titleMetadata.setCopyright(bookDefinition.getCopyright());
		titleMetadata.setDisplayName(bookDefinition.getProviewDisplayName());
		titleMetadata.setFrontMatterTocLabel(bookDefinition.getFrontMatterTocLabel());
		titleMetadata.setFrontMatterPages(bookDefinition.getFrontMatterPages());
		
		//TODO: Remove the calls to these methods when the book definition object is introduced to this step.
//		addAuthors(bookDefinition, titleMetadata);
		

		
		Long jobInstanceId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		
		LOG.debug("Generated title metadata for display name: " + titleMetadata.getDisplayName());
		
		File titleXml = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.TITLE_XML_FILE));
		
		String tocXmlFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE);
		OutputStream titleManifest = null;
		InputStream tocXml = null;
		String status = "Completed";

		try {
			titleManifest = new FileOutputStream(titleXml);
			tocXml = new FileInputStream(tocXmlFile);
			
			File documentsDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR));
			//TODO: refactor the titleMetadataService to use the method that takes a book definition instead of a titleManifest object.
			if(bookDefinition.isSplitBook()){	
				splitBookTitle(titleMetadata,jobInstanceId,jobExecutionContext);				
			}
			else{
				addArtwork(jobExecutionContext, titleMetadata);
				addAssets(jobExecutionContext, titleMetadata);
				titleMetadataService.generateTitleManifest(titleManifest, tocXml, titleMetadata, jobInstanceId, documentsDirectory, JobExecutionKey.ALT_ID_DIR_PATH);
			}
		} 
		catch(Exception e)
		{
		  status = "Failed";
		  throw (e);
		}
		finally {
			tocXml.close();
			titleManifest.close();
			PublishingStats jobstats = new PublishingStats();
		    jobstats.setJobInstanceId(jobId);
		    jobstats.setPublishStatus("generateTitleManifest : " + status);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	protected void splitBookTitle(final TitleMetadata titleMetadata, final Long jobInstanceId,
			final ExecutionContext jobExecutionContext) throws Exception {
		OutputStream splitTitleManifest = null;

		InputStream splitTocXml = null;

		try {

			File splitTitleXml = new File(getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.SPLIT_TITLE_XML_FILE));

			String splitTocXmlFile = getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.FORMAT_SPLITTOC_FILE);

			splitTocXml = new FileInputStream(splitTocXmlFile);
			splitTitleManifest = new FileOutputStream(splitTitleXml);
			File transformedDocsDir = new File(getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
			String docToSplitBookFile = getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.DOC_TO_SPLITBOOK_FILE);
			String splitNodeInfoFile = getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.SPLIT_NODE_INFO_FILE);
			titleMetadataService.generateSplitTitleManifest(splitTitleManifest, splitTocXml, titleMetadata,
					jobInstanceId, transformedDocsDir, docToSplitBookFile, splitNodeInfoFile);
		} catch (Exception e) {
			throw (e);
		} finally {
			splitTocXml.close();
			splitTitleManifest.close();
		}
	}

	/**
	 * Reads in a list of TOC Guids that are associated to each Doc Guid to later be used
	 * for anchor insertion and generates a map.
	 * 
	 * @param docGuidsFile file containing the DOC to TOC guid relationships
	 * @return documentlist ordered by file
	 */
	protected ArrayList<Doc> readTOCGuidList(File docGuidsFile)
		throws EBookFormatException
	{
		ArrayList<Doc> docToTocGuidList = new ArrayList<Doc>();

		BufferedReader reader = null;
		try
		{
			LOG.info("Reading in TOC anchor map file...");
			int numDocs = 0;
			
			reader = new BufferedReader(new FileReader(docGuidsFile));
			String input = reader.readLine();
			while (input != null)
			{
				numDocs++;
				String[] line = input.split(",", -1);
				Doc doc = new Doc(line[0], line[0]+".html", 0, null);
				docToTocGuidList.add(doc);
				
				input = reader.readLine();
			}
			LOG.info("Generated Doc List " + numDocs );
		}
		catch(IOException e)
		{
			String message = "Could not read the DOC guid list file: " + 
					docGuidsFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close DOC guid to TOC guid file reader.", e);
			}
		}
		return(docToTocGuidList);
	}
	
	private void addAssets(ExecutionContext jobExecutionContext,
			TitleMetadata titleMetadata) {
		//All gathered images (dynamic and static) are expected to be here by the time this step executes.
		File imagesDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_ASSETS_DIR));
		ArrayList<Asset> assets = titleMetadataService.createAssets(imagesDirectory);
		LOG.debug(assets);
		titleMetadata.setAssets(assets);
	}

	private void addArtwork(ExecutionContext jobExecutionContext,
			TitleMetadata titleMetadata) {
		File coverArtFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.COVER_ART_PATH));
		Artwork coverArt = titleMetadataService.createArtwork(coverArtFile); //factory method, returns Artwork.
		titleMetadata.setArtwork(coverArt);
	}

	public void setTitleMetadataService(TitleMetadataService titleMetadataService) {
		this.titleMetadataService = titleMetadataService;
	}
	
	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	
}
