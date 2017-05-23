package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata.TitleMetadataBuilder;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

public class CreateDirectoriesAndMoveResources extends AbstractSbTasklet
{
    /**
     * To update publishingStatsService table.
     */
    private PublishingStatsService publishingStatsService;

    private MoveResourcesUtil moveResourcesUtil;

    private TitleMetadataService titleMetadataService;

    private BookDefinitionService bookDefinitionService;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet
     * #executeStep(org.springframework.batch.core.StepContribution,
     * org.springframework.batch.core.scope.context.ChunkContext)
     */
    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobParameters jobParameters = getJobParameters(chunkContext);
        final Long jobId = getJobInstance(chunkContext).getId();
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String publishStatus = "Completed";
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final String materialId = bookDefinition.getMaterialId();
        final File coverArtFile = moveResourcesUtil.createCoverArt(jobExecutionContext);

        final TitleMetadataBuilder titleMetadataBuilder = TitleMetadata.builder(bookDefinition)
            .versionNumber(jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED))
            // TODO: verify that default of 1234 for material id is valid.
            .materialId(StringUtils.isNotBlank(materialId) ? materialId : "1234")
            .artworkFile(coverArtFile);

        OutputStream titleManifest = null;
        InputStream splitTitleXMLStream = null;
        boolean firstSplitBook;

        try
        {
            final File assembleDirectory =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_DIR));
            int parts = 0;
            if (bookDefinition.isSplitTypeAuto())
            {
                parts = bookDefinitionService.getSplitPartsForEbook(bookDefinition.getEbookDefinitionId());
            }
            else
            {
                parts = bookDefinition.getSplitEBookParts();
            }

            final String docToSplitBook =
                getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOC_TO_SPLITBOOK_FILE);

            // docMap contains SplitBook part to Doc mapping
            final Map<String, List<Doc>> docMap = new HashMap<>();
            // splitBookImgMap contains SplitBook part to Img mapping
            final Map<String, List<String>> splitBookImgMap = new HashMap<>();
            readDocImgFile(new File(docToSplitBook), docMap, splitBookImgMap);

            // Assets that are needed for all books
            addAssetsForAllBooks(jobExecutionContext, bookDefinition, titleMetadataBuilder);

            // Create title.xml and directories needed. Move content for all
            // splitBooks
            for (int i = 1; i <= parts; i++)
            {
                firstSplitBook = false;
                String splitTitle = bookDefinition.getTitleId() + "_pt" + i;
                final StringBuffer proviewDisplayName = new StringBuffer();
                proviewDisplayName.append(bookDefinition.getProviewDisplayName());
                proviewDisplayName.append(" (eBook " + i);
                proviewDisplayName.append(" of " + parts);
                proviewDisplayName.append(")");
                titleMetadataBuilder.displayName(proviewDisplayName.toString());
                titleMetadataBuilder.fullyQualifiedTitleId(fullyQualifiedTitleId + "_pt" + i);

                final String key = String.valueOf(i);

                // Add needed images corresponding to the split Book to Assets
                // imgList contains file names belong to the split book
                List<String> imgList = new ArrayList<>();

                if (splitBookImgMap.containsKey(key))
                {
                    imgList = splitBookImgMap.get(key);
                    for (final String imgFileName : imgList)
                    {
                        titleMetadataBuilder.assetFileName(imgFileName);
                    }
                }

                // Get all documents corresponding to the split Book
                List<Doc> docList = new ArrayList<>();
                if (docMap.containsKey(key))
                {
                    docList = docMap.get(key);
                }

                // Only for first split book
                if (i == 1)
                {
                    splitTitle = bookDefinition.getTitleId();
                    final List<FrontMatterPdf> pdfList = new ArrayList<>();
                    final List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
                    for (final FrontMatterPage fmp : fmps)
                    {
                        for (final FrontMatterSection fms : fmp.getFrontMatterSections())
                        {
                            pdfList.addAll(fms.getPdfs());
                        }
                    }

                    for (final FrontMatterPdf pdf : pdfList)
                    {
                        titleMetadataBuilder.assetFileName(pdf.getPdfFilename());
                    }
                    titleMetadataBuilder.fullyQualifiedTitleId(fullyQualifiedTitleId);
                    firstSplitBook = true;
                }

                final File ebookDirectory = new File(assembleDirectory, splitTitle);
                ebookDirectory.mkdir();

                final File splitTitleXml =
                    new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.SPLIT_TITLE_XML_FILE));
                splitTitleXMLStream = new FileInputStream(splitTitleXml);

                final File titleXml = new File(ebookDirectory, "title.xml");
                titleManifest = new FileOutputStream(titleXml);

                titleMetadataService.generateTitleXML(
                    titleMetadataBuilder.build(),
                    docList,
                    splitTitleXMLStream,
                    titleManifest,
                    JobExecutionKey.ALT_ID_DIR_PATH);
                moveResources(jobExecutionContext, ebookDirectory, firstSplitBook, imgList, docList, coverArtFile);
            }
        }
        catch (final Exception e)
        {
            publishStatus = "Failed";
            throw (e);
        }
        finally
        {
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

    public void moveResources(
        final ExecutionContext jobExecutionContext,
        final File ebookDirectory,
        final boolean firstSplitBook,
        final List<String> imgList,
        final List<Doc> docList,
        final File coverArtFile) throws IOException
    {
        // Move assets
        final File assetsDirectory = createAssetsDirectory(ebookDirectory);
        // static images
        final File staticImagesDir =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_DEST_DIR));
        moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);
        // Style sheets
        moveResourcesUtil.moveStylesheet(assetsDirectory);
        // Dynamic images
        final File dynamicImagesDir =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
        final List<File> dynamicImgFiles = filterFiles(dynamicImagesDir, imgList);
        moveResourcesUtil.copyFilesToDestination(dynamicImgFiles, assetsDirectory);
        // Frontmatter pdf
        moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, assetsDirectory, firstSplitBook);

        final File artworkDirectory = createArtworkDirectory(ebookDirectory);
        FileUtils.copyFileToDirectory(coverArtFile, artworkDirectory);
        moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);

        // Move Documents
        final File documentsDirectory = createDocumentsDirectory(ebookDirectory);
        if (firstSplitBook)
        {
            final File frontMatter =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR));
            moveResourcesUtil.copySourceToDestination(frontMatter, documentsDirectory);
        }

        final File transformedDocsDir = new File(
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));

        final List<String> srcIdList = new ArrayList<>();
        for (final Doc doc : docList)
        {
            srcIdList.add(doc.getSrc());
        }
        final List<File> documentFiles = filterFiles(transformedDocsDir, srcIdList);
        moveResourcesUtil.copyFilesToDestination(documentFiles, documentsDirectory);
    }

    protected List<File> filterFiles(final File directory, final List<String> fileNameList)
    {
        if (directory == null || !directory.isDirectory())
        {
            throw new IllegalArgumentException("Directory must not be null and must be a directory.");
        }
        final List<File> filter = new ArrayList<>();
        for (final File file : directory.listFiles())
        {
            if (fileNameList.contains(file.getName()))
            {
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
    public void readDocImgFile(
        final File docToSplitBook,
        final Map<String, List<Doc>> docMap,
        final Map<String, List<String>> splitBookImgMap)
    {
        String line = null;
        try (BufferedReader stream = new BufferedReader(new FileReader(docToSplitBook)))
        {
            while ((line = stream.readLine()) != null)
            {
                List<String> imgList = null;
                final String[] splitted = line.split("\\|");

                if (splitted.length == 4)
                {
                    imgList = new ArrayList<>();
                    if (splitted[3].contains(","))
                    {
                        final String[] imgStringArray = splitted[3].split(",");

                        for (final String imgId : imgStringArray)
                        {
                            imgList.add(imgId);
                        }
                    }
                    else
                    {
                        imgList.add(splitted[3]);
                    }
                }

                final String splitTitlePart = splitted[2];
                final Doc document = new Doc(splitted[0], splitted[1], Integer.parseInt(splitTitlePart), imgList);

                List<Doc> docList = null;
                if (docMap.containsKey(splitTitlePart))
                {
                    docList = docMap.get(splitTitlePart);
                }
                else
                {
                    docList = new ArrayList<>();
                }

                docList.add(document);
                docMap.put(splitTitlePart, docList);

                if (imgList != null && imgList.size() > 0)
                {
                    if (splitBookImgMap.containsKey(splitTitlePart))
                    {
                        final List<String> splitImgList = splitBookImgMap.get(splitTitlePart);
                        splitImgList.addAll(imgList);
                        splitBookImgMap.put(splitTitlePart, splitImgList);
                    }
                    else
                    {
                        splitBookImgMap.put(splitTitlePart, imgList);
                    }
                }
            }
        }
        catch (final IOException iox)
        {
            throw new RuntimeException("Unable to find File : " + docToSplitBook.getAbsolutePath() + " " + iox);
        }
    }

    /**
     * All split books gets static,.css files and frontmatter images
     *
     * @param jobExecutionContext
     * @param bookDefinition
     * @throws FileNotFoundException
     */
    protected void addAssetsForAllBooks(
        final ExecutionContext jobExecutionContext,
        final BookDefinition bookDefinition,
        final TitleMetadataBuilder builder) throws FileNotFoundException
    {
        final File staticImagesDir =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_DEST_DIR));
        final String staticContentDir =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.STATIC_CONTENT_DIR);
        builder
            .assetFilesFromDirectory(staticImagesDir)
            .assetFile(new File(staticContentDir, MoveResourcesUtil.DOCUMENT_CSS_FILE))
            .assetFile(new File(MoveResourcesUtil.EBOOK_GENERATOR_CSS_FILE));

        final File frontMatterImagesDir = new File(MoveResourcesUtil.EBOOK_GENERATOR_IMAGES_DIR);
        final List<File> filter = moveResourcesUtil.filterFiles(frontMatterImagesDir, bookDefinition);
        for (final File file : frontMatterImagesDir.listFiles())
        {
            if (!filter.contains(file))
            {
                builder.assetFileName(file.getName());
            }
        }
    }

    private File createDocumentsDirectory(final File ebookDirectory)
    {
        return new File(ebookDirectory, "documents");
    }

    private File createArtworkDirectory(final File ebookDirectory)
    {
        final File artworkDirectory = new File(ebookDirectory, "artwork");
        artworkDirectory.mkdirs();
        return artworkDirectory;
    }

    private File createAssetsDirectory(final File parentDirectory)
    {
        final File assetsDirectory = new File(parentDirectory, "assets");
        return assetsDirectory;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setTitleMetadataService(final TitleMetadataService titleMetadataService)
    {
        this.titleMetadataService = titleMetadataService;
    }

    public MoveResourcesUtil getMoveResourcesUtil()
    {
        return moveResourcesUtil;
    }

    @Required
    public void setMoveResourcesUtil(final MoveResourcesUtil moveResourcesUtil)
    {
        this.moveResourcesUtil = moveResourcesUtil;
    }

    public BookDefinitionService getBookDefinitionService()
    {
        return bookDefinitionService;
    }

    @Required
    public void setBookDefinitionService(final BookDefinitionService bookDefinitionService)
    {
        this.bookDefinitionService = bookDefinitionService;
    }
}
