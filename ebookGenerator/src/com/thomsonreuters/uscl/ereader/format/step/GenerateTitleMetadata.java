/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Author;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;

/**
 * This class is responsible for generating title metadata based on information taken from the Job Parameters, Execution Context, and the file-system.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class GenerateTitleMetadata extends AbstractSbTasklet {

	private static final Logger LOG = Logger.getLogger(GenerateTitleMetadata.class);
	private static final String VERSION_NUMBER_PREFIX = "v";
	private TitleMetadataService titleMetadataService;
	private DocMetadataService docMetadataService; 

	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}

	private String stylesheetPath;
	
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = getJobParameters(chunkContext);
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		String fullyQualifiedTitleId = jobParameters.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
		String versionNumber = VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.MAJOR_VERSION);
		
		TitleMetadata titleMetadata = new TitleMetadata(fullyQualifiedTitleId, versionNumber);
		String materialId = jobParameters.getString(JobParameterKey.MATERIAL_ID);
		
		titleMetadata.setMaterialId(StringUtils.isNotBlank(materialId) ? materialId : "1234");
		titleMetadata.setCopyright(jobParameters.getString(JobParameterKey.COPYRIGHT));
		titleMetadata.setDisplayName(jobParameters.getString(JobParameterKey.BOOK_NAME));
		
		addAuthors(jobParameters, titleMetadata);
		addArtwork(jobExecutionContext, titleMetadata);
		addAssets(jobExecutionContext, titleMetadata);
		
		long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();
		addDocuments(jobExecutionContext, titleMetadata, new Integer((int) jobId));
		addTableOfContents(jobExecutionContext, titleMetadata);
		//addStylesheet(titleMetadata);
		
		LOG.debug("Generated title metadata: " + titleMetadata);
		
		File titleXml = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.INTERMEDIATE_TITLE_XML_FILE));
		titleMetadataService.writeToFile(titleMetadata, titleXml);
		
		return ExitStatus.COMPLETED;
	}

	private void addStylesheet(TitleMetadata titleMetadata) {
		File stylesheetFile = new File(stylesheetPath);
		Asset stylesheet = titleMetadataService.createStylesheet(stylesheetFile);
		titleMetadata.getAssets().add(stylesheet);
	}

	private void addTableOfContents(ExecutionContext jobExecutionContext,
			TitleMetadata titleMetadata) {
		File tocXml = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
		//TODO: add gathered TOC to titleMetadata.
		ArrayList<TocEntry> tocEntries = titleMetadataService.createTableOfContents(tocXml);
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.setTocEntries(tocEntries);
		titleMetadata.setTableOfContents(tableOfContents);
	}

	private void addDocuments(ExecutionContext jobExecutionContext,
			TitleMetadata titleMetadata, Integer jobInstanceId) {
		File documentsDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR));
		ArrayList<DocMetadata> docMetaList = (ArrayList<DocMetadata>) docMetadataService.findOrderedDocMetadataByJobId(jobInstanceId);

		ArrayList<Doc> documents = new ArrayList<Doc>();
		for (DocMetadata docDetail : docMetaList)
		{
			Doc doc = new Doc(docDetail.getDocUuid(), docDetail.getDocUuid()+".html");
			documents.add(doc);	
		}
//		ArrayList<Doc> documents = titleMetadataService.createDocuments(documentsDirectory);
		titleMetadata.setDocuments(documents);
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

	private void addAuthors(JobParameters jobParameters,
			TitleMetadata titleMetadata) {
		String authorsParameter = jobParameters.getString(JobParameterKey.AUTHORS);
		if (StringUtils.isNotBlank(authorsParameter)){
			ArrayList<Author> authors = titleMetadataService.createAuthors(authorsParameter);
			titleMetadata.setAuthors(authors);
		}
	}

	public void setTitleMetadataService(TitleMetadataService titleMetadataService) {
		this.titleMetadataService = titleMetadataService;
	}
	
	public void setStylesheetPath(String stylesheetPath) {
		this.stylesheetPath = stylesheetPath;
	}
}
