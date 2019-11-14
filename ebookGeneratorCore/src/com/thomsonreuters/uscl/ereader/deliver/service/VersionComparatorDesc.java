package com.thomsonreuters.uscl.ereader.deliver.service;

public class VersionComparatorDesc extends VersionComparator {
    @Override
    public int compare(final String v1, final String v2) {
        return super.compare(v2, v1);
    }
}
