package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractor;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import org.easymock.EasyMock;
import org.junit.After;
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
 */
public final class ProviewClientImplTest {
    //private static final Logger LOG = LogManager.getLogger(ProviewClientImplTest.class);

    private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
    private static final String PROVIEW_HOST_PARAM = "proviewHost";
    private static final String USCL = "uscl";
    private static final String ALL = "/all";
    private static InetAddress PROVIEW_HOST;
    private String getTitlesUriTemplate = "/v1/titles/";

    private ProviewClientImpl proviewClient;
    private RestTemplate mockRestTemplate;
    private ResponseEntity<?> mockResponseEntity;
    private HttpHeaders mockHeaders;
    private ProviewRequestCallbackFactory mockRequestCallbackFactory;
    private ProviewResponseExtractorFactory mockResponseExtractorFactory;
    private ProviewRequestCallback mockRequestCallback;
    private ProviewResponseExtractor mockResponseExtractor;

    @Before
    public void setUp() throws Exception {
        PROVIEW_HOST = InetAddress.getLocalHost();
        proviewClient = new ProviewClientImpl();
        mockRequestCallback = new ProviewRequestCallback();
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
    public void tearDown() {
        //Intentionally left blank
    }

    @Test
    public void testGetAllTitlesHappyPath() throws Exception {
        proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
        final String expectedResponse = "YARR!";

        EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
        EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
        EasyMock.expect(
            mockRestTemplate.execute(
                "http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate + USCL + ALL,
                HttpMethod.GET,
                mockRequestCallback,
                mockResponseExtractor,
                urlParameters))
            .andReturn("YARR!");

        replayAll();
        final String response = proviewClient.getAllPublishedTitles(USCL);
        verifyAll();

        assertTrue("Response did not match expected result!", response.equals(expectedResponse));
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
