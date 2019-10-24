package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

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
    @XmlElement(name = "titleVersion")
    private String titleVersion;
    @XmlElement(name = "lastUpdated")
    private String lastUpdated;
    @XmlElement(name = "name")
    private String displayName;
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

    private TitleMetadata() {
        //JaxB required default constructor
    }

    private TitleMetadata(final TitleMetadataBuilder builder) {
        titleId = builder.fullyQualifiedTitleId;
        titleVersion = builder.versionNumber;
        lastUpdated = DATE_FORMAT.format(new Date());
        proviewFeatures = builder.proviewFeatures;
        keywords = builder.keywords;
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

        authorNames = Optional.ofNullable(builder.authors)
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
        return builder().fullyQualifiedTitleId(bookDefinition.getFullyQualifiedTitleId())
            .keywords(bookDefinition.getKeyWords())
            .authors(bookDefinition.getAuthors())
            .isPilotBook(bookDefinition.getIsPilotBook())
            .isbn(bookDefinition.getIsbnNormalized())
            .materialId(bookDefinition.getMaterialId())
            .copyright(bookDefinition.getCopyright())
            .displayName(bookDefinition.getProviewDisplayName())
            .frontMatterTocLabel(bookDefinition.getFrontMatterTocLabel())
            .frontMatterPages(bookDefinition.getFrontMatterPages());
    }

    /**
     * Builder contain logic of creation of assets and artwork
     */
    public static final class TitleMetadataBuilder {
        private static final String VERSION_NUMBER_PREFIX = "v";

        private String fullyQualifiedTitleId;
        private String versionNumber;
        private List<Feature> proviewFeatures;
        private List<Keyword> keywords;
        private List<Author> authors;
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

        private TitleMetadataBuilder() {
            //No instances from the outside
        }

        @NotNull
        public TitleMetadataBuilder fullyQualifiedTitleId(@NotNull final String fullyQualifiedTitleId) {
            this.fullyQualifiedTitleId = fullyQualifiedTitleId;
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
            this.artworkFileName = Optional.ofNullable(artworkFileName)
                .filter(StringUtils::isNotEmpty)
                .orElseThrow(() -> new IllegalArgumentException("coverImage must not be blank"));
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFilesFromDirectory(@NotNull final File assetDirectory) {
            checkFile(assetDirectory, File::isDirectory, "Directory must not be null and must be a directory.");
            Stream.of(assetDirectory.listFiles())
                .map(File::getName)
                .forEach(this::assetFileName);
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFile(@NotNull final File assetFile) {
            checkFile(assetFile, File::exists, "File must not be null and should exist.");
            assetFileName(assetFile.getName());
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFileName(@NotNull final String assetFileName) {
            assetFileNames = Optional.ofNullable(assetFileNames).orElseGet(TreeSet::new);
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

        @NotNull
        public TitleMetadata build() {
            final TitleMetadata titleMetadata = new TitleMetadata(this);
            createArtwork(titleMetadata);
            createAssets(titleMetadata);
            return titleMetadata;
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
            Optional.ofNullable(file)
                .filter(predicate)
                .orElseThrow(() ->new IllegalArgumentException(message));
        }
    }
}
