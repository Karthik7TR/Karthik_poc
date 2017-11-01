package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class JobSummaryControllerTest {
    //private static final Logger log = LogManager.getLogger(JobSummaryControllerTest.class);
    public static final int JOB_EXEC_ID_COUNT = 50;
    private JobSummaryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private JobService mockJobService;
    private GeneratorRestClient mockManagerService;
    private JobExecutionController mockJobExecutionController;
    private MessageSourceAccessor mockMessageSourceAccessor;
    private OutageService mockOutageService;
    private HandlerAdapter handlerAdapter;
    private List<Long> jobExecutionIds;
    private List<Long> jobExecutionIdSubList;
    private List<JobExecution> jobExecutions;
    private List<JobSummary> JOB_SUMMARY_LIST = new ArrayList<>();

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        mockJobService = EasyMock.createMock(JobService.class);
        mockManagerService = EasyMock.createMock(GeneratorRestClient.class);
        mockJobExecutionController = EasyMock.createMock(JobExecutionController.class);
        mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
        mockOutageService = EasyMock.createMock(OutageService.class);
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new JobSummaryController(mockJobService, mockOutageService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "validator", new JobSummaryValidator());
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "generatorRestClient", mockManagerService);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "messageSourceAccessor", mockMessageSourceAccessor);
        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "jobExecutionController", mockJobExecutionController);

        // Set up the Job execution ID list stored in the session
        jobExecutionIds = new ArrayList<>();
        jobExecutions = new ArrayList<>();
        for (long id = 0; id < JOB_EXEC_ID_COUNT; id++) {
            jobExecutionIds.add(id);
            final JobExecution jobExecution = new JobExecution(id);
            jobExecution.setJobInstance(new JobInstance(id + 100, "bogusJobName"));
            jobExecutions.add(jobExecution);
        }
        jobExecutionIdSubList = jobExecutionIds.subList(0, PageAndSort.DEFAULT_ITEMS_PER_PAGE);
        request.getSession().setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
    }

    @Test
    public void testJobSummaryInboundGet() throws Exception {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock.expect(
            mockJobService.findJobExecutions(EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class)))
            .andReturn(jobExecutionIds);
        EasyMock.expect(mockJobService.findJobSummary(jobExecutionIdSubList)).andReturn(JOB_SUMMARY_LIST);
        EasyMock.replay(mockJobService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(PageAndSort.class.getName());
        Assert.assertEquals(false, pageAndSort.isAscendingSort());
        Assert.assertEquals(DisplayTagSortProperty.START_TIME, pageAndSort.getSortProperty());

        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testJobSummaryPaging() throws Exception {
        // Set up the request URL
        final int newPageNumber = 2;
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));
        final HttpSession session = request.getSession();

        // Record expected service calls
        final int startIndex = (newPageNumber - 1) * PageAndSort.DEFAULT_ITEMS_PER_PAGE;
        final int endIndex = startIndex + PageAndSort.DEFAULT_ITEMS_PER_PAGE;
        final List<Long> subList = jobExecutionIds.subList(startIndex, endIndex);
        EasyMock.expect(mockJobService.findJobSummary(subList)).andReturn(JOB_SUMMARY_LIST);
        EasyMock.replay(mockJobService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(PageAndSort.class.getName());
        Assert.assertEquals(newPageNumber, pageAndSort.getPageNumber().intValue());

        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testJobSummarySorting() throws Exception {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("sort", DisplayTagSortProperty.BATCH_STATUS.toString());
        request.setParameter("dir", "asc");
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock.expect(
            mockJobService.findJobExecutions(EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class)))
            .andReturn(jobExecutionIds);
        EasyMock.expect(mockJobService.findJobSummary(jobExecutionIdSubList)).andReturn(JOB_SUMMARY_LIST);
        EasyMock.replay(mockJobService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);
    }

    @Test
    public void testRestartJob() throws Exception {
        final Long id = 2002L;

        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_JOB_OPERATION);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("jobCommand", JobSummaryForm.JobCommand.RESTART_JOB.toString());
        request.setParameter("jobExecutionIds", id.toString());
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
        // Record
        final SimpleRestServiceResponse restResponse = new SimpleRestServiceResponse(id + 1);
        EasyMock.expect(mockManagerService.restartJob(id)).andReturn(restResponse);

        verifyJobOperation();
    }

    @Test
    public void testStopJob() throws Exception {
        final Long id = 2003L;

        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_JOB_OPERATION);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("jobCommand", JobSummaryForm.JobCommand.STOP_JOB.toString());
        request.setParameter("jobExecutionIds", id.toString());
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
        // Record
        final SimpleRestServiceResponse jobOperationResponse = new SimpleRestServiceResponse(id);
        EasyMock.expect(mockManagerService.stopJob(id)).andReturn(jobOperationResponse);

        verifyJobOperation();
    }

    private void verifyJobOperation() throws Exception {
        // Common recordings for stop and restart
        EasyMock.expect(mockJobService.findJobSummary(jobExecutionIdSubList)).andReturn(JOB_SUMMARY_LIST);
        EasyMock
            .expect(
                mockJobExecutionController.authorizedForJobOperation(
                    EasyMock.anyLong(),
                    EasyMock.anyObject(String.class),
                    EasyMock.anyObject(List.class)))
            .andReturn(true);
        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        // Replay
        EasyMock.replay(mockManagerService);
        EasyMock.replay(mockJobService);
        EasyMock.replay(mockJobExecutionController);
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final List<InfoMessage> messages = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals(InfoMessage.Type.SUCCESS, messages.get(0).getType());

        // Verify calls to the mock methods
        EasyMock.verify(mockManagerService);
        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);
    }

    /**
     * Test the submission of the multi-selected rows, or changing the number of objects displayed per page.
     * @throws Exception
     */
    @Test
    public void testChangeDisplayedRowsPerPage() throws Exception {
        final int EXPECTED_OBJECTS_PER_PAGE = 33;
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_CHANGE_ROW_COUNT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("objectsPerPage", String.valueOf(EXPECTED_OBJECTS_PER_PAGE));
        final HttpSession session = request.getSession();

        // Record expected service calls
        //EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIdSubList)).andReturn(jobExecutions.subList(0,EXPECTED_OBJECTS_PER_PAGE));
        EasyMock.expect(mockJobService.findJobSummary(jobExecutionIds.subList(0, EXPECTED_OBJECTS_PER_PAGE)))
            .andReturn(JOB_SUMMARY_LIST);
        EasyMock.replay(mockJobService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_JOB_SUMMARY, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);
        // Ensure the number of rows was changed
        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(PageAndSort.class.getName());
        Assert.assertEquals(EXPECTED_OBJECTS_PER_PAGE, pageAndSort.getObjectsPerPage().intValue());

        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);
    }

    /**
     * Verify the state of the session and reqeust (model) as expected before the
     * rendering of the job Summary page.
     */
    public static void validateModel(final HttpSession session, final Map<String, Object> model) {
        Assert.assertNotNull(session.getAttribute(FilterForm.FORM_NAME));
        Assert.assertNotNull(session.getAttribute(PageAndSort.class.getName()));
        Assert.assertNotNull(session.getAttribute(WebConstants.KEY_JOB_EXECUTION_IDS));
        Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        Assert.assertNotNull(model.get(FilterForm.FORM_NAME));
        Assert.assertNotNull(model.get(JobSummaryForm.FORM_NAME));
    }
}
