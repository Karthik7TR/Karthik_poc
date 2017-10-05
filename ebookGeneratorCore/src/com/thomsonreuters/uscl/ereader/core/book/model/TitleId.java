package com.thomsonreuters.uscl.ereader.core.book.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class TitleId {
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

    public int getPartNumber() {
        return partNumber;
    }

    public String getTitleId() {
        if (partNumber == 1)
            return headTitleId;
        return headTitleId + _PT + partNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((headTitleId == null) ? 0 : headTitleId.hashCode());
        result = prime * result + partNumber;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TitleId other = (TitleId) obj;
        if (headTitleId == null) {
            if (other.headTitleId != null)
                return false;
        } else if (!headTitleId.equals(other.headTitleId))
            return false;
        if (partNumber != other.partNumber)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TitleId [headTitleId=" + headTitleId + ", partNumber=" + partNumber + "]";
    }
}
