package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.smoketest.SmokeTestController;
import com.thomsonreuters.uscl.ereader.sap.service.SapService;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * Tests for login/logout and authentication.
 */
public final class SmokeTestControllerTest {
    private SmokeTest SMOKE_TEST;
    private List<SmokeTest> SMOKE_TEST_LIST;
    private List<String> APP_NAMES;

    private SmokeTestService mockService;
    private MiscConfigSyncService mockMiscConfigSyncService;

    private SmokeTestController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private SapService sapService;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        mockService = EasyMock.createMock(SmokeTestService.class);
        mockMiscConfigSyncService = EasyMock.createMock(MiscConfigSyncService.class);
        sapService = EasyMock.createMock(SapService.class);

        controller =
            new SmokeTestController(mockMiscConfigSyncService, mockService, sapService, "workstation", "image");

        SMOKE_TEST = new SmokeTest();
        SMOKE_TEST.setName("name");
        SMOKE_TEST.setIsRunning(false);
        SMOKE_TEST.setAddress("123");

        SMOKE_TEST_LIST = new ArrayList<>();
        SMOKE_TEST_LIST.add(SMOKE_TEST);

        APP_NAMES = new ArrayList<>();
        APP_NAMES.add("1");
        APP_NAMES.add("2");

        EasyMock.expect(mockMiscConfigSyncService.getProviewHost()).andReturn(InetAddress.getLocalHost());
        EasyMock.expect(mockMiscConfigSyncService.getNovusEnvironment()).andReturn(NovusEnvironment.Client);
        EasyMock
            .expect(
                mockService.getApplicationStatus(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
            .andReturn(SMOKE_TEST);
        EasyMock
            .expect(
                mockService.getApplicationStatus(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
            .andReturn(SMOKE_TEST);
        EasyMock.expect(mockService.testMQConnection()).andReturn(SMOKE_TEST);
        EasyMock.expect(mockService.testNovusAvailability()).andReturn(Arrays.asList(SMOKE_TEST));
        EasyMock.expect(mockService.testConnection()).andReturn(SMOKE_TEST);
        EasyMock.expect(mockService.getRunningApplications()).andReturn(APP_NAMES);
        EasyMock.expect(mockService.getCIServerStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getCIApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getTestServerStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getTestApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getQAServerStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getQAApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getLowerEnvDatabaseServerStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getProdServerStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getProdApplicationStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getProdDatabaseServerStatuses()).andReturn(SMOKE_TEST_LIST);
        EasyMock.expect(mockService.getSMTPStatus()).andReturn(SMOKE_TEST);

        EasyMock.replay(mockMiscConfigSyncService);
        EasyMock.replay(mockService);
    }

    @Test
    public void testInboundGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_SMOKE_TEST);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_SMOKE_TEST, mav.getViewName());

        EasyMock.verify(mockMiscConfigSyncService);
        EasyMock.verify(mockService);
    }
}
