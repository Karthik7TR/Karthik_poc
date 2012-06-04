/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ProviewListFilterForm {

	private String title;
	private String titleId;
	private String minVersions;
	private String maxVersions;
	private Integer minVersionsInt;
	private Integer maxVersionsInt;

	public String getMinVersions() {
		return minVersions;
	}

	public void setMinVersions(String minVersions) {
		this.minVersions = minVersions == null ? null : minVersions.trim();
		try {
			minVersionsInt = Integer.parseInt(minVersions);
		} catch (NumberFormatException e) {
			minVersions = null;
			minVersionsInt = 0;
		}
	}

	public String getMaxVersions() {
		return maxVersions;
	}

	public void setMaxVersions(String maxVersions) {
		this.maxVersions = maxVersions == null ? null : maxVersions.trim();
		try {
			maxVersionsInt = Integer.parseInt(maxVersions);
		} catch (NumberFormatException e) {
			maxVersions = null;
			maxVersionsInt = 99999;
		}

	}

	public static final String FORM_NAME = "proviewListFilterForm";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId == null ? null : titleId.trim();
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
