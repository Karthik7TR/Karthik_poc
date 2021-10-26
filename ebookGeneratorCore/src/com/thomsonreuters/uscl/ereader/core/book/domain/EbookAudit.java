package com.thomsonreuters.uscl.ereader.core.book.domain;

import static com.thomsonreuters.uscl.ereader.util.ValueConverter.getStringForBooleanValue;
import static com.thomsonreuters.uscl.ereader.util.ValueConverter.isEqualsYes;

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
import lombok.Getter;
import lombok.Setter;
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
    @Getter @Setter
    private Long auditId;

    @Column(name = "EBOOK_DEFINITION_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Long ebookDefinitionId;

    @Column(name = "TITLE_ID", length = 40, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String titleId;

    @Column(name = "PROVIEW_DISPLAY_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String proviewDisplayName;

    @Column(name = "COPYRIGHT", length = 1024, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String copyright;

    @Column(name = "COPYRIGHT_PAGE_TEXT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String copyrightPageText;

    @Column(name = "MATERIAL_ID", length = 64, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String materialId;

    @Column(name = "IS_TOC_FLAG", length = 1, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isTocFlag;

    @Column(name = "ROOT_TOC_GUID", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String rootTocGuid;

    @Column(name = "DOC_COLLECTION_NAME", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String docCollectionName;

    @Column(name = "TOC_COLLECTION_NAME", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String tocCollectionName;

    @Column(name = "NORT_DOMAIN", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String nortDomain;

    @Column(name = "NORT_FILTER_VIEW", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String nortFilterView;

    @Column(name = "DOCUMENT_TYPE_CODES_ID")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Long documentTypeCodesId;

    @Column(name = "COVER_IMAGE", length = 256)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String coverImage;

    @Column(name = "ISBN", length = 64)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String isbn;

    @Column(name = "ISSN", length = 20)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String issn;

    @Column(name = "PUBLISH_DATE_TEXT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String publishDateText;

    @Column(name = "PUBLISHER_CODES_ID")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Long publisherCodesId;

    @Column(name = "CURRENCY", length = 2048)
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
    @Getter @Setter
    private String frontMatterTheme;

    @Column(name = "ONE_PASS_SSO_LINK_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String onePassSsoLinkFlag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PUBLISH_CUTOFF_DATE")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Date publishCutoffDate;

    @Column(name = "EBOOK_DEFINITION_COMPLETE_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)

    private String ebookDefinitionCompleteFlag;

    @Column(name = "PUBLISHED_ONCE_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String publishedOnceFlag;

    @Column(name = "AUTHOR_NAMES_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String authorNamesConcat;

    @Column(name = "PILOT_BOOKS_CONCAT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String pilotBooksConcat;

    @Column(name = "BOOK_NAMES_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String bookNamesConcat;

    @Column(name = "KEYWORDS_CONCAT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String keywordsConcat;

    @Column(name = "AUDIT_NOTE", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String auditNote;

    @Column(name = "AUDIT_TYPE", length = 10)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String auditType;

    @Column(name = "UPDATED_BY", length = 32, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String updatedBy;

    @Column(name = "IS_DELETED_FLAG", length = 1, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isDeletedFlag;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Date lastUpdated;

    @Column(name = "FRONT_MATTER_TOC_LABEL", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String frontMatterTocLabel;

    @Column(name = "AUTHOR_DISPLAY_VERTICAL_FLAG", length = 1)
    @Basic(fetch = FetchType.EAGER)
    private String authorDisplayVerticalFlag;

    @Column(name = "ENABLE_COPY_FEATURE_FLAG", length = 1, nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String enableCopyFeatureFlag;

    @Column(name = "FRONT_MATTER_CONCAT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String frontMatterConcat;

    @Column(name = "ADDITIONAL_TRADEMARK_INFO", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
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
    @Getter @Setter
    private String excludeDocumentsConcat;

    @Column(name = "RENAME_TOC_ENTRY_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String renameTocEntryConcat;

    @Column(name = "TABLE_VIEWER_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String tableViewerConcat;

    @Column(name = "DOCUMENT_COPYRIGHT_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String documentCopyrightConcat;

    @Column(name = "DOCUMENT_CURRENCY_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String documentCurrencyConcat;

    @Column(name = "IS_FINAL_STAGE", length = 1)
    private String isFinalStage;

    @Column(name = "USE_RELOAD_CONTENT", length = 1)
    private String useReloadContent;

    @Column(name = "NORT_FILE_LOCATION_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String nortFileLocationConcat;

    @Column(name = "SOURCE_TYPE", length = 10)
    private String sourceType;

    @Column(name = "CWB_BOOK_NAME", length = 1028)
    @Getter @Setter
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
    @Getter @Setter
    private Integer splitEBookParts;

    @Column(name = "SPLIT_DOCUMENTS_CONCAT", length = 2048)
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String splitDocumentsConcat;

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

    @Column(name = "PRINT_PAGE_NUMBERS")
    @Basic(fetch = FetchType.EAGER)
    private String printPageNumbers;

    @Column(name = "INLINE_TOC_INCLUDED")
    @Basic(fetch = FetchType.EAGER)
    private String inlineTocIncluded;

    @Column(name = "INDEX_INCLUDED")
    @Basic(fetch = FetchType.EAGER)
    private String indexIncluded;

    @Column(name="INDEX_TOC_COLLECTION_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String indexTocCollectionName;

    @Column(name="INDEX_DOC_COLLECTION_NAME")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String indexDocCollectionName;

    @Column(name="INDEX_TOC_ROOT_GUID")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String indexTocRootGuid;

    @Column(name="SUBSTITUTE_TOC_HEADERS_LEVEL")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private Integer substituteTocHeadersLevel;

    @Column(name="TITLE_PAGE_IMAGE_INCLUDED")
    @Basic(fetch = FetchType.EAGER)
    @Getter @Setter
    private String titlePageImageIncluded;

    public void setIsTocFlag(final boolean isTocFlag) {
        this.isTocFlag = getStringForBooleanValue(isTocFlag);
    }

    public boolean getIsTocFlag() {
        return isEqualsYes(isTocFlag);
    }

    public void setIsELooseleafsEnabled(final boolean isELooseleafsEnabled) {
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

    public void setPublishedOnceFlag(final boolean publishedOnceFlag) {
        this.publishedOnceFlag = getStringForBooleanValue(publishedOnceFlag);
    }

    public boolean getPublishedOnceFlag() {
        return isEqualsYes(publishedOnceFlag);
    }

    public EbookAudit() {
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
        setIssn(that.getIssn());
        setPublishDateText(that.getPublishDateText());
        setPublisherCodesId(that.getPublisherCodesId());
        setCurrency(that.getCurrency());
        setIsELooseleafsEnabled(that.isELooseleafsEnabled());
        setPublishedDate(that.getPublishedDate());
        setReleaseNotes(that.getReleaseNotes());
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
        setPrintPageNumbers(that.isPrintPageNumbers());
        setInlineTocIncluded(that.isInlineTocIncluded());
        setIndexIncluded(that.isIndexIncluded());
        setTocCollectionName(that.getTocCollectionName());
        setDocCollectionName(that.getDocCollectionName());
        setRootTocGuid(that.getRootTocGuid());
        setSubstituteTocHeadersLevel(that.getSubstituteTocHeadersLevel());
        setTitlePageImageIncluded(that.isTitlePageImageIncluded());
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
        setIssn(that.getIssn());
        setPublishDateText(that.getPublishDateText());
        setPublisherCodesId(that.getPublisherCodes().getId());
        setCurrency(that.getCurrency());
        setIsELooseleafsEnabled(that.isELooseleafsEnabled());
        setPublishedDate(that.getPublishedDate());
        setReleaseNotes(that.getReleaseNotes());
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
        setSplitDocumentsConcat(maxString(concatString(that.getSplitDocuments()), MAX_CHARACTER_2048));
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
        setPrintPageNumbers(that.isPrintPageNumbers());
        setInlineTocIncluded(that.isInlineTocIncluded());
        setIndexIncluded(that.isIndexIncluded());
        setTocCollectionName(that.getTocCollectionName());
        setDocCollectionName(that.getDocCollectionName());
        setRootTocGuid(that.getRootTocGuid());
        setSubstituteTocHeadersLevel(that.getSubstituteTocHeadersLevel());
        setTitlePageImageIncluded(that.isTitlePageImageIncluded());
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
        buffer.append("issn=[").append(issn).append("] ");
        buffer.append("publishDateText=[").append(publishDateText).append("] ");
        buffer.append("publisherCodesId=[").append(publisherCodesId).append("] ");
        buffer.append("currency=[").append(currency).append("] ");
        buffer.append("eLooseleafsEnabled=[").append(eLooseleafsEnabled).append("] ");
        buffer.append("publishedDate=[").append(publishedDate).append("] ");
        buffer.append("releaseNotes=[").append(releaseNotes).append("] ");
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
        buffer.append("printPageNumbers=[").append(printPageNumbers).append("] ");
        buffer.append("inlineTocIncluded=[").append(inlineTocIncluded).append("] ");
        buffer.append("indexIncluded=[").append(indexIncluded).append("] ");
        buffer.append("substituteTocHeadersLevel=[").append(substituteTocHeadersLevel).append("] ");
        if (isEqualsYes(indexIncluded)) {
            buffer.append("indexTocCollectionName=[").append(indexTocCollectionName).append("] ");
            buffer.append("indexDocCollectionName=[").append(indexDocCollectionName).append("] ");
            buffer.append("indexTocRootGuid=[").append(indexTocRootGuid).append("] ");
        }
        buffer.append("titlePageImageIncluded=[").append(titlePageImageIncluded).append("] ");
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

    public void setIsDeletedFlag(final boolean isDeletedFlag) {
        this.isDeletedFlag = getStringForBooleanValue(isDeletedFlag);
    }

    public boolean getIsDeletedFlag() {
        return isEqualsYes(isDeletedFlag);
    }

    public boolean getEnableCopyFeatureFlag() {
        return isEqualsYes(enableCopyFeatureFlag);
    }

    public void setEnableCopyFeatureFlag(final boolean enableCopyFeatureFlag) {
        this.enableCopyFeatureFlag = getStringForBooleanValue(enableCopyFeatureFlag);
    }

    public boolean getAuthorDisplayVerticalFlag() {
        return isEqualsYes(authorDisplayVerticalFlag);
    }

    public void setAuthorDisplayVerticalFlag(final boolean authorDisplayVerticalFlag) {
        this.authorDisplayVerticalFlag = getStringForBooleanValue(authorDisplayVerticalFlag);
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

    public boolean isFinalStage() {
        return isEqualsYes(isFinalStage);
    }

    public void setIsFinalStage(final boolean isFinalStage) {
        this.isFinalStage = getStringForBooleanValue(isFinalStage);
    }

    public boolean getUseReloadContent() {
        return isEqualsYes(useReloadContent);
    }

    public void setUseReloadContent(final boolean useReloadContent) {
        this.useReloadContent = getStringForBooleanValue(useReloadContent);
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
        return isEqualsYes(isSplitBook);
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

    public boolean isPrintPageNumbers() {
        return isEqualsYes(printPageNumbers);
    }

    public void setPrintPageNumbers(final boolean isPrintPageNumbers) {
        printPageNumbers = getStringForBooleanValue(isPrintPageNumbers);
    }

    public boolean isInlineTocIncluded() {
        return isEqualsYes(inlineTocIncluded);
    }

    public void setInlineTocIncluded(final boolean isInlineTocIncluded) {
        inlineTocIncluded = getStringForBooleanValue(isInlineTocIncluded);
    }

    public boolean isIndexIncluded() {
        return isEqualsYes(indexIncluded);
    }

    public void setIndexIncluded(final boolean isIndexIncluded) {
        indexIncluded = getStringForBooleanValue(isIndexIncluded);
    }

    public void setTitlePageImageIncluded(final boolean isTitlePageImageIncluded) {
        titlePageImageIncluded = getStringForBooleanValue(isTitlePageImageIncluded);
    }

    public boolean isTitlePageImageIncluded() {
        return isEqualsYes(titlePageImageIncluded);
    }
}
