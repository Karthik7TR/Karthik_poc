package com.thomsonreuters.uscl.ereader.common.deliver.comparsion;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

public class SplitTitleIdsComparator implements Comparator<String> {
    private static final String ORDER_GROUP = "order";

    private final Pattern splitTitleIdPattern;

    public SplitTitleIdsComparator(@NotNull final String titleId) {
        splitTitleIdPattern = Pattern.compile(String.format("%s_pt(?<%s>\\d+)", titleId, ORDER_GROUP));
    }

    @Override
    public int compare(@NotNull final String firstTitleId, @NotNull final String secondTitleId) {
        return -Integer.compare(getTitleOrderValue(firstTitleId), getTitleOrderValue(secondTitleId));
    }

    private int getTitleOrderValue(final String titleId) {
        int titleOrderValue = 0;
        final Matcher orderMatcher = splitTitleIdPattern.matcher(titleId);
        if (orderMatcher.matches()) {
            titleOrderValue = Integer.valueOf(orderMatcher.group(ORDER_GROUP));
        }
        return titleOrderValue;
    }
}
