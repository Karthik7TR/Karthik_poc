package com.thomsonreuters.uscl.ereader.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

/**
 *
 * @author Ravi Nandikolla c139353
 */
public class NormalizationRulesUtil {
    private static final String DASH = "-";
    private static final String DOT = ".";
    private static final String BRACES_REGEX = "\\(.*\\)";
    private static final String PUNCTUATION_REGEX = "\\.|,|'|";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String UNICODE_SECTION_SYMBOL = "\u00A7";
    private static final String UNICODE_PARAGRAPH_SYMBOL = "\u00B6";
    private static final String UNICODE_LEFT_BRACKET_SYMBOL = "\u005B";
    private static final String UNICODE_RIGHT_BRACKET_SYMBOL = "\u005D";
    private static final String UNICODE_CARET_SYMBOL = "\u005E";

    // Special hypens
    private static final String UNICODE_HEBREW_PUNCTUATION = "\u05BE";
    private static final String UNICODE_HYPHEN = "\u2010";
    private static final String UNICODE_NON_BREAKING_HYPHEN = "\u2011";
    private static final String UNICODE_FIGURE_DASH = "\u2012";
    private static final String UNICODE_EN_DASH = "\u2013";
    private static final String UNICODE_EM_DASH = "\u2014";
    private static final String UNICODE_HORIZONTAL_BAR = "\u2015";
    private static final String UNICODE_SMALL_EM_DASH = "\uFE58";
    private static final String UNICODE_SMALL_HYPEN_MINUS = "\uFE63";
    private static final String UNICODE_FULLWIDTH_HYPEN_MINUS = "\uFF0D";

    // Special whitespace
    private static final String UNICODE_NO_BREAK_SPACE = "\u00A0";
    private static final String UNICODE_EN_QUAD = "\u2000";
    private static final String UNICODE_EM_QUAD = "\u2001";
    private static final String UNICODE_EN_SPACE = "\u2002";
    private static final String UNICODE_EM_SPACE = "\u2003";
    private static final String UNICODE_THREE_PER_EM_SPACE = "\u2004";
    private static final String UNICODE_FOUR_PER_EM_SPACE = "\u2005";
    private static final String UNICODE_SIX_PER_EM_SPACE = "\u2006";
    private static final String UNICODE_FIGURE_SPACE = "\u2007";
    private static final String UNICODE_PUNCTUATION_SPACE = "\u2008";
    private static final String UNICODE_THIN_SPACE = "\u2009";
    private static final String UNICODE_HAIR_SPACE = "\u200A";
    private static final String UNICODE_NARROW_NO_BREAK_SPACE = "\u202F";
    private static final String UNICODE_MEDIUM_MATHEMATICAL_SPACE = "\u205F";
    private static final String UNICODE_IDEOGRAPHIC_SPACE = "\u3000";
    
    private static final String BOOK_NAME_GROUP = "bookName";
    private static final String PARAGRAPH_GROUP = "paragraph";
    private static final String PARAGRAPH_SIGNS_2 = "SS";
    private static final String PARAGRAPH_SIGNS_3 = "SSS";
    private static final Pattern CITE_COLON_SEPARATED_PATTERN = Pattern.compile(String.format("(?<%s>.+)S(?<%s>[0-9]+:[0-9]+)", BOOK_NAME_GROUP, PARAGRAPH_GROUP));
    private static final Pattern CITE_DOT_SEPARATED_PATTERN = Pattern.compile(String.format("(?<%s>.+[A-Za-z])(?<%s>[0-9]+\\.[0-9]+([\\.\\-][0-9A-Za-z]+)*)", BOOK_NAME_GROUP, PARAGRAPH_GROUP));

    /**
     * This method will apply list of normalization rules for given cite.
     *
     * @param normalizedCite
     *
     * @return normalized cite.
     */
    public static String applyCitationNormalizationRules(String normalizedCite) {
        if (normalizedCite != null) {
            normalizedCite = normalizedCite.toUpperCase();
            normalizedCite = normalizedCite.replace(UNICODE_SECTION_SYMBOL, "S");
            normalizedCite = normalizedCite.replace(UNICODE_PARAGRAPH_SYMBOL, "P");
            normalizedCite = normalizedCite.replace(UNICODE_LEFT_BRACKET_SYMBOL, "(");
            normalizedCite = normalizedCite.replace(UNICODE_RIGHT_BRACKET_SYMBOL, ")");
            normalizedCite = normalizedCite.replace(UNICODE_CARET_SYMBOL, DASH);
        }

        return normalizedCite;
    }

    public static String applyTableOfContentNormalizationRules(String tocLabel) {
        tocLabel = hyphenNormalizationRules(tocLabel);
        tocLabel = whiteSpaceNormalizationRules(tocLabel);
        return tocLabel;
    }

    public static String hyphenNormalizationRules(String text) {
        if (StringUtils.isNotBlank(text)) {
            // Replace different unicode hypens into HYPHEN-MINUS which is used on the keyboard
            text = StringUtils.replace(text, UNICODE_FIGURE_DASH, DASH);
            text = StringUtils.replace(text, UNICODE_EN_DASH, DASH);
            text = StringUtils.replace(text, UNICODE_EM_DASH, DASH);
            text = StringUtils.replace(text, UNICODE_HORIZONTAL_BAR, DASH);
            text = StringUtils.replace(text, UNICODE_HEBREW_PUNCTUATION, DASH);
            text = StringUtils.replace(text, UNICODE_HYPHEN, DASH);
            text = StringUtils.replace(text, UNICODE_NON_BREAKING_HYPHEN, DASH);
            text = StringUtils.replace(text, UNICODE_SMALL_EM_DASH, DASH);
            text = StringUtils.replace(text, UNICODE_SMALL_HYPEN_MINUS, DASH);
            text = StringUtils.replace(text, UNICODE_FULLWIDTH_HYPEN_MINUS, DASH);
        }

        return text;
    }
    public static String replaceHyphenToDash(String text) {
        return StringUtils.replace(hyphenNormalizationRules(text), DASH, " — ");
    }
    public static String whiteSpaceNormalizationRules(String text) {
        // replace special white space with \u0020
        if (StringUtils.isNotBlank(text)) {
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

    public static String pubPageNormalizationRules(String cite) {
        if (StringUtils.isNotBlank(cite)) {
            cite = StringEscapeUtils.unescapeXml(cite);
            cite = StringEscapeUtils.unescapeHtml4(cite);
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
            cite = cite.replaceAll(WHITESPACE_REGEX, StringUtils.EMPTY);
            cite = cite.trim();
        }

        return cite;
    }

    public static String normalizeNoDashesNoWhitespaces(final String cite) {
        return ofNullable(cite).map(c -> c.replaceAll(DASH, StringUtils.EMPTY).replaceAll(WHITESPACE_REGEX, StringUtils.EMPTY))
                .orElse(null);
    }

    public static String normalizeThirdLineCiteKeepingDecimalDot(final String cite) {
        return ofNullable(pubPageNormalizationRules(cite))
                .map(NormalizationRulesUtil::removeBraces)
                .map(NormalizationRulesUtil::removePunctuationKeepingTrailingDecimalNumber)
                .orElse(null);
    }

    public static String normalizeThirdLineCite(final String cite) {
        return ofNullable(pubPageNormalizationRules(cite))
                .map(NormalizationRulesUtil::removePunctuationAndBraces)
                .map(NormalizationRulesUtil::normalizeNoDashesNoWhitespaces)
                .orElse(null);
    }

    public static String normalizeCiteNoParagraphSign(final String cite) {
        return ofNullable(normalizeThirdLineCite(cite))
                .map(NormalizationRulesUtil::removeParagraphSign)
                .orElse(null);
    }

    public static String normalizeCiteTrailingDot(final String cite) {
        return ofNullable(normalizeNoDashesNoWhitespaces(cite))
                .map(c -> c + DOT)
                .orElse(null);
    }

    public static String normalizeCiteTrailingDot2(final String cite) {
        return ofNullable(normalizeNoDashesNoWhitespaces(cite))
                .map(c -> c + DOT + DOT)
                .orElse(null);
    }

    public static String normalizeCiteExtra3ParagraphSigns(final String cite) {
        return ofNullable(normalizeNoDashesNoWhitespaces(cite))
                .map(NormalizationRulesUtil::addExtra3ParagraphSigns)
                .orElse(null);
    }

    public static String normalizeCiteExtra2ParagraphSignsTrailingDot(final String cite) {
        return ofNullable(normalizeNoDashesNoWhitespaces(cite))
                .map(NormalizationRulesUtil::addExtra2ParagraphSignsTrailingDot)
                .orElse(null);
    }

    private static String removeParagraphSign(final String cite) {
        final Matcher matcher = CITE_COLON_SEPARATED_PATTERN.matcher(cite);
        if (matcher.matches()) {
            return matcher.group(BOOK_NAME_GROUP) + matcher.group(PARAGRAPH_GROUP);
        }
        return cite;
    }

    private static String addExtra3ParagraphSigns(final String cite) {
        final Matcher matcher = CITE_COLON_SEPARATED_PATTERN.matcher(cite);
        if (matcher.matches()) {
            return matcher.group(BOOK_NAME_GROUP) + PARAGRAPH_SIGNS_3 + matcher.group(PARAGRAPH_GROUP);
        }
        return cite;
    }

    private static String addExtra2ParagraphSignsTrailingDot(final String cite) {
        final Matcher matcher = CITE_COLON_SEPARATED_PATTERN.matcher(cite);
        if (matcher.matches()) {
            return matcher.group(BOOK_NAME_GROUP) + PARAGRAPH_SIGNS_2 + matcher.group(PARAGRAPH_GROUP) + DOT;
        }
        return cite;
    }

    private static String removePunctuationKeepingTrailingDecimalNumber(final String cite) {
        final Matcher matcher = CITE_DOT_SEPARATED_PATTERN.matcher(cite);
        if (matcher.matches()) {
            return removePunctuation(matcher.group(BOOK_NAME_GROUP)) + matcher.group(PARAGRAPH_GROUP);
        }
        return cite;
    }

    private static String removePunctuationAndBraces(final String cite) {
        return normalizeNoDashesNoWhitespaces(removePunctuation(removeBraces(cite)));
    }

    private static String removeBraces(final String cite) {
        return cite.replaceAll(BRACES_REGEX, StringUtils.EMPTY);
    }

    private static String removePunctuation(final String cite) {
        return cite.replaceAll(PUNCTUATION_REGEX, StringUtils.EMPTY);
    }
}
