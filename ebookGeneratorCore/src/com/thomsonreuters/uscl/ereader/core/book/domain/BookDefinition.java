package com.thomsonreuters.uscl.ereader.core.book.domain;

import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.util.ValueConverter.getStringForBooleanValue;
import static com.thomsonreuters.uscl.ereader.util.ValueConverter.isEqualsYes;

@Entity
@NamedQueries({
    @NamedQuery(name = "findBookDefnBySearchCriterion", query = "select myBook from BookDefinition myBook "),
    @NamedQuery(name = "countBookDefinitions", query = "select count(*) from BookDefinition myBook")})
@Table(name = "EBOOK_DEFINITION")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "BookDefinition")
@EqualsAndHashCode(of = "ebookDefinitionId")
@ToString(includeFieldNames = true, of = {"ebookDefinitionId", "fullyQualifiedTitleId", "proviewDisplayName", "copyright", "copyrightPageText",
                                          "materialId", "isTocFlag", "rootTocGuid", "docCollectionName", "tocCollectionName",
                                          "nortDomain", "nortFilterView", "coverImage", "isbn", "issn", "publishDateText",
                                          "currency", "isProviewTableViewFlag", "eLooseleafsEnabled", "publishedDate", "releaseNotes", "keyciteToplineFlag",
                                          "autoUpdateSupportFlag", "searchIndexFlag", "onePassSsoLinkFlag", "publishCutoffDate",
                                          "ebookDefinitionCompleteFlag", "publishedOnceFlag", "isDeletedFlag",
                                          "lastUpdated", "frontMatterTocLabel", "isAuthorDisplayVertical", "additionalTrademarkInfo", "enableCopyFeatureFlag",
                                          "isPilotBook", "includeAnnotations", "includeNotesOfDecisions", "isFinalStage", "useReloadContent",
                                          "sourceType", "cwbBookName", "isInsStyleFlag", "isDelStyleFlag", "isRemoveEditorNoteHeadFlag",
                                          "frontMatterTheme", "subGroupHeading", "groupName", "printPageNumbers",
                                          "inlineTocIncluded", "indexIncluded", "indexTocCollectionName", "indexDocCollectionName",
                                          "indexTocRootGuid"})
public class BookDefinition implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String Y = "Y";
    private static final String N = "N";
    private static final String CW_EG = "cw/eg";

    public enum PilotBookStatus {
        TRUE,
        FALSE,
        IN_PROGRESS
    };

    public enum SourceType {
        TOC,
        NORT,
        FILE,
        XPP
    };

    @Column(name = "EBOOK_DEFINITION_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @SequenceGenerator(name = "bookDefinitionIdSequence", sequenceName = "EBOOK_DEFINITION_ID_SEQ")
    @GeneratedValue(generator = "bookDefinitionIdSequence")
    @Getter @Setter
    private Long ebookDefinitionId;

    @Column(name = "TITLE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String fullyQualifiedTitleId;

    @Column(name = "PROVIEW_DISPLAY_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String proviewDisplayName;

    @Column(name = "COPYRIGHT", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String copyright;

    @Column(name = "COPYRIGHT_PAGE_TEXT")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String copyrightPageText;

    @Column(name = "MATERIAL_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String materialId;

    @Column(name = "IS_TOC_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isTocFlag;

    @Column(name = "ROOT_TOC_GUID")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String rootTocGuid;

    @Column(name = "DOC_COLLECTION_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String docCollectionName;

    @Column(name = "TOC_COLLECTION_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String tocCollectionName;

    @Column(name = "NORT_DOMAIN")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String nortDomain;

    @Column(name = "NORT_FILTER_VIEW")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String nortFilterView;

    @Column(name = "COVER_IMAGE")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String coverImage;

    @Column(name = "ISBN")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String isbn;

    @Column(name = "ISSN")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String issn;

    @Column(name = "PUBLISH_DATE_TEXT")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String publishDateText;

    @Column(name = "CURRENCY")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String currency;

    @Column(name = "ELOOSELEAFS_ENABLED")
    private String eLooseleafsEnabled;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISHED_DATE")
    @Getter @Setter
    private Date publishedDate;

    @Column(name = "RELEASE_NOTES")
    @Getter @Setter
    private String releaseNotes;

    @Column(name = "KEYCITE_TOPLINE_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String keyciteToplineFlag;

    @Column(name = "ENABLE_COPY_FEATURE_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String enableCopyFeatureFlag;

    @Column(name = "AUTO_UPDATE_SUPPORT_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String autoUpdateSupportFlag;

    @Column(name = "SEARCH_INDEX_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String searchIndexFlag;

    @Column(name = "FRONT_MATTER_THEME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    protected String frontMatterTheme;

    @Column(name = "ONE_PASS_SSO_LINK_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String onePassSsoLinkFlag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISH_CUTOFF_DATE")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Date publishCutoffDate;

    @Column(name = "EBOOK_DEFINITION_COMPLETE_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String ebookDefinitionCompleteFlag;

    @Column(name = "PUBLISHED_ONCE_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String publishedOnceFlag;

    @Column(name = "IS_DELETED_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isDeletedFlag;

    @Column(name = "PROVIEW_TABLE_VIEW_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isProviewTableViewFlag;

    @Column(name = "IS_FINAL_STAGE", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isFinalStage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Date lastUpdated;

    @Column(name = "FRONT_MATTER_TOC_LABEL")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String frontMatterTocLabel;

    @Column(name = "AUTHOR_DISPLAY_VERTICAL_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String isAuthorDisplayVertical;

    @Column(name = "ADDITIONAL_TRADEMARK_INFO")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String additionalTrademarkInfo;

    @Column(name = "IS_PILOT_BOOK")
    @Basic(fetch = FetchType.EAGER)
    private String isPilotBook;

    @Column(name = "INCLUDE_ANNOTATIONS")
    private String includeAnnotations;

    @Column(name = "INCLUDE_NOTES_OF_DECISIONS")
    private String includeNotesOfDecisions;

    @Column(name = "USE_RELOAD_CONTENT")
    @Basic(fetch = FetchType.EAGER)
    private String useReloadContent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "PUBLISHER_CODES_ID", referencedColumnName = "PUBLISHER_CODES_ID")})
    @Getter @Setter
    private PublisherCode publisherCodes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "DOCUMENT_TYPE_CODES_ID", referencedColumnName = "DOCUMENT_TYPE_CODES_ID")})
    @Getter @Setter
    private DocumentTypeCode documentTypeCodes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "EBOOK_KEYWORDS",
        joinColumns = {@JoinColumn(name = "EBOOK_DEFINITION_ID", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "KEYWORD_TYPE_VALUES_ID", nullable = false, updatable = false)})
    private Set<KeywordTypeValue> keywordTypeValues;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<Author> authors;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<VersionIsbn> isbns;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<PilotBook> pilotBooks;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<EbookName> ebookNames;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<FrontMatterPage> frontMatterPages;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<ExcludeDocument> excludeDocuments;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<SplitNodeInfo> splitNodes;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<SplitDocument> splitDocuments;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<PrintComponent> printComponents;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<RenameTocEntry> renameTocEntries;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<TableViewer> tableViewers;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<DocumentCopyright> documentCopyrights;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<DocumentCurrency> documentCurrencies;

    @OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<NortFileLocation> nortFileLocations;

    @Column(name = "SOURCE_TYPE")
    @Basic(fetch = FetchType.EAGER)
    private String sourceType;

    @Column(name = "CWB_BOOK_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String cwbBookName;

    @Column(name = "IS_INS_STYLE_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isInsStyleFlag;

    @Column(name = "IS_DEL_STYLE_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isDelStyleFlag;

    @Column(name = "IS_REMOVE_EDNOTE_HEAD_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isRemoveEditorNoteHeadFlag;

    @Column(name = "IS_SPLIT_BOOK", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isSplitBook;

    @Column(name = "IS_SPLIT_TYPE_AUTO", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isSplitTypeAuto;

    @Column(name = "SPLIT_EBOOK_PARTS")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Integer splitEBookParts;

    @Column(name = "SUBGROUP_HEADING")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String subGroupHeading;

    @Column(name = "GROUP_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String groupName;

    @Column(name = "PRINT_SET_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String printSetNumber;

    @Column(name = "PRINT_SUB_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String printSubNumber;

    @Column(name = "NOTES", length = 2048)
    @Getter @Setter
    private String notes;

    @Column(name = "PRINT_PAGE_NUMBERS")
    private String printPageNumbers;

    @Column(name = "INLINE_TOC_INCLUDED")
    private String inlineTocIncluded;

    @Column(name="INDEX_INCLUDED")
    private String indexIncluded;

    @Column(name="INDEX_TOC_COLLECTION_NAME")
    @Getter @Setter
    private String indexTocCollectionName;

    @Column(name="INDEX_DOC_COLLECTION_NAME")
    @Getter @Setter
    private String indexDocCollectionName;

    @Column(name="INDEX_TOC_ROOT_GUID")
    @Getter @Setter
    private String indexTocRootGuid;

    @Column(name = "VERSION_WITH_PREVIOUS_DOC_IDS")
    @Getter @Setter
    private String versionWithPreviousDocIds;

    @Column(name="SUBSTITUTE_TOC_HEADERS_LEVEL")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Integer substituteTocHeadersLevel;

    public void setIsTocFlag(final boolean isTocFlag) {
        this.isTocFlag = getStringForBooleanValue(isTocFlag);
    }

    public boolean isTocFlag() {
        return isEqualsYes(isTocFlag);
    }

    @Transient
    public String getIsbnNormalized() {
        if (StringUtils.isNotBlank(isbn)) {
            return isbn.replaceAll("-|\\s", "");
        }
        return isbn;
    }

    public void setIsProviewTableViewFlag(final boolean isProviewTableViewFlag) {
        this.isProviewTableViewFlag = getStringForBooleanValue(isProviewTableViewFlag);
    }

    public boolean isProviewTableViewFlag() {
        return isEqualsYes(isProviewTableViewFlag);
    }

    public void setIsFinalStage(final boolean isFinalStage) {
        this.isFinalStage = getStringForBooleanValue(isFinalStage);
    }

    public boolean isFinalStage() {
        return isEqualsYes(isFinalStage);
    }

    public void setELooseleafsEnabled(final boolean isELooseleafsEnabled) {
        this.eLooseleafsEnabled = getStringForBooleanValue(isELooseleafsEnabled);
    }

    public boolean isELooseleafsEnabled() {
        return isEqualsYes(eLooseleafsEnabled);
    }

    public void setKeyciteToplineFlag(final boolean keyciteToplineFlag) {
        this.keyciteToplineFlag = getStringForBooleanValue(keyciteToplineFlag);
    }

    public boolean getKeyciteToplineFlag() {
        return isEqualsYes(keyciteToplineFlag);
    }

    public void setEnableCopyFeatureFlag(final boolean enableCopyFeatureFlag) {
        this.enableCopyFeatureFlag = getStringForBooleanValue(enableCopyFeatureFlag);
    }

    public boolean getEnableCopyFeatureFlag() {
        return isEqualsYes(enableCopyFeatureFlag);
    }

    public void setAutoUpdateSupportFlag(final boolean autoUpdateSupportFlag) {
        this.autoUpdateSupportFlag = getStringForBooleanValue(autoUpdateSupportFlag);
    }

    public boolean getAutoUpdateSupportFlag() {
        return isEqualsYes(autoUpdateSupportFlag);
    }

    public void setSearchIndexFlag(final boolean searchIndexFlag) {
        this.searchIndexFlag = getStringForBooleanValue(searchIndexFlag);
    }

    public boolean getSearchIndexFlag() {
        return isEqualsYes(searchIndexFlag);
    }

    public void setOnePassSsoLinkFlag(final boolean onePassSsoLinkFlag) {
        this.onePassSsoLinkFlag = getStringForBooleanValue(onePassSsoLinkFlag);
    }

    public boolean getOnePassSsoLinkFlag() {
        return isEqualsYes(onePassSsoLinkFlag);
    }

    public void setEbookDefinitionCompleteFlag(final boolean ebookDefinitionCompleteFlag) {
        this.ebookDefinitionCompleteFlag = getStringForBooleanValue(ebookDefinitionCompleteFlag);
    }

    public boolean getEbookDefinitionCompleteFlag() {
        return isEqualsYes(ebookDefinitionCompleteFlag);
    }

    public void setUseReloadContent(final boolean useReloadContent) {
        this.useReloadContent = getStringForBooleanValue(useReloadContent);
    }

    public boolean getUseReloadContent() {
        return isEqualsYes(useReloadContent);
    }

    public void setPublishedOnceFlag(final boolean publishedOnceFlag) {
        this.publishedOnceFlag = getStringForBooleanValue(publishedOnceFlag);
    }

    public boolean getPublishedOnceFlag() {
        return isEqualsYes(publishedOnceFlag);
    }

    public void setIsDeletedFlag(final boolean isDeletedFlag) {
        this.isDeletedFlag = getStringForBooleanValue(isDeletedFlag);
    }

    public boolean isDeletedFlag() {
        return isEqualsYes(isDeletedFlag);
    }

    public void setIsAuthorDisplayVertical(final boolean isAuthorDisplayVertical) {
        this.isAuthorDisplayVertical = getStringForBooleanValue(isAuthorDisplayVertical);
    }

    public boolean isAuthorDisplayVertical() {
        return isEqualsYes(isAuthorDisplayVertical);
    }

    public void setIncludeAnnotations(final boolean includeAnnotations) {
        this.includeAnnotations = getStringForBooleanValue(includeAnnotations);
    }

    public boolean getIncludeAnnotations() {
        return isEqualsYes(includeAnnotations);
    }

    public void setIncludeNotesOfDecisions(final boolean includeNotesOfDecisions) {
        this.includeNotesOfDecisions = getStringForBooleanValue(includeNotesOfDecisions);
    }

    public boolean getIncludeNotesOfDecisions() {
        return isEqualsYes(includeNotesOfDecisions);
    }

    public boolean getIsPilotBook() {
        return isEqualsYes(isPilotBook);
    }

    public PilotBookStatus getPilotBookStatus() {
            if (isEqualsYes(isPilotBook)) {
                return PilotBookStatus.TRUE;
            } else if ("I".equalsIgnoreCase(isPilotBook)) {
                return PilotBookStatus.IN_PROGRESS;
            } else {
                return PilotBookStatus.FALSE;
            }
     }

    public void setPilotBookStatus(final PilotBookStatus status) {
        switch (status) {
        case TRUE:
            isPilotBook = Y;
            break;
        case IN_PROGRESS:
            isPilotBook = "I";
            break;
        default:
            isPilotBook = N;
            break;
        }
    }

    public SourceType getSourceType() {
        if (StringUtils.isBlank(sourceType)) {
            return SourceType.TOC;
        } else {
            if (sourceType.equalsIgnoreCase("NORT")) {
                return SourceType.NORT;
            } else if (sourceType.equalsIgnoreCase("FILE")) {
                return SourceType.FILE;
            } else if (sourceType.equalsIgnoreCase("XPP")) {
                return SourceType.XPP;
            } else {
                return SourceType.TOC;
            }
        }
    }

    public void setSourceType(final SourceType type) {
        switch (type) {
        case NORT:
            sourceType = "NORT";
            break;
        case FILE:
            sourceType = "FILE";
            break;
        case XPP:
            sourceType = "XPP";
            break;
        default:
            sourceType = "TOC";
            break;
        }
    }

    public void setIsInsStyleFlag(final boolean isInsStyleFlag) {
        this.isInsStyleFlag = getStringForBooleanValue(isInsStyleFlag);
    }

    public boolean isInsStyleFlag() {
        return isEqualsYes(isInsStyleFlag);
    }

    public void setIsDelStyleFlag(final boolean isDelStyleFlag) {
        this.isDelStyleFlag = getStringForBooleanValue(isDelStyleFlag);
    }

    public boolean isDelStyleFlag() {
        return isEqualsYes(isDelStyleFlag);
    }

    public void setIsRemoveEditorNoteHeadFlag(final boolean isRemoveEditorNoteHeadFlag) {
        this.isRemoveEditorNoteHeadFlag = getStringForBooleanValue(isRemoveEditorNoteHeadFlag);
    }

    public boolean isRemoveEditorNoteHeadFlag() {
        return isEqualsYes(isRemoveEditorNoteHeadFlag);
    }

    public boolean isSplitBook() {
        return SourceType.XPP.equals(getSourceType())
            ? getPrintComponents().stream().anyMatch(PrintComponent::getSplitter)
            : Y.equalsIgnoreCase(isSplitBook);
    }

    public void setIsSplitBook(final boolean isSplitBook) {
        this.isSplitBook = getStringForBooleanValue(isSplitBook);
    }

    public boolean isSplitTypeAuto() {
        return isEqualsYes(isSplitTypeAuto);
    }

    public void setIsSplitTypeAuto(final boolean isSplitTypeAuto) {
        this.isSplitTypeAuto = getStringForBooleanValue(isSplitTypeAuto);
    }

    public void setKeywordTypeValues(final Collection<KeywordTypeValue> keywordTypeValues) {
        this.keywordTypeValues = new LinkedHashSet<>(keywordTypeValues);
    }

    public Set<KeywordTypeValue> getKeywordTypeValues() {
        keywordTypeValues = Optional.ofNullable(keywordTypeValues).orElseGet(LinkedHashSet::new);
        return keywordTypeValues;
    }

    public void setAuthors(final Collection<Author> authors) {
        this.authors = new LinkedHashSet<>(authors);
    }

    public List<Author> getAuthors() {
        authors = Optional.ofNullable(authors).orElseGet(LinkedHashSet::new);
        return getOrderedListFromCollection(authors);
    }

    public void setIsbns(final Collection<VersionIsbn> isbns) {
        this.isbns = new LinkedHashSet<>(isbns);
    }

    public Set<VersionIsbn> getIsbns() {
        return Optional.ofNullable(isbns).orElseGet(LinkedHashSet::new);
    }

    public void setPilotBooks(final Collection<PilotBook> pilotBooks) {
        this.pilotBooks = new LinkedHashSet<>(pilotBooks);
    }

    public List<PilotBook> getPilotBooks() {
        pilotBooks = Optional.ofNullable(pilotBooks).orElseGet(LinkedHashSet::new);
        return getOrderedListFromCollection(pilotBooks);
    }

    public void setNortFileLocations(final Collection<NortFileLocation> nortFileLocations) {
        this.nortFileLocations = new LinkedHashSet<>(nortFileLocations);
    }

    public List<NortFileLocation> getNortFileLocations() {
        nortFileLocations = Optional.ofNullable(nortFileLocations).orElseGet(LinkedHashSet::new);
        return getOrderedListFromCollection(nortFileLocations);
    }

    public void setEbookNames(final Collection<EbookName> ebookNames) {
        this.ebookNames = new LinkedHashSet<>(ebookNames);
    }

    public List<EbookName> getEbookNames() {
        ebookNames = Optional.ofNullable(ebookNames).orElseGet(LinkedHashSet::new);
        return getOrderedListFromCollection(ebookNames);
    }

    public List<ExcludeDocument> getExcludeDocuments() {
        excludeDocuments = Optional.ofNullable(excludeDocuments).orElseGet(HashSet::new);
        return excludeDocuments.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public void setExcludeDocuments(final Collection<ExcludeDocument> excludeDocuments) {
        this.excludeDocuments = new HashSet<>(excludeDocuments);
    }

    public List<SplitNodeInfo> getSplitNodesAsList() {
        splitNodes = Optional.ofNullable(splitNodes).orElseGet(HashSet::new);
        return splitNodes.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public Set<SplitNodeInfo> getSplitNodes() {
        splitNodes = Optional.ofNullable(splitNodes).orElseGet(HashSet::new);
        return splitNodes;
    }

    public void setSplitNodes(final Collection<SplitNodeInfo> splitNodes) {
        this.splitNodes = new HashSet<>(splitNodes);
    }

    public Set<SplitDocument> getSplitDocuments() {
        splitDocuments = Optional.ofNullable(splitDocuments).orElseGet(HashSet::new);
        return splitDocuments;
    }

    public List<SplitDocument> getSplitDocumentsAsList() {
        splitDocuments = Optional.ofNullable(splitDocuments).orElseGet(HashSet::new);
        return splitDocuments.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public void setSplitDocuments(final Collection<SplitDocument> splitDocuments) {
        this.splitDocuments = new HashSet<>(splitDocuments);
    }

    public Set<PrintComponent> getPrintComponents() {
        printComponents = Optional.ofNullable(printComponents).orElseGet(HashSet::new);
        return printComponents;
    }

    public void setPrintComponents(final Collection<PrintComponent> printComponents) {
        this.printComponents = new HashSet<>(printComponents);
    }

    public List<RenameTocEntry> getRenameTocEntries() {
        renameTocEntries = Optional.ofNullable(renameTocEntries).orElseGet(HashSet::new);
        return renameTocEntries.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public void setRenameTocEntries(final Collection<RenameTocEntry> renameTocEntries) {
        this.renameTocEntries = new HashSet<>(renameTocEntries);
    }

    public List<TableViewer> getTableViewers() {
        tableViewers = Optional.ofNullable(tableViewers).orElseGet(HashSet::new);
        return tableViewers.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public void setTableViewers(final Collection<TableViewer> tableViewers) {
        this.tableViewers = new HashSet<>(tableViewers);
    }

    public List<DocumentCopyright> getDocumentCopyrights() {
        documentCopyrights = Optional.ofNullable(documentCopyrights).orElseGet(HashSet::new);
        return documentCopyrights.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public void setDocumentCopyrights(final Collection<DocumentCopyright> documentCopyrights) {
        this.documentCopyrights = new HashSet<>(documentCopyrights);
    }

    public List<DocumentCurrency> getDocumentCurrencies() {
        documentCurrencies = Optional.ofNullable(documentCurrencies).orElseGet(HashSet::new);
        return documentCurrencies.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public void setDocumentCurrencies(final Collection<DocumentCurrency> documentCurrencies) {
        this.documentCurrencies = new HashSet<>(documentCurrencies);
    }

    public List<FrontMatterPage> getFrontMatterPages() {
        frontMatterPages = Optional.ofNullable(frontMatterPages).orElseGet(LinkedHashSet::new);

        // Sort by sequence numbers
        final List<FrontMatterPage> pageList = new ArrayList<>();
        pageList.addAll(frontMatterPages);
        Collections.sort(pageList);

        // Sort Sections
        for (final FrontMatterPage page : pageList) {
            // Sort by sequence numbers
            final List<FrontMatterSection> sections = new ArrayList<>();
            sections.addAll(page.getFrontMatterSections());
            Collections.sort(sections);
            page.setFrontMatterSections(sections);

            // Sort PDFs
            for (final FrontMatterSection section : sections) {
                // Sort by sequence numbers
                final List<FrontMatterPdf> pdfs = new ArrayList<>();
                pdfs.addAll(section.getPdfs());
                Collections.sort(pdfs);
                section.setPdfs(pdfs);
            }
        }

        return pageList;
    }

    public Set<String> getFrontMatterPdfFileNames() {
        return getFrontMatterPages().stream()
                .map(FrontMatterPage::getFrontMatterSections)
                .flatMap(Collection::stream)
                .map(FrontMatterSection::getPdfs)
                .flatMap(Collection::stream)
                .map(FrontMatterPdf::getPdfFilename)
                .collect(Collectors.toSet());
    }

    private <T extends Comparable<T>> List<T> getOrderedListFromCollection(final Collection<T> collection) {
        return collection.stream()
            .sorted()
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setFrontMatterPages(final Collection<FrontMatterPage> frontMatterPage) {
        frontMatterPages = new LinkedHashSet<>(frontMatterPage);
    }

    public void setPrintPageNumbers(final boolean isPrintPageNumbers) {
        printPageNumbers = getStringForBooleanValue(isPrintPageNumbers);
    }

    public boolean isPrintPageNumbers() {
        return isEqualsYes(printPageNumbers);
    }

    public void setInlineTocIncluded(final boolean isInlineTocIncluded) {
        inlineTocIncluded = getStringForBooleanValue(isInlineTocIncluded);
    }

    public boolean isInlineTocIncluded() {
        return isEqualsYes(inlineTocIncluded);
    }

    public void setIndexIncluded(final boolean isIndexIncluded) {
        indexIncluded = getStringForBooleanValue(isIndexIncluded);
    }

    public boolean isIndexIncluded() {
        return isEqualsYes(indexIncluded);
    }

    public BookDefinition() {
        super();
        setIsDeletedFlag(false);
        setPublishedOnceFlag(false);
        setOnePassSsoLinkFlag(true);
        setIncludeAnnotations(false);
        setIncludeNotesOfDecisions(true);
        setIsProviewTableViewFlag(false);
        setIsFinalStage(true);
        setPilotBookStatus(PilotBookStatus.FALSE);
        setSourceType(SourceType.TOC);
        setUseReloadContent(false);
        setIsTocFlag(false);
        setIsInsStyleFlag(false);
        setIsDelStyleFlag(false);
        setIsRemoveEditorNoteHeadFlag(false);
        setSubstituteTocHeadersLevel(0);
    }

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    public void copy(final BookDefinition that) {
        setEbookDefinitionId(that.getEbookDefinitionId());
        setFullyQualifiedTitleId(that.getFullyQualifiedTitleId());
        setProviewDisplayName(that.getProviewDisplayName());
        setCopyright(that.getCopyright());
        setCopyrightPageText(that.getCopyrightPageText());
        setMaterialId(that.getMaterialId());
        setIsTocFlag(that.isTocFlag());
        setRootTocGuid(that.getRootTocGuid());
        setDocCollectionName(that.getDocCollectionName());
        setTocCollectionName(that.getTocCollectionName());
        setNortDomain(that.getNortDomain());
        setNortFilterView(that.getNortFilterView());
        setCoverImage(that.getCoverImage());
        setIsbn(that.getIsbn());
        setIssn(that.getIssn());
        setPublishDateText(that.getPublishDateText());
        setCurrency(that.getCurrency());
        setELooseleafsEnabled(that.isELooseleafsEnabled());
        setPublishedDate(that.getPublishedDate());
        setReleaseNotes(that.getReleaseNotes());
        setKeyciteToplineFlag(that.getKeyciteToplineFlag());
        setAutoUpdateSupportFlag(that.getAutoUpdateSupportFlag());
        setSearchIndexFlag(that.getSearchIndexFlag());
        setOnePassSsoLinkFlag(that.getOnePassSsoLinkFlag());
        setPublishCutoffDate(that.getPublishCutoffDate());
        setEbookDefinitionCompleteFlag(that.getEbookDefinitionCompleteFlag());
        setPublishedOnceFlag(that.getPublishedOnceFlag());
        setIsDeletedFlag(that.isDeletedFlag());
        setEnableCopyFeatureFlag(that.getEnableCopyFeatureFlag());
        setPilotBookStatus(that.getPilotBookStatus());
        setLastUpdated(that.getLastUpdated());
        setPublisherCodes(that.getPublisherCodes());
        setDocumentTypeCodes(that.getDocumentTypeCodes());
        setFrontMatterTocLabel(that.getFrontMatterTocLabel());
        setIsAuthorDisplayVertical(that.isAuthorDisplayVertical());
        setAdditionalTrademarkInfo(that.getAdditionalTrademarkInfo());
        setAuthors(new java.util.LinkedHashSet<>(that.getAuthors()));
        setPilotBooks(new java.util.LinkedHashSet<>(that.getPilotBooks()));
        setNortFileLocations(new java.util.LinkedHashSet<>(that.getNortFileLocations()));
        setEbookNames(new java.util.LinkedHashSet<>(that.getEbookNames()));
        setFrontMatterPages(new java.util.LinkedHashSet<>(that.getFrontMatterPages()));
        setExcludeDocuments(new HashSet<>(that.getExcludeDocuments()));
        setSplitDocuments(new HashSet<>(that.getSplitDocuments()));
        setPrintComponents(new HashSet<>(that.getPrintComponents()));
        setRenameTocEntries(new HashSet<>(that.getRenameTocEntries()));
        setTableViewers(new HashSet<>(that.getTableViewers()));
        setDocumentCopyrights(new HashSet<>(that.getDocumentCopyrights()));
        setDocumentCurrencies(new HashSet<>(that.getDocumentCurrencies()));
        setIncludeAnnotations(that.getIncludeAnnotations());
        setIncludeNotesOfDecisions(that.getIncludeNotesOfDecisions());
        setIsFinalStage(that.isFinalStage());
        setUseReloadContent(that.getUseReloadContent());
        setSourceType(that.getSourceType());
        setCwbBookName(that.getCwbBookName());
        setIsInsStyleFlag(that.isInsStyleFlag());
        setIsDelStyleFlag(that.isDelStyleFlag());
        setIsRemoveEditorNoteHeadFlag(that.isRemoveEditorNoteHeadFlag());
        setIsSplitBook(that.isSplitBook());
        setSubGroupHeading(that.getSubGroupHeading());
        setGroupName(that.getGroupName());
        setPrintSetNumber(that.getPrintSetNumber());
        setPrintSubNumber(that.getPrintSubNumber());
        setPrintPageNumbers(that.isPrintPageNumbers());
        setInlineTocIncluded(that.isInlineTocIncluded());
        setSubstituteTocHeadersLevel(that.getSubstituteTocHeadersLevel());
    }

    /**
     * The base title ID, without any of the leading slash-separated namespace components.  Example: "ak_2010_federal".
     * This is a transient field because we are making the space-for-time tradeoff and
     * calculating this value once when the fullTitleId is set.  The TITLE_ID column in the database
     * holds the fully-qualified value.
     * @return the right-most component of the slash separated title ID path, or null if blank string.
     */
    @Transient
    public String getTitleId() {
        final StringTokenizer tokenizer = new StringTokenizer(fullyQualifiedTitleId, "/");
        String component = null;
        while (tokenizer.hasMoreTokens()) {
            component = tokenizer.nextToken();
        }
        return component;
    }

    /**
     * The proview keywords as derived from the book definition.
     * @return List of keywords.
     */
    @Transient
    public List<Keyword> getKeyWords() {
        return getKeywordTypeValues().stream()
            .map(value -> new Keyword(value.getKeywordTypeCode().getBaseName(), value.getName()))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Provides the status of the book definition
     * @return String indicating the status
     */
    @Transient
    public String getBookStatus() {
        final String status;
        if (isDeletedFlag()) {
            status = "Deleted";
        } else if (getEbookDefinitionCompleteFlag()) {
            status = "Ready";
        } else {
            status = "Incomplete";
        }
        return status;
    }

    @Transient
    public boolean isCwBook() {
        return getFullyQualifiedTitleId().startsWith(CW_EG);
    }
}
