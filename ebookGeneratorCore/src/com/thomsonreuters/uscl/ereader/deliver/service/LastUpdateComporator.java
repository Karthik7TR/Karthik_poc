package com.thomsonreuters.uscl.ereader.deliver.service;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LastUpdateComporator implements Comparator<String> {
    private static Logger LOG = LogManager.getLogger(LastUpdateComporator.class);

    @Override
    public int compare(final String l1, final String l2) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dateFormat.parse(l1).compareTo(dateFormat.parse(l2));
        } catch (final Exception e) {
            LOG.error("Failed to parse last Update date: ", e);
            return 0;
        }
    }
}
