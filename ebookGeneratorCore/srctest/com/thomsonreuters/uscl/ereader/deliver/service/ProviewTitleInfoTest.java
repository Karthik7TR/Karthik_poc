package com.thomsonreuters.uscl.ereader.deliver.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewTitleInfoTest {
    private ProviewTitleInfo titleInfo;
    private String date = "date";
    private String publisher = "uscl";
    private String status = "review";
    private String title = "Proview Name";
    private String titleId = "uscl/an/titleid";
    private Integer numOfVersions = 1;
    private String proviewVersion = "v1.0";

    @Before
    public void setUp() {
        titleInfo = new ProviewTitleInfo();
    }

    @Test
    public void objectTest() {
        titleInfo.setLastupdate(date);
        titleInfo.setPublisher(publisher);
        titleInfo.setStatus(status);
        titleInfo.setTitle(title);
        titleInfo.setTitleId(titleId);
        titleInfo.setTotalNumberOfVersions(numOfVersions);
        titleInfo.setVersion(proviewVersion);

        Assert.assertEquals(date, titleInfo.getLastupdate());
        Assert.assertEquals(publisher, titleInfo.getPublisher());
        Assert.assertEquals(status, titleInfo.getStatus());
        Assert.assertEquals(title, titleInfo.getTitle());
        Assert.assertEquals(titleId, titleInfo.getTitleId());
        Assert.assertEquals(numOfVersions, titleInfo.getTotalNumberOfVersions());
        Assert.assertEquals(proviewVersion, titleInfo.getVersion());

        Assert.assertNotNull(titleInfo.hashCode());

        final ProviewTitleInfo titleInfo2 = new ProviewTitleInfo();
        Assert.assertFalse(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());
        titleInfo2.setLastupdate(date);
        Assert.assertFalse(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());
        titleInfo2.setPublisher(publisher);
        Assert.assertFalse(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());
        titleInfo2.setStatus(status);
        Assert.assertFalse(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());
        titleInfo2.setTitle(title);
        Assert.assertFalse(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());
        titleInfo2.setTitleId(titleId);
        Assert.assertFalse(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());
        titleInfo2.setTotalNumberOfVersions(numOfVersions);
        titleInfo2.setVersion(proviewVersion);
        Assert.assertTrue(titleInfo2.equals(titleInfo));
        Assert.assertNotNull(titleInfo2.hashCode());

        Assert.assertNotNull(titleInfo.toString());
    }

    @Test
    public void versionTest() {
        titleInfo.setVersion("v1.1");
        // Assert.assertEquals(1, titleInfo.getMajorVersion());
    }
}
