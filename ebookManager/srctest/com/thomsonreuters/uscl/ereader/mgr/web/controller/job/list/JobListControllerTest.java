package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

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
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty;

public class JobListControllerTest {
	private JobListController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private JobService mockJobService;
	private HandlerAdapter handlerAdapter;
	private List<Long> jobExecutionIds;
	private List<JobExecution> jobExecutions;
    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	
    	this.mockJobService = EasyMock.createMock(JobService.class);
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new JobListController();
    	controller.setJobService(mockJobService);
    	controller.setValidator(new JobListValidator());
    	
    	// Set up the Job execution ID list stored in the session
    	Long JEID = 1965l;
    	this.jobExecutionIds = new ArrayList<Long>();
    	jobExecutionIds.add(JEID);
    	this.jobExecutions = new ArrayList<JobExecution>();
    	jobExecutions.add(new JobExecution(JEID));
    	request.getSession().setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
    }
    
	@Test
	public void testJobListGet() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_LIST);
    	request.setMethod(HttpMethod.GET.name());
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockJobService.findJobExecutions(
    				EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class))).andReturn(jobExecutionIds);
    	EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIds)).andReturn(jobExecutions);
    	EasyMock.replay(mockJobService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	JobListFilterControllerTest.validateModel(session, model);
    	
    	EasyMock.verify(mockJobService);
	}
	
	@Test
	public void testJobListPaging() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_LIST_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("page", "1");
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIds)).andReturn(jobExecutions);
    	EasyMock.replay(mockJobService);
    	
       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	JobListFilterControllerTest.validateModel(session, model);
    	EasyMock.verify(mockJobService);
	}
	
	@Test
	public void testJobListSorting() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_LIST_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("sort", DisplayTagSortProperty.BATCH_STATUS.toString());
    	request.setParameter("dir", "asc");
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockJobService.findJobExecutions(
    				EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class))).andReturn(jobExecutionIds);
    	EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIds)).andReturn(jobExecutions);
    	EasyMock.replay(mockJobService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	JobListFilterControllerTest.validateModel(session, model);
    	
    	EasyMock.verify(mockJobService);
	}
	
	/**
	 * Test the submission of the multi-selected rows, or changing the number of objects displayed per page.
	 * @throws Exception
	 */
	@Test
	public void testChangeObjectsPerPage() throws Exception {
		int EXPECTED_OBJECTS_PER_PAGE = 33;
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_JOB_LIST_POST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("jobCommand", JobListForm.JobCommand.CHANGE_OBJECTS_PER_PAGE.toString());
    	request.setParameter("objectsPerPage", String.valueOf(EXPECTED_OBJECTS_PER_PAGE));
    	HttpSession session = request.getSession();

    	// Record expected service calls
    	EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIds)).andReturn(jobExecutions);
    	EasyMock.replay(mockJobService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	JobListFilterControllerTest.validateModel(session, model);
    	// Ensure the number of rows was changed
    	PageAndSort pageAndSort = (PageAndSort) session.getAttribute(PageAndSort.class.getName());
    	Assert.assertEquals(EXPECTED_OBJECTS_PER_PAGE, pageAndSort.getObjectsPerPage().intValue());
    	
    	EasyMock.verify(mockJobService);
	}
}
