package com.thomsonreuters.uscl.ereader;

import java.util.List;

public class GroupDefinition {
	
	

	private String groupId;	
	private String name;
	private String type;
	private String order;
	private String headTitle;
	private String groupVersion;
	
	private List<SubGroupInfo> subGroupInfoList;
	
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
	
	public String getGroupVersion() {
		return groupVersion;
	}

	public void setGroupVersion(String groupVersion) {
		this.groupVersion = groupVersion;
	}

	
	public static class SubGroupInfo {
		private String heading;
		private List<String> titles;
		

		public String getHeading() {
			return heading;
		}
		public void setHeading(String heading) {
			this.heading = heading;
		}
		public List<String> getTitles() {
			return titles;
		}
		public void setTitles(List<String> title) {
			this.titles = title;
		}
		
	}
}
