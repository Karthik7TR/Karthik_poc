package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.List;

public class ProviewGroup  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229230493652304110L;
	private String proviewName;	
	private String groupName;	
	private String groupVersion;
	private String groupId;	
	private String groupIdByVersion;
	//For second screen
	private List<GroupDetails> groupDetailList;
	private String groupStatus;
	
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
	
		
	public static class GroupDetails implements Serializable{
		
		private static final long serialVersionUID = -4229230493652304110L;
		private String bookStatus;
		private List<String> titleIdList; 
		private String subGroupName;
		private List<String> titleIdListWithVersion; 
		public List<String> getTitleIdListWithVersion() {
			return titleIdListWithVersion;
		}

		public void setTitleIdListWithVersion(List<String> titleIdListWithVersion) {
			this.titleIdListWithVersion = titleIdListWithVersion;
		}
		private String bookVersion;
		private String id;
		private String proviewDisplayName;

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
		public List<String> getTitleIdList() {
			return titleIdList;
		}
		public void setTitleIdList(List<String> titleList) {
			this.titleIdList = titleList;
		}
		
	}

}

