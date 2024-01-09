package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage.Type;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class JobExecutionControllerTest {
    private static final Long JEID = 1936L;
    private static final Long JIID = 1938L;
    private static final Long ID_TO_RESTART = 1965L;
    private static final Long RESTARTED_ID = 1970L;
    private static final Long ID_TO_STOP = 1997L;
    private static final String RESOURCE_BUNDLE_MESSAGE = "Status of operation";
    private static final JobInstance JOB_INSTANCE = new JobInstance(JIID, "bogusJobName");
    private static final JobExecution JOB_EXECUTION = new JobExecution(JOB_INSTANCE, new JobParameters());

    private JobExecutionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private JobService mockJobService;
    private PublishingStatsService mockPublishingStatsService;
    private EbookAudit mockJobInstanceBookInfo;
    private OutageService mockOutageService;
    private MessageSourceAccessor mockMessageSourceAccessor;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockJobService = EasyMock.createMock(JobService.class);
        mockPublishingStatsService = EasyMock.createMock(PublishingStatsService.class);
        mockJobInstanceBookInfo = EasyMock.createMock(EbookAudit.class);
        mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
        mockOutageService = EasyMock.createMock(OutageService.class);
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new JobExecutionController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "jobService", mockJobService);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "publishingStatsService", mockPublishingStatsService);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "validator", new JobExecutionFormValidator());
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "messageSourceAccessor", mockMessageSourceAccessor);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "outageService", mockOutageService);
    }

    /**
     * Test the inbound GET to the page
     */
    @Test
    public void testJobExecutionDetailsInboundGet() throws Exception {
        // Set up the request URL
        request.setRequestURI(String.format("/" + WebConstants.MVC_JOB_EXECUTION_DETAILS));
        request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, JEID.toString());
        request.setMethod(HttpMethod.GET.name());
        final PublishingStats bogusStats = new PublishingStats();
        bogusStats.setAudit(mockJobInstanceBookInfo);

        EasyMock.expect(mockJobService.findJobExecution(JEID)).andReturn(JOB_EXECUTION);
        EasyMock.expect(mockPublishingStatsService.findPublishingStatsByJobId((JOB_INSTANCE.getId())))
            .andReturn(bogusStats);
        EasyMock.replay(mockJobService);
        EasyMock.replay(mockPublishingStatsService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        final JobExecutionVdo job = (JobExecutionVdo) model.get(WebConstants.KEY_JOB);
        Assert.assertNotNull(job);
        Assert.assertEquals(JOB_EXECUTION, job.getJobExecution());
        Assert.assertEquals(mockJobInstanceBookInfo, job.getBookInfo());
        Assert.assertEquals(bogusStats, job.getPublishingStats());

        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testHandleRestartJobOperationSuccessResponse() {
        final List<InfoMessage> messages = new ArrayList<>();
        final SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(RESTARTED_ID);

        final Object[] args = {ID_TO_RESTART.toString(), RESTARTED_ID.toString()};
        EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args)))
            .andReturn(RESOURCE_BUNDLE_MESSAGE);
        EasyMock.replay(mockMessageSourceAccessor);

        JobExecutionController
            .handleRestartJobOperationResponse(messages, ID_TO_RESTART, joResp, mockMessageSourceAccessor);
        verifyOperationResponseHandlerMessages(messages, Type.SUCCESS);
    }

    @Test
    public void testHandleRestartJobOperationFailureResponse() {
        final String failureText = "Job already running";
        final List<InfoMessage> messages = new ArrayList<>();
        final SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(ID_TO_RESTART, false, failureText);

        final Object[] args = {ID_TO_RESTART.toString(), failureText};
        EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args)))
            .andReturn(RESOURCE_BUNDLE_MESSAGE);
        EasyMock.replay(mockMessageSourceAccessor);

        JobExecutionController
            .handleRestartJobOperationResponse(messages, ID_TO_RESTART, joResp, mockMessageSourceAccessor);
        verifyOperationResponseHandlerMessages(messages, Type.FAIL);
    }

    @Test
    public void testHandleStopJobOperationSuccessResponse() {
        final List<InfoMessage> messages = new ArrayList<>();
        final SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(ID_TO_STOP);

        final Object[] args = {ID_TO_STOP.toString()};
        EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args)))
            .andReturn(RESOURCE_BUNDLE_MESSAGE);
        EasyMock.replay(mockMessageSourceAccessor);

        JobExecutionController.handleStopJobOperationResponse(messages, ID_TO_STOP, joResp, mockMessageSourceAccessor);
        verifyOperationResponseHandlerMessages(messages, Type.SUCCESS);
    }

    @Test
    public void testHandleStopJobOperationFailureResponse() {
        final String failureText = "Job already stopped";
        final List<InfoMessage> messages = new ArrayList<>();
        final SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(ID_TO_STOP, false, failureText);

        final Object[] args = {ID_TO_STOP.toString(), failureText};
        EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args)))
            .andReturn(RESOURCE_BUNDLE_MESSAGE);
        EasyMock.replay(mockMessageSourceAccessor);

        JobExecutionController.handleStopJobOperationResponse(messages, ID_TO_STOP, joResp, mockMessageSourceAccessor);
        verifyOperationResponseHandlerMessages(messages, Type.FAIL);
    }

    private void verifyOperationResponseHandlerMessages(
        final List<InfoMessage> messages,
        final InfoMessage.Type expectedMessageType) {
        Assert.assertEquals(1, messages.size());
        final InfoMessage message = messages.get(0);
        Assert.assertEquals(RESOURCE_BUNDLE_MESSAGE, message.getText());
        Assert.assertEquals(expectedMessageType, message.getType());
        EasyMock.verify(mockMessageSourceAccessor);
    }
}
