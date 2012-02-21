package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.LinkedHashMap;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class EditBookDefinitionForm {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	public static final String FORM_NAME = "editBookDefinitionForm";
	private static final int PUBLISHER_INDEX = 0;
	//private static final int contentTypeIndex = 1;
	private static final int TITLE_NAME_INDEX = 2;
	
	private String titleId;
	private String bookName;
	private long majorVersion;
	private long minorVersion;
	private String copyright;
	private String materialId;
	private String authorInfo;
	private String rootTocGuid;
	private String tocCollectionName;
	private String nortDomain;
	private String nortFilterView;
	private String contentType;
	private String contentSubtype;
	private String coverImage;
	private String isbn;
	private boolean materialIdEmbeddedInDocText;
	
	// Keywords used in Proview
	private String[] typeKeyword;
	private String[] subjectKeyword;
	private String[] publisherKeyword;
	private String[] jurisdictionKeyword;
	
	private boolean autoUpdateSupport;
	private boolean searchIndex;
	private boolean onePassSSOLinking;
	private String language;
	private boolean imageView;
	private String imageCollectionInformation;
	private String nameSpacePubId;
	private String currency;
	private boolean isComplete;
	
	// Fully qualified title ID parts
	private String publisher;
	private String state;
	private String pubType;
	private String pubAbbr;
	private String jurisdiction;
	private String pubInfo;
	
	public EditBookDefinitionForm() {
		super();
		
		this.autoUpdateSupport = true;
		this.searchIndex = true;
		this.onePassSSOLinking = true;
	}
	
	public void initialize(BookDefinition bookDefinition) {
		this.titleId = bookDefinition.getPrimaryKey().getFullyQualifiedTitleId();
		this.bookName = bookDefinition.getBookName();
		this.majorVersion = bookDefinition.getMajorVersion();
		this.minorVersion = bookDefinition.getMinorVersion();
		this.copyright = bookDefinition.getCopyright();
		this.materialId = bookDefinition.getMaterialId();
		this.authorInfo = bookDefinition.getAuthorInfo();
		this.rootTocGuid = bookDefinition.getRootTocGuid();
		this.tocCollectionName = bookDefinition.getTocCollectionName();
		this.nortDomain = bookDefinition.getNortDomain();
		this.nortFilterView = bookDefinition.getNortFilterView();
		this.contentType = bookDefinition.getContentType();
		this.contentSubtype = bookDefinition.getContentSubtype();
		this.coverImage = bookDefinition.getCoverImage();
		this.isbn = bookDefinition.getIsbn();
		
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
	
	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public long getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(long majorVersion) {
		this.majorVersion = majorVersion;
	}

	public long getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(long minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(String authorInfo) {
		this.authorInfo = authorInfo;
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

	public String getContentSubtype() {
		return contentSubtype;
	}

	public void setContentSubtype(String contentSubtype) {
		this.contentSubtype = contentSubtype;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean isMaterialIdEmbeddedInDocText() {
		return materialIdEmbeddedInDocText;
	}

	public void setMaterialIdEmbeddedInDocText(boolean materialIdEmbeddedInDocText) {
		this.materialIdEmbeddedInDocText = materialIdEmbeddedInDocText;
	}

	public String[] getTypeKeyword() {
		return typeKeyword;
	}

	public void setTypeKeyword(String[] typeKeyword) {
		this.typeKeyword = typeKeyword;
	}

	public String[] getSubjectKeyword() {
		return subjectKeyword;
	}

	public void setSubjectKeyword(String[] subjectKeyword) {
		this.subjectKeyword = subjectKeyword;
	}

	public String[] getPublisherKeyword() {
		return publisherKeyword;
	}

	public void setPublisherKeyword(String[] publisherKeyword) {
		this.publisherKeyword = publisherKeyword;
	}

	public String[] getJurisdictionKeyword() {
		return jurisdictionKeyword;
	}

	public void setJurisdictionKeyword(String[] jurisdictionKeyword) {
		this.jurisdictionKeyword = jurisdictionKeyword;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isImageView() {
		return imageView;
	}

	public void setImageView(boolean imageView) {
		this.imageView = imageView;
	}

	public String getImageCollectionInformation() {
		return imageCollectionInformation;
	}

	public void setImageCollectionInformation(String imageCollectionInformation) {
		this.imageCollectionInformation = imageCollectionInformation;
	}

	public String getNameSpacePubId() {
		return nameSpacePubId;
	}

	public void setNameSpacePubId(String nameSpacePubId) {
		this.nameSpacePubId = nameSpacePubId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
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

	
	// STUB Drop down menu on Create/Edit Book Definition
	// TODO: connect with model
	public static Map<String, String> getStates() {
		Map<String,String> states = new LinkedHashMap<String,String>();
		states.put("fl", "Florida");
		states.put("mn", "Minnesota");
		states.put("wi", "Wisconsin");
		return states;
	}
	
	public static Map<String, String> getJurisdictions() {
		Map<String,String> jurisdictions = new LinkedHashMap<String,String>();
		jurisdictions.put("us", "US");
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
		publisherKeywords.put("AAA", "AAA");
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
}
