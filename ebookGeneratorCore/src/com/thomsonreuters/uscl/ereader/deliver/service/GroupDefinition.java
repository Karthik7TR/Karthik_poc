/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class GroupDefinition implements Comparable<GroupDefinition> {
	public static final String VERSION_NUMBER_PREFIX = "v";
	
	private String groupId;	
	private String name;
	private String type;
	private String status;
	private String order;
	private String headTitle;
	private Long groupVersion;
	
	private List<SubGroupInfo> subGroupInfoList = new ArrayList<>();
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getHeadTitle() {
		return headTitle;
	}

	public void setHeadTitle(String headTitle) {
		this.headTitle = headTitle;
	}
	
	public String getFirstSubgroupHeading() {
		String subgroupHeading = null;
		if(subGroupInfoList != null && subGroupInfoList.size() > 0) {
			SubGroupInfo subGroupInfo = subGroupInfoList.get(0);
			subgroupHeading = subGroupInfo.getHeading();
		}
		return subgroupHeading;
	}

	public List<SubGroupInfo> getSubGroupInfoList() {
		return subGroupInfoList;
	}

	public void setSubGroupInfoList(List<SubGroupInfo> subGroupInfoList) {
		this.subGroupInfoList = subGroupInfoList;
	}
	
	public void addSubGroupInfo(SubGroupInfo subGroupInfo) {
		this.subGroupInfoList.add(subGroupInfo);
	}
	
	public Long getGroupVersion() {
		return groupVersion;
	}

	public void setGroupVersion(Long groupVersion) {
		this.groupVersion = groupVersion;
	}
	
	public String getProviewGroupVersionString() {
		return VERSION_NUMBER_PREFIX + String.valueOf(groupVersion);
	}

	public void setProviewGroupVersionString(String version) {
		String numberStr = StringUtils.substringAfterLast(version, "v");
		this.groupVersion = Long.valueOf(numberStr);
	}
	
	public Boolean subgroupExists() {
		boolean subgroupExists = false;
		for(SubGroupInfo subGroupInfo : this.subGroupInfoList) {
			String subHeading = subGroupInfo.getHeading();
			if(StringUtils.isNotBlank(subHeading)) {
				subgroupExists = true;
				break;
			}
		}
		
		return subgroupExists;
	}
	
	public Boolean isSimilarGroup(GroupDefinition previousGroup) {
		if (this == previousGroup) {
			return true;
		} else if (previousGroup == null) {
			return false;
		}
		
		if(!StringUtils.equals(name, previousGroup.getName())) {
			return false;
		} else if(!StringUtils.equalsIgnoreCase(headTitle, previousGroup.getHeadTitle())) {
			return false;
		}
		
		if(subGroupInfoList.size() != previousGroup.getSubGroupInfoList().size()) {
			return false;
		}
		
		for(int i = 0; i < subGroupInfoList.size(); i++) {
			SubGroupInfo currentSubgroup = subGroupInfoList.get(i);
			SubGroupInfo previousSubgroup = previousGroup.getSubGroupInfoList().get(i);
			if(!currentSubgroup.equals(previousSubgroup)) {
				return false;
			}
		}
		return true;
	}
	
	public static class SubGroupInfo {
		private String heading;
		private List<String> titles = new ArrayList<String>();
		

		public String getHeading() {
			return heading;
		}
		public void setHeading(String heading) {
			this.heading = heading;
		}
		public List<String> getTitles() {
			return titles;
		}
		public void addTitle(String title) {
			this.titles.add(title);
		}
		public void setTitles(List<String> titles) {
			this.titles = titles;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((heading == null) ? 0 : heading.hashCode());
			result = prime * result
					+ ((titles == null) ? 0 : titles.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubGroupInfo other = (SubGroupInfo) obj;
			if (heading == null) {
				if (other.heading != null)
					return false;
			} else if (!heading.equals(other.heading))
				return false;
			if (titles == null) {
				if (other.titles != null)
					return false;
			} else if (!titles.equals(other.titles))
				return false;
			return true;
		}
		
	}

	@Override
	public int compareTo(GroupDefinition o) {
		return o.getGroupVersion().compareTo(this.getGroupVersion());
	}
}
