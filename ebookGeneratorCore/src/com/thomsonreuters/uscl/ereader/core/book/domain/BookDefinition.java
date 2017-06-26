package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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

import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@NamedQueries({
    @NamedQuery(name = "findBookDefnBySearchCriterion", query = "select myBook from BookDefinition myBook "),
    @NamedQuery(name = "countBookDefinitions", query = "select count(*) from BookDefinition myBook")})
@Table(name = "EBOOK_DEFINITION")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "BookDefinition")
public class BookDefinition implements Serializable
{
    private static final long serialVersionUID = 1L;

    public enum PilotBookStatus
    {
        TRUE,
        FALSE,
        IN_PROGRESS
    };

    public enum SourceType
    {
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
    private Long ebookDefinitionId;

    @Column(name = "TITLE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String fullyQualifiedTitleId;

    @Column(name = "PROVIEW_DISPLAY_NAME")
    @Basic(fetch = FetchType.EAGER)
    private String proviewDisplayName;

    @Column(name = "COPYRIGHT", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String copyright;

    @Column(name = "COPYRIGHT_PAGE_TEXT")
    @Basic(fetch = FetchType.EAGER)
    private String copyrightPageText;

    @Column(name = "MATERIAL_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String materialId;

    @Column(name = "IS_TOC_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isTocFlag;

    @Column(name = "ROOT_TOC_GUID")
    @Basic(fetch = FetchType.EAGER)
    private String rootTocGuid;

    @Column(name = "DOC_COLLECTION_NAME")
    @Basic(fetch = FetchType.EAGER)
    private String docCollectionName;

    @Column(name = "TOC_COLLECTION_NAME")
    @Basic(fetch = FetchType.EAGER)
    private String tocCollectionName;

    @Column(name = "NORT_DOMAIN")
    @Basic(fetch = FetchType.EAGER)
    private String nortDomain;

    @Column(name = "NORT_FILTER_VIEW")
    @Basic(fetch = FetchType.EAGER)
    private String nortFilterView;

    @Column(name = "COVER_IMAGE")
    @Basic(fetch = FetchType.EAGER)
    private String coverImage;

    @Column(name = "ISBN")
    @Basic(fetch = FetchType.EAGER)
    private String isbn;

    @Column(name = "PUBLISH_DATE_TEXT")
    @Basic(fetch = FetchType.EAGER)
    private String publishDateText;

    @Column(name = "CURRENCY")
    @Basic(fetch = FetchType.EAGER)
    private String currency;

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
    protected String frontMatterTheme;

    @Column(name = "ONE_PASS_SSO_LINK_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String onePassSsoLinkFlag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISH_CUTOFF_DATE")
    @Basic(fetch = FetchType.EAGER)
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
    private Date lastUpdated;

    @Column(name = "FRONT_MATTER_TOC_LABEL")
    @Basic(fetch = FetchType.EAGER)
    private String frontMatterTocLabel;

    @Column(name = "AUTHOR_DISPLAY_VERTICAL_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String isAuthorDisplayVertical;

    @Column(name = "ADDITIONAL_TRADEMARK_INFO")
    @Basic(fetch = FetchType.EAGER)
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
    private PublisherCode publisherCodes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "DOCUMENT_TYPE_CODES_ID", referencedColumnName = "DOCUMENT_TYPE_CODES_ID")})
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
    private Integer splitEBookParts;

    @Column(name = "IS_SPLIT_LOCK")
    @Basic(fetch = FetchType.EAGER)
    private String isSplitLock;

    @Column(name = "SUBGROUP_HEADING")
    @Basic(fetch = FetchType.EAGER)
    private String subGroupHeading;

    @Column(name = "GROUP_NAME")
    @Basic(fetch = FetchType.EAGER)
    private String groupName;

    @Column(name = "PRINT_SET_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    private String printSetNumber;

    public void setEbookDefinitionId(final Long ebookDefinitionId)
    {
        this.ebookDefinitionId = ebookDefinitionId;
    }

    public Long getEbookDefinitionId()
    {
        return ebookDefinitionId;
    }

    public void setFullyQualifiedTitleId(final String fullyQualifiedTitleId)
    {
        this.fullyQualifiedTitleId = fullyQualifiedTitleId;
    }

    public String getFullyQualifiedTitleId()
    {
        return fullyQualifiedTitleId;
    }

    public void setProviewDisplayName(final String proviewDisplayName)
    {
        this.proviewDisplayName = proviewDisplayName;
    }

    public String getProviewDisplayName()
    {
        return proviewDisplayName;
    }

    public void setCopyright(final String copyright)
    {
        this.copyright = copyright;
    }

    public String getCopyright()
    {
        return copyright;
    }

    public void setCopyrightPageText(final String copyrightPageText)
    {
        this.copyrightPageText = copyrightPageText;
    }

    public String getCopyrightPageText()
    {
        return copyrightPageText;
    }

    public void setMaterialId(final String materialId)
    {
        this.materialId = materialId;
    }

    public String getMaterialId()
    {
        return materialId;
    }

    public void setIsTocFlag(final boolean isTocFlag)
    {
        this.isTocFlag = ((isTocFlag) ? "Y" : "N");
    }

    public boolean isTocFlag()
    {
        return ((isTocFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setRootTocGuid(final String rootTocGuid)
    {
        this.rootTocGuid = rootTocGuid;
    }

    public String getRootTocGuid()
    {
        return rootTocGuid;
    }

    public void setDocCollectionName(final String docCollectionName)
    {
        this.docCollectionName = docCollectionName;
    }

    public String getDocCollectionName()
    {
        return docCollectionName;
    }

    public void setTocCollectionName(final String tocCollectionName)
    {
        this.tocCollectionName = tocCollectionName;
    }

    public String getTocCollectionName()
    {
        return tocCollectionName;
    }

    public void setNortDomain(final String nortDomain)
    {
        this.nortDomain = nortDomain;
    }

    public String getNortDomain()
    {
        return nortDomain;
    }

    public void setNortFilterView(final String nortFilterView)
    {
        this.nortFilterView = nortFilterView;
    }

    public String getNortFilterView()
    {
        return nortFilterView;
    }

    public void setCoverImage(final String coverImage)
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setIsbn(final String isbn)
    {
        this.isbn = isbn;
    }

    public String getIsbn()
    {
        return isbn;
    }

    @Transient
    public String getIsbnNormalized()
    {
        if (StringUtils.isNotBlank(isbn))
        {
            return isbn.replaceAll("-|\\s", "");
        }
        return isbn;
    }

    public void setPublishDateText(final String publishDateText)
    {
        this.publishDateText = publishDateText;
    }

    public String getPublishDateText()
    {
        return publishDateText;
    }

    public void setCurrency(final String currency)
    {
        this.currency = currency;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setIsProviewTableViewFlag(final boolean isProviewTableViewFlag)
    {
        this.isProviewTableViewFlag = ((isProviewTableViewFlag) ? "Y" : "N");
    }

    public boolean isProviewTableViewFlag()
    {
        return ((isProviewTableViewFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsFinalStage(final boolean isFinalStage)
    {
        this.isFinalStage = ((isFinalStage) ? "Y" : "N");
    }

    public boolean isFinalStage()
    {
        return ((isFinalStage.equalsIgnoreCase("Y") ? true : false));
    }

    public void setKeyciteToplineFlag(final boolean keyciteToplineFlag)
    {
        this.keyciteToplineFlag = ((keyciteToplineFlag) ? "Y" : "N");
    }

    public boolean getKeyciteToplineFlag()
    {
        return ((keyciteToplineFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setEnableCopyFeatureFlag(final boolean enableCopyFeatureFlag)
    {
        this.enableCopyFeatureFlag = ((enableCopyFeatureFlag) ? "Y" : "N");
    }

    public boolean getEnableCopyFeatureFlag()
    {
        return ((enableCopyFeatureFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setAutoUpdateSupportFlag(final boolean autoUpdateSupportFlag)
    {
        this.autoUpdateSupportFlag = ((autoUpdateSupportFlag) ? "Y" : "N");
    }

    public boolean getAutoUpdateSupportFlag()
    {
        return ((autoUpdateSupportFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setSearchIndexFlag(final boolean searchIndexFlag)
    {
        this.searchIndexFlag = ((searchIndexFlag) ? "Y" : "N");
    }

    public boolean getSearchIndexFlag()
    {
        return ((searchIndexFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setOnePassSsoLinkFlag(final boolean onePassSsoLinkFlag)
    {
        this.onePassSsoLinkFlag = ((onePassSsoLinkFlag) ? "Y" : "N");
    }

    public boolean getOnePassSsoLinkFlag()
    {
        return ((onePassSsoLinkFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setPublishCutoffDate(final Date publishCutoffDate)
    {
        this.publishCutoffDate = publishCutoffDate;
    }

    public Date getPublishCutoffDate()
    {
        return publishCutoffDate;
    }

    public void setEbookDefinitionCompleteFlag(final boolean ebookDefinitionCompleteFlag)
    {
        this.ebookDefinitionCompleteFlag = ((ebookDefinitionCompleteFlag) ? "Y" : "N");
    }

    public boolean getEbookDefinitionCompleteFlag()
    {
        return ((ebookDefinitionCompleteFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setUseReloadContent(final boolean useReloadContent)
    {
        this.useReloadContent = ((useReloadContent) ? "Y" : "N");
    }

    public boolean getUseReloadContent()
    {
        return ((useReloadContent.equalsIgnoreCase("Y") ? true : false));
    }

    public void setPublishedOnceFlag(final boolean publishedOnceFlag)
    {
        this.publishedOnceFlag = ((publishedOnceFlag) ? "Y" : "N");
    }

    public boolean getPublishedOnceFlag()
    {
        return ((publishedOnceFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsDeletedFlag(final boolean isDeletedFlag)
    {
        this.isDeletedFlag = ((isDeletedFlag) ? "Y" : "N");
    }

    public boolean isDeletedFlag()
    {
        return ((isDeletedFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsAuthorDisplayVertical(final boolean isAuthorDisplayVertical)
    {
        this.isAuthorDisplayVertical = ((isAuthorDisplayVertical) ? "Y" : "N");
    }

    public boolean isAuthorDisplayVertical()
    {
        return ((isAuthorDisplayVertical.equalsIgnoreCase("Y") ? true : false));
    }

    public String getAdditionalTrademarkInfo()
    {
        return additionalTrademarkInfo;
    }

    public void setAdditionalTrademarkInfo(final String additionalTrademarkInfo)
    {
        this.additionalTrademarkInfo = additionalTrademarkInfo;
    }

    public void setIncludeAnnotations(final boolean includeAnnotations)
    {
        this.includeAnnotations = ((includeAnnotations) ? "Y" : "N");
    }

    public boolean getIncludeAnnotations()
    {
        return ((includeAnnotations.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIncludeNotesOfDecisions(final boolean includeNotesOfDecisions)
    {
        this.includeNotesOfDecisions = ((includeNotesOfDecisions) ? "Y" : "N");
    }

    public boolean getIncludeNotesOfDecisions()
    {
        return ((includeNotesOfDecisions.equalsIgnoreCase("Y") ? true : false));
    }

    public boolean getIsPilotBook()
    {
        if (StringUtils.isBlank(isPilotBook))
        {
            return false;
        }
        else
        {
            return isPilotBook.equalsIgnoreCase("Y");
        }
    }

    public PilotBookStatus getPilotBookStatus()
    {
        if (StringUtils.isBlank(isPilotBook))
        {
            return PilotBookStatus.FALSE;
        }
        else
        {
            if (isPilotBook.equalsIgnoreCase("Y"))
            {
                return PilotBookStatus.TRUE;
            }
            else if (isPilotBook.equalsIgnoreCase("I"))
            {
                return PilotBookStatus.IN_PROGRESS;
            }
            else
            {
                return PilotBookStatus.FALSE;
            }
        }
    }

    public void setPilotBookStatus(final PilotBookStatus status)
    {
        switch (status)
        {
        case TRUE:
            isPilotBook = "Y";
            break;
        case IN_PROGRESS:
            isPilotBook = "I";
            break;
        default:
            isPilotBook = "N";
            break;
        }
    }

    public SourceType getSourceType()
    {
        if (StringUtils.isBlank(sourceType))
        {
            return SourceType.TOC;
        }
        else
        {
            if (sourceType.equalsIgnoreCase("NORT"))
            {
                return SourceType.NORT;
            }
            else if (sourceType.equalsIgnoreCase("FILE"))
            {
                return SourceType.FILE;
            }
            else if (sourceType.equalsIgnoreCase("XPP"))
            {
                return SourceType.XPP;
            }
            else
            {
                return SourceType.TOC;
            }
        }
    }

    public void setSourceType(final SourceType type)
    {
        switch (type)
        {
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

    public String getCwbBookName()
    {
        return cwbBookName;
    }

    public void setCwbBookName(final String cwbBookName)
    {
        this.cwbBookName = cwbBookName;
    }

    public String getFrontMatterTheme()
    {
        return frontMatterTheme;
    }

    public void setFrontMatterTheme(final String frontMatterTheme)
    {
        this.frontMatterTheme = frontMatterTheme;
    }

    public void setIsInsStyleFlag(final boolean isInsStyleFlag)
    {
        this.isInsStyleFlag = ((isInsStyleFlag) ? "Y" : "N");
    }

    public boolean isInsStyleFlag()
    {
        return ((isInsStyleFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsDelStyleFlag(final boolean isDelStyleFlag)
    {
        this.isDelStyleFlag = ((isDelStyleFlag) ? "Y" : "N");
    }

    public boolean isDelStyleFlag()
    {
        return ((isDelStyleFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsRemoveEditorNoteHeadFlag(final boolean isRemoveEditorNoteHeadFlag)
    {
        this.isRemoveEditorNoteHeadFlag = ((isRemoveEditorNoteHeadFlag) ? "Y" : "N");
    }

    public boolean isRemoveEditorNoteHeadFlag()
    {
        return ((isRemoveEditorNoteHeadFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public boolean isSplitBook()
    {
        return ((isSplitBook.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsSplitBook(final boolean isSplitBook)
    {
        this.isSplitBook = ((isSplitBook) ? "Y" : "N");
    }

    public boolean isSplitLock()
    {
        return ((isSplitLock.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsSplitLock(final boolean isSplitLock)
    {
        this.isSplitLock = ((isSplitLock) ? "Y" : "N");
    }

    public boolean isSplitTypeAuto()
    {
        return ((isSplitTypeAuto.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsSplitTypeAuto(final boolean isSplitTypeAuto)
    {
        this.isSplitTypeAuto = ((isSplitTypeAuto) ? "Y" : "N");
    }

    public Integer getSplitEBookParts()
    {
        return splitEBookParts;
    }

    public void setSplitEBookParts(final Integer splitEBookParts)
    {
        this.splitEBookParts = splitEBookParts;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(final String groupName)
    {
        this.groupName = groupName;
    }

    //Subgroup heading within the group
    public String getSubGroupHeading()
    {
        return subGroupHeading;
    }

    public void setSubGroupHeading(final String subGroupHeading)
    {
        this.subGroupHeading = subGroupHeading;
    }

    public void setLastUpdated(final Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public String getFrontMatterTocLabel()
    {
        return frontMatterTocLabel;
    }

    public void setFrontMatterTocLabel(final String frontMatterTocLabel)
    {
        this.frontMatterTocLabel = frontMatterTocLabel;
    }

    public void setPublisherCodes(final PublisherCode publisherCodes)
    {
        this.publisherCodes = publisherCodes;
    }

    public PublisherCode getPublisherCodes()
    {
        return publisherCodes;
    }

    public void setDocumentTypeCodes(final DocumentTypeCode documentTypeCodes)
    {
        this.documentTypeCodes = documentTypeCodes;
    }

    public DocumentTypeCode getDocumentTypeCodes()
    {
        return documentTypeCodes;
    }

    public void setKeywordTypeValues(final Collection<KeywordTypeValue> keywordTypeValues)
    {
        this.keywordTypeValues = new java.util.LinkedHashSet<>(keywordTypeValues);
    }

    public Set<KeywordTypeValue> getKeywordTypeValues()
    {
        if (keywordTypeValues == null)
        {
            keywordTypeValues = new java.util.LinkedHashSet<>();
        }
        return keywordTypeValues;
    }

    public void setAuthors(final Collection<Author> authors)
    {
        this.authors = new java.util.LinkedHashSet<>(authors);
    }

    public List<Author> getAuthors()
    {
        if (authors == null)
        {
            authors = new java.util.LinkedHashSet<>();
        }
        // Sort by sequence numbers
        final List<Author> authorList = new ArrayList<>();
        authorList.addAll(authors);
        Collections.sort(authorList);
        return authorList;
    }

    public void setPilotBooks(final Collection<PilotBook> pilotBooks)
    {
        this.pilotBooks = new java.util.LinkedHashSet<>(pilotBooks);
    }

    public List<PilotBook> getPilotBooks()
    {
        if (pilotBooks == null)
        {
            pilotBooks = new java.util.LinkedHashSet<>();
        }
        // Sort by sequence numbers
        final List<PilotBook> pilotBookList = new ArrayList<>();
        pilotBookList.addAll(pilotBooks);
        Collections.sort(pilotBookList);
        return pilotBookList;
    }

    public void setNortFileLocations(final Collection<NortFileLocation> nortFileLocations)
    {
        this.nortFileLocations = new java.util.LinkedHashSet<>(nortFileLocations);
    }

    public List<NortFileLocation> getNortFileLocations()
    {
        if (nortFileLocations == null)
        {
            nortFileLocations = new java.util.LinkedHashSet<>();
        }
        // Sort by sequence numbers
        final List<NortFileLocation> nortFileLocationList = new ArrayList<>();
        nortFileLocationList.addAll(nortFileLocations);
        Collections.sort(nortFileLocationList);
        return nortFileLocationList;
    }

    public void setEbookNames(final Collection<EbookName> ebookNames)
    {
        this.ebookNames = new java.util.LinkedHashSet<>(ebookNames);
    }

    public List<EbookName> getEbookNames()
    {
        if (ebookNames == null)
        {
            ebookNames = new java.util.LinkedHashSet<>();
        }

        // Sort by sequence numbers
        final List<EbookName> nameList = new ArrayList<>();
        nameList.addAll(ebookNames);
        Collections.sort(nameList);

        return nameList;
    }

    public List<ExcludeDocument> getExcludeDocuments()
    {
        if (excludeDocuments == null)
        {
            excludeDocuments = new HashSet<>();
        }
        // Change to list
        final List<ExcludeDocument> documents = new ArrayList<>();
        documents.addAll(excludeDocuments);
        return documents;
    }

    public void setExcludeDocuments(final Collection<ExcludeDocument> excludeDocuments)
    {
        this.excludeDocuments = new HashSet<>(excludeDocuments);
    }

    public List<SplitNodeInfo> getSplitNodesAsList()
    {
        if (splitNodes == null)
        {
            splitNodes = new HashSet<>();
        }
        // Change to list
        final List<SplitNodeInfo> splitNodeInfoList = new ArrayList<>();
        splitNodeInfoList.addAll(splitNodes);
        return splitNodeInfoList;
    }

    public Set<SplitNodeInfo> getSplitNodes()
    {
        if (splitNodes == null)
        {
            splitNodes = new HashSet<>();
        }
        return splitNodes;
    }

    public void setSplitNodes(final Collection<SplitNodeInfo> splitNodes)
    {
        this.splitNodes = new HashSet<>(splitNodes);
    }

    public Set<SplitDocument> getSplitDocuments()
    {
        if (splitDocuments == null)
        {
            splitDocuments = new HashSet<>();
        }
        return splitDocuments;
    }

    public List<SplitDocument> getSplitDocumentsAsList()
    {
        if (splitDocuments == null)
        {
            splitDocuments = new HashSet<>();
        }
        // Change to list
        final List<SplitDocument> documents = new ArrayList<>();
        documents.addAll(splitDocuments);
        return documents;
    }

    public void setSplitDocuments(final Collection<SplitDocument> splitDocuments)
    {
        this.splitDocuments = new HashSet<>(splitDocuments);
    }

    public Set<PrintComponent> getPrintComponents()
    {
        if (printComponents == null)
        {
            printComponents = new HashSet<>();
        }
        return printComponents;
    }

    public void setPrintComponents(final Collection<PrintComponent> printComponents)
    {
        this.printComponents = new HashSet<>(printComponents);
    }

    public List<RenameTocEntry> getRenameTocEntries()
    {
        if (renameTocEntries == null)
        {
            renameTocEntries = new HashSet<>();
        }
        // Change to list
        final List<RenameTocEntry> labels = new ArrayList<>();
        labels.addAll(renameTocEntries);
        return labels;
    }

    public void setRenameTocEntries(final Collection<RenameTocEntry> renameTocEntries)
    {
        this.renameTocEntries = new HashSet<>(renameTocEntries);
    }

    public List<TableViewer> getTableViewers()
    {
        if (tableViewers == null)
        {
            tableViewers = new HashSet<>();
        }
        // Change to list
        final List<TableViewer> documents = new ArrayList<>();
        documents.addAll(tableViewers);
        return documents;
    }

    public void setTableViewers(final Collection<TableViewer> tableViewers)
    {
        this.tableViewers = new HashSet<>(tableViewers);
    }

    public List<DocumentCopyright> getDocumentCopyrights()
    {
        if (documentCopyrights == null)
        {
            documentCopyrights = new HashSet<>();
        }
        // Change to list
        final List<DocumentCopyright> copyrights = new ArrayList<>();
        copyrights.addAll(documentCopyrights);
        return copyrights;
    }

    public void setDocumentCopyrights(final Collection<DocumentCopyright> documentCopyrights)
    {
        this.documentCopyrights = new HashSet<>(documentCopyrights);
    }

    public List<DocumentCurrency> getDocumentCurrencies()
    {
        if (documentCurrencies == null)
        {
            documentCurrencies = new HashSet<>();
        }
        // Change to list
        final List<DocumentCurrency> documents = new ArrayList<>();
        documents.addAll(documentCurrencies);
        return documents;
    }

    public void setDocumentCurrencies(final Collection<DocumentCurrency> documentCurrencies)
    {
        this.documentCurrencies = new HashSet<>(documentCurrencies);
    }

    public List<FrontMatterPage> getFrontMatterPages()
    {
        if (frontMatterPages == null)
        {
            frontMatterPages = new java.util.LinkedHashSet<>();
        }

        // Sort by sequence numbers
        final List<FrontMatterPage> pageList = new ArrayList<>();
        pageList.addAll(frontMatterPages);
        Collections.sort(pageList);

        // Sort Sections
        for (final FrontMatterPage page : pageList)
        {
            // Sort by sequence numbers
            final List<FrontMatterSection> sections = new ArrayList<>();
            sections.addAll(page.getFrontMatterSections());
            Collections.sort(sections);
            page.setFrontMatterSections(sections);

            // Sort PDFs
            for (final FrontMatterSection section : sections)
            {
                // Sort by sequence numbers
                final List<FrontMatterPdf> pdfs = new ArrayList<>();
                pdfs.addAll(section.getPdfs());
                Collections.sort(pdfs);
                section.setPdfs(pdfs);
            }
        }

        return pageList;
    }

    public void setFrontMatterPages(final Collection<FrontMatterPage> frontMatterPage)
    {
        frontMatterPages = new java.util.LinkedHashSet<>(frontMatterPage);
    }

    public String getPrintSetNumber()
    {
        return printSetNumber;
    }

    public void setPrintSetNumber(final String printSetNumber)
    {
        this.printSetNumber = printSetNumber;
    }

    public BookDefinition()
    {
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
        setIsSplitLock(false);
    }

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    public void copy(final BookDefinition that)
    {
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
        setPublishDateText(that.getPublishDateText());
        setCurrency(that.getCurrency());
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
        setIsSplitLock(that.isSplitLock());
        setSubGroupHeading(that.getSubGroupHeading());
        setGroupName(that.getGroupName());
        setPrintSetNumber(that.getPrintSetNumber());
    }

    /**
     * Returns a textual representation of a bean.
     *
     */
    @Override
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder();

        buffer.append("ebookDefinitionId=[").append(ebookDefinitionId).append("] ");
        buffer.append("fullyQualifiedTitleId=[").append(fullyQualifiedTitleId).append("] ");
        buffer.append("proviewDisplayName=[").append(proviewDisplayName).append("] ");
        buffer.append("copyright=[").append(copyright).append("] ");
        buffer.append("copyrightPageText=[").append(copyrightPageText).append("] ");
        buffer.append("materialId=[").append(materialId).append("] ");
        buffer.append("isTocFlag=[").append(isTocFlag).append("] ");
        buffer.append("rootTocGuid=[").append(rootTocGuid).append("] ");
        buffer.append("docCollectionName=[").append(docCollectionName).append("] ");
        buffer.append("tocCollectionName=[").append(tocCollectionName).append("] ");
        buffer.append("nortDomain=[").append(nortDomain).append("] ");
        buffer.append("nortFilterView=[").append(nortFilterView).append("] ");
        buffer.append("coverImage=[").append(coverImage).append("] ");
        buffer.append("isbn=[").append(isbn).append("] ");
        buffer.append("publishDateText=[").append(publishDateText).append("] ");
        buffer.append("currency=[").append(currency).append("] ");
        buffer.append("isProviewTableViewFlag=[").append(isProviewTableViewFlag).append("] ");
        buffer.append("keyciteToplineFlag=[").append(keyciteToplineFlag).append("] ");
        buffer.append("autoUpdateSupportFlag=[").append(autoUpdateSupportFlag).append("] ");
        buffer.append("searchIndexFlag=[").append(searchIndexFlag).append("] ");
        buffer.append("onePassSsoLinkFlag=[").append(onePassSsoLinkFlag).append("] ");
        buffer.append("publishCutoffDate=[").append(publishCutoffDate).append("] ");
        buffer.append("ebookDefinitionCompleteFlag=[").append(ebookDefinitionCompleteFlag).append("] ");
        buffer.append("publishedOnceFlag=[").append(publishedOnceFlag).append("] ");
        buffer.append("isDeletedFlag=[").append(isDeletedFlag).append("] ");
        buffer.append("lastUpdated=[").append(lastUpdated).append("] ");
        buffer.append("frontMatterTocLabel=[").append(frontMatterTocLabel).append("] ");
        buffer.append("isAuthorDisplayVertical=[").append(isAuthorDisplayVertical).append("] ");
        buffer.append("additionalTrademarkInfo=[").append(additionalTrademarkInfo).append("] ");
        buffer.append("enableCopyFeatureFlag=[").append(enableCopyFeatureFlag).append("] ");
        buffer.append("pilotBookStatus=[").append(isPilotBook).append("] ");
        buffer.append("includeAnnotations=[").append(includeAnnotations).append("] ");
        buffer.append("includeNotesOfDecisions=[").append(includeNotesOfDecisions).append("] ");
        buffer.append("isFinalStage=[").append(isFinalStage).append("] ");
        buffer.append("useReloadContent=[").append(useReloadContent).append("] ");
        buffer.append("sourceType=[").append(sourceType).append("] ");
        buffer.append("cwbBookName=[").append(cwbBookName).append("] ");
        buffer.append("isInsStyleFlag=[").append(isInsStyleFlag).append("] ");
        buffer.append("isDelStyleFlag=[").append(isDelStyleFlag).append("] ");
        buffer.append("isRemoveEditorNoteHeadFlag=[").append(isRemoveEditorNoteHeadFlag).append("] ");
        buffer.append("frontMatterTheme=[").append(frontMatterTheme).append("] ");
        buffer.append("subGroupHeading=[").append(subGroupHeading).append("] ");
        buffer.append("groupName=[").append(groupName).append("] ");

        return buffer.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ebookDefinitionId == null) ? 0 : ebookDefinitionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof BookDefinition))
            return false;
        final BookDefinition equalCheck = (BookDefinition) obj;
        if ((ebookDefinitionId == null && equalCheck.ebookDefinitionId != null)
            || (ebookDefinitionId != null && equalCheck.ebookDefinitionId == null))
            return false;
        if (ebookDefinitionId != null && !ebookDefinitionId.equals(equalCheck.ebookDefinitionId))
            return false;
        return true;
    }

    /**
     * The base title ID, without any of the leading slash-separated namespace components.  Example: "ak_2010_federal".
     * This is a transient field because we are making the space-for-time tradeoff and
     * calculating this value once when the fullTitleId is set.  The TITLE_ID column in the database
     * holds the fully-qualified value.
     * @return the right-most component of the slash separated title ID path, or null if blank string.
     */
    @Transient
    public String getTitleId()
    {
        final StringTokenizer tokenizer = new StringTokenizer(fullyQualifiedTitleId, "/");
        String component = null;
        while (tokenizer.hasMoreTokens())
        {
            component = tokenizer.nextToken();
        }
        return (component);
    }

    /**
     * The proview features as derived from the book definition.
     * @return List of Feature.
     */
    @Transient
    public List<Feature> getProviewFeatures()
    {
        final List<Feature> proviewFeatures = new ArrayList<>();
        proviewFeatures.add(new Feature("Print"));
        if (getAutoUpdateSupportFlag())
            proviewFeatures.add(new Feature("AutoUpdate"));
        if (getSearchIndexFlag())
            proviewFeatures.add(new Feature("SearchIndex"));
        if (getEnableCopyFeatureFlag())
            proviewFeatures.add(new Feature("Copy"));
        if (getOnePassSsoLinkFlag())
        {
            proviewFeatures.add(new Feature("OnePassSSO", "www.westlaw.com"));
            proviewFeatures.add(new Feature("OnePassSSO", "next.westlaw.com"));
        }
        if (isSplitBook())
        {
            proviewFeatures.add(new Feature("FullAnchorMap"));
            proviewFeatures.add(new Feature("CombinedTOC"));
        }
        if (getSourceType() == SourceType.XPP)
        {
            proviewFeatures.add(new Feature("PageNos"));
            proviewFeatures.add(new Feature("SpanPages"));
        }
        return (proviewFeatures);
    }

    /**
     * The proview keywords as derived from the book definition.
     * @return List of keywords.
     */
    @Transient
    public List<Keyword> getKeyWords()
    {
        final List<Keyword> keywords = new ArrayList<>();
        final Collection<KeywordTypeValue> keywordValues = getKeywordTypeValues();
        for (final KeywordTypeValue value : keywordValues)
        {
            keywords.add(new Keyword(value.getKeywordTypeCode().getName(), value.getName()));
        }
        return (keywords);
    }

    /**
     * Provides the status of the book definition
     * @return String indicating the status
     */
    @Transient
    public String getBookStatus()
    {
        final String status;
        if (isDeletedFlag())
        {
            status = "Deleted";
        }
        else
        {
            if (getEbookDefinitionCompleteFlag())
            {
                status = "Ready";
            }
            else
            {
                status = "Incomplete";
            }
        }
        return status;
    }
}
