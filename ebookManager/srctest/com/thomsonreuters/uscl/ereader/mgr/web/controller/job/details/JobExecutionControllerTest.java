package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;
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

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage.Type;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class JobExecutionControllerTest {
	private static final Long JEID = 1936l;
	private static final Long JIID = 1938l;
	private static final Long ID_TO_RESTART = 1965l;
	private static final Long RESTARTED_ID = 1970l;
	private static final Long ID_TO_STOP = 1997l;
	private static final String RESOURCE_BUNDLE_MESSAGE = "Status of operation";
	private static final JobInstance JOB_INSTANCE = new JobInstance(JIID, new JobParameters(), "bogusJobName");
	private static final JobExecution JOB_EXECUTION = new JobExecution(JOB_INSTANCE);
	
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
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	this.mockJobService = EasyMock.createMock(JobService.class);
    	this.mockPublishingStatsService = EasyMock.createMock(PublishingStatsService.class);
    	this.mockJobInstanceBookInfo = EasyMock.createMock(EbookAudit.class);
    	this.mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
    	this.mockOutageService = EasyMock.createMock(OutageService.class);
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new JobExecutionController();
    	controller.setJobService(mockJobService);
    	controller.setPublishingStatsService(mockPublishingStatsService);
    	controller.setValidator(new JobExecutionFormValidator());
    	controller.setMessageSourceAccessor(mockMessageSourceAccessor);
    	controller.setOutageService(mockOutageService);
    }

    /**
     * Test the inbound GET to the page
     */
	@Test
	public void testJobExecutionDetailsInboundGet() throws Exception {
    	// Set up the request URL
    	request.setRequestURI(String.format("/"+WebConstants.MVC_JOB_EXECUTION_DETAILS));
    	request.setParameter(WebConstants.KEY_JOB_EXECUTION_ID, JEID.toString());
    	request.setMethod(HttpMethod.GET.name());
    	PublishingStats bogusStats = new PublishingStats();
    	bogusStats.setAudit(mockJobInstanceBookInfo);
    	
    	EasyMock.expect(mockJobService.findJobExecution(JEID)).andReturn(JOB_EXECUTION);
    	EasyMock.expect(mockPublishingStatsService.findPublishingStatsByJobId((JOB_INSTANCE.getId()))).andReturn(bogusStats);
    	EasyMock.replay(mockJobService);
    	EasyMock.replay(mockPublishingStatsService);
    	
    	EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
    	EasyMock.replay(mockOutageService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	JobExecutionVdo job = (JobExecutionVdo) model.get(WebConstants.KEY_JOB);
    	Assert.assertNotNull(job);
    	Assert.assertEquals(JOB_EXECUTION, job.getJobExecution());
    	Assert.assertEquals(mockJobInstanceBookInfo, job.getBookInfo());
    	Assert.assertEquals(bogusStats, job.getPublishingStats());
    	
    	EasyMock.verify(mockJobService);
    	EasyMock.verify(mockOutageService);
	}
	

	@Test
	public void testHandleRestartJobOperationSuccessResponse() {
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(RESTARTED_ID);

		Object[] args = { ID_TO_RESTART.toString(), RESTARTED_ID.toString() };
		EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args))).andReturn(RESOURCE_BUNDLE_MESSAGE);
		EasyMock.replay(mockMessageSourceAccessor);

		JobExecutionController.handleRestartJobOperationResponse(messages, ID_TO_RESTART, joResp, mockMessageSourceAccessor);
		verifyOperationResponseHandlerMessages(messages, Type.SUCCESS);
	}
	
	@Test
	public void testHandleRestartJobOperationFailureResponse() {
		String failureText = "Job already running";
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(ID_TO_RESTART, false, failureText);
		
		Object[] args = { ID_TO_RESTART.toString(), failureText };
		EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args))).andReturn(RESOURCE_BUNDLE_MESSAGE);
		EasyMock.replay(mockMessageSourceAccessor);

		JobExecutionController.handleRestartJobOperationResponse(messages, ID_TO_RESTART, joResp, mockMessageSourceAccessor);
		verifyOperationResponseHandlerMessages(messages, Type.FAIL);
	}
	
	@Test
	public void testHandleStopJobOperationSuccessResponse() {
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(ID_TO_STOP);

		Object[] args = { ID_TO_STOP.toString() };
		EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args))).andReturn(RESOURCE_BUNDLE_MESSAGE);
		EasyMock.replay(mockMessageSourceAccessor);

		JobExecutionController.handleStopJobOperationResponse(messages, ID_TO_STOP, joResp, mockMessageSourceAccessor);
		verifyOperationResponseHandlerMessages(messages, Type.SUCCESS);
	}
	
	@Test
	public void testHandleStopJobOperationFailureResponse() {
		String failureText = "Job already stopped";
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		SimpleRestServiceResponse joResp = new SimpleRestServiceResponse(ID_TO_STOP, false, failureText);

		Object[] args = { ID_TO_STOP.toString() , failureText};
		EasyMock.expect(mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.aryEq(args))).andReturn(RESOURCE_BUNDLE_MESSAGE);
		EasyMock.replay(mockMessageSourceAccessor);

		JobExecutionController.handleStopJobOperationResponse(messages, ID_TO_STOP, joResp, mockMessageSourceAccessor);
		verifyOperationResponseHandlerMessages(messages, Type.FAIL);
	}
		
	private void verifyOperationResponseHandlerMessages(List<InfoMessage> messages, InfoMessage.Type expectedMessageType) {
		Assert.assertEquals(1, messages.size());
		InfoMessage message = messages.get(0);
		Assert.assertEquals(RESOURCE_BUNDLE_MESSAGE, message.getText());
		Assert.assertEquals(expectedMessageType, message.getType());
		EasyMock.verify(mockMessageSourceAccessor);
	}
}
