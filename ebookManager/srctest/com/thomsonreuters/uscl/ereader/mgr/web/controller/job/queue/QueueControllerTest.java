package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.displaytag.pagination.PaginatedList;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm.DisplayTagSortProperty;

public class QueueControllerTest {
	
	private static final PageAndSort<DisplayTagSortProperty> PAGE_AND_SORT =
				new PageAndSort<DisplayTagSortProperty>(1, DisplayTagSortProperty.SUBMITTED_AT, false);
	private static final int JOB_REQUEST_COUNT = 50;
	private static final BookDefinition BOOK_DEF = new BookDefinition();
	private static List<JobRequest> JOB_REQUESTS;
	
	private QueueController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private JobRequestService mockJobRequestService;
	//private BookDefinitionService mockBookDefinitionService;
	private HandlerAdapter handlerAdapter;
	
	static {
		JOB_REQUESTS = new ArrayList<JobRequest>();
		for (long pk = 0; pk < JOB_REQUEST_COUNT; pk++) {
			BOOK_DEF.setEbookDefinitionId(pk+345);
			int priority = (int) pk;
			JobRequest jr = JobRequest.createQueuedJobRequest(BOOK_DEF, "ver"+pk, priority, "auser");
			jr.setSubmittedAt(new Date(System.currentTimeMillis()-(pk*1000)));
			jr.setPrimaryKey(pk);
			JOB_REQUESTS.add(jr);
		}
	}

    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	request.getSession().setAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT, PAGE_AND_SORT);
    	
    	this.mockJobRequestService = EasyMock.createMock(JobRequestService.class);
    	//this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
    	
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new QueueController();
    	controller.setJobRequestService(mockJobRequestService);
    	//controller.setBookDefinitionService(mockBookDefinitionService);
    	controller.setValidator(new QueueFormValidator());
    }

    /**
     * Test the inbound GET to the page
     */
	@Test
	public void testInboundGet() throws Exception {
    	// Set up the request URL
    	request.setRequestURI(String.format("/"+WebConstants.MVC_JOB_QUEUE));
    	request.setMethod(HttpMethod.GET.name());
    	HttpSession httpSession = request.getSession();
    	
    	EasyMock.expect(mockJobRequestService.findAllJobRequests()).andReturn(JOB_REQUESTS);
    	EasyMock.replay(mockJobRequestService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_QUEUE, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	verifyModel(model, httpSession); 
    	EasyMock.verify(mockJobRequestService);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPaging() throws Exception {
		Integer pageNumber = new Integer(3);
    	// Set up the request URL
    	request.setRequestURI(String.format("/"+WebConstants.MVC_JOB_QUEUE_PAGE_AND_SORT));
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("page", pageNumber.toString());
    	HttpSession httpSession = request.getSession();
    	
    	// Place the test job requests on the session
    	httpSession.setAttribute(WebConstants.KEY_JOB_REQUESTS_QUEUED, JOB_REQUESTS);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_QUEUE, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	
    	PageAndSort<DisplayTagSortProperty> actualPageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT);
    	Assert.assertEquals(pageNumber, actualPageAndSort.getPageNumber());
    	verifyModel(model, httpSession); 
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSorting() throws Exception {
    	// Set up the request URL
    	request.setRequestURI(String.format("/"+WebConstants.MVC_JOB_QUEUE_PAGE_AND_SORT));
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("sort", DisplayTagSortProperty.PRIORITY.toString());  // Sort on book name
    	request.setParameter("dir", "asc");											// ascending order
    	HttpSession httpSession = request.getSession();
    	
    	EasyMock.expect(mockJobRequestService.findAllJobRequests()).andReturn(JOB_REQUESTS);
    	
    	EasyMock.replay(mockJobRequestService);

    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_JOB_QUEUE, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	
    	verifyModel(model, httpSession);
    	PageAndSort<DisplayTagSortProperty> actualPageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT);

    	PaginatedList paginatedList = (PaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
    	List<JobRequest> actualJobRequestRows = (List<JobRequest>) paginatedList.getList();
    	// Verify that the job request priorities are in ascending order or run (highest to lowest is considered ascending in this case). 
    	int lastPriority = Integer.MAX_VALUE;
    	for (int i=0; i < actualPageAndSort.getObjectsPerPage(); i++) {
    		JobRequest row = actualJobRequestRows.get(i);
    		int priority = row.getPriority(); 
    		Assert.assertTrue(priority < lastPriority);
			lastPriority = priority;
    	}
    	EasyMock.verify(mockJobRequestService);
	}

	private void verifyModel(Map<String,Object> model, HttpSession httpSession) {
    	Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    	Assert.assertEquals(JOB_REQUESTS, httpSession.getAttribute(WebConstants.KEY_JOB_REQUESTS_QUEUED));
    	Assert.assertEquals(PAGE_AND_SORT, httpSession.getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT));
	}
}
