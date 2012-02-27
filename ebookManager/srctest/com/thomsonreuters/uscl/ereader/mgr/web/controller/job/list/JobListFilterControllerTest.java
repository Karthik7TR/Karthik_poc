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

public class JobListFilterControllerTest {
	private FilterController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private JobService mockJobService;
	private HandlerAdapter handlerAdapter;
	
    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	this.mockJobService = EasyMock.createMock(JobService.class);
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new FilterController();
    	controller.setJobService(mockJobService);
    	controller.setValidator(new FilterFormValidator());
    }
	@Test
	public void testJobListFilterPost() throws Exception {
    	// Set up the request URL
		String titleId = "uscl/junit/test/abc";
    	request.setRequestURI("/"+WebConstants.MVC_JOB_LIST_FILTER_POST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
    	HttpSession session = request.getSession();
    	
    	//FilterForm filterForm = new FilterForm();
    	//filterForm.setTitleId(titleId);

    	Long JEID = 1965l;
    	List<Long> jobExecutionIds = new ArrayList<Long>();
    	jobExecutionIds.add(JEID);
    	List<JobExecution> jobExecutions = new ArrayList<JobExecution>();
    	jobExecutions.add(new JobExecution(JEID));
    	
    	EasyMock.expect(mockJobService.findJobExecutions(
    				EasyMock.anyObject(JobFilter.class), EasyMock.anyObject(JobSort.class))).andReturn(jobExecutionIds);
    	EasyMock.expect(mockJobService.findJobExecutions(jobExecutionIds)).andReturn(jobExecutions);
    	EasyMock.replay(mockJobService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	Assert.assertNotNull(session.getAttribute(FilterForm.FORM_NAME));
    	Assert.assertNotNull(session.getAttribute(PageAndSort.class.getName()));
    	Assert.assertNotNull(session.getAttribute(WebConstants.KEY_JOB_EXECUTION_IDS));
    	Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    	Assert.assertNotNull(model.get(JobListForm.FORM_NAME));
    	
    	EasyMock.verify(mockJobService);
	}
}
