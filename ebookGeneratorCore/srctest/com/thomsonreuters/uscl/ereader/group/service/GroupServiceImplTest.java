package com.thomsonreuters.uscl.ereader.group.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class GroupServiceImplTest {
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_ID = "uscl/an_book_lohisplitnodeinfo";
    private static final String SUBGROUP_NAME = "2015";
    private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/book_lohisplitnodeinfo";

    private GroupDefinition GROUP_INFO_SPLIT_ONE_SUBGROUP() {
        final GroupDefinition group = initializeGroupDefinition();
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo_pt2/v1");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private static final String GROUP_INFO_SPLIT_ONE_SUBGROUP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\"><name>"
            + GROUP_NAME
            + "</name><type>standard</type>"
            + "<headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2015\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title>"
            + "</subgroup></members></group>";

    private GroupDefinition GROUP_INFO_SPLIT_TWO_SUBGROUP() {
        final GroupDefinition group = initializeGroupDefinition();
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo/v2");
        SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v2");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo_pt2/v2");
        group.getSubGroupInfoList().add(subgroup);
        subgroup = new SubGroupInfo();
        subgroup.setHeading("2014");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo_pt2/v1");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private static final String GROUP_INFO_SPLIT_TWO_SUBGROUP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\"><name>"
            + GROUP_NAME
            + "</name><type>standard</type>"
            + "<headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle><members><subgroup heading=\"2015\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title>"
            + "</subgroup><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
            + "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";

    private GroupDefinition GROUP_INFO_NO_SUBGROUP() {
        final GroupDefinition group = initializeGroupDefinition();
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo");
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private static final String GROUP_INFO_NO_SUBGROUP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\"><name>"
            + GROUP_NAME
            + "</name><type>standard</type>"
            + "<headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title>"
            + "</subgroup></members></group>";

    private GroupDefinition GROUP_INFO_SINGLE_TITLE_SUBGROUP() {
        final GroupDefinition group = initializeGroupDefinition();
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo/v1");
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2014");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private static final String GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\"><name>"
            + GROUP_NAME
            + "</name><type>standard</type>"
            + "<headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";

    private GroupDefinition GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP() {
        final GroupDefinition group = initializeGroupDefinition();
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo/v2");
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v2");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private static final String GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\"><name>"
            + GROUP_NAME
            + "</name><type>standard</type>"
            + "<headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle><members><subgroup heading=\"2015\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v2</title>"
            + "<title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";

    private GroupDefinition GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP() {
        final GroupDefinition group = initializeGroupDefinition();
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo/v2");
        SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v2");
        group.getSubGroupInfoList().add(subgroup);
        subgroup = new SubGroupInfo();
        subgroup.setHeading("2014");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private static final String GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\"><name>"
            + GROUP_NAME
            + "</name><type>standard</type>"
            + "<headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle><members><subgroup heading=\"2015\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup><subgroup heading=\"2014\">"
            + "<title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";

    private List<GroupDefinition> MULTIPLE_GROUP_INFO() {
        final List<GroupDefinition> groupList = new ArrayList<>();
        GroupDefinition group = initializeGroupDefinition();
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo");
        SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo");
        group.getSubGroupInfoList().add(subgroup);
        groupList.add(group);
        group = initializeGroupDefinition();
        group.setGroupVersion(2L);
        group.setHeadTitle("uscl/an/book_lohisplitnodeinfo");
        subgroup = new SubGroupInfo();
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo");
        group.getSubGroupInfoList().add(subgroup);
        groupList.add(group);
        return groupList;
    }

    private static final String MULTIPLE_GROUP_INFO_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><groups>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v1\">"
            + "<name>"
            + GROUP_NAME
            + "</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup>"
            + "<title>uscl/an/book_lohisplitnodeinfo</title></subgroup></members></group>"
            + "<group id=\""
            + GROUP_ID
            + "\" status=\"Review\" version=\"v2\">"
            + "<name>"
            + GROUP_NAME
            + "</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup>"
            + "<title>uscl/an/book_lohisplitnodeinfo</title></subgroup></members></group></groups>";

    private GroupServiceImpl groupService;
    private ProviewHandler mockProviewHandler;
    private List<String> splitTitles;
    private ProviewTitleContainer proviewTitleContainer = new ProviewTitleContainer();
    private BookDefinition bookDefinition;

    private List<GroupDefinition> groupDefinitionList;

    @Before
    public void setUp() throws Exception {
        groupService = new GroupServiceImpl();
        mockProviewHandler = EasyMock.createMock(ProviewHandler.class);
        groupService.setProviewHandler(mockProviewHandler);

        splitTitles = new ArrayList<>();
        splitTitles.add("uscl/an/book_lohisplitnodeinfo");
        splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");

        bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(FULLY_QUALIFIED_TITLE_ID);
        bookDefinition.setGroupName(GROUP_NAME);
        bookDefinition.setSubGroupHeading(SUBGROUP_NAME);

        final DocumentTypeCode documentTypeCode = new DocumentTypeCode();
        documentTypeCode.setId(Long.parseLong("1"));
        documentTypeCode.setAbbreviation("an");
        documentTypeCode.setName("Analytical");
        bookDefinition.setDocumentTypeCodes(documentTypeCode);

        final PublisherCode publisherCode = new PublisherCode();
        publisherCode.setId(1L);
        publisherCode.setName("uscl");
        bookDefinition.setPublisherCodes(publisherCode);

        bookDefinition.setIsSplitBook(true);

        groupDefinitionList = new ArrayList<>();
    }

    @Test
    public void testTitleWithVersion() {
        Assert.assertTrue(groupService.isTitleWithVersion("us/an/ascl/v1.0"));
        Assert.assertTrue(groupService.isTitleWithVersion("us/an/ascl/v1"));
        Assert.assertTrue(groupService.isTitleWithVersion("us/an/ascl/v10.00"));
        Assert.assertFalse(groupService.isTitleWithVersion("us/an/ascl/va"));
        Assert.assertTrue(groupService.isTitleWithVersion("us/an/vascl/v1"));
    }

    @Test
    public void testGetGroupInfoByVersion() {
        final String groupId = "uscl/b_subgroup";
        final String status = "Review";
        final Long groupVersion = 10L;
        final String groupName = "Group1";
        final String type = "standard";
        final String headTitle = "uscl/an/b_subgroup/v1";
        final String fkjkresult = "<group id=\""
            + groupId
            + "\" status=\""
            + status
            + "\" version=\"v"
            + groupVersion
            + "\">"
            + "<name>"
            + groupName
            + "</name><type>"
            + type
            + "</type><headtitle>"
            + headTitle
            + "</headtitle>"
            + "<members><subgroup heading=\"2013\"><title>uscl/an/b_subgroup/v1</title></subgroup></members></group>";
        final GroupDefinition result = new GroupDefinition();
        result.setGroupId(groupId);
        result.setStatus(status);
        result.setGroupVersion(groupVersion);
        result.setName(groupName);
        result.setType(type);
        result.setHeadTitle(headTitle);
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2013");
        subgroup.addTitle("uscl/an/b_subgroup/v1");
        result.addSubGroupInfo(subgroup);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionByVersion(groupId, 10)).andReturn(result);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition group = groupService.getGroupInfoByVersion(groupId, Long.valueOf(10));
            Assert.assertEquals(groupVersion, group.getGroupVersion());
            Assert.assertEquals(groupId, group.getGroupId());
            Assert.assertEquals(headTitle, group.getHeadTitle());
            Assert.assertEquals(groupName, group.getName());
            Assert.assertEquals(status, group.getStatus());
            Assert.assertEquals(type, group.getType());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetGroupInfoByVersion2() {
        final String groupId = "uscl/b_subgroup";
        final String status = "Review";
        final Long groupVersion = 10L;
        final String groupName = "Group1";
        final String type = "standard";
        final String headTitle = "uscl/an/b_subgroup/v1";
        final String sdfresult = "<group id=\""
            + groupId
            + "\" status=\""
            + status
            + "\" version=\"v"
            + groupVersion
            + "\">"
            + "<name>"
            + groupName
            + "</name><type>"
            + type
            + "</type><headtitle>"
            + headTitle
            + "</headtitle>"
            + "<members><subgroup heading=\"2013\"><title>uscl/an/b_subgroup/v1</title></subgroup></members></group>";
        final GroupDefinition result = new GroupDefinition();
        result.setGroupId(groupId);
        result.setStatus(status);
        result.setGroupVersion(groupVersion);
        result.setName(groupName);
        result.setType(type);
        result.setHeadTitle(headTitle);
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2013");
        subgroup.addTitle("uscl/an/b_subgroup/v1");
        result.addSubGroupInfo(subgroup);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionByVersion(groupId, 11))
                .andThrow(new ProviewRuntimeException("400", "No such group id and version exist"));
            EasyMock.expect(mockProviewHandler.getGroupDefinitionByVersion(groupId, 10)).andReturn(result);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition group = groupService.getGroupInfoByVersionAutoDecrement(groupId, Long.valueOf(11));
            Assert.assertEquals(groupVersion, group.getGroupVersion());
            Assert.assertEquals(groupId, group.getGroupId());
            Assert.assertEquals(headTitle, group.getHeadTitle());
            Assert.assertEquals(groupName, group.getName());
            Assert.assertEquals(status, group.getStatus());
            Assert.assertEquals(type, group.getType());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetGroupInfoByVersion3() {
        final String errorCode = "404";
        final String errorMessage = "error message";

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionByVersion("uscl/groupT", 11L))
                .andThrow(new ProviewRuntimeException(errorCode, errorMessage));
            EasyMock.replay(mockProviewHandler);

            groupService.getGroupInfoByVersion("uscl/groupT", Long.valueOf(11));
            Assert.fail();
        } catch (final ProviewException ex) {
            Assert.assertEquals(errorMessage, ex.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testBuildGroupDefinitionNoSubgroup() {
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        final GroupDefinition groupDef;
        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(null, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testBuildGroupDefinitionForSplits() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final GroupDefinition groupDef;
        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", splitTitles);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v1",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt2/v1",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSplitEmptySubgroup() throws Exception {
        bookDefinition.setSubGroupHeading(null);
        final String groupId = groupService.getGroupId(bookDefinition);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);

            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v1.0", splitTitles);
            Assert.fail();
        } catch (final ProviewException ex) {
            Assert.assertTrue(ex.getMessage().contains("Subgroup name cannot be empty"));
        }
        EasyMock.verify(mockProviewHandler);
    }

    /**
     *
     * Adding subgroup for split title requires previous group to already have subgroups with title(s) Has no previous group.
     */
    @Test
    public void testNoPreviousSubgroupsError() {
        final String groupId = groupService.getGroupId(bookDefinition);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);
            Assert.fail();
        } catch (final Exception ex) {
            Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
        }
        EasyMock.verify(mockProviewHandler);
    }

    /**
     * Adding subgroup for split title requires previous group to already have subgroups with title(s) Previous group only has
     * group with no subgroups
     */
    @Test
    public void testNoPreviousSubgroupsError2() {
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);
            Assert.fail();
        } catch (final Exception ex) {
            Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
        }
        EasyMock.verify(mockProviewHandler);
    }

    /**
     * Adding subgroup for single title requires previous group to already have subgroups with title(s) Previous group only has
     * group with no subgroups
     */
    @Test
    public void testNoPreviousSubgroupsError3() {
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v2.0", null);
            Assert.fail();
        } catch (final Exception ex) {
            Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
        }
        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testEmptyGroup() {
        bookDefinition.setGroupName(null);
        bookDefinition.setSubGroupHeading(null);
        final String groupId = groupService.getGroupId(bookDefinition);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v1.0", splitTitles);
            Assert.fail();
        } catch (final Exception ex) {
            Assert.assertTrue(ex.getMessage().contains(CoreConstants.EMPTY_GROUP_ERROR_MESSAGE));
        }
        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testDuplicateSubgroupHeading() {
        bookDefinition.setSubGroupHeading("2014");
        bookDefinition.setIsSplitBook(false);
        splitTitles = null;
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition group = groupService.createGroupDefinition(bookDefinition, "v3.0", splitTitles);
            final SubGroupInfo subgroup = group.getSubGroupInfoList().get(1);
            Assert.assertEquals("2015", group.getSubGroupInfoList().get(0).getHeading());
            Assert.assertTrue(subgroup.getHeading().equals("2014"));
            Assert.assertTrue(subgroup.getTitles().contains("uscl/an/book_lohisplitnodeinfo/v3"));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testDuplicateSubgroupHeadingSplitBook() {
        bookDefinition.setSubGroupHeading("2014");
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v3.0", splitTitles);
            Assert.fail();
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSimilarGroupDef() {
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andReturn(groupDefinitionList)
                .times(2);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition lastGroup = groupService.getLastGroup(bookDefinition);
            final GroupDefinition currentGroup =
                groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);
            Assert.assertTrue(currentGroup.isSimilarGroup(lastGroup));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetGroupDefVersionChange() {
        final String subgroupName = "2016";
        bookDefinition.setSubGroupHeading(subgroupName);

        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v3.0", splitTitles);

            Assert.assertEquals(subgroupName, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v3",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt2/v3",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(1));

            assertGroup(groupDef, GROUP_NAME, FULLY_QUALIFIED_TITLE_ID + "/v3", "2015", "2014", 1);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetGroupDefGroupNameChangeOnlyChange() {
        final String groupName = "ChangeName";

        bookDefinition.setGroupName(groupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);
            assertGroup(groupDef, groupName, FULLY_QUALIFIED_TITLE_ID + "/v2", "2015", "2014", 0);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetGroupDefGpSubGpNameChange() {
        final String groupName = "GroupChangeName";
        final String subGroupName = "SubGroupChangeName";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setSubGroupHeading(subGroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);
            assertGroup(groupDef, groupName, FULLY_QUALIFIED_TITLE_ID + "/v2", subGroupName, "2014", 0);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetGroupDefSubGpNameChangeOnly() {
        final String subGroupName = "SubGroupChangeName";
        bookDefinition.setSubGroupHeading(subGroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);
            assertGroup(groupDef, GROUP_NAME, FULLY_QUALIFIED_TITLE_ID + "/v2", subGroupName, "2014", 0);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /**
     * Going from two split book to three split book
     */
    @Test
    public void testGetGroupDefSplitChange() {
        splitTitles = new ArrayList<>();
        splitTitles.add("uscl/an/book_lohisplitnodeinfo");
        splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
        splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt3");
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.1", splitTitles);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals("2015", groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(3, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt2/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt3/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(2));

            Assert.assertEquals("2014", groupDef.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(1).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v1",
                groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt2/v1",
                groupDef.getSubGroupInfoList().get(1).getTitles().get(1));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSplitSubgroupNameChangeOnMajorVersionUpdate() {
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, "v3.0", splitTitles);
            Assert.fail();
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupNameChangeWithNoSubGroups() {
        final String groupName = "GroupNameChange";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(groupName, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
            Assert.assertEquals(null, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /*
     * Single title with one subgroup. Major version change only
     */
    @Test
    public void testVersionChangeOnly() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setSubGroupHeading("2014");
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(0);
            Assert.assertEquals("2014", subgroupInfo.getHeading());
            Assert.assertEquals(2, subgroupInfo.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo.getTitles().get(0));
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo.getTitles().get(1));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupSubGroupVersionChange() {
        final String groupName = "change name";
        final String subgroupName = "sub change name";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setSubGroupHeading(subgroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(groupName, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(subgroupName, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));

            Assert.assertEquals("2014", groupDef.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(1).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v1",
                groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSubGroupVersionChange() {
        final String subgroupName = "sub change name";
        bookDefinition.setSubGroupHeading(subgroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(subgroupName, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));

            Assert.assertEquals("2014", groupDef.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(1).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v1",
                groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testLastGroup() {
        final String groupId = groupService.getGroupId(bookDefinition);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(MULTIPLE_GROUP_INFO());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.getLastGroup(bookDefinition);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(Long.valueOf(2), groupDef.getGroupVersion());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(0);
            Assert.assertNull(subgroupInfo.getHeading());
            Assert.assertEquals(1, subgroupInfo.getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, subgroupInfo.getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testAllGroupsVersionDescendingForBook() {
        final String groupId = groupService.getGroupId(bookDefinition);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(MULTIPLE_GROUP_INFO());
            EasyMock.replay(mockProviewHandler);

            final List<GroupDefinition> groups = groupService.getGroups(bookDefinition);
            Assert.assertEquals(2, groups.size());

            // Check order
            final GroupDefinition group1 = groups.get(0);
            Assert.assertEquals(Long.valueOf(2), group1.getGroupVersion());
            final GroupDefinition group2 = groups.get(1);
            Assert.assertEquals(Long.valueOf(1), group2.getGroupVersion());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSubGroupsToNoSub() {
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(null, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /*
     * Single title with two subgroups. Major version change only
     */
    @Test
    public void testSingleBookNoSubChangeMajorVersion() {
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v3.0", null);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
            Assert.assertEquals(SUBGROUP_NAME, subgroupInfo1.getHeading());
            Assert.assertEquals(2, subgroupInfo1.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3", subgroupInfo1.getTitles().get(0));
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo1.getTitles().get(1));

            final SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
            Assert.assertEquals("2014", subgroupInfo2.getHeading());
            Assert.assertEquals(1, subgroupInfo2.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSingleBookSubgroupNameChange() {
        final String subgroupName = "random name";
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setSubGroupHeading(subgroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
            Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
            Assert.assertEquals(1, subgroupInfo1.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo1.getTitles().get(0));

            final SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
            Assert.assertEquals("2014", subgroupInfo2.getHeading());
            Assert.assertEquals(1, subgroupInfo2.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    protected ProviewTitleContainer mockContainer() {
        final ProviewTitleContainer container = new ProviewTitleContainer();

        final List<ProviewTitleInfo> infos = new ArrayList<>();
        final ProviewTitleInfo info = new ProviewTitleInfo();
        info.setVersion("v1.0");
        infos.add(info);
        final ProviewTitleInfo info2 = new ProviewTitleInfo();
        info2.setVersion("v2.0");
        infos.add(info2);
        container.setProviewTitleInfos(infos);
        return container;
    }

    @Test
    public void testNoToYesSubgroupSingleBook() {
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
            Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                FULLY_QUALIFIED_TITLE_ID + "/v1",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /**
     * User removing subgroups from book definition Previous group has 1 subgroup
     */
    @Test
    public void testYesToNoSubgroupSingleBook() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setSubGroupHeading(null);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
            Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /**
     * User removing subgroups from book definition Previous group has two subgroups
     */
    @Test
    public void testYesToNoSubgroupSingleBook2() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setSubGroupHeading(null);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
            Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /**
     * Converting single title group to split title group
     */
    @Test
    public void testSingleBookToSplitVersion() {
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", splitTitles);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v1",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt2/v1",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSingleBookWithSubgroupToSplitVersion() {
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.0", splitTitles);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(3, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo_pt2/v2",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
            Assert.assertEquals(
                "uscl/an/book_lohisplitnodeinfo/v1",
                groupDef.getSubGroupInfoList().get(0).getTitles().get(2));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    /**
     * Changing group from single title to split title with minor update and subgroup name change
     */
    @Test
    public void testSingleBookWithSubgroupToSplitVersionMinorUpdate() {
        final String subgroupName = "changing it";
        bookDefinition.setSubGroupHeading(subgroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.2", splitTitles);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
            Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
            Assert.assertEquals(2, subgroupInfo1.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo1.getTitles().get(0));
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2", subgroupInfo1.getTitles().get(1));

            final SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
            Assert.assertEquals(SUBGROUP_NAME, subgroupInfo2.getHeading());
            Assert.assertEquals(1, subgroupInfo2.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSingleBookWithSubgroupToSplitVersionMajorUpdate() {
        final String subgroupName = "2016";
        bookDefinition.setSubGroupHeading(subgroupName);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v3.0", splitTitles);

            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
            Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
            Assert.assertEquals(2, subgroupInfo1.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3", subgroupInfo1.getTitles().get(0));
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v3", subgroupInfo1.getTitles().get(1));

            final SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
            Assert.assertEquals(SUBGROUP_NAME, subgroupInfo2.getHeading());
            Assert.assertEquals(2, subgroupInfo2.getTitles().size());
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo2.getTitles().get(0));
            Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(1));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSplitBookToSingleWithNoSubgroup() {
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        groupDefinitionList.add(GROUP_INFO_SPLIT_ONE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
            Assert.assertNull(subgroupInfo1.getHeading());
            Assert.assertEquals(1, subgroupInfo1.getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, subgroupInfo1.getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testSplitBookToSingleWithSubgroup() {
        final String subgroupName = "sub group name change";
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setVersion("v1.1");
        final List<ProviewTitleInfo> titleInfoList = new ArrayList<>();
        titleInfoList.add(titleInfo);
        proviewTitleContainer.setProviewTitleInfos(titleInfoList);

        groupDefinitionList.add(GROUP_INFO_SPLIT_ONE_SUBGROUP());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
            Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
            Assert.assertEquals(1, subgroupInfo1.getTitles().size());
            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", subgroupInfo1.getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupIdWithContentType() {
        bookDefinition = new BookDefinition();
        final DocumentTypeCode dc = new DocumentTypeCode();
        dc.setAbbreviation("sc");
        bookDefinition.setDocumentTypeCodes(dc);
        final PublisherCode publisherCode = new PublisherCode();
        publisherCode.setId(Long.valueOf(1));
        publisherCode.setName("ucl");
        bookDefinition.setPublisherCodes(publisherCode);
        bookDefinition.setFullyQualifiedTitleId("uscl/sc/book_abcd");

        final String groupId = groupService.getGroupId(bookDefinition);
        Assert.assertEquals("ucl/sc_book_abcd", groupId);
    }

    @Test
    public void testGroupIdWithNoContentType() {
        bookDefinition = new BookDefinition();
        final PublisherCode publisherCode = new PublisherCode();
        publisherCode.setId(Long.valueOf(1));
        publisherCode.setName("ucl");
        bookDefinition.setPublisherCodes(publisherCode);
        bookDefinition.setFullyQualifiedTitleId("uscl/book_abcd");

        final String groupId = groupService.getGroupId(bookDefinition);
        Assert.assertEquals("ucl/book_abcd", groupId);
    }

    @Test
    public void testGetProViewTitlesForGroupNoTitles() {
        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(bookDefinition.getFullyQualifiedTitleId()))
                .andReturn(null);
            EasyMock.replay(mockProviewHandler);

            final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDefinition);
            Assert.assertEquals(0, proviewTitleMap.size());
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetPilotBooksForGroup() {
        final String pilotBookTitleId = "titleID";
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(pilotBookTitleId);
        final List<PilotBook> pilotBookList = new ArrayList<>();
        pilotBookList.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBookList);

        final ProviewTitleContainer titleContainer = new ProviewTitleContainer();
        final List<ProviewTitleInfo> titleInfoList = new ArrayList<>();
        titleContainer.setProviewTitleInfos(titleInfoList);
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setVersion("v2");
        titleInfoList.add(titleInfo);
        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(pilotBookTitleId)).andReturn(titleContainer);
            EasyMock.replay(mockProviewHandler);

            final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getPilotBooksForGroup(bookDefinition);
            Assert.assertEquals(1, proviewTitleMap.size());
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetProViewTitlesForGroupOneTitles() {
        final List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
        final ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setLastupdate("date");
        proviewTitleInfo.setPublisher("uscl");
        proviewTitleInfo.setStatus("Review");
        proviewTitleInfo.setTitle("Book name");
        proviewTitleInfo.setTitleId("uscl/an/test");
        proviewTitleInfo.setTotalNumberOfVersions(2);
        proviewTitleInfo.setVersion("v1.0");
        proviewTitleInfos.add(proviewTitleInfo);

        final ProviewTitleContainer container = new ProviewTitleContainer();
        container.setProviewTitleInfos(proviewTitleInfos);
        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(bookDefinition.getFullyQualifiedTitleId()))
                .andReturn(container);
            EasyMock.replay(mockProviewHandler);

            final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDefinition);
            Assert.assertEquals(1, proviewTitleMap.size());
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetProViewTitlesForGroupSplitTitles() {
        List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
        ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setLastupdate("date");
        proviewTitleInfo.setPublisher("uscl");
        proviewTitleInfo.setStatus("Review");
        proviewTitleInfo.setTitle("Book name");
        proviewTitleInfo.setTitleId("uscl/an/test");
        proviewTitleInfo.setTotalNumberOfVersions(2);
        proviewTitleInfo.setVersion("v1.0");
        proviewTitleInfos.add(proviewTitleInfo);

        final ProviewTitleInfo proviewTitleInfo2 = new ProviewTitleInfo();
        proviewTitleInfo2.setLastupdate("date");
        proviewTitleInfo2.setPublisher("uscl");
        proviewTitleInfo2.setStatus("Review");
        proviewTitleInfo2.setTitle("Book name");
        proviewTitleInfo2.setTitleId("uscl/an/test");
        proviewTitleInfo2.setTotalNumberOfVersions(2);
        proviewTitleInfo2.setVersion("v2.0");
        proviewTitleInfos.add(proviewTitleInfo2);

        final ProviewTitleContainer container = new ProviewTitleContainer();
        container.setProviewTitleInfos(proviewTitleInfos);

        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
        splitNodeInfo.setBookDefinition(bookDefinition);
        splitNodeInfo.setBookVersionSubmitted("v2.0");
        splitNodeInfo.setSpitBookTitle("uscl/an/test_pt2");
        splitNodes.add(splitNodeInfo);
        bookDefinition.setSplitNodes(splitNodes);

        proviewTitleInfos = new ArrayList<>();
        proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setLastupdate("date");
        proviewTitleInfo.setPublisher("uscl");
        proviewTitleInfo.setStatus("Review");
        proviewTitleInfo.setTitle("Book name");
        proviewTitleInfo.setTitleId("uscl/an/test_pt2");
        proviewTitleInfo.setTotalNumberOfVersions(2);
        proviewTitleInfo.setVersion("v2.0");
        proviewTitleInfos.add(proviewTitleInfo);

        final ProviewTitleContainer container2 = new ProviewTitleContainer();
        container2.setProviewTitleInfos(proviewTitleInfos);

        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/test_pt2")).andReturn(container2);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(bookDefinition.getFullyQualifiedTitleId()))
                .andReturn(container);
            EasyMock.replay(mockProviewHandler);

            final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDefinition);
            Assert.assertEquals(3, proviewTitleMap.size());
            Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test/v1"));
            Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test/v2"));
            Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test_pt2/v2"));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGetProViewTitlesForGroupPilotBook() {
        bookDefinition.setFullyQualifiedTitleId("uscl/an/test_waspilot");
        List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
        ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setLastupdate("date");
        proviewTitleInfo.setPublisher("uscl");
        proviewTitleInfo.setStatus("Review");
        proviewTitleInfo.setTitle("Book name");
        proviewTitleInfo.setTitleId("uscl/an/test_waspilot");
        proviewTitleInfo.setTotalNumberOfVersions(2);
        proviewTitleInfo.setVersion("v1.0");
        proviewTitleInfos.add(proviewTitleInfo);

        final ProviewTitleContainer container = new ProviewTitleContainer();
        container.setProviewTitleInfos(proviewTitleInfos);

        proviewTitleInfos = new ArrayList<>();
        proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setLastupdate("date");
        proviewTitleInfo.setPublisher("uscl");
        proviewTitleInfo.setStatus("Review");
        proviewTitleInfo.setTitle("Book name");
        proviewTitleInfo.setTitleId("uscl/an/test");
        proviewTitleInfo.setTotalNumberOfVersions(2);
        proviewTitleInfo.setVersion("v10");
        proviewTitleInfos.add(proviewTitleInfo);

        final ProviewTitleContainer container2 = new ProviewTitleContainer();
        container2.setProviewTitleInfos(proviewTitleInfos);

        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(bookDefinition.getFullyQualifiedTitleId()))
                .andReturn(container);
            EasyMock.replay(mockProviewHandler);

            final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDefinition);
            Assert.assertEquals(1, proviewTitleMap.size());
            Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test_waspilot/v1"));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockProviewHandler);
    }

    private void assertGroup(
        final GroupDefinition groupDef,
        final String groupName,
        final String headTitle,
        final String firstSubgroupName,
        final String secondSubgroup,
        final Integer startIndex) {
        Assert.assertEquals(headTitle, groupDef.getHeadTitle());
        Assert.assertEquals(groupName, groupDef.getName());
        Assert.assertEquals(startIndex + 2, groupDef.getSubGroupInfoList().size());

        final SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(startIndex);
        Assert.assertEquals(firstSubgroupName, subgroupInfo.getHeading());
        Assert.assertEquals(2, subgroupInfo.getTitles().size());
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo.getTitles().get(0));
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2", subgroupInfo.getTitles().get(1));

        final SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(startIndex + 1);
        Assert.assertEquals(secondSubgroup, subgroupInfo2.getHeading());
        Assert.assertEquals(2, subgroupInfo2.getTitles().size());
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(0));
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1", subgroupInfo2.getTitles().get(1));
    }

    @Test
    public void testMajorVersion() {
        proviewTitleContainer = new ProviewTitleContainer();
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setTitleId("uscl/an/title_id");
        title.setVersion("v2");
        final List<ProviewTitleInfo> titleList = new ArrayList<>();
        titleList.add(title);

        final ProviewTitleInfo title1 = new ProviewTitleInfo();
        title1.setTitleId("uscl/an/title_id");
        title1.setVersion("v3");
        titleList.add(title1);

        proviewTitleContainer.setProviewTitleInfos(titleList);

        final String titleId = "uscl/an/title_id";

        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/title_id"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final List<ProviewTitleInfo> proviewTitleInfo = groupService.getMajorVersionProviewTitles(titleId);
            Assert.assertEquals(Integer.valueOf(3), proviewTitleInfo.get(0).getMajorVersion());
            Assert.assertEquals(1, proviewTitleInfo.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private GroupDefinition GROUP_INFO_PILOT_BOOK() {
        final GroupDefinition group = initializeGroupDefinition();
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle("uscl/an/book_pilotBook");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private GroupDefinition GROUP_INFO_PILOT_BOOK_INSUB() {
        final GroupDefinition group = initializeGroupDefinition();
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle("uscl/an/book_pilotBook");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private GroupDefinition GROUP_INFO_PILOT_BOOK_PREVIOUS_SUB() {
        final GroupDefinition group = initializeGroupDefinition();
        SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2016");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v2");
        group.getSubGroupInfoList().add(subgroup);
        subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle("uscl/an/book_pilotBook");
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private GroupDefinition initializeGroupDefinition() {
        final GroupDefinition groupDefinition = new GroupDefinition();
        groupDefinition.setGroupId(GROUP_ID);
        groupDefinition.setStatus("Review");
        groupDefinition.setName(GROUP_NAME);
        groupDefinition.setHeadTitle("uscl/an/book_lohisplitnodeinfo");
        groupDefinition.setProviewGroupVersionString("v1");
        final List<SubGroupInfo> subList = new ArrayList<>();
        groupDefinition.setSubGroupInfoList(subList);
        return groupDefinition;
    }

    @Test
    public void testGroupForPilotBook() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(true);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals(null, currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupForPilotBookWithSubgroup() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2015");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(true);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupForPilotBookInFirstSub() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2015");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_INSUB());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(proviewTitleContainer);
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(true);

            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupForPilotBookWithVersionChange() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_INSUB());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(true);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupForPilotBookWithNameChange() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_INSUB());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(true);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v1.0", null);

            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupForPilotBookWithVersion() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_PREVIOUS_SUB());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(true);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v3.0", null);

            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testGroupForDeletedPilotBook() {
        final String groupId = groupService.getGroupId(bookDefinition);

        final List<PilotBook> pilotBooks = new ArrayList<>();
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId("uscl/an/book_pilotBook");
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);

        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_PREVIOUS_SUB());

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/book_lohisplitnodeinfo"))
                .andReturn(mockContainer());
            EasyMock.expect(mockProviewHandler.isTitleInProview("uscl/an/book_pilotBook")).andReturn(false);
            EasyMock.replay(mockProviewHandler);

            final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, "v2.0", null);

            Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
            Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
            Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }
}
