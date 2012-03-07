/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.ArrayList;
import java.util.List;

public class ProviewTitleContainer {

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
					.parseDouble(proviewTitleInfo.getVesrion().substring(1));
			if (proviewTitleInfoVersionDouble >= latestVersion) {
				latestVersion = proviewTitleInfoVersionDouble;
				latestProviewTitleInfo = proviewTitleInfo;

			}
		}
		return latestProviewTitleInfo;
	}

}
