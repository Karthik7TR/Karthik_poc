package com.thomsonreuters.uscl.ereader.deliver.service;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;

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
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Component tests for ProviewHandlerImpl.
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public final class ProviewHandlerImplTest
{
    // private static final Logger LOG = LogManager.getLogger(ProviewHandlerImplTest.class);

    private ProviewHandlerImpl proviewHandler;

    private static final String GROUP_ID = "uscl/test_group_id";

    private ProviewClient mockProviewClient;
    private GroupDefinition groupDefinition;

    @Before
    public void setUp()
    {
        proviewHandler = new ProviewHandlerImpl();
        mockProviewClient = EasyMock.createMock(ProviewClient.class);
        proviewHandler.setProviewClient(mockProviewClient);
    }

    @After
    public void tearDown()
    {
        //Intentionally left blank
    }

    private String getGroupsRequestXml(final GroupDefinition groupDefinition)
    {
        String buffer = "<group id=\""
            + groupDefinition.getGroupId()
            + "\"><name>"
            + groupDefinition.getName()
            + "</name><type>"
            + groupDefinition.getType()
            + "</type><headtitle>"
            + groupDefinition.getHeadTitle()
            + "</headtitle><members>";
        for (final SubGroupInfo subgroup : groupDefinition.getSubGroupInfoList())
        {
            if (subgroup.getHeading() == null)
            {
                buffer += "<subgroup>";
            }
            else
            {
                buffer += "<subgroup heading=\"" + subgroup.getHeading() + "\">";
            }
            for (final String title : subgroup.getTitles())
            {
                buffer += "<title>" + title + "</title>";
            }
            buffer += "</subgroup>";
        }
        buffer += "</members></group>";
        return buffer;
    }

    private String getGroupsResponseXml(final GroupDefinition groupDefinition)
    {
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
        for (final SubGroupInfo subgroup : groupDefinition.getSubGroupInfoList())
        {
            if (subgroup.getHeading() == null)
            {
                buffer += "<subgroup>";
            }
            else
            {
                buffer += "<subgroup heading=\"" + subgroup.getHeading() + "\">";
            }
            for (final String title : subgroup.getTitles())
            {
                buffer += "<title>" + title + "</title>";
            }
            buffer += "</subgroup>";
        }
        buffer += "</members></group>";
        return buffer;
    }

    private void initGroupDef()
    {
        groupDefinition = new GroupDefinition();
        groupDefinition.setGroupId(GROUP_ID);
        groupDefinition.setGroupVersion(1L);
        groupDefinition.setHeadTitle("uscl/test/title_id");
        groupDefinition.setName("Group Name");
        groupDefinition.setStatus("test");
        groupDefinition.setType("someType");
        groupDefinition.setSubGroupInfoList(new ArrayList<SubGroupInfo>());
    }

    private void initSubgroupHeading()
    {
        final SubGroupInfo subgroup = new SubGroupInfo();
        subgroup.setHeading("2017");
        subgroup.addTitle("test1");
        subgroup.addTitle("test2");
        groupDefinition.addSubGroupInfo(subgroup);
    }

    @Test
    public void testGetAllLatestProviewGroupInfo() throws Exception
    {
        final String response = "<groups><group id=\"uscl/abook_testgroup\" status=\"Review\" version=\"v2\">"
            + "<name>Group1</name><type>standard</type><headtitle>uscl/an/abook_testgroup/v1</headtitle>"
            + "<members><subgroup heading=\"2010\"><title>uscl/an/abook_testgroup/v1</title>"
            + "<title>uscl/an/abook_testgroup_pt2/v1</title></subgroup></members></group>"
            + "<group id=\"uscl/abook_testgroup\" status=\"Final\" version=\"v1\"><name>Group1</name>"
            + "<type>standard</type><headtitle>uscl/an/abook_testgroup</headtitle><members><subgroup>"
            + "<title>uscl/an/abook_testgroup</title></subgroup></members></group></groups>";
        EasyMock.expect(mockProviewClient.getAllProviewGroups()).andReturn(response);
        EasyMock.replay(mockProviewClient);

        final List<ProviewGroup> proviewGroups = proviewHandler.getAllLatestProviewGroupInfo();
        Assert.assertEquals(1, proviewGroups.size());
        Assert.assertEquals((Integer) 2, proviewGroups.get(0).getTotalNumberOfVersions());
    }

    @Test
    public void testGetProviewGroupContainerById() throws Exception
    {
        initGroupDef();
        initSubgroupHeading();
        final String response = getGroupsResponseXml(groupDefinition);
        EasyMock.expect(mockProviewClient.getProviewGroupById(GROUP_ID)).andReturn(response);
        EasyMock.replay(mockProviewClient);

        final ProviewGroupContainer groupContainer = proviewHandler.getProviewGroupContainerById(GROUP_ID);

        Assert.assertNotNull(groupContainer);
        Assert.assertNotNull(groupContainer.getProviewGroups());
        Assert.assertEquals(1, groupContainer.getProviewGroups().size());
    }

    @Test
    public void testGetGroupDefinitionByVersion() throws Exception
    {
        initGroupDef();
        initSubgroupHeading();
        final String response = getGroupsResponseXml(groupDefinition);

        EasyMock.expect(mockProviewClient.getProviewGroupInfo(GROUP_ID, groupDefinition.getProviewGroupVersionString()))
            .andReturn(response);
        EasyMock.replay(mockProviewClient);

        final GroupDefinition groupDef =
            proviewHandler.getGroupDefinitionByVersion(GROUP_ID, groupDefinition.getGroupVersion());
        Assert.assertTrue(groupDefinition.equals(groupDef));
    }

    @Test
    public void testBuildRequestBodyNoSubgroup() throws Exception
    {
        initGroupDef();
        final String expected = getGroupsRequestXml(groupDefinition);
        final String actual = proviewHandler.buildRequestBody(groupDefinition);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroup() throws Exception
    {
        initGroupDef();

        EasyMock
            .expect(
                mockProviewClient.createGroup(
                    groupDefinition.getGroupId(),
                    groupDefinition.getProviewGroupVersionString(),
                    getGroupsRequestXml(groupDefinition)))
            .andReturn("");
        EasyMock.replay(mockProviewClient);

        final String response = proviewHandler.createGroup(groupDefinition);

        Assert.assertEquals("", response);
    }

    @Test
    public void testPromoteGroup() throws Exception
    {
        initGroupDef();

        EasyMock
            .expect(
                mockProviewClient
                    .promoteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
            .andReturn("");
        EasyMock.replay(mockProviewClient);

        final String response =
            proviewHandler.promoteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

        Assert.assertEquals("", response);
    }

    @Test
    public void testRemoveGroup() throws Exception
    {
        initGroupDef();

        EasyMock.expect(
            mockProviewClient.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
            .andReturn("");
        EasyMock.replay(mockProviewClient);

        final String response =
            proviewHandler.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

        Assert.assertEquals("", response);
    }

    @Test
    public void testDeleteGroup() throws Exception
    {
        initGroupDef();

        EasyMock.expect(
            mockProviewClient.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
            .andReturn("=)");
        EasyMock.replay(mockProviewClient);

        final String response =
            proviewHandler.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

        Assert.assertEquals("=)", response);
    }

    @Test
    public void testGetLatestProviewTitleInfo() throws Exception
    {
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
        EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn(response);
        EasyMock.replay(mockProviewClient);

        final ProviewTitleInfo titleInfo = proviewHandler.getLatestProviewTitleInfo(titleId);

        Assert.assertEquals(titleId, titleInfo.getTitleId());
        Assert.assertEquals(latest, titleInfo.getLastupdate());
    }

    @Test
    public void testGetSingleTitleGroupDetails() throws Exception
    {
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
        EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn(response);
        EasyMock.replay(mockProviewClient);

        final List<GroupDetails> groupDetailsList = proviewHandler.getSingleTitleGroupDetails(titleId);

        Assert.assertEquals(4, groupDetailsList.size());
        Assert.assertEquals(titleId, groupDetailsList.get(0).getTitleId());
    }

    @Test
    public void testGetAllLatestProviewTitleInfo() throws Exception
    {
        final String response = "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\">"
            + "<title id=\"uscl/abadocs/art\" version=\"v1.0\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Cleanup\"> Handbook of Practical "
            + "Planning for Art Collectors and Their Advisors</title>"
            + "<title id=\"uscl/abadocs/art\" version=\"v1.1\" publisher=\"uscl\" "
            + "lastupdate=\"20150508\" status=\"Review\">Handbook of Practical "
            + "Planning for Art Collectors and Their Advisors </title></titles>";
        EasyMock.expect(mockProviewClient.getAllPublishedTitles()).andReturn(response);
        EasyMock.replay(mockProviewClient);

        final List<ProviewTitleInfo> titleInfo = proviewHandler.getAllLatestProviewTitleInfo();

        Assert.assertEquals(1, titleInfo.size());
    }

    @Test
    public void testGetAllLatestProviewTitleInfoByMap() throws Exception
    {
        final Map<String, ProviewTitleContainer> map = new HashMap<>();
        final ProviewTitleContainer groupContainer = new ProviewTitleContainer();
        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setVersion("v2");
        final List<ProviewTitleInfo> titleList = new ArrayList<>();
        titleList.add(title);
        groupContainer.setProviewTitleInfos(titleList);
        map.put("testGroupId", groupContainer);

        final List<ProviewTitleInfo> proviewGroups = proviewHandler.getAllLatestProviewTitleInfo(map);
        Assert.assertEquals(1, proviewGroups.size());
    }

    @Test
    public void testPublishTitle() throws Exception
    {
        final String titleId = "testTileId";
        final Version bookVersion = version("v1.2");
        final String fileContents = "Have some content";
        final File tempRootDir = new File(System.getProperty("java.io.tmpdir"));
        tempRootDir.mkdir();
        try
        {
            final File eBook = makeFile(tempRootDir, "tempBookFile", fileContents);

            EasyMock.expect(mockProviewClient.publishTitle(titleId, "v1.2", eBook)).andReturn("=)");
            EasyMock.replay(mockProviewClient);

            final String response = proviewHandler.publishTitle(titleId, bookVersion, eBook);

            Assert.assertEquals("=)", response);
        }
        catch (final Exception e)
        {
            throw e;
        }
        finally
        {
            try
            { // may fail due to the input stream opened in publishTitle(..)
                FileUtils.deleteDirectory(tempRootDir);
            }
            catch (final Exception e)
            {
                //The file is in the temporary files directory, not a big deal
            }
        }
    }

    @Test
    public void testPromoteTitle() throws Exception
    {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";

        EasyMock.expect(mockProviewClient.promoteTitle(titleId, bookVersion)).andReturn("=)");
        EasyMock.replay(mockProviewClient);

        final String response = proviewHandler.promoteTitle(titleId, bookVersion);

        Assert.assertEquals("=)", response);
    }

    @Test
    public void testRemoveTitle() throws Exception
    {
        final String titleId = "testTileId";
        final Version bookVersion = version("v1.2");

        EasyMock.expect(mockProviewClient.removeTitle(titleId, "v1.2")).andReturn("=)");
        EasyMock.replay(mockProviewClient);

        final String response = proviewHandler.removeTitle(titleId, bookVersion);

        Assert.assertEquals("=)", response);
    }

    @Test
    public void testDeleteTitle() throws Exception
    {
        final String titleId = "testTileId";
        final Version bookVersion = version("v1.2");

        final boolean response = proviewHandler.deleteTitle(titleId, bookVersion);

        Assert.assertEquals(true, response);
    }

    @Test
    public void testHasTitleIdBeenPublishedNoBook() throws Exception
    {
        final String titleId = "testTileId";
        EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn("<title></title>");
        EasyMock.replay(mockProviewClient);

        final boolean reponse = proviewHandler.hasTitleIdBeenPublished(titleId);
        Assert.assertTrue(!reponse);
    }

    @Test
    public void testHasTitleIdBeenPublishedFalse() throws Exception
    {
        final String titleId = "testTileId";
        EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn(
            "<title id=\""
                + titleId
                + "\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>");
        EasyMock.replay(mockProviewClient);

        final boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
        Assert.assertTrue(!response);
    }

    @Test
    public void testHasTitleIdBeenPublishedTrue() throws Exception
    {
        final String titleId = "testTileId";
        EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn(
            "<title id=\""
                + titleId
                + "\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"final\">Test Book Name</title>");
        EasyMock.replay(mockProviewClient);

        final boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
        Assert.assertTrue(response);
    }

    @Test
    public void testGetAllLatestProviewGroupInfoByMap() throws Exception
    {
        final Map<String, ProviewGroupContainer> map = new HashMap<>();
        final ProviewGroupContainer groupContainer = new ProviewGroupContainer();
        final ProviewGroup group = new ProviewGroup();
        group.setGroupVersion("v2");
        final List<ProviewGroup> groupList = new ArrayList<>();
        groupList.add(group);
        groupContainer.setProviewGroups(groupList);
        map.put("testGroupId", groupContainer);

        final List<ProviewGroup> proviewGroups = proviewHandler.getAllLatestProviewGroupInfo(map);
        Assert.assertEquals(1, proviewGroups.size());
    }

    /**
     * makeFile( File directory, String name, String content ) helper method to streamline file creation
     *
     * @param directory Location the new file will be created in
     * @param name Name of the new file
     * @param content Content to be written into the new file
     * @return returns a File object directing to the new file returns null if any errors occur
     */
    private File makeFile(final File directory, final String name, final String content)
    {
        final File file = new File(directory, name);
        try (FileOutputStream out = new FileOutputStream(file))
        {
            file.createNewFile();
            out.write(content.getBytes());
            out.flush();
            out.close();
            return file;
        }
        catch (final Exception e)
        {
            return null;
        }
    }
}
