package com.thomsonreuters.uscl.ereader.deliver.service;

public interface TitleInfo
{
    String getTitle();

    String getTitleId();

    Integer getTotalNumberOfVersions();

    String getVersion();

    Integer getMajorVersion();

    Integer getMinorVersion();

    String getPublisher();

    String getLastupdate();

    String getStatus();
}
