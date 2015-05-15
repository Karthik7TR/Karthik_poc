/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Ravi Nandikolla c139353
 */
public class NormalizationRulesUtil
{
    private static String UNICODE_SECTION_SYMBOL = "\u00A7";
    private static String UNICODE_PARAGRAPH_SYMBOL = "\u00B6";
    private static String UNICODE_LEFT_BRACKET_SYMBOL = "\u005B";
    private static String UNICODE_RIGHT_BRACKET_SYMBOL = "\u005D";
    private static String UNICODE_CARET_SYMBOL = "\u005E";
    
    // Special hypens
    private static String UNICODE_HEBREW_PUNCTUATION = "\u05BE";
    private static String UNICODE_HYPHEN = "\u2010";
    private static String UNICODE_NON_BREAKING_HYPHEN = "\u2011";
    private static String UNICODE_FIGURE_DASH = "\u2012";
    private static String UNICODE_EN_DASH = "\u2013";
    private static String UNICODE_EM_DASH = "\u2014";
    private static String UNICODE_HORIZONTAL_BAR = "\u2015";
    private static String UNICODE_SMALL_EM_DASH = "\uFE58";
    private static String UNICODE_SMALL_HYPEN_MINUS = "\uFE63";
    private static String UNICODE_FULLWIDTH_HYPEN_MINUS = "\uFF0D";
    
    // Special whitespace
    private static String UNICODE_NO_BREAK_SPACE = "\u00A0";
    private static String UNICODE_EN_QUAD = "\u2000";
    private static String UNICODE_EM_QUAD = "\u2001";
    private static String UNICODE_EN_SPACE = "\u2002";
    private static String UNICODE_EM_SPACE = "\u2003";
    private static String UNICODE_THREE_PER_EM_SPACE = "\u2004";
    private static String UNICODE_FOUR_PER_EM_SPACE = "\u2005";
    private static String UNICODE_SIX_PER_EM_SPACE = "\u2006";
    private static String UNICODE_FIGURE_SPACE = "\u2007";
    private static String UNICODE_PUNCTUATION_SPACE = "\u2008";
    private static String UNICODE_THIN_SPACE = "\u2009";
    private static String UNICODE_HAIR_SPACE = "\u200A";
    private static String UNICODE_NARROW_NO_BREAK_SPACE = "\u202F";
    private static String UNICODE_MEDIUM_MATHEMATICAL_SPACE = "\u205F";
    private static String UNICODE_IDEOGRAPHIC_SPACE = "\u3000";
    
    
    /**
     * This method will apply list of normalization rules for given cite.
     *
     * @param normalizedCite
     *
     * @return normalized cite.
     */
    public static String applyCitationNormalizationRules(String normalizedCite)
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
    
    public static String applyTableOfContentNormalizationRules(String tocLabel) 
    {
    	tocLabel = hyphenNormalizationRules(tocLabel);
    	tocLabel = whiteSpaceNormalizationRules(tocLabel);
    	return tocLabel;
    }
    
    public static String hyphenNormalizationRules(String text) 
    {
    	if(StringUtils.isNotBlank(text))
    	{
	    	// Replace different unicode hypens into HYPHEN-MINUS which is used on the keyboard
	    	text = StringUtils.replace(text, UNICODE_FIGURE_DASH, "-");
	    	text = StringUtils.replace(text, UNICODE_EN_DASH, "-");
	    	text = StringUtils.replace(text, UNICODE_EM_DASH, "-");
	    	text = StringUtils.replace(text, UNICODE_HORIZONTAL_BAR, "-");
	    	text = StringUtils.replace(text, UNICODE_HEBREW_PUNCTUATION, "-");
	    	text = StringUtils.replace(text, UNICODE_HYPHEN, "-");
	    	text = StringUtils.replace(text, UNICODE_NON_BREAKING_HYPHEN, "-");
	    	text = StringUtils.replace(text, UNICODE_SMALL_EM_DASH, "-");
	    	text = StringUtils.replace(text, UNICODE_SMALL_HYPEN_MINUS, "-");
	    	text = StringUtils.replace(text, UNICODE_FULLWIDTH_HYPEN_MINUS, "-");
    	}
    	
    	return text;
    }
    
    public static String whiteSpaceNormalizationRules(String text) {
    	// replace special white space with \u0020
    	if(StringUtils.isNotBlank(text))
    	{
    		text = StringUtils.replace(text, UNICODE_NO_BREAK_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_EN_QUAD, " ");
    		text = StringUtils.replace(text, UNICODE_EM_QUAD, " ");
    		text = StringUtils.replace(text, UNICODE_EN_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_EM_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_THREE_PER_EM_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_FOUR_PER_EM_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_SIX_PER_EM_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_FIGURE_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_PUNCTUATION_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_THIN_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_HAIR_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_NARROW_NO_BREAK_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_MEDIUM_MATHEMATICAL_SPACE, " ");
    		text = StringUtils.replace(text, UNICODE_IDEOGRAPHIC_SPACE, " ");
    	}
    	
    	return text;
    }
    
    public static String pubPageNormalizationRules(String cite)
    {
    	if(StringUtils.isNotBlank(cite))
    	{
    		cite = StringEscapeUtils.unescapeXml(cite);
    		cite = StringEscapeUtils.unescapeHtml(cite);
    		cite = applyCitationNormalizationRules(cite);
    		
    		// Replace special apostrophe
            // unicode values u2018 and u2019
            cite = StringUtils.replace(cite, "\u2018", "'");
            cite = StringUtils.replace(cite, "\u2019", "'");
            cite = StringUtils.replace(cite, "\ufffd", "'");
            
            // Replace special double quotes
            cite = StringUtils.replace(cite, "\u201C", "\"");
            cite = StringUtils.replace(cite, "\u201D", "\"");

            // Replace special dashes
            cite = hyphenNormalizationRules(cite);

            // Remove spaces
    		cite = cite.replaceAll("\\s", "");
    		cite = cite.trim();
    	}
    	
    	return cite;
    }
}
