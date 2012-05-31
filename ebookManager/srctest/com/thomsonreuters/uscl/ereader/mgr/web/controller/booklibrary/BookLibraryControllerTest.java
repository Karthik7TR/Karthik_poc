/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.displaytag.properties.SortOrderEnum;
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

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryPaginatedList;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionFormValidator;

public class BookLibraryControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + "." + BookLibrarySelectionForm.FORM_NAME;
	
	private List<LibraryList> LIBRARY_LIST = new ArrayList<LibraryList>();
	
	private BookLibraryController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerAdapter handlerAdapter;
	private CodeService mockCodeService;
	private OutageService mockOutageService;
	private LibraryListService mockLibraryListService;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		handlerAdapter = new AnnotationMethodHandlerAdapter();

		// Mock up the dashboard service
		this.mockLibraryListService = EasyMock.createMock(LibraryListService.class);
		this.mockCodeService = EasyMock.createMock(CodeService.class);
		this.mockOutageService = EasyMock.createMock(OutageService.class);

		// Set up the controller
		this.controller = new BookLibraryController();
		controller.setLibraryListService(mockLibraryListService);
		controller.setValidator(new BookLibrarySelectionFormValidator());
		controller.setCodeService(mockCodeService);
		controller.setOutageService(mockOutageService);
	}

	/**
	 * Test the GET to the Book List page.
	 */
	@Test
	public void testBookList() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
		request.setMethod(HttpMethod.GET.name());
		
		EasyMock.expect(mockLibraryListService.findBookDefinitions(EasyMock.anyObject(LibraryListFilter.class), EasyMock.anyObject(LibraryListSort.class))).andReturn(LIBRARY_LIST);
		EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class))).andReturn(1);
		EasyMock.replay(mockLibraryListService);
		
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
		EasyMock.replay(mockOutageService);

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);
			// Verify the returned view name
			assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());

			// Check the state of the model
			Map<String, Object> model = mav.getModel();
			
			HttpSession session = request.getSession();
			validateModel(session, model);
			
			BookLibraryPaginatedList paginatedList = (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
			Assert.assertEquals(1, paginatedList.getFullListSize());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		EasyMock.verify(mockLibraryListService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockOutageService);

	}

	/**
	 * Test the GET to the Book List paging results
	 */
	@Test
	public void testPaging() {
		int newPageNumber = 3;
		request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING);
		request.setMethod(HttpMethod.GET.name());
    	request.setParameter("page", String.valueOf(newPageNumber));
		
		int expectedBookCount = 61;
		EasyMock.expect(mockLibraryListService.findBookDefinitions(EasyMock.anyObject(LibraryListFilter.class), EasyMock.anyObject(LibraryListSort.class))).andReturn(LIBRARY_LIST);
		EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class))).andReturn(expectedBookCount);
		EasyMock.replay(mockLibraryListService);
		
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
		EasyMock.replay(mockOutageService);

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);
			// Verify the returned view name
			assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());

			// Check the state of the model
			Map<String, Object> model = mav.getModel();
			
			HttpSession session = request.getSession();
			validateModel(session, model);
			
			BookLibraryPaginatedList paginatedList = (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
			Assert.assertEquals(expectedBookCount, paginatedList.getFullListSize());
			Assert.assertEquals(newPageNumber, paginatedList.getPageNumber());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		EasyMock.verify(mockLibraryListService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockOutageService);
	}
	
	/**
	 * Test the GET to the Book List sorting results
	 */
	@Test
	public void testSorting() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("sort", DisplayTagSortProperty.LAST_GENERATED_DATE.toString());
    	request.setParameter("dir", "asc");

		EasyMock.expect(mockLibraryListService.findBookDefinitions(EasyMock.anyObject(LibraryListFilter.class), EasyMock.anyObject(LibraryListSort.class))).andReturn(LIBRARY_LIST);
		EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class))).andReturn(1);
		EasyMock.replay(mockLibraryListService);
		
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
		EasyMock.replay(mockOutageService);

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);
			// Verify the returned view name
			assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());

			// Check the state of the model
			Map<String, Object> model = mav.getModel();
			
			HttpSession session = request.getSession();
			validateModel(session, model);
			
			BookLibraryPaginatedList paginatedList = (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
			Assert.assertEquals(1, paginatedList.getFullListSize());
			Assert.assertEquals(1, paginatedList.getPageNumber());
			Assert.assertEquals(SortOrderEnum.ASCENDING, paginatedList.getSortDirection());
			Assert.assertEquals(DisplayTagSortProperty.LAST_GENERATED_DATE.toString(), paginatedList.getSortCriterion());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		EasyMock.verify(mockLibraryListService);
		EasyMock.verify(mockCodeService);
		EasyMock.verify(mockOutageService);
	}

	/**
	 * Test the POST of selection to postBookDefinitionSelections
	 */
	@Test
	public void postBookDefinitionSelectionsTest() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST_SELECTION_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", BookLibrarySelectionForm.Command.GENERATE.toString());

		String[] selectedEbookKeys = { "uscl/imagedoc4" };
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
	 * Test the POST of multiple selection to postBookDefinitionSelections
	 */
	@Test
	public void postBookDefinitionMultipleSelectionsTest() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST_SELECTION_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", BookLibrarySelectionForm.Command.GENERATE.toString());

		String[] selectedEbookKeys = { "uscl/imagedoc3", "uscl/imagedoc4" };
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
	 * Test the POST of No selection to postBookDefinitionSelections
	 */
	@Test
	public void postBookDefinitionNoSelectionsTest() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST_SELECTION_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", BookLibrarySelectionForm.Command.GENERATE.toString());

		String[] selectedEbookKeys = {};
		request.setParameter("selectedEbookKeys", selectedEbookKeys);

		EasyMock.expect(mockLibraryListService.findBookDefinitions(EasyMock.anyObject(LibraryListFilter.class), EasyMock.anyObject(LibraryListSort.class))).andReturn(LIBRARY_LIST);
		EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class))).andReturn(1);
		EasyMock.replay(mockLibraryListService);
		
		EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
		EasyMock.replay(mockCodeService);
		
		EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
		EasyMock.replay(mockOutageService);

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);

			Map<String, Object> model = mav.getModel();
			BindingResult bindingResult = (BindingResult) model
					.get(BINDING_RESULT_KEY);
			assertNotNull(bindingResult);
			assertTrue(bindingResult.hasErrors());

			// Verify the returned view name
			assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
			
			HttpSession session = request.getSession();
			validateModel(session, model);
			
			BookLibraryPaginatedList paginatedList = (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
			Assert.assertEquals(1, paginatedList.getFullListSize());


			EasyMock.verify(mockLibraryListService);
			EasyMock.verify(mockCodeService);
			EasyMock.verify(mockOutageService);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Verify the state of the session and reqeust (model) as expected before the
	 * rendering of the page.
	 */
	public static void validateModel(HttpSession session, Map<String,Object> model) {
    	Assert.assertNotNull(session.getAttribute(BookLibraryFilterForm.FORM_NAME));
    	Assert.assertNotNull(session.getAttribute(BookLibraryController.PAGE_AND_SORT_NAME));
    	Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    	Assert.assertNotNull(model.get(BookLibraryFilterForm.FORM_NAME));
    	Assert.assertNotNull(model.get(BookLibrarySelectionForm.FORM_NAME));
	}
}
