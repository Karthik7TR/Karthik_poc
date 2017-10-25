package com.thomsonreuters.uscl.ereader.sap.component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Java representation of "Low Level Rule" field values.
 */
public enum MediaLowLevelRule {
    UNSUPPORTED(StringUtils.EMPTY),
    LL_30("30"),
    LL_35("35"),
    LL_34("34"),
    LL_A9("A9"),
    LL_28("28"),
    LL_A8("A8"),
    LL_K9("K9"),
    LL_37("37"),
    LL_80("80"),
    LL_K0("K0");

    private static final String LL_CODE_GROUP = "codeGroup";
    private static final Pattern LL_RULE_PATTERN = Pattern.compile(String.format("(?<%s>[A-Za-z0-9]{2})-.*", LL_CODE_GROUP));

    private final String ruleValue;

    MediaLowLevelRule(final String ruleValue) {
        this.ruleValue = ruleValue;
    }

    /**
     * Get representation by SAP field value
     */
    public static MediaLowLevelRule getByRuleValue(final String ruleValue) {
        MediaLowLevelRule result = UNSUPPORTED;

        final Matcher ruleMatcher = LL_RULE_PATTERN.matcher(ruleValue);
        if (ruleMatcher.matches()) {
            final String ruleCode = ruleMatcher.group(LL_CODE_GROUP);
            for (final MediaLowLevelRule currentRule : values()) {
                if (StringUtils.equalsIgnoreCase(currentRule.ruleValue, ruleCode)) {
                    result = currentRule;
                    break;
                }
            }
        }
        return result;
    }
}
