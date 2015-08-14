package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Assert;

import org.easymock.EasyMock;
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

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class JobSummaryFilterControllerTest {
	private FilterController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private JobService mockJobService;
	private OutageService mockOutageService;
	private HandlerAdapter handlerAdapter;
	
    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	this.mockJobService = EasyMock.createMock(JobService.class);
    	this.mockOutageService = EasyMock.createMock(OutageService.class);
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new FilterController();
    	controller.setJobService(mockJobService);
    	controller.setValidator(new FilterFormValidator());
    	controller.setOutageService(mockOutageService);
    }
	@Test
	public void testJobSummaryFilterPost() throws Exception {
    	// Set up the request URL
		// Filter form values
		String titleId = "uscl/junit/test/abc";
		String fromDate = "01/01/2012 00:00:00";
		String toDate = "03/01/2012 23:59:59";
    	request.setRequestURI("/"+WebConstants.MVC_JOB_SUMMARY_FILTER_POST);
    	request.setMethod(HttpMethod.POST.name());
    	// The filter values
    	request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
    	request.setParameter("fromDateString", fromDate);
    	request.setParameter("toDateString", toDate);
    	HttpSession session = request.getSession();
    	
    	Long JEID = 1965l;
    	List<Long> jobExecutionIds = new ArrayList<Long>();
    	jobExecutionIds.add(JEID);
    	List<JobExecution> jobExecutions = new ArrayList<JobExecution>();
    	JobExecution jobExecution = new JobExecution(JEID);
    	jobExecution.setJobInstance(new JobInstance(3456l, "bogusJobName"));
    	jobExecutions.add(jobExecution);
    	
    	EasyMock.expect(mockJobService.findJobExecutions(
   				EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class))).andReturn(jobExecutionIds);
    	EasyMock.expect(mockJobService.findJobSummary(jobExecutionIds)).andReturn(new ArrayList<JobSummary>());
    	EasyMock.replay(mockJobService);
    	
    	EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
    	EasyMock.replay(mockOutageService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	JobSummaryControllerTest.validateModel(session, model);
    	
    	// Verify the saved filter form
    	FilterForm filterForm = (FilterForm) model.get(FilterForm.FORM_NAME);
    	Assert.assertEquals(titleId, filterForm.getTitleId());
    	Assert.assertEquals(fromDate, filterForm.getFromDateString());
    	Assert.assertEquals(toDate, filterForm.getToDateString());
    	
    	EasyMock.verify(mockJobService);
    	EasyMock.verify(mockOutageService);
	}
}
