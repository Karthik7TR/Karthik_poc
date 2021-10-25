package com.thomsonreuters.uscl.ereader.core.book.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

@ToString(of = {"headTitleId", "partNumber"})
@EqualsAndHashCode(of = {"headTitleId", "partNumber"})
public class TitleId implements Comparable<TitleId> {
    private static final String _PT = "_pt";

    @NotNull
    private String headTitleId;
    private int partNumber;

    public TitleId(@NotNull final String titleId) {
        Assert.notNull(titleId);

        final int indexOfPt = titleId.lastIndexOf(_PT);
        if (indexOfPt < 0) {
            headTitleId = titleId;
            partNumber = 1;
        } else {
            try {
                headTitleId = titleId.substring(0, indexOfPt);
                partNumber = Integer.valueOf(titleId.substring(indexOfPt + _PT.length()));
            } catch (final NumberFormatException e) {
                headTitleId = titleId;
                partNumber = 1;
            }
        }
    }

    @NotNull
    public String getHeadTitleId() {
        return headTitleId;
    }

    public boolean isHeadTitle() {
        return partNumber == 1;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public String getPartTitle(int partNumber) throws Exception {
        if (partNumber < 1) {
            throw new Exception("Split book part number should be greater than 0");
        }
        if (partNumber == 1) {
            return headTitleId;
        }
        return headTitleId + _PT + partNumber;
    }

    public String getTitleId() {
        if (partNumber == 1)
            return headTitleId;
        return headTitleId + _PT + partNumber;
    }

    @NotNull
    public String escapeSlashWithDash() {
        return headTitleId.replace("/", "-");
    }

    @Override
    public int compareTo(final TitleId o) {
        int lexicographicalHeadTitleIdDiff = getHeadTitleId().compareToIgnoreCase(o.getHeadTitleId());
        int partNumberDiff = getPartNumber() - o.getPartNumber();
        return lexicographicalHeadTitleIdDiff == 0 ? partNumberDiff : lexicographicalHeadTitleIdDiff;
    }
}
