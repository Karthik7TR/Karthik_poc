package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_MINOR_VERSIONS_MAPPING_XML_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_THESAURUS_FIELDS_XML_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_THESAURUS_TEMPLATE_XML_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_THESAURUS_XML_FILE;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.RELEASE_NOTES_HEADER;
import static java.util.Optional.ofNullable;

/**
 * This class represents the metadata within a title. Instances of this class are mutable.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@XmlRootElement(name = "ManifestMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"titleId", "titleVersion", "status", "onlineexpiration"})
public final class TitleMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    //TODO: SimpleDateFormat is not thread safe
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final Set<String> THESAURUS_FILE_NAMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            FORMAT_THESAURUS_FIELDS_XML_FILE.getName(),
            FORMAT_THESAURUS_TEMPLATE_XML_FILE.getName(),
            FORMAT_THESAURUS_XML_FILE.getName()
    )));
    @XmlElement(name = "apiVersion")
    private final String apiVersion = "v1";
    @XmlElement(name = "language")
    private final String language = "eng";
    @XmlElement(name = "status")
    private final String status = "Review";
    @XmlElement(name = "onlineexpiration")
    private final String onlineexpiration = "29991231";
    @XmlElement(name = "titleId")
    private String titleId;
    @XmlTransient
    private String titleIdCaseSensitive;
    @XmlElement(name = "titleVersion")
    private String titleVersion;
    @XmlElement(name = "lastUpdated")
    private String lastUpdated;
    @XmlElement(name = "name")
    private String displayName;
    @XmlTransient
    private String publishedDate;
    @XmlTransient
    private List<InfoField> infoFields;
    @XmlElement(name = "material")
    private String materialId;
    @XmlElement(name = "copyright")
    private String copyright;
    private String frontMatterTocLabel;
    @XmlElement(name = "isbn")
    private String isbn;
    @XmlTransient
    private List<FrontMatterPage> frontMatterPages;

    @XmlElement(name = "artwork")
    private Artwork artwork;

    @XmlTransient
    private TableOfContents tableOfContents;
    @XmlElementWrapper(name = "authors")
    @XmlElement(name = "author")
    private List<String> authorNames;
    @XmlElementWrapper(name = "docs")
    @XmlElement(name = "doc")
    private List<Doc> documents;
    @XmlElementWrapper(name = "assets")
    @XmlElement(name = "asset")
    private List<Asset> assets;
    @XmlElementWrapper(name = "features")
    @XmlElement(name = "feature")
    private List<Feature> proviewFeatures;
    @XmlElementWrapper(name = "keywords")
    @XmlElement(name = "keyword")
    private List<Keyword> keywords;
    private Boolean isPilotBook;
    @XmlTransient
    private boolean inlineToc;
    @XmlTransient
    private boolean indexIncluded;
    @XmlTransient
    private boolean isCwBook;
    @XmlTransient
    private boolean isElooseleafsEnabled;
    @XmlTransient
    private boolean isPagesEnabled;
    @XmlTransient
    private boolean isCombinedBook;

    private TitleMetadata() {
        //JaxB required default constructor
    }

    private TitleMetadata(final TitleMetadataBuilder builder) {
        titleId = builder.fullyQualifiedTitleId;
        titleIdCaseSensitive = ofNullable(builder.titleIdCaseSensitive).orElse(titleId);
        titleVersion = builder.versionNumber;
        proviewFeatures = builder.proviewFeatures;
        keywords = builder.keywords;
        publishedDate = builder.publishedDate;
        infoFields = builder.infoFields;
        isPilotBook = builder.isPilotBook;
        isbn = builder.isbn;
        materialId = builder.materialId;
        copyright = builder.copyright;
        displayName = builder.displayName;
        frontMatterTocLabel = builder.frontMatterTocLabel;
        frontMatterPages = builder.frontMatterPages;
        documents = builder.documents;
        inlineToc = builder.inlineToc;
        indexIncluded = builder.indexIncluded;
        isCwBook = builder.isCwBook;
        isElooseleafsEnabled = builder.isElooseleafsEnabled;
        isPagesEnabled = builder.isPagesEnabled;
        isCombinedBook = builder.isCombinedBook;

        authorNames = ofNullable(builder.authors)
            .filter(CollectionUtils::isNotEmpty)
            .map(Collection::stream)
            .map(stream -> stream.map(Author::getFullName))
            .orElseGet(() -> Stream.of("."))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Use this method to create simple, empty builder
     * @return
     */
    public static TitleMetadataBuilder builder() {
        return new TitleMetadataBuilder();
    }

    /**
     * Use this method to create builder, some fields will be written with values from BookDefinition.
     * If builder setter method will be called, field value will be overwritten.
     * @param bookDefinition
     * @return
     */
    public static TitleMetadataBuilder builder(final BookDefinition bookDefinition) {
        return defaultBuilder(bookDefinition)
            .frontMatterPages(bookDefinition.getFrontMatterPages());
    }

    public static TitleMetadataBuilder builder(final CombinedBookDefinition combinedBookDefinition) {
        final BookDefinition primaryBookDefinition = combinedBookDefinition.getPrimaryTitle().getBookDefinition();
        final List<FrontMatterPage> frontMatterPages = combinedBookDefinition.getOrderedBookDefinitionList().stream()
                .flatMap(bookDefinition -> bookDefinition.getFrontMatterPages().stream())
                .collect(Collectors.toList());
        return defaultBuilder(primaryBookDefinition)
                .frontMatterPages(frontMatterPages)
                .isCombinedBook(true);
    }

    @NotNull
    private static TitleMetadataBuilder defaultBuilder(BookDefinition bookDefinition) {
        return builder().fullyQualifiedTitleId(bookDefinition.getFullyQualifiedTitleId())
                .keywords(bookDefinition.getKeyWords())
                .authors(bookDefinition.getAuthors())
                .publishedDate(getFormattedPublishedDate(bookDefinition.getPublishedDate()))
                .infoFields(getInfoFields(bookDefinition))
                .isElooseleafsEnabled(bookDefinition.isELooseleafsEnabled())
                .isCwBook(bookDefinition.isCwBook())
                .isPilotBook(bookDefinition.getIsPilotBook())
                .isbn(bookDefinition.getIsbnNormalized())
                .materialId(bookDefinition.getMaterialId())
                .copyright(bookDefinition.getCopyright())
                .displayName(bookDefinition.getProviewDisplayName())
                .frontMatterTocLabel(bookDefinition.getFrontMatterTocLabel());
    }

    private static String getFormattedPublishedDate(final Date publishedDate) {
        String formattedPublishedDate = null;
        if (publishedDate != null) {
            formattedPublishedDate = DATE_FORMAT.format(publishedDate);
        }
        return formattedPublishedDate;
    }

    private static List<InfoField> getInfoFields(final BookDefinition bookDefinition) {
        final List<InfoField> infoFields = new ArrayList<>();
        String releaseNotesText = bookDefinition.getReleaseNotes();
        if (!StringUtils.isBlank(releaseNotesText)) {
            InfoField releaseNotes = new InfoField(RELEASE_NOTES_HEADER,
                    releaseNotesText);
            infoFields.add(releaseNotes);
        }
        return infoFields;
    }

    /**
     * Builder contain logic of creation of assets and artwork
     */
    public static final class TitleMetadataBuilder {
        private static final String VERSION_NUMBER_PREFIX = "v";

        private String fullyQualifiedTitleId;
        private String titleIdCaseSensitive;
        private String versionNumber;
        private List<Feature> proviewFeatures;
        private List<Keyword> keywords;
        private List<Author> authors;
        private String publishedDate;
        private List<InfoField> infoFields;
        private boolean isPilotBook;
        private String isbn;
        private String materialId;
        private String copyright;
        private String displayName;
        private String frontMatterTocLabel;
        private List<FrontMatterPage> frontMatterPages;
        private String artworkFileName;
        private Set<String> assetFileNames;
        private List<Doc> documents;
        private boolean inlineToc;
        private boolean indexIncluded;
        private boolean isCwBook;
        private boolean isElooseleafsEnabled;
        private boolean isPagesEnabled;
        private boolean isCombinedBook;
        private Date lastUpdated;

        private TitleMetadataBuilder() {
            //No instances from the outside
        }

        @NotNull
        public TitleMetadataBuilder fullyQualifiedTitleId(@NotNull final String fullyQualifiedTitleId) {
            this.fullyQualifiedTitleId = fullyQualifiedTitleId;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder titleIdCaseSensitive(@NotNull final String titleIdCaseSensitive) {
            this.titleIdCaseSensitive = titleIdCaseSensitive;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder versionNumber(@NotNull final String versionNumber) {
            this.versionNumber = VERSION_NUMBER_PREFIX + versionNumber;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder proviewFeatures(@NotNull final List<Feature> proviewFeatures) {
            this.proviewFeatures = proviewFeatures;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder keywords(@NotNull final List<Keyword> keywords) {
            this.keywords = keywords;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder authors(@NotNull final List<Author> authors) {
            this.authors = authors;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder publishedDate(final String publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder infoFields(@NotNull final List<InfoField> infoFields) {
            this.infoFields = infoFields;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder isPilotBook(@NotNull final boolean isPilotBook) {
            this.isPilotBook = isPilotBook;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder isbn(@NotNull final String isbn) {
            this.isbn = isbn;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder materialId(@NotNull final String materialId) {
            this.materialId = materialId;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder copyright(@NotNull final String copyright) {
            this.copyright = copyright;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder displayName(@NotNull final String displayName) {
            this.displayName = displayName;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder frontMatterTocLabel(@NotNull final String frontMatterTocLabel) {
            this.frontMatterTocLabel = frontMatterTocLabel;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder frontMatterPages(@NotNull final List<FrontMatterPage> frontMatterPages) {
            this.frontMatterPages = frontMatterPages;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder artworkFile(@NotNull final File artworkFile) {
            checkFile(artworkFile, File::exists, String.format("coverImage must not be null and must exists [%s].", artworkFile));
            artworkFileName = artworkFile.getName();
            return this;
        }

        @NotNull
        public TitleMetadataBuilder artworkFileName(@NotNull final String artworkFileName) {
            this.artworkFileName = ofNullable(artworkFileName)
                .filter(StringUtils::isNotEmpty)
                .orElseThrow(() -> new IllegalArgumentException("coverImage must not be blank"));
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFilesFromDirectory(@NotNull final File assetDirectory) {
            checkFile(assetDirectory, File::isDirectory, "Directory must not be null and must be a directory.");
            Stream.of(assetDirectory.listFiles())
                .map(File::getName)
                .filter(this::filesFilterFromAssets)
                .forEach(this::assetFileName);
            return this;
        }

        private boolean filesFilterFromAssets(final String fileName) {
            return !THESAURUS_FILE_NAMES.contains(fileName) && !ASSEMBLE_MINOR_VERSIONS_MAPPING_XML_FILE.getName().equals(fileName);
        }

        @NotNull
        public TitleMetadataBuilder assetFile(@NotNull final File assetFile) {
            checkFile(assetFile, File::exists, "File must not be null and should exist.");
            assetFileName(assetFile.getName());
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFileName(@NotNull final String assetFileName) {
            assetFileNames = ofNullable(assetFileNames).orElseGet(TreeSet::new);
            assetFileNames.add(assetFileName);
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFileNames(@NotNull final Set<String> assetFileNames) {
            this.assetFileNames = assetFileNames;
            return this;
        }

        public TitleMetadataBuilder documents(@NotNull final List<Doc> documents) {
            this.documents = documents;
            return this;
        }

        public TitleMetadataBuilder inlineToc(final boolean inlineToc) {
            this.inlineToc = inlineToc;
            return this;
        }

        public TitleMetadataBuilder indexIncluded(final boolean indexIncluded) {
            this.indexIncluded = indexIncluded;
            return this;
        }

        public TitleMetadataBuilder isCwBook(final boolean isCwBook) {
            this.isCwBook = isCwBook;
            return this;
        }

        public TitleMetadataBuilder isElooseleafsEnabled(final boolean isElooseleafsEnabled) {
            this.isElooseleafsEnabled = isElooseleafsEnabled;
            return this;
        }

        public TitleMetadataBuilder isCombinedBook(final boolean isCombinedBook) {
            this.isCombinedBook = isCombinedBook;
            return this;
        }

        public TitleMetadataBuilder isPagesEnabled(final boolean isPagesEnabled) {
            this.isPagesEnabled = isPagesEnabled;
            return this;
        }

        public TitleMetadataBuilder lastUpdated(final Date lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        @NotNull
        public TitleMetadata build() {
            final TitleMetadata titleMetadata = new TitleMetadata(this);
            createLastUpdated(titleMetadata);
            createArtwork(titleMetadata);
            createAssets(titleMetadata);
            return titleMetadata;
        }

        private void createLastUpdated(final TitleMetadata titleMetadata) {
            if (this.lastUpdated == null) {
                lastUpdated = new Date();
            }
            titleMetadata.lastUpdated = DATE_FORMAT.format(lastUpdated);
        }

        private void createArtwork(final TitleMetadata titleMetadata) {
            if (StringUtils.isNoneBlank(artworkFileName)) {
                final Artwork artwork = new Artwork(artworkFileName);
                titleMetadata.setArtwork(artwork);
            }
        }

        private void createAssets(final TitleMetadata titleMetadata) {
            if (assetFileNames != null && !assetFileNames.isEmpty()) {
                final List<Asset> assets = assetFileNames.stream()
                    .map(assetName -> new Asset(StringUtils.substringBeforeLast(assetName, "."), assetName))
                    .collect(Collectors.toCollection(ArrayList::new));
                titleMetadata.setAssets(assets);
            }
        }

        private void checkFile(final File file, final Predicate<File> predicate, final String message) {
            ofNullable(file)
                .filter(predicate)
                .orElseThrow(() ->new IllegalArgumentException(message));
        }
    }
}
