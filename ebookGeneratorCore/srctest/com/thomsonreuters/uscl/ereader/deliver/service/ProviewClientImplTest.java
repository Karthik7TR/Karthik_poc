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
import com.thomsonreuters.uscl.ereader.deliver.rest.SimpleProviewResponseExtractor;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * Component tests for ProviewClientImpl.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public final class ProviewClientImplTest {
    private static final String TITLE_ID = "titleId";
    private static final String EBOOK_VERSION_NUMBER = "eBookVersionNumber";
    private static final String PROVIEW_HOST_PARAM = "proviewHost";

    private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
    private static final String USCL = "uscl";
    private static final String ALL = "/all";
    private static InetAddress PROVIEW_HOST;
    private Map<String, String> urlParameters = new HashMap<>();
    private String allGroupsUriTemplate = "http://{proviewHost}/v1/group/{groupId}/{groupVersionNumber}/";
    private String getTitlesUriTemplate = "/v1/titles/";
    private ProviewClientImpl proviewClient;
    private RestTemplate mockRestTemplate;
    private ResponseEntity<?> mockResponseEntity;
    private HttpHeaders mockHeaders;
    private ProviewRequestCallbackFactory mockRequestCallbackFactory;
    private ProviewResponseExtractorFactory mockResponseExtractorFactory;
    private ProviewRequestCallback mockRequestCallback;
    private ProviewXMLRequestCallback mockXMLRequestCallback;
    private ProviewResponseExtractor mockResponseExtractor;
    private SimpleProviewResponseExtractor mockSimpleResponseExtractor;
    private ClientHttpResponse mockClientHttpResponse;

    @Before
    public void setUp() throws Exception {
        proviewClient = new ProviewClientImpl();
        PROVIEW_HOST = InetAddress.getLocalHost();

        urlParameters.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        mockRequestCallback = new ProviewRequestCallback();
        mockXMLRequestCallback = new ProviewXMLRequestCallback();
        mockResponseExtractor = new ProviewResponseExtractor();
        mockSimpleResponseExtractor = new SimpleProviewResponseExtractor();
        mockRestTemplate = EasyMock.createMock(RestTemplate.class);
        mockRequestCallbackFactory = EasyMock.createMock(ProviewRequestCallbackFactory.class);
        mockResponseExtractorFactory = EasyMock.createMock(ProviewResponseExtractorFactory.class);
        proviewClient.setRestTemplate(mockRestTemplate);
        proviewClient.setProviewRequestCallbackFactory(mockRequestCallbackFactory);
        proviewClient.setProviewResponseExtractorFactory(mockResponseExtractorFactory);
        proviewClient.setProviewHost(PROVIEW_HOST);
        mockResponseEntity = EasyMock.createMock(ResponseEntity.class);
        mockHeaders = EasyMock.createMock(HttpHeaders.class);
        mockClientHttpResponse = new MockClientHttpResponse(new byte[]{}, HttpStatus.OK);
    }

    @After
    public void tearDown() {
        //Intentionally left blank
    }

    @Test
    public void testGetAllProviewGroups() throws Exception {
        final String response = "<groups><group id=\"uscl/abook_testgroup\" status=\"Review\" version=\"v2\">"
            + "<name>Group1</name><type>standard</type><headtitle>uscl/an/abook_testgroup/v1</headtitle>"
            + "<members><subgroup heading=\"2010\"><title>uscl/an/abook_testgroup/v1</title>"
            + "<title>uscl/an/abook_testgroup_pt2/v1</title></subgroup></members></group>"
            + "<group id=\"uscl/abook_testgroup\" status=\"Final\" version=\"v1\"><name>Group1</name>"
            + "<type>standard</type><headtitle>uscl/an/abook_testgroup</headtitle><members><subgroup>"
            + "<title>uscl/an/abook_testgroup</title></subgroup></members></group></groups>";

        proviewClient.setAllGroupsUriTemplate(allGroupsUriTemplate);
        urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    allGroupsUriTemplate + USCL,
                    HttpMethod.GET,
                    mockRequestCallback,
                    mockResponseExtractor,
                    urlParameters))
            .andReturn(response);

        replayAll();
        final String proviewGroups = proviewClient.getAllProviewGroups(USCL);
        Assert.assertEquals(response, proviewGroups);
    }

    @Test
    public void testGetProviewGroupContainerById() throws Exception {
        final String groupId = "testGroupId";
        final String response = "<groups><group id=\""
            + groupId
            + "\" status=\"Test\" version=\"v1\">"
            + "<name>Test Group</name><type>standard</type><headtitle>testTitleId</headtitle>"
            + "<members><subgroup><title>test1</title><title>test2</title></subgroup></members>"
            + "</group></groups>";

        final String singleGroupUriTemplate = "";
        proviewClient.setSingleGroupUriTemplate(singleGroupUriTemplate);
        urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
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
    public void testGetProviewGroupInfo() throws Exception {
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
                    createGroupURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.getProviewGroupInfo("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();
        Assert.assertEquals("", response);
    }

    @Test
    public void testCreateGroup() throws Exception {
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
                    createGroupURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.createGroup("uscl/groupTest", "v1", "<group></group>");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testPromoteGroup() throws Exception {
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
                    createGroupURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.promoteGroup("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testRemoveGroup() throws Exception {
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
                    createGroupURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.removeGroup("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testDeleteGroup() throws Exception {
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
                    createGroupURLParameters()))
            .andReturn("");

        replayAll();
        final String response = proviewClient.deleteGroup("uscl/groupTest", "v1");
        System.out.println("response " + response);
        verifyAll();

        Assert.assertEquals("", response);
    }

    @Test
    public void testGetLatestProviewTitleInfo() throws Exception {
        final String singleTitleTemplate = "/v1/titles/titleId";

        proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);

        urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
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
    public void testGetSingleTitleGroupDetails() throws Exception {
        final String singleTitleTemplate = "/v1/titles/titleId";

        proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);

        urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
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
    public void testGetAllLatestProviewTitleInfo() throws Exception {
        proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        final String response =
            "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate + USCL + ALL,
                HttpMethod.GET,
                mockRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn(response);

        replayAll();
        final String titleInfo = proviewClient.getAllPublishedTitles(USCL);
        verifyAll();

        Assert.assertEquals(response, titleInfo);
    }

    @Test
    public void getSingleTitleInfoByVersion() throws Exception {
        final String singleTitleByVersionUriTemplate = "/v1/titles/titleId/eBookVersionNumber";

        proviewClient
            .setSingleTitleByVersionUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate);

        final String titleId = "uscl/an/coi";
        final String bookVersion =  "v1.0";

        urlParameters = createTitleVersionUrlParams(titleId, bookVersion);

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
    public void testPublishTitle() throws Exception {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String fileContents = "Have some content";
        final File tempRootDir = new File(System.getProperty("java.io.tmpdir"));
        tempRootDir.mkdir();
        try {
            final File eBook = makeFile(tempRootDir, "tempBookFile", fileContents);

            final String publishTitleUriTemplate = "SomeURI";

            proviewClient.setPublishTitleUriTemplate(publishTitleUriTemplate);

            urlParameters = createTitleVersionUrlParams(titleId, bookVersion);

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
        } catch (final Exception e) {
            throw e;
        } finally {
            try { // may fail due to the input stream opened in publishTitle(..)
                FileUtils.deleteDirectory(tempRootDir);
            } catch (final Exception e) {
                // The file is in the temporary files directory, not a big deal
            }
        }
    }

    @Test
    public void testPromoteTitle() throws Exception {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String promoteTitleUriTemplate = "SomeURI";

        proviewClient.setPromoteTitleUriTemplate(promoteTitleUriTemplate);

        urlParameters = createTitleVersionUrlParams(titleId, bookVersion);

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getSimpleResponseExtractor()).andReturn(mockSimpleResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    promoteTitleUriTemplate,
                    HttpMethod.PUT,
                    mockRequestCallback,
                    mockSimpleResponseExtractor,
                    urlParameters))
            .andReturn(mockClientHttpResponse);
        EasyMock.expectLastCall();
        replayAll();

        final HttpStatus response = proviewClient.promoteTitle(titleId, bookVersion);

        Assert.assertEquals(HttpStatus.OK, response);
    }

    @Test
    public void testRemoveTitle() throws Exception {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String removeTitleUriTemplate = "SomeURI";

        proviewClient.setRemoveTitleUriTemplate(removeTitleUriTemplate);

        urlParameters = createTitleVersionUrlParams(titleId, bookVersion);
        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getSimpleResponseExtractor()).andReturn(mockSimpleResponseExtractor);
        EasyMock
            .expect(
                mockRestTemplate.execute(
                    removeTitleUriTemplate,
                    HttpMethod.PUT,
                    mockRequestCallback,
                    mockSimpleResponseExtractor,
                    urlParameters))
            .andReturn(mockClientHttpResponse);
        EasyMock.expectLastCall();
        replayAll();

        final HttpStatus response = proviewClient.removeTitle(titleId, bookVersion);

        Assert.assertEquals(HttpStatus.OK, response);
    }

    @Test
    public void testDeleteTitle() throws Exception {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String deleteTitleUriTemplate = "SomeURI";

        proviewClient.setDeleteTitleUriTemplate(deleteTitleUriTemplate);

        urlParameters = createTitleVersionUrlParams(titleId, bookVersion);

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getSimpleResponseExtractor()).andReturn(mockSimpleResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                deleteTitleUriTemplate,
                HttpMethod.DELETE,
                mockRequestCallback,
                mockSimpleResponseExtractor,
                urlParameters))
            .andReturn(mockClientHttpResponse);
        EasyMock.expectLastCall();
        replayAll();

        final HttpStatus response = proviewClient.deleteTitle(titleId, bookVersion);

        Assert.assertEquals(HttpStatus.OK, response);
    }

    @Test
    public void testChangeTitleVersionToSuperseded() {
        final String titleId = "testTileId";
        final String bookVersion = "v1.2";
        final String modifySingleTitleWithVersionUriTemplate = "SomeURI";

        proviewClient.setModifySingleTitleWithVersionUriTemplate(modifySingleTitleWithVersionUriTemplate);

        urlParameters = createTitleVersionUrlParams(titleId, bookVersion);

        EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        EasyMock.expect(
            mockRestTemplate.execute(
                modifySingleTitleWithVersionUriTemplate,
                HttpMethod.PUT,
                mockXMLRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn("");
        EasyMock.expectLastCall();
        replayAll();

        final HttpStatus response = proviewClient.changeTitleVersionToSuperseded(titleId, bookVersion);

        Assert.assertEquals(HttpStatus.OK, response);
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

    private Map<String, String> createGroupURLParameters() {
        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParams.put("groupId", "uscl/groupTest");
        urlParams.put("groupVersionNumber", "v1");
        return urlParams;
    }

    private Map<String, String> createTitleVersionUrlParams(final String titleId, final String bookVersion) {
        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        urlParams.put(TITLE_ID, titleId);
        urlParams.put(EBOOK_VERSION_NUMBER, bookVersion);
        return urlParams;
    }

    private void verifyAll() {
        EasyMock.verify(mockRestTemplate);
        EasyMock.verify(mockResponseEntity);
        EasyMock.verify(mockHeaders);
        EasyMock.verify(mockRequestCallbackFactory);
        EasyMock.verify(mockResponseExtractorFactory);
    }

    private void replayAll() {
        EasyMock.replay(mockHeaders);
        EasyMock.replay(mockResponseEntity);
        EasyMock.replay(mockRestTemplate);
        EasyMock.replay(mockRequestCallbackFactory);
        EasyMock.replay(mockResponseExtractorFactory);
    }
}
