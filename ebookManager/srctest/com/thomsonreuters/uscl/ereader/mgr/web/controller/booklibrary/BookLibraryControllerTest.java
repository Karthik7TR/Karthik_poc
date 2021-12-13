package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm.DisplayTagSortProperty;
import org.displaytag.properties.SortOrderEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

@RunWith(MockitoJUnitRunner.class)
public final class BookLibraryControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + BookLibraryFilterForm.FORM_NAME;

    @InjectMocks
    private BookLibraryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    @Mock
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    @Mock
    private OutageService mockOutageService;
    @Mock
    private LibraryListService mockLibraryListService;
    @Spy
    @SuppressWarnings("unused")
    private final Validator validator = new BookLibraryFilterFormValidator();
    private final List<LibraryList> libraryList = new ArrayList<>();

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    /**
     * General test of the GET request to the Book List page.
     */
    @Test
    public void testBookList() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        when(mockLibraryListService.findBookDefinitions(any(LibraryListFilter.class), any(LibraryListSort.class)))
                .thenReturn(libraryList);
        when(mockLibraryListService.numberOfBookDefinitions(any(LibraryListFilter.class)))
            .thenReturn(1);
        when(keywordTypeCodeSevice.getAllKeywordTypeCodes()).thenReturn(new ArrayList<>());
        when(mockOutageService.getAllPlannedOutagesToDisplay()).thenReturn(new ArrayList<>());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            validateModel(model);
            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            assertEquals(1, paginatedList.getFullListSize());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        verify(mockLibraryListService).findBookDefinitions(any(), any());
        verify(mockLibraryListService).numberOfBookDefinitions(any());
        verify(keywordTypeCodeSevice).getAllKeywordTypeCodes();
        verify(mockOutageService).getAllPlannedOutagesToDisplay();
    }

    /**
     * Test the GET to the Book List paging results
     */
    @Test
    public void testPaging() {
        final int newPageNumber = 3;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));
        final int expectedBookCount = 61;
        when(mockLibraryListService.findBookDefinitions(any(LibraryListFilter.class), any(LibraryListSort.class)))
                .thenReturn(libraryList);
        when(mockLibraryListService.numberOfBookDefinitions(any(LibraryListFilter.class)))
                .thenReturn(expectedBookCount);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            validateModel(model);
            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            assertEquals(expectedBookCount, paginatedList.getFullListSize());
            assertEquals(newPageNumber, paginatedList.getPageNumber());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Test the GET to the Book List sorting results
     */
    @Test
    public void testSorting() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("sort", DisplayTagSortProperty.LAST_GENERATED_DATE.toString());
        request.setParameter("dir", "asc");
        when(mockLibraryListService.findBookDefinitions(any(LibraryListFilter.class), any(LibraryListSort.class)))
                .thenReturn(libraryList);
        when(mockLibraryListService.numberOfBookDefinitions(any(LibraryListFilter.class))).thenReturn(1);
        when(keywordTypeCodeSevice.getAllKeywordTypeCodes()).thenReturn(new ArrayList<>());
        when(mockOutageService.getAllPlannedOutagesToDisplay()).thenReturn(new ArrayList<>());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            validateModel(model);
            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            assertEquals(1, paginatedList.getFullListSize());
            assertEquals(1, paginatedList.getPageNumber());
            assertEquals(SortOrderEnum.ASCENDING, paginatedList.getSortDirection());
            assertEquals(DisplayTagSortProperty.LAST_GENERATED_DATE.toString(), paginatedList.getSortCriterion());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void bookDefinitionSelectionsTest() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("command", BookLibraryFilterForm.Command.GENERATE.toString());
        final String[] selectedEbookKeys = {"uscl/imagedoc4"};
        request.setParameter("selectedEbookKeys", selectedEbookKeys);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void bookDefinitionMultipleSelectionsTest() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("command", BookLibraryFilterForm.Command.GENERATE.toString());
        final String[] selectedEbookKeys = {"uscl/imagedoc3", "uscl/imagedoc4"};
        request.setParameter("selectedEbookKeys", selectedEbookKeys);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void bookDefinitionNoSelectionsTest() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("command", BookLibraryFilterForm.Command.GENERATE.toString());
        final String[] selectedEbookKeys = {};
        request.setParameter("selectedEbookKeys", selectedEbookKeys);
        when(mockLibraryListService.findBookDefinitions(any(LibraryListFilter.class), any(LibraryListSort.class)))
            .thenReturn(libraryList);
        when(mockLibraryListService.numberOfBookDefinitions(any(LibraryListFilter.class)))
            .thenReturn(1);
        when(keywordTypeCodeSevice.getAllKeywordTypeCodes()).thenReturn(new ArrayList<>());
        when(mockOutageService.getAllPlannedOutagesToDisplay()).thenReturn(new ArrayList<>());

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
            final Map<String, Object> model = mav.getModel();
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            assertTrue(bindingResult.hasErrors());
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());
            validateModel(model);
            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            assertEquals(1, paginatedList.getFullListSize());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testBookLibraryFilterGetWithoutParams() throws Exception {
        testBookLibraryFilterGet();
    }

    public Map<String, Object> testBookLibraryFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());
        when(mockLibraryListService.findBookDefinitions(any(LibraryListFilter.class), any(LibraryListSort.class)))
                .thenReturn(libraryList);
        when(mockLibraryListService.numberOfBookDefinitions(any(LibraryListFilter.class)))
                .thenReturn(1);
        when(keywordTypeCodeSevice.getAllKeywordTypeCodes()).thenReturn(new ArrayList<>());
        when(mockOutageService.getAllPlannedOutagesToDisplay()).thenReturn(new ArrayList<>());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        validateModel(model);

        return model;
    }

    @Test
    public void testBookLibraryFilterGetWithParams() throws Exception {
        // Set up the request URL
        // Filter form values
        final String titleId = "uscl/junit/test/abc";
        final String fromDate = "01/01/2012";
        final String toDate = "03/01/2012";
        // The filter values
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("fromString", fromDate);
        request.setParameter("toString", toDate);

        Map<String, Object> model = testBookLibraryFilterGet();

        // Verify the saved filter form
        final BookLibraryFilterForm filterForm = (BookLibraryFilterForm) model.get(BookLibraryFilterForm.FORM_NAME);
        assertEquals(titleId, filterForm.getTitleId());
        assertEquals(fromDate, filterForm.getFromString());
        assertEquals(toDate, filterForm.getToString());
    }

    /**
     * Verify the state of the request (model) as expected before the page renders.
     */
    public static void validateModel(final Map<String, Object> model) {
        assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNotNull(model.get(BookLibraryFilterForm.FORM_NAME));
        assertNotNull(model.get(WebConstants.KEY_KEYWORD_TYPE_CODE));
    }
}
