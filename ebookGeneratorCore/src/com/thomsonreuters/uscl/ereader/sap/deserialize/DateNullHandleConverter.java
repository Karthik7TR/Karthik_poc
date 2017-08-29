package com.thomsonreuters.uscl.ereader.sap.deserialize;

import java.util.Date;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Convert date string representation to date, if string representation null or empty or conteins "null", result will be null
 */
public class DateNullHandleConverter extends StdConverter<String, Date>
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public Date convert(final String input)
    {
        Date output = null;
        if (input != null && !"null".equalsIgnoreCase(input) && !StringUtils.EMPTY.equals(input))
        {
            output = new Date(DATE_FORMAT.parseDateTime(input).withZone(DateTimeZone.forID("US/Central")).getMillis());
        }
        return output;
    }
}
