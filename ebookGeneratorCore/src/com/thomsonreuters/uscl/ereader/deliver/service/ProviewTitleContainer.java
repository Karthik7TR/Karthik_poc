/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.ArrayList;
import java.util.List;

public class ProviewTitleContainer {
	private static final String PROVIEW_STATUS_FINAL = "final";
	List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<ProviewTitleInfo>();

	public List<ProviewTitleInfo> getProviewTitleInfos() {
		return proviewTitleInfos;
	}

	public void setProviewTitleInfos(List<ProviewTitleInfo> proviewTitleInfos) {
		this.proviewTitleInfos = proviewTitleInfos;
	}

	/**
	 * 
	 * @return the latest version of the title
	 */
	public ProviewTitleInfo getLatestVersion() {

		Double latestVersion = 0.0;
		ProviewTitleInfo latestProviewTitleInfo = null;

		for (ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {
			Double proviewTitleInfoVersionDouble = Double
					.parseDouble(proviewTitleInfo.getVersion().substring(1));
			if (proviewTitleInfoVersionDouble >= latestVersion) {
				latestVersion = proviewTitleInfoVersionDouble;
				latestProviewTitleInfo = proviewTitleInfo;

			}
		}
		return latestProviewTitleInfo;
	}
	
	/**
	 * Determine if this title has been published to public
	 * @return boolean
	 */
	public boolean hasBeenPublished() {
		
		boolean isPublished = false;
		
		for (ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {
			if (proviewTitleInfo.getStatus().equalsIgnoreCase(PROVIEW_STATUS_FINAL)) {
				isPublished = true;
				break;
			}
		}
		return isPublished;
	}
}
