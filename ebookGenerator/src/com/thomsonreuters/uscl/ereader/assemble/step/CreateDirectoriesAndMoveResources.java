package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.proview.feature.FeaturesListBuilder;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata.TitleMetadataBuilder;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class CreateDirectoriesAndMoveResources extends BookStepImpl {
    /**
     * To update publishingStatsService table.
     */
    @Autowired
    private PublishingStatsService publishingStatsService;
    @Autowired
    private MoveResourcesUtil moveResourcesUtil;
    @Autowired
    private TitleMetadataService titleMetadataService;
    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private ProviewFeaturesListBuilderFactory featuresListBuilderFactory;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet
     * #executeStep(org.springframework.batch.core.StepContribution,
     * org.springframework.batch.core.scope.context.ChunkContext)
     */
    @Override
    public ExitStatus executeStep()
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext();
        final JobParameters jobParameters = getJobParameters();
        final Long jobId = getJobInstanceId();
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String publishStatus = "Completed";
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final File coverArtFile = moveResourcesUtil.createCoverArt(jobExecutionContext);
        final String versionNumber = jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);

        final TitleMetadataBuilder titleMetadataBuilder =
            TitleMetadata.builder(bookDefinition).versionNumber(versionNumber).artworkFile(coverArtFile);

        OutputStream titleManifest = null;
        InputStream splitTitleXMLStream = null;
        boolean firstSplitBook;

        try {
            final File assembleDirectory =
                new File(getJobExecutionPropertyString(JobExecutionKey.ASSEMBLE_DIR));
            int parts = 0;
            if (bookDefinition.isSplitTypeAuto()) {
                parts = bookDefinitionService.getSplitPartsForEbook(bookDefinition.getEbookDefinitionId());
            } else {
                parts = bookDefinition.getSplitEBookParts();
            }

            final String docToSplitBook =
                    getJobExecutionPropertyString(JobExecutionKey.DOC_TO_SPLITBOOK_FILE);

            // docMap contains SplitBook part to Doc mapping
            final Map<String, List<Doc>> docMap = new HashMap<>();
            // splitBookImgMap contains SplitBook part to Img mapping
            final Map<String, List<String>> splitBookImgMap = new HashMap<>();
            readDocImgFile(new File(docToSplitBook), docMap, splitBookImgMap);

            // Assets that are needed for all books
            addAssetsForAllBooks(jobExecutionContext, bookDefinition, titleMetadataBuilder);

            final FeaturesListBuilder featuresListBuilder = featuresListBuilderFactory.create(bookDefinition)
                .withBookVersion(new Version("v" + versionNumber))
                .withTitleDocs(getDocsByTitles(docMap))
                .withPageNumbers(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS))
                .withThesaurus(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_THESAURUS));

            // Create title.xml and directories needed. Move content for all
            // splitBooks
            for (int i = 1; i <= parts; i++) {
                firstSplitBook = false;
                String splitTitle = String.format("%s_pt%s", bookDefinition.getTitleId(), i);
                titleMetadataBuilder.displayName(String.format("%s (eBook %s of %s)", bookDefinition.getProviewDisplayName(), i, parts));
                titleMetadataBuilder.fullyQualifiedTitleId(String.format("%s_pt%s", fullyQualifiedTitleId, i));

                final String key = String.valueOf(i);

                // Add needed images corresponding to the split Book to Assets
                // imgList contains file names belong to the split book
                final List<String> imgList = splitBookImgMap.computeIfAbsent(key, k -> new ArrayList<>());
                imgList.forEach(titleMetadataBuilder::assetFileName);

                // Get all documents corresponding to the split Book
                final List<Doc> docList = docMap.computeIfAbsent(key, k -> new ArrayList<>());

                // Only for first split book
                if (i == 1) {
                    splitTitle = bookDefinition.getTitleId();

                    bookDefinition.getFrontMatterPages().stream()
                        .map(FrontMatterPage::getFrontMatterSections)
                        .flatMap(Collection::stream)
                        .map(FrontMatterSection::getPdfs)
                        .flatMap(Collection::stream)
                        .map(FrontMatterPdf::getPdfFilename)
                        .forEach(titleMetadataBuilder::assetFileName);

                    titleMetadataBuilder.fullyQualifiedTitleId(fullyQualifiedTitleId);
                    firstSplitBook = true;
                }

                featuresListBuilder.forTitleId(new BookTitleId(key,
                        new Version(BigInteger.ZERO, BigInteger.ZERO)));
                titleMetadataBuilder.proviewFeatures(featuresListBuilder.getFeatures());

                final File ebookDirectory = new File(assembleDirectory, splitTitle);
                ebookDirectory.mkdir();

                final File splitTitleXml =
                    new File(getJobExecutionPropertyString(JobExecutionKey.SPLIT_TITLE_XML_FILE));
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
                //Drop assets for current split book part
                titleMetadataBuilder.assetFileNames(null);
                addAssetsForAllBooks(jobExecutionContext, bookDefinition, titleMetadataBuilder);
            }
        } catch (final Exception e) {
            publishStatus = "Failed";
            throw (e);
        } finally {
            jobstats.setPublishStatus("createDirectoriesAndMoveResources: " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    @NotNull
    private Map<BookTitleId, List<Doc>> getDocsByTitles(@NotNull final Map<String, List<Doc>> docMap) {
        return docMap.entrySet().stream()
            .collect(Collectors.toMap(entry -> new BookTitleId(entry.getKey(),
                            new Version(BigInteger.ZERO, BigInteger.ZERO)),
                Entry::getValue, (oldVal, newVal) -> newVal, HashMap::new));
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
        final File coverArtFile) throws IOException {
        // Move assets
        final File assetsDirectory = createAssetsDirectory(ebookDirectory);
        // static images
        final File staticImagesDir =
            new File(getJobExecutionPropertyString(JobExecutionKey.IMAGE_STATIC_DEST_DIR));
        moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);
        // Style sheets
        moveResourcesUtil.moveStylesheet(assetsDirectory);
        moveResourcesUtil.moveThesaurus(this, assetsDirectory);
        // Dynamic images
        final File dynamicImagesDir =
            new File(getJobExecutionPropertyString(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
        final List<File> dynamicImgFiles = filterFiles(dynamicImagesDir, imgList);
        moveResourcesUtil.copyFilesToDestination(dynamicImgFiles, assetsDirectory);
        // Frontmatter pdf
        moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, assetsDirectory, firstSplitBook);

        final File artworkDirectory = createArtworkDirectory(ebookDirectory);
        FileUtils.copyFileToDirectory(coverArtFile, artworkDirectory);
        moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);

        // Move Documents
        final File documentsDirectory = createDocumentsDirectory(ebookDirectory);
        if (firstSplitBook) {
            final File frontMatter =
                new File(getJobExecutionPropertyString(JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR));
            moveResourcesUtil.copySourceToDestination(frontMatter, documentsDirectory);
        }

        final File transformedDocsDir = new File(
                getJobExecutionPropertyString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));

        final List<String> srcIdList = new ArrayList<>();
        for (final Doc doc : docList) {
            srcIdList.add(doc.getSrc());
        }
        final List<File> documentFiles = filterFiles(transformedDocsDir, srcIdList);
        moveResourcesUtil.copyFilesToDestination(documentFiles, documentsDirectory);
    }

    protected List<File> filterFiles(final File directory, final List<String> fileNameList) {
        if (directory == null || !directory.isDirectory()) {
            throw new IllegalArgumentException("Directory must not be null and must be a directory.");
        }
        return Stream.of(directory.listFiles())
            .filter(file -> fileNameList.contains(file.getName()))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public void readDocImgFile(
        final File docToSplitBook,
        final Map<String, List<Doc>> docMap,
        final Map<String, List<String>> splitBookImgMap) {
        String line = null;
        try (BufferedReader stream = new BufferedReader(new FileReader(docToSplitBook))) {
            while ((line = stream.readLine()) != null) {
                final String[] splitted = line.split("\\|");
                List<String> imgList = null;
                if (splitted.length == 4) {
                    imgList = Stream.of(splitted[3].split(","))
                        .collect(Collectors.toCollection(ArrayList::new));
                }

                final String splitTitlePart = splitted[2];
                final Doc document = new Doc(splitted[0], splitted[1], Integer.parseInt(splitTitlePart), imgList);
                docMap.computeIfAbsent(splitTitlePart, key -> new ArrayList<>()).add(document);
                Optional.ofNullable(imgList)
                    .filter(CollectionUtils::isNotEmpty)
                    .ifPresent(list -> splitBookImgMap.computeIfAbsent(splitTitlePart, key -> new ArrayList<>()).addAll(list));
            }
        } catch (final IOException iox) {
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
        final TitleMetadataBuilder builder) throws FileNotFoundException {
        final File staticImagesDir =
            new File(getJobExecutionPropertyString(JobExecutionKey.IMAGE_STATIC_DEST_DIR));
        final String staticContentDir =
                getJobExecutionPropertyString(JobExecutionKey.STATIC_CONTENT_DIR);
        builder.assetFilesFromDirectory(staticImagesDir)
            .assetFile(new File(staticContentDir, MoveResourcesUtil.DOCUMENT_CSS_FILE))
            .assetFile(new File(MoveResourcesUtil.EBOOK_GENERATOR_CSS_FILE));

        final File frontMatterImagesDir = new File(MoveResourcesUtil.EBOOK_GENERATOR_IMAGES_DIR);
        final List<File> filter = moveResourcesUtil.filterFiles(frontMatterImagesDir, bookDefinition);
        Stream.of(frontMatterImagesDir.listFiles())
            .filter(file -> !filter.contains(file))
            .map(File::getName)
            .forEach(builder::assetFileName);
    }

    private File createDocumentsDirectory(final File ebookDirectory) {
        return new File(ebookDirectory, "documents");
    }

    private File createArtworkDirectory(final File ebookDirectory) {
        final File artworkDirectory = new File(ebookDirectory, "artwork");
        artworkDirectory.mkdirs();
        return artworkDirectory;
    }

    private File createAssetsDirectory(final File parentDirectory) {
        return new File(parentDirectory, "assets");
    }

    public void setMoveResourcesUtil(final MoveResourcesUtil moveResourcesUtil) {
        this.moveResourcesUtil = moveResourcesUtil;
    }
}
