/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

public class SupportForm {
	//private static final Logger log = Logger.getLogger(SupportForm.class);
	public static final String FORM_NAME = "supportForm";
	
	private Long id;
	private String linkDescription;
	private String linkAddress;
	
	public SupportForm() {
		super();
	}
	
	public void initialize(SupportPageLink spl) {
		this.id = spl.getId();
		this.linkDescription = spl.getLinkDescription();
		this.linkAddress = spl.getLinkAddress();
	}
	
	public SupportPageLink makeCode() {
		SupportPageLink spl = new SupportPageLink();
		spl.setId(id);
		spl.setLinkAddress(linkAddress);
		spl.setLinkDescription(linkDescription);
		
		return spl;
	}

	public Long getSupportPageLinkId() {
		return id;
	}

	public void setSupportPageLinkId(Long id) {
		this.id = id;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getLinkAddress() {
		return linkAddress;
	}

	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
	}

	
}
