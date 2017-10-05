package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the metadata within a title. Instances of this class are mutable.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@XmlRootElement(name = "ManifestMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleMetadata implements Serializable {
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
    private boolean isPilotBook;

    //TODO: two constructors below used only in tests, change their access level to private, or remove (keep default for jaxb)
    public TitleMetadata() {
        addDefaults();
    }

    public TitleMetadata(final String titleId, final String titleVersion) {
        this.titleId = titleId;
        this.titleVersion = titleVersion;
        lastUpdated = DATE_FORMAT.format(new Date());

        addDefaults();
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

        authorNames = new ArrayList<>();
        if (builder.authors == null || builder.authors.isEmpty()) {
            authorNames.add(".");
        } else {
            for (final Author author : builder.authors) {
                authorNames.add(author.getFullName());
            }
        }
    }

    private void addDefaults() {
        proviewFeatures = new ArrayList<>();
        proviewFeatures.add(new Feature("AutoUpdate"));
        proviewFeatures.add(new Feature("SearchIndex"));
        proviewFeatures.add(new Feature("OnePassSSO", "www.westlaw.com"));

        keywords = new ArrayList<>();
        keywords.add(new Keyword("publisher", "Thomson Reuters"));
        keywords.add(new Keyword("jurisdiction", ".")); //TODO: Confirm with the business exactly how they want to use jurisdiction.
    }

    public void addFeature(final String featureName) {
        proviewFeatures.add(new Feature(featureName));
    }

    public void addFeature(final String featureName, final String featureValue) {
        proviewFeatures.add(new Feature(featureName, featureValue));
    }

    public void setAuthors(final List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setDocuments(final List<Doc> documents) {
        this.documents = documents;
    }

    public void setKeywords(final List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public void setMaterialId(final String materialId) {
        this.materialId = materialId;
    }

    public void setTableOfContents(final TableOfContents tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }

    public void setArtwork(final Artwork artwork) {
        this.artwork = artwork;
    }

    public void setAssets(final List<Asset> assetsForSplitBook) {
        assets = assetsForSplitBook;
    }

    public void setFrontMatterTocLabel(final String frontMatterTocLabel) {
        this.frontMatterTocLabel = frontMatterTocLabel;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean retVal;
        if (this == obj) {
            retVal = true;
        } else if (obj instanceof TitleMetadata) {
            final TitleMetadata rhs = (TitleMetadata) obj;
            retVal = compareFieldsForEquality(rhs);
        } else {
            retVal = false;
        }
        return retVal;
    }

    public String getStatus() {
        return status;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getTitleVersion() {
        return titleVersion;
    }

    public String getFrontMatterTocLabel() {
        return frontMatterTocLabel;
    }

    private boolean compareFieldsForEquality(final TitleMetadata rhs) {
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(getTitleId(), rhs.getTitleId());
        builder.append(getTitleVersion(), rhs.getTitleVersion());
        builder.append(getStatus(), rhs.getStatus());
        builder.append(getOnlineexpiration(), rhs.getOnlineexpiration());
        return builder.isEquals();
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of
     * hashtables such as those provided by <code>java.util.Hashtable</code>.
     *
     * @return a hash code value for this object.
     *
     * @see Object#equals(Object)
     * @see java.util.Hashtable
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getTitleId());
        builder.append(getTitleVersion());
        builder.append(getStatus());
        builder.append(getOnlineexpiration());

        return builder.toHashCode();
    }

    public Artwork getCoverArt() {
        return artwork;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public TableOfContents getTableOfContents() {
        return tableOfContents;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getLanguage() {
        return language;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getCopyright() {
        return copyright;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public List<Doc> getDocuments() {
        return documents;
    }

    public List<Feature> getProviewFeatures() {
        return proviewFeatures;
    }

    public void setFrontMatterPages(final List<FrontMatterPage> frontMatterPages) {
        this.frontMatterPages = frontMatterPages;
    }

    public List<FrontMatterPage> getFrontMatterPages() {
        return frontMatterPages;
    }

    public void setProviewFeatures(final List<Feature> proviewFeatures) {
        this.proviewFeatures = proviewFeatures;
    }

    public String getOnlineexpiration() {
        return onlineexpiration;
    }

    public void setIsPilotBook(final boolean isPilotBook) {
        this.isPilotBook = isPilotBook;
    }

    public boolean getIsPilotBook() {
        return isPilotBook;
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
        return new TitleMetadataBuilder().fullyQualifiedTitleId(bookDefinition.getFullyQualifiedTitleId())
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
            if (artworkFile == null || !artworkFile.exists()) {
                throw new IllegalArgumentException(
                    "coverImage must not be null and must exists [" + artworkFile + "].");
            }
            artworkFileName = artworkFile.getName();
            return this;
        }

        @NotNull
        public TitleMetadataBuilder artworkFileName(@NotNull final String artworkFileName) {
            if (StringUtils.isBlank(artworkFileName)) {
                throw new IllegalArgumentException("coverImage must not be blank");
            }
            this.artworkFileName = artworkFileName;
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFilesFromDirectory(@NotNull final File assetDirectory) {
            if (assetDirectory == null || !assetDirectory.isDirectory()) {
                throw new IllegalArgumentException("Directory must not be null and must be a directory.");
            }
            for (final File assetFile : assetDirectory.listFiles()) {
                assetFileName(assetFile.getName());
            }
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFile(@NotNull final File assetFile) {
            if (assetFile == null || !assetFile.exists()) {
                throw new IllegalArgumentException("File must not be null and should exist.");
            }
            assetFileName(assetFile.getName());
            return this;
        }

        @NotNull
        public TitleMetadataBuilder assetFileName(@NotNull final String assetFileName) {
            if (assetFileNames == null) {
                assetFileNames = new TreeSet<>();
            }
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
                final List<Asset> assets = new ArrayList<>();
                for (final String assetFileName : assetFileNames) {
                    assets.add(new Asset(StringUtils.substringBeforeLast(assetFileName, "."), assetFileName));
                }
                titleMetadata.setAssets(assets);
            }
        }
    }
}
