package com.thomsonreuters.uscl.ereader.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Ravi Nandikolla c139353
 */
public class CitationNormalizationRulesUtil
{
    private static String UNICODE_SECTION_SYMBOL = "\u00A7";
    private static String UNICODE_PARAGRAPH_SYMBOL = "\u00B6";
    private static String UNICODE_LEFT_BRACKET_SYMBOL = "\u005B";
    private static String UNICODE_RIGHT_BRACKET_SYMBOL = "\u005D";
    private static String UNICODE_CARET_SYMBOL = "\u005E";

    /**
     * This method will apply list of normalization rules for given cite.
     *
     * @param normalizedCite
     *
     * @return normalized cite.
     */
    public static String applyNormalizationRules(String normalizedCite)
    {
        if (normalizedCite != null)
        {
            normalizedCite = normalizedCite.toUpperCase();

            normalizedCite = normalizedCite.replace(String.valueOf(UNICODE_SECTION_SYMBOL), "S");
            normalizedCite = normalizedCite.replace(String.valueOf(UNICODE_PARAGRAPH_SYMBOL), "P");
            normalizedCite = normalizedCite.replace(
                    String.valueOf(UNICODE_LEFT_BRACKET_SYMBOL), "(");
            normalizedCite = normalizedCite.replace(
                    String.valueOf(UNICODE_RIGHT_BRACKET_SYMBOL), ")");
            normalizedCite = normalizedCite.replace(String.valueOf(UNICODE_CARET_SYMBOL), "-");
        }

        return normalizedCite;
    }
    
    public static String pubPageNormalizationRules(String cite)
    {
    	if(StringUtils.isNotBlank(cite))
    	{
    		cite = StringEscapeUtils.unescapeXml(cite);
    		cite = StringEscapeUtils.unescapeHtml(cite);
    		cite = applyNormalizationRules(cite);
    		
    		// Replace special apostrophe
            // unicode values u2018 and u2019
            cite = StringUtils.replace(cite, "\u2018", "'");
            cite = StringUtils.replace(cite, "\u2019", "'");
            cite = StringUtils.replace(cite, "\ufffd", "'");
            
            // Replace special double quotes
            cite = StringUtils.replace(cite, "\u201C", "\"");
            cite = StringUtils.replace(cite, "\u201D", "\"");

            // Replace special dashes
            cite = StringUtils.replace(cite, "\u2012", "-");
            cite = StringUtils.replace(cite, "\u2013", "-");
            cite = StringUtils.replace(cite, "\u2014", "-");
            cite = StringUtils.replace(cite, "\u2015", "-");

            // Remove spaces
    		cite = cite.replaceAll("\\s", "");
    		cite = cite.trim();
    	}
    	
    	return cite;
    }
}
