/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * handle a list of ProviewGroups
 * 
 * @author uc209819
 *
 */
public class ProviewGroupContainer implements Serializable {
	private static final long serialVersionUID = -1985883914988566602L;
	private static final String PROVIEW_STATUS_FINAL = "final";
	
	private List<ProviewGroup> proviewGroups = new ArrayList<ProviewGroup>();
	
	public List<ProviewGroup> getProviewGroups(){
		return proviewGroups;
	}
	
	public void setProviewGroups(List<ProviewGroup> proviewGroups){
		this.proviewGroups = proviewGroups;
	}

	public String getGroupId(){
		return proviewGroups.get(0).getGroupId();
	}
	
	/**
	 * 
	 * @return the latest version of the group
	 */
	public ProviewGroup getLatestVersion() {

		Integer latestVersion = 0;
		ProviewGroup latestProviewGroup = null;

		for (ProviewGroup proviewGroup : proviewGroups) {

			String currentVersion = proviewGroup.getGroupVersion().substring(1);
			Integer intVersion = 0;
			
			intVersion = Integer.parseInt(currentVersion);

			if (intVersion > latestVersion) {
				latestProviewGroup = proviewGroup;
				latestVersion = intVersion;
			}
		}
		return latestProviewGroup;
	}
	
	public List<ProviewGroup> getAllMajorVersions() {
		Map<Integer, ProviewGroup> map = new HashMap<>();
		
		for (ProviewGroup proviewGroup : proviewGroups) {
			Integer key = 0;
			if(!map.containsKey(key)) {
				map.put(key, proviewGroup);
			} else {
				ProviewGroup previousTitleInfo = map.get(key);
				if(proviewGroup.getVersion() > previousTitleInfo.getVersion()) {
					map.put(key, proviewGroup);
				}
			}
		}

		List<ProviewGroup> list = new ArrayList<>(map.values());
		Collections.sort(list);
		
		return list;
	}

	/**
	 * Determine if this group has been published to public
	 * 
	 * @return boolean
	 */
	public boolean hasBeenPublished() {

		boolean isPublished = false;

		for (ProviewGroup proviewGroup : proviewGroups) {
			if (proviewGroup.getGroupStatus().equalsIgnoreCase(
					PROVIEW_STATUS_FINAL)) {
				isPublished = true;
				break;
			}
		}
		return isPublished;
	}

	public ProviewGroup getGroupByVersion(String version) {
		for(ProviewGroup group : proviewGroups){
			if (version.equals(group.getVersion().toString())) {
				return group;
			}
		}
		return null;
	}
}