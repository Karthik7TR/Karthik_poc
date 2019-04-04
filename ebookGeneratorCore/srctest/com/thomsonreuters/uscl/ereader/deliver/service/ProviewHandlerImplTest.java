package com.thomsonreuters.uscl.ereader.deliver.service;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

/**
 * Component tests for ProviewHandlerImpl.
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
@RunWith(MockitoJUnitRunner.class)
public final class ProviewHandlerImplTest {
    // private static final Logger LOG = LogManager.getLogger(ProviewHandlerImplTest.class);

    @InjectMocks
    private ProviewHandlerImpl proviewHandler;

    private static final String GROUP_ID = "uscl/test_group_id";

    @Mock
    private ProviewClient mockProviewClient;
    private GroupDefinition groupDefinition;

    @Mock
    private SupersededProviewHandlerHelper mockSupersededHandler;

    @Before
    public void setUp() {
        proviewHandler.setProviewClient(mockProviewClient);
    }

    @After
    public void tearDown() {
        //Intentionally left blank
    }

    private String getGroupsRequestXml(final GroupDefinition groupDefinition) {
        String buffer = "<group id=\""
            + groupDefinition.getGroupId()
            + "\"><name>"
            + groupDefinition.getName()
            + "</name><type>"
            + groupDefinition.getType()
            + "</type><headtitle>"
            + groupDefinition.getHeadTitle()
            + "</headtitle><members>";
        for (final SubGroupInfo subgroup : groupDefinition.getSubGroupInfoList()) {
            if (subgroup.getHeading() == null) {
                buffer += "<subgroup>";
            } else {
                buffer += "<subgroup heading=\"" + subgroup.getHeading() + "\">";
            }
            for (final String title : subgroup.getTitles()) {
                buffer += "<title>" + title + "</title>";
            }
            buffer += "</subgroup>";
        }
        buffer += "</members></group>";
        return buffer;
    }

    private String getGroupsResponseXml(final GroupDefinition groupDefinition) {
        String buffer = "<group id=\""
            + groupDefinition.getGroupId()
            + "\" status=\""
            + groupDefinition.getStatus()
            + "\" "
            + "version=\"v"
            + groupDefinition.getGroupVersion()
            + "\">"
            + "<name>"
            + groupDefinition.getName()
            + "</name><type>"
            + groupDefinition.getType()
            + "</type><headtitle>"
            + groupDefinition.getHeadTitle()
            + "</headtitle><members>";
        for (final SubGroupInfo subgroup : groupDefinition.getSubGroupInfoList()) {
            if (subgroup.getHeading() == null) {
                buffer += "<subgroup>";
            } else {
                buffer += "<subgroup heading=\"" + subgroup.getHeading() + "\">";
            }
            for (final String title : subgroup.getTitles()) {
                buffer += "<title>" + title + "</title>";
            }
            buffer += "</subgroup>";
        }
        buffer += "</members></group>";
        return buffer;
    }

    private void initGroupDef() {
        groupDefinition = new GroupDefinition();
        groupDefinition.setGroupId(GROUP_ID);
        groupDefinition.setGroupVersion(1L);
        groupDefinition.setHeadTitle("uscl/test/title_id");
        groupDefinition.setName("Group Name");
        groupDefinition.setStatus("test");
        groupDefinition.setType("someType");
        groupDefinition.setSubGroupInfoList(new ArrayList<SubGroupInfo>());
    }

    private void initSubgroupHeading() {
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2017");
        subgroup.addTitle("test1");
        subgroup.addTitle("test2");
        groupDefinition.addSubGroupInfo(subgroup);
    }

    @Test
    public void testGetAllLatestProviewGroupInfo() throws Exception {
        final String response = "<groups><group id=\"uscl/abook_testgroup\" status=\"Review\" version=\"v2\">"
            + "<name>Group1</name><type>standard</type><headtitle>uscl/an/abook_testgroup/v1</headtitle>"
            + "<members><subgroup heading=\"2010\"><title>uscl/an/abook_testgroup/v1</title>"
            + "<title>uscl/an/abook_testgroup_pt2/v1</title></subgroup></members></group>"
            + "<group id=\"uscl/abook_testgroup\" status=\"Final\" version=\"v1\"><name>Group1</name>"
            + "<type>standard</type><headtitle>uscl/an/abook_testgroup</headtitle><members><subgroup>"
            + "<title>uscl/an/abook_testgroup</title></subgroup></members></group></groups>";
        when(mockProviewClient.getAllProviewGroups()).thenReturn(response);

        final List<ProviewGroup> proviewGroups = proviewHandler.getAllLatestProviewGroupInfo();
        assertEquals(1, proviewGroups.size());
        assertEquals((Integer) 2, proviewGroups.get(0).getTotalNumberOfVersions());
    }

    @Test
    public void testGetProviewGroupContainerById() throws Exception {
        initGroupDef();
        initSubgroupHeading();
        final String response = getGroupsResponseXml(groupDefinition);
        when(mockProviewClient.getProviewGroupById(GROUP_ID)).thenReturn(response);


        final ProviewGroupContainer groupContainer = proviewHandler.getProviewGroupContainerById(GROUP_ID);

        assertNotNull(groupContainer);
        assertNotNull(groupContainer.getProviewGroups());
        assertEquals(1, groupContainer.getProviewGroups().size());
    }

    @Test
    public void testGetGroupDefinitionByVersion() throws Exception {
        initGroupDef();
        initSubgroupHeading();
        final String response = getGroupsResponseXml(groupDefinition);

        when(mockProviewClient.getProviewGroupInfo(GROUP_ID, groupDefinition.getProviewGroupVersionString()))
            .thenReturn(response);

        final GroupDefinition groupDef =
            proviewHandler.getGroupDefinitionByVersion(GROUP_ID, groupDefinition.getGroupVersion());
        assertTrue(groupDefinition.equals(groupDef));
    }

    @Test
    public void testBuildRequestBodyNoSubgroup() throws Exception {
        initGroupDef();
        final String expected = getGroupsRequestXml(groupDefinition);
        final String actual = proviewHandler.buildRequestBody(groupDefinition);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroup() throws Exception {
        initGroupDef();

        when(mockProviewClient.createGroup(
             groupDefinition.getGroupId(),
             groupDefinition.getProviewGroupVersionString(),
             getGroupsRequestXml(groupDefinition)))
             .thenReturn("");

        final String response = proviewHandler.createGroup(groupDefinition);

        assertEquals("", response);
    }

    @Test
    public void testPromoteGroup() throws Exception {
        initGroupDef();

        when(mockProviewClient.promoteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
            .thenReturn("");

        final String response =
            proviewHandler.promoteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

        assertEquals("", response);
    }

    @Test
    public void testRemoveGroup() throws Exception {
        initGroupDef();

        when(mockProviewClient.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
            .thenReturn("");

        final String response =
            proviewHandler.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

        assertEquals("", response);
    }

    @Test
    public void testDeleteGroup() throws Exception {
        initGroupDef();

        when(mockProviewClient.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
            .thenReturn("=)");

        final String response =
            proviewHandler.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

        assertEquals("=)", response);
    }

    @Test
    public void testGetLatestProviewTitleInfo() throws Exception {
        final String titleId = "testTileId";
        final String latest = "20200101";
        final String response = "<titles><title id=\""
            + titleId
            + "\" version=\"v1.0\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
            + "<title id=\""
            + titleId
            + "\" version=\"v1.1\" publisher=\"uscl\" "
            + "lastupdate=\"20150509\" status=\"Cleanup\">Test Book Name</title>"
            + "<title id=\""
            + titleId
            + "\" version=\"v2.0\" publisher=\"uscl\" "
            + "lastupdate=\"20150510\" status=\"Cleanup\">Test Book Name</title>"
            + "<title id=\""
            + titleId
            + "\" version=\"v2.1\" publisher=\"uscl\" "
            + "lastupdate=\""
            + latest
            + "\" status=\"Cleanup\">Test Book Name</title></titles>";
        when(mockProviewClient.getSinglePublishedTitle(titleId)).thenReturn(response);

        final ProviewTitleInfo titleInfo = proviewHandler.getLatestProviewTitleInfo(titleId);

        assertEquals(titleId, titleInfo.getTitleId());
        assertEquals(latest, titleInfo.getLastupdate());
    }

    @Test
    public void testGetSingleTitleGroupDetails() throws Exception {
        final String titleId = "testTileId";
        final String response = "<titles><title id=\""
            + titleId
            + "\" version=\"v1.0\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
            + "<title id=\""
            + titleId
            + "\" version=\"v1.1\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
            + "<title id=\""
            + titleId
            + "\" version=\"v2.0\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
            + "<title id=\""
            + titleId
            + "\" version=\"v2.1\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title></titles>";
        when(mockProviewClient.getSinglePublishedTitle(titleId)).thenReturn(response);

        final List<GroupDetails> groupDetailsList = proviewHandler.getSingleTitleGroupDetails(titleId);

        assertEquals(4, groupDetailsList.size());
        assertEquals(titleId, groupDetailsList.get(0).getTitleId());
    }

    @Test
    public void testGetAllLatestProviewTitleInfo() throws Exception {
        final String response = "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\">"
            + "<title id=\"uscl/abadocs/art\" version=\"v1.0\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\"> Handbook of Practical "
            + "Planning for Art Collectors and Their Advisors</title>"
            + "<title id=\"uscl/abadocs/art\" version=\"v1.1\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Review\">Handbook of Practical "
            + "Planning for Art Collectors and Their Advisors </title></titles>";
        when(mockProviewClient.getAllPublishedTitles()).thenReturn(response);

        final List<ProviewTitleInfo> titleInfo = proviewHandler.getAllLatestProviewTitleInfo();

        assertEquals(1, titleInfo.size());
    }

    @Test
    public void testGetAllLatestProviewTitleInfoByMap() throws Exception {
        final Map<String, ProviewTitleContainer> map = new HashMap<>();
        final ProviewTitleContainer groupContainer = new ProviewTitleContainer();
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setVersion("v2");
        final List<ProviewTitleInfo> titleList = new ArrayList<>();
        titleList.add(title);
        groupContainer.setProviewTitleInfos(titleList);
        map.put("testGroupId", groupContainer);

        final List<ProviewTitleInfo> proviewGroups = proviewHandler.getAllLatestProviewTitleInfo(map);
        assertEquals(1, proviewGroups.size());
    }

    @Test
    public void testPublishTitle() throws Exception {
        final String titleId = "testTileId";
        final Version bookVersion = version("v1.2");
        final String fileContents = "Have some content";
        final File tempRootDir = new File(System.getProperty("java.io.tmpdir"));
        tempRootDir.mkdir();
        try {
            final File eBook = makeFile(tempRootDir, "tempBookFile", fileContents);

            when(mockProviewClient.publishTitle(titleId, "v1.2", eBook)).thenReturn("=)");

            final String response = proviewHandler.publishTitle(titleId, bookVersion, eBook);

            assertEquals("=)", response);
        } catch (final Exception e) {
            throw e;
        } finally {
            try { // may fail due to the input stream opened in publishTitle(..)
                FileUtils.deleteDirectory(tempRootDir);
            } catch (final Exception e) {
                //The file is in the temporary files directory, not a big deal
            }
        }
    }

    @Test
    public void testPromoteTitle() throws Exception {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";

        doNothing().when(mockSupersededHandler).markTitleVersionAsSupersededInThread(any(), any(), any());
        when(mockProviewClient.promoteTitle(titleId, bookVersion)).thenReturn(HttpStatus.OK);
        when(mockProviewClient.getAllPublishedTitles()).thenReturn("<titles></titles>");

        final boolean response = proviewHandler.promoteTitle(titleId, bookVersion);

        verify(mockSupersededHandler).markTitleVersionAsSupersededInThread(any(), any(), any());

        assertTrue(response);
    }

    @Test
    public void testPromoteTitleFail() throws Exception {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";

        doNothing().when(mockSupersededHandler).markTitleVersionAsSupersededInThread(any(), any(), any());
        when(mockProviewClient.promoteTitle(titleId, bookVersion)).thenReturn(HttpStatus.FORBIDDEN);

        final boolean response = proviewHandler.promoteTitle(titleId, bookVersion);

        verify(mockSupersededHandler, never()).markTitleVersionAsSupersededInThread(any(), any(), any());

        assertFalse(response);
    }

    @Test
    public void testRemoveTitle() throws Exception {
        final String titleId = "testTileId";
        final Version bookVersion = version("v1.2");

        when(mockProviewClient.removeTitle(titleId, "v1.2")).thenReturn(HttpStatus.OK);

        final boolean response = proviewHandler.removeTitle(titleId, bookVersion);

        assertTrue(response);
    }

    @Test
    public void testDeleteTitle() throws Exception {
        final String titleId = "testTileId";
        final Version bookVersion = version("v1.2");

        when(mockProviewClient.deleteTitle(titleId, "v1.2")).thenReturn(HttpStatus.OK);

        final boolean response = proviewHandler.deleteTitle(titleId, bookVersion);

        assertTrue(response);
    }

    @Test
    public void testHasTitleIdBeenPublishedNoBook() throws Exception {
        final String titleId = "testTileId";
        when(mockProviewClient.getSinglePublishedTitle(titleId)).thenReturn("<title></title>");

        final boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
        assertFalse(response);
    }

    @Test
    public void testHasTitleIdBeenPublishedFalse() throws Exception {
        final String titleId = "testTileId";
        when(mockProviewClient.getSinglePublishedTitle(titleId)).thenReturn(
            "<title id=\""
                + titleId
                + "\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>");

        final boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
        assertTrue(!response);
    }

    @Test
    public void testHasTitleIdBeenPublishedTrue() throws Exception {
        final String titleId = "testTileId";
        when(mockProviewClient.getSinglePublishedTitle(titleId)).thenReturn(
            "<title id=\""
                + titleId
                + "\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"final\">Test Book Name</title>");

        final boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
        assertTrue(response);
    }

    @Test
    public void testGetAllLatestProviewGroupInfoByMap() throws Exception {
        final Map<String, ProviewGroupContainer> map = new HashMap<>();
        final ProviewGroupContainer groupContainer = new ProviewGroupContainer();
        final ProviewGroup group = new ProviewGroup();
        group.setGroupVersion("v2");
        final List<ProviewGroup> groupList = new ArrayList<>();
        groupList.add(group);
        groupContainer.setProviewGroups(groupList);
        map.put("testGroupId", groupContainer);

        final List<ProviewGroup> proviewGroups = proviewHandler.getAllLatestProviewGroupInfo(map);
        assertEquals(1, proviewGroups.size());
    }

    /**
     * makeFile( File directory, String name, String content ) helper method to streamline file creation
     *
     * @param directory Location the new file will be created in
     * @param name Name of the new file
     * @param content Content to be written into the new file
     * @return returns a File object directing to the new file returns null if any errors occur
     */
    private File makeFile(final File directory, final String name, final String content) {
        final File file = new File(directory, name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            file.createNewFile();
            out.write(content.getBytes());
            out.flush();
            out.close();
            return file;
        } catch (final Exception e) {
            return null;
        }
    }
}
