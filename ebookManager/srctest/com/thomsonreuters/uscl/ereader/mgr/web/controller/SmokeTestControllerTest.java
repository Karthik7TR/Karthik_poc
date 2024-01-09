package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.net.InetAddress;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.smoketest.SmokeTestController;
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
    private Map<String, List<String>> APP_NAMES;

    private SmokeTestService mockService;
    private MiscConfigSyncService mockMiscConfigSyncService;

    private SmokeTestController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        mockService = EasyMock.createMock(SmokeTestService.class);
        mockMiscConfigSyncService = EasyMock.createMock(MiscConfigSyncService.class);

        controller =
            new SmokeTestController(mockMiscConfigSyncService, mockService, "workstation");

        SMOKE_TEST = new SmokeTest();
        SMOKE_TEST.setName("name");
        SMOKE_TEST.setIsRunning(false);
        SMOKE_TEST.setAddress("123");

        SMOKE_TEST_LIST = new ArrayList<>();
        SMOKE_TEST_LIST.add(SMOKE_TEST);

        List<String> versions = Arrays.asList("a","b", "c");
        APP_NAMES = Stream.of("ci", "test", "qed", "prod").collect(Collectors.toMap(Function.identity(), item -> versions));

        EasyMock.expect(mockMiscConfigSyncService.getProviewHost()).andReturn(InetAddress.getLocalHost());
        EasyMock.expect(mockMiscConfigSyncService.getNovusEnvironment()).andReturn(NovusEnvironment.Client);
        EasyMock.expect(mockService.getRunningApplications()).andReturn(APP_NAMES);
        EasyMock.expect(mockService.getApplicationStatuses())
            .andReturn(Collections.singletonMap("appTest", SMOKE_TEST_LIST));
        EasyMock.expect(mockService.getServerStatuses())
            .andReturn(Collections.singletonMap("test", SMOKE_TEST_LIST));
        EasyMock.expect(mockService.getDatabaseServerStatuses())
            .andReturn(Collections.singletonMap("db", SMOKE_TEST));
        EasyMock.expect(mockService.getExternalSystemsStatuses())
            .andReturn(SMOKE_TEST_LIST);

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
