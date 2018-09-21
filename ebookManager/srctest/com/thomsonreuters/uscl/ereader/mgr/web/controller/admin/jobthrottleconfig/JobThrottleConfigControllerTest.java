package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class JobThrottleConfigControllerTest {
    private static final String HOST_NAME = "localhost";
    private static final int PORT_NUM = 1289;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private AppConfigService appConfigService;
    private ManagerService mockManagerService;
    private GeneratorRestClient mockGeneratorRestClient;
    private JobThrottleConfigController controller;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        appConfigService = EasyMock.createMock(AppConfigService.class);
        mockManagerService = EasyMock.createMock(ManagerService.class);
        mockGeneratorRestClient = EasyMock.createMock(GeneratorRestClient.class);

        // Set up the controller
        controller = new JobThrottleConfigController(
            appConfigService,
            mockManagerService,
            mockGeneratorRestClient,
            new JobThrottleConfigFormValidator(),
            HOST_NAME + "," + HOST_NAME,
            PORT_NUM);
    }

    @Test
    public void testInboundGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG);
        request.setMethod(HttpMethod.GET.name());
        final JobThrottleConfig config = new JobThrottleConfig();
        EasyMock.expect(appConfigService.loadJobThrottleConfig()).andReturn(config);
        EasyMock.replay(appConfigService);
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG, mav.getViewName());
        EasyMock.verify(appConfigService);
    }

    @Test
    public void testSubmitForm() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG);
        request.setMethod(HttpMethod.POST.name());

        final JobThrottleConfig config = new JobThrottleConfig(8, true, "foobar", "foobarXppPathway", "foobarXppBundles", 6);

        request.setParameter(
            JobThrottleConfig.Key.coreThreadPoolSize.toString(),
            String.valueOf(config.getCoreThreadPoolSize()));
        request.setParameter(
            JobThrottleConfig.Key.stepThrottleEnabled.toString(),
            String.valueOf(config.isStepThrottleEnabled()));
        request.setParameter(JobThrottleConfig.Key.throttleStepName.toString(), config.getThrottleStepName());
        request.setParameter(JobThrottleConfig.Key.throttleStepNameXppPathway.toString(), config.getThrottleStepNameXppPathway());
        request.setParameter(JobThrottleConfig.Key.throttleStepNameXppBundles.toString(), config.getThrottleStepNameXppBundles());
        request.setParameter(
            JobThrottleConfig.Key.throtttleStepMaxJobs.toString(),
            String.valueOf(config.getThrottleStepMaxJobs()));

        final InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);
        final Map<String, Collection<String>> stepNames = Collections.singletonMap("ebookGeneratorJob", Arrays.asList("a", "b", "c"));
        appConfigService.saveJobThrottleConfig(config);
        EasyMock.expect(appConfigService.loadJobThrottleConfig()).andReturn(config).times(2);
        EasyMock.expect(mockManagerService.pushJobThrottleConfiguration(config, socketAddr))
            .andReturn(new SimpleRestServiceResponse())
            .times(2);
        EasyMock.expect(mockGeneratorRestClient.getStepNames()).andReturn(stepNames);
        EasyMock.replay(appConfigService);
        EasyMock.replay(mockManagerService);
        EasyMock.replay(mockGeneratorRestClient);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
        // Expect 3 info messages 1 success, and 2 failures for inability to push to hosts
        assertEquals(3, mesgs.size());
        assertEquals(InfoMessage.Type.SUCCESS, mesgs.get(0).getType());
        assertEquals(InfoMessage.Type.FAIL, mesgs.get(1).getType());
        assertEquals(InfoMessage.Type.FAIL, mesgs.get(2).getType());
        assertNotNull(model.get(JobThrottleConfigController.KEY_STEP_NAMES));

        EasyMock.verify(appConfigService);
        EasyMock.verify(mockManagerService);
    }
}
