package com.thomsonreuters.uscl.ereader.mgr.web;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class FormUtils {
    private FormUtils() { }

    public static String parseDate(final Date date) {
        return Optional.ofNullable(date)
            .map(Date::toInstant)
            .map(instant -> instant.atOffset(ZoneOffset.UTC))
            .map(Object::toString)
            .orElse(null);
    }

    public static Date parseDate(final String dateString) {
        return Optional.ofNullable(dateString)
            .filter(StringUtils::isNotBlank)
            .map(OffsetDateTime::parse)
            .map(OffsetDateTime::toInstant)
            .map(Date::from)
            .orElse(null);
    }
}
