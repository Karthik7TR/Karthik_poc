/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;

/**
 */

@Entity
@NamedQueries({ @NamedQuery(name = "findBookDefnBySearchCriterion", query = "select myBook from BookDefinition myBook "),
@NamedQuery(name = "countBookDefinitions", query = "select count(*) from BookDefinition myBook") })
@Table(name="EBOOK_DEFINITION", schema="EBOOK")
public class BookDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */

	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@SequenceGenerator(name="bookDefinitionIdSequence", sequenceName="EBOOK_DEFINITION_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="bookDefinitionIdSequence")
	Long ebookDefinitionId;
	/**
	 */

	@Column(name = "TITLE_ID", length = 40, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String fullyQualifiedTitleId;
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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PUBLISH_CUTOFF_DATE")
	@Basic(fetch = FetchType.EAGER)
	Calendar publishCutoffDate;
	/**
	 */

	@Column(name = "EBOOK_DEFINITION_COMPLETE_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String ebookDefinitionCompleteFlag;
	/**
	 */

	@Column(name = "PUBLISHED_ONCE_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String publishedOnceFlag;
	/**
	 */

	@Column(name = "IS_DELETED_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isDeletedFlag;
	
	
	/**
	 */

	@Column(name = "PROVIEW_TABLE_VIEW_FLAG", length = 1, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isProviewTableViewFlag;	
	
	
	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Date lastUpdated;

	/**
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "PUBLISHER_CODES_ID", referencedColumnName = "PUBLISHER_CODES_ID") })
	PublisherCode publisherCodes;
	/**
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "DOCUMENT_TYPE_CODES_ID", referencedColumnName = "DOCUMENT_TYPE_CODES_ID") })
	DocumentTypeCode documentTypeCodes;

	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	java.util.Set<FrontMatter> frontMatters;
	/**
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "EBOOK_KEYWORDS", schema="EBOOK", joinColumns = { 
			@JoinColumn(name = "EBOOK_DEFINITION_ID", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "KEYWORD_TYPE_VALUES_ID", 
					nullable = false, updatable = false) })
	java.util.Set<KeywordTypeValue> keywordTypeValues;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	java.util.Set<Author> authors;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	java.util.Set<EbookName> ebookNames;
	


	/**
	 */
	public void setEbookDefinitionId(Long ebookDefinitionId) {
		this.ebookDefinitionId = ebookDefinitionId;
	}

	/**
	 */
	public Long getEbookDefinitionId() {
		return this.ebookDefinitionId;
	}

	/**
	 */
	public void setFullyQualifiedTitleId(String fullyQualifiedTitleId) {
		this.fullyQualifiedTitleId = fullyQualifiedTitleId;
	}

	/**
	 */
	public String getFullyQualifiedTitleId() {
		return this.fullyQualifiedTitleId;
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
		this.isTocFlag = ( (isTocFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean getIsTocFlag() {
		return ((this.isTocFlag.equalsIgnoreCase("Y") ? true : false));		
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
		this.keyciteToplineFlag = ( (keyciteToplineFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean IsKeyciteToplineFlag() {
		return ((this.keyciteToplineFlag.equalsIgnoreCase("Y") ? true : false));		
	}

	/**
	 */
	public void setAutoUpdateSupportFlag(boolean autoUpdateSupportFlag) {
		this.autoUpdateSupportFlag = ( (autoUpdateSupportFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getAutoUpdateSupportFlag() {
		return ((this.autoUpdateSupportFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setSearchIndexFlag(boolean searchIndexFlag) {
		this.searchIndexFlag = ( (searchIndexFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean IsSearchIndexFlag() {
		return ((this.searchIndexFlag.equalsIgnoreCase("Y") ? true : false));		
	}

	/**
	 */
	public void setOnePassSsoLinkFlag(boolean onePassSsoLinkFlag) {
		this.onePassSsoLinkFlag = ( (onePassSsoLinkFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean IsOnePassSsoLinkFlag() {
		return ((this.onePassSsoLinkFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setPublishCutoffDate(Calendar publishCutoffDate) {
		this.publishCutoffDate = publishCutoffDate;
	}

	/**
	 */
	public Calendar getPublishCutoffDate() {
		return this.publishCutoffDate;
	}

	/**
	 */
	public void setEbookDefinitionCompleteFlag(boolean ebookDefinitionCompleteFlag) {
		this.ebookDefinitionCompleteFlag = ( (ebookDefinitionCompleteFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean IsEbookDefinitionCompleteFlag() {
		return ((this.ebookDefinitionCompleteFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setPublishedOnceFlag(boolean publishedOnceFlag) {
		this.publishedOnceFlag = ( (publishedOnceFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean IsPublishedOnceFlag() {
		return( (this.publishedOnceFlag.equalsIgnoreCase("Y") ? true : false));
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
	public void setPublisherCodes(PublisherCode publisherCodes) {
		this.publisherCodes = publisherCodes;
	}

	/**
	 */
	public PublisherCode getPublisherCodes() {
		return publisherCodes;
	}

	/**
	 */
	public void setDocumentTypeCodes(DocumentTypeCode documentTypeCodes) {
		this.documentTypeCodes = documentTypeCodes;
	}

	/**
	 */
	public DocumentTypeCode getDocumentTypeCodes() {
		return documentTypeCodes;
	}

	/**
	 */
	public void setFrontMatters(Set<FrontMatter> frontMatters) {
		this.frontMatters = frontMatters;
	}

	/**
	 */
	public Set<FrontMatter> getFrontMatters() {
		if (frontMatters == null) {
			frontMatters = new java.util.LinkedHashSet<FrontMatter>();
		}
		return frontMatters;
	}

	/**
	 */
	public void setKeywordTypeValueses(Set<KeywordTypeValue> keywordTypeValueses) {
		this.keywordTypeValues = keywordTypeValueses;
	}

	/**
	 */
	public Set<KeywordTypeValue> getKeywordTypeValueses() {
		if (keywordTypeValues == null) {
			keywordTypeValues = new java.util.LinkedHashSet<KeywordTypeValue>();
		}
		return keywordTypeValues;
	}

	/**
	 */
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	/**
	 */
	public Set<Author> getAuthors() {
		if (authors == null) {
			authors = new java.util.LinkedHashSet<Author>();
		}
		return authors;
	}

	/**
	 */
	public void setEbookNames(Set<EbookName> ebookNames) {
		this.ebookNames = ebookNames;
	}

	/**
	 */
// TODO: FIX THIS: There IS order here for the name elements, should return a List<EbookName> in proper presentation (sequence) order not a Set.
	public Set<EbookName> getEbookNames() {
		if (ebookNames == null) {
			ebookNames = new java.util.LinkedHashSet<EbookName>();
		}
		return ebookNames;
	}

	/**
	 */
	public BookDefinition() {
		this.setIsDeletedFlag(false);
		this.setPublishedOnceFlag(false);
		this.setOnePassSsoLinkFlag(true);
	}

	/**
	 * Copies the contents of the specified bean into this bean.
	 *
	 */
	public void copy(BookDefinition that) {
		setEbookDefinitionId(that.getEbookDefinitionId());
		setFullyQualifiedTitleId(that.getFullyQualifiedTitleId());
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
		setCoverImage(that.getCoverImage());
		setIsbn(that.getIsbn());
		setPublishDateText(that.getPublishDateText());
		setCurrency(that.getCurrency());
		setKeyciteToplineFlag(that.IsKeyciteToplineFlag());
		setAutoUpdateSupportFlag(that.getAutoUpdateSupportFlag());
		setSearchIndexFlag(that.IsSearchIndexFlag());
		setOnePassSsoLinkFlag(that.IsOnePassSsoLinkFlag());
		setPublishCutoffDate(that.getPublishCutoffDate());
		setEbookDefinitionCompleteFlag(that.IsEbookDefinitionCompleteFlag());
		setPublishedOnceFlag(that.IsPublishedOnceFlag());
		setIsDeletedFlag(that.getIsDeletedFlag());
		setLastUpdated(that.getLastUpdated());
		setPublisherCodes(that.getPublisherCodes());
		setDocumentTypeCodes(that.getDocumentTypeCodes());
		setFrontMatters(new java.util.LinkedHashSet<FrontMatter>(that.getFrontMatters()));
		setAuthors(new java.util.LinkedHashSet<Author>(that.getAuthors()));
		setEbookNames(new java.util.LinkedHashSet<EbookName>(that.getEbookNames()));
	}

	/**
	 * Returns a textual representation of a bean.
	 *
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

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
		buffer.append("keyciteToplineFlag=[").append(keyciteToplineFlag).append("] ");
		buffer.append("autoUpdateSupportFlag=[").append(autoUpdateSupportFlag).append("] ");
		buffer.append("searchIndexFlag=[").append(searchIndexFlag).append("] ");
		buffer.append("onePassSsoLinkFlag=[").append(onePassSsoLinkFlag).append("] ");
		buffer.append("publishCutoffDate=[").append(publishCutoffDate).append("] ");
		buffer.append("ebookDefinitionCompleteFlag=[").append(ebookDefinitionCompleteFlag).append("] ");
		buffer.append("publishedOnceFlag=[").append(publishedOnceFlag).append("] ");
		buffer.append("isDeletedFlag=[").append(isDeletedFlag).append("] ");
		buffer.append("lastUpdated=[").append(lastUpdated).append("] ");

		return buffer.toString();
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((ebookDefinitionId == null) ? 0 : ebookDefinitionId.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof BookDefinition))
			return false;
		BookDefinition equalCheck = (BookDefinition) obj;
		if ((ebookDefinitionId == null && equalCheck.ebookDefinitionId != null) || (ebookDefinitionId != null && equalCheck.ebookDefinitionId == null))
			return false;
		if (ebookDefinitionId != null && !ebookDefinitionId.equals(equalCheck.ebookDefinitionId))
			return false;
		return true;
	}

	/**
	 */
	public void setIsProviewTableViewFlag(boolean isProviewTableViewFlag) {
		this.isProviewTableViewFlag =( (isProviewTableViewFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean IsProviewTableViewFlag() {
		return( (this.isProviewTableViewFlag.equalsIgnoreCase("Y") ? true : false));
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
		StringTokenizer tokenizer = new StringTokenizer(fullyQualifiedTitleId, "/");
		String component = null;
		while (tokenizer.hasMoreTokens()) {
			component = tokenizer.nextToken();
		}
		return (component);
	}

	/**
	 * The proview features as derived from the book definition.
	 * @return List of Feature.
	 */
	@Transient
	public ArrayList<Feature> getProviewFeatures() {

		ArrayList<Feature> proviewFeatures = new ArrayList<Feature>();
		proviewFeatures.add(new Feature("AutoUpdate"));
		if (IsSearchIndexFlag()) proviewFeatures.add(new Feature("SearchIndex"));
		if (IsOnePassSsoLinkFlag()) proviewFeatures.add(new Feature("OnePassSSO", "www.westlaw.com"));		
		return (proviewFeatures);
	}

	/**
	 * The proview keywords as derived from the book definition.
	 * @return List of keywords.
	 */
	@Transient
	public ArrayList<Keyword> getKeyWords() {
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		keywords.add(new Keyword("publisher", "Thomson Reuters"));
		return (keywords);
	}	
	
	/**
	 * Provides the status of the book definition
	 * @return String indicating the status
	 */
	@Transient
	public String getBookStatus() {
		String status;
		if (getIsDeletedFlag()) {
			status = "Deleted";
		} else { 
			if (IsEbookDefinitionCompleteFlag()) {
				status = "Complete";
			} else {
				status = "Incomplete";
			}
		}
		return status;
	}
}
