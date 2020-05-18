package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewHttpResponseErrorHandler;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Integration tests for ProviewClientImpl. These do not get run during CI
 * builds.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 */
public final class ProviewClientImplIntegrationTest {
    private static final Logger LOG = LogManager.getLogger(ProviewClientImplIntegrationTest.class);

    private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
    private String getTitlesUriTemplate = "/v1/titles/";
    private String publishTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}";
    private String removeTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}/status/removed";
    private String promoteTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}/status/final";
    private String deleteTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}";
    private String getSingleTitleTemplate = "/v1/titles/{titleId}";
    private String getSingleGroupTemplate = "/v1/group/{groupId}";
    private String validateTitleUriTemplate = "";

    private static final String USCL = "uscl";

    private static final String PROVIEW_USERNAME = "publisher";
    private static final String PROVIEW_PASSWORD = "f9R_zBq37a";
    private static final String PROVIEW_INVALID_USERNAME = "YARR";
    private static final String PROVIEW_INVALID_PASSWORD = "PIRATES!";

    private ProviewClientImpl proviewClient;
    private CloseableAuthenticationHttpClientFactory defaultHttpClient;
    private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
    private ProviewResponseExtractorFactory proviewResponseExtractorFactory;

    @Before
    public void setUp() throws Exception {
        proviewClient = new ProviewClientImpl();
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);

        defaultHttpClient =
            new CloseableAuthenticationHttpClientFactory(PROVIEW_DOMAIN_PREFIX, PROVIEW_USERNAME, PROVIEW_PASSWORD);

        requestFactory.setHttpClient(defaultHttpClient.getCloseableAuthenticationHttpClient());

        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new ProviewMessageConverter<File>());
        restTemplate.setErrorHandler(new ProviewHttpResponseErrorHandler());
        proviewClient.setRestTemplate(restTemplate);
        proviewRequestCallbackFactory = new ProviewRequestCallbackFactory();
        proviewResponseExtractorFactory = new ProviewResponseExtractorFactory();
        proviewClient.setProviewRequestCallbackFactory(proviewRequestCallbackFactory);
        proviewClient.setProviewResponseExtractorFactory(proviewResponseExtractorFactory);
    }

    @After
    public void tearDown() {
        //Intentionally left blank
    }

    @Ignore
    @Test
    public void testGetAllTitlesHappyPath() throws Exception {
        proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
        final String publisherInformation = proviewClient.getAllPublishedTitles(USCL);
        System.out.println(publisherInformation);
        assertTrue(publisherInformation.startsWith("<titles apiversion=\"v1\" publisher=\"uscl\""));
    }

    @Ignore
    @Test
    public void testRemoveTitle() {
        proviewClient.setRemoveTitleUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + removeTitleUriTemplate);
        HttpStatus publisherInformation = null;
        try {
            publisherInformation = proviewClient.removeTitle("uscl/an/blm", "v1.1");
        } catch (final Exception e) {
            if (e.getMessage().contains("Collection doesn't exist")) {
                fail("Expected an exception as title doesn't exist on ProView!");
            }
        }

        System.out.println(publisherInformation);
    }

    @Ignore
    @Test
    public void testPromoteTitle() {
        proviewClient.setPromoteTitleUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + promoteTitleUriTemplate);
        HttpStatus publisherInformation = null;
        try {
            publisherInformation = proviewClient.promoteTitle("uscl/an/an_frcpbkeenantest2", "v2");
        } catch (final Exception e) {
            if (e.getMessage().contains("Collection doesn't exist")) {
                fail("Expected an exception as title doesn't exist on ProView!");
            }
        }

        System.out.println(publisherInformation);
    }

    @Ignore
    @Test
    public void testDeleteTitle() {
        HttpStatus publisherInformation = null;
        proviewClient.setDeleteTitleUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + deleteTitleUriTemplate);
        try {
            publisherInformation = proviewClient.deleteTitle("uscl/an/blm", "v1.0");
        } catch (final Exception e) {
            if (e.getMessage().contains("Collection doesn't exist")) {
                fail("Expected an exception as title doesn't exist on ProView!");
            }
        }

        System.out.println(publisherInformation);
    }

    @Ignore
    @Test
    public void testPublishBookFailsBecauseItAlreadyExistsOnProview() throws Exception {
        proviewClient.setPublishTitleUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + publishTitleUriTemplate);
        final String integrationTestTitleId = "uscl/cr/generator_integration_test";
        final String eBookVersionNumber = "v2";

        final File eBookDirectory = new File("/nas/ebookbuilder/data/");
        final File eBook = new File(eBookDirectory, "proview_client_integration_test.gz");
        try {
            proviewClient.publishTitle(integrationTestTitleId, eBookVersionNumber, eBook);
            fail("Expected an exception related to the title already existing on ProView!");
        } catch (final ProviewRuntimeException e) {
            // expected
        }
    }

    @Ignore
    @Test
    public void testGetAllGroupsHappyPath() throws Exception {
        proviewClient.setProviewHost(InetAddress.getLocalHost());
        proviewClient
            .setAllGroupsUriTemplate("http://" + "proviewpublishing.int.qed.thomsonreuters.com" + "/v1/group/");
        boolean thrown = false;
        try {
            proviewClient.getAllProviewGroups(USCL);
        } catch (final ProviewRuntimeException e) {
            thrown = true;
        }

        Assert.assertFalse(thrown);
    }

    @Ignore
    @Test
    public void testGetSinglePublishedTitle() throws Exception {
        proviewClient.setProviewHost(InetAddress.getLocalHost());
        proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getSingleTitleTemplate);
        try {
            proviewClient.getSinglePublishedTitle("uscl/an/book_lohisplitnodeinfo");
        } catch (final Exception e) {
            if (!e.getMessage().contains("uscl/an/book_lohisplitnodeinfo does not exist")) {
                fail("Expected an exception as title doesn't exist on ProView!");
            }
        }
    }

    @Ignore
    @Test
    public void testCreateGroup() throws Exception {
        proviewClient.setCreateGroupUriTemplate(
            "http://" + "proviewpublishing.int.qed.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}");
        proviewClient.setProviewHostname("proviewpublishing.int.qed.thomsonreuters.com");
        final GroupDefinition groupDefinition = new GroupDefinition();
        groupDefinition.setGroupId("uscl/groupFinalTest");
        groupDefinition.setProviewGroupVersionString("v1");
        groupDefinition.setName("Group Test");
        groupDefinition.setType("standard");
        groupDefinition.setOrder("newerfirst");
        groupDefinition.setHeadTitle("uscl/an/book_lohisplitnodeinfo");

        final List<SubGroupInfo> subGroupInfoList = new ArrayList<>();
        final SubGroupInfo subGroupInfo = new SubGroupInfo();
        subGroupInfo.setHeading("2014");
        final List<String> titleList = new ArrayList<>();
        titleList.add("uscl/an/book_lohisplitnodeinfo/v1");
        titleList.add("uscl/an/book_lohisplitnodeinfo_pt2/v1");
        subGroupInfo.setTitles(titleList);
        subGroupInfoList.add(subGroupInfo);
        groupDefinition.setSubGroupInfoList(subGroupInfoList);

        try {
            String response = proviewClient.createGroup(
                groupDefinition.getGroupId(),
                groupDefinition.getProviewGroupVersionString(),
                "<group></group>"); // See ProviewHanderImpl#buildRequestBody()
            Assert.assertEquals(response.length(), 0);
            proviewClient.setGetGroupUriTemplate(
                "http://" + PROVIEW_DOMAIN_PREFIX + "/v1/group/{groupId}/{groupVersionNumber}/info");
            response = proviewClient.getProviewGroupInfo("uscl/groupTest", "v1");
            Assert.assertEquals(response.length(), 325);
            updateStatusAndDelete(groupDefinition);
        } catch (final ProviewRuntimeException e) {
            e.printStackTrace();
        }
    }

    private void updateStatusAndDelete(final GroupDefinition groupDefinition) throws Exception {
        proviewClient.setRemoveGroupStatusUriTemplate(
            "http://"
                + "proviewpublishing.int.qed.thomsonreuters.com"
                + "/v1/group/{groupId}/{groupVersionNumber}/status/Removed");
        proviewClient.setProviewHostname("proviewpublishing.int.qed.thomsonreuters.com");
        groupDefinition.setGroupId("uscl/groupTest");
        groupDefinition.setProviewGroupVersionString("v1");
        proviewClient.setDeleteGroupUriTemplate(
            "http://" + "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}");

        try {
            String response =
                proviewClient.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());
            Assert.assertEquals(response.contains("Group status changed to Removed"), true);
            response =
                proviewClient.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());
            Assert.assertEquals(response.length(), 0);
        } catch (final ProviewRuntimeException e) {
            e.printStackTrace();
            // expected
        }
    }

    @Ignore
    @Test
    // Not sure if we should test invalid credentials
    public void testGetAllTitlesFailsDueToInvalidCredetials() throws Exception {
        defaultHttpClient = new CloseableAuthenticationHttpClientFactory(
            PROVIEW_DOMAIN_PREFIX,
            PROVIEW_INVALID_USERNAME,
            PROVIEW_INVALID_PASSWORD);

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(defaultHttpClient.getCloseableAuthenticationHttpClient());

        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        proviewClient.setRestTemplate(restTemplate);

        proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

        try {
            proviewClient.getAllPublishedTitles(USCL);
        } catch (final Exception e) {
            fail("Expected an exception as invalid username/password for ProView!");
            System.out.println(e.getMessage()); // expected
        }
    }
}
