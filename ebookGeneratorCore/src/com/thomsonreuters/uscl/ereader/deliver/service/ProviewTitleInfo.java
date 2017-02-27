package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Placeholder for proview title info
 *
 * @author U0057241
 *
 */
public class ProviewTitleInfo implements TitleInfo, Serializable, Comparable<ProviewTitleInfo>
{
    /**
     *
     */
    private static final long serialVersionUID = -4229230493652304110L;
    private String titleId;
    private String version;

    private String publisher;
    private String lastupdate;
    private String status;
    private String title;
    private Integer totalNumberOfVersions;

    @Override
    public Integer getTotalNumberOfVersions()
    {
        return totalNumberOfVersions;
    }

    public void setTotalNumberOfVersions(final Integer totalNumberOfVersions)
    {
        this.totalNumberOfVersions = totalNumberOfVersions;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    public void setVersion(final String version)
    {
        this.version = version;
    }

    @Override
    public Integer getMajorVersion()
    {
        Integer majorVersion = null;
        final String versionStr = StringUtils.substringAfter(version, "v");
        final String majorVersionStr = StringUtils.substringBefore(versionStr, ".");
        try
        {
            if (StringUtils.isNotBlank(majorVersionStr))
            {
                majorVersion = Integer.valueOf(majorVersionStr);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return majorVersion;
    }

    @Override
    public Integer getMinorVersion()
    {
        Integer minorVersion = null;
        final String number = StringUtils.substringAfter(version, ".");
        try
        {
            if (StringUtils.isNotBlank(number))
            {
                minorVersion = Integer.valueOf(number);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return minorVersion;
    }

    @Override
    public String getTitleId()
    {
        return titleId;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = titleId;
    }

    @Override
    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(final String publisher)
    {
        this.publisher = publisher;
    }

    @Override
    public String getLastupdate()
    {
        return lastupdate;
    }

    public void setLastupdate(final String lastupdate)
    {
        this.lastupdate = lastupdate;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lastupdate == null) ? 0 : lastupdate.hashCode());
        result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((titleId == null) ? 0 : titleId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
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
        final ProviewTitleInfo other = (ProviewTitleInfo) obj;
        if (lastupdate == null)
        {
            if (other.lastupdate != null)
                return false;
        }
        else if (!lastupdate.equals(other.lastupdate))
            return false;
        if (publisher == null)
        {
            if (other.publisher != null)
                return false;
        }
        else if (!publisher.equals(other.publisher))
            return false;
        if (status == null)
        {
            if (other.status != null)
                return false;
        }
        else if (!status.equals(other.status))
            return false;
        if (title == null)
        {
            if (other.title != null)
                return false;
        }
        else if (!title.equals(other.title))
            return false;
        if (titleId == null)
        {
            if (other.titleId != null)
                return false;
        }
        else if (!titleId.equals(other.titleId))
            return false;
        if (version == null)
        {
            if (other.version != null)
                return false;
        }
        else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ProviewTitleInfo [titleId="
            + titleId
            + ", vesrion="
            + version
            + ", publisher="
            + publisher
            + ", lastupdate="
            + lastupdate
            + ", status="
            + status
            + ", title="
            + title
            + "]";
    }

    @Override
    public int compareTo(final ProviewTitleInfo info)
    {
        final int versionDiff = info.getMajorVersion().compareTo(getMajorVersion());

        if (versionDiff == 0)
        {
            return getTitleId().compareToIgnoreCase(info.getTitleId());
        }
        return versionDiff;
    }
}
