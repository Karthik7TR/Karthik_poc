package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class JobSummaryFilterControllerTest {
    private FilterController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private JobService mockJobService;
    private OutageService mockOutageService;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockJobService = EasyMock.createMock(JobService.class);
        mockOutageService = EasyMock.createMock(OutageService.class);
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new FilterController(mockJobService, mockOutageService, new FilterFormValidator());
    }

    @Test
    public void testJobSummaryFilterGetWithoutParams() throws Exception {
        testJobSummaryFilterGet();
    }

    @Test
    public void testJobSummaryFilterGetWithParams() throws Exception {
        // Set up the request URL
        // Filter form values
        final String titleId = "uscl/junit/test/abc";
        final String fromDate = "01/01/2012 00:00:00";
        final String toDate = "03/01/2012 23:59:59";
        // The filter values
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("fromDateString", fromDate);
        request.setParameter("toDateString", toDate);

        Map<String, Object> model = testJobSummaryFilterGet();

        // Verify the saved filter form
        final FilterForm filterForm = (FilterForm) model.get(FilterForm.FORM_NAME);
        Assert.assertEquals(titleId, filterForm.getTitleId());
        Assert.assertEquals(fromDate, filterForm.getFromDateString());
        Assert.assertEquals(toDate, filterForm.getToDateString());
    }

    public Map<String, Object> testJobSummaryFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_JOB_SUMMARY_FILTER);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        final Long JEID = 1965L;
        final List<Long> jobExecutionIds = new ArrayList<>();
        jobExecutionIds.add(JEID);
        final List<JobExecution> jobExecutions = new ArrayList<>();
        final JobExecution jobExecution = new JobExecution(JEID);
        jobExecution.setJobInstance(new JobInstance(3456L, "bogusJobName"));
        jobExecutions.add(jobExecution);

        EasyMock.expect(
                mockJobService.findJobExecutions(EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class)))
                .andReturn(jobExecutionIds);
        EasyMock.expect(mockJobService.findJobSummary(jobExecutionIds)).andReturn(new ArrayList<>());
        EasyMock.replay(mockJobService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<>());
        EasyMock.replay(mockOutageService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        JobSummaryControllerTest.validateModel(session, model);

        EasyMock.verify(mockJobService);
        EasyMock.verify(mockOutageService);

        return model;
    }
}
