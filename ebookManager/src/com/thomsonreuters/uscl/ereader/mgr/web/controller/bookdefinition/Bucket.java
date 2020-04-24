package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

public enum Bucket {
    ELOOSELEAFS,
    BOOKS;

    public static String getBucket(final boolean isELooseLeafsEnabled) {
        return isELooseLeafsEnabled ? ELOOSELEAFS.toString() : BOOKS.toString();
    }

    public static boolean isElooseLeafsEnabled(final String bucketName) {
        return ELOOSELEAFS.toString().equals(bucketName);
    }
}
