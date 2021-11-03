package com.thomsonreuters.uscl.ereader.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

public final class EbookHtmlUtils {
    private static final OutputSettings PRETTY_PRINT_FALSE = new OutputSettings().prettyPrint(false);
    private static final Whitelist SIMPLE_STYLING_HTML_WHITELIST = Whitelist.simpleText()
            .addTags("mark", "cite", "dfn");

    private EbookHtmlUtils() {}

    public static String unescapeHtmlStylingTagsAndRemoveOthers(final String input) {
        final String unescapedInput = StringEscapeUtils.unescapeXml(Optional.ofNullable(input).orElse(EMPTY));
        return Jsoup.clean(unescapedInput, EMPTY, SIMPLE_STYLING_HTML_WHITELIST, PRETTY_PRINT_FALSE);
    }
}
