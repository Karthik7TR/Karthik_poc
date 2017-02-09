package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

public class Title
{
    private String titleId;
    private String proviewName;
    private Integer version;

    public String getTitleId()
    {
        return titleId;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = titleId;
    }

    public String getProviewName()
    {
        return proviewName;
    }

    public void setProviewName(final String proviewName)
    {
        this.proviewName = proviewName;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(final Integer version)
    {
        this.version = version;
    }
}
