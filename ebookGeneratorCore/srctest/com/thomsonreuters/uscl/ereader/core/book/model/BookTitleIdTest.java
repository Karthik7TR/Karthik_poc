package com.thomsonreuters.uscl.ereader.core.book.model;

import org.junit.Assert;
import org.junit.Test;

public final class BookTitleIdTest {
    private static final String TITLE_WITHOUT_VERSION = "uscl/an/title";
    private static final String TITLE_WITHOUT_VERSION_WITH_V = "uscl/an/vtdc";
    private static final String FULL_VERSION = "/v1.0";
    private static final String MAJOR_VERSION = "/v1";

    @Test
    public void testTitleIdVWithFullVersion() {
        String actual = BookTitleId.getTitleIdWithoutVersion(TITLE_WITHOUT_VERSION_WITH_V + FULL_VERSION);
        Assert.assertEquals(TITLE_WITHOUT_VERSION_WITH_V, actual);
    }

    @Test
    public void testTitleIdVWithMajorVersion() {
        String actual = BookTitleId.getTitleIdWithoutVersion(TITLE_WITHOUT_VERSION_WITH_V + MAJOR_VERSION);
        Assert.assertEquals(TITLE_WITHOUT_VERSION_WITH_V, actual);
    }

    @Test
    public void testTitleIdVWithoutVersion() {
        String actual = BookTitleId.getTitleIdWithoutVersion(TITLE_WITHOUT_VERSION_WITH_V);
        Assert.assertEquals(TITLE_WITHOUT_VERSION_WITH_V, actual);
    }

    @Test
    public void testTitleIdWithFullVersion() {
        String actual = BookTitleId.getTitleIdWithoutVersion(TITLE_WITHOUT_VERSION + FULL_VERSION);
        Assert.assertEquals(TITLE_WITHOUT_VERSION, actual);
    }

    @Test
    public void testTitleIdWithMajorVersion() {
        String actual = BookTitleId.getTitleIdWithoutVersion(TITLE_WITHOUT_VERSION + MAJOR_VERSION);
        Assert.assertEquals(TITLE_WITHOUT_VERSION, actual);
    }

    @Test
    public void testTitleIdWithoutVersion() {
        String actual = BookTitleId.getTitleIdWithoutVersion(TITLE_WITHOUT_VERSION);
        Assert.assertEquals(TITLE_WITHOUT_VERSION, actual);
    }
}
