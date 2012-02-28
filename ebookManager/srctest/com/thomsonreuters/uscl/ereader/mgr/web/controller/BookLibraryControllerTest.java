/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookDefinitionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryService;

public class BookLibraryControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+BookLibrarySelectionForm.FORM_NAME;
    private BookLibraryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private BookLibraryService mockLibraryService;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the dashboard service
    	this.mockLibraryService = EasyMock.createMock(BookLibraryService.class);
    	
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
    	
    	EasyMock.expect(mockLibraryService.getBooksOnPage("bookName", true, 1, BookLibraryController.NUMBER_BOOK_DEF_SHOWN)).andReturn(new ArrayList<BookDefinitionVdo>());
    	EasyMock.expect(mockLibraryService.getTotalBookCount()).andReturn((long) 1);
    	EasyMock.replay(mockLibraryService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();

	        assertTrue(model.get(WebConstants.KEY_PAGINATED_LIST) instanceof List<?>);
	        Long totalBookCount = Long.valueOf(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE).toString());
	        assertEquals( 1l, totalBookCount.longValue());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
        EasyMock.verify(mockLibraryService);
        
	}

	/**
     * Test the GET to the Book List paging and sorted results
     */
	@Test
	public void testPagingAndSorting() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter(new ParamEncoder(WebConstants.KEY_VDO).encodeParameterName(TableTagParameters.PARAMETER_ORDER), "1");
    	request.setParameter(new ParamEncoder(WebConstants.KEY_VDO).encodeParameterName(TableTagParameters.PARAMETER_SORT), "bookName");
    	request.setParameter(new ParamEncoder(WebConstants.KEY_VDO).encodeParameterName(TableTagParameters.PARAMETER_PAGE), "3");
    	
    	// Mock page 3
    	long expectedBookCount = 61;
    	EasyMock.expect(mockLibraryService.getBooksOnPage("bookName", true, 3, BookLibraryController.NUMBER_BOOK_DEF_SHOWN)).andReturn(new ArrayList<BookDefinitionVdo>());
    	EasyMock.expect(mockLibraryService.getTotalBookCount()).andReturn(expectedBookCount);
    	EasyMock.replay(mockLibraryService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();

	        assertTrue(model.get(WebConstants.KEY_PAGINATED_LIST) instanceof List<?>);
	        Long totalBookCount = Long.valueOf(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE).toString());
	        Assert.assertEquals(expectedBookCount, totalBookCount.longValue());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
        EasyMock.verify(mockLibraryService);
	}
	
	/**
	 *  Test the POST of selection to postBookDefinitionSelections
	 */
	@Test
	public void postBookDefinitionSelectionsTest() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_LIBRARY_LIST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("command", Command.GENERATE.toString());
    	request.setParameter("isAscending", "true");
    	request.setParameter("page", "1");
    	request.setParameter("sort", "bookName");
    	
    	String[] selectedEbookKeys = {"uscl/imagedoc4"};
    	request.setParameter("selectedEbookKeys", selectedEbookKeys);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 *  Test the POST of multiple selection to postBookDefinitionSelections
	 */
	@Test
	public void postBookDefinitionMultipleSelectionsTest() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_LIBRARY_LIST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("command", Command.GENERATE.toString());
    	request.setParameter("isAscending", "true");
    	request.setParameter("page", "1");
    	request.setParameter("sort", "bookName");
    	
    	String[] selectedEbookKeys = {"uscl/imagedoc3", "uscl/imagedoc4"};
    	request.setParameter("selectedEbookKeys", selectedEbookKeys);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 *  Test the POST of No selection to postBookDefinitionSelections
	 */
	@Test
	public void postBookDefinitionNoSelectionsTest() {
		request.setRequestURI("/"+ WebConstants.MVC_BOOK_LIBRARY_LIST);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("command", Command.GENERATE.toString());
    	request.setParameter("isAscending", "true");
    	request.setParameter("page", "1");
    	request.setParameter("sort", "bookName");
    	
    	String[] selectedEbookKeys = {};
    	request.setParameter("selectedEbookKeys", selectedEbookKeys);
    	
    	EasyMock.expect(mockLibraryService.getBooksOnPage("bookName", true, 1, BookLibraryController.NUMBER_BOOK_DEF_SHOWN)).andReturn(new ArrayList<BookDefinitionVdo>());
    	EasyMock.expect(mockLibraryService.getTotalBookCount()).andReturn((long) 1);
    	EasyMock.replay(mockLibraryService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			
			Map<String,Object> model = mav.getModel();
	    	BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	assertTrue(bindingResult.hasErrors());
	    	
	    	// Verify the returned view name
			assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
			
			assertTrue(model.get(WebConstants.KEY_PAGINATED_LIST) instanceof List<?>);
			Long totalBookCount = Long.valueOf(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE).toString());
			assertEquals(totalBookCount, Long.valueOf(1));
	        
	        EasyMock.verify(mockLibraryService);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
