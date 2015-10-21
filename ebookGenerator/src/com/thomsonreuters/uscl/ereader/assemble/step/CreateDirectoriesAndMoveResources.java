/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class CreateDirectoriesAndMoveResources extends AbstractSbTasklet {

	/**
	 * To update publishingStatsService table.
	 */
	private PublishingStatsService publishingStatsService;
	private static final String VERSION_NUMBER_PREFIX = "v";

	private MoveResourcesUtil moveResourcesUtil;

	private TitleMetadataService titleMetadataService;	
	
	BookDefinitionService bookDefinitionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet
	 * #executeStep(org.springframework.batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParameters = getJobParameters(chunkContext);
		Long jobId = getJobInstance(chunkContext).getId();
		PublishingStats jobstats = new PublishingStats();
		jobstats.setJobInstanceId(jobId);
		String publishStatus = "Completed";
		BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);

		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String versionNumber = VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
		TitleMetadata titleMetadata = new TitleMetadata(fullyQualifiedTitleId, versionNumber,
				bookDefinition.getProviewFeatures(), bookDefinition.getKeyWords(), bookDefinition.getAuthors(),
				bookDefinition.getIsPilotBook(), bookDefinition.getIsbnNormalized());
		String materialId = bookDefinition.getMaterialId();

		// TODO: verify that default of 1234 for material id is valid.
		titleMetadata.setMaterialId(StringUtils.isNotBlank(materialId) ? materialId : "1234");
		titleMetadata.setCopyright(bookDefinition.getCopyright());
		titleMetadata.setFrontMatterTocLabel(bookDefinition.getFrontMatterTocLabel());
		titleMetadata.setFrontMatterPages(bookDefinition.getFrontMatterPages());

		File coverArtFile = addArtwork(jobExecutionContext, titleMetadata);

		OutputStream titleManifest = null;
		InputStream splitTitleXMLStream = null;
		boolean firstSplitBook;

		try {

			File assembleDirectory = new File(getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.ASSEMBLE_DIR));
			int parts = 0;
			if(bookDefinition.isSplitTypeAuto()){
				parts = bookDefinitionService.getSplitPartsForEbook(bookDefinition.getEbookDefinitionId());
			}
			else{
				parts = bookDefinition.getSplitEBookParts();
			}
			
			String docToSplitBook = getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.DOC_TO_SPLITBOOK_FILE);

			// docMap contains SplitBook part to Doc mapping
			Map<String, List<Doc>> docMap = new HashMap<String, List<Doc>>();
			// splitBookImgMap contains SplitBook part to Img mapping
			Map<String, List<String>> splitBookImgMap = new HashMap<String, List<String>>();
			readDocImgFile(new File(docToSplitBook), docMap, splitBookImgMap);

			// Assets that are needed for all books
			ArrayList<Asset> assetsForAllbooks = getAssestsListForAllBooks(jobExecutionContext, bookDefinition);

			// Create title.xml and directories needed. Move content for all
			// splitBooks
			for (int i = 1; i <= parts; i++) {
				firstSplitBook = false;
				String splitTitle = bookDefinition.getTitleId() + "_pt" + i;				
				StringBuffer proviewDisplayName = new StringBuffer();
				proviewDisplayName.append(bookDefinition.getProviewDisplayName());
				proviewDisplayName.append(" (eBook "+i);
				proviewDisplayName.append(" of "+parts);
				proviewDisplayName.append(")");
				titleMetadata.setDisplayName(proviewDisplayName.toString());
				titleMetadata.setTitleId(fullyQualifiedTitleId+ "_pt" + i);

				String key = String.valueOf(i);

				// Add needed images corresponding to the split Book to Assets
				// list				
				ArrayList<Asset> assetsForSplitBook = new ArrayList<Asset>();
				assetsForSplitBook.addAll(assetsForAllbooks);
				// imgList contains file names belong to the split book
				List<String> imgList = new ArrayList<String>();
				
				if (splitBookImgMap.containsKey(key)) {
					imgList = splitBookImgMap.get(key);
					for (String imgFileName : imgList) {
						Asset asset = new Asset(StringUtils.substringBeforeLast(imgFileName, "."), imgFileName);
						assetsForSplitBook.add(asset);
					}
				}
				
				// Get all documents corresponding to the split Book
				List<Doc> docList = new ArrayList<Doc>();
				if (docMap.containsKey(key)) {
					docList = docMap.get(key);
				}
				
				// Only for first split book
				if (i == 1) {
					splitTitle = bookDefinition.getTitleId();
					ArrayList<FrontMatterPdf> pdfList = new ArrayList<FrontMatterPdf>();
					List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
					for (FrontMatterPage fmp : fmps) {
						for (FrontMatterSection fms : fmp.getFrontMatterSections()) {
							pdfList.addAll(fms.getPdfs());
						}
					}

					for (FrontMatterPdf pdf : pdfList) {
						Asset asset = new Asset(StringUtils.substringBeforeLast(pdf.getPdfFilename(), "."),
								pdf.getPdfFilename());
						assetsForSplitBook.add(asset);
					}
					titleMetadata.setTitleId(fullyQualifiedTitleId);
					firstSplitBook = true;
				}
				

				titleMetadata.setAssets(assetsForSplitBook);

				File ebookDirectory = new File(assembleDirectory, splitTitle);
				ebookDirectory.mkdir();

				File splitTitleXml = new File(getRequiredStringProperty(jobExecutionContext,
						JobExecutionKey.SPLIT_TITLE_XML_FILE));
				splitTitleXMLStream = new FileInputStream(splitTitleXml);

				File titleXml = new File(ebookDirectory, "title.xml");
				titleManifest = new FileOutputStream(titleXml);

				titleMetadataService.generateTitleXML(titleMetadata, docList, splitTitleXMLStream, titleManifest,
						JobExecutionKey.ALT_ID_DIR_PATH);
				moveResouces(jobExecutionContext, ebookDirectory, firstSplitBook, imgList, docList, coverArtFile);

			}
		} catch (Exception e) {
			publishStatus = "Failed";
			throw (e);
		} finally {
			jobstats.setPublishStatus("createDirectoriesAndMoveResources: " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}

		return ExitStatus.COMPLETED;
	}
	
	/**
	 * Move resources to appropriate splitbook 
	 * @param jobExecutionContext
	 * @param ebookDirectory
	 * @param firstSplitBook
	 * @param imgList
	 * @param docList
	 * @throws IOException
	 */

	public void moveResouces(final ExecutionContext jobExecutionContext, File ebookDirectory,
			boolean firstSplitBook, List<String> imgList, List<Doc> docList, File coverArtFile) throws IOException {
		// Move assets
		File assetsDirectory = createAssetsDirectory(ebookDirectory);
		// static images
		File staticImagesDir = new File(getRequiredStringProperty(jobExecutionContext,
				JobExecutionKey.IMAGE_STATIC_DEST_DIR));
		moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);
		// Style sheets
		moveResourcesUtil.moveStylesheet(jobExecutionContext, assetsDirectory);
		// Dynamic images
		File dynamicImagesDir = new File(getRequiredStringProperty(jobExecutionContext,
				JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
		List<File> dynamicImgFiles = filterFiles(dynamicImagesDir, imgList);
		moveResourcesUtil.copyFilesToDestination(dynamicImgFiles, assetsDirectory);
		// Frontmatter pdf
		moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, assetsDirectory, firstSplitBook);

		File artworkDirectory = createArtworkDirectory(ebookDirectory);
		FileUtils.copyFileToDirectory(coverArtFile, artworkDirectory);
		moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);

		// Move Documents
		File documentsDirectory = createDocumentsDirectory(ebookDirectory);
		if (firstSplitBook) {
			File frontMatter = new File(getRequiredStringProperty(jobExecutionContext,
					JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR));
			moveResourcesUtil.copySourceToDestination(frontMatter, documentsDirectory);
		}

		File transformedDocsDir = new File(getRequiredStringProperty(jobExecutionContext,
				JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));

		List<String> srcIdList = new ArrayList<String>();
		for (Doc doc : docList) {
			srcIdList.add(doc.getSrc());
		}
		List<File> documentFiles = filterFiles(transformedDocsDir, srcIdList);
		moveResourcesUtil.copyFilesToDestination(documentFiles, documentsDirectory);
	}

	protected List<File> filterFiles(File directory, List<String> fileNameList) {
		if (directory == null || !directory.isDirectory()) {
			throw new IllegalArgumentException("Directory must not be null and must be a directory.");
		}
		List<File> filter = new ArrayList<File>();
		for (File file : directory.listFiles()) {
			if (fileNameList.contains(file.getName())) {
				filter.add(file);
			}
		}
		return filter;
	}

	/**
	 * @param fileName
	 *            contains altId for corresponding Guid
	 * @return a map (Guid as a Key and altId as a Value)
	 */
	public void readDocImgFile(final File docToSplitBook, Map<String, List<Doc>> docMap,
			Map<String, List<String>> splitBookImgMap) {
		String line = null;
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(docToSplitBook));

			while ((line = stream.readLine()) != null) {
				List<String> imgList = null;
				String[] splitted = line.split("\\|");

				if (splitted.length == 4) {
					imgList = new ArrayList<String>();
					if (splitted[3].contains(",")) {
						String[] imgStringArray = splitted[3].split(",");

						for (String imgId : imgStringArray) {
							imgList.add(imgId);
						}
					} else {
						imgList.add(splitted[3]);
					}

				}

				String splitTitlePart = splitted[2];
				Doc document = new Doc(splitted[0], splitted[1], Integer.parseInt(splitTitlePart), imgList);

				List<Doc> docList = null;
				if (docMap.containsKey(splitTitlePart)) {
					docList = docMap.get(splitTitlePart);
				} else {
					docList = new ArrayList<Doc>();

				}

				docList.add(document);
				docMap.put(splitTitlePart, docList);

				if (imgList != null && imgList.size() > 0) {
					if (splitBookImgMap.containsKey(splitTitlePart)) {
						List<String> splitImgList = splitBookImgMap.get(splitTitlePart);
						splitImgList.addAll(imgList);
						splitBookImgMap.put(splitTitlePart, splitImgList);
					} else {
						splitBookImgMap.put(splitTitlePart, imgList);
					}
				}

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

	/**
	 * All split books gets static,.css files and fronmatter images
	 * 
	 * @param jobExecutionContext
	 * @param bookDefinition
	 */
	protected ArrayList<Asset> getAssestsListForAllBooks(final ExecutionContext jobExecutionContext,
			BookDefinition bookDefinition) {
		ArrayList<Asset> assests = new ArrayList<Asset>();
		File staticImagesDir = new File(getRequiredStringProperty(jobExecutionContext,
				JobExecutionKey.IMAGE_STATIC_DEST_DIR));
		assests.addAll(getAssestsfromDirectories(staticImagesDir));
		File stylesheet = new File(MoveResourcesUtil.DOCUMENT_CSS_FILE);
		assests.add(getAssestsfromFile(stylesheet));
		stylesheet = new File(MoveResourcesUtil.EBOOK_GENERATOR_CSS_FILE);
		assests.add(getAssestsfromFile(stylesheet));

		File frontMatterImagesDir = new File(MoveResourcesUtil.EBOOK_GENERATOR_IMAGES_DIR);
		List<File> filter = moveResourcesUtil.filterFiles(frontMatterImagesDir, bookDefinition);
		for (File file : frontMatterImagesDir.listFiles()) {
			if (!filter.contains(file)) {
				Asset asset = new Asset(StringUtils.substringBeforeLast(file.getName(), "."), file.getName());
				assests.add(asset);
			}

		}
		return assests;
	}

	/**
	 * Add only image files that are required.
	 * 
	 * @param frontMatterImagesDir
	 * @param bookDefinition
	 * @return
	 */
	public List<Asset> getAssestsfromDirectories(File directory) {
		List<Asset> assests = new ArrayList<Asset>();
		if (directory == null || !directory.isDirectory()) {
			throw new IllegalArgumentException("Directory must not be null and must be a directory.");
		}
		for (File file : directory.listFiles()) {
			Asset asset = new Asset(StringUtils.substringBeforeLast(file.getName(), "."), file.getName());
			assests.add(asset);

		}

		return assests;

	}

	private File addArtwork(ExecutionContext jobExecutionContext, TitleMetadata titleMetadata) {
		File coverArtFile = moveResourcesUtil.createCoverArt(jobExecutionContext);
		Artwork coverArt = titleMetadataService.createArtwork(coverArtFile); 
		titleMetadata.setArtwork(coverArt);
		return coverArtFile;
	}

	/**
	 * Add only image files that are required.
	 * 
	 * @param frontMatterImagesDir
	 * @param bookDefinition
	 * @return
	 */
	public Asset getAssestsfromFile(File file) {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("File must not be null and should exist.");
		}

		Asset asset = new Asset(StringUtils.substringBeforeLast(file.getName(), "."), file.getName());

		return asset;

	}

	private File createDocumentsDirectory(File ebookDirectory) throws IOException {
		return new File(ebookDirectory, "documents");
	}

	private File createArtworkDirectory(File ebookDirectory) {
		File artworkDirectory = new File(ebookDirectory, "artwork");
		artworkDirectory.mkdirs();
		return artworkDirectory;
	}

	private File createAssetsDirectory(final File parentDirectory) {
		File assetsDirectory = new File(parentDirectory, "assets");
		return assetsDirectory;
	}

	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	@Required
	public void setTitleMetadataService(TitleMetadataService titleMetadataService) {
		this.titleMetadataService = titleMetadataService;
	}

	public MoveResourcesUtil getMoveResourcesUtil() {
		return moveResourcesUtil;
	}

	@Required
	public void setMoveResourcesUtil(MoveResourcesUtil moveResourcesUtil) {
		this.moveResourcesUtil = moveResourcesUtil;
	}

	public BookDefinitionService getBookDefinitionService() {
		return bookDefinitionService;
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}

}
