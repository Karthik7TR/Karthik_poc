package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GroupDefinition implements Comparable<GroupDefinition>
{
    public static final String VERSION_NUMBER_PREFIX = "v";

    public static final String REVIEW_STATUS = "Review";

    private String groupId;
    private String name;
    private String type;
    private String status;
    private String order;
    private String headTitle;
    private Long groupVersion;

    private List<SubGroupInfo> subGroupInfoList = new ArrayList<>();

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(final String groupId)
    {
        this.groupId = groupId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder(final String order)
    {
        this.order = order;
    }

    public String getHeadTitle()
    {
        return headTitle;
    }

    public void setHeadTitle(final String headTitle)
    {
        this.headTitle = headTitle;
    }

    public String getFirstSubgroupHeading()
    {
        String subgroupHeading = null;
        if (subGroupInfoList != null && subGroupInfoList.size() > 0)
        {
            final SubGroupInfo subGroupInfo = subGroupInfoList.get(0);
            subgroupHeading = subGroupInfo.getHeading();
        }
        return subgroupHeading;
    }

    public List<SubGroupInfo> getSubGroupInfoList()
    {
        return subGroupInfoList;
    }

    public void setSubGroupInfoList(final List<SubGroupInfo> subGroupInfoList)
    {
        this.subGroupInfoList = subGroupInfoList;
    }

    public void addSubGroupInfo(final SubGroupInfo subGroupInfo)
    {
        subGroupInfoList.add(subGroupInfo);
    }

    public Long getGroupVersion()
    {
        return groupVersion;
    }

    public void setGroupVersion(final Long groupVersion)
    {
        this.groupVersion = groupVersion;
    }

    public String getProviewGroupVersionString()
    {
        return VERSION_NUMBER_PREFIX + String.valueOf(groupVersion);
    }

    public void setProviewGroupVersionString(final String version)
    {
        final String numberStr = StringUtils.substringAfterLast(version, "v");
        groupVersion = Long.valueOf(numberStr);
    }

    public Boolean subgroupExists()
    {
        boolean subgroupExists = false;
        for (final SubGroupInfo subGroupInfo : subGroupInfoList)
        {
            final String subHeading = subGroupInfo.getHeading();
            if (StringUtils.isNotBlank(subHeading))
            {
                subgroupExists = true;
                break;
            }
        }

        return subgroupExists;
    }

    public Boolean isSimilarGroup(final GroupDefinition previousGroup)
    {
        if (this == previousGroup)
        {
            return true;
        }
        else if (previousGroup == null)
        {
            return false;
        }

        if (!StringUtils.equals(name, previousGroup.getName()))
        {
            return false;
        }
        else if (!StringUtils.equalsIgnoreCase(headTitle, previousGroup.getHeadTitle()))
        {
            return false;
        }

        if (subGroupInfoList.size() != previousGroup.getSubGroupInfoList().size())
        {
            return false;
        }

        for (int i = 0; i < subGroupInfoList.size(); i++)
        {
            final SubGroupInfo currentSubgroup = subGroupInfoList.get(i);
            final SubGroupInfo previousSubgroup = previousGroup.getSubGroupInfoList().get(i);
            if (!currentSubgroup.equals(previousSubgroup))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GroupDefinition that = (GroupDefinition) obj;
        if (groupId != null)
        {
            if (!groupId.equals(that.groupId))
                return false;
        }
        else if (that.groupId != null)
            return false;

        if (groupVersion != null)
        {
            if (!groupVersion.equals(that.groupVersion))
                return false;
        }
        else if (that.groupVersion != null)
            return false;

        if (name != null)
        {
            if (!name.equals(that.name))
                return false;
        }
        else if (that.name != null)
            return false;

        if (type != null)
        {
            if (!type.equals(that.type))
                return false;
        }
        else if (that.type != null)
            return false;

        if (status != null)
        {
            if (!status.equals(that.status))
                return false;
        }
        else if (that.status != null)
            return false;

        if (order != null)
        {
            if (!order.equals(that.order))
                return false;
        }
        else if (that.order != null)
            return false;

        if (headTitle != null)
        {
            if (!headTitle.equals(that.headTitle))
                return false;
        }
        else if (that.headTitle != null)
            return false;

        if (subGroupInfoList != null)
        {
            if (!subGroupInfoList.equals(that.subGroupInfoList))
                return false;
        }
        else if (that.subGroupInfoList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(groupId)
                .append(groupVersion)
                .append(name)
                .append(type)
                .append(status)
                .append(order)
                .append(headTitle)
                .append(subGroupInfoList)
                .toHashCode();
    }

    @Override
    public int compareTo(final GroupDefinition o)
    {
        return o.getGroupVersion().compareTo(getGroupVersion());
    }

    public static class SubGroupInfo
    {
        private String heading;
        private List<String> titles = new ArrayList<>();

        public String getHeading()
        {
            return heading;
        }

        public void setHeading(final String heading)
        {
            this.heading = heading;
        }

        public List<String> getTitles()
        {
            return titles;
        }

        public void addTitle(final String title)
        {
            titles.add(title);
        }

        public void setTitles(final List<String> titles)
        {
            this.titles = titles;
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(17, 37)
                .append(heading)
                .append(titles)
                .toHashCode();
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final SubGroupInfo other = (SubGroupInfo) obj;
            if (heading == null)
            {
                if (other.heading != null)
                    return false;
            }
            else if (!heading.equals(other.heading))
                return false;
            if (titles == null)
            {
                if (other.titles != null)
                    return false;
            }
            else if (!titles.equals(other.titles))
                return false;
            return true;
        }
    }
}
