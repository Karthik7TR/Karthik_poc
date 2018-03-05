package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class VersionComparator implements Comparator<String> {
    private static Logger LOG = LogManager.getLogger(VersionComparator.class);

    @Override
    public int compare(final String v1, final String v2) {
        try {
            return new Version(v1).compareTo(new Version(v2));
        } catch (final Exception e) {
            LOG.error("Failed to parse version: ", e);
            return 0;
        }
    }
}
