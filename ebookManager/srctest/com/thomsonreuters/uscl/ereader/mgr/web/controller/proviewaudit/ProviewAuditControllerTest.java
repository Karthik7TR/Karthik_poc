package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm.parseDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditFilterForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewAuditControllerTest {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    @InjectMocks
    private ProviewAuditController controller;
    @Mock
    @SuppressWarnings("unused")
    private ProviewAuditFilterFormValidator validator;
    @Mock
    private ProviewAuditService auditService;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        when(validator.supports(any())).thenReturn(true);
    }

    @Test
    public void auditList_noQueryStringIsGiven_paginatedListIsSortedByDefault() throws Exception {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        mockAuditServiceCalls();

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        validateProviewAuditListModelAndView(mav);
        final PaginatedList paginatedList = (PaginatedList) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        assertNotNull(paginatedList);
        assertEquals(SortOrderEnum.ASCENDING, paginatedList.getSortDirection());
        assertEquals(DisplayTagSortProperty.REQUEST_DATE.toString(), paginatedList.getSortCriterion());
        verifyAuditServiceCalls();
    }

    @Test
    public void auditList_pageNumberTwoIsGiven_secondPageOfPaginatedListIsReturned() throws Exception {
        // Set up the request URL
        final int newPageNumber = 2;
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));
        mockAuditServiceCalls();

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        validateProviewAuditListModelAndView(mav);
        final PaginatedList paginatedList = (PaginatedList) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        assertEquals(newPageNumber, paginatedList.getPageNumber());
        verifyAuditServiceCalls();
    }

    @Test
    public void auditList_sortingParametersAreGiven_paginatedListIsSortedAccordingly() throws Exception {
        final String direction = "asc";
        final String sort = DisplayTagSortProperty.TITLE_ID.toString();
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("sort", sort);
        request.setParameter("dir", direction);

        mockAuditServiceCalls();

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        // Verify
        validateProviewAuditListModelAndView(mav);
        final PaginatedList paginatedList = (PaginatedList) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        assertEquals(sort, paginatedList.getSortCriterion());
        assertEquals(SortOrderEnum.ASCENDING, paginatedList.getSortDirection());
        verifyAuditServiceCalls();
    }

    @Test
    public void auditList_objectsPerPageParameterIsGiven_paginatedListWithGivenOPPIsReturned() throws Exception {
        final int expectedRowCount = 42;
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_OBJECTS_PER_PAGE, String.valueOf(expectedRowCount));
        mockAuditServiceCalls();

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        validateProviewAuditListModelAndView(mav);
        final PaginatedList paginatedList = (PaginatedList) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        final int actualRowCount = paginatedList.getObjectsPerPage();
        assertEquals(expectedRowCount, actualRowCount);
    }

    @Test
    public void auditList_filteringParametersAreGiven_filteringParametersArePassedToService() throws Exception {
        // Set up the request URL
        // Filter form values
        final String titleId = "uscl/junit/test/abc%";
        final String fromDate = "01/01/2012 00:00:00";
        final String toDate = "03/01/2012 00:00:00";
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        // The filter values
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("requestFromDateString", fromDate);
        request.setParameter("requestToDateString", toDate);
        final ArgumentCaptor<ProviewAuditFilter> auditFilterCaptor = ArgumentCaptor.forClass(ProviewAuditFilter.class);
        when(auditService.findProviewAudits(auditFilterCaptor.capture(), any(ProviewAuditSort.class)))
                .thenReturn(Collections.emptyList());
        when(auditService.numberProviewAudits(any(ProviewAuditFilter.class))).thenReturn(0);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        validateProviewAuditListModelAndView(mav);
        final ProviewAuditFilter proviewAuditFilter = auditFilterCaptor.getValue();
        assertEquals(titleId, proviewAuditFilter.getTitleId());
        assertEquals(fromDate, parseDate(proviewAuditFilter.getFrom()));
        assertEquals(toDate, parseDate(proviewAuditFilter.getTo()));
        verify(auditService).findProviewAudits(any(ProviewAuditFilter.class), any(ProviewAuditSort.class));
        verify(auditService).numberProviewAudits(any(ProviewAuditFilter.class));
    }

    private void mockAuditServiceCalls() {
        when(auditService.findProviewAudits(any(ProviewAuditFilter.class), any(ProviewAuditSort.class))).thenReturn(new ArrayList<>());
        when(auditService.numberProviewAudits(any(ProviewAuditFilter.class))).thenReturn(0);
    }

    private void verifyAuditServiceCalls() {
        verify(auditService).findProviewAudits(any(ProviewAuditFilter.class), any(ProviewAuditSort.class));
        verify(auditService).numberProviewAudits(any(ProviewAuditFilter.class));
    }

    private void validateProviewAuditListModelAndView(final ModelAndView modelAndView) {
        assertNotNull(modelAndView);
        assertEquals(WebConstants.VIEW_PROVIEW_AUDIT_LIST, modelAndView.getViewName());
        final Map<String, Object> model = modelAndView.getModel();
        assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNotNull(model.get(WebConstants.KEY_PAGE_SIZE));
        assertNotNull(model.get(ProviewAuditFilterForm.FORM_NAME));
    }
}
