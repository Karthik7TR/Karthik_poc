package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(final String v1, final String v2) {
        try {
            return new Version(v1).compareTo(new Version(v2));
        } catch (final Exception e) {
            log.error("Failed to parse version: ", e);
            return 0;
        }
    }
}
