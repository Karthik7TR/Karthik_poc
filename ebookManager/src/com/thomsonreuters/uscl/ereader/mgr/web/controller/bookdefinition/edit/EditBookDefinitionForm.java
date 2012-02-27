/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.AutoPopulatingList;

import com.thomsonreuters.uscl.ereader.core.book.domain.AdditionalFrontMatter;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class EditBookDefinitionForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "editBookDefinitionForm";
	private static final int PUBLISHER_INDEX = 0;
	private static final int TITLE_NAME_INDEX = 2;
	
	private long bookdefinitionId;
	private String titleId;
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
	private String contentType;
	private String isbn;
	private Collection<AdditionalFrontMatter> additionalFrontMatter;

	private String publishDateText;
	
	// Keywords used in Proview
	private String[] keywords;
	
	private boolean autoUpdateSupport;
	private boolean searchIndex;
	private boolean onePassSSOLinking;
	private String imageCollectionInformation;
	
	private String currency;
	private boolean isComplete;
	private boolean keyCiteToplineFlag;
	
	// Fully qualified title ID parts
	private String publisher;
	private String state;
	private String pubType;
	private String pubAbbr;
	private String jurisdiction;
	private String pubInfo;
	
	public EditBookDefinitionForm() {
		super();
		
		this.authorInfo = new AutoPopulatingList<Author>(Author.class);
		this.nameLines = new AutoPopulatingList<EbookName>(EbookName.class);
		this.additionalFrontMatter = new AutoPopulatingList<AdditionalFrontMatter>(AdditionalFrontMatter.class);
		this.autoUpdateSupport = true;
		this.searchIndex = true;
		this.onePassSSOLinking = true;
		this.keyCiteToplineFlag = false;
		this.isComplete = false;
	}
	
	public void initialize(BookDefinition bookDefinition) {
		this.titleId = bookDefinition.getPrimaryKey().getFullyQualifiedTitleId();
		this.copyright = bookDefinition.getCopyright();
		this.materialId = bookDefinition.getMaterialId();
		this.rootTocGuid = bookDefinition.getRootTocGuid();
		this.tocCollectionName = bookDefinition.getTocCollectionName();
		this.nortDomain = bookDefinition.getNortDomain();
		this.nortFilterView = bookDefinition.getNortFilterView();
		this.contentType = bookDefinition.getContentType();
		this.isbn = bookDefinition.getIsbn();
		
		// TODO: update to get from bookDefinition
		EbookName ebookName = new EbookName();
		ebookName.setBook(bookDefinition);
		ebookName.setNameText(bookDefinition.getBookName());
		ebookName.setSequenceNumber((long) 1);
		this.nameLines.add(ebookName);
		
		// Parse titleId
		String[] fullyqualifiedtitleArray = this.titleId.split("/");
		String [] titleIdArray = fullyqualifiedtitleArray[TITLE_NAME_INDEX].split("_");
		
		this.publisher = fullyqualifiedtitleArray[PUBLISHER_INDEX];

		if (contentType.equals(WebConstants.KEY_ANALYTICAL)) {
			this.pubAbbr = titleIdArray[0];
			this.pubInfo = createPubInfo(titleIdArray);
		} else if (contentType.equals(WebConstants.KEY_COURT_RULES)) {
			this.state = titleIdArray[0];
			this.pubType = titleIdArray[1];
			this.pubInfo = createPubInfo(titleIdArray);
		} else if (contentType.equals(WebConstants.KEY_SLICE_CODES)) {
			this.jurisdiction = titleIdArray[0];
			this.pubInfo = createPubInfo(titleIdArray);
		} else {
			
		}
		
		//TODO: add initialization of other properties once in book definition model
	}
	
	private String createPubInfo(String[] titleId) {
		int index;
		StringBuilder pubInfo = new StringBuilder();
		
		if (contentType.equals(WebConstants.KEY_COURT_RULES)) {
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
	
	public long getBookdefinitionId() {
		return bookdefinitionId;
	}

	public void setBookdefinitionId(long bookdefinitionId) {
		this.bookdefinitionId = bookdefinitionId;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Collection<AdditionalFrontMatter> getAdditionalFrontMatter() {
		return additionalFrontMatter;
	}

	public void setAdditionalFrontMatter(
			Collection<AdditionalFrontMatter> additionalFrontMatter) {
		this.additionalFrontMatter = additionalFrontMatter;
	}

	public String getPublishDateText() {
		return publishDateText;
	}

	public void setPublishDateText(String publishDateText) {
		this.publishDateText = publishDateText;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
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

	public boolean isOnePassSSOLinking() {
		return onePassSSOLinking;
	}

	public void setOnePassSSOLinking(boolean onePassSSOLinking) {
		this.onePassSSOLinking = onePassSSOLinking;
	}

	public String getImageCollectionInformation() {
		return imageCollectionInformation;
	}

	public void setImageCollectionInformation(String imageCollectionInformation) {
		this.imageCollectionInformation = imageCollectionInformation;
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

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public static Map<String, String> getContentTypes() {
		Map<String,String> contentTypes = new LinkedHashMap<String,String>();
		contentTypes.put("Analytical", "Analytical");
		contentTypes.put("Court Rules", "Court Rules");
		contentTypes.put("Slice Codes", "Slice Codes");
		return contentTypes;
	}

	public void removeEmptyRows() {
		//Clear out empty author
        synchronized(this.authorInfo) {
            for (Iterator<Author> i = this.authorInfo.iterator(); i.hasNext();) {
            	Author author = i.next();
                if (author == null || author.isNameEmpty()) {
                    i.remove();
                }
            }
        }
        
        //Clear out empty name line
        synchronized(this.nameLines) {
            for (Iterator<EbookName> i = this.nameLines.iterator(); i.hasNext();) {
            	EbookName nameLine = i.next();
                if (nameLine == null || nameLine.isEmpty()) {
                    i.remove();
                }
            }
        }
        
        //Clear out empty additional front matter
        synchronized(this.additionalFrontMatter) {
            for (Iterator<AdditionalFrontMatter> i = this.additionalFrontMatter.iterator(); i.hasNext();) {
            	AdditionalFrontMatter frontMatter = i.next();
                if (frontMatter == null || frontMatter.isEmpty()) {
                    i.remove();
                }
            }
        }
    }
	
	// STUB Drop down menu on Create/Edit Book Definition
	// TODO: connect with model
	public static Map<String, String> getStates() {
		Map<String,String> states = new LinkedHashMap<String,String>();
		states.put("al", "Alabama");
		states.put("ak", "Alaska");
		states.put("az", "Arizona");
		states.put("ar", "Arkansas");
		states.put("ca", "California");
		states.put("co", "Colorado");
		states.put("ct", "Connecticut");
		states.put("de", "Delaware");
		states.put("fl", "Florida");
		states.put("ga", "Georgia");
		states.put("hi", "Hawaii");
		states.put("id", "Idaho");
		states.put("il", "Illinois");
		states.put("in", "Indiana");
		states.put("ia", "Iowa");
		states.put("mn", "Minnesota");
		states.put("wi", "Wisconsin");
		return states;
	}
	
	public static Map<String, String> getJurisdictions() {
		Map<String,String> jurisdictions = new LinkedHashMap<String,String>();
		jurisdictions.put("us", "Federal");
		jurisdictions.put("al", "Alabama");
		jurisdictions.put("ak", "Alaska");
		jurisdictions.put("az", "Arizona");
		jurisdictions.put("ar", "Arkansas");
		jurisdictions.put("ca", "California");
		jurisdictions.put("co", "Colorado");
		jurisdictions.put("ct", "Connecticut");
		jurisdictions.put("de", "Delaware");
		jurisdictions.put("fl", "Florida");
		jurisdictions.put("ga", "Georgia");
		jurisdictions.put("hi", "Hawaii");
		jurisdictions.put("id", "Idaho");
		jurisdictions.put("il", "Illinois");
		jurisdictions.put("in", "Indiana");
		jurisdictions.put("ia", "Iowa");
		jurisdictions.put("mn", "Minnesota");
		jurisdictions.put("wi", "Wisconsin");
		return jurisdictions;
	}
	
	public static Map<String, String> getPubTypes() {
		Map<String,String> pubTypes = new LinkedHashMap<String,String>();
		pubTypes.put("local", "Local");
		pubTypes.put("state", "State");
		pubTypes.put("fed", "Fed");
		pubTypes.put("fedrule", "FedRule");
		pubTypes.put("feddist", "FedDist");
		pubTypes.put("bankr", "Bankr");
		return pubTypes;
	}
	
	public static Map<String, String> getTypeKeywords() {
		Map<String,String> typeKeywords = new LinkedHashMap<String,String>();
		typeKeywords.put("A", "A");
		typeKeywords.put("B", "B");
		typeKeywords.put("C", "C");
		typeKeywords.put("D", "D");
		return typeKeywords;
	}
	public static Map<String, String> getSubjectKeywords() {
		Map<String,String> subjectKeywords = new LinkedHashMap<String,String>();
		subjectKeywords.put("AA", "AA");
		subjectKeywords.put("BB", "BB");
		subjectKeywords.put("CC", "CC");
		subjectKeywords.put("DD", "DD");
		return subjectKeywords;
	}
	public static Map<String, String> getPublisherKeywords() {
		Map<String,String> publisherKeywords = new LinkedHashMap<String,String>();
		publisherKeywords.put("Thomson Reuters Westlaw", "Thomson Reuters Westlaw");
		publisherKeywords.put("BBB", "BBB");
		publisherKeywords.put("CCC", "CCC");
		publisherKeywords.put("DDD", "DDD");
		return publisherKeywords;
	}
	public static Map<String, String> getJurisdictionKeywords() {
		Map<String,String> jurisdictionKeywords = new LinkedHashMap<String,String>();
		jurisdictionKeywords.put("1", "1");
		jurisdictionKeywords.put("2", "2");
		jurisdictionKeywords.put("3", "3");
		jurisdictionKeywords.put("4", "4");
		return jurisdictionKeywords;
	}
	
	public static Map<String, String> getPublishers() {
		Map<String,String> jurisdictionKeywords = new LinkedHashMap<String,String>();
		jurisdictionKeywords.put("uscl", "US Core Legal");
		return jurisdictionKeywords;
	}
}
