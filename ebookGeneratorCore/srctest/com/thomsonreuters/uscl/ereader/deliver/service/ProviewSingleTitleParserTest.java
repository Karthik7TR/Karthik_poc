package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewSingleTitleParserTest {
    private ProviewSingleTitleParser parser;

    @Before
    public void setUp() {
        parser = new ProviewSingleTitleParser();
    }

    @Test
    public void testParser() {
        final String response = "<titles><title id=\"uscl/an/book_lohinosubtosub\" "
            + "version=\"v1.0\" lastupdate=\"20160425\" status=\"Final\" "
            + "name=\"Single To Split\"/><title id=\"uscl/an/book_lohinosubtosub\" "
            + "version=\"v1.1\" lastupdate=\"20160426\" status=\"Final\" "
            + "name=\"Single To Split\"/></titles>";

        final List<GroupDetails> groupDetails = parser.process(response);
        Assert.assertEquals(2, groupDetails.size());
        Assert.assertEquals("v1.0", groupDetails.get(0).getBookVersion());
        Assert.assertEquals("v1.1", groupDetails.get(1).getBookVersion());
        Assert.assertEquals("Final", groupDetails.get(1).getBookStatus());
        Assert.assertEquals("Single To Split", groupDetails.get(1).getProviewDisplayName());
        Assert.assertEquals("20160426", groupDetails.get(1).getLastupdate());
    }
}
