package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ProviewGroup  implements Serializable, Comparable<ProviewGroup> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229230493652304110L;
	private String proviewName;	
	private String groupName;
	private String groupVersion;
	private String groupId;
	private String groupIdByVersion;
	private Integer totalNumberOfVersions;
	private String headTitle;
	//subgroup information parsed from proview xml
	private List<SubgroupInfo> subgroupInfoList;
	//For third screen
	private List<GroupDetails> groupDetailList;
	private String groupStatus;
	
	public Integer getTotalNumberOfVersions() {
		return totalNumberOfVersions;
	}

	public void setTotalNumberOfVersions(Integer totalNumberOfVersions) {
		this.totalNumberOfVersions = totalNumberOfVersions;
	}
	
	public String getGroupIdByVersion() {
		return groupIdByVersion;
	}

	public void setGroupIdByVersion(String groupIdByVersion) {
		this.groupIdByVersion = groupIdByVersion;
	}
	
	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public List<GroupDetails> getGroupDetailList() {
		return groupDetailList;
	}

	public void setGroupDetailList(List<GroupDetails> bookInfoList) {
		this.groupDetailList = bookInfoList;
	}

	public List<SubgroupInfo> getSubgroupInfoList() {
		return subgroupInfoList;
	}

	public void setSubgroupInfoList(List<SubgroupInfo> subgroupInfoList) {
		this.subgroupInfoList = subgroupInfoList;
	}

	public String getHeadTitle() {
		return headTitle;
	}

	public void setHeadTitle(String headTitle) {
		this.headTitle = headTitle;
	}

	// Begin These fields can be deleted 
	private String titleId;
	private String bookVersion;
	private String bookDefId;
	public String getBookDefId() {
		return bookDefId;
	}

	public void setBookDefId(String bookDefId) {
		this.bookDefId = bookDefId;
	}
	
	public String getBookVersion() {
		return bookVersion;
	}

	public void setBookVersion(String bookVersion) {
		this.bookVersion = bookVersion;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}
	// End These fields can be deleted 

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupVersion() {
		return groupVersion;
	}

	public void setGroupVersion(String groupVersion) {
		this.groupVersion = groupVersion;
	}
	
	public Integer getVersion() {
		Integer majorVersion = null;
		String number = StringUtils.substringAfter(this.groupVersion, "v");
		try {
			if(StringUtils.isNotBlank(number)) {
				majorVersion = Integer.valueOf(number);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return majorVersion;
	}
	
	/*public Integer getMinorVersion() {
		Integer minorVersion = null;
		String number = StringUtils.substringAfter(this.groupVersion, ".");
		try {
			if(StringUtils.isNotBlank(number)) {
				minorVersion = Integer.valueOf(number);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return minorVersion;
	}
	*/
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}	

	public String getProviewName() {
		return proviewName;
	}

	public void setProviewName(String proviewName) {
		this.proviewName = proviewName;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((proviewName == null) ? 0 : proviewName.hashCode());
		result = prime * result + ((groupVersion == null) ? 0 : groupVersion.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((groupStatus == null) ? 0 : groupStatus.hashCode());
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
		ProviewGroup other = (ProviewGroup) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (proviewName == null) {
			if (other.proviewName != null)
				return false;
		} else if (!proviewName.equals(proviewName))
			return false;
		if (groupStatus == null) {
			if (other.groupStatus != null)
				return false;
		} else if (!groupStatus.equals(other.groupStatus))
			return false;
		if (groupVersion == null) {
			if (other.groupVersion != null)
				return false;
		} else if (!groupVersion.equals(other.groupVersion))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProviewGroup [ groupId=" + groupId + "groupStatus="+ groupStatus
				+ ", groupName=" + groupName + ", groupVersion=" + groupVersion + ",proviewTitle=" + proviewName + "]";
	}
	
	@Override
	public int compareTo(ProviewGroup info) {
		int version  = info.getVersion().compareTo(this.getVersion());
		
		if(version == 0) {
			return this.getTitleId().compareToIgnoreCase(info.getTitleId());
		} 
		return version;
	}
	
	public static class SubgroupInfo{
		private List<String> titleIdList;
		private String subGroupName;
		
		public List<String> getTitleIdList() {
			return titleIdList;
		}
		public void setTitleIdList(List<String> titleIdList) {
			this.titleIdList = titleIdList;
		}
		public String getSubGroupName() {
			return subGroupName;
		}
		public void setSubGroupName(String subGroupName) {
			this.subGroupName = subGroupName;
		}
	}
		
	public static class GroupDetails implements Serializable, Comparable<GroupDetails>{
		
		private static final long serialVersionUID = -4229230493652304110L;
		private List<ProviewTitleInfo> titleInfoList;
		private String bookStatus;
		private String subGroupName;
		private String bookVersion;
		private String id;
		private String proviewDisplayName;
		
		//These are for titles with no subgroups
		private String[] titleIdWithVersionArray;
		private String titleId;
		

		public String getTitleId() {
			return titleId;
		}

		public void setTitleId(String titleId) {
			this.titleId = titleId;
		}

		public String[] getTitleIdWithVersionArray() {
			return titleIdWithVersionArray;
		}

		public void setTitleIdtWithVersionArray(String[] titleIdWithVersionArray) {
			this.titleIdWithVersionArray = titleIdWithVersionArray;
		}
		
		public List<String> getTitleIdList() {
			List<String> titleIdList = new ArrayList<String>();
			for (ProviewTitleInfo title : titleInfoList) {
				titleIdList.add(title.getTitleId());
			}
			return titleIdList;
		}
		
		public List<String> getTitleIdListWithVersion() {
			List<String> titleIdListWithVersion = new ArrayList<String>();
			for (ProviewTitleInfo title : titleInfoList) {
				titleIdListWithVersion.add(title.getTitleId()+"/"+title.getVersion());
			}
			return titleIdListWithVersion;
		}

		public String getProviewDisplayName() {
			return proviewDisplayName;
		}

		public void setProviewDisplayName(String proviewDisplayName) {
			this.proviewDisplayName = proviewDisplayName;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		public String getBookVersion() {
			return bookVersion;
		}
		public void setBookVersion(String bookVersion) {
			this.bookVersion = bookVersion;
		}
		
		public Integer getMajorVersion() {
			Integer majorVersion = null;
			String version = StringUtils.substringAfter(bookVersion, "v");
			String number = StringUtils.substringBefore(version, ".");
			try {
				if(StringUtils.isNotBlank(number)) {
					majorVersion = Integer.valueOf(number);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			return majorVersion;
		}
		
		public String getSubGroupName() {
			return subGroupName;
		}
		public void setSubGroupName(String subGroupName) {
			this.subGroupName = subGroupName;
		}
		public String getBookStatus() {
			return bookStatus;
		}
		public void setBookStatus(String bookStatus) {
			this.bookStatus = bookStatus;
		}
		public List<ProviewTitleInfo> getTitleInfoList() {
			return titleInfoList;
		}
		public void setTitleIdList(List<ProviewTitleInfo> titleInfoList) {
			this.titleInfoList = titleInfoList;
			for (ProviewTitleInfo titleInfo : titleInfoList) {
				if (updateStatus(titleInfo)) {
					bookStatus = titleInfo.getStatus();
				}
			}
		}
		public void addTitleInfo(ProviewTitleInfo titleInfo) {
			if (titleInfoList == null) {
				titleInfoList = new ArrayList<ProviewTitleInfo>();
			}
			if(updateStatus(titleInfo)) {
				bookStatus = titleInfo.getStatus();
			}
			titleInfoList.add(titleInfo);
		}
		
		private boolean updateStatus(ProviewTitleInfo titleInfo) {
			if ("review".equalsIgnoreCase(bookStatus)) {
				return false;
			}
			if ("review".equalsIgnoreCase(titleInfo.getStatus())) {
				return true;
			}
			return false;
		}

		@Override
		public int compareTo(GroupDetails info) {
			int version  = info.getBookVersion().compareTo(this.getBookVersion());
			
			if(version == 0) {
				return info.getTitleId().compareToIgnoreCase(this.getTitleId());
			} 
			return version;
		}
		
	}

}

