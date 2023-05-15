package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

public enum Bucket {
    BOOKS,
    ELOOSELEAFS,

    PERIODICALS;

    public static String getBucket(final boolean isELooseLeafsEnabled, final boolean isPeriodicalsEnabled) {
        return isELooseLeafsEnabled ? ELOOSELEAFS.toString() : isPeriodicalsEnabled ? PERIODICALS.toString() : BOOKS.toString();
    }

    public static boolean isElooseLeafsEnabled(final String bucketName) {
        return ELOOSELEAFS.toString().equals(bucketName);
    }

    public static boolean isPeriodicalsEnabled(final String bucketName) {
        return PERIODICALS.toString().equals(bucketName);
    }
}
