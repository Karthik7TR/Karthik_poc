package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

/**
 */

@Entity
@Table(schema = "EBOOK", name = "EBOOK_AUDIT")
public class EbookAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */

	@Column(name = "AUDIT_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "AuditSequence")
	@SequenceGenerator(name="AuditSequence", sequenceName = "AUDIT_ID_SEQ")	
	Long auditId;
	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TIMESTAMP", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Date timestamp;
	/**
	 */
	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	Long ebookDefinitionId;
	/**
	 */

	@Column(name = "COPYRIGHT", length = 1024, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String copyright;
	/**
	 */

	@Column(name = "COPYRIGHT_PAGE_TEXT", length = 1024)
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
	Integer documentTypeCodesId;
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

	Integer publisherCodesId;
	/**
	 */

	@Column(name = "CURRENCY", length = 1024)
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

	@Column(name = "AUTHOR_NAMES_CONCAT", length = 1024)
	@Basic(fetch = FetchType.EAGER)

	String authorNamesConcat;
	/**
	 */

	@Column(name = "BOOK_NAMES_CONCAT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String bookNamesConcat;
	/**
	 */

	@Column(name = "KEYWORDS_CONCAT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String keywordsConcat;
	/**
	 */

	@Column(name = "FRONT_MATTER_CONCAT", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	String frontMatterConcat;
	/**
	 */

	@Column(name = "UPDATED_BY", length = 32, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String updatedBy;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false) })
	BookDefinition ebookDefinition;

	/**
	 */

	@Column(name = "TITLE_ID", length = 40, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String titleId;

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
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 */
	public Date getTimestamp() {
		return this.timestamp;
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
	public void setIsTocFlag(String isTocFlag) {
		this.isTocFlag = isTocFlag;
	}

	/**
	 */
	public String getIsTocFlag() {
		return this.isTocFlag;
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
	public void setDocumentTypeCodesId(Integer documentTypeCodesId) {
		this.documentTypeCodesId = documentTypeCodesId;
	}

	/**
	 */
	public Integer getDocumentTypeCodesId() {
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
	public void setPublisherCodesId(Integer publisherCodesId) {
		this.publisherCodesId = publisherCodesId;
	}

	/**
	 */
	public Integer getPublisherCodesId() {
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
	public void setKeyciteToplineFlag(String keyciteToplineFlag) {
		this.keyciteToplineFlag = keyciteToplineFlag;
	}

	/**
	 */
	public String getKeyciteToplineFlag() {
		return this.keyciteToplineFlag;
	}

	/**
	 */
	public void setAutoUpdateSupportFlag(String autoUpdateSupportFlag) {
		this.autoUpdateSupportFlag = autoUpdateSupportFlag;
	}

	/**
	 */
	public String getAutoUpdateSupportFlag() {
		return this.autoUpdateSupportFlag;
	}

	/**
	 */
	public void setSearchIndexFlag(String searchIndexFlag) {
		this.searchIndexFlag = searchIndexFlag;
	}

	/**
	 */
	public String getSearchIndexFlag() {
		return this.searchIndexFlag;
	}

	/**
	 */
	public void setOnePassSsoLinkFlag(String onePassSsoLinkFlag) {
		this.onePassSsoLinkFlag = onePassSsoLinkFlag;
	}

	/**
	 */
	public String getOnePassSsoLinkFlag() {
		return this.onePassSsoLinkFlag;
	}

	/**
	 */
	public void setEbookDefinitionCompleteFlag(String ebookDefinitionCompleteFlag) {
		this.ebookDefinitionCompleteFlag = ebookDefinitionCompleteFlag;
	}

	/**
	 */
	public String getEbookDefinitionCompleteFlag() {
		return this.ebookDefinitionCompleteFlag;
	}

	/**
	 */
	public void setPublishedOnceFlag(String publishedOnceFlag) {
		this.publishedOnceFlag = publishedOnceFlag;
	}

	/**
	 */
	public String getPublishedOnceFlag() {
		return this.publishedOnceFlag;
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
	public void setEbookDefinition(BookDefinition ebookDefinition) {
		this.ebookDefinition = ebookDefinition;
	}

	/**
	 */
	public BookDefinition getEbookDefinition() {
		return ebookDefinition;
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
		setTimestamp(that.getTimestamp());
		setTitleId(that.getTitleId());
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
		setEbookDefinitionCompleteFlag(that.getEbookDefinitionCompleteFlag());
		setPublishedOnceFlag(that.getPublishedOnceFlag());
		setAuthorNamesConcat(that.getAuthorNamesConcat());
		setBookNamesConcat(that.getBookNamesConcat());
		setKeywordsConcat(that.getKeywordsConcat());
		setFrontMatterConcat(that.getFrontMatterConcat());
		setUpdatedBy(that.getUpdatedBy());
		setAuditNote(that.getAuditNote());
		setAuditType(that.getAuditType());
		setEbookDefinition(that.getEbookDefinition());
	}

	/**
	 * Returns a textual representation of a bean.
	 *
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("auditId=[").append(auditId).append("] ");
		buffer.append("timestamp=[").append(timestamp).append("] ");
		buffer.append("titleId=[").append(titleId).append("] ");
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
		buffer.append("ebookDefinitionCompleteFlag=[").append(ebookDefinitionCompleteFlag).append("] ");
		buffer.append("publishedOnceFlag=[").append(publishedOnceFlag).append("] ");
		buffer.append("authorNamesConcat=[").append(authorNamesConcat).append("] ");
		buffer.append("bookNamesConcat=[").append(bookNamesConcat).append("] ");
		buffer.append("keywordsConcat=[").append(keywordsConcat).append("] ");
		buffer.append("frontMatterConcat=[").append(frontMatterConcat).append("] ");
		buffer.append("updatedBy=[").append(updatedBy).append("] ");
		buffer.append("auditNote=[").append(auditNote).append("] ");
		buffer.append("auditType=[").append(auditType).append("] ");

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
}
