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

import org.apache.commons.lang.StringUtils;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;


/**
 */

@Entity
@NamedQueries({
	@NamedQuery(name = "findEbookAuditByPrimaryKey", query = "select myEbookAudit from EbookAudit myEbookAudit where myEbookAudit.auditId = :auditId") })
@Table(name = "EBOOK_AUDIT")
public class EbookAudit implements Serializable {
	//private static final Logger log = Logger.getLogger(EbookAudit.class);
	private static final long serialVersionUID = 1L;
	
	// Lowered the Max characters to account for some unicode characters
	private static final int MAX_CHARACTER_1024 = 1000;
	private static final int MAX_CHARACTER_2048 = 2000;
	public static enum AUDIT_TYPE {DELETE, RESTORE, CREATE, EDIT};

	/**
	 */

	@Column(name = "AUDIT_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(generator = "AuditSequence")
	@SequenceGenerator(name="AuditSequence", sequenceName = "AUDIT_ID_SEQ")	
	Long auditId;
	/**
	 */
	
	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Long ebookDefinitionId;
	/**
	 */
	
	@Column(name = "TITLE_ID", length = 40, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String titleId;
	/**
	 */
	
	@Column(name = "PROVIEW_DISPLAY_NAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String proviewDisplayName;

	/**
	 */

	@Column(name = "COPYRIGHT", length = 1024, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String copyright;
	/**
	 */

	@Column(name = "COPYRIGHT_PAGE_TEXT", length = 2048)
	@Basic(fetch = FetchType.EAGER)
	String copyrightPageText;
	/**
	 */

	@Column(name = "MATERIAL_ID", length = 64, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String materialId;
	/**
	 */

	@Column(name = "IS_TOC_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isTocFlag;
	/**
	 */

	@Column(name = "ROOT_TOC_GUID", length = 64)
	@Basic(fetch = FetchType.EAGER)
	String rootTocGuid;
	/**
	 */

	@Column(name = "DOC_COLLECTION_NAME", length = 64)
	@Basic(fetch = FetchType.EAGER)
	String docCollectionName;
	/**
	 */

	@Column(name = "TOC_COLLECTION_NAME", length = 64)
	@Basic(fetch = FetchType.EAGER)
	String tocCollectionName;
	/**
	 */

	@Column(name = "NORT_DOMAIN", length = 64)
	@Basic(fetch = FetchType.EAGER)
	String nortDomain;
	/**
	 */

	@Column(name = "NORT_FILTER_VIEW", length = 64)
	@Basic(fetch = FetchType.EAGER)
	String nortFilterView;
	/**
	 */

	@Column(name = "DOCUMENT_TYPE_CODES_ID")
	@Basic(fetch = FetchType.EAGER)
	Long documentTypeCodesId;
	/**
	 */

	@Column(name = "COVER_IMAGE", length = 256)
	@Basic(fetch = FetchType.EAGER)
	String coverImage;
	/**
	 */

	@Column(name = "ISBN", length = 64)
	@Basic(fetch = FetchType.EAGER)
	String isbn;
	/**
	 */

	@Column(name = "PUBLISH_DATE_TEXT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String publishDateText;
	/**
	 */

	@Column(name = "PUBLISHER_CODES_ID")
	@Basic(fetch = FetchType.EAGER)

	Long publisherCodesId;
	/**
	 */

	@Column(name = "CURRENCY", length = 2048)
	@Basic(fetch = FetchType.EAGER)
	String currency;
	/**
	 */

	@Column(name = "KEYCITE_TOPLINE_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)

	String keyciteToplineFlag;
	/**
	 */

	@Column(name = "AUTO_UPDATE_SUPPORT_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)
	String autoUpdateSupportFlag;
	/**
	 */

	@Column(name = "SEARCH_INDEX_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)

	String searchIndexFlag;
	/**
	 */

	@Column(name = "ONE_PASS_SSO_LINK_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)
	String onePassSsoLinkFlag;
	/**
	 */
	
	@Column(name = "PROVIEW_TABLE_VIEW_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isProviewTableViewFlag;
	
	/**
	 */
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PUBLISH_CUTOFF_DATE")
	@Basic(fetch = FetchType.EAGER)
	Date publishCutoffDate;
	/**
	 */

	@Column(name = "EBOOK_DEFINITION_COMPLETE_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)

	String ebookDefinitionCompleteFlag;
	/**
	 */

	@Column(name = "PUBLISHED_ONCE_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)
	String publishedOnceFlag;
	/**
	 */

	@Column(name = "AUTHOR_NAMES_CONCAT", length = 2048)
	@Basic(fetch = FetchType.EAGER)

	String authorNamesConcat;
	/**
	 */

	@Column(name = "BOOK_NAMES_CONCAT", length = 2048)
	@Basic(fetch = FetchType.EAGER)
	String bookNamesConcat;
	/**
	 */

	@Column(name = "KEYWORDS_CONCAT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String keywordsConcat;
	/**
	 */

	@Column(name = "AUDIT_NOTE", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String auditNote;
	/**
	 */

	@Column(name = "AUDIT_TYPE", length = 10)
	@Basic(fetch = FetchType.EAGER)
	String auditType;
	/**
	 */
	
	@Column(name = "UPDATED_BY", length = 32, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String updatedBy;
	/**
	 */
	
	@Column(name = "IS_DELETED_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isDeletedFlag;

	/**
	 */
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Date lastUpdated;
	
	/**
	 */
	
	@Column(name = "FRONT_MATTER_TOC_LABEL", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String frontMatterTocLabel;
	/**
	 */
	
	@Column(name = "AUTHOR_DISPLAY_VERTICAL_FLAG", length = 1)
	@Basic(fetch = FetchType.EAGER)
	String authorDisplayVerticalFlag;
	/**
	 */
	
	@Column(name = "ENABLE_COPY_FEATURE_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String enableCopyFeatureFlag;
	
	/**
	 */
	@Column(name = "FRONT_MATTER_CONCAT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String frontMatterConcat;
	
	/**
	 */
	@Column(name = "ADDITIONAL_TRADEMARK_INFO", length = 2048)
	@Basic(fetch = FetchType.EAGER)
	String additionalTrademarkInfo;
	
	/**
	 */
	@Column(name = "IS_PILOT_BOOK", length = 1)
	@Basic(fetch = FetchType.EAGER)
	String pilotBookStatus;
	
	/**
	 */
	@Column(name = "EXCLUDED_DOCUMENTS_CONCAT", length = 2048)
	@Basic(fetch = FetchType.EAGER)
	String excludeDocumentsConcat;

	/**
	 */
	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	/**
	 */
	public Long getAuditId() {
		return this.auditId;
	}

	/**
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 */
	public Date getLastUpdated() {
		return this.lastUpdated;
	}

	/**
	 */
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	/**
	 */
	public String getTitleId() {
		return this.titleId;
	}

	/**
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 */
	public void setCopyrightPageText(String copyrightPageText) {
		this.copyrightPageText = copyrightPageText;
	}

	/**
	 */
	public String getCopyrightPageText() {
		return this.copyrightPageText;
	}

	/**
	 */
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	/**
	 */
	public String getMaterialId() {
		return this.materialId;
	}

	/**
	 */
	public void setIsTocFlag(boolean isTocFlag) {
		this.isTocFlag =( (isTocFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getIsTocFlag() {
		return((this.isTocFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setRootTocGuid(String rootTocGuid) {
		this.rootTocGuid = rootTocGuid;
	}

	/**
	 */
	public String getRootTocGuid() {
		return this.rootTocGuid;
	}

	/**
	 */
	public void setDocCollectionName(String docCollectionName) {
		this.docCollectionName = docCollectionName;
	}

	/**
	 */
	public String getDocCollectionName() {
		return this.docCollectionName;
	}

	/**
	 */
	public void setTocCollectionName(String tocCollectionName) {
		this.tocCollectionName = tocCollectionName;
	}

	/**
	 */
	public String getTocCollectionName() {
		return this.tocCollectionName;
	}

	/**
	 */
	public void setNortDomain(String nortDomain) {
		this.nortDomain = nortDomain;
	}

	/**
	 */
	public String getNortDomain() {
		return this.nortDomain;
	}

	/**
	 */
	public void setNortFilterView(String nortFilterView) {
		this.nortFilterView = nortFilterView;
	}

	/**
	 */
	public String getNortFilterView() {
		return this.nortFilterView;
	}

	/**
	 */
	public void setDocumentTypeCodesId(Long documentTypeCodesId) {
		this.documentTypeCodesId = documentTypeCodesId;
	}

	/**
	 */
	public Long getDocumentTypeCodesId() {
		return this.documentTypeCodesId;
	}

	/**
	 */
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	/**
	 */
	public String getCoverImage() {
		return this.coverImage;
	}

	/**
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 */
	public String getIsbn() {
		return this.isbn;
	}

	/**
	 */
	public void setPublishDateText(String publishDateText) {
		this.publishDateText = publishDateText;
	}

	/**
	 */
	public String getPublishDateText() {
		return this.publishDateText;
	}

	/**
	 */
	public void setPublisherCodesId(Long publisherCodesId) {
		this.publisherCodesId = publisherCodesId;
	}

	/**
	 */
	public Long getPublisherCodesId() {
		return this.publisherCodesId;
	}

	/**
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 */
	public String getCurrency() {
		return this.currency;
	}

	/**
	 */
	public void setKeyciteToplineFlag(boolean keyciteToplineFlag) {
		this.keyciteToplineFlag =( (keyciteToplineFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getKeyciteToplineFlag() {
		return( (this.keyciteToplineFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setAutoUpdateSupportFlag(boolean autoUpdateSupportFlag) {
		this.autoUpdateSupportFlag =( (autoUpdateSupportFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getAutoUpdateSupportFlag() {
		return( (this.autoUpdateSupportFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setSearchIndexFlag(boolean searchIndexFlag) {
		this.searchIndexFlag =( (searchIndexFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getSearchIndexFlag() {
		return( (this.searchIndexFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setOnePassSsoLinkFlag(boolean onePassSsoLinkFlag) {
		this.onePassSsoLinkFlag =( (onePassSsoLinkFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getOnePassSsoLinkFlag() {
		return( (this.onePassSsoLinkFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setEbookDefinitionCompleteFlag(boolean ebookDefinitionCompleteFlag) {
		this.ebookDefinitionCompleteFlag =( (ebookDefinitionCompleteFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getEbookDefinitionCompleteFlag() {
		return( (this.ebookDefinitionCompleteFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setPublishedOnceFlag(boolean publishedOnceFlag) {
		this.publishedOnceFlag =( (publishedOnceFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getPublishedOnceFlag() {
		return( (this.publishedOnceFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setAuthorNamesConcat(String authorNamesConcat) {
		this.authorNamesConcat = authorNamesConcat;
	}

	/**
	 */
	public String getAuthorNamesConcat() {
		return this.authorNamesConcat;
	}

	/**
	 */
	public void setBookNamesConcat(String bookNamesConcat) {
		this.bookNamesConcat = bookNamesConcat;
	}

	/**
	 */
	public String getBookNamesConcat() {
		return this.bookNamesConcat;
	}

	/**
	 */
	public void setKeywordsConcat(String keywordsConcat) {
		this.keywordsConcat = keywordsConcat;
	}

	/**
	 */
	public String getKeywordsConcat() {
		return this.keywordsConcat;
	}

	/**
	 */
	public void setFrontMatterConcat(String frontMatterConcat) {
		this.frontMatterConcat = frontMatterConcat;
	}

	/**
	 */
	public String getFrontMatterConcat() {
		return this.frontMatterConcat;
	}

	/**
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 */
	public String getUpdatedBy() {
		return this.updatedBy;
	}

	/**
	 */
	public void setAuditNote(String auditNote) {
		this.auditNote = auditNote;
	}

	/**
	 */
	public String getAuditNote() {
		return this.auditNote;
	}

	/**
	 */
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	/**
	 */
	public String getAuditType() {
		return this.auditType;
	}

	/**
	 */
	public EbookAudit() {
	}
	
	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(EbookAudit that) {
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
		setIsProviewTableViewFlag(that.getIsProviewTableViewFlag());
		setPublishCutoffDate(that.getPublishCutoffDate());
		setEbookDefinitionCompleteFlag(that.getEbookDefinitionCompleteFlag());
		setPublishedOnceFlag(that.getPublishedOnceFlag());
		setAuthorNamesConcat(that.getAuthorNamesConcat());
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
	}

	/**
	 * Copies the contents of the BookDefinition into this bean.
	 *
	 */
	@Transient
	public void loadBookDefinition(BookDefinition that, AUDIT_TYPE auditType, String user, String note) {
		setEbookDefinitionId(that.getEbookDefinitionId());
		setTitleId(that.getFullyQualifiedTitleId());
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
		setIsProviewTableViewFlag(that.isProviewTableViewFlag());
		setPublishCutoffDate(that.getPublishCutoffDate());
		setEbookDefinitionCompleteFlag(that.getEbookDefinitionCompleteFlag());
		setPublishedOnceFlag(that.getPublishedOnceFlag());
		setAuthorNamesConcat(maxString(concatString(that.getAuthors()), MAX_CHARACTER_2048));
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
	}
	
	@Transient
	private String maxString(String buffer, int maxCharacters) {
		return StringUtils.abbreviate(buffer.toString(), maxCharacters);
	}
	
	@Transient
	private String concatString(Collection<?> collection) {
		StringBuilder buffer = new StringBuilder();
		for(Object item : collection) {
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

		StringBuilder buffer = new StringBuilder();

		buffer.append("auditId=[").append(auditId).append("] ");
		buffer.append("ebookDefinitionId=[").append(ebookDefinitionId).append("] ");
		buffer.append("lastUpdated=[").append(lastUpdated).append("] ");
		buffer.append("titleId=[").append(titleId).append("] ");
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
		buffer.append("isProviewTableViewFlag=[").append(isProviewTableViewFlag).append("] ");
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
		
		return buffer.toString();
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((auditId == null) ? 0 : auditId.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof EbookAudit))
			return false;
		EbookAudit equalCheck = (EbookAudit) obj;
		if ((auditId == null && equalCheck.auditId != null) || (auditId != null && equalCheck.auditId == null))
			return false;
		if (auditId != null && !auditId.equals(equalCheck.auditId))
			return false;
		return true;
	}

	public Long getEbookDefinitionId() {
		return ebookDefinitionId;
	}

	public void setEbookDefinitionId(Long ebookDefinitionId) {
		this.ebookDefinitionId = ebookDefinitionId;
	}

	/**
	 */
	public void setProviewDisplayName(String proviewDisplayName) {
		this.proviewDisplayName = proviewDisplayName;
	}

	/**
	 */
	public String getProviewDisplayName() {
		return this.proviewDisplayName;
	}

	/**
	 */
	public void setPublishCutoffDate(Date publishCutoffDate) {
		this.publishCutoffDate = publishCutoffDate;
	}

	/**
	 */
	public Date getPublishCutoffDate() {
		return this.publishCutoffDate;
	}

	/**
	 */
	public void setIsDeletedFlag(boolean isDeletedFlag) {
		this.isDeletedFlag =( (isDeletedFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getIsDeletedFlag() {
		return( (this.isDeletedFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setIsProviewTableViewFlag(boolean isProviewTableViewFlag) {
		this.isProviewTableViewFlag =( (isProviewTableViewFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getIsProviewTableViewFlag() {
		return( (this.isProviewTableViewFlag.equalsIgnoreCase("Y") ? true : false));
	}

	public boolean getEnableCopyFeatureFlag() {
		return ( (this.enableCopyFeatureFlag.equalsIgnoreCase("Y") ? true : false));
	}

	public void setEnableCopyFeatureFlag(boolean enableCopyFeatureFlag) {
		this.enableCopyFeatureFlag =( (enableCopyFeatureFlag) ? "Y" : "N");
	}
	public boolean getAuthorDisplayVerticalFlag() {
		return ( (this.authorDisplayVerticalFlag.equalsIgnoreCase("Y") ? true : false));
	}

	public void setAuthorDisplayVerticalFlag(boolean authorDisplayVerticalFlag) {
		this.authorDisplayVerticalFlag =( (authorDisplayVerticalFlag) ? "Y" : "N");
	}
	
	public String getAdditionalTrademarkInfo() {
		return additionalTrademarkInfo;
	}

	public void setAdditionalTrademarkInfo(String additionalTrademarkInfo) {
		this.additionalTrademarkInfo = additionalTrademarkInfo;
	}

	public PilotBookStatus getPilotBookStatus() {
		if(StringUtils.isBlank(this.pilotBookStatus)) {
			return PilotBookStatus.FALSE;
		} else {
			if(this.pilotBookStatus.equalsIgnoreCase("Y")) {
				return PilotBookStatus.TRUE;
			} else if(this.pilotBookStatus.equalsIgnoreCase("I")) {
				return PilotBookStatus.IN_PROGRESS;
			} else {
				return PilotBookStatus.FALSE;
			}
		}
	}

	public void setPilotBookStatus(PilotBookStatus status) {
		switch(status) {
			case TRUE:
				this.pilotBookStatus = "Y";
				break;
			case IN_PROGRESS:
				this.pilotBookStatus = "I";
				break;
			default:
				this.pilotBookStatus = "N";
				break;
		}
	}

	public String getExcludeDocumentsConcat() {
		return excludeDocumentsConcat;
	}

	public void setExcludeDocumentsConcat(String excludeDocumentsConcat) {
		this.excludeDocumentsConcat = excludeDocumentsConcat;
	}

	public String getFrontMatterTocLabel() {
		return frontMatterTocLabel;
	}

	public void setFrontMatterTocLabel(String frontMatterTocLabel) {
		this.frontMatterTocLabel = frontMatterTocLabel;
	}
}
