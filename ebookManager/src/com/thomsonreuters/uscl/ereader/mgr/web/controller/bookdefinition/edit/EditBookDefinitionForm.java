package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class EditBookDefinitionForm {
	public static final String FORM_NAME = "editBookDefinitionForm";
	
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
	private String keywords;
	private String type;
	private String value;
	private boolean autoUpdateSupport;
	private boolean searchIndex;
	private boolean onePassSSOLinking;
	private String language;
	private boolean imageView;
	private String imageCollectionInformation;
	private String nameSpacePubId;
	private String currency;
	private boolean isComplete;
	private int year;
	private String state;
	private String pubType;
	
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
		
		//TODO: add initialization of other properties once in book definition model
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

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public Map<String, String> getContentTypes() {
		Map<String,String> contentTypes = new LinkedHashMap<String,String>();
		contentTypes.put("Analytical", "Analytical");
		contentTypes.put("Court Rules", "Court Rules");
		contentTypes.put("Random", "Random");
		return contentTypes;
	}
	
	public Map<String, String> getStates() {
		Map<String,String> states = new LinkedHashMap<String,String>();
		states.put("fl", "Florida");
		states.put("mn", "Minnesota");
		states.put("wi", "Wisconsin");
		return states;
	}
	
	public Map<String, String> getYears() {
		Map<String,String> years = new LinkedHashMap<String,String>();
		years.put("", "None");
		years.put("2010", "2010");
		years.put("2011", "2011");
		years.put("2012", "2012");
		return years;
	}
	
	public Map<String, String> getPubTypes() {
		Map<String,String> pubTypes = new LinkedHashMap<String,String>();
		pubTypes.put("abc", "Abc");
		pubTypes.put("def", "Def");
		return pubTypes;
	}
}
