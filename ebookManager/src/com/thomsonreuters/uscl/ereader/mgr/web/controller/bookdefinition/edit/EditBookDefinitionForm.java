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

	public EditBookDefinitionForm() {
		super();
	}
	
	public void initialize(BookDefinition bookDefinition) {
		this.titleId = bookDefinition.getPrimaryKey().getFullyQualifiedTitleId();
		this.bookName = bookDefinition.getBookName();
		this.majorVersion = bookDefinition.getMajorVersion();
		this.minorVersion = bookDefinition.getMinorVersion();
		
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
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
