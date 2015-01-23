/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;

/**
 */

@Entity
@NamedQueries({ @NamedQuery(name = "findBookDefnBySearchCriterion", query = "select myBook from BookDefinition myBook "),
@NamedQuery(name = "countBookDefinitions", query = "select count(*) from BookDefinition myBook") })
@Table(name="EBOOK_DEFINITION")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "BookDefinition")
public class BookDefinition implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum PilotBookStatus {TRUE, FALSE, IN_PROGRESS};
	public static enum SourceType {TOC, NORT, FILE};

	/**
	 */

	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@SequenceGenerator(name="bookDefinitionIdSequence", sequenceName="EBOOK_DEFINITION_ID_SEQ")
	@GeneratedValue(generator="bookDefinitionIdSequence")
	Long ebookDefinitionId;
	/**
	 */

	@Column(name = "TITLE_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String fullyQualifiedTitleId;
	/**
	 */

	@Column(name = "PROVIEW_DISPLAY_NAME")
	@Basic(fetch = FetchType.EAGER)
	String proviewDisplayName;
	/**
	 */

	@Column(name = "COPYRIGHT", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String copyright;
	/**
	 */

	@Column(name = "COPYRIGHT_PAGE_TEXT")
	@Basic(fetch = FetchType.EAGER)
	String copyrightPageText;
	/**
	 */

	@Column(name = "MATERIAL_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String materialId;
	/**
	 */

	@Column(name = "IS_TOC_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isTocFlag;
	/**
	 */

	@Column(name = "ROOT_TOC_GUID")
	@Basic(fetch = FetchType.EAGER)
	String rootTocGuid;
	/**
	 */

	@Column(name = "DOC_COLLECTION_NAME")
	@Basic(fetch = FetchType.EAGER)
	String docCollectionName;
	/**
	 */

	@Column(name = "TOC_COLLECTION_NAME")
	@Basic(fetch = FetchType.EAGER)
	String tocCollectionName;
	/**
	 */

	@Column(name = "NORT_DOMAIN")
	@Basic(fetch = FetchType.EAGER)
	String nortDomain;
	/**
	 */

	@Column(name = "NORT_FILTER_VIEW")
	@Basic(fetch = FetchType.EAGER)
	String nortFilterView;
	/**
	 */

	@Column(name = "COVER_IMAGE")
	@Basic(fetch = FetchType.EAGER)
	String coverImage;
	/**
	 */

	@Column(name = "ISBN")
	@Basic(fetch = FetchType.EAGER)
	String isbn;
	/**
	 */

	@Column(name = "PUBLISH_DATE_TEXT")
	@Basic(fetch = FetchType.EAGER)
	String publishDateText;
	/**
	 */

	@Column(name = "CURRENCY")
	@Basic(fetch = FetchType.EAGER)
	String currency;
	/**
	 */

	@Column(name = "KEYCITE_TOPLINE_FLAG")
	@Basic(fetch = FetchType.EAGER)
	String keyciteToplineFlag;
	/**
	 */

	@Column(name = "ENABLE_COPY_FEATURE_FLAG")
	@Basic(fetch = FetchType.EAGER)
	String enableCopyFeatureFlag;
	/**
	 */

	@Column(name = "AUTO_UPDATE_SUPPORT_FLAG")
	@Basic(fetch = FetchType.EAGER)
	String autoUpdateSupportFlag;
	/**
	 */

	@Column(name = "SEARCH_INDEX_FLAG")
	@Basic(fetch = FetchType.EAGER)
	String searchIndexFlag;
	/**
	 */

	@Column(name = "ONE_PASS_SSO_LINK_FLAG")
	@Basic(fetch = FetchType.EAGER)
	String onePassSsoLinkFlag;
	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PUBLISH_CUTOFF_DATE")
	@Basic(fetch = FetchType.EAGER)
	Date publishCutoffDate;
	/**
	 */

	@Column(name = "EBOOK_DEFINITION_COMPLETE_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String ebookDefinitionCompleteFlag;
	/**
	 */

	@Column(name = "PUBLISHED_ONCE_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String publishedOnceFlag;
	/**
	 */

	@Column(name = "IS_DELETED_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isDeletedFlag;
	
	/**
	 */

	@Column(name = "PROVIEW_TABLE_VIEW_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isProviewTableViewFlag;	
	/**
	 */
	
	@Column(name = "IS_FINAL_STAGE", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isFinalStage;
	/**
	 */
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	Date lastUpdated;
	
	/**
	 */
	@Column(name = "FRONT_MATTER_TOC_LABEL")
	@Basic(fetch = FetchType.EAGER)
	String frontMatterTocLabel;	
	
	/**
	 */
	@Column(name = "AUTHOR_DISPLAY_VERTICAL_FLAG")
	@Basic(fetch = FetchType.EAGER)
	String isAuthorDisplayVertical;	
	
	/**
	 */
	@Column(name = "ADDITIONAL_TRADEMARK_INFO")
	@Basic(fetch = FetchType.EAGER)
	String additionalTrademarkInfo;	
	
	/**
	 */
	@Column(name = "IS_PILOT_BOOK")
	@Basic(fetch = FetchType.EAGER)
	String isPilotBook;	
	
	/**
	 */
	@Column(name = "INCLUDE_ANNOTATIONS")
	String includeAnnotations;	
	
	/**
	 */
	@Column(name = "USE_RELOAD_CONTENT")
	@Basic(fetch = FetchType.EAGER)
	String useReloadContent;	

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
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "EBOOK_KEYWORDS", joinColumns = { 
			@JoinColumn(name = "EBOOK_DEFINITION_ID", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "KEYWORD_TYPE_VALUES_ID", 
					nullable = false, updatable = false) })
	Set<KeywordTypeValue> keywordTypeValues;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<Author> authors;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<EbookName> ebookNames;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<FrontMatterPage> frontMatterPages;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<ExcludeDocument> excludeDocuments;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<RenameTocEntry> renameTocEntries;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<TableViewer> tableViewers;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<DocumentCopyright> documentCopyrights;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<DocumentCurrency> documentCurrencies;
	/**
	 */
	@OneToMany(mappedBy = "ebookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({CascadeType.ALL})
	Set<NortFileLocation> nortFileLocations;
	/**
	 */
	@Column(name = "SOURCE_TYPE")
	@Basic(fetch = FetchType.EAGER)
	String sourceType;
	/**
	 */
	@Column(name = "CWB_BOOK_NAME")
	@Basic(fetch = FetchType.EAGER)
	String cwbBookName;
	/**
	 */
	@Column(name = "IS_INS_STYLE_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isInsStyleFlag;
	/**
	 */
	@Column(name = "IS_DEL_STYLE_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isDelStyleFlag;
	/**
	 */
	@Column(name = "IS_REMOVE_EDNOTE_HEAD_FLAG", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	String isRemoveEditorNoteHeadFlag;
	

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
	public boolean isTocFlag() {
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
	public void setIsProviewTableViewFlag(boolean isProviewTableViewFlag) {
		this.isProviewTableViewFlag =( (isProviewTableViewFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean isProviewTableViewFlag() {
		return( (this.isProviewTableViewFlag.equalsIgnoreCase("Y") ? true : false));
	}
	
	/**
	 */
	public void setIsFinalStage(boolean isFinalStage) {
		this.isFinalStage =( (isFinalStage) ? "Y" : "N");
	}

	/**
	 */
	public boolean isFinalStage() {
		return( (this.isFinalStage.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setKeyciteToplineFlag(boolean keyciteToplineFlag) {
		this.keyciteToplineFlag = ( (keyciteToplineFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean getKeyciteToplineFlag() {
		return ((this.keyciteToplineFlag.equalsIgnoreCase("Y") ? true : false));		
	}

	public void setEnableCopyFeatureFlag(boolean enableCopyFeatureFlag) {
		this.enableCopyFeatureFlag = ((enableCopyFeatureFlag) ? "Y" : "N");
	}

	public boolean getEnableCopyFeatureFlag() {
		return ((this.enableCopyFeatureFlag.equalsIgnoreCase("Y") ? true : false));
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
	public boolean getSearchIndexFlag() {
		return ((this.searchIndexFlag.equalsIgnoreCase("Y") ? true : false));		
	}

	/**
	 */
	public void setOnePassSsoLinkFlag(boolean onePassSsoLinkFlag) {
		this.onePassSsoLinkFlag = ( (onePassSsoLinkFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean getOnePassSsoLinkFlag() {
		return ((this.onePassSsoLinkFlag.equalsIgnoreCase("Y") ? true : false));
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
	public void setEbookDefinitionCompleteFlag(boolean ebookDefinitionCompleteFlag) {
		this.ebookDefinitionCompleteFlag = ( (ebookDefinitionCompleteFlag) ? "Y" : "N");		
	}

	/**
	 */
	public boolean getEbookDefinitionCompleteFlag() {
		return ((this.ebookDefinitionCompleteFlag.equalsIgnoreCase("Y") ? true : false));
	}
	
	/**
	 */
	public void setUseReloadContent(boolean useReloadContent) {
		this.useReloadContent = ( (useReloadContent) ? "Y" : "N");		
	}

	/**
	 */
	public boolean getUseReloadContent() {
		return ((this.useReloadContent.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setPublishedOnceFlag(boolean publishedOnceFlag) {
		this.publishedOnceFlag = ( (publishedOnceFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean getPublishedOnceFlag() {
		return( (this.publishedOnceFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 */
	public void setIsDeletedFlag(boolean isDeletedFlag) {
		this.isDeletedFlag =( (isDeletedFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean isDeletedFlag() {
		return( (this.isDeletedFlag.equalsIgnoreCase("Y") ? true : false));
	}
	
	/**
	 */
	public void setIsAuthorDisplayVertical(boolean isAuthorDisplayVertical) {
		this.isAuthorDisplayVertical =( (isAuthorDisplayVertical) ? "Y" : "N");
	}
	
	/**
	 */
	public boolean isAuthorDisplayVertical() {
		return( (this.isAuthorDisplayVertical.equalsIgnoreCase("Y") ? true : false));
	}

	public String getAdditionalTrademarkInfo() {
		return additionalTrademarkInfo;
	}

	public void setAdditionalTrademarkInfo(String additionalTrademarkInfo) {
		this.additionalTrademarkInfo = additionalTrademarkInfo;
	}
	
	/**
	 */
	public void setIncludeAnnotations(boolean includeAnnotations) {
		this.includeAnnotations =( (includeAnnotations) ? "Y" : "N");
	}
	
	/**
	 */
	public boolean getIncludeAnnotations() {
		return( (this.includeAnnotations.equalsIgnoreCase("Y") ? true : false));
	}
	
	public boolean getIsPilotBook() {
		if(StringUtils.isBlank(this.isPilotBook)) {
			return false;
		} else {
			if(this.isPilotBook.equalsIgnoreCase("Y")) {
				return true;
			} else {
				return false;
			} 
		}
	}
	
	public PilotBookStatus getPilotBookStatus() {
		if(StringUtils.isBlank(this.isPilotBook)) {
			return PilotBookStatus.FALSE;
		} else {
			if(this.isPilotBook.equalsIgnoreCase("Y")) {
				return PilotBookStatus.TRUE;
			} else if(this.isPilotBook.equalsIgnoreCase("I")) {
				return PilotBookStatus.IN_PROGRESS;
			} else {
				return PilotBookStatus.FALSE;
			}
		}
	}

	public void setPilotBookStatus(PilotBookStatus status) {
		switch(status) {
			case TRUE:
				this.isPilotBook = "Y";
				break;
			case IN_PROGRESS:
				this.isPilotBook = "I";
				break;
			default:
				this.isPilotBook = "N";
				break;
		}
	}
	
	public SourceType getSourceType() {
		if(StringUtils.isBlank(this.sourceType)) {
			return SourceType.TOC;
		} else {
			if(this.sourceType.equalsIgnoreCase("NORT")) {
				return SourceType.NORT;
			} else if(this.sourceType.equalsIgnoreCase("FILE")) {
				return SourceType.FILE;
			} else {
				return SourceType.TOC;
			}
		}
	}

	public void setSourceType(SourceType type) {
		switch(type) {
			case NORT:
				this.sourceType = "NORT";
				break;
			case FILE:
				this.sourceType = "FILE";
				break;
			default:
				this.sourceType = "TOC";
				break;
		}
	}

	public String getCwbBookName() {
		return cwbBookName;
	}

	public void setCwbBookName(String cwbBookName) {
		this.cwbBookName = cwbBookName;
	}
	
	 /**
	 */
	public void setIsInsStyleFlag(boolean isInsStyleFlag) {
		this.isInsStyleFlag =( (isInsStyleFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean isInsStyleFlag() {
		return( (this.isInsStyleFlag.equalsIgnoreCase("Y") ? true : false));
	}
	
	/**
	 */
	public void setIsDelStyleFlag(boolean isDelStyleFlag) {
		this.isDelStyleFlag =( (isDelStyleFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean isDelStyleFlag() {
		return( (this.isDelStyleFlag.equalsIgnoreCase("Y") ? true : false));
	}
	/**
	 */
	public void setIsRemoveEditorNoteHeadFlag(boolean isRemoveEditorNoteHeadFlag) {
		this.isRemoveEditorNoteHeadFlag =( (isRemoveEditorNoteHeadFlag) ? "Y" : "N");
	}

	/**
	 */
	public boolean isRemoveEditorNoteHeadFlag() {
		return( (this.isRemoveEditorNoteHeadFlag.equalsIgnoreCase("Y") ? true : false));
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

	public String getFrontMatterTocLabel() {
		return frontMatterTocLabel;
	}

	public void setFrontMatterTocLabel(String frontMatterTocLabel) {
		this.frontMatterTocLabel = frontMatterTocLabel;
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
	public void setKeywordTypeValues(Collection<KeywordTypeValue> keywordTypeValues) {
		
		this.keywordTypeValues = new java.util.LinkedHashSet<KeywordTypeValue>(keywordTypeValues);
	}

	/**
	 */
	public Set<KeywordTypeValue> getKeywordTypeValues() {
		if (keywordTypeValues == null) {
			keywordTypeValues = new java.util.LinkedHashSet<KeywordTypeValue>();
		}
		return keywordTypeValues;
	}

	/**
	 */
	public void setAuthors(Collection<Author> authors) {
		this.authors = new java.util.LinkedHashSet<Author>(authors);
	}

	/**
	 */
	public ArrayList<Author> getAuthors() {
		if (authors == null) {
			authors = new java.util.LinkedHashSet<Author>();
		}	
		// Sort by sequence numbers
		ArrayList<Author> authorList = new ArrayList<Author>();
		authorList.addAll(authors);
		Collections.sort(authorList);	
		return authorList;
	}
	
	/**
	 */
	public void setNortFileLocations(Collection<NortFileLocation> nortFileLocations) {
		this.nortFileLocations = new java.util.LinkedHashSet<NortFileLocation>(nortFileLocations);
	}

	/**
	 */
	public ArrayList<NortFileLocation> getNortFileLocations() {
		if (nortFileLocations == null) {
			nortFileLocations = new java.util.LinkedHashSet<NortFileLocation>();
		}	
		// Sort by sequence numbers
		ArrayList<NortFileLocation> nortFileLocationList = new ArrayList<NortFileLocation>();
		nortFileLocationList.addAll(nortFileLocations);
		Collections.sort(nortFileLocationList);	
		return nortFileLocationList;
	}

	/**
	 */
	public void setEbookNames(Collection<EbookName> ebookNames) {
		this.ebookNames = new java.util.LinkedHashSet<EbookName>(ebookNames);
	}

	/**
	 */
	public List<EbookName> getEbookNames() {
		if (ebookNames == null) {
			ebookNames = new java.util.LinkedHashSet<EbookName>();
		}
		
		// Sort by sequence numbers
		List<EbookName> nameList = new ArrayList<EbookName>();
		nameList.addAll(ebookNames);
		Collections.sort(nameList);
				
		return nameList;
	}

	public List<ExcludeDocument> getExcludeDocuments() {
		if(excludeDocuments == null) {
			excludeDocuments = new HashSet<ExcludeDocument>();
		}
		// Change to list
		List<ExcludeDocument> documents = new ArrayList<ExcludeDocument>();
		documents.addAll(excludeDocuments);
		return documents;
	}

	public void setExcludeDocuments(Collection<ExcludeDocument> excludeDocuments) {
		this.excludeDocuments = new HashSet<ExcludeDocument>(excludeDocuments);
	}

	public List<RenameTocEntry> getRenameTocEntries() {
		if(renameTocEntries == null) {
			renameTocEntries = new HashSet<RenameTocEntry>();
		}
		// Change to list
		List<RenameTocEntry> labels = new ArrayList<RenameTocEntry>();
		labels.addAll(renameTocEntries);
		return labels;
	}

	public void setRenameTocEntries(Collection<RenameTocEntry> renameTocEntries) {
		this.renameTocEntries = new HashSet<RenameTocEntry>(renameTocEntries);
	}
	
	public List<TableViewer> getTableViewers() {
		if(tableViewers == null) {
			tableViewers = new HashSet<TableViewer>();
		}
		// Change to list
		List<TableViewer> documents = new ArrayList<TableViewer>();
		documents.addAll(tableViewers);
		return documents;
	}

	public void setTableViewers(Collection<TableViewer> tableViewers) {
		this.tableViewers = new HashSet<TableViewer>(tableViewers);
	}
	
	public List<DocumentCopyright> getDocumentCopyrights() {
		if(documentCopyrights == null) {
			documentCopyrights = new HashSet<DocumentCopyright>();
		}
		// Change to list
		List<DocumentCopyright> copyrights = new ArrayList<DocumentCopyright>();
		copyrights.addAll(documentCopyrights);
		return copyrights;
	}

	public void setDocumentCopyrights(Collection<DocumentCopyright> documentCopyrights) {
		this.documentCopyrights = new HashSet<DocumentCopyright>(documentCopyrights);
	}
	
	public List<DocumentCurrency> getDocumentCurrencies() {
		if(documentCurrencies == null) {
			documentCurrencies = new HashSet<DocumentCurrency>();
		}
		// Change to list
		List<DocumentCurrency> documents = new ArrayList<DocumentCurrency>();
		documents.addAll(documentCurrencies);
		return documents;
	}

	public void setDocumentCurrencies(Collection<DocumentCurrency> documentCurrencies) {
		this.documentCurrencies = new HashSet<DocumentCurrency>(documentCurrencies);
	}

	public List<FrontMatterPage> getFrontMatterPages() {
		if (frontMatterPages == null) {
			frontMatterPages = new java.util.LinkedHashSet<FrontMatterPage>();
		}
		
		// Sort by sequence numbers
		List<FrontMatterPage> pageList = new ArrayList<FrontMatterPage>();
		pageList.addAll(frontMatterPages);
		Collections.sort(pageList);
		
		// Sort Sections
		for(FrontMatterPage page: pageList){
			// Sort by sequence numbers
			List<FrontMatterSection> sections = new ArrayList<FrontMatterSection>();
			sections.addAll(page.getFrontMatterSections());
			Collections.sort(sections);
			page.setFrontMatterSections(sections);
			
			// Sort PDFs
			for(FrontMatterSection section: sections) {
				// Sort by sequence numbers
				List<FrontMatterPdf> pdfs = new ArrayList<FrontMatterPdf>();
				pdfs.addAll(section.getPdfs());
				Collections.sort(pdfs);
				section.setPdfs(pdfs);
			}
		}
			
		return pageList;
	}

	public void setFrontMatterPages(Collection<FrontMatterPage> frontMatterPage) {
		this.frontMatterPages = new java.util.LinkedHashSet<FrontMatterPage>(frontMatterPage);
	}

	/**
	 */
	public BookDefinition() {
		super();
		this.setIsDeletedFlag(false);
		this.setPublishedOnceFlag(false);
		this.setOnePassSsoLinkFlag(true);
		this.setIncludeAnnotations(false);
		this.setIsProviewTableViewFlag(false);
		this.setIsFinalStage(true);
		this.setPilotBookStatus(PilotBookStatus.FALSE);
		this.setSourceType(SourceType.TOC);
		this.setUseReloadContent(false);
		this.setIsTocFlag(false);
		this.setIsInsStyleFlag(false);
		this.setIsDelStyleFlag(false);
		this.setIsRemoveEditorNoteHeadFlag(false);
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
		setAuthors(new java.util.LinkedHashSet<Author>(that.getAuthors()));
		setNortFileLocations(new java.util.LinkedHashSet<NortFileLocation>(that.getNortFileLocations()));
		setEbookNames(new java.util.LinkedHashSet<EbookName>(that.getEbookNames()));
		setFrontMatterPages(new java.util.LinkedHashSet<FrontMatterPage>(that.getFrontMatterPages()));
		setExcludeDocuments(new HashSet<ExcludeDocument>(that.getExcludeDocuments()));
		setRenameTocEntries(new HashSet<RenameTocEntry>(that.getRenameTocEntries()));
		setTableViewers(new HashSet<TableViewer>(that.getTableViewers()));
		setDocumentCopyrights(new HashSet<DocumentCopyright>(that.getDocumentCopyrights()));
		setDocumentCurrencies(new HashSet<DocumentCurrency>(that.getDocumentCurrencies()));
		setIncludeAnnotations(that.getIncludeAnnotations());
		setIsFinalStage(that.isFinalStage());
		setUseReloadContent(that.getUseReloadContent());
		setSourceType(that.getSourceType());
		setCwbBookName(that.getCwbBookName());
		setIsInsStyleFlag(that.isInsStyleFlag());
		setIsDelStyleFlag(that.isDelStyleFlag());
		setIsRemoveEditorNoteHeadFlag(that.isRemoveEditorNoteHeadFlag());
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
		buffer.append("isFinalStage=[").append(isFinalStage).append("] ");
		buffer.append("useReloadContent=[").append(useReloadContent).append("] ");
		buffer.append("sourceType=[").append(sourceType).append("] ");
		buffer.append("cwbBookName=[").append(cwbBookName).append("] ");
		buffer.append("isInsStyleFlag=[").append(isInsStyleFlag).append("] ");
		buffer.append("isDelStyleFlag=[").append(isDelStyleFlag).append("] ");
		buffer.append("isRemoveEditorNoteHeadFlag=[").append(isRemoveEditorNoteHeadFlag).append("] ");
		
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
		proviewFeatures.add(new Feature("Print"));
		if (getAutoUpdateSupportFlag()) proviewFeatures.add(new Feature("AutoUpdate"));
		if (getSearchIndexFlag()) proviewFeatures.add(new Feature("SearchIndex"));
		if (getEnableCopyFeatureFlag()) proviewFeatures.add(new Feature("Copy"));
		if (getOnePassSsoLinkFlag()) {
			proviewFeatures.add(new Feature("OnePassSSO", "www.westlaw.com"));	
			proviewFeatures.add(new Feature("OnePassSSO", "next.westlaw.com"));			
		}
		return (proviewFeatures);
	}

	/**
	 * The proview keywords as derived from the book definition.
	 * @return List of keywords.
	 */
	@Transient
	public ArrayList<Keyword> getKeyWords() {
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		Collection<KeywordTypeValue> keywordValues = getKeywordTypeValues();
		for(KeywordTypeValue value : keywordValues) {
			keywords.add(new Keyword(value.getKeywordTypeCode().getName(), value.getName()));
		}		
		return (keywords);
	}	
	
	/**
	 * Provides the status of the book definition
	 * @return String indicating the status
	 */
	@Transient
	public String getBookStatus() {
		String status;
		if (isDeletedFlag()) {
			status = "Deleted";
		} else { 
			if (getEbookDefinitionCompleteFlag()) {
				status = "Ready";
			} else {
				status = "Incomplete";
			}
		}
		return status;
	}
}
