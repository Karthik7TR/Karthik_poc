package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

public class JobSummaryControllerTest {
	//private static final Logger log = Logger.getLogger(JobSummaryControllerTest.class);
	public static final int JOB_EXEC_ID_COUNT = 50;
	private JobSummaryController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private JobService mockJobService;
	private ManagerService mockManagerService;
	private MessageSourceAccessor mockMessageSourceAccessor;
	private HandlerAdapter handlerAdapter;
	private List<Long> jobExecutionIds;
	private List<Long> jobExecutionIdSubList;
	private List<JobExecution> jobExecutions;
	private List<JobSummary> JOB_SUMMARY_LIST = new ArrayList<JobSummary>();

    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	
    	this.mockJobService = EasyMock.createMock(JobService.class);
    	this.mockManagerService = EasyMock.createMock(ManagerService.class);
    	this.mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new JobSummaryController();
    	controller.setJobService(mockJobService);
    	controller.setValidator(new JobSummaryValidator());
    	controller.setManagerService(mockManagerService);
    	controller.setMessageSourceAccessor(mockMessageSourceAccessor);
    	
    	// Set up the Job execution ID list stored in the session
    	this.jobExecutionIds = new ArrayList<Long>();
    	this.jobExecutions = new ArrayList<JobExecution>();
    	for (long id = 0; id < JOB_EXEC_ID_COUNT; id++) {
    		jobExecutionIds.add(id);
    		JobExecution jobExecution = new JobExecution(id);
    		jobExecution.setJobInstance(new JobInstance(id+100, new JobParameters(), "bogusJobName"));
    		jobExecutions.add(jobExecution);
    	}
    	this.jobExecutionIdSubList = jobExecutionIds.subList(0, PageAndSort.DEFAULT_ITEMS_PER_PAGE);
    	request.getSession().setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
    }
    
	@Test
	public void testJobSummaryInboundGet() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY);
    	request.setMethod(HttpMethod.GET.name());
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockJobService.findJobExecutions(
    				EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class))).andReturn(jobExecutionIds);
    	EasyMock.expect(mockJobService.findJobSummary(jobExecutionIdSubList)).andReturn(JOB_SUMMARY_LIST);
    	EasyMock.replay(mockJobService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(PageAndSort.class.getName());
    	Assert.assertEquals(false, pageAndSort.isAscendingSort());
    	Assert.assertEquals(DisplayTagSortProperty.START_TIME, pageAndSort.getSortProperty());
    	
    	EasyMock.verify(mockJobService);
	}
	
	@Test
	public void testJobSummaryPaging() throws Exception {
    	// Set up the request URL
		int newPageNumber = 2;
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("page", String.valueOf(newPageNumber));
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	int startIndex = (newPageNumber - 1) * PageAndSort.DEFAULT_ITEMS_PER_PAGE;
    	int endIndex = startIndex + PageAndSort.DEFAULT_ITEMS_PER_PAGE;
    	List<Long> subList = jobExecutionIds.subList(startIndex, endIndex);
    	EasyMock.expect(mockJobService.findJobSummary(subList)).andReturn(JOB_SUMMARY_LIST);
    	EasyMock.replay(mockJobService);
    	
       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
       	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(PageAndSort.class.getName());
    	Assert.assertEquals(newPageNumber, pageAndSort.getPageNumber().intValue());

    	EasyMock.verify(mockJobService);
	}
	
	@Test
	public void testJobSummarySorting() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("sort", DisplayTagSortProperty.BATCH_STATUS.toString());
    	request.setParameter("dir", "asc");
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockJobService.findJobExecutions(
    				EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class))).andReturn(jobExecutionIds);
    	EasyMock.expect(mockJobService.findJobSummary(jobExecutionIdSubList)).andReturn(JOB_SUMMARY_LIST);
    	EasyMock.replay(mockJobService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	EasyMock.verify(mockJobService);
	}
	
	@Test
	public void testRestartJob() throws Exception {
		Long id = 2002l;
		
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY_JOB_OPERATION);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("jobCommand", JobSummaryForm.JobCommand.RESTART_JOB.toString());
    	request.setParameter("jobExecutionIds", id.toString());
    	HttpSession session = request.getSession();
    	session.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
    	// Record
    	JobOperationResponse jobOperationResponse = new JobOperationResponse(id+1);
    	EasyMock.expect(mockManagerService.restartJob(id)).andReturn(jobOperationResponse);

    	verifyJobOperation();
	}
	
	@Test
	public void testStopJob() throws Exception {
		Long id = 2003l;
		
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY_JOB_OPERATION);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("jobCommand", JobSummaryForm.JobCommand.STOP_JOB.toString());
    	request.setParameter("jobExecutionIds", id.toString());
    	HttpSession session = request.getSession();
    	session.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
    	// Record
    	JobOperationResponse jobOperationResponse = new JobOperationResponse(id);
    	EasyMock.expect(mockManagerService.stopJob(id)).andReturn(jobOperationResponse);

    	verifyJobOperation();
	}
	
    private void verifyJobOperation() throws Exception {
    	// Common recordings for stop and restart
    	EasyMock.expect(mockJobService.findJobSummary(jobExecutionIdSubList)).andReturn(JOB_SUMMARY_LIST);
    	// Replay
    	EasyMock.replay(mockManagerService);
    	EasyMock.replay(mockJobService);
    	
       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	@SuppressWarnings("unchecked")
    	List<InfoMessage> messages = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
    	Assert.assertEquals(1, messages.size());
    	Assert.assertEquals(InfoMessage.Type.SUCCESS, messages.get(0).getType());
    	
    	// Verify calls to the mock methods
    	EasyMock.verify(mockManagerService);
    	EasyMock.verify(mockJobService);
	}
	
	/**
	 * Test the submission of the multi-selected rows, or changing the number of objects displayed per page.
	 * @throws Exception
	 */
	@Test
	public void testChangeDisplayedRowsPerPage() throws Exception {
		int EXPECTED_OBJECTS_PER_PAGE = 33;
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY_CHANGE_ROW_COUNT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("objectsPerPage", String.valueOf(EXPECTED_OBJECTS_PER_PAGE));
    	HttpSession session = request.getSession();

    	// Record expected service calls
    	//EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIdSubList)).andReturn(jobExecutions.subList(0,EXPECTED_OBJECTS_PER_PAGE));
    	EasyMock.expect(mockJobService.findJobSummary(jobExecutionIds.subList(0, EXPECTED_OBJECTS_PER_PAGE))).andReturn(JOB_SUMMARY_LIST);
    	EasyMock.replay(mockJobService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	// Ensure the number of rows was changed
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(PageAndSort.class.getName());
    	Assert.assertEquals(EXPECTED_OBJECTS_PER_PAGE, pageAndSort.getObjectsPerPage().intValue());
    	
    	EasyMock.verify(mockJobService);
	}
	
	/**
	 * Verify the state of the session and reqeust (model) as expected before the
	 * rendering of the job Summary page.
	 */
	public static void validateModel(HttpSession session, Map<String,Object> model) {
    	Assert.assertNotNull(session.getAttribute(FilterForm.FORM_NAME));
    	Assert.assertNotNull(session.getAttribute(PageAndSort.class.getName()));
    	Assert.assertNotNull(session.getAttribute(WebConstants.KEY_JOB_EXECUTION_IDS));
    	Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    	Assert.assertNotNull(model.get(FilterForm.FORM_NAME));
    	Assert.assertNotNull(model.get(JobSummaryForm.FORM_NAME));
	}
}
