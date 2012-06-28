/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

public class PublishingStatsForm {
	public static final String FORM_NAME = "publishingStatsForm";
	private String objectsPerPage;

	public String getObjectsPerPage() {
		return objectsPerPage;
	}

	public void setObjectsPerPage(String objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
	}

}
