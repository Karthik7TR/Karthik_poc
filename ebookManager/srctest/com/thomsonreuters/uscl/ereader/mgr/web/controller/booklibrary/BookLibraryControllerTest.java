package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;
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

public final class BookLibraryControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + BookLibrarySelectionForm.FORM_NAME;

    private List<LibraryList> LIBRARY_LIST = new ArrayList<>();

    private BookLibraryController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CodeService mockCodeService;
    private OutageService mockOutageService;
    private LibraryListService mockLibraryListService;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the dashboard service
        mockLibraryListService = EasyMock.createMock(LibraryListService.class);
        mockCodeService = EasyMock.createMock(CodeService.class);
        mockOutageService = EasyMock.createMock(OutageService.class);

        // Set up the controller
        controller = new BookLibraryController(
            mockLibraryListService,
            mockCodeService,
            mockOutageService,
            new BookLibrarySelectionFormValidator());
    }

    /**
     * Test the GET to the Book List page.
     */
    @Test
    public void testBookList() {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST);
        request.setMethod(HttpMethod.GET.name());

        EasyMock
            .expect(
                mockLibraryListService.findBookDefinitions(
                    EasyMock.anyObject(LibraryListFilter.class),
                    EasyMock.anyObject(LibraryListSort.class)))
            .andReturn(LIBRARY_LIST);
        EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockLibraryListService);

        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            final HttpSession session = request.getSession();
            validateModel(session, model);

            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            Assert.assertEquals(1, paginatedList.getFullListSize());
        } catch (final Exception e) {
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
        final int newPageNumber = 3;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));

        final int expectedBookCount = 61;
        EasyMock
            .expect(
                mockLibraryListService.findBookDefinitions(
                    EasyMock.anyObject(LibraryListFilter.class),
                    EasyMock.anyObject(LibraryListSort.class)))
            .andReturn(LIBRARY_LIST);
        EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class)))
            .andReturn(expectedBookCount);
        EasyMock.replay(mockLibraryListService);

        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            final HttpSession session = request.getSession();
            validateModel(session, model);

            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            Assert.assertEquals(expectedBookCount, paginatedList.getFullListSize());
            Assert.assertEquals(newPageNumber, paginatedList.getPageNumber());
        } catch (final Exception e) {
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

        EasyMock
            .expect(
                mockLibraryListService.findBookDefinitions(
                    EasyMock.anyObject(LibraryListFilter.class),
                    EasyMock.anyObject(LibraryListSort.class)))
            .andReturn(LIBRARY_LIST);
        EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockLibraryListService);

        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_BOOK_LIBRARY_LIST, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            final HttpSession session = request.getSession();
            validateModel(session, model);

            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            Assert.assertEquals(1, paginatedList.getFullListSize());
            Assert.assertEquals(1, paginatedList.getPageNumber());
            Assert.assertEquals(SortOrderEnum.ASCENDING, paginatedList.getSortDirection());
            Assert
                .assertEquals(DisplayTagSortProperty.LAST_GENERATED_DATE.toString(), paginatedList.getSortCriterion());
        } catch (final Exception e) {
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

        final String[] selectedEbookKeys = {};
        request.setParameter("selectedEbookKeys", selectedEbookKeys);

        EasyMock
            .expect(
                mockLibraryListService.findBookDefinitions(
                    EasyMock.anyObject(LibraryListFilter.class),
                    EasyMock.anyObject(LibraryListSort.class)))
            .andReturn(LIBRARY_LIST);
        EasyMock.expect(mockLibraryListService.numberOfBookDefinitions(EasyMock.anyObject(LibraryListFilter.class)))
            .andReturn(1);
        EasyMock.replay(mockLibraryListService);

        EasyMock.expect(mockCodeService.getAllKeywordTypeCodes()).andReturn(new ArrayList<KeywordTypeCode>());
        EasyMock.replay(mockCodeService);

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.replay(mockOutageService);

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

            final HttpSession session = request.getSession();
            validateModel(session, model);

            final BookLibraryPaginatedList paginatedList =
                (BookLibraryPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
            Assert.assertEquals(1, paginatedList.getFullListSize());

            EasyMock.verify(mockLibraryListService);
            EasyMock.verify(mockCodeService);
            EasyMock.verify(mockOutageService);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Verify the state of the session and reqeust (model) as expected before the
     * rendering of the page.
     */
    public static void validateModel(final HttpSession session, final Map<String, Object> model) {
        Assert.assertNotNull(session.getAttribute(BookLibraryFilterForm.FORM_NAME));
        Assert.assertNotNull(session.getAttribute(BaseBookLibraryController.PAGE_AND_SORT_NAME));
        Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        Assert.assertNotNull(model.get(BookLibraryFilterForm.FORM_NAME));
        Assert.assertNotNull(model.get(BookLibrarySelectionForm.FORM_NAME));
    }
}
