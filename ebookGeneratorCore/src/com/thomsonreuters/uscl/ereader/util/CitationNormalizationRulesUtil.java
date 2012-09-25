package com.thomsonreuters.uscl.ereader.util;

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
    
    public static String noSpacesNormalizationRules(String cite)
    {
    	if(StringUtils.isNotBlank(cite))
    	{
    		cite = applyNormalizationRules(cite);
    		cite = cite.replaceAll("\\s", "");
    		cite = cite.trim();
    	}
    	
    	return cite;
    }
}
