package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import org.apache.commons.lang3.StringUtils;

/**
 */

@Entity
@NamedQueries({
    @NamedQuery(
        name = "findEbookAuditByPrimaryKey",
        query = "select myEbookAudit from EbookAudit myEbookAudit where myEbookAudit.auditId = :auditId")})
@Table(name = "EBOOK_AUDIT")
public class EbookAudit implements Serializable {
    //private static final Logger log = LogManager.getLogger(EbookAudit.class);
    private static final long serialVersionUID = 2L;

    // Lowered the Max characters to account for some unicode characters
    private static final int MAX_CHARACTER_1024 = 1000;
    public static final int MAX_CHARACTER_2048 = 2000;

    public enum AUDIT_TYPE {
        DELETE,
        RESTORE,
        CREATE,
        EDIT,
        GROUP
    };

    @Column(name = "AUDIT_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "AuditSequence")
    @SequenceGenerator(name = "AuditSequence", sequenceName = "AUDIT_ID_SEQ")
    private Long auditId;

    @Column(name = "EBOOK_DEFINITION_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private Long ebookDefinitionId;

    @Column(name = "TITLE_ID", length = 40, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String titleId;

    @Column(name = "PROVIEW_DISPLAY_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String proviewDisplayName;

    @Column(name = "COPYRIGHT", length = 1024, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String copyright;

    @Column(name = "COPYRIGHT_PAGE_TEXT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String copyrightPageText;

    @Column(name = "MATERIAL_ID", length = 64, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String materialId;

    @Column(name = "IS_TOC_FLAG", length = 1, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isTocFlag;

    @Column(name = "ROOT_TOC_GUID", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String rootTocGuid;

    @Column(name = "DOC_COLLECTION_NAME", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String docCollectionName;

    @Column(name = "TOC_COLLECTION_NAME", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String tocCollectionName;

    @Column(name = "NORT_DOMAIN", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String nortDomain;

    @Column(name = "NORT_FILTER_VIEW", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String nortFilterView;

    @Column(name = "DOCUMENT_TYPE_CODES_ID")
    @Basic(fetch = FetchType.EAGER)
    private Long documentTypeCodesId;

    @Column(name = "COVER_IMAGE", length = 256)
    @Basic(fetch = FetchType.EAGER)
    private String coverImage;

    @Column(name = "ISBN", length = 64)
    @Basic(fetch = FetchType.EAGER)
    private String isbn;

    @Column(name = "PUBLISH_DATE_TEXT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String publishDateText;

    @Column(name = "PUBLISHER_CODES_ID")
    @Basic(fetch = FetchType.EAGER)

    private Long publisherCodesId;

    @Column(name = "CURRENCY", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String currency;

    @Column(name = "KEYCITE_TOPLINE_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)

    private String keyciteToplineFlag;

    @Column(name = "AUTO_UPDATE_SUPPORT_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String autoUpdateSupportFlag;

    @Column(name = "SEARCH_INDEX_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)

    private String searchIndexFlag;

    @Column(name = "FRONT_MATTER_THEME")
    @Basic(fetch = FetchType.EAGER)
    private String frontMatterTheme;

    @Column(name = "ONE_PASS_SSO_LINK_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String onePassSsoLinkFlag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISH_CUTOFF_DATE")
    @Basic(fetch = FetchType.EAGER)
    private Date publishCutoffDate;

    @Column(name = "EBOOK_DEFINITION_COMPLETE_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)

    private String ebookDefinitionCompleteFlag;

    @Column(name = "PUBLISHED_ONCE_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String publishedOnceFlag;

    @Column(name = "AUTHOR_NAMES_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)

    private String authorNamesConcat;

    @Column(name = "PILOT_BOOKS_CONCAT", length = 1024)
    @Basic(fetch = FetchType.EAGER)

    private String pilotBooksConcat;

    @Column(name = "BOOK_NAMES_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String bookNamesConcat;

    @Column(name = "KEYWORDS_CONCAT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String keywordsConcat;

    @Column(name = "AUDIT_NOTE", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String auditNote;

    @Column(name = "AUDIT_TYPE", length = 10)
    @Basic(fetch = FetchType.EAGER)
    private String auditType;

    @Column(name = "UPDATED_BY", length = 32, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String updatedBy;

    @Column(name = "IS_DELETED_FLAG", length = 1, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isDeletedFlag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private Date lastUpdated;

    @Column(name = "FRONT_MATTER_TOC_LABEL", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String frontMatterTocLabel;

    @Column(name = "AUTHOR_DISPLAY_VERTICAL_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String authorDisplayVerticalFlag;

    @Column(name = "ENABLE_COPY_FEATURE_FLAG", length = 1, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String enableCopyFeatureFlag;

    @Column(name = "FRONT_MATTER_CONCAT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String frontMatterConcat;

    @Column(name = "ADDITIONAL_TRADEMARK_INFO", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String additionalTrademarkInfo;

    @Column(name = "IS_PILOT_BOOK", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String pilotBookStatus;

    @Column(name = "INCLUDE_ANNOTATIONS", length = 1)
    private String includeAnnotations;

    @Column(name = "INCLUDE_NOTES_OF_DECISIONS", length = 1)
    private String includeNotesOfDecisions;

    @Column(name = "EXCLUDED_DOCUMENTS_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String excludeDocumentsConcat;

    @Column(name = "RENAME_TOC_ENTRY_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String renameTocEntryConcat;

    @Column(name = "TABLE_VIEWER_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String tableViewerConcat;

    @Column(name = "DOCUMENT_COPYRIGHT_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String documentCopyrightConcat;

    @Column(name = "DOCUMENT_CURRENCY_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String documentCurrencyConcat;

    @Column(name = "IS_FINAL_STAGE", length = 1)
    private String isFinalStage;

    @Column(name = "USE_RELOAD_CONTENT", length = 1)
    private String useReloadContent;

    @Column(name = "NORT_FILE_LOCATION_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String nortFileLocationConcat;

    @Column(name = "SOURCE_TYPE", length = 10)
    private String sourceType;

    @Column(name = "CWB_BOOK_NAME", length = 1028)
    private String cwbBookName;

    @Column(name = "IS_INS_STYLE_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String isInsStyleFlag;

    @Column(name = "IS_DEL_STYLE_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String isDelStyleFlag;

    @Column(name = "IS_REMOVE_EDNOTE_HEAD_FLAG")
    @Basic(fetch = FetchType.EAGER)
    private String isRemoveEditorNoteHeadFlag;

    @Column(name = "IS_SPLIT_BOOK")
    @Basic(fetch = FetchType.EAGER)
    private String isSplitBook;

    @Column(name = "IS_SPLIT_TYPE_AUTO")
    @Basic(fetch = FetchType.EAGER)
    private String isSplitTypeAuto;

    @Column(name = "SPLIT_EBOOK_PARTS")
    @Basic(fetch = FetchType.EAGER)
    private Integer splitEBookParts;

    @Column(name = "SPLIT_DOCUMENTS_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    private String splitDocumentsConcat;

    @Column(name = "SUBGROUP_HEADING")
    @Basic(fetch = FetchType.EAGER)
    private String subGroupHeading;

    @Column(name = "GROUP_NAME")
    @Basic(fetch = FetchType.EAGER)
    private String groupName;

    @Column(name = "PRINT_SET_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    private String printSetNumber;

    public void setAuditId(final Long auditId) {
        this.auditId = auditId;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyrightPageText(final String copyrightPageText) {
        this.copyrightPageText = copyrightPageText;
    }

    public String getCopyrightPageText() {
        return copyrightPageText;
    }

    public void setMaterialId(final String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setIsTocFlag(final boolean isTocFlag) {
        this.isTocFlag = ((isTocFlag) ? "Y" : "N");
    }

    public boolean getIsTocFlag() {
        return ((isTocFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setRootTocGuid(final String rootTocGuid) {
        this.rootTocGuid = rootTocGuid;
    }

    public String getRootTocGuid() {
        return rootTocGuid;
    }

    public void setDocCollectionName(final String docCollectionName) {
        this.docCollectionName = docCollectionName;
    }

    public String getDocCollectionName() {
        return docCollectionName;
    }

    public void setTocCollectionName(final String tocCollectionName) {
        this.tocCollectionName = tocCollectionName;
    }

    public String getTocCollectionName() {
        return tocCollectionName;
    }

    public void setNortDomain(final String nortDomain) {
        this.nortDomain = nortDomain;
    }

    public String getNortDomain() {
        return nortDomain;
    }

    public void setNortFilterView(final String nortFilterView) {
        this.nortFilterView = nortFilterView;
    }

    public String getNortFilterView() {
        return nortFilterView;
    }

    public void setDocumentTypeCodesId(final Long documentTypeCodesId) {
        this.documentTypeCodesId = documentTypeCodesId;
    }

    public Long getDocumentTypeCodesId() {
        return documentTypeCodesId;
    }

    public void setCoverImage(final String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setPublishDateText(final String publishDateText) {
        this.publishDateText = publishDateText;
    }

    public String getPublishDateText() {
        return publishDateText;
    }

    public void setPublisherCodesId(final Long publisherCodesId) {
        this.publisherCodesId = publisherCodesId;
    }

    public Long getPublisherCodesId() {
        return publisherCodesId;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setKeyciteToplineFlag(final boolean keyciteToplineFlag) {
        this.keyciteToplineFlag = ((keyciteToplineFlag) ? "Y" : "N");
    }

    public boolean getKeyciteToplineFlag() {
        return ((keyciteToplineFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setAutoUpdateSupportFlag(final boolean autoUpdateSupportFlag) {
        this.autoUpdateSupportFlag = ((autoUpdateSupportFlag) ? "Y" : "N");
    }

    public boolean getAutoUpdateSupportFlag() {
        return ((autoUpdateSupportFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setSearchIndexFlag(final boolean searchIndexFlag) {
        this.searchIndexFlag = ((searchIndexFlag) ? "Y" : "N");
    }

    public boolean getSearchIndexFlag() {
        return ((searchIndexFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setOnePassSsoLinkFlag(final boolean onePassSsoLinkFlag) {
        this.onePassSsoLinkFlag = ((onePassSsoLinkFlag) ? "Y" : "N");
    }

    public boolean getOnePassSsoLinkFlag() {
        return ((onePassSsoLinkFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setEbookDefinitionCompleteFlag(final boolean ebookDefinitionCompleteFlag) {
        this.ebookDefinitionCompleteFlag = ((ebookDefinitionCompleteFlag) ? "Y" : "N");
    }

    public boolean getEbookDefinitionCompleteFlag() {
        return ((ebookDefinitionCompleteFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setPublishedOnceFlag(final boolean publishedOnceFlag) {
        this.publishedOnceFlag = ((publishedOnceFlag) ? "Y" : "N");
    }

    public boolean getPublishedOnceFlag() {
        return ((publishedOnceFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setAuthorNamesConcat(final String authorNamesConcat) {
        this.authorNamesConcat = authorNamesConcat;
    }

    public String getAuthorNamesConcat() {
        return authorNamesConcat;
    }

    public String getPilotBooksConcat() {
        return pilotBooksConcat;
    }

    public void setPilotBooksConcat(final String pilotBookConcat) {
        pilotBooksConcat = pilotBookConcat;
    }

    public void setBookNamesConcat(final String bookNamesConcat) {
        this.bookNamesConcat = bookNamesConcat;
    }

    public String getBookNamesConcat() {
        return bookNamesConcat;
    }

    public void setKeywordsConcat(final String keywordsConcat) {
        this.keywordsConcat = keywordsConcat;
    }

    public String getKeywordsConcat() {
        return keywordsConcat;
    }

    public void setFrontMatterConcat(final String frontMatterConcat) {
        this.frontMatterConcat = frontMatterConcat;
    }

    public String getFrontMatterConcat() {
        return frontMatterConcat;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setAuditNote(final String auditNote) {
        this.auditNote = auditNote;
    }

    public String getAuditNote() {
        return auditNote;
    }

    public void setAuditType(final String auditType) {
        this.auditType = auditType;
    }

    public String getAuditType() {
        return auditType;
    }

    public EbookAudit() {
    }

    public String getFrontMatterTheme() {
        return frontMatterTheme;
    }

    public void setFrontMatterTheme(final String frontMatterTheme) {
        this.frontMatterTheme = frontMatterTheme;
    }

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    public void copy(final EbookAudit that) {
        setAuditId(that.getAuditId());
        setEbookDefinitionId(that.getEbookDefinitionId());
        setLastUpdated(that.getLastUpdated());
        setTitleId(that.getTitleId());
        setProviewDisplayName(that.getProviewDisplayName());
        setCopyright(that.getCopyright());
        setCopyrightPageText(that.getCopyrightPageText());
        setMaterialId(that.getMaterialId());
        setIsTocFlag(that.getIsTocFlag());
        setRootTocGuid(that.getRootTocGuid());
        setDocCollectionName(that.getDocCollectionName());
        setTocCollectionName(that.getTocCollectionName());
        setNortDomain(that.getNortDomain());
        setNortFilterView(that.getNortFilterView());
        setDocumentTypeCodesId(that.getDocumentTypeCodesId());
        setCoverImage(that.getCoverImage());
        setIsbn(that.getIsbn());
        setPublishDateText(that.getPublishDateText());
        setPublisherCodesId(that.getPublisherCodesId());
        setCurrency(that.getCurrency());
        setKeyciteToplineFlag(that.getKeyciteToplineFlag());
        setAutoUpdateSupportFlag(that.getAutoUpdateSupportFlag());
        setSearchIndexFlag(that.getSearchIndexFlag());
        setOnePassSsoLinkFlag(that.getOnePassSsoLinkFlag());
        setPublishCutoffDate(that.getPublishCutoffDate());
        setEbookDefinitionCompleteFlag(that.getEbookDefinitionCompleteFlag());
        setPublishedOnceFlag(that.getPublishedOnceFlag());
        setAuthorNamesConcat(that.getAuthorNamesConcat());
        setPilotBooksConcat(that.getPilotBooksConcat());
        setBookNamesConcat(that.getBookNamesConcat());
        setKeywordsConcat(that.getKeywordsConcat());
        setAuditNote(that.getAuditNote());
        setAuditType(that.getAuditType());
        setIsDeletedFlag(that.getIsDeletedFlag());
        setUpdatedBy(that.getUpdatedBy());
        setFrontMatterTocLabel(that.getFrontMatterTocLabel());
        setAuthorDisplayVerticalFlag(that.getAuthorDisplayVerticalFlag());
        setEnableCopyFeatureFlag(that.getEnableCopyFeatureFlag());
        setFrontMatterConcat(that.getFrontMatterConcat());
        setAdditionalTrademarkInfo(that.getAdditionalTrademarkInfo());
        setPilotBookStatus(that.getPilotBookStatus());
        setExcludeDocumentsConcat(that.getExcludeDocumentsConcat());
        setSplitDocumentsConcat(that.getSplitDocumentsConcat());
        setIncludeAnnotations(that.getIncludeAnnotations());
        setIncludeNotesOfDecisions(that.getIncludeNotesOfDecisions());
        setRenameTocEntryConcat(that.getRenameTocEntryConcat());
        setTableViewerConcat(that.getTableViewerConcat());
        setDocumentCopyrightConcat(that.getDocumentCopyrightConcat());
        setDocumentCurrencyConcat(that.getDocumentCurrencyConcat());
        setIsFinalStage(that.isFinalStage());
        setUseReloadContent(that.getUseReloadContent());
        setNortFileLocationConcat(that.getNortFileLocationConcat());
        setSourceType(that.getSourceType());
        setCwbBookName(that.getCwbBookName());
        setIsInsStyleFlag(that.isInsStyleFlag());
        setIsDelStyleFlag(that.isDelStyleFlag());
        setIsRemoveEditorNoteHeadFlag(that.isRemoveEditorNoteHeadFlag());
        setFrontMatterTheme(that.frontMatterTheme);
        setIsSplitBook(that.isSplitBook());
        setIsSplitTypeAuto(that.isSplitTypeAuto());
        setSplitEBookParts(that.getSplitEBookParts());
        setSubGroupHeading(that.getSubGroupHeading());
        setGroupName(that.getGroupName());
        setPrintSetNumber(that.getPrintSetNumber());
    }

    /**
     * Copies the contents of the BookDefinition into this bean.
     *
     */
    @Transient
    public void loadBookDefinition(
        final BookDefinition that,
        final AUDIT_TYPE auditType,
        final String user,
        final String note) {
        setEbookDefinitionId(that.getEbookDefinitionId());
        setTitleId(that.getFullyQualifiedTitleId());
        setProviewDisplayName(that.getProviewDisplayName());
        setCopyright(that.getCopyright());
        setCopyrightPageText(that.getCopyrightPageText());
        setMaterialId(that.getMaterialId());
        // TODO: remove setIsTocFlag once column is removed from table
        setIsTocFlag(true);
        setRootTocGuid(that.getRootTocGuid());
        setDocCollectionName(that.getDocCollectionName());
        setTocCollectionName(that.getTocCollectionName());
        setNortDomain(that.getNortDomain());
        setNortFilterView(that.getNortFilterView());
        setDocumentTypeCodesId(that.getDocumentTypeCodes().getId());
        setCoverImage(that.getCoverImage());
        setIsbn(that.getIsbn());
        setPublishDateText(that.getPublishDateText());
        setPublisherCodesId(that.getPublisherCodes().getId());
        setCurrency(that.getCurrency());
        setKeyciteToplineFlag(that.getKeyciteToplineFlag());
        setAutoUpdateSupportFlag(that.getAutoUpdateSupportFlag());
        setSearchIndexFlag(that.getSearchIndexFlag());
        setOnePassSsoLinkFlag(that.getOnePassSsoLinkFlag());
        setPublishCutoffDate(that.getPublishCutoffDate());
        setEbookDefinitionCompleteFlag(that.getEbookDefinitionCompleteFlag());
        setPublishedOnceFlag(that.getPublishedOnceFlag());
        setAuthorNamesConcat(maxString(concatString(that.getAuthors()), MAX_CHARACTER_2048));
        setPilotBooksConcat(maxString(concatString(that.getPilotBooks()), MAX_CHARACTER_1024));
        setBookNamesConcat(maxString(concatString(that.getEbookNames()), MAX_CHARACTER_2048));
        setKeywordsConcat(maxString(concatString(that.getKeywordTypeValues()), MAX_CHARACTER_1024));
        setAuditNote(note);
        setAuditType(auditType.toString());
        setUpdatedBy(user);
        setIsDeletedFlag(that.isDeletedFlag());
        setLastUpdated(that.getLastUpdated());
        setFrontMatterTocLabel(that.getFrontMatterTocLabel());
        setAuthorDisplayVerticalFlag(that.isAuthorDisplayVertical());
        setEnableCopyFeatureFlag(that.getEnableCopyFeatureFlag());
        setFrontMatterConcat(concatString(that.getFrontMatterPages()));
        setAdditionalTrademarkInfo(that.getAdditionalTrademarkInfo());
        setPilotBookStatus(that.getPilotBookStatus());
        setExcludeDocumentsConcat(maxString(concatString(that.getExcludeDocuments()), MAX_CHARACTER_2048));
        setSplitDocumentsConcat(maxString(concatString(that.getSplitDocumentsAsList()), MAX_CHARACTER_2048));
        setRenameTocEntryConcat(maxString(concatString(that.getRenameTocEntries()), MAX_CHARACTER_2048));
        setTableViewerConcat(maxString(concatString(that.getTableViewers()), MAX_CHARACTER_2048));
        setDocumentCopyrightConcat(maxString(concatString(that.getDocumentCopyrights()), MAX_CHARACTER_2048));
        setDocumentCurrencyConcat(maxString(concatString(that.getDocumentCurrencies()), MAX_CHARACTER_2048));
        setIsFinalStage(that.isFinalStage());
        setIncludeAnnotations(that.getIncludeAnnotations());
        setIncludeNotesOfDecisions(that.getIncludeNotesOfDecisions());
        setUseReloadContent(that.getUseReloadContent());
        setNortFileLocationConcat(maxString(concatString(that.getNortFileLocations()), MAX_CHARACTER_2048));
        setSourceType(that.getSourceType());
        setCwbBookName(that.getCwbBookName());
        setIsInsStyleFlag(that.isInsStyleFlag());
        setIsDelStyleFlag(that.isDelStyleFlag());
        setIsRemoveEditorNoteHeadFlag(that.isRemoveEditorNoteHeadFlag());
        setFrontMatterTheme(that.frontMatterTheme);
        setIsSplitBook(that.isSplitBook());
        setIsSplitTypeAuto(that.isSplitTypeAuto());
        setSplitEBookParts(that.getSplitEBookParts());
        setSubGroupHeading(that.getSubGroupHeading());
        setGroupName(that.getGroupName());
        setPrintSetNumber(that.getPrintSetNumber());
    }

    @Transient
    private String maxString(final String buffer, final int maxCharacters) {
        return StringUtils.abbreviate(buffer.toString(), maxCharacters);
    }

    @Transient
    private String concatString(final Collection<?> collection) {
        final StringBuilder buffer = new StringBuilder();
        for (final Object item : collection) {
            buffer.append(item.toString());
            buffer.append(", ");
        }

        return buffer.toString();
    }

    /**
     * Returns a textual representation of a bean.
     *
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        buffer.append("auditId=[").append(auditId).append("] ");
        buffer.append("ebookDefinitionId=[").append(ebookDefinitionId).append("] ");
        buffer.append("lastUpdated=[").append(lastUpdated).append("] ");
        buffer.append("titleId=[").append(titleId).append("] ");
        buffer.append("proviewDisplayName=[").append(proviewDisplayName).append("] ");
        buffer.append("copyright=[").append(copyright).append("] ");
        buffer.append("copyrightPageText=[").append(copyrightPageText).append("] ");
        buffer.append("materialId=[").append(materialId).append("] ");
        buffer.append("rootTocGuid=[").append(rootTocGuid).append("] ");
        buffer.append("docCollectionName=[").append(docCollectionName).append("] ");
        buffer.append("tocCollectionName=[").append(tocCollectionName).append("] ");
        buffer.append("nortDomain=[").append(nortDomain).append("] ");
        buffer.append("nortFilterView=[").append(nortFilterView).append("] ");
        buffer.append("documentTypeCodesId=[").append(documentTypeCodesId).append("] ");
        buffer.append("coverImage=[").append(coverImage).append("] ");
        buffer.append("isbn=[").append(isbn).append("] ");
        buffer.append("publishDateText=[").append(publishDateText).append("] ");
        buffer.append("publisherCodesId=[").append(publisherCodesId).append("] ");
        buffer.append("currency=[").append(currency).append("] ");
        buffer.append("keyciteToplineFlag=[").append(keyciteToplineFlag).append("] ");
        buffer.append("autoUpdateSupportFlag=[").append(autoUpdateSupportFlag).append("] ");
        buffer.append("searchIndexFlag=[").append(searchIndexFlag).append("] ");
        buffer.append("onePassSsoLinkFlag=[").append(onePassSsoLinkFlag).append("] ");
        buffer.append("publishCutoffDate=[").append(publishCutoffDate).append("] ");
        buffer.append("ebookDefinitionCompleteFlag=[").append(ebookDefinitionCompleteFlag).append("] ");
        buffer.append("publishedOnceFlag=[").append(publishedOnceFlag).append("] ");
        buffer.append("authorNamesConcat=[").append(authorNamesConcat).append("] ");
        buffer.append("bookNamesConcat=[").append(bookNamesConcat).append("] ");
        buffer.append("keywordsConcat=[").append(keywordsConcat).append("] ");
        buffer.append("frontMatterConcat=[").append(frontMatterConcat).append("] ");
        buffer.append("updatedBy=[").append(updatedBy).append("] ");
        buffer.append("auditNote=[").append(auditNote).append("] ");
        buffer.append("auditType=[").append(auditType).append("] ");
        buffer.append("isDeltedFlag=[").append(isDeletedFlag).append("] ");
        buffer.append("frontMatterTocLabel=[").append(frontMatterTocLabel).append("] ");
        buffer.append("authorDisplayVerticalFlag=[").append(authorDisplayVerticalFlag).append("] ");
        buffer.append("enableCopyFeatureFlag=[").append(enableCopyFeatureFlag).append("] ");
        buffer.append("additionalTrademarkInfo=[").append(additionalTrademarkInfo).append("] ");
        buffer.append("pilotBookStatus=[").append(pilotBookStatus).append("] ");
        buffer.append("excludeDocumentsConcat=[").append(excludeDocumentsConcat).append("] ");
        buffer.append("splitDocumentsConcat=[").append(splitDocumentsConcat).append("] ");
        buffer.append("includeAnnotations=[").append(includeAnnotations).append("] ");
        buffer.append("includeNotesOfDecisions=[").append(includeNotesOfDecisions).append("] ");
        buffer.append("renameTocEntryConcat=[").append(renameTocEntryConcat).append("] ");
        buffer.append("tableViewerConcat=[").append(tableViewerConcat).append("] ");
        buffer.append("isFinalStage=[").append(isFinalStage).append("] ");
        buffer.append("documentCopyrightConcat=[").append(documentCopyrightConcat).append("] ");
        buffer.append("documentCurrencyConcat=[").append(documentCurrencyConcat).append("] ");
        buffer.append("useReloadContent=[").append(useReloadContent).append("] ");
        buffer.append("nortFileLocationConcat=[").append(nortFileLocationConcat).append("] ");
        buffer.append("sourceType=[").append(sourceType).append("] ");
        buffer.append("cwbBookName=[").append(cwbBookName).append("] ");
        buffer.append("isInsStyleFlag=[").append(isInsStyleFlag).append("] ");
        buffer.append("isDelStyleFlag=[").append(isDelStyleFlag).append("] ");
        buffer.append("isRemoveEditorNoteHeadFlag=[").append(isRemoveEditorNoteHeadFlag).append("] ");
        buffer.append("frontMatterTheme=[").append(frontMatterTheme).append("] ");
        buffer.append("isSplitTypeAuto=[").append(isSplitTypeAuto).append("] ");
        buffer.append("isSplitBook=[").append(isSplitBook).append("] ");
        buffer.append("SplitEBookParts=[").append(splitEBookParts).append("] ");
        buffer.append("subGroupHeading=[").append(subGroupHeading).append("] ");
        buffer.append("groupName=[").append(groupName).append("] ");
        buffer.append("printSetNumber=[").append(printSetNumber).append("] ");
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((auditId == null) ? 0 : auditId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof EbookAudit))
            return false;
        final EbookAudit equalCheck = (EbookAudit) obj;
        if ((auditId == null && equalCheck.auditId != null) || (auditId != null && equalCheck.auditId == null))
            return false;
        if (auditId != null && !auditId.equals(equalCheck.auditId))
            return false;
        return true;
    }

    public Long getEbookDefinitionId() {
        return ebookDefinitionId;
    }

    public void setEbookDefinitionId(final Long ebookDefinitionId) {
        this.ebookDefinitionId = ebookDefinitionId;
    }

    public void setProviewDisplayName(final String proviewDisplayName) {
        this.proviewDisplayName = proviewDisplayName;
    }

    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    public void setPublishCutoffDate(final Date publishCutoffDate) {
        this.publishCutoffDate = publishCutoffDate;
    }

    public Date getPublishCutoffDate() {
        return publishCutoffDate;
    }

    public void setIsDeletedFlag(final boolean isDeletedFlag) {
        this.isDeletedFlag = ((isDeletedFlag) ? "Y" : "N");
    }

    public boolean getIsDeletedFlag() {
        return ((isDeletedFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public boolean getEnableCopyFeatureFlag() {
        return ((enableCopyFeatureFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setEnableCopyFeatureFlag(final boolean enableCopyFeatureFlag) {
        this.enableCopyFeatureFlag = ((enableCopyFeatureFlag) ? "Y" : "N");
    }

    public boolean getAuthorDisplayVerticalFlag() {
        return ((authorDisplayVerticalFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setAuthorDisplayVerticalFlag(final boolean authorDisplayVerticalFlag) {
        this.authorDisplayVerticalFlag = ((authorDisplayVerticalFlag) ? "Y" : "N");
    }

    public String getAdditionalTrademarkInfo() {
        return additionalTrademarkInfo;
    }

    public void setAdditionalTrademarkInfo(final String additionalTrademarkInfo) {
        this.additionalTrademarkInfo = additionalTrademarkInfo;
    }

    public void setIncludeAnnotations(final boolean includeAnnotations) {
        this.includeAnnotations = ((includeAnnotations) ? "Y" : "N");
    }

    public boolean getIncludeAnnotations() {
        return ((includeAnnotations.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIncludeNotesOfDecisions(final boolean includeNotesOfDecisions) {
        this.includeNotesOfDecisions = ((includeNotesOfDecisions) ? "Y" : "N");
    }

    public boolean getIncludeNotesOfDecisions() {
        return ((includeNotesOfDecisions.equalsIgnoreCase("Y") ? true : false));
    }

    public PilotBookStatus getPilotBookStatus() {
        if (StringUtils.isBlank(pilotBookStatus)) {
            return PilotBookStatus.FALSE;
        } else {
            if (pilotBookStatus.equalsIgnoreCase("Y")) {
                return PilotBookStatus.TRUE;
            } else if (pilotBookStatus.equalsIgnoreCase("I")) {
                return PilotBookStatus.IN_PROGRESS;
            } else {
                return PilotBookStatus.FALSE;
            }
        }
    }

    public void setPilotBookStatus(final PilotBookStatus status) {
        switch (status) {
        case TRUE:
            pilotBookStatus = "Y";
            break;
        case IN_PROGRESS:
            pilotBookStatus = "I";
            break;
        default:
            pilotBookStatus = "N";
            break;
        }
    }

    public String getExcludeDocumentsConcat() {
        return excludeDocumentsConcat;
    }

    public void setExcludeDocumentsConcat(final String excludeDocumentsConcat) {
        this.excludeDocumentsConcat = excludeDocumentsConcat;
    }

    public String getSplitDocumentsConcat() {
        return splitDocumentsConcat;
    }

    public void setSplitDocumentsConcat(final String splitDocumentsConcat) {
        this.splitDocumentsConcat = splitDocumentsConcat;
    }

    public String getRenameTocEntryConcat() {
        return renameTocEntryConcat;
    }

    public void setRenameTocEntryConcat(final String renameTocEntryConcat) {
        this.renameTocEntryConcat = renameTocEntryConcat;
    }

    public String getTableViewerConcat() {
        return tableViewerConcat;
    }

    public void setTableViewerConcat(final String tableViewerConcat) {
        this.tableViewerConcat = tableViewerConcat;
    }

    public String getFrontMatterTocLabel() {
        return frontMatterTocLabel;
    }

    public void setFrontMatterTocLabel(final String frontMatterTocLabel) {
        this.frontMatterTocLabel = frontMatterTocLabel;
    }

    public String getDocumentCopyrightConcat() {
        return documentCopyrightConcat;
    }

    public void setDocumentCopyrightConcat(final String documentCopyrightConcat) {
        this.documentCopyrightConcat = documentCopyrightConcat;
    }

    public String getDocumentCurrencyConcat() {
        return documentCurrencyConcat;
    }

    public void setDocumentCurrencyConcat(final String documentCurrencyConcat) {
        this.documentCurrencyConcat = documentCurrencyConcat;
    }

    public boolean isFinalStage() {
        return ((isFinalStage.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsFinalStage(final boolean isFinalStage) {
        this.isFinalStage = ((isFinalStage) ? "Y" : "N");
    }

    public boolean getUseReloadContent() {
        return ((useReloadContent.equalsIgnoreCase("Y") ? true : false));
    }

    public void setUseReloadContent(final boolean isFinalStage) {
        useReloadContent = ((isFinalStage) ? "Y" : "N");
    }

    public void setNortFileLocationConcat(final String nortFileLocationConcat) {
        this.nortFileLocationConcat = nortFileLocationConcat;
    }

    public String getNortFileLocationConcat() {
        return nortFileLocationConcat;
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

    public String getCwbBookName() {
        return cwbBookName;
    }

    public void setCwbBookName(final String cwbBookName) {
        this.cwbBookName = cwbBookName;
    }

    public void setIsInsStyleFlag(final boolean isInsStyleFlag) {
        this.isInsStyleFlag = ((isInsStyleFlag) ? "Y" : "N");
    }

    public boolean isInsStyleFlag() {
        return ((isInsStyleFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsDelStyleFlag(final boolean isDelStyleFlag) {
        this.isDelStyleFlag = ((isDelStyleFlag) ? "Y" : "N");
    }

    public boolean isDelStyleFlag() {
        return ((isDelStyleFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsRemoveEditorNoteHeadFlag(final boolean isRemoveEditorNoteHeadFlag) {
        this.isRemoveEditorNoteHeadFlag = ((isRemoveEditorNoteHeadFlag) ? "Y" : "N");
    }

    public boolean isRemoveEditorNoteHeadFlag() {
        return ((isRemoveEditorNoteHeadFlag.equalsIgnoreCase("Y") ? true : false));
    }

    public boolean isSplitBook() {
        return ((isSplitBook.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsSplitBook(final boolean isSplitBook) {
        this.isSplitBook = ((isSplitBook) ? "Y" : "N");
    }

    public boolean isSplitTypeAuto() {
        return ((isSplitTypeAuto.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsSplitTypeAuto(final boolean isSplitTypeAuto) {
        this.isSplitTypeAuto = ((isSplitTypeAuto) ? "Y" : "N");
    }

    public Integer getSplitEBookParts() {
        return splitEBookParts;
    }

    public void setSplitEBookParts(final Integer splitEBookParts) {
        this.splitEBookParts = splitEBookParts;
    }

    public String getSubGroupHeading() {
        return subGroupHeading;
    }

    public void setSubGroupHeading(final String subGroupHeading) {
        this.subGroupHeading = subGroupHeading;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public String getPrintSetNumber() {
        return printSetNumber;
    }

    public void setPrintSetNumber(final String printSetNumber) {
        this.printSetNumber = printSetNumber;
    }
}
