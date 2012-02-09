/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class BookLibraryFilterForm {

	public static final String FORM_NAME = "bookLibraryFilterForm";

	private String name;
	private String from;
	private String to;
	private String userName;
	private String eBookDefStatus;
	private String publishingSttaus;
	private String titleId;
	private String isbn;
	private String authorName;
	private String materialNumber;
	private String publisher;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String getFormName() {
		return FORM_NAME;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String geteBookDefStatus() {
		return eBookDefStatus;
	}

	public void seteBookDefStatus(String eBookDefStatus) {
		this.eBookDefStatus = eBookDefStatus;
	}

	public String getPublishingSttaus() {
		return publishingSttaus;
	}

	public void setPublishingSttaus(String publishingSttaus) {
		this.publishingSttaus = publishingSttaus;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
