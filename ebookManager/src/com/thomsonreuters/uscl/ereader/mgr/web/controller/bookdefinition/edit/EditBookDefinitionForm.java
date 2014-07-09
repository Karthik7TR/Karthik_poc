/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.AutoPopulatingList;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class EditBookDefinitionForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "editBookDefinitionForm";
	
	private static final int PUBLISHER_INDEX = 0;
	private static final int PRODUCT_CODE_INDEX = 1;
	private static final int TITLE_NAME_INDEX = 2;

	private Long bookdefinitionId;
	private String titleId;
	private String proviewDisplayName;
	private String copyright;
	private String copyrightPageText;
	private String materialId;
	private List<Author> authorInfo;
	private List<FrontMatterPage> frontMatters;
	private boolean isExcludeDocumentsUsed;
	private Collection<ExcludeDocument> excludeDocuments;
	private Collection<ExcludeDocument> excludeDocumentsCopy;
	private boolean isRenameTocEntriesUsed;
	private Collection<RenameTocEntry> renameTocEntries;
	private Collection<RenameTocEntry> renameTocEntriesCopy;
	private boolean isTableViewersUsed;
	private Collection<TableViewer> tableViewers;
	private Collection<TableViewer> tableViewersCopy;
	private Collection<DocumentCopyright> documentCopyrights;
	private Collection<DocumentCopyright> documentCopyrightsCopy;
	private Collection<DocumentCurrency> documentCurrencies;
	private Collection<DocumentCurrency> documentCurrenciesCopy;
	private boolean isFinalStage;
	private boolean isAuthorDisplayVertical;
	private String frontMatterTocLabel;
	private boolean isTOC;
	private String rootTocGuid;
	private String docCollectionName;
	private String tocCollectionName;
	private String nortDomain;
	private String nortFilterView;
	private Long contentTypeId;
	private String isbn;
	private boolean isPublicationCutoffDateUsed;
	private String publicationCutoffDate;
	private boolean includeAnnotations;
	private boolean useReloadContent;

	private String publishDateText;
	
	// Keywords used in Proview
	private Collection<String> keywords;
	
	private String currency;
	private String additionalTrademarkInfo;
	private boolean isComplete;
	private boolean keyCiteToplineFlag;
	private boolean autoUpdateSupport;
	private boolean searchIndex;
	private boolean enableCopyFeatureFlag;
	private PilotBookStatus pilotBookStatus;

	// Fully qualified title ID parts
	private String publisher;
	private String state;
	private String pubType;
	private String pubAbbr;
	private String jurisdiction;
	private String pubInfo;
	private String productCode;
	private String comment;
	private EbookName frontMatterTitle = new EbookName();
	private EbookName frontMatterSubtitle = new EbookName();
	private EbookName frontMatterSeries = new EbookName();
	
	/** Used to preview front matter, holds the FM page sequence number that uniquely identifies
	    what page the user want to preview. */
	private Long selectedFrontMatterPreviewPage;
	
	private boolean validateForm;
	
	public EditBookDefinitionForm() {
		super();
		this.authorInfo = new AutoPopulatingList<Author>(Author.class);
		this.frontMatters = new AutoPopulatingList<FrontMatterPage>(FrontMatterPage.class);
		this.excludeDocuments = new AutoPopulatingList<ExcludeDocument>(ExcludeDocument.class);
		this.excludeDocumentsCopy = new AutoPopulatingList<ExcludeDocument>(ExcludeDocument.class);
		this.renameTocEntries = new AutoPopulatingList<RenameTocEntry>(RenameTocEntry.class);
		this.renameTocEntriesCopy = new AutoPopulatingList<RenameTocEntry>(RenameTocEntry.class);
		this.tableViewers = new AutoPopulatingList<TableViewer>(TableViewer.class);
		this.tableViewersCopy = new AutoPopulatingList<TableViewer>(TableViewer.class);
		this.documentCopyrights = new AutoPopulatingList<DocumentCopyright>(DocumentCopyright.class);
		this.documentCopyrightsCopy = new AutoPopulatingList<DocumentCopyright>(DocumentCopyright.class);
		this.documentCurrencies = new AutoPopulatingList<DocumentCurrency>(DocumentCurrency.class);
		this.documentCurrenciesCopy = new AutoPopulatingList<DocumentCurrency>(DocumentCurrency.class);
		this.keywords = new AutoPopulatingList<String>(String.class);
		this.isComplete = false;
		this.validateForm = false;
		this.keyCiteToplineFlag = true;
		this.autoUpdateSupport = true;
		this.searchIndex = true;
		this.enableCopyFeatureFlag = true;
		this.isExcludeDocumentsUsed = false;
		this.isRenameTocEntriesUsed = false;
		this.isTableViewersUsed = false;
		this.isFinalStage = true;
		this.isPublicationCutoffDateUsed = false;
		this.includeAnnotations = false;
		this.useReloadContent = false;
		this.pilotBookStatus = PilotBookStatus.FALSE;
		this.copyright = "\u00A9";
		this.frontMatterTocLabel = "Publishing Information";
		this.publishDateText = "see Title page for currentness";
	}
	
	/**
	 * Reset some book definition fields before copying
	 * in to the form
	 * @param bookDef
	 */
	public void copyBookDefinition(BookDefinition bookDef, List<KeywordTypeCode> keywordCodes) {
		bookDef.setEbookDefinitionId(null);
		bookDef.setProviewDisplayName(null);
		bookDef.setIsbn(null);
		bookDef.setMaterialId(null);
		bookDef.setRootTocGuid(null);
		bookDef.setNortFilterView(null);
		bookDef.setEbookDefinitionCompleteFlag(false);
		bookDef.setFrontMatterPages(new AutoPopulatingList<FrontMatterPage>(FrontMatterPage.class));
		bookDef.setExcludeDocuments(new AutoPopulatingList<ExcludeDocument>(ExcludeDocument.class));
		bookDef.setRenameTocEntries(new AutoPopulatingList<RenameTocEntry>(RenameTocEntry.class));
		bookDef.setTableViewers(new AutoPopulatingList<TableViewer>(TableViewer.class));
		bookDef.setDocumentCopyrights(new AutoPopulatingList<DocumentCopyright>(DocumentCopyright.class));
		bookDef.setDocumentCurrencies(new AutoPopulatingList<DocumentCurrency>(DocumentCurrency.class));
		bookDef.setPilotBookStatus(PilotBookStatus.FALSE);
		bookDef.setUseReloadContent(false);
		
		// Need to null surrogate and foreign keys.
		// New keys will be made when Copy of Book Definition is saved.
		for(EbookName name : bookDef.getEbookNames()) {
			name.setEbookDefinition(null);
			name.setEbookNameId(null);
		}
		
		for(Author author: bookDef.getAuthors()) {
			author.setAuthorId(null);
			author.setEbookDefinition(null);
		}
		
		initialize(bookDef, keywordCodes);
	}
	
	public void initialize(BookDefinition book, List<KeywordTypeCode> keywordCodes) {
		if(book != null) {
			this.bookdefinitionId = book.getEbookDefinitionId();
			this.titleId = book.getFullyQualifiedTitleId();
			this.proviewDisplayName = book.getProviewDisplayName();
			this.copyright = book.getCopyright();
			this.copyrightPageText = book.getCopyrightPageText();
			this.materialId = book.getMaterialId();
			this.rootTocGuid = book.getRootTocGuid();
			this.tocCollectionName = book.getTocCollectionName();
			this.docCollectionName = book.getDocCollectionName();
			this.nortDomain = book.getNortDomain();
			this.nortFilterView = book.getNortFilterView();
			this.isbn = book.getIsbn();
			this.authorInfo = book.getAuthors();
			this.frontMatters = book.getFrontMatterPages();
			this.publishDateText = book.getPublishDateText();
			this.currency = book.getCurrency();
			this.isTOC = book.isTocFlag();
			this.isComplete = book.getEbookDefinitionCompleteFlag();
			this.keyCiteToplineFlag = book.getKeyciteToplineFlag();
			this.autoUpdateSupport = book.getAutoUpdateSupportFlag();
			this.searchIndex = book.getSearchIndexFlag();
			this.isAuthorDisplayVertical = book.isAuthorDisplayVertical();
			this.enableCopyFeatureFlag = book.getEnableCopyFeatureFlag();
			this.pilotBookStatus = book.getPilotBookStatus();
			this.frontMatterTocLabel = book.getFrontMatterTocLabel();
			this.additionalTrademarkInfo = book.getAdditionalTrademarkInfo();
			this.excludeDocuments = book.getExcludeDocuments();
			this.excludeDocumentsCopy = book.getExcludeDocuments();
			this.renameTocEntries = book.getRenameTocEntries();
			this.renameTocEntriesCopy = book.getRenameTocEntries();
			this.tableViewers = book.getTableViewers();
			this.tableViewersCopy = book.getTableViewers();
			this.documentCopyrights = book.getDocumentCopyrights();
			this.documentCopyrightsCopy = book.getDocumentCopyrights();
			this.documentCurrencies = book.getDocumentCurrencies();
			this.documentCurrenciesCopy = book.getDocumentCurrencies();
			this.includeAnnotations = book.getIncludeAnnotations();
			this.isFinalStage = book.isFinalStage();
			this.useReloadContent = book.getUseReloadContent();
			
			// Determine if ExcludeDocuments are present in Book Definition
			if (book.getExcludeDocuments().size() > 0) {
				this.isExcludeDocumentsUsed = true;
			}
			
			// Determine if RenameTocEntries are present in Book Definition
			if (book.getRenameTocEntries().size() > 0) {
				this.isRenameTocEntriesUsed = true;
			}
			
			if(this.tableViewers.size() > 0) {
				this.isTableViewersUsed = true;
			}
			
			
			// Determine if Publish Cut-off Date is used
			Date date = book.getPublishCutoffDate();
			if (date != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(CoreConstants.DATE_FORMAT_PATTERN);
				this.publicationCutoffDate = sdf.format(date);
				this.isPublicationCutoffDateUsed = true;
			}
			
			/*
			 * Field keywords needs to be assembled in sorted order of KeywordTypeCode
			 * This is to get the path to correctly show the selected value on the Edit/Copy 
			 * Book Definition form.  The values are selected based on the index in keywords field
			 */
			HashMap<KeywordTypeCode, String> keywordMap = new LinkedHashMap<KeywordTypeCode, String>();
			for(KeywordTypeCode keywordCode : keywordCodes) {
				keywordMap.put(keywordCode, "");
			}
			
			Collection<KeywordTypeValue> keywordValues = book.getKeywordTypeValues();
			for(KeywordTypeValue value : keywordValues) {
				keywordMap.put(value.getKeywordTypeCode(), value.getId().toString());
			}
			
			this.keywords.addAll(keywordMap.values());
			
			setupFrontMatterNames(book.getEbookNames());
			
			parseTitleId(book);
		}
	}
	
	private void setupFrontMatterNames(List<EbookName> names){
		for(EbookName name: names){
			switch(name.getSequenceNum()){
			case 1:
				this.frontMatterTitle = name;
				break;
			case 2:
				this.frontMatterSubtitle = name;
				break;
			case 3:
				this.frontMatterSeries = name;
				break;
			default:
				break;
			}
		}
	}
	
	public void loadBookDefinition(BookDefinition book) throws ParseException {
		book.setEbookDefinitionId(bookdefinitionId);
		
		List<Author> authors = new ArrayList<Author>();
		int i = 1;
		for(Author author : authorInfo) {
			Author authorCopy = new Author();
			authorCopy.copy(author);
			authorCopy.setEbookDefinition(book);
			// Update the sequence number to be in order
			authorCopy.setSequenceNum(i);
			authors.add(authorCopy);
			i++;
		}
		book.setAuthors(authors);
		
		// Add Front Matter Book Names
		Set<EbookName> ebookNames = new HashSet<EbookName>();
		if(!frontMatterTitle.isEmpty()){
			frontMatterTitle.setEbookDefinition(book);
			ebookNames.add(frontMatterTitle);
		}
		if(!frontMatterSubtitle.isEmpty()){
			frontMatterSubtitle.setEbookDefinition(book);
			ebookNames.add(frontMatterSubtitle);
		}
		if(!frontMatterSeries.isEmpty()){
			frontMatterSeries.setEbookDefinition(book);
			ebookNames.add(frontMatterSeries);
		}
		book.setEbookNames(ebookNames);

		List<FrontMatterPage> pages = new ArrayList<FrontMatterPage>();
		i = 1;
		for(FrontMatterPage page : frontMatters) {
			FrontMatterPage pageCopy = new FrontMatterPage();
			pageCopy.copy(page);
			
			int j = 1;
			for(FrontMatterSection section : pageCopy.getFrontMatterSections()) {
				int k = 1;
				for(FrontMatterPdf pdf : section.getPdfs()){
					//Set foreign key on Pdf
					pdf.setSection(section);
					// Update the sequence numbers to be in order
					pdf.setSequenceNum(k);
					k++;
				}
				// Update the sequence numbers to be in order
				section.setSequenceNum(j);
				// Set foreign key on Section
				section.setFrontMatterPage(pageCopy);
				j++;
			}
			// Set foreign key on Page
			pageCopy.setEbookDefinition(book);
			// Update the sequence numbers to be in order
			pageCopy.setSequenceNum(i);
			pages.add(pageCopy);
			i++;
		}
		book.setFrontMatterPages(pages);
		
		// Compare with copy to determine if date needs update
		for(ExcludeDocument document : excludeDocuments) {
			boolean exists = false;
			document.setBookDefinition(book);
			for(ExcludeDocument documentCopy : excludeDocumentsCopy) {
				if(document.equals(documentCopy)) {
					exists = true;
				}
			}
			// Update date
			if(!exists) {
				document.setLastUpdated(new Date());
			}
		}
		book.setExcludeDocuments(excludeDocuments);
		
		// Compare with copy to determine if date needs update
		for(RenameTocEntry label : renameTocEntries) {
			boolean exists = false;
			label.setBookDefinition(book);
			for(RenameTocEntry labelCopy : renameTocEntriesCopy) {
				if(label.equals(labelCopy)) {
					exists = true;
				}
			}
			// Update date
			if(!exists) {
				label.setLastUpdated(new Date());
			}
		}
		book.setRenameTocEntries(renameTocEntries);
		
		// Compare with copy to determine if date needs update
		for(TableViewer document : tableViewers) {
			boolean exists = false;
			document.setBookDefinition(book);
			for(TableViewer documentCopy : tableViewersCopy) {
				if(document.equals(documentCopy)) {
					exists = true;
				}
			}
			// Update date
			if(!exists) {
				document.setLastUpdated(new Date());
			}
		}
		book.setTableViewers(tableViewers);
		
		for(DocumentCopyright documentCopyright: documentCopyrights) {
			boolean exists = false;
			documentCopyright.setBookDefinition(book);
			for(DocumentCopyright documentCopyrightCopy : documentCopyrightsCopy) {
				if(documentCopyright.equals(documentCopyrightCopy)) {
					exists = true;
				}
			}
			// Update date
			if(!exists) {
				documentCopyright.setLastUpdated(new Date());
			}
		}
		book.setDocumentCopyrights(documentCopyrights);
		
		for(DocumentCurrency documentCurrency : documentCurrencies) {
			boolean exists = false;
			documentCurrency.setBookDefinition(book);
			for(DocumentCurrency documentCurrencyCopy : documentCurrenciesCopy) {
				if(documentCurrency.equals(documentCurrencyCopy)) {
					exists = true;
				}
			}
			// Update date
			if(!exists) {
				documentCurrency.setLastUpdated(new Date());
			}
		}
		book.setDocumentCurrencies(documentCurrencies);
		
		book.setAutoUpdateSupportFlag(autoUpdateSupport);
		book.setCopyright(copyright);
		book.setCopyrightPageText(copyrightPageText);
		book.setCurrency(currency);
		book.setAdditionalTrademarkInfo(additionalTrademarkInfo);
		
		DocumentTypeCode dtc = new DocumentTypeCode();
		dtc.setId(contentTypeId != null ? contentTypeId : 0L);
		book.setDocumentTypeCodes(dtc);
		book.setEbookDefinitionCompleteFlag(isComplete);
		book.setFullyQualifiedTitleId(titleId);
		book.setCoverImage(this.createCoverImageName());
		
		book.setIsbn(isbn);
		book.setIsTocFlag(isTOC);
		book.setEnableCopyFeatureFlag(enableCopyFeatureFlag);
		book.setPilotBookStatus(pilotBookStatus);
		book.setKeyciteToplineFlag(keyCiteToplineFlag);

		Set<KeywordTypeValue> keywordValues = new HashSet<KeywordTypeValue>();
		for(String id : keywords) {
			if(StringUtils.isNotBlank(id)) {
				KeywordTypeValue keywordValue = new KeywordTypeValue();
				keywordValue.setId(Long.valueOf(id));
				keywordValues.add(keywordValue);
			}
		}
		book.setKeywordTypeValues(keywordValues);
		
		book.setMaterialId(materialId);
		book.setNortDomain(nortDomain);
		book.setNortFilterView(nortFilterView);
		book.setProviewDisplayName(proviewDisplayName);
		
		// Parse Date
		DateFormat formatter = new SimpleDateFormat(CoreConstants.DATE_FORMAT_PATTERN); 
		Date date = publicationCutoffDate != null? (Date)formatter.parse(publicationCutoffDate) : null; 
		book.setPublishCutoffDate(date);

		
		book.setPublishDateText(publishDateText);
		
		PublisherCode publishercode = new PublisherCode();
		publishercode.setName(publisher);
		book.setPublisherCodes(publishercode);
		
		book.setRootTocGuid(rootTocGuid);
		book.setSearchIndexFlag(searchIndex);
		book.setTocCollectionName(tocCollectionName);
		book.setDocCollectionName(docCollectionName);
		book.setIsAuthorDisplayVertical(isAuthorDisplayVertical);
		book.setFrontMatterTocLabel(frontMatterTocLabel);
		book.setIncludeAnnotations(includeAnnotations);
		book.setIsFinalStage(isFinalStage);
		book.setUseReloadContent(useReloadContent);
	}
	
	private void parseTitleId(BookDefinition book) {
		DocumentTypeCode documentType = book.getDocumentTypeCodes(); 
		if(documentType != null) {
			this.contentTypeId = documentType.getId();
		}
		
		// Parse titleId
		String[] fullyqualifiedtitleArray = this.titleId.split("/");
		String [] titleIdArray = fullyqualifiedtitleArray[TITLE_NAME_INDEX].split("_");
		
		this.publisher = fullyqualifiedtitleArray[PUBLISHER_INDEX];
	
		if (documentType.getName().equals(WebConstants.DOCUMENT_TYPE_ANALYTICAL)) {
			this.pubAbbr = titleIdArray[0];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		} else if (documentType.getName().equals(WebConstants.DOCUMENT_TYPE_COURT_RULES)) {
			this.state = titleIdArray[0];
			this.pubType = titleIdArray[1];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		} else if (documentType.getName().equals(WebConstants.DOCUMENT_TYPE_SLICE_CODES)) {
			this.jurisdiction = titleIdArray[0];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		} else {
			this.productCode = fullyqualifiedtitleArray[PRODUCT_CODE_INDEX];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		}
	}
	
	private String createPubInfo(DocumentTypeCode documentType, String[] titleId) {
		int index;
		StringBuilder pubInfo = new StringBuilder();
		
		if (documentType.getName().equalsIgnoreCase(WebConstants.DOCUMENT_TYPE_COURT_RULES)) {
			index = 2;
		} else if(documentType.getName().equalsIgnoreCase(WebConstants.DOCUMENT_TYPE_ANALYTICAL) || 
				documentType.getName().equalsIgnoreCase(WebConstants.DOCUMENT_TYPE_SLICE_CODES)) {
			index = 1;
		} else {
			index = 0;
		}
		
		for(int i = index; i < titleId.length ; i++) {
			pubInfo.append(titleId[i]);
			pubInfo.append("_");
		}
		if (pubInfo.length() > 0)
			pubInfo.deleteCharAt(pubInfo.length() - 1);
		
		return pubInfo.toString();
	}
	
	public Long getBookdefinitionId() {
		return bookdefinitionId;
	}

	public void setBookdefinitionId(Long bookdefinitionId) {
		this.bookdefinitionId = bookdefinitionId;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getProviewDisplayName() {
		return proviewDisplayName;
	}

	public void setProviewDisplayName(String proviewDisplayName) {
		this.proviewDisplayName = proviewDisplayName;
	}

	
	public List<FrontMatterPage> getFrontMatters() {
		return frontMatters;
	}

	public void setFrontMatters(List<FrontMatterPage> frontMatters) {
		this.frontMatters = frontMatters;
	}


	public boolean isExcludeDocumentsUsed() {
		return isExcludeDocumentsUsed;
	}

	public void setExcludeDocumentsUsed(boolean isExcludeDocumentsUsed) {
		this.isExcludeDocumentsUsed = isExcludeDocumentsUsed;
	}

	public Collection<ExcludeDocument> getExcludeDocuments() {
		return excludeDocuments;
	}

	public void setExcludeDocuments(Collection<ExcludeDocument> excludeDocuments) {
		this.excludeDocuments = excludeDocuments;
	}

	public Collection<ExcludeDocument> getExcludeDocumentsCopy() {
		return excludeDocumentsCopy;
	}

	public void setExcludeDocumentsCopy(
			Collection<ExcludeDocument> excludeDocumentsCopy) {
		this.excludeDocumentsCopy = excludeDocumentsCopy;
	}

	public boolean isRenameTocEntriesUsed() {
		return isRenameTocEntriesUsed;
	}

	public void setRenameTocEntriesUsed(boolean isRenameTocEntriesUsed) {
		this.isRenameTocEntriesUsed = isRenameTocEntriesUsed;
	}

	public Collection<RenameTocEntry> getRenameTocEntries() {
		return renameTocEntries;
	}

	public void setRenameTocEntries(Collection<RenameTocEntry> renameTocEntries) {
		this.renameTocEntries = renameTocEntries;
	}

	public Collection<RenameTocEntry> getRenameTocEntriesCopy() {
		return renameTocEntriesCopy;
	}

	public void setRenameTocEntriesCopy(
			Collection<RenameTocEntry> renameTocEntriesCopy) {
		this.renameTocEntriesCopy = renameTocEntriesCopy;
	}

	public boolean isTableViewersUsed() {
		return isTableViewersUsed;
	}

	public void setTableViewersUsed(boolean isTableViewersUsed) {
		this.isTableViewersUsed = isTableViewersUsed;
	}

	public Collection<TableViewer> getTableViewers() {
		return tableViewers;
	}

	public void setTableViewers(Collection<TableViewer> tableViewers) {
		this.tableViewers = tableViewers;
	}

	public Collection<TableViewer> getTableViewersCopy() {
		return tableViewersCopy;
	}

	public void setTableViewersCopy(Collection<TableViewer> tableViewersCopy) {
		this.tableViewersCopy = tableViewersCopy;
	}

	public Collection<DocumentCopyright> getDocumentCopyrights() {
		return documentCopyrights;
	}

	public void setDocumentCopyrights(
			Collection<DocumentCopyright> documentCopyrights) {
		this.documentCopyrights = documentCopyrights;
	}

	public Collection<DocumentCopyright> getDocumentCopyrightsCopy() {
		return documentCopyrightsCopy;
	}

	public void setDocumentCopyrightsCopy(
			Collection<DocumentCopyright> documentCopyrightsCopy) {
		this.documentCopyrightsCopy = documentCopyrightsCopy;
	}

	public Collection<DocumentCurrency> getDocumentCurrencies() {
		return documentCurrencies;
	}

	public void setDocumentCurrencies(
			Collection<DocumentCurrency> documentCurrencies) {
		this.documentCurrencies = documentCurrencies;
	}

	public Collection<DocumentCurrency> getDocumentCurrenciesCopy() {
		return documentCurrenciesCopy;
	}

	public void setDocumentCurrenciesCopy(
			Collection<DocumentCurrency> documentCurrenciesCopy) {
		this.documentCurrenciesCopy = documentCurrenciesCopy;
	}

	public boolean isFinalStage() {
		return isFinalStage;
	}

	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getCopyrightPageText() {
		return copyrightPageText;
	}

	public void setCopyrightPageText(String copyrightPageText) {
		this.copyrightPageText = copyrightPageText;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public List<Author> getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(List<Author> authorInfo) {
		this.authorInfo = authorInfo;
	}

	public boolean getIsAuthorDisplayVertical() {
		return isAuthorDisplayVertical;
	}

	public void setIsAuthorDisplayVertical(boolean isAuthorDisplayVertical) {
		this.isAuthorDisplayVertical = isAuthorDisplayVertical;
	}

	public String getFrontMatterTocLabel() {
		return frontMatterTocLabel;
	}

	public void setFrontMatterTocLabel(String frontMatterTocLabel) {
		this.frontMatterTocLabel = frontMatterTocLabel;
	}

	public boolean getIsTOC() {
		return isTOC;
	}

	public void setIsTOC(boolean isTOC) {
		this.isTOC = isTOC;
	}

	public String getRootTocGuid() {
		return rootTocGuid;
	}

	public void setRootTocGuid(String rootTocGuid) {
		this.rootTocGuid = rootTocGuid;
	}

	public String getTocCollectionName() {
		return tocCollectionName;
	}

	public void setTocCollectionName(String tocCollectionName) {
		this.tocCollectionName = tocCollectionName;
	}

	public String getDocCollectionName() {
		return docCollectionName;
	}

	public void setDocCollectionName(String docCollectionName) {
		this.docCollectionName = docCollectionName;
	}

	public String getNortDomain() {
		return nortDomain;
	}

	public void setNortDomain(String nortDomain) {
		this.nortDomain = nortDomain;
	}

	public String getNortFilterView() {
		return nortFilterView;
	}

	public void setNortFilterView(String nortFilterView) {
		this.nortFilterView = nortFilterView;
	}

	public Long getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(Long contentTypeId) {
		this.contentTypeId = contentTypeId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean isPublicationCutoffDateUsed() {
		return isPublicationCutoffDateUsed;
	}

	public void setPublicationCutoffDateUsed(boolean isPublicationCutoffDateUsed) {
		this.isPublicationCutoffDateUsed = isPublicationCutoffDateUsed;
	}

	public String getPublicationCutoffDate() {
		return publicationCutoffDate;
	}

	public void setPublicationCutoffDate(String publicationCutoffDate) {
		this.publicationCutoffDate = publicationCutoffDate;
	}

	public boolean isIncludeAnnotations() {
		return includeAnnotations;
	}

	public void setIncludeAnnotations(boolean includeAnnotations) {
		this.includeAnnotations = includeAnnotations;
	}

	public String getPublishDateText() {
		return publishDateText;
	}

	public void setPublishDateText(String publishDateText) {
		this.publishDateText = publishDateText;
	}

	public Collection<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Collection<String> keywords) {
		this.keywords = keywords;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public boolean getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isEnableCopyFeatureFlag() {
		return enableCopyFeatureFlag;
	}

	public void setEnableCopyFeatureFlag(boolean enableCopyFeatureFlag) {
		this.enableCopyFeatureFlag = enableCopyFeatureFlag;
	}

	public PilotBookStatus getPilotBook() {
		return pilotBookStatus;
	}

	public void setPilotBook(PilotBookStatus isPilotBook) {
		this.pilotBookStatus = isPilotBook;
	}

	public String getAdditionalTrademarkInfo() {
		return additionalTrademarkInfo;
	}

	public void setAdditionalTrademarkInfo(String additionalTrademarkInfo) {
		this.additionalTrademarkInfo = additionalTrademarkInfo;
	}

	public boolean isKeyCiteToplineFlag() {
		return keyCiteToplineFlag;
	}

	public void setKeyCiteToplineFlag(boolean keyCiteToplineFlag) {
		this.keyCiteToplineFlag = keyCiteToplineFlag;
	}

	public boolean isAutoUpdateSupport() {
		return autoUpdateSupport;
	}

	public void setAutoUpdateSupport(boolean autoUpdateSupport) {
		this.autoUpdateSupport = autoUpdateSupport;
	}

	public boolean isSearchIndex() {
		return searchIndex;
	}

	public void setSearchIndex(boolean searchIndex) {
		this.searchIndex = searchIndex;
	}
	
	public boolean getUseReloadContent() {
		return useReloadContent;
	}
	
	public void setUseReloadContent(boolean useReloadContent) {
		this.useReloadContent = useReloadContent;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPubType() {
		return pubType;
	}

	public void setPubType(String pubType) {
		this.pubType = pubType;
	}

	public String getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	public String getPubInfo() {
		return pubInfo;
	}

	public void setPubInfo(String pubInfo) {
		this.pubInfo = pubInfo;
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPubAbbr() {
		return pubAbbr;
	}

	public void setPubAbbr(String pubAbbr) {
		this.pubAbbr = pubAbbr;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public EbookName getFrontMatterTitle() {
		return frontMatterTitle;
	}

	public void setFrontMatterTitle(EbookName frontMatterTitle) {
		this.frontMatterTitle = frontMatterTitle;
	}

	public EbookName getFrontMatterSubtitle() {
		return frontMatterSubtitle;
	}

	public void setFrontMatterSubtitle(EbookName frontMatterSubtitle) {
		this.frontMatterSubtitle = frontMatterSubtitle;
	}

	public EbookName getFrontMatterSeries() {
		return frontMatterSeries;
	}

	public void setFrontMatterSeries(EbookName frontMatterSeries) {
		this.frontMatterSeries = frontMatterSeries;
	}

	public boolean isValidateForm() {
		return validateForm;
	}

	public void setValidateForm(boolean validateForm) {
		this.validateForm = validateForm;
	}
	public Long getSelectedFrontMatterPreviewPage() {
		return selectedFrontMatterPreviewPage;
	}
	public void setSelectedFrontMatterPreviewPage(Long fmPageSeqNum) {
		this.selectedFrontMatterPreviewPage = fmPageSeqNum;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public String createCoverImageName() {
		String[] titleIdArray = titleId.split("/");
		StringBuilder buffer = new StringBuilder(StringUtils.join(titleIdArray, "_"));
		buffer.append("_cover");
		buffer.append(".png");
		return buffer.toString();
	}
	
	public String createPilotBookCsvName() {
		String[] titleIdArray = titleId.split("/");
		StringBuilder buffer = new StringBuilder(StringUtils.join(titleIdArray, "_"));
		buffer.append(".csv");
		return buffer.toString();
	}
	
	public void removeEmptyRows() {
		//Clear out empty author
        for (Iterator<Author> i = authorInfo.iterator(); i.hasNext();) {
        	Author author = i.next();
            if (author == null || author.isNameEmpty()) {
                i.remove();
            }
        }
        
        //Clear out empty ExcludeDocument
        for (Iterator<ExcludeDocument> i = excludeDocuments.iterator(); i.hasNext();) {
        	ExcludeDocument document = i.next();
            if (document == null || document.isEmpty()) {
                i.remove();
            }
        }
        
        //Clear out empty RenameTocEntry
        for (Iterator<RenameTocEntry> i = renameTocEntries.iterator(); i.hasNext();) {
        	RenameTocEntry label = i.next();
            if (label == null || label.isEmpty()) {
                i.remove();
            }
        }
        
        // Clear out empty Tableviewer
        for (Iterator<TableViewer> i = tableViewers.iterator(); i.hasNext();) {
        	TableViewer document = i.next();
            if (document == null || document.isEmpty()) {
                i.remove();
            }
        }
        
        // Clear out empty DocumentCurrency
        for (Iterator<DocumentCurrency> i = documentCurrencies.iterator(); i.hasNext();) {
        	DocumentCurrency currency = i.next();
            if (currency == null || currency.isEmpty()) {
                i.remove();
            }
        }
        
        // Clear out empty DocumentCopyright
        for (Iterator<DocumentCopyright> i = documentCopyrights.iterator(); i.hasNext();) {
        	DocumentCopyright copyright = i.next();
            if (copyright == null || copyright.isEmpty()) {
                i.remove();
            }
        }
        
        //Clear out front matter line
	    for (Iterator<FrontMatterPage> i = frontMatters.iterator(); i.hasNext();) {
	    	FrontMatterPage page = i.next();
	    	
	        if (page == null) {
	        	// Remove page from Collection
	            i.remove();
	        } else {
	        	for(Iterator<FrontMatterSection> j = page.getFrontMatterSections().iterator(); j.hasNext();) {
	        		FrontMatterSection section = j.next();
	        		if(section == null) {
	        			// Remove Section from Collection
	        			j.remove();
	        		} else {
	        			for(Iterator<FrontMatterPdf> k = section.getPdfs().iterator(); k.hasNext();) {
	        				FrontMatterPdf pdf = k.next();
	        				if (pdf == null || pdf.isEmpty()) {
	        					// Remove Pdf from Collection
	        					k.remove();
	        				}
	        			}
	        		}
	        	}
	        }
	    }
    }
}
