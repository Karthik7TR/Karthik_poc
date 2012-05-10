/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProviewTitleContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1985883914988566602L;
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

		Integer latestIntMajorPart = 0;
		Integer latestIntMinorPart = 0;
		ProviewTitleInfo latestProviewTitleInfo = null;

		for (ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {

			String currentVersion = proviewTitleInfo.getVersion().substring(1);
			String majorPart;
			String minorPart;
			Integer intMajorPart = 0;
			Integer intMinorPart = 0;

			if (currentVersion.contains(".")) {
				majorPart = currentVersion.substring(0,
						currentVersion.indexOf("."));
				minorPart = currentVersion.substring(currentVersion
						.indexOf(".") + 1);

				intMajorPart = Integer.parseInt(majorPart);
				intMinorPart = Integer.parseInt(minorPart);

			} else {
				majorPart = currentVersion;
				intMajorPart = Integer.parseInt(majorPart);
			}

			if (intMajorPart > latestIntMajorPart) {
				latestProviewTitleInfo = proviewTitleInfo;
				latestIntMajorPart = intMajorPart;
				latestIntMinorPart = intMinorPart;
			} else if (intMajorPart == latestIntMajorPart) {
				if (intMinorPart >= latestIntMinorPart) {
					latestProviewTitleInfo = proviewTitleInfo;
					latestIntMajorPart = intMajorPart;
					latestIntMinorPart = intMinorPart;
				}
			}

		}
		return latestProviewTitleInfo;
	}

	/**
	 * Determine if this title has been published to public
	 * 
	 * @return boolean
	 */
	public boolean hasBeenPublished() {

		boolean isPublished = false;

		for (ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {
			if (proviewTitleInfo.getStatus().equalsIgnoreCase(
					PROVIEW_STATUS_FINAL)) {
				isPublished = true;
				break;
			}
		}
		return isPublished;
	}
}
