package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractor;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewXMLRequestCallback;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Component tests for ProviewClientImpl.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public final class ProviewClientImplTest
{
    // private static final Logger LOG = LogManager.getLogger(ProviewClientImplTest.class);

    private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
    private static InetAddress PROVIEW_HOST;
    private static Map<String, String> urlParameters = new HashMap<>();
    private String allGroupsUriTemplate = "http://{proviewHost}/v1/group/{groupId}/{groupVersionNumber}";
    private String getTitlesUriTemplate = "/v1/titles/uscl/all";
    private ProviewClientImpl proviewClient;
    private RestTemplate mockRestTemplate;
    private ResponseEntity<?> mockResponseEntity;
    private HttpHeaders mockHeaders;
    private ProviewRequestCallbackFactory mockRequestCallbackFactory;
    private ProviewResponseExtractorFactory mockResponseExtractorFactory;
    private ProviewRequestCallback mockRequestCallback;
    private ProviewXMLRequestCallback mockXMLRequestCallback;
    private ProviewResponseExtractor mockResponseExtractor;

    @Before
    public void setUp() throws Exception
    {
        proviewClient = new ProviewClientImpl();
        PROVIEW_HOST = InetAddress.getLocalHost();

        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        mockRequestCallback = new ProviewRequestCallback();
        mockXMLRequestCallback = new ProviewXMLRequestCallback();
        mockResponseExtractor = new ProviewResponseExtractor();
        mockRestTemplate = EasyMock.createMock(RestTemplate.class);
        mockRequestCallbackFactory = EasyMock.createMock(ProviewRequestCallbackFactory.class);
        mockResponseExtractorFactory = EasyMock.createMock(ProviewResponseExtractorFactory.class);
        proviewClient.setRestTemplate(mockRestTemplate);
        proviewClient.setProviewRequestCallbackFactory(mockRequestCallbackFactory);
        proviewClient.setProviewResponseExtractorFactory(mockResponseExtractorFactory);
        proviewClient.setProviewHost(PROVIEW_HOST);
        mockResponseEntity = EasyMock.createMock(ResponseEntity.class);
        mockHeaders = EasyMock.createMock(HttpHeaders.class);
    }

    @After
    public void tearDown()
    {
        //Intentionally left blank
    }

    @Test
    public void testGetAllProviewGroups() throws Exception
    {
        final String response = "<groups><group id=\"uscl/abook_testgroup\" status=\"Review\" version=\"v2\">"
            + "<name>Group1</name><type>standard</type><headtitle>uscl/an/abook_testgroup/v1</headtitle>"
            + "<members><subgroup heading=\"2010\"><title>uscl/an/abook_testgroup/v1</title>"
            + "<title>uscl/an/abook_testgroup_pt2/v1</title></subgroup></members></group>"
            + "<group id=\"uscl/abook_testgroup\" status=\"Final\" version=\"v1\"><name>Group1</name>"
            + "<type>standard</type><headtitle>uscl/an/abook_testgroup</headtitle><members><subgroup>"
            + "<title>uscl/an/abook_testgroup</title></subgroup></members></group></groups>";

        proviewClient.setAllGroupsUriTemplate(allGroupsUriTemplate);
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    allGroupsUriTemplate,
                    HttpMethod.GET,
                    mockRequestCallback,
                    mockResponseExtractor,
                    urlParameters))
            .andReturn(response);

        replayAll();
        final String proviewGroups = proviewClient.getAllProviewGroups();
        Assert.assertEquals(response, proviewGroups);
    }

    @Test
    public void testGetProviewGroupContainerById() throws Exception
    {
        final String groupId = "testGroupId";
        final String response = "<groups><group id=\""
            + groupId
            + "\" status=\"Test\" version=\"v1\">"
            + "<name>Test Group</name><type>standard</type><headtitle>testTitleId</headtitle>"
            + "<members><subgroup><title>test1</title><title>test2</title></subgroup></members>"
            + "</group></groups>";

        final String singleGroupUriTemplate = "";
        proviewClient.setSingleGroupUriTemplate(singleGroupUriTemplate);
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("groupId", groupId);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    singleGroupUriTemplate,
                    HttpMethod.GET,
                    mockXMLRequestCallback,
                    mockResponseExtractor,
                    urlParameters))
            .andReturn(response);
        replayAll();

        final String groupContainer = proviewClient.getProviewGroupById(groupId);

        Assert.assertNotNull(groupContainer);
        Assert.assertEquals(response, groupContainer);
    }

    @Test
    public void testGetProviewGroupInfo() throws Exception
    {
        getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber/info";

        proviewClient.setGetGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
                    HttpMethod.GET,
                    mockXMLRequestCallback,
                    mockResponseExtractor,
                    createURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.getProviewGroupInfo("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();
        Assert.assertEquals("", response);
    }

    @Test
    public void testCreateGroup() throws Exception
    {
        getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber";

        proviewClient.setCreateGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
                    HttpMethod.PUT,
                    mockXMLRequestCallback,
                    mockResponseExtractor,
                    createURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.createGroup("uscl/groupTest", "v1", "<group></group>");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testPromoteGroup() throws Exception
    {
        getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber";

        proviewClient.setCreateGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
        proviewClient.setPromoteGroupStatusUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
                    HttpMethod.PUT,
                    mockXMLRequestCallback,
                    mockResponseExtractor,
                    createURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.promoteGroup("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testRemoveGroup() throws Exception
    {
        getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber/status/removed";

        proviewClient.setRemoveGroupStatusUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
                    HttpMethod.PUT,
                    mockXMLRequestCallback,
                    mockResponseExtractor,
                    createURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.removeGroup("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testDeleteGroup() throws Exception
    {
        getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber";

        proviewClient.setCreateGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
        proviewClient.setDeleteGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
                    HttpMethod.DELETE,
                    mockXMLRequestCallback,
                    mockResponseExtractor,
                    createURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.deleteGroup("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testGetLatestProviewTitleInfo() throws Exception
    {
        final String singleTitleTemplate = "/v1/titles/titleId";

        proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("titleId", "uscl/sc/ca_evid");

        final String response =
            "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";
        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                "http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate,
                HttpMethod.GET,
                mockRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn(response);

        replayAll();
        final String titleInfo = proviewClient.getSinglePublishedTitle("uscl/sc/ca_evid");

        Assert.assertEquals(response, titleInfo);
    }

    @Test
    public void testGetSingleTitleGroupDetails() throws Exception
    {
        final String singleTitleTemplate = "/v1/titles/titleId";

        proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("titleId", "uscl/sc/ca_evid");

        final String response =
            "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";
        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                "http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate,
                HttpMethod.GET,
                mockRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn(response);

        replayAll();
        final String groupDetailsList = proviewClient.getSinglePublishedTitle("uscl/sc/ca_evid");

        Assert.assertEquals(response, groupDetailsList);
    }

    @Test
    public void testGetAllLatestProviewTitleInfo() throws Exception
    {
        proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        final String response =
            "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
                HttpMethod.GET,
                mockRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn(response);

        replayAll();
        final String titleInfo = proviewClient.getAllPublishedTitles();
        verifyAll();

        Assert.assertEquals(response, titleInfo);
    }

    @Test
    public void getSingleTitleInfoByVersion() throws Exception
    {
        final String singleTitleByVersionUriTemplate = "/v1/titles/titleId/eBookVersionNumber";

        proviewClient
            .setSingleTitleByVersionUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate);

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("titleId", "uscl/an/coi");
        urlParameters.put("eBookVersionNumber", "v1.0");

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                "http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate,
                HttpMethod.GET,
                mockXMLRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn("");

        replayAll();
        final String response = proviewClient.getSingleTitleInfoByVersion("uscl/an/coi", "v1.0");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testPublishTitle() throws Exception
    {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String fileContents = "Have some content";
        final File tempRootDir = new File(System.getProperty("java.io.tmpdir"));
        tempRootDir.mkdir();
        try
        {
            final File eBook = makeFile(tempRootDir, "tempBookFile", fileContents);

            final String publishTitleUriTemplate = "SomeURI";

            proviewClient.setPublishTitleUriTemplate(publishTitleUriTemplate);

            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
            urlParameters.put("titleId", titleId);
            urlParameters.put("eBookVersionNumber", bookVersion);

            EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
            EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
            EasyMock.expect(
                mockRestTemplate.execute(
                    publishTitleUriTemplate,
                    HttpMethod.PUT,
                    mockRequestCallback,
                    mockResponseExtractor,
                    urlParameters))
                .andReturn("=)");
            EasyMock.expectLastCall(); // mock
                                       // proviewRequestCallback.setEbookInputStream(ebookInputStream);
            replayAll();

            final String response = proviewClient.publishTitle(titleId, bookVersion, eBook);

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
                // The file is in the temporary files directory, not a big deal
            }
        }
    }

    @Test
    public void testPromoteTitle() throws Exception
    {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String promoteTitleUriTemplate = "SomeURI";

        proviewClient.setPromoteTitleUriTemplate(promoteTitleUriTemplate);

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("titleId", titleId);
        urlParameters.put("eBookVersionNumber", bookVersion);

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    promoteTitleUriTemplate,
                    HttpMethod.PUT,
                    mockRequestCallback,
                    mockResponseExtractor,
                    urlParameters))
            .andReturn("=)");
        EasyMock.expectLastCall();
        replayAll();

        final String response = proviewClient.promoteTitle(titleId, bookVersion);

        Assert.assertEquals("=)", response);
    }

    @Test
    public void testRemoveTitle() throws Exception
    {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String removeTitleUriTemplate = "SomeURI";

        proviewClient.setRemoveTitleUriTemplate(removeTitleUriTemplate);

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("titleId", titleId);
        urlParameters.put("eBookVersionNumber", bookVersion);

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    removeTitleUriTemplate,
                    HttpMethod.PUT,
                    mockRequestCallback,
                    mockResponseExtractor,
                    urlParameters))
            .andReturn("=)");
        EasyMock.expectLastCall();
        replayAll();

        final String response = proviewClient.removeTitle(titleId, bookVersion);

        Assert.assertEquals("=)", response);
    }

    @Test
    public void testDeleteTitle() throws Exception
    {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String deleteTitleUriTemplate = "SomeURI";

        proviewClient.setDeleteTitleUriTemplate(deleteTitleUriTemplate);

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("titleId", titleId);
        urlParameters.put("eBookVersionNumber", bookVersion);

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                deleteTitleUriTemplate,
                HttpMethod.DELETE,
                mockRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn("=)");
        EasyMock.expectLastCall();
        replayAll();

        final String response = proviewClient.deleteTitle(titleId, bookVersion);

        Assert.assertEquals("=)", response);
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
        try
        {
            final File file = new File(directory, name);
            file.createNewFile();
            final FileOutputStream out = new FileOutputStream(file);
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

    private Map<String, String> createURLParameters()
    {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParameters.put("groupId", "uscl/groupTest");
        urlParameters.put("groupVersionNumber", "v1");
        return urlParameters;
    }

    private void verifyAll()
    {
        EasyMock.verify(mockRestTemplate);
        EasyMock.verify(mockResponseEntity);
        EasyMock.verify(mockHeaders);
        EasyMock.verify(mockRequestCallbackFactory);
        EasyMock.verify(mockResponseExtractorFactory);
    }

    private void replayAll()
    {
        EasyMock.replay(mockHeaders);
        EasyMock.replay(mockResponseEntity);
        EasyMock.replay(mockRestTemplate);
        EasyMock.replay(mockRequestCallbackFactory);
        EasyMock.replay(mockResponseExtractorFactory);
    }
}
