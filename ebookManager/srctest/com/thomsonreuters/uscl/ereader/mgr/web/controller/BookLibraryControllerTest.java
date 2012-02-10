/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookDefinitionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class BookLibraryControllerTest {
    private BookLibraryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private BookLibrarySelectionForm selectionForm;
    private BookLibraryService mockLibraryService;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the dashboard service
    	this.mockLibraryService = EasyMock.createMock(BookLibraryService.class);
    	this.selectionForm = new BookLibrarySelectionForm();
    	
    	EasyMock.expect(mockLibraryService.getBooksOnPage("bookName", true, 1, 20)).andReturn(new ArrayList<BookDefinitionVdo>());
    	EasyMock.expect(mockLibraryService.getTotalBookCount()).andReturn((long) 1);
    	EasyMock.replay(mockLibraryService);

    	// Set up the controller
    	this.controller = new BookLibraryController();
    	controller.setBookLibraryService(mockLibraryService);
    	controller.setValidator(new BookLibrarySelectionFormValidator());
	}

	/**
     * Test the GET to the Book List page.
     */
	@Test
	public void testBookList() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_LIBRARY_LIST);
    	request.setMethod(HttpMethod.GET.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();

	        assertTrue(model.get(WebConstants.KEY_PAGINATED_LIST) instanceof List<?>);
	        String totalBookCount = model.get(WebConstants.KEY_TOTAL_BOOK_SIZE).toString();
	        assertTrue(totalBookCount.equals("1"));
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        EasyMock.verify(mockLibraryService);
        
	}

	@Test
	public void testPagingAndSorting() {
		fail("Not yet implemented");
	}

	@Test
	public void testGenerateEbookPreview() {
		fail("Not yet implemented");
	}

	@Test
	public void testGenerateBulkEbookPreview() {
		fail("Not yet implemented");
	}

	@Test
	public void testBookDefinitionPromotion() {
		fail("Not yet implemented");
	}

}
