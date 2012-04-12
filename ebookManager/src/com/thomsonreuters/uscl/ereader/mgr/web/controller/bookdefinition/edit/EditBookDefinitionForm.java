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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.AutoPopulatingList;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class EditBookDefinitionForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "editBookDefinitionForm";
	
	private static final int PUBLISHER_INDEX = 0;
	private static final int TITLE_NAME_INDEX = 2;

	private Long bookdefinitionId;
	private String titleId;
	private String proviewDisplayName;
	private String copyright;
	private String copyrightPageText;
	private String materialId;
	private Collection<Author> authorInfo;
	private Collection<EbookName> nameLines;
	private Collection<FrontMatterPage> frontMatters;
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
	private String publicationCutoffDate;

	private String publishDateText;
	
	// Keywords used in Proview
	private Collection<Long> keywords;
	
	private String currency;
	private String additionalTrademarkInfo;
	private boolean isComplete;
	private boolean keyCiteToplineFlag;
	private boolean autoUpdateSupport;
	private boolean searchIndex;
	private boolean isProviewTableView;
	private boolean enableCopyFeatureFlag;

	// Fully qualified title ID parts
	private String publisher;
	private String state;
	private String pubType;
	private String pubAbbr;
	private String jurisdiction;
	private String pubInfo;
	
	private String comment;
	private boolean validateForm;
	
	public EditBookDefinitionForm() {
		super();
		this.authorInfo = new AutoPopulatingList<Author>(Author.class);
		this.nameLines = new AutoPopulatingList<EbookName>(EbookName.class);
		this.frontMatters = new AutoPopulatingList<FrontMatterPage>(FrontMatterPage.class);
		this.keywords = new ArrayList<Long>();
		this.isProviewTableView = false;
		this.isComplete = false;
		this.validateForm = false;
		this.keyCiteToplineFlag = true;
		this.autoUpdateSupport = true;
		this.searchIndex = true;
		this.enableCopyFeatureFlag = false;
		this.copyright = "�";
		this.frontMatterTocLabel = "Publishing Information";
	}
	
	public void initialize(BookDefinition book) {
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
			this.nameLines = book.getEbookNames();
			this.frontMatters = book.getFrontMatterPages();
			this.publishDateText = book.getPublishDateText();
			this.currency = book.getCurrency();
			this.isTOC = book.isTocFlag();
			this.isComplete = book.getEbookDefinitionCompleteFlag();
			this.keyCiteToplineFlag = book.getKeyciteToplineFlag();
			this.autoUpdateSupport = book.getAutoUpdateSupportFlag();
			this.searchIndex = book.getSearchIndexFlag();
			this.isProviewTableView = book.isProviewTableViewFlag();
			this.isAuthorDisplayVertical = book.isAuthorDisplayVertical();
			this.enableCopyFeatureFlag = book.getEnableCopyFeatureFlag();
			this.frontMatterTocLabel = book.getFrontMatterTocLabel();
			this.additionalTrademarkInfo = book.getAdditionalTrademarkInfo();
			
			Date date = book.getPublishCutoffDate();
			if (date != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				this.publicationCutoffDate = sdf.format(date);
			}
			
			
			Collection<KeywordTypeValue> keywordValues = book.getKeywordTypeValues();
			for(KeywordTypeValue value : keywordValues) {
				this.keywords.add(value.getId());
			}
			
			parseTitleId(book);
		}
	}
	
	public void loadBookDefinition(BookDefinition book) throws ParseException {
		Set<Author> authors = new HashSet<Author>(authorInfo);
		for(Author author : authors) {
			author.setEbookDefinition(book);
		}
		book.setAuthors(authors);
		
		Set<EbookName> ebookNames = new HashSet<EbookName>(nameLines);
		for(EbookName name : ebookNames) {
			name.setEbookDefinition(book);
		}
		book.setEbookNames(ebookNames);
		
		Set<FrontMatterPage> addFrontMatters = new HashSet<FrontMatterPage>();
		for(FrontMatterPage page : frontMatters) {
			for(FrontMatterSection section : page.getFrontMatterSections()) {
				for(FrontMatterPdf pdf : section.getPdfs()){
					//Set foreign key on Pdf
					pdf.setSection(section);
				}
				// Set foreign key on Section
				section.setFrontMatterPage(page);
			}
			// Set foreign key on Page
			page.setEbookDefinition(book);
			addFrontMatters.add(page);
		}
		book.setFrontMatterPages(addFrontMatters);
		
		
		book.setAutoUpdateSupportFlag(autoUpdateSupport);
		book.setCopyright(copyright);
		book.setCopyrightPageText(copyrightPageText);
		book.setCurrency(currency);
		book.setAdditionalTrademarkInfo(additionalTrademarkInfo);
		
		DocumentTypeCode dtc = new DocumentTypeCode();
		dtc.setId(contentTypeId);
		book.setDocumentTypeCodes(dtc);
		book.setEbookDefinitionCompleteFlag(isComplete);
		book.setEbookDefinitionId(bookdefinitionId);
		book.setFullyQualifiedTitleId(titleId);
		// Needs to come after setting fully qualified title id
		book.setCoverImage(book.getTitleId() + ".png");
		
		book.setIsbn(isbn);
		book.setIsProviewTableViewFlag(isProviewTableView);
		book.setIsTocFlag(isTOC);
		book.setEnableCopyFeatureFlag(enableCopyFeatureFlag);
		book.setKeyciteToplineFlag(keyCiteToplineFlag);
		
		
		if(keywords != null) {
			Set<KeywordTypeValue> keywordValues = new HashSet<KeywordTypeValue>();
			for(Long id : keywords) {
				KeywordTypeValue keywordValue = new KeywordTypeValue();
				keywordValue.setId(id);
				keywordValues.add(keywordValue);
			}
			book.setKeywordTypeValues(keywordValues);
		}
		
		book.setMaterialId(materialId);
		book.setNortDomain(nortDomain);
		book.setNortFilterView(nortFilterView);
		book.setProviewDisplayName(proviewDisplayName);
		
		if(publicationCutoffDate != null) {
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
			Date date = (Date)formatter.parse(publicationCutoffDate); 
			book.setPublishCutoffDate(date);
		}
		
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
	}
	
	private void parseTitleId(BookDefinition book) {
		DocumentTypeCode documentType = book.getDocumentTypeCodes(); 
		this.contentTypeId = documentType.getId();
		
		// Parse titleId
		String[] fullyqualifiedtitleArray = this.titleId.split("/");
		String [] titleIdArray = fullyqualifiedtitleArray[TITLE_NAME_INDEX].split("_");
		
		this.publisher = fullyqualifiedtitleArray[PUBLISHER_INDEX];
	
		if (documentType.getName().equals(WebConstants.KEY_ANALYTICAL)) {
			this.pubAbbr = titleIdArray[0];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		} else if (documentType.getName().equals(WebConstants.KEY_COURT_RULES)) {
			this.state = titleIdArray[0];
			this.pubType = titleIdArray[1];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		} else if (documentType.getName().equals(WebConstants.KEY_SLICE_CODES)) {
			this.jurisdiction = titleIdArray[0];
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		} else {
			this.pubInfo = createPubInfo(documentType, titleIdArray);
		}
	}
	
	private String createPubInfo(DocumentTypeCode documentType, String[] titleId) {
		int index;
		StringBuilder pubInfo = new StringBuilder();
		
		if (documentType.getName().equalsIgnoreCase(WebConstants.KEY_COURT_RULES)) {
			index = 2;
		} else {
			index = 1;
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

	public Collection<EbookName> getNameLines() {
		return nameLines;
	}

	public void setNameLines(Collection<EbookName> nameLines) {
		this.nameLines = nameLines;
	}
	
	public Collection<FrontMatterPage> getFrontMatters() {
		return frontMatters;
	}

	public void setFrontMatters(Collection<FrontMatterPage> frontMatters) {
		this.frontMatters = frontMatters;
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

	public Collection<Author> getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(Collection<Author> authorInfo) {
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

	public String getPublicationCutoffDate() {
		return publicationCutoffDate;
	}

	public void setPublicationCutoffDate(String publicationCutoffDate) {
		this.publicationCutoffDate = publicationCutoffDate;
	}

	public String getPublishDateText() {
		return publishDateText;
	}

	public void setPublishDateText(String publishDateText) {
		this.publishDateText = publishDateText;
	}

	public Collection<Long> getKeywords() {
		return keywords;
	}

	public void setKeywords(Collection<Long> keywords) {
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

	public boolean getIsProviewTableView() {
		return isProviewTableView;
	}

	public void setIsProviewTableView(boolean isProviewTableView) {
		this.isProviewTableView = isProviewTableView;
	}

	public boolean isEnableCopyFeatureFlag() {
		return enableCopyFeatureFlag;
	}

	public void setEnableCopyFeatureFlag(boolean enableCopyFeatureFlag) {
		this.enableCopyFeatureFlag = enableCopyFeatureFlag;
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

	public boolean isValidateForm() {
		return validateForm;
	}

	public void setValidateForm(boolean validateForm) {
		this.validateForm = validateForm;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public void removeEmptyRows() {
		//Clear out empty author
        for (Iterator<Author> i = authorInfo.iterator(); i.hasNext();) {
        	Author author = i.next();
            if (author == null || author.isNameEmpty()) {
                i.remove();
            }
        }
        
        //Clear out empty name line
        for (Iterator<EbookName> i = nameLines.iterator(); i.hasNext();) {
        	EbookName nameLine = i.next();
            if (nameLine == null || nameLine.isEmpty()) {
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
