package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.recovery;

import org.springframework.stereotype.Service;

@Service
public class SectionNumberServiceImpl implements SectionNumberService {
    private static final String DELIMETERS = String.join("|",
            "-",
            "(?:\\sto\\s)");
    private static final String PREFIX_PATTERN = "(?:[Aa][Rr][Tt]\\.|Rule)\\s?";
    private static final String SECTION_NUMBER_PATTERN = String.format("((\\d+):\\d+(?:\\.\\d+|[a-z])?)(?:(?:%s)(\\d+(?:\\.\\d+)?)?)?", DELIMETERS);
    private static final String PRIMARY_SOURCE_SECTION_NUMBER_PATTERN = String.format("%s\\d+(?:\\.\\d+)?", PREFIX_PATTERN);
    private static final String COMBINED_PATTERN = String.format("(%s|%s)", SECTION_NUMBER_PATTERN, PRIMARY_SOURCE_SECTION_NUMBER_PATTERN);

    @Override
    public String getPattern() {
        return COMBINED_PATTERN;
    }

    @Override
    public String getPrefixPattern() {
        return PREFIX_PATTERN;
    }
}
