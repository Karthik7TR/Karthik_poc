package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterTitlePageFilter;

public class MoveResourcesUtil {

	/**
	 * The file path of the user generated files for front matter pdfs.
	 */
	private static final String EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH = "/apps/eBookBuilder/generator/images/pdf/";
	/**
	 * The directory of the static files for front matter logos and keycite
	 * logo.
	 */
	public static final String EBOOK_GENERATOR_IMAGES_DIR = "/apps/eBookBuilder/coreStatic/images";
	/**
	 * The file path to the CSS file to apply on the documents.
	 */
	public static final String DOCUMENT_CSS_FILE = "/apps/eBookBuilder/staticContent/document.css";
	/**
	 * The file path to the ebookGenerator CSS file used by front matter.
	 */
	public static final String EBOOK_GENERATOR_CSS_FILE = "/apps/eBookBuilder/coreStatic/css/ebook_generator.css";
	/**
	 * The file path to the ebookGenerator Cover Image.
	 */
	public static final String EBOOK_COVER_FILEPATH = "/apps/eBookBuilder/generator/images/cover/";

	/**
	 * The default file to the ebookGenerator Cover Image.
	 */
	public static final String DEFAULT_EBOOK_COVER_FILE = "/apps/eBookBuilder/staticContent/coverArt.PNG";

	public void moveCoverArt(final ExecutionContext jobExecutionContext, final File artworkDirectory)
			throws IOException {		
		File coverArt = createCoverArt(jobExecutionContext);
		FileUtils.copyFileToDirectory(coverArt, artworkDirectory);
	}
	
	public File createCoverArt(final ExecutionContext jobExecutionContext){
		BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		String titleCover = bookDefinition.getCoverImage();

		File coverArt = new File(EBOOK_COVER_FILEPATH + titleCover);
		if (!coverArt.exists()) {
			coverArt = new File(DEFAULT_EBOOK_COVER_FILE);
		}
		jobExecutionContext.putString(JobExecutionKey.COVER_ART_PATH, coverArt.getAbsolutePath());
		return coverArt;
	}

	public void copySourceToDestination(final File sourceDir, final File destinationDirectory) throws IOException {
		FileUtils.copyDirectory(sourceDir, destinationDirectory);
	}
	
	public void copyFilesToDestination(final List<File> fileList, final File destinationDirectory) throws IOException {
		for(File file : fileList){
			FileUtils.copyFileToDirectory(file, destinationDirectory);
		}
		
	}

	public void moveFrontMatterImages(final ExecutionContext jobExecutionContext, final File assetsDirectory,
			boolean move) throws IOException {

		BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		File frontMatterImagesDir = new File(EBOOK_GENERATOR_IMAGES_DIR);

		List<File> filter = filterFiles(frontMatterImagesDir, bookDefinition);

		for (File file : frontMatterImagesDir.listFiles()) {
			if (!filter.contains(file)) {
				FileUtils.copyFileToDirectory(file, assetsDirectory);
			}

		}

		if (move) {
			ArrayList<FrontMatterPdf> pdfList = new ArrayList<FrontMatterPdf>();
			List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
			for (FrontMatterPage fmp : fmps) {
				for (FrontMatterSection fms : fmp.getFrontMatterSections()) {
					pdfList.addAll(fms.getPdfs());
				}
			}

			for (FrontMatterPdf pdf : pdfList) {
				File pdfFile = new File(EBOOK_FRONT_MATTER_PDF_IMAGES_FILEPATH + pdf.getPdfFilename());
				FileUtils.copyFileToDirectory(pdfFile, assetsDirectory);
			}
		}
	}

	/**
	 * Add only image files that are required.
	 * 
	 * @param frontMatterImagesDir
	 * @param bookDefinition
	 * @return
	 */
	public List<File> filterFiles(File frontMatterImagesDir, BookDefinition bookDefinition) {
		List<File> filter = new ArrayList<File>();

		for (File file : frontMatterImagesDir.listFiles()) {
			if (!bookDefinition.getFrontMatterTheme().equalsIgnoreCase(FrontMatterTitlePageFilter.AAJ_PRESS_THEME)
					&& file.getName().startsWith("AAJ")) {
				filter.add(file);
			}
			if (!bookDefinition.getKeyciteToplineFlag() && file.getName().startsWith("keycite")) {
				filter.add(file);
			}
		}

		return filter;

	}

	protected void moveStylesheet(final ExecutionContext jobExecutionContext, final File assetsDirectory)
			throws IOException {
		File stylesheet = new File(DOCUMENT_CSS_FILE);
		FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);
		stylesheet = new File(EBOOK_GENERATOR_CSS_FILE);
		FileUtils.copyFileToDirectory(stylesheet, assetsDirectory);

	}

}
