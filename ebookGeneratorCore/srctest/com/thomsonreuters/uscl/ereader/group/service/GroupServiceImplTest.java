package com.thomsonreuters.uscl.ereader.group.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public final class GroupServiceImplTest {
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_ID = "uscl/an_book_lohisplitnodeinfo";
    private static final String SUBGROUP_NAME = "2015";
    private static final String SUBGROUP_NAME_2 = "2016";
    private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/book_lohisplitnodeinfo";
    private static final String CW_FULLY_QUALIFIED_TITLE_ID = "cw/an/book_lohisplitnodeinfo";
    private static final String PILOT_BOOK_TITLE_ID = "uscl/an/book_pilotBook";
    private static final String EXCEPTION_MESSAGE = "Message";
    private static final String EXCEPTION_TITLE_DOES_NOT_EXIST = "This Title does not exist";
    private static final String INTERNAL_SERVER_ERROR = "500";
    private static final String V_1_MAJOR = "/v1";
    private static final String V_2_MAJOR = "/v2";
    private static final String V1 = "v1.0";
    private static final String V2 = "v2.0";
    private static final String V3 = "v3.0";
    private static final String PT_2 = "_pt2";

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
    private BookDefinitionService mockBookDefinitionService;
    private List<String> splitTitles;
    private ProviewTitleContainer proviewTitleContainer = new ProviewTitleContainer();
    private BookDefinition bookDefinition;

    private List<GroupDefinition> groupDefinitionList;
    private List<PilotBook> pilotBooks;

    @Before
    public void setUp() {
        groupService = new GroupServiceImpl();
        mockProviewHandler = EasyMock.createMock(ProviewHandler.class);
        groupService.setProviewHandler(mockProviewHandler);
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        groupService.setBookDefinitionService(mockBookDefinitionService);

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
        pilotBooks = new ArrayList<>();
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

            final GroupDefinition group = groupService.getGroupInfoByVersion(groupId, 10L);
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

            final GroupDefinition group = groupService.getGroupInfoByVersionAutoDecrement(groupId, 11L);
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

            groupService.getGroupInfoByVersion("uscl/groupT", 11L);
            Assert.fail();
        } catch (final ProviewException ex) {
            Assert.assertEquals(errorMessage, ex.getMessage());
        }

        EasyMock.verify(mockProviewHandler);
    }

    @Test
    public void testBuildGroupDefinitionNoSubgroup() {
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setIsSplitBook(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        final GroupDefinition groupDef;
        try {
            EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(FULLY_QUALIFIED_TITLE_ID))
                    .andReturn(bookDefinition);
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler, mockBookDefinitionService);

            groupDef = groupService.createGroupDefinition(bookDefinition, V1, null);

            Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
            Assert.assertEquals(GROUP_NAME, groupDef.getName());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

            Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
            Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
            Assert.assertEquals(
                FULLY_QUALIFIED_TITLE_ID,
                groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
    }

    @Test
    public void testBuildGroupDefinitionForSplits() {
        bookDefinition.setELooseleafsEnabled(false);
        final String groupId = groupService.getGroupId(bookDefinition);

        final GroupDefinition groupDef;
        try {
            EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(FULLY_QUALIFIED_TITLE_ID))
                    .andReturn(bookDefinition);
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler, mockBookDefinitionService);

            groupDef = groupService.createGroupDefinition(bookDefinition, V1, splitTitles);

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

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
    }

    @Test
    public void testSplitEmptySubgroup() throws Exception {
        bookDefinition.setSubGroupHeading(null);
        final String groupId = groupService.getGroupId(bookDefinition);

        try {
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andThrow(new ProviewRuntimeException("404", "No such groups exist"));
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);

            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, V1, splitTitles);
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
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, V2, splitTitles);
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
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, V2, splitTitles);
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
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, V2, null);
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
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            groupService.createGroupDefinition(bookDefinition, V1, splitTitles);
            Assert.fail();
        } catch (final Exception ex) {
            Assert.assertTrue(ex.getMessage().contains(CoreConstants.EMPTY_GROUP_ERROR_MESSAGE));
        }
        EasyMock.verify(mockProviewHandler);
    }

    @SneakyThrows
    @Test
    public void testDuplicateSubgroupHeading() {
        bookDefinition.setSubGroupHeading("2014");
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setIsSplitBook(false);
        splitTitles = null;
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition group = groupService.createGroupDefinition(bookDefinition, V3, splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        final SubGroupInfo subgroup = group.getSubGroupInfoList().get(1);
        Assert.assertEquals("2015", group.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals("2014", subgroup.getHeading());
        Assert.assertTrue(subgroup.getTitles().contains("uscl/an/book_lohisplitnodeinfo/v3"));
    }

    @Test
    public void testDuplicateSubgroupHeadingSplitBook() {
        bookDefinition.setSubGroupHeading("2014");
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            setUpSimpleBookGroup(mockContainer());

            groupService.createGroupDefinition(bookDefinition, V3, splitTitles);

            Assert.fail();
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
        }

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
    }

    @Test
    public void testSimilarGroupDef() {
        final String groupId = groupService.getGroupId(bookDefinition);
        bookDefinition.setELooseleafsEnabled(false);

        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(FULLY_QUALIFIED_TITLE_ID))
                    .andReturn(bookDefinition);
            EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId))
                .andReturn(groupDefinitionList)
                .times(2);
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(mockContainer());
            EasyMock.replay(mockProviewHandler, mockBookDefinitionService);

            final GroupDefinition lastGroup = groupService.getLastGroup(bookDefinition);
            final GroupDefinition currentGroup =
                groupService.createGroupDefinition(bookDefinition, V2, splitTitles);
            Assert.assertTrue(currentGroup.isSimilarGroup(lastGroup));
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
    }

    @SneakyThrows
    @Test
    public void testGetGroupDefVersionChange() {
        final String subgroupName = "2016";
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V3, splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(subgroupName, groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals(
            "uscl/an/book_lohisplitnodeinfo/v3",
            groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        Assert.assertEquals(
            "uscl/an/book_lohisplitnodeinfo_pt2/v3",
            groupDef.getSubGroupInfoList().get(0).getTitles().get(1));

        assertGroup(groupDef, GROUP_NAME, FULLY_QUALIFIED_TITLE_ID + "/v3", "2015", "2014", 1);
    }

    @SneakyThrows
    @Test
    public void testGetGroupDefGroupNameChangeOnlyChange() {
        final String groupName = "ChangeName";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        assertGroup(groupDef, groupName, FULLY_QUALIFIED_TITLE_ID + "/v2",
                "2015", "2014", 0);
    }

    @SneakyThrows
    @Test
    public void testGetGroupDefGpSubGpNameChange() {
        final String groupName = "GroupChangeName";
        final String subGroupName = "SubGroupChangeName";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setSubGroupHeading(subGroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        assertGroup(groupDef, groupName, FULLY_QUALIFIED_TITLE_ID + "/v2",
                subGroupName, "2014", 0);
    }

    @SneakyThrows
    @Test
    public void testGetGroupDefSubGpNameChangeOnly() {
        final String subGroupName = "SubGroupChangeName";
        bookDefinition.setSubGroupHeading(subGroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, splitTitles);

        EasyMock.verify(mockProviewHandler);
        assertGroup(groupDef, GROUP_NAME, FULLY_QUALIFIED_TITLE_ID + "/v2",
                    subGroupName, "2014", 0);
    }

    /**
     * Going from two split book to three split book
     */
    @SneakyThrows
    @Test
    public void testGetGroupDefSplitChange() {
        bookDefinition.setELooseleafsEnabled(false);
        splitTitles = new ArrayList<>();
        splitTitles.add("uscl/an/book_lohisplitnodeinfo");
        splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
        splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt3");
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.1", splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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
    }

    @Test
    public void testSplitSubgroupNameChangeOnMajorVersionUpdate() {
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_TWO_SUBGROUP());

        try {
            setUpSimpleBookGroup(mockContainer());

            groupService.createGroupDefinition(bookDefinition, V3, splitTitles);

            Assert.fail();
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
        }

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
    }

    @SneakyThrows
    @Test
    public void testGroupNameChangeWithNoSubGroups() {
        final String groupName = "GroupNameChange";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
        Assert.assertEquals(groupName, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
        Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
    }

    /*
     * Single title with one subgroup. Major version change only
     */
    @SneakyThrows
    @Test
    public void testVersionChangeOnly() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setSubGroupHeading("2014");
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

        final SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(0);
        Assert.assertEquals("2014", subgroupInfo.getHeading());
        Assert.assertEquals(2, subgroupInfo.getTitles().size());
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo.getTitles().get(0));
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo.getTitles().get(1));
    }

    @SneakyThrows
    @Test
    public void testGroupSubGroupVersionChange() {
        final String groupName = "change name";
        final String subgroupName = "sub change name";
        bookDefinition.setGroupName(groupName);
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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
    }

    @SneakyThrows
    @Test
    public void testSubGroupVersionChange() {
        final String subgroupName = "sub change name";
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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

    @SneakyThrows
    @Test
    public void testSubGroupsToNoSub() {
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setIsSplitBook(false);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

        Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
    }

    /*
     * Single title with two subgroups. Major version change only
     */
    @SneakyThrows
    @Test
    public void testSingleBookNoSubChangeMajorVersion() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V3, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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
    }

    @SneakyThrows
    @Test
    public void testSingleBookSubgroupNameChange() {
        final String subgroupName = "random name";
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setSubGroupHeading(subgroupName);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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
    }

    protected ProviewTitleContainer mockContainer() {
        final ProviewTitleContainer container = new ProviewTitleContainer();

        final List<ProviewTitleInfo> infos = new ArrayList<>();
        final ProviewTitleInfo info = new ProviewTitleInfo();
        info.setVersion(V1);
        infos.add(info);
        final ProviewTitleInfo info2 = new ProviewTitleInfo();
        info2.setVersion(V2);
        infos.add(info2);
        container.setProviewTitleInfos(infos);
        return container;
    }

    @SneakyThrows
    @Test
    public void testNoToYesSubgroupSingleBook() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
        Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals(
            FULLY_QUALIFIED_TITLE_ID + "/v1",
            groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
    }

    /**
     * User removing subgroups from book definition Previous group has 1 subgroup
     */
    @SneakyThrows
    @Test
    public void testYesToNoSubgroupSingleBook() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setSubGroupHeading(null);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
        Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
    }

    /**
     * User removing subgroups from book definition Previous group has two subgroups
     */
    @SneakyThrows
    @Test
    public void testYesToNoSubgroupSingleBook2() {
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setSubGroupHeading(null);
        groupDefinitionList.add(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
        Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
    }

    /**
     * Converting single title group to split title group
     */
    @SneakyThrows
    @Test
    public void testSingleBookToSplitVersion() {
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_NO_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V1, splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

        Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",
            groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
        Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",
            groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
    }

    @SneakyThrows
    @Test
    public void testSingleBookWithSubgroupToSplitVersion() {
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, splitTitles);

        EasyMock.verify(mockProviewHandler);
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
    }

    /**
     * Changing group from single title to split title with minor update and subgroup name change
     */
    @SneakyThrows
    @Test
    public void testSingleBookWithSubgroupToSplitVersionMinorUpdate() {
        final String subgroupName = "changing it";
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, "v2.2", splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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
    }

    @SneakyThrows
    @Test
    public void testSingleBookWithSubgroupToSplitVersionMajorUpdate() {
        final String subgroupName = "2016";
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V3, splitTitles);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
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
    }

    @SneakyThrows
    @Test
    public void testSplitBookToSingleWithNoSubgroup() {
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_SPLIT_ONE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());

        final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
        Assert.assertNull(subgroupInfo1.getHeading());
        Assert.assertEquals(1, subgroupInfo1.getTitles().size());
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, subgroupInfo1.getTitles().get(0));
    }

    @SneakyThrows
    @Test
    public void testSplitBookToSingleWithSubgroup() {
        final String subgroupName = "sub group name change";
        bookDefinition.setSubGroupHeading(subgroupName);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setIsSplitBook(false);
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setVersion("v1.1");
        final List<ProviewTitleInfo> titleInfoList = new ArrayList<>();
        titleInfoList.add(titleInfo);
        proviewTitleContainer.setProviewTitleInfos(titleInfoList);
        groupDefinitionList.add(GROUP_INFO_SPLIT_ONE_SUBGROUP());
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition groupDef = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + V_2_MAJOR, groupDef.getHeadTitle());
        Assert.assertEquals(GROUP_NAME, groupDef.getName());
        Assert.assertEquals(2, groupDef.getSubGroupInfoList().size());

        final SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
        Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
        Assert.assertEquals(1, subgroupInfo1.getTitles().size());
        Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + V_2_MAJOR, subgroupInfo1.getTitles().get(0));
    }

    @Test
    public void testGroupIdWithContentType() {
        bookDefinition = new BookDefinition();
        final DocumentTypeCode dc = new DocumentTypeCode();
        dc.setAbbreviation("sc");
        bookDefinition.setDocumentTypeCodes(dc);
        final PublisherCode publisherCode = new PublisherCode();
        publisherCode.setId(1L);
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
        publisherCode.setId(1L);
        publisherCode.setName("ucl");
        bookDefinition.setPublisherCodes(publisherCode);
        bookDefinition.setFullyQualifiedTitleId("uscl/book_abcd");

        final String groupId = groupService.getGroupId(bookDefinition);
        Assert.assertEquals("ucl/book_abcd", groupId);
    }

    @SneakyThrows
    @Test
    public void testGetProViewTitlesForGroupNoTitles() {
        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(bookDefinition.getFullyQualifiedTitleId()))
            .andReturn(null);
        EasyMock.replay(mockProviewHandler);

        final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDefinition);

        EasyMock.verify(mockProviewHandler);
        Assert.assertEquals(0, proviewTitleMap.size());
    }

    @SneakyThrows
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
        titleInfo.setVersion(V2);
        titleInfoList.add(titleInfo);

        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(pilotBookTitleId)).andReturn(titleContainer);
        EasyMock.replay(mockProviewHandler);

        final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getPilotBooksForGroup(bookDefinition);

        Assert.assertEquals(1, proviewTitleMap.size());
    }

    @SneakyThrows
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
        proviewTitleInfo.setVersion(V1);
        proviewTitleInfos.add(proviewTitleInfo);
        final ProviewTitleContainer container = new ProviewTitleContainer();
        container.setProviewTitleInfos(proviewTitleInfos);

        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(bookDefinition.getFullyQualifiedTitleId()))
            .andReturn(container);
        EasyMock.replay(mockProviewHandler);

        final Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(bookDefinition);

        EasyMock.verify(mockProviewHandler);
        Assert.assertEquals(1, proviewTitleMap.size());
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
        proviewTitleInfo.setVersion(V1);
        proviewTitleInfos.add(proviewTitleInfo);

        final ProviewTitleInfo proviewTitleInfo2 = new ProviewTitleInfo();
        proviewTitleInfo2.setLastupdate("date");
        proviewTitleInfo2.setPublisher("uscl");
        proviewTitleInfo2.setStatus("Review");
        proviewTitleInfo2.setTitle("Book name");
        proviewTitleInfo2.setTitleId("uscl/an/test");
        proviewTitleInfo2.setTotalNumberOfVersions(2);
        proviewTitleInfo2.setVersion(V2);
        proviewTitleInfos.add(proviewTitleInfo2);

        final ProviewTitleContainer container = new ProviewTitleContainer();
        container.setProviewTitleInfos(proviewTitleInfos);

        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
        splitNodeInfo.setBookDefinition(bookDefinition);
        splitNodeInfo.setBookVersionSubmitted(V2);
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
        proviewTitleInfo.setVersion(V2);
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
        proviewTitleInfo.setVersion(V1);
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
        title.setVersion(V2);
        final List<ProviewTitleInfo> titleList = new ArrayList<>();
        titleList.add(title);

        final ProviewTitleInfo title1 = new ProviewTitleInfo();
        title1.setTitleId("uscl/an/title_id");
        title1.setVersion(V3);
        titleList.add(title1);

        proviewTitleContainer.setProviewTitleInfos(titleList);

        final String titleId = "uscl/an/title_id";

        try {
            EasyMock.expect(mockProviewHandler.getProviewTitleContainer("uscl/an/title_id"))
                .andReturn(proviewTitleContainer);
            EasyMock.replay(mockProviewHandler);

            final List<ProviewTitleInfo> proviewTitleInfo = groupService.getMajorVersionProviewTitles(titleId);
            Assert.assertEquals(BigInteger.valueOf(3L), proviewTitleInfo.get(0).getMajorVersion());
            Assert.assertEquals(2, proviewTitleInfo.size());
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private GroupDefinition GROUP_INFO_PILOT_BOOK() {
        final GroupDefinition group = initializeGroupDefinition();
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle(PILOT_BOOK_TITLE_ID);
        group.getSubGroupInfoList().add(subgroup);
        return group;
    }

    private GroupDefinition GROUP_INFO_PILOT_BOOK_INSUB() {
        final GroupDefinition group = initializeGroupDefinition();
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2015");
        subgroup.addTitle("uscl/an/book_lohisplitnodeinfo/v1");
        subgroup.addTitle(PILOT_BOOK_TITLE_ID);
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
        subgroup.addTitle(PILOT_BOOK_TITLE_ID);
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

    @SneakyThrows
    @Test
    public void testGroupForPilotBook() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading(null);
        bookDefinition.setELooseleafsEnabled(false);
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(true);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().size());
        Assert.assertNull(currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testGroupForPilotBookWithSubgroup() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2015");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(true);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().size());
        Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testGroupForPilotBookInFirstSub() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2015");
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_INSUB());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(true);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().size());
        Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(3, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testGroupForPilotBookWithVersionChange() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_INSUB());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(true);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler);
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
        Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testGroupForPilotBookWithNameChange() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_INSUB());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(true);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
        Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testGroupForPilotBookWithVersion() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        bookDefinition.setELooseleafsEnabled(false);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_PREVIOUS_SUB());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(true);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V3, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
        Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testGroupForDeletedPilotBook() {
        final PilotBook pilotBook = new PilotBook();
        pilotBook.setPilotBookTitleId(PILOT_BOOK_TITLE_ID);
        pilotBooks.add(pilotBook);
        bookDefinition.setPilotBooks(pilotBooks);
        bookDefinition.setSubGroupHeading("2016");
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setELooseleafsEnabled(false);
        final Set<SplitNodeInfo> splitNodes = new HashSet<>();
        bookDefinition.setSplitNodes(splitNodes);
        groupDefinitionList.add(GROUP_INFO_PILOT_BOOK_PREVIOUS_SUB());

        EasyMock.expect(mockProviewHandler.isTitleInProview(PILOT_BOOK_TITLE_ID)).andReturn(false);
        setUpSimpleBookGroup(mockContainer());

        final GroupDefinition currentGroup = groupService.createGroupDefinition(bookDefinition, V2, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        Assert.assertEquals(2, currentGroup.getSubGroupInfoList().size());
        Assert.assertEquals("2016", currentGroup.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(0).getTitles().size());
        Assert.assertEquals("2015", currentGroup.getSubGroupInfoList().get(1).getHeading());
        Assert.assertEquals(1, currentGroup.getSubGroupInfoList().get(1).getTitles().size());
    }

    @SneakyThrows
    @Test
    public void testSetGroupTypeStandard() {
        bookDefinition.setELooseleafsEnabled(false);
        setUpSimpleBookGroup(mockContainer());

        GroupDefinition groupDefinition = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        checkGroupType(groupDefinition, CoreConstants.GROUP_TYPE_STANDARD);
    }

    @SneakyThrows
    @Test
    public void testSetGroupTypeEreference() {
        bookDefinition.setELooseleafsEnabled(true);
        bookDefinition.setFullyQualifiedTitleId(CW_FULLY_QUALIFIED_TITLE_ID);
        setUpCWBookGroup(mockContainer());

        GroupDefinition groupDefinition = groupService.createGroupDefinition(bookDefinition, V1, null);

        EasyMock.verify(mockProviewHandler, mockBookDefinitionService);
        checkGroupType(groupDefinition, CoreConstants.GROUP_TYPE_EREFERENCE);
    }

    @Test
    @SneakyThrows
    public void testSubgroupChangedAfterTitlesWereRemovedFromPreviousSubgroup() {
        groupDefinitionList.add(GROUP_INFO_SPLIT_ONE_SUBGROUP());
        bookDefinition.setSubGroupHeading(SUBGROUP_NAME_2);
        setUpSimpleBookGroup(new ProviewTitleContainer());
        List<String> titles = Arrays.asList(FULLY_QUALIFIED_TITLE_ID, FULLY_QUALIFIED_TITLE_ID + PT_2);

        GroupDefinition groupDefinition = groupService.createGroupDefinition(bookDefinition, V1, titles);
        Assert.assertEquals(1, groupDefinition.getSubGroupInfoList().size());
        Assert.assertEquals(SUBGROUP_NAME_2, groupDefinition.getSubGroupInfoList().get(0).getHeading());
        Assert.assertEquals(titles.stream().map(item -> item + V_1_MAJOR).collect(Collectors.toList()),
                groupDefinition.getSubGroupInfoList().get(0).getTitles());
    }

    @Test
    @SneakyThrows
    public void testCreateGroupSuccess() {
        EasyMock.expect(mockProviewHandler.createGroup(GROUP_INFO_NO_SUBGROUP())).andReturn(StringUtils.EMPTY);
        EasyMock.replay(mockProviewHandler);
        groupService.createGroup(GROUP_INFO_NO_SUBGROUP());
    }

    @Test
    public void testCreateGroupFailure1() {
        final ProviewRuntimeException throwable = new ProviewRuntimeException(INTERNAL_SERVER_ERROR, EXCEPTION_TITLE_DOES_NOT_EXIST);
        try {
            EasyMock.expect(mockProviewHandler.createGroup(GROUP_INFO_NO_SUBGROUP()))
                    .andThrow(throwable);
            EasyMock.replay(mockProviewHandler);
            groupService.createGroup(GROUP_INFO_NO_SUBGROUP());
        } catch (Exception e) {
            Assert.assertEquals(CoreConstants.NO_TITLE_IN_PROVIEW, e.getMessage());
            Assert.assertEquals(throwable, e.getCause());
        }
    }

    @Test
    public void testCreateGroupFailure2() {
        final ProviewRuntimeException throwable = new ProviewRuntimeException(INTERNAL_SERVER_ERROR, EXCEPTION_MESSAGE);
        try {
            EasyMock.expect(mockProviewHandler.createGroup(GROUP_INFO_NO_SUBGROUP()))
                    .andThrow(throwable);
            EasyMock.replay(mockProviewHandler);
            groupService.createGroup(GROUP_INFO_NO_SUBGROUP());
        } catch (Exception e) {
            Assert.assertEquals(EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @SneakyThrows
    private void setUpSimpleBookGroup(ProviewTitleContainer container) {
        final String groupId = groupService.getGroupId(bookDefinition);
        EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(bookDefinition);
        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(FULLY_QUALIFIED_TITLE_ID))
                .andReturn(container);
        EasyMock.replay(mockProviewHandler, mockBookDefinitionService);
    }

    @SneakyThrows
    private void setUpCWBookGroup(ProviewTitleContainer container) {
        final String groupId = groupService.getGroupId(bookDefinition);
        EasyMock.expect(mockProviewHandler.getGroupDefinitionsById(groupId)).andReturn(groupDefinitionList);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(CW_FULLY_QUALIFIED_TITLE_ID))
                .andReturn(bookDefinition);
        EasyMock.expect(mockProviewHandler.getProviewTitleContainer(CW_FULLY_QUALIFIED_TITLE_ID))
                .andReturn(container);
        EasyMock.replay(mockProviewHandler, mockBookDefinitionService);
    }

    private void checkGroupType(final GroupDefinition groupDefinition, final String expectedGroupType) {
        String actualGroupType = groupDefinition.getType();
        Assert.assertEquals(expectedGroupType, actualGroupType);
    }
}
