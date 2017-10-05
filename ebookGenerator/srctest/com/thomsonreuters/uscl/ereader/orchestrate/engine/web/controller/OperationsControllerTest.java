package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.util.Date;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * Unit tests for the OperationsController which handles the URL request(s) to restart or stop a job.
 */
public final class OperationsControllerTest {
    private static final Long JOB_EXEC_ID = Long.valueOf(1234);

    private OperationsController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private EngineService mockEngineService;
    private OutageProcessor mockOutageProcessor;
    private MessageSourceAccessor mockAccessor;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockEngineService = EasyMock.createMock(EngineService.class);
        mockOutageProcessor = EasyMock.createMock(OutageProcessor.class);
        mockAccessor = EasyMock.createMock(MessageSourceAccessor.class);

        handlerAdapter = new AnnotationMethodHandlerAdapter();
        controller = new OperationsController(new FlowJob());
        controller.setEngineService(mockEngineService);
        controller.setOutageProcessor(mockOutageProcessor);
        controller.setMessageSourceAccessor(mockAccessor);
    }

    @Test
    public void testRestartJob() throws Exception {
        request.setRequestURI("/service/restart/job/" + JOB_EXEC_ID);
        request.setMethod(HttpMethod.GET.name());
        final Long restartedJobExecId = Long.valueOf(JOB_EXEC_ID.longValue() + 1L);
        EasyMock.expect(mockOutageProcessor.findPlannedOutageInContainer(EasyMock.anyObject(Date.class)))
            .andReturn(null);
        EasyMock.expect(mockEngineService.restartJob(JOB_EXEC_ID)).andReturn(restartedJobExecId);
        EasyMock.replay(mockOutageProcessor);
        EasyMock.replay(mockEngineService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        Assert.assertNotNull(mav);
        Assert.assertEquals(CoreConstants.VIEW_SIMPLE_REST_RESPONSE, mav.getViewName());

        final Map<String, Object> model = mav.getModel();
        final SimpleRestServiceResponse opResponseActual =
            (SimpleRestServiceResponse) model.get(CoreConstants.KEY_SIMPLE_REST_RESPONSE);
        Assert.assertEquals(restartedJobExecId, opResponseActual.getId());

        EasyMock.verify(mockOutageProcessor);
        EasyMock.verify(mockEngineService);
    }

    @Test
    public void futureTest() {
        Assert.assertTrue(true);
    }
}
