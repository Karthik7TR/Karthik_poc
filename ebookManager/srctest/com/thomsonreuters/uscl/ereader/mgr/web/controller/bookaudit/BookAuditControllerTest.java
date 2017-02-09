package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm.DisplayTagSortProperty;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class BookAuditControllerTest
{
    //private static final Logger log = LogManager.getLogger(BookAuditControllerTest.class);
    private BookAuditController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private EBookAuditService mockAuditService;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() throws Exception
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockAuditService = EasyMock.createMock(EBookAuditService.class);

        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new BookAuditController();
        controller.setAuditService(mockAuditService);
    }

    @Test
    public void testAuditListInboundGet() throws Exception
    {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock
            .expect(
                mockAuditService.findEbookAudits(
                    EasyMock.anyObject(EbookAuditFilter.class),
                    EasyMock.anyObject(EbookAuditSort.class)))
            .andReturn(new ArrayList<EbookAudit>());
        EasyMock.expect(mockAuditService.numberEbookAudits(EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(false, pageAndSort.isAscendingSort());
        Assert.assertEquals(DisplayTagSortProperty.SUBMITTED_DATE, pageAndSort.getSortProperty());

        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testSpecificAuditListInboundGet() throws Exception
    {
        final Long bookDefinitionId = 1L;
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_SPECIFIC);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", bookDefinitionId.toString());
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock
            .expect(
                mockAuditService.findEbookAudits(
                    EasyMock.anyObject(EbookAuditFilter.class),
                    EasyMock.anyObject(EbookAuditSort.class)))
            .andReturn(new ArrayList<EbookAudit>());
        EasyMock.expect(mockAuditService.numberEbookAudits(EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(false, pageAndSort.isAscendingSort());
        Assert.assertEquals(DisplayTagSortProperty.SUBMITTED_DATE, pageAndSort.getSortProperty());

        final BookAuditFilterForm form = (BookAuditFilterForm) session.getAttribute(BookAuditFilterForm.FORM_NAME);
        Assert.assertEquals(bookDefinitionId, form.getBookDefinitionId());

        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testAuditListPaging() throws Exception
    {
        // Set up the request URL
        final int newPageNumber = 2;
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock
            .expect(
                mockAuditService.findEbookAudits(
                    EasyMock.anyObject(EbookAuditFilter.class),
                    EasyMock.anyObject(EbookAuditSort.class)))
            .andReturn(new ArrayList<EbookAudit>());
        EasyMock.expect(mockAuditService.numberEbookAudits(EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(newPageNumber, pageAndSort.getPageNumber().intValue());

        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testAuditListSorting() throws Exception
    {
        final String direction = "asc";
        final String sort = DisplayTagSortProperty.ACTION.toString();
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("sort", sort);
        request.setParameter("dir", direction);
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock
            .expect(
                mockAuditService.findEbookAudits(
                    EasyMock.anyObject(EbookAuditFilter.class),
                    EasyMock.anyObject(EbookAuditSort.class)))
            .andReturn(new ArrayList<EbookAudit>());
        EasyMock.expect(mockAuditService.numberEbookAudits(EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);

        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(sort, pageAndSort.getSortProperty().toString());
        Assert.assertEquals(true, pageAndSort.isAscendingSort());

        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testAuditDetail() throws Exception
    {
        final Long auditId = 1L;
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(auditId);
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_DETAIL);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", auditId.toString());

        // Record expected service calls
        EasyMock.expect(mockAuditService.findEBookAuditByPrimaryKey(auditId)).andReturn(audit);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_DETAIL, mav.getViewName());
        final Map<String, Object> model = mav.getModel();

        final EbookAudit actualAudit = (EbookAudit) model.get(WebConstants.KEY_BOOK_AUDIT_DETAIL);
        Assert.assertEquals(audit, actualAudit);

        EasyMock.verify(mockAuditService);
    }

    /**
     * Test the submission of the multi-selected rows, or changing the number of objects displayed per page.
     * @throws Exception
     */
    @Test
    public void testChangeDisplayedRowsPerPage() throws Exception
    {
        final int EXPECTED_OBJECTS_PER_PAGE = 33;
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_CHANGE_ROW_COUNT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("objectsPerPage", String.valueOf(EXPECTED_OBJECTS_PER_PAGE));
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock
            .expect(
                mockAuditService.findEbookAudits(
                    EasyMock.anyObject(EbookAuditFilter.class),
                    EasyMock.anyObject(EbookAuditSort.class)))
            .andReturn(new ArrayList<EbookAudit>());
        EasyMock.expect(mockAuditService.numberEbookAudits(EasyMock.anyObject(EbookAuditFilter.class))).andReturn(33);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(session, model);
        // Ensure the number of rows was changed
        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(EXPECTED_OBJECTS_PER_PAGE, pageAndSort.getObjectsPerPage().intValue());

        EasyMock.verify(mockAuditService);
    }

    /**
     * Verify the state of the session and request (model) as expected before the
     * rendering of the Audit List page.
     */
    public static void validateModel(final HttpSession session, final Map<String, Object> model)
    {
        Assert.assertNotNull(session.getAttribute(BookAuditFilterForm.FORM_NAME));
        Assert.assertNotNull(session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME));
        Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        Assert.assertNotNull(model.get(BookAuditFilterForm.FORM_NAME));
        Assert.assertNotNull(model.get(BookAuditForm.FORM_NAME));
    }
}
