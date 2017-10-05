package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ProviewGroup implements Serializable, Comparable<ProviewGroup> {
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

    public void setTotalNumberOfVersions(final Integer totalNumberOfVersions) {
        this.totalNumberOfVersions = totalNumberOfVersions;
    }

    public String getGroupIdByVersion() {
        return groupIdByVersion;
    }

    public void setGroupIdByVersion(final String groupIdByVersion) {
        this.groupIdByVersion = groupIdByVersion;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(final String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public List<GroupDetails> getGroupDetailList() {
        return groupDetailList;
    }

    public void setGroupDetailList(final List<GroupDetails> bookInfoList) {
        groupDetailList = bookInfoList;
    }

    public List<SubgroupInfo> getSubgroupInfoList() {
        return subgroupInfoList;
    }

    public void setSubgroupInfoList(final List<SubgroupInfo> subgroupInfoList) {
        this.subgroupInfoList = subgroupInfoList;
    }

    public String getHeadTitle() {
        return headTitle;
    }

    public void setHeadTitle(final String headTitle) {
        this.headTitle = headTitle;
    }

    // Begin These fields can be deleted
    private String titleId;
    private String bookVersion;
    private String bookDefId;

    public String getBookDefId() {
        return bookDefId;
    }

    public void setBookDefId(final String bookDefId) {
        this.bookDefId = bookDefId;
    }

    public String getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(final String bookVersion) {
        this.bookVersion = bookVersion;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(final String titleId) {
        this.titleId = titleId;
    }
    // End These fields can be deleted

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getGroupVersion() {
        return groupVersion;
    }

    public void setGroupVersion(final String groupVersion) {
        this.groupVersion = groupVersion;
    }

    public Integer getVersion() {
        Integer majorVersion = null;
        final String number = StringUtils.substringAfter(groupVersion, "v");
        try {
            if (StringUtils.isNotBlank(number)) {
                majorVersion = Integer.valueOf(number);
            }
        } catch (final Exception e) {
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

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public String getProviewName() {
        return proviewName;
    }

    public void setProviewName(final String proviewName) {
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
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProviewGroup other = (ProviewGroup) obj;
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
        return "ProviewGroup [ groupId="
            + groupId
            + "groupStatus="
            + groupStatus
            + ", groupName="
            + groupName
            + ", groupVersion="
            + groupVersion
            + ",proviewTitle="
            + proviewName
            + "]";
    }

    @Override
    public int compareTo(final ProviewGroup info) {
        final int version = info.getVersion().compareTo(getVersion());

        if (version == 0) {
            return getTitleId().compareToIgnoreCase(info.getTitleId());
        }
        return version;
    }

    public static class SubgroupInfo {
        private List<String> titleIdList;
        private String subGroupName;

        public List<String> getTitleIdList() {
            return titleIdList;
        }

        public void setTitleIdList(final List<String> titleIdList) {
            this.titleIdList = titleIdList;
        }

        public String getSubGroupName() {
            return subGroupName;
        }

        public void setSubGroupName(final String subGroupName) {
            this.subGroupName = subGroupName;
        }
    }

    public static class GroupDetails implements Serializable, Comparable<GroupDetails> {
        private static final long serialVersionUID = -4229230493652304110L;
        private List<ProviewTitleInfo> titleInfoList;
        private String bookStatus;
        private String subGroupName;
        private String bookVersion;
        private String lastupdate;
        private String id;
        private String proviewDisplayName;
        private boolean isPilotBook;

        //These are for titles with no subgroups
        private String[] titleIdWithVersionArray;
        private String titleId;

        public String getTitleId() {
            return titleId;
        }

        public void setTitleId(final String titleId) {
            this.titleId = titleId;
        }

        public String[] getTitleIdWithVersionArray() {
            return titleIdWithVersionArray;
        }

        public void setTitleIdtWithVersionArray(final String[] titleIdWithVersionArray) {
            this.titleIdWithVersionArray = titleIdWithVersionArray;
        }

        public List<String> getTitleIdList() {
            final List<String> titleIdList = new ArrayList<>();
            for (final ProviewTitleInfo title : titleInfoList) {
                titleIdList.add(title.getTitleId());
            }
            return titleIdList;
        }

        public List<String> getTitleIdListWithVersion() {
            final List<String> titleIdListWithVersion = new ArrayList<>();
            for (final ProviewTitleInfo title : titleInfoList) {
                titleIdListWithVersion.add(title.getTitleId() + "/" + title.getVersion());
            }
            return titleIdListWithVersion;
        }

        public String getProviewDisplayName() {
            return proviewDisplayName;
        }

        public void setProviewDisplayName(final String proviewDisplayName) {
            this.proviewDisplayName = proviewDisplayName;
        }

        public boolean isPilotBook() {
            return isPilotBook;
        }

        public void setPilotBook(final boolean isPilotBook) {
            this.isPilotBook = isPilotBook;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getBookVersion() {
            return bookVersion;
        }

        public void setBookVersion(final String bookVersion) {
            this.bookVersion = bookVersion;
        }

        public String getLastupdate() {
            return lastupdate;
        }

        public void setLastupdate(final String lastupdate) {
            this.lastupdate = lastupdate;
        }

        public String getIdWithVersion() {
            return id + "/" + bookVersion;
        }

        public Integer getMajorVersion() {
            Integer majorVersion = null;
            final String version = StringUtils.substringAfter(bookVersion, "v");
            final String number = StringUtils.substringBefore(version, ".");
            try {
                if (StringUtils.isNotBlank(number)) {
                    majorVersion = Integer.valueOf(number);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return majorVersion;
        }

        public String getSubGroupName() {
            return subGroupName;
        }

        public void setSubGroupName(final String subGroupName) {
            this.subGroupName = subGroupName;
        }

        public String getBookStatus() {
            return bookStatus;
        }

        public void setBookStatus(final String bookStatus) {
            this.bookStatus = bookStatus;
        }

        public List<ProviewTitleInfo> getTitleInfoList() {
            return titleInfoList;
        }

        public void setTitleIdList(final List<ProviewTitleInfo> titleInfoList) {
            this.titleInfoList = titleInfoList;
            for (final ProviewTitleInfo titleInfo : titleInfoList) {
                if (updateStatus(titleInfo)) {
                    bookStatus = titleInfo.getStatus();
                }
            }
        }

        public void addTitleInfo(final ProviewTitleInfo titleInfo) {
            if (titleInfoList == null) {
                titleInfoList = new ArrayList<>();
            }
            if (updateStatus(titleInfo)) {
                bookStatus = titleInfo.getStatus();
            }
            titleInfoList.add(titleInfo);
        }

        private boolean updateStatus(final ProviewTitleInfo titleInfo) {
            if ("review".equalsIgnoreCase(bookStatus)) {
                return false;
            }
            if ("review".equalsIgnoreCase(titleInfo.getStatus())) {
                return true;
            }
            return false;
        }

        @Override
        public int compareTo(final GroupDetails info) {
            int version = 0;
            if (info.isPilotBook() ^ isPilotBook()) {
                version = isPilotBook() ? 1 : -1;
            } else {
                version = -info.getProviewDisplayName().compareTo(getProviewDisplayName());
                if (version == 0) {
                    version = info.getBookVersion().compareTo(getBookVersion());
                    if (version == 0) {
                        final String infoId = info.getTitleId() == null ? info.getId() : info.getTitleId();
                        final String thisId = getTitleId() == null ? getId() : getTitleId();
                        version = infoId.compareToIgnoreCase(thisId);
                    }
                }
            }
            return version;
        }
    }
}
