package com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class CiteQueryMapperResponse {
    private final Collection<String> failedTags;
    private final String mapFilePath;

    public CiteQueryMapperResponse(final String mapFilePath) {
        this.mapFilePath = mapFilePath;
        failedTags = new ArrayList<>();
    }

    public String getMapFilePath() {
        return mapFilePath;
    }

    public void addFailedTag(final String tag) {
        Optional.ofNullable(tag)
            .filter(StringUtils::isNotBlank)
            .ifPresent(failedTags::add);
    }

    public Collection<String> getFailedTags() {
        return Collections.unmodifiableCollection(failedTags);
    }
}
