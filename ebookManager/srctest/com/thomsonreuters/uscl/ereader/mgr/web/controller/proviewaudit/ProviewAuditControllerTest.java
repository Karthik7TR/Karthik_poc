package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
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

public final class ProviewAuditControllerTest {
    private ProviewAuditController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ProviewAuditService mockAuditService;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockAuditService = EasyMock.createMock(ProviewAuditService.class);

        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new ProviewAuditController(mockAuditService);
    }

    @Test
    public void testAuditListInboundGet() throws Exception {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        recordExpectedServiceCalls();

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        validateProviewAuditListModelAndView(mav, session);
        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(false, pageAndSort.isAscendingSort());
        Assert.assertEquals(DisplayTagSortProperty.REQUEST_DATE, pageAndSort.getSortProperty());

        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testAuditListPaging() throws Exception {
        // Set up the request URL
        final int newPageNumber = 2;
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));
        final HttpSession session = request.getSession();

        recordExpectedServiceCalls();

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        validateProviewAuditListModelAndView(mav, session);
        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(newPageNumber, pageAndSort.getPageNumber().intValue());

        EasyMock.verify(mockAuditService);
    }

    @Test
    public void testAuditListSorting() throws Exception {
        final String direction = "asc";
        final String sort = DisplayTagSortProperty.TITLE_ID.toString();
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("sort", sort);
        request.setParameter("dir", direction);
        final HttpSession session = request.getSession();

        recordExpectedServiceCalls();

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        validateProviewAuditListModelAndView(mav, session);
        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
        Assert.assertEquals(sort, pageAndSort.getSortProperty().toString());
        Assert.assertEquals(true, pageAndSort.isAscendingSort());

        EasyMock.verify(mockAuditService);
    }

    @Test
    @SneakyThrows
    public void testAuditListUpdateNumberOfRows() {
        final int expectedRowCount = 122;
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("objectsPerPage", String.valueOf(expectedRowCount));

        recordExpectedServiceCalls();

        final HttpSession session = request.getSession();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        validateProviewAuditListModelAndView(modelAndView, session);
        final PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
        final int actualRowCount = pageAndSort.getObjectsPerPage();
        Assert.assertEquals(expectedRowCount, actualRowCount);
    }

    private void recordExpectedServiceCalls() {
        EasyMock
            .expect(
                mockAuditService.findProviewAudits(
                    EasyMock.anyObject(ProviewAuditFilter.class),
                    EasyMock.anyObject(ProviewAuditSort.class)))
            .andReturn(new ArrayList<>());
        EasyMock.expect(mockAuditService.numberProviewAudits(EasyMock.anyObject(ProviewAuditFilter.class)))
            .andReturn(0);
        EasyMock.replay(mockAuditService);
    }

    private void validateProviewAuditListModelAndView(final ModelAndView modelAndView, final HttpSession session) {
        assertNotNull(modelAndView);
        Assert.assertEquals(WebConstants.VIEW_PROVIEW_AUDIT_LIST, modelAndView.getViewName());
        final Map<String, Object> model = modelAndView.getModel();
        validateModel(session, model);
    }

    /**
     * Verify the state of the session and request (model) as expected before the
     * rendering of the Audit List page.
     */
    public static void validateModel(final HttpSession session, final Map<String, Object> model) {
        Assert.assertNotNull(session.getAttribute(ProviewAuditFilterForm.FORM_NAME));
        Assert.assertNotNull(session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME));
        Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        Assert.assertNotNull(model.get(ProviewAuditFilterForm.FORM_NAME));
        Assert.assertNotNull(model.get(ProviewAuditForm.FORM_NAME));
    }
}
