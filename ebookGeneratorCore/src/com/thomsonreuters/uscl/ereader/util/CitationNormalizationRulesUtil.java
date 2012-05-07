package com.thomsonreuters.uscl.ereader.util;

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
     * 
     * This method will apply list of normalization rules for given cite.
     * @param normalizedCite
     *
     * @return normalized cite. 
     */
    public static String applyNormalizationRules(String normalizedCite)
    {
        normalizedCite = normalizedCite.toUpperCase();

        if (normalizedCite.contains(String.valueOf(UNICODE_SECTION_SYMBOL)))
        {
            normalizedCite = normalizedCite.replace(String.valueOf(UNICODE_SECTION_SYMBOL), "s");
        }

        if (normalizedCite.contains(String.valueOf(UNICODE_PARAGRAPH_SYMBOL)))
        {
            normalizedCite = normalizedCite.replace(String.valueOf(UNICODE_PARAGRAPH_SYMBOL), "p");
        }

        if (normalizedCite.contains(String.valueOf(UNICODE_LEFT_BRACKET_SYMBOL)))
        {
            normalizedCite = normalizedCite.replace(
                    String.valueOf(UNICODE_LEFT_BRACKET_SYMBOL), "(");
        }

        if (normalizedCite.contains(String.valueOf(UNICODE_RIGHT_BRACKET_SYMBOL)))
        {
            normalizedCite = normalizedCite.replace(
                    String.valueOf(UNICODE_RIGHT_BRACKET_SYMBOL), ")");
        }

        if (normalizedCite.contains(String.valueOf(UNICODE_CARET_SYMBOL)))
        {
            normalizedCite = normalizedCite.replace(String.valueOf(UNICODE_CARET_SYMBOL), "-");
        }

        return normalizedCite;
    }
}
