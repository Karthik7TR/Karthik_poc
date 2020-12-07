package com.thomsonreuters.uscl.ereader.util;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 *
 */
public class UrlParsingUtil {
    private static final String UTF8_ENCODING = "utf-8";
    private static final Pattern DOCUMENT_UUID_PATTERN = Pattern.compile(
        ".*/Document/FullText\\?([a-zA-Z]{1}[a-fA-F0-9]{10}[-]?[a-fA-F0-9]{11}[-]?[a-fA-F0-9]{11})/View/FullText.html?.*");
    private static final int RESULT_GROUP = 1;

    /**
     * @param resourceUrl
     * @return urlContents is a Map , which contains all the Url query values and/or references.
     * @throws SAXException
     */
    public static Map<String, String> parseUrlContents(final String resourceUrl) {
        final Map<String, String> urlContents = new HashMap<>();

        final String documentUuid = getDocumentUuid(resourceUrl);
        urlContents.put("documentUuid", documentUuid);

        try {
            final URL aURL = new URL(resourceUrl);
            urlContents.put("reference", aURL.getRef());

            final String queryString = URLDecoder.decode(aURL.getQuery(), UTF8_ENCODING);

            final StringTokenizer pairs = new StringTokenizer(queryString, "&");

            while (pairs.hasMoreTokens()) {
                final String pair = pairs.nextToken();
                final StringTokenizer parts = new StringTokenizer(pair, "=");
                String name = parts.nextToken();
                String value = null;

                if (parts.hasMoreTokens()) {
                    value = parts.nextToken();
                }

                if ("cite".equalsIgnoreCase(name) && value != null) {
                    if (value.startsWith("UUID")) {
                        value = getCiteDocumentUuid(value);
                        name = "documentUuid";
                    } else {
                        value = applyCiteNormalization(value);
                    }
                }

                urlContents.put(name, value);
            }
        } catch (final Exception e) {
            throw new EBookException(
                UTF8_ENCODING
                    + " encoding not supported when attempting to parse normalized cite from URL: "
                    + resourceUrl,
                e);
        }

        return urlContents;
    }

    /**
     *
     * @param cite
     *
     * @return
     */
    private static String applyCiteNormalization(final String cite) {
        return NormalizationRulesUtil.applyCitationNormalizationRules(cite);
    }

    /**
     * Determines if the url matches a known document UUID pattern.
     *
     * @param resourceUrl the URL to compare to the pattern.
     *
     * @return document UUID if the url is a document UUID, null otherwise.
     */
    private static String getDocumentUuid(final String resourceUrl) {
        final Matcher matcher = DOCUMENT_UUID_PATTERN.matcher(resourceUrl);

        if (!matcher.find()) {
            return null;
        }

        return matcher.group(RESULT_GROUP);
    }

    /**
     * @param cite
     * @return
     */
    private static String getCiteDocumentUuid(final String cite) {
        return cite.split("\\(")[1].split("\\)")[0].trim();
    }
}
