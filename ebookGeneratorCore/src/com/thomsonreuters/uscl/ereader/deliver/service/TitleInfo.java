package com.thomsonreuters.uscl.ereader.deliver.service;

import java.math.BigInteger;
import java.util.List;

public interface TitleInfo {
    String getTitle();

    String getTitleId();

    Integer getTotalNumberOfVersions();

    List<String> getSplitParts();

    String getVersion();

    BigInteger getMajorVersion();

    BigInteger getMinorVersion();

    String getPublisher();

    String getLastupdate();

    String getStatus();
}
