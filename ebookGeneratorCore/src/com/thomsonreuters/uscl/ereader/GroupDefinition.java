package com.thomsonreuters.uscl.ereader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class GroupDefinition {
	public static final String VERSION_NUMBER_PREFIX = "v";
	
	private String groupId;	
	private String name;
	private String type;
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
		
	}
}
