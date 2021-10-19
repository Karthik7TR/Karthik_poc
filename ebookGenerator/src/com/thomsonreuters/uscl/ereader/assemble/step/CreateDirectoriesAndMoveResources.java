package com.thomsonreuters.uscl.ereader.assemble.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.proview.feature.FeaturesListBuilder;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.FileUtils;
import com.thomsonreuters.uscl.ereader.core.service.DateProvider;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata.TitleMetadataBuilder;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
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
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.FrontMatterFileName.ADDITIONAL_FRONT_MATTER;
import static com.thomsonreuters.uscl.ereader.FrontMatterFileName.FRONT_MATTER_TITLE;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.DASH;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.PNG;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.SLASH;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.TITLE_PAGE_IMAGE;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class CreateDirectoriesAndMoveResources extends BookStepImpl {
    private static final String ESCAPED_TITLE_ID_REGEX = FRONT_MATTER_TITLE + "-(?<%s>.*).html";
    private static final String ESCAPED_TITLE_ID = "escapedTitleId";
    private static final Pattern ESCAPED_TITLE_ID_PATTERN = Pattern.compile(String.format(ESCAPED_TITLE_ID_REGEX, ESCAPED_TITLE_ID));
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
    @Autowired
    private ImageFileSystem imageFileSystem;
    @Autowired
    private FormatFileSystem formatFileSystem;
    @Autowired
    private AssembleFileSystem assembleFileSystem;
    @Autowired
    private DateProvider dateProvider;
    @Autowired
    private NasFileSystem nasFileSystem;
    @Autowired
    private CoverArtUtil coverArtUtil;
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
        final Long jobId = getJobInstanceId();
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String publishStatus = "Completed";
        final BookDefinition bookDefinition = getBookDefinition();

        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final File coverArtFile = coverArtUtil.getCoverArt(bookDefinition);
        final String versionNumber = getJobParameterString(JobParameterKey.BOOK_VERSION_SUBMITTED);

        final TitleMetadataBuilder titleMetadataBuilder =
            TitleMetadata.builder(bookDefinition)
                    .versionNumber(versionNumber)
                    .artworkFile(coverArtFile)
                    .lastUpdated(dateProvider.getDate());

        OutputStream titleManifest = null;
        InputStream splitTitleXMLStream = null;

        try {
            final File assembleDirectory = assembleFileSystem.getAssembleDirectory(this);
            final File docToSplitBook = formatFileSystem.getDocToSplitBook(this);

            // docMap contains SplitBook part to Doc mapping
            final Map<String, List<Doc>> docMap = new HashMap<>();
            // splitBookImgMap contains SplitBook part to Img mapping
            final Map<String, List<String>> splitBookImgMap = new HashMap<>();
            readDocImgFile(docToSplitBook, docMap, splitBookImgMap);
            final int parts = docMap.keySet().size();
            // Assets that are needed for all books
            addAssetsForAllBooks(bookDefinition, titleMetadataBuilder);

            final FeaturesListBuilder featuresListBuilder = featuresListBuilderFactory.create(bookDefinition)
                .withBookVersion(new Version("v" + versionNumber))
                .withTitleDocs(getDocsByTitles(docMap))
                .withPageNumbers(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS))
                .withThesaurus(getJobExecutionPropertyBoolean(JobExecutionKey.WITH_THESAURUS))
                .withMinorVersionMapping(Objects.nonNull(bookDefinition.getVersionWithPreviousDocIds()));

            // Create title.xml and directories needed. Move content for all
            // splitBooks
            for (int i = 1; i <= parts; i++) {
                String splitTitle = i == 1 ? bookDefinition.getTitleId() : String.format("%s_pt%s", bookDefinition.getTitleId(), i);
                final File ebookDirectory = new File(assembleDirectory, splitTitle);
                ebookDirectory.mkdir();
                final File assetsDirectory = createAssetsDirectory(ebookDirectory);

                titleMetadataBuilder.displayName(String.format("%s (eBook %s of %s)", bookDefinition.getProviewDisplayName(), i, parts));
                if (i == 1) {
                    titleMetadataBuilder.fullyQualifiedTitleId(fullyQualifiedTitleId);
                } else {
                    titleMetadataBuilder.fullyQualifiedTitleId(String.format("%s_pt%s", fullyQualifiedTitleId, i));
                }
                final String key = String.valueOf(i);

                // Add needed images corresponding to the split Book to Assets
                // imgList contains file names belong to the split book
                final List<String> imgList = splitBookImgMap.computeIfAbsent(key, k -> new ArrayList<>());
                imgList.forEach(titleMetadataBuilder::assetFileName);

                // Get all documents corresponding to the split Book
                final List<Doc> docList = docMap.computeIfAbsent(key, k -> new ArrayList<>());

                // Only for first split book
                boolean isFrontMatterPagesExist = docList.stream().anyMatch(item -> item.getSrc().contains(ADDITIONAL_FRONT_MATTER));
                if (isFrontMatterPagesExist) {
                    bookDefinition.getFrontMatterPdfFileNames()
                        .forEach(titleMetadataBuilder::assetFileName);
                    if (bookDefinition.isCwBook()) {
                        Optional.of(formatFileSystem.getFrontMatterPdfImagesDir(this))
                                .filter(File::exists)
                                .map(FileUtils::listFiles)
                                .map(Collection::stream)
                                .orElseGet(Stream::empty)
                                .map(File::getName)
                                .forEach(titleMetadataBuilder::assetFileName);
                    }
                }
                docList.stream()
                        .map(Doc::getSrc)
                        .filter(item -> item.contains(FRONT_MATTER_TITLE))
                        .findAny()
                        .flatMap(this::findBookDefinitionForFrontMatterTitleWithImageIncluded)
                        .ifPresent(book -> {
                            File titlePageImage = new File(assetsDirectory, TITLE_PAGE_IMAGE + DASH + new TitleId(book.getFullyQualifiedTitleId()).escapeSlashWithDash() + PNG);
                            moveResourcesUtil.moveTitlePageImage(book, titlePageImage);
                            titleMetadataBuilder.assetFile(titlePageImage);
                        });
                featuresListBuilder.forTitleId(new BookTitleId(key,
                        new Version(BigInteger.ZERO, BigInteger.ZERO)));
                titleMetadataBuilder.proviewFeatures(featuresListBuilder.getFeatures());

                final File splitTitleXml = formatFileSystem.getSplitTitleXml(this);
                splitTitleXMLStream = new FileInputStream(splitTitleXml);

                final File titleXml = new File(ebookDirectory, "title.xml");
                titleManifest = new FileOutputStream(titleXml);

                titleMetadataService.generateTitleXML(
                    titleMetadataBuilder.build(),
                    docList,
                    splitTitleXMLStream,
                    titleManifest,
                    nasFileSystem.getPilotBookCsvDirectory().getAbsolutePath());
                moveResources(jobExecutionContext, ebookDirectory, assetsDirectory, isFrontMatterPagesExist, imgList, docList, coverArtFile);
                //Drop assets for current split book part
                titleMetadataBuilder.assetFileNames(null);
                addAssetsForAllBooks(bookDefinition, titleMetadataBuilder);
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

    private Optional<BookDefinition> findBookDefinitionForFrontMatterTitleWithImageIncluded(final String frontMatterSrcFilename) {
        return getCombinedBookDefinition().getSources().stream()
                .map(CombinedBookDefinitionSource::getBookDefinition)
                .filter(book -> book.getFullyQualifiedTitleId().equals(extractTitleId(frontMatterSrcFilename)))
                .filter(BookDefinition::isTitlePageImageIncluded)
                .findAny();
    }

    private String extractTitleId(final String frontMatterSrcFilename) {
        Matcher matcher = ESCAPED_TITLE_ID_PATTERN.matcher(frontMatterSrcFilename);
        if (matcher.matches()) {
            return matcher.group(ESCAPED_TITLE_ID).replace(DASH, SLASH);
        } else {
            return getBookDefinition().getFullyQualifiedTitleId();
        }
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
     * @param isFrontMatterPagesExist
     * @param imgList
     * @param docList
     * @throws IOException
     */
    public void moveResources(
        final ExecutionContext jobExecutionContext,
        final File ebookDirectory,
        final File assetsDirectory,
        final boolean isFrontMatterPagesExist,
        final List<String> imgList,
        final List<Doc> docList,
        final File coverArtFile) {
        // static images
        final File staticImagesDir = imageFileSystem.getImageStaticDirectory(this);
        moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);
        // Style sheets
        moveResourcesUtil.moveStylesheet(assetsDirectory);
        moveResourcesUtil.moveThesaurus(this, assetsDirectory);
        // Dynamic images
        final File dynamicImagesDir = imageFileSystem.getImageDynamicDirectory(this);
        final List<File> dynamicImgFiles = filterFiles(dynamicImagesDir, imgList);
        moveResourcesUtil.copyFilesToDestination(dynamicImgFiles, assetsDirectory);
        // Frontmatter pdf
        moveResourcesUtil.moveFrontMatterImages(this, assetsDirectory, isFrontMatterPagesExist);

        final File artworkDirectory = createArtworkDirectory(ebookDirectory);
        FileUtils.copyFileToDirectory(coverArtFile, artworkDirectory);
        moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);

        // Move Documents
        final File documentsDirectory = createDocumentsDirectory(ebookDirectory);
        final File transformedDocsDir = new File(getJobExecutionPropertyString(JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));

        final List<String> srcIdList = new ArrayList<>();
        for (final Doc doc : docList) {
            srcIdList.add(doc.getSrc());
        }

        final List<File> documentFiles = filterFilesFromDirectories(transformedDocsDir, formatFileSystem.getFrontMatterHtmlDir(this), srcIdList);
        moveResourcesUtil.copyFilesToDestination(documentFiles, documentsDirectory);
    }

    private List<File> filterFilesFromDirectories(final File transformedDocsDir, final File frontMatterHtmlDir,  final List<String> srcIdList) {
        return Stream.of(filterFiles(transformedDocsDir, srcIdList),
                        filterFiles(frontMatterHtmlDir, srcIdList))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
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
     * @param bookDefinition
     * @throws FileNotFoundException
     */
    protected void addAssetsForAllBooks(
        final BookDefinition bookDefinition,
        final TitleMetadataBuilder builder) {
        final File staticImagesDir = imageFileSystem.getImageStaticDirectory(this);
        final File staticContentDir = nasFileSystem.getStaticContentDirectory();
        builder.assetFilesFromDirectory(staticImagesDir)
            .assetFile(new File(staticContentDir, MoveResourcesUtil.DOCUMENT_CSS_FILE))
            .assetFile(nasFileSystem.getFrontMatterCssFile());

        final File frontMatterImagesDir = nasFileSystem.getFrontMatterImagesDirectory();
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
}
