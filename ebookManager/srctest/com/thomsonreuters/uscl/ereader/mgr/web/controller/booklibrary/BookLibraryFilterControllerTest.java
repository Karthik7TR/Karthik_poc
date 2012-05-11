package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.mgr.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.domain.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.domain.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class BookLibraryFilterControllerTest {
	private List<LibraryList> LIBRARY_LIST = new ArrayList<LibraryList>();
	
	private BookLibraryFilterController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private LibraryListService mockLibraryListService;
	private HandlerAdapter handlerAdapter;
	
    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	this.mockLibraryListService = EasyMock.createMock(LibraryListService.class);
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new BookLibraryFilterController();
    	controller.setLibraryListService(mockLibraryListService);
    }
	@Test
	public void testJobSummaryFilterPost() throws Exception {
    	// Set up the request URL
		// Filter form values
		String titleId = "uscl/junit/test/abc";
		String fromDate = "01/01/2012";
		String toDate = "03/01/2012";
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST);
    	request.setMethod(HttpMethod.POST.name());
    	// The filter values
    	request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
    	request.setParameter("fromString", fromDate);
    	request.setParameter("toString", toDate);
    	HttpSession session = request.getSession();
    	
    	EasyMock.expect(mockLibraryListService.findBookDefinitions(EasyMock.anyObject(LibraryListFilter.class), EasyMock.anyObject(LibraryListSort.class))).andReturn(LIBRARY_LIST);
		EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class))).andReturn(1);
		EasyMock.replay(mockLibraryListService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BookLibraryControllerTest.validateModel(session, model);
    	
    	// Verify the saved filter form
    	BookLibraryFilterForm filterForm = (BookLibraryFilterForm) model.get(BookLibraryFilterForm.FORM_NAME);
    	Assert.assertEquals(titleId, filterForm.getTitleId());
    	Assert.assertEquals(fromDate, filterForm.getFromString());
    	Assert.assertEquals(toDate, filterForm.getToString());
    	
    	EasyMock.verify(mockLibraryListService);
	}
}
