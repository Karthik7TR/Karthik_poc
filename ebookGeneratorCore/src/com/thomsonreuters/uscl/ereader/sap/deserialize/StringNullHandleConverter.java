package com.thomsonreuters.uscl.ereader.sap.deserialize;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Convert string representation to string, with handling of string which contains "null"
 */
public class StringNullHandleConverter extends StdConverter<String, String> {
    @Override
    public String convert(final String input) {
        String result = null;
        if (StringUtils.isNotBlank(input) && !"null".equalsIgnoreCase(input)) {
            result = input;
        }
        return result;
    }
}
