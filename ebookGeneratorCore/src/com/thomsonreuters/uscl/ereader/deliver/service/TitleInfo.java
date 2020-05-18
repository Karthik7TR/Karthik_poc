package com.thomsonreuters.uscl.ereader.deliver.service;

import java.math.BigInteger;

public interface TitleInfo {
    String getTitle();

    String getTitleId();

    Integer getTotalNumberOfVersions();

    Integer getSplitParts();

    String getVersion();

    BigInteger getMajorVersion();

    BigInteger getMinorVersion();

    String getPublisher();

    String getLastupdate();

    String getStatus();
}
