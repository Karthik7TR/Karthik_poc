package com.thomsonreuters.uscl.ereader.format.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class CssStylingService {
    private static final BigDecimal FONT_SIZE_BIG_MULTIPLIER = BigDecimal.valueOf(6);
    private static final BigDecimal FONT_SIZE_MEDIUM_MULTIPLIER = BigDecimal.valueOf(2.36d);
    private static final BigDecimal FONT_SIZE_SMALL_MULTIPLIER = BigDecimal.valueOf(0.083d);
    private static final BigDecimal LARGE_INDENT = BigDecimal.valueOf(3d);
    private static final BigDecimal BIG_INDENT = BigDecimal.valueOf(2.5d);
    private static final BigDecimal MEDIUM_INDENT = BigDecimal.ONE;
    private static final BigDecimal SMALL_INDENT = BigDecimal.valueOf(0.1d);
    private static final String P_UNIT = "p";
    private static final String Q_UNIT = "q";
    private static final String D_UNIT = "d";
    private static final String Z_UNIT = "z";
    private static final String M_UNIT = "m";
    private static final String I_UNIT = "i";
    private static final String LOWERCASE = "lower";
    private static final String UPPERCASE = "upper";
    private static final String SMALLCAP = "smallcap";
    private static final int FONT_SCALE = 1;
    private static final int BOLD = 1;
    private static final int ITALIC = 2;
    private static final int BOLD_ITALIC = 3;
    private static final String DEFAULT_TEXT_ALIGN = "left";
    private static final String DEFAULT_MARGIN_TOP = "0.1";
    private static final String DEFAULT_MARGIN_LEFT = "0.350";
    private static final String DEFAULT_FONT_SIZE = "1.0";
    private static final String DEFAULT_PADDING_TOP = "1";

    public String getStyleByElement(final Element element) {
        return getStyleByElement(element, StringUtils.EMPTY);
    }

    public String getStyleByElement(final Element element, final String prefix) {
        final StringBuilder style = new StringBuilder();
        Optional.ofNullable(element.attr(String.format("%salign", prefix)))
            .filter(StringUtils::isNotBlank)
            .ifPresent(align -> style.append(textAlign(align)));
        Optional.ofNullable(element.attr(String.format("%sprelead", prefix)))
            .filter(StringUtils::isNotBlank)
            .map(this::getIndentValue)
            .ifPresent(prelead -> style.append(marginTop(prelead)));
        Optional.ofNullable(element.attr(String.format("%slindent", prefix)))
            .filter(lindent -> StringUtils.isNoneBlank(lindent) && !"0".equals(lindent))
            .map(this::getIndentValue)
            .ifPresent(lindent -> style.append(marginLeft(lindent)));
        Optional.ofNullable(element.attr(String.format("%sfv", prefix)))
            .filter(StringUtils::isNotBlank)
            .ifPresent(prelead -> style.append(getFontWeightStyle(prelead)).append(getFontStyle(prelead)));
        Optional.ofNullable(element.attr(String.format("%ssize", prefix)))
            .filter(StringUtils::isNotBlank)
            .map(this::getFontSize)
            .ifPresent(size -> style.append(fontSize(size)));
        Optional.ofNullable(element.attr(String.format("%scm", prefix)))
            .filter(StringUtils::isNotBlank)
            .ifPresent(cm -> style.append(getCmStyle(cm)));
        Optional.ofNullable(element.attr(String.format("%sunderline", prefix)))
            .filter(StringUtils::isNotBlank)
            .ifPresent(underline -> style.append("text-decoration: underline; "));
        return style.toString();
    }

    public boolean hasStylingAttribute(final Element element) {
        return element.hasAttr("lindent")
            || element.hasAttr("align")
            || element.hasAttr("prelead")
            || element.hasAttr("size")
            || element.hasAttr("fv");
    }

    private String getIndentValue(final String value) {
        if (value.length() <= 1) {
            return value;
        }
        final BigDecimal indentValue = new BigDecimal(value.substring(0, value.length() - 1));
        final BigDecimal multiplier;
        if (value.endsWith(Q_UNIT)) {
            multiplier = SMALL_INDENT;
        } else if (value.endsWith(P_UNIT)) {
            multiplier = LARGE_INDENT;
        } else if (value.endsWith(I_UNIT)) {
            multiplier = BIG_INDENT;
        } else {
            multiplier = MEDIUM_INDENT;
        }
        return indentValue.multiply(multiplier).toString();
    }

    private String getFontWeightStyle(final String value) {
        final Integer intValue = Integer.valueOf(value);
        return fontWeight(intValue);
    }

    private String fontWeight(final Integer intValue) {
        String style = StringUtils.EMPTY;
        if (intValue == BOLD || intValue == BOLD_ITALIC) {
            style = "font-weight: bold; ";
        }
        return style;
    }

    private String getFontSize(final String value) {
        final BigDecimal size = new BigDecimal(value.substring(0, value.length() - 1));
        final BigDecimal result;
        if (value.endsWith(Q_UNIT) || value.endsWith(D_UNIT)) {
            result = size.multiply(FONT_SIZE_SMALL_MULTIPLIER);
        } else if (value.endsWith(I_UNIT) || value.endsWith(M_UNIT)) {
            result = size.multiply(FONT_SIZE_BIG_MULTIPLIER);
        } else if (value.endsWith(Z_UNIT)) {
            result = size.multiply(FONT_SIZE_MEDIUM_MULTIPLIER);
        } else {
            result = size;
        }
        return result.setScale(FONT_SCALE, RoundingMode.HALF_UP).toString();
    }

    private String getFontStyle(final String value) {
        String style = StringUtils.EMPTY;
        final Integer intValue = Integer.valueOf(value);
        if (intValue == ITALIC || intValue == BOLD_ITALIC) {
            style = "font-style: italic; ";
        }
        return style;
    }

    private String getCmStyle(final String value) {
        final String style;
        switch (value) {
        case SMALLCAP:
            style = "font-variant: small-caps; ";
            break;
        case UPPERCASE:
            style = "text-transform: uppercase; ";
            break;
        case LOWERCASE:
            style = "text-transform: lowercase; ";
            break;
        default:
            style = StringUtils.EMPTY;
            break;
        }
        return style;
    }

    public String getDefaultIndexStyle(final boolean isHeader) {
        final StringBuilder style = new StringBuilder();

        style.append(textAlign(DEFAULT_TEXT_ALIGN))
             .append(marginTop(DEFAULT_MARGIN_TOP))
             .append(marginLeft(DEFAULT_MARGIN_LEFT))
             .append(fontSize(DEFAULT_FONT_SIZE));

        if (isHeader) {
            style.append(paddingTop(DEFAULT_PADDING_TOP));
        }

        return style.toString();
    }

    public String listTypeNone() {
        return "list-style-type: none";
    }

    private String fontSize(final String value) {
        return String.format("font-size: %sem; ", value);
    }

    private String marginLeft(final String value) {
        return String.format("margin-left: %sem; ", value);
    }

    private String marginTop(final String value) {
        return String.format("margin-top: %sem; ", value);
    }

    private String textAlign(final String value) {
        return String.format("text-align: %s; ", value);
    }

    private String paddingTop(final String value) {
        return String.format("padding-top: %sem; ", value);
    }

    public String fontWeightBold() {
        return fontWeight(BOLD);
    }
}
