/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * This step is responsible for moving resources, identified by well-known JobExecutionKeys, to the assembly directory
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class MoveResourcesToAssemblyDirectory extends AbstractSbTasklet {
	/**
	 * The file path of the user generated files for front matter pdfs.
	 */
	private static final String EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH = "/apps/eBookBuilder/generator/images/pdf/";
	/**
	 * The directory of the static files for front matter logos and keycite logo.
	 */
	private static final String EBOOK_GENERATOR_IMAGES_DIR = "/apps/eBookBuilder/coreStatic/images";
	/**
	 * The file path to the CSS file to apply on the documents.
	 */
	private static final String DOCUMENT_CSS_FILE = "/apps/eBookBuilder/staticContent/document.css";
	/**
	 * The file path to the ebookGenerator CSS file used by front matter.
	 */
	private static final String EBOOK_GENERATOR_CSS_FILE = "/apps/eBookBuilder/coreStatic/css/ebook_generator.css";
	/**
	 * The file path to the ebookGenerator Cover Image.
	 */
	private static final String EBOOK_COVER_FILEPATH = "/apps/eBookBuilder/generator/images/cover/";
	
	/**
	 * The default file to the ebookGenerator Cover Image.
	 */
	private static final String DEFAULT_EBOOK_COVER_FILE = "/apps/eBookBuilder/staticContent/coverArt.PNG";

	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet#executeStep(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		File ebookDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey  .EBOOK_DIRECTORY));
		File assetsDirectory = createAssetsDirectory(ebookDirectory);
		File artworkDirectory = createArtworkDirectory(ebookDirectory);
		File documentsDirectory = createDocumentsDirectory(ebookDirectory);
		
		moveCoverArt(jobExecutionContext, artworkDirectory);
		moveImages(jobExecutionContext, assetsDirectory);
		moveFrontMatterImages(jobExecutionContext, assetsDirectory);
		moveStylesheet(jobExecutionContext, assetsDirectory);
		moveFrontMatterHTML(jobExecutionContext, documentsDirectory);
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
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		String titleCover = bookDefinition.getCoverImage();
		
		File coverArt = new File(EBOOK_COVER_FILEPATH+titleCover);
		if (!coverArt.exists())
		{ 
			coverArt = new File(DEFAULT_EBOOK_COVER_FILE);			
		}
		jobExecutionContext.putString(
					JobExecutionKey.COVER_ART_PATH, coverArt.getAbsolutePath());		

		FileUtils.copyFileToDirectory(coverArt, artworkDirectory);
	}
	
	private void moveFrontMatterHTML(final ExecutionContext jobExecutionContext,
			final File documentsDirectory) throws IOException {
		File frontMatter = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR));
		FileUtils.copyDirectory(frontMatter, documentsDirectory);
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
	
	private void moveFrontMatterImages(final ExecutionContext jobExecutionContext,final File assetsDirectory) throws IOException {
		File frontMatterImagesDir = new File(EBOOK_GENERATOR_IMAGES_DIR);
		FileUtils.copyDirectory(frontMatterImagesDir, assetsDirectory);

		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		 
		ArrayList<FrontMatterPdf> pdfList = new ArrayList<FrontMatterPdf>();
		 List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		 for (FrontMatterPage fmp : fmps)
			 {
			 for (FrontMatterSection fms : fmp.getFrontMatterSections())
			 	{
					 pdfList.addAll(fms.getPdfs());
			 	}
			 }
		 
		for (FrontMatterPdf pdf : pdfList)
		{
			File pdfFile = new File(EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH+pdf.getPdfFilename());
			FileUtils.copyFileToDirectory(pdfFile, assetsDirectory);
		}
	}

	
	private void moveStylesheet(final ExecutionContext jobExecutionContext, final File assetsDirectory) throws IOException {
		File stylesheet = new File(DOCUMENT_CSS_FILE);
		FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
		stylesheet = new File(EBOOK_GENERATOR_CSS_FILE);
		FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);

	}
	private File createAssetsDirectory (final File parentDirectory) {
		File assetsDirectory = new File(parentDirectory, "assets");
		return assetsDirectory;
	}
	
}
