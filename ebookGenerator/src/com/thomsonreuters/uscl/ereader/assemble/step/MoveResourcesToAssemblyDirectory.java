/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step is responsible for moving resources, identified by well-known JobExecutionKeys, to the assembly directory
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class MoveResourcesToAssemblyDirectory extends AbstractSbTasklet {

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet#executeStep(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		File ebookDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_DIRECTORY));
		File assetsDirectory = createAssetsDirectory(ebookDirectory);
		File artworkDirectory = createArtworkDirectory(ebookDirectory);
		File documentsDirectory = createDocumentsDirectory(ebookDirectory);
		
		moveCoverArt(jobExecutionContext, artworkDirectory);
		moveImages(jobExecutionContext, assetsDirectory);
		moveStylesheet(jobExecutionContext, assetsDirectory);
		moveDocuments(jobExecutionContext, documentsDirectory);
		
		return ExitStatus.COMPLETED;
	}

	private File createDocumentsDirectory(File ebookDirectory) throws IOException {
		return new File(ebookDirectory, "documents");
	}

	private File createArtworkDirectory(File ebookDirectory) {
		File artworkDirectory = new File(ebookDirectory, "artwork");
		artworkDirectory.mkdirs();
		return artworkDirectory;
	}

	private void moveCoverArt(final ExecutionContext jobExecutionContext,
			final File artworkDirectory) throws IOException {
		File coverArt = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.COVER_ART_PATH));
		FileUtils.copyFileToDirectory(coverArt, artworkDirectory);
	}

	private void moveDocuments(final ExecutionContext jobExecutionContext,
			final File documentsDirectory) throws IOException {
		File transformedDocsDir= new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
		FileUtils.copyDirectory(transformedDocsDir, documentsDirectory);
	}
	
	private void moveImages(final ExecutionContext jobExecutionContext, final File assetsDirectory) throws IOException {
		File dynamicImagesDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
		File staticImagesDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_DEST_DIR));
		FileUtils.copyDirectory(dynamicImagesDir, assetsDirectory);
		FileUtils.copyDirectory(staticImagesDir, assetsDirectory);
	}
	
	private void moveStylesheet(final ExecutionContext jobExecutionContext, final File assetsDirectory) throws IOException {
		File stylesheet = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCUMENT_CSS_FILE));
		FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
	}
	private File createAssetsDirectory (final File parentDirectory) {
		File assetsDirectory = new File(parentDirectory, "assets");
		return assetsDirectory;
	}
	
}
