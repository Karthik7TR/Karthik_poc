package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.recovery;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SectionNumberServiceImpl implements SectionNumberService {
    private static final String DELIMETERS = String.join("|", "-", "\\sto\\s");
    // USCA, USRA, U.S.C. etc
    private static final String EXTERNAL_LAW_PATTERN = "U\\.?S\\.?[a-zA-Z]\\.?";
    // Art.; ART.; Rule; RULE; Rules; RULES; R.;
    private static final String PRIMARY_SOURCE_PREFIX_PATTERN =
        String.format("(?:[Aa][Rr][Tt]\\.|[Rr][Uu][Ll][Ee][Ss]?|R\\.|%s)", EXTERNAL_LAW_PATTERN);
    // 12:34-567; 12:34-56:76; 12:34; 12:34-567a; 12:34-56:78.9b
    private static final String SECTION_NUMBER_PATTERN = String
        .format("(((\\d+):\\d+(?:\\.\\d+)?(?:[a-z])?)(?:(?:%s)((?:\\d+:)?\\d+(?:\\.\\d+)?[a-z]?)?)?)", DELIMETERS);
    // 123; 123a; 123.1a; all of that should be preceded by PRIMARY_SOURCE_PREFIX_PATTERN
    private static final String PRIMARY_SOURCE_SECTION_NUMBER_PATTERN = String.format(
        "(?<=%s.*?)(?<!\\()(\\d+(?:\\.\\d+)?(?:[a-z](?:-\\d+)?)?)(?!(?:\\)|(?:\\d+)?\\s%s))",
        PRIMARY_SOURCE_PREFIX_PATTERN,
        EXTERNAL_LAW_PATTERN);

    @NotNull
    @Override
    public String getPattern() {
        return String.format("%s|%s", SECTION_NUMBER_PATTERN, PRIMARY_SOURCE_SECTION_NUMBER_PATTERN);
    }

    @NotNull
    @Override
    public String getPrefixPattern() {
        // Optional space character match added here because it would break PRIMARY_SOURCE_SECTION_NUMBER_PATTERN otherwise
        return String.format("%s\\s?", PRIMARY_SOURCE_PREFIX_PATTERN);
    }
}
