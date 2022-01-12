package com.thomsonreuters.uscl.ereader.deliver.service;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LastUpdateComporator implements Comparator<String> {

    @Override
    public int compare(final String l1, final String l2) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dateFormat.parse(l1).compareTo(dateFormat.parse(l2));
        } catch (final Exception e) {
            log.error("Failed to parse last Update date: ", e);
            return 0;
        }
    }
}
