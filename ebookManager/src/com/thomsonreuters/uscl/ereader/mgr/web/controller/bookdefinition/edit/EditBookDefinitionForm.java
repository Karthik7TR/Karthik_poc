package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

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
	private String docCollectionName;
	private String tocCollectionName;
	private String nortDomain;
	private String nortFilterView;
	private String contentType;
	private String contentSubtype;
	private String coverImage;
	private String isbn;
	private String materialIdEmbeddedInDocText;

	public EditBookDefinitionForm() {
		super();
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
		this.docCollectionName = bookDefinition.getDocCollectionName();
		this.tocCollectionName = bookDefinition.getTocCollectionName();
		this.nortDomain = bookDefinition.getNortDomain();
		this.nortFilterView = bookDefinition.getNortFilterView();
		this.contentType = bookDefinition.getContentType();
		this.contentSubtype = bookDefinition.getContentSubtype();
		this.coverImage = bookDefinition.getCoverImage();
		this.isbn = bookDefinition.getIsbn();
		this.materialIdEmbeddedInDocText = bookDefinition.getMaterialIdEmbeddedInDocText();
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
	
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	
	public long getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
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

	public String getDocCollectionName() {
		return docCollectionName;
	}

	public void setDocCollectionName(String docCollectionName) {
		this.docCollectionName = docCollectionName;
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

	public String getMaterialIdEmbeddedInDocText() {
		return materialIdEmbeddedInDocText;
	}

	public void setMaterialIdEmbeddedInDocText(String materialIdEmbeddedInDocText) {
		this.materialIdEmbeddedInDocText = materialIdEmbeddedInDocText;
	}

	public void setMajorVersion(long majorVersion) {
		this.majorVersion = majorVersion;
	}

	public void setMinorVersion(long minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
