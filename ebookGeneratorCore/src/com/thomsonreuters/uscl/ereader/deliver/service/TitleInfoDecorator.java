package com.thomsonreuters.uscl.ereader.deliver.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public abstract class TitleInfoDecorator implements TitleInfo
{
    @NotNull
    protected TitleInfo titleInfo;

    public TitleInfoDecorator(@NotNull final TitleInfo titleInfo)
    {
        Assert.notNull(titleInfo);
        this.titleInfo = titleInfo;
    }

    @Override
    public String getTitle()
    {
        return titleInfo.getTitle();
    }

    @Override
    public String getTitleId()
    {
        return titleInfo.getTitleId();
    }

    @Override
    public Integer getTotalNumberOfVersions()
    {
        return titleInfo.getTotalNumberOfVersions();
    }

    @Override
    public String getVersion()
    {
        return titleInfo.getVersion();
    }

    @Override
    public Integer getMajorVersion()
    {
        return titleInfo.getMajorVersion();
    }

    @Override
    public Integer getMinorVersion()
    {
        return titleInfo.getMinorVersion();
    }

    @Override
    public String getPublisher()
    {
        return titleInfo.getPublisher();
    }

    @Override
    public String getLastupdate()
    {
        return titleInfo.getLastupdate();
    }

    @Override
    public String getStatus()
    {
        return titleInfo.getStatus();
    }
}
