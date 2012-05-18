/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.util;

import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;


/**
 * 
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 *
 */
public class UrlParsingUtil
{
    private static final String UTF8_ENCODING = "utf-8";
    private static final Pattern DOCUMENT_UUID_PATTERN =
        Pattern.compile(
            ".*/Document/FullText\\?([a-zA-Z]{1}[a-fA-F0-9]{10}[-]?[a-fA-F0-9]{11}[-]?[a-fA-F0-9]{11})/View/FullText.html?.*");
    private static final int RESULT_GROUP = 1;

    /**
     * @param resourceUrl
     * @return urlContents is a Map , which contains all the Url query values and/or references.
     * @throws SAXException
     */
    public static Map<String, String> parseUrlContents(final String resourceUrl)
        throws SAXException
    {
        Map<String, String> urlContents = new HashMap<String, String>();

        String documentUuid = getDocumentUuid(resourceUrl);
        urlContents.put("documentUuid", documentUuid);

        try
        {
            URL aURL = new URL(resourceUrl);
            urlContents.put("reference", aURL.getRef());

            String queryString = URLDecoder.decode(aURL.getQuery(), UTF8_ENCODING);

            StringTokenizer pairs = new StringTokenizer(queryString, "&");

            while (pairs.hasMoreTokens())
            {
                String pair = pairs.nextToken();
                StringTokenizer parts = new StringTokenizer(pair, "=");
                String name = parts.nextToken();
                String value = null;

                if (parts.hasMoreTokens())
                {
                    value = parts.nextToken();
                }

                if ("cite".equalsIgnoreCase(name))
                {
                	if (value.startsWith("UUID"))
                	{
                		value = getCiteDocumentUuid(value);
                		name = "documentUuid";
                	}
                	else 
                	{
                      value = applyCiteNormalization(value);
                	}
                    
                }

                urlContents.put(name, value);
            }
        }
        catch (Exception e)
        {
            throw new SAXException(
                UTF8_ENCODING
                + " encoding not supported when attempting to parse normalized cite from URL: "
                + resourceUrl, e);
        }

        return urlContents;
    }

    /**
     * 
     * @param cite
     *
     * @return
     */
    private static String applyCiteNormalization(final String cite)
    {
        return CitationNormalizationRulesUtil.applyNormalizationRules(cite);
    }

    /**
     * Determines if the url matches a known document UUID pattern.
     *
     * @param resourceUrl the URL to compare to the pattern.
     *
     * @return document UUID if the url is a document UUID, null otherwise.
     */
    private static String getDocumentUuid(String resourceUrl)
    {
        Matcher matcher = DOCUMENT_UUID_PATTERN.matcher(resourceUrl);

        if (!matcher.find())
        {
            return null;
        }

        return matcher.group(RESULT_GROUP);
    }
    
    /**
     * @param cite
     * @return
     */
    private static String getCiteDocumentUuid(String cite)
    {
    		return cite.split("\\(")[1].split("\\)")[0].trim();
    }
}
