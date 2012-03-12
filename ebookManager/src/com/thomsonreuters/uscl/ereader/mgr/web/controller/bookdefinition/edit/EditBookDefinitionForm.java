/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.AutoPopulatingList;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatter;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class EditBookDefinitionForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "editBookDefinitionForm";
	
	private static final int PUBLISHER_INDEX = 0;
	private static final int TITLE_NAME_INDEX = 2;
	
	private static CodeService codeService;
	
	private Long bookdefinitionId;
	private String titleId;
	private String proviewDisplayName;
	private Collection<EbookName> nameLines;
	private String copyright;
	private String copyrightPageText;
	private String materialId;
	private Collection<Author> authorInfo;
	private boolean isTOC;
	private String rootTocGuid;
	private String tocCollectionName;
	private String nortDomain;
	private String nortFilterView;
	private Long contentTypeId;
	private String isbn;
	private Collection<FrontMatter> additionalFrontMatter;
	private String publicationCutoffDate;

	private String publishDateText;
	
	// Keywords used in Proview
	private Collection<Long> keywords;
	
	private String currency;
	private boolean isComplete;
	private boolean keyCiteToplineFlag;
	private boolean autoUpdateSupport;
	private boolean searchIndex;
	
	// Fully qualified title ID parts
	private String publisher;
	private String state;
	private String pubType;
	private String pubAbbr;
	private String jurisdiction;
	private String pubInfo;
	
	private boolean validateForm;
	
	public EditBookDefinitionForm() {
		super();
		this.authorInfo = new AutoPopulatingList<Author>(Author.class);
		this.nameLines = new AutoPopulatingList<EbookName>(EbookName.class);
		this.additionalFrontMatter = new AutoPopulatingList<FrontMatter>(FrontMatter.class);
		this.keywords = new ArrayList<Long>();
		this.keyCiteToplineFlag = false;
		this.isComplete = false;
		this.validateForm = false;
		this.autoUpdateSupport = true;
		this.searchIndex = true;
	}
	
	public void initialize(BookDefinition book) {
		if(book != null) {
			this.bookdefinitionId = book.getEbookDefinitionId();
			this.titleId = book.getTitleId();
			this.proviewDisplayName = book.getProviewDisplayName();
			this.copyright = book.getCopyright();
			this.copyrightPageText = book.getCopyrightPageText();
			this.materialId = book.getMaterialId();
			this.rootTocGuid = book.getRootTocGuid();
			this.tocCollectionName = book.getTocCollectionName();
			this.nortDomain = book.getNortDomain();
			this.nortFilterView = book.getNortFilterView();
			this.isbn = book.getIsbn();
			this.authorInfo = book.getAuthors();
			this.nameLines = book.getEbookNames();
			this.additionalFrontMatter = book.getFrontMatters();
			this.publishDateText = book.getPublishDateText();
			this.currency = book.getCurrency();
			this.isTOC = book.getIsTocFlag();
			this.isComplete = book.IsEbookDefinitionCompleteFlag();
			this.keyCiteToplineFlag = book.IsKeyciteToplineFlag();
			this.autoUpdateSupport = book.getAutoUpdateSupportFlag();
			this.searchIndex = book.IsSearchIndexFlag();
			
			Calendar date = book.getPublishCutoffDate();
			if (date != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				this.publicationCutoffDate = sdf.format(date.getTime());
			}
			
			
			Collection<KeywordTypeValue> keywordValues = book.getKeywordTypeValueses();
			for(KeywordTypeValue value : keywordValues) {
				this.keywords.add(value.getId());
			}
			
			parseTitleId(book);
		}
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

	public Collection<FrontMatter> getAdditionalFrontMatter() {
		return additionalFrontMatter;
	}

	public void setAdditionalFrontMatter(
			Collection<FrontMatter> additionalFrontMatter) {
		this.additionalFrontMatter = additionalFrontMatter;
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
        for (Iterator<Author> i = this.authorInfo.iterator(); i.hasNext();) {
        	Author author = i.next();
            if (author == null || author.isNameEmpty()) {
                i.remove();
            }
        }
        
        //Clear out empty name line
        for (Iterator<EbookName> i = this.nameLines.iterator(); i.hasNext();) {
        	EbookName nameLine = i.next();
            if (nameLine == null || nameLine.isEmpty()) {
                i.remove();
            }
        }
        
        //Clear out empty additional front matter
        for (Iterator<FrontMatter> i = this.additionalFrontMatter.iterator(); i.hasNext();) {
        	FrontMatter frontMatter = i.next();
            if (frontMatter == null || frontMatter.isEmpty()) {
                i.remove();
            }
        }
    }
	

	public static Map<Long, String> getDocumentTypes() {
		List<DocumentTypeCode> codes = codeService.getAllDocumentTypeCodes();
		Map<Long,String> documentTypes = new LinkedHashMap<Long,String>();
		
		for(DocumentTypeCode code : codes) {
			documentTypes.put(code.getId(), code.getName());
		}

		return documentTypes;
	}
	
	public static Map<String, String> getStates() {
		List<StateCode> codes = codeService.getAllStateCodes();
		Map<String ,String> states = new LinkedHashMap<String, String>();
		
		for(StateCode code : codes) {
			states.put(code.getName().toLowerCase(), code.getName());
		}
		
		return states;
	}
	
	public static Map<String, String> getJurisdictions() {
		List<JurisTypeCode> codes = codeService.getAllJurisTypeCodes();
		Map<String,String> jurisdictions = new LinkedHashMap<String,String>();
		
		for(JurisTypeCode code : codes) {
			jurisdictions.put(code.getName().toLowerCase(), code.getName());
		}
		
		return jurisdictions;
	}
	
	public static Map<String, String> getPubTypes() {
		List<PubTypeCode> codes = codeService.getAllPubTypeCodes();
		Map<String,String> pubTypes = new LinkedHashMap<String,String>();

		for(PubTypeCode code : codes) {
			pubTypes.put(code.getName().toLowerCase(), code.getName());
		}
		
		return pubTypes;
	}
	
	public static Map<String, String> getPublishers() {
		List<PublisherCode> codes = codeService.getAllPublisherCodes();
		Map<String,String> publishers = new LinkedHashMap<String,String>();
		
		for(PublisherCode code : codes) {
			publishers.put(code.getName().toLowerCase(), code.getName());
		}
		
		return publishers;
	}
	
	public static List<KeywordTypeCode> getKeywordCodes() {
		return codeService.getAllKeywordTypeCodes();
	}
	
	@Required
	public static void setCodeService(CodeService service) {
		codeService = service;
	}
}
