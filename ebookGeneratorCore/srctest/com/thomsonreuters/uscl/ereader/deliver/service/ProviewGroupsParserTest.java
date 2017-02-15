package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ProviewGroupsParserTest
{
    private static final String GROUP_ID = "groupID";
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_INFO_PILOT_BOOK_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\">"
            + "<name>"
            + GROUP_NAME
            + "</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup>"
            + "<title>uscl/an/book_lohisplitnodeinfo</title><title>uscl/an/book_pilotBook</title></subgroup></members></group>";
    private static final String GROUP_INFO_PILOT_BOOK_INSUB_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\">"
            + "<name>"
            + GROUP_NAME
            + "</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup heading=\"2015\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_pilotBook</title></subgroup></members></group>";
    private static final String GROUP_INFO_PILOT_BOOK_PREVIOUS_SUB_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\">"
            + "<name>"
            + GROUP_NAME
            + "</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup heading=\"2016\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup><subgroup heading=\"2015\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_pilotBook</title></subgroup></members></group>";

    private ProviewGroupsParser parser;

    @Before
    public void setUp()
    {
        parser = new ProviewGroupsParser();
    }

    @Test
    public void testParser()
    {
        final String allGroupsResponse =
            "<groups><group id=\"testGroupID1\" status=\"Test\" version=\"v1\"><name>Test Group Name</name>"
                + "<type>standard</type><headtitle>testHeadTitle</headtitle><members><subgroup heading=\"Subgroup Heading 1\">"
                + "<title>testTitleId/v1</title><title>testTitleId_pt2/v1</title></subgroup></members></group>"
                + "<group id=\"testGroupID2\" status=\"Test\" version=\"v2\"><name>Test Group Name</name>"
                + "<type>standard</type><headtitle>testHeadTitle2</headtitle><members><subgroup heading=\"Subgroup Heading 2\">"
                + "<title>testTitleId/v1</title><title>testTitleId_pt2/v1</title></subgroup></members></group></groups>";
        final Map<String, ProviewGroupContainer> map = parser.process(allGroupsResponse);

        final ProviewGroupContainer container = map.get("testGroupID1");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("testHeadTitle", container.getProviewGroups().get(0).getHeadTitle());
    }
}
