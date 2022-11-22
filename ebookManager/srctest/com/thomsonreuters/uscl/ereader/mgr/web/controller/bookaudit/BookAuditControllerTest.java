package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfileService;
import com.thomsonreuters.uscl.ereader.core.book.userprofile.UserProfiles;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm.DisplayTagSortProperty;
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
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
public final class BookAuditControllerTest {
    private static final String SLASH = "/";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    @InjectMocks
    private BookAuditController controller;
    @Mock
    private EBookAuditService mockAuditService;
    @Mock
    private UserProfileService mockUserProfiles;
    @Mock
    private OutageService outageService;
    @Spy
    @SuppressWarnings("unused")
    private final Validator validator = new BookAuditFilterFormValidator();

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    @Test
    public void auditList_noParametersAreGiven_requiredServicesAreCalledAndListReturned() throws Exception {
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        when(mockAuditService.findEbookAudits(any(EbookAuditFilter.class), any(EbookAuditSort.class)))
                .thenReturn(new ArrayList<>());
        when(mockAuditService.numberEbookAudits(any(EbookAuditFilter.class))).thenReturn(0);
        when(outageService.getAllPlannedOutagesToDisplay()).thenReturn(Collections.emptyList());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(model);
        verifyMocksInteractions();
    }

    public static void validateModel(final Map<String, Object> model) {
        assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNotNull(model.get(BookAuditFilterForm.FORM_NAME));
        assertNotNull(model.get(WebConstants.KEY_PAGE_SIZE));
        assertTrue(model.get("org.springframework.validation.BindingResult.ebookAuditFilterForm").toString().contains("0 errors"));
    }

    private void verifyMocksInteractions() {
        verify(mockAuditService).findEbookAudits(any(), any());
        verify(mockAuditService).numberEbookAudits(any());
        verify(outageService).getAllPlannedOutagesToDisplay();
    }

    @Test
    public void auditList_bookDefinitionIdIsGiven_listReturned() throws Exception {
        final Long bookDefinitionId = 1L;
        final String userid="c286054";
        UserProfiles userProfiles = null;
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("bookDefinitionId", bookDefinitionId.toString());
        final EbookAudit audit = new EbookAudit();
        audit.setEbookDefinitionId(bookDefinitionId);
        when(mockAuditService.findEbookAudits(any(EbookAuditFilter.class), any(EbookAuditSort.class)))
                .thenReturn(Collections.singletonList(audit));
        when(mockAuditService.numberEbookAudits(any(EbookAuditFilter.class))).thenReturn(0);
        when(mockUserProfiles.getUserProfileById((userid))).thenReturn(userProfiles);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(model);
        final BookAuditPaginatedList paginatedList = getPaginatedList(model);
        final List<EbookAudit> audits = paginatedList.getList();
        assertEquals(bookDefinitionId, audits.get(0).getEbookDefinitionId());
        verifyMocksInteractions();
    }

    private BookAuditPaginatedList getPaginatedList(final Map<String, Object> model) {
        final BookAuditPaginatedList paginatedList = (BookAuditPaginatedList) model.get(WebConstants.KEY_PAGINATED_LIST);
        assertNotNull(paginatedList);
        return paginatedList;
    }

    @Test
    public void auditList_pageIsGiven_listWithGivenPageIsReturned() throws Exception {
        final int newPageNumber = 2;
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("page", String.valueOf(newPageNumber));
        when(mockAuditService.findEbookAudits(any(EbookAuditFilter.class), any(EbookAuditSort.class)))
                .thenReturn(Collections.emptyList());
        when(mockAuditService.numberEbookAudits(any(EbookAuditFilter.class))).thenReturn(0);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(model);
        assertEquals(newPageNumber, getPaginatedList(model).getPageNumber());
        verifyMocksInteractions();
    }

    @Test
    public void auditList_ascendingSortingIsGiven_sortedListReturned() throws Exception {
        final String direction = "asc";
        final String sort = DisplayTagSortProperty.ACTION.toString();
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("sort", sort);
        request.setParameter("dir", direction);
        when(mockAuditService.findEbookAudits(any(EbookAuditFilter.class), any(EbookAuditSort.class)))
                .thenReturn(Collections.emptyList());
        when(mockAuditService.numberEbookAudits(any(EbookAuditFilter.class))).thenReturn(0);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(model);
        assertEquals(Boolean.TRUE, getPaginatedList(model).isAscendingSort());
        assertEquals(sort, getPaginatedList(model).getSortCriterion());
        verifyMocksInteractions();
    }

    @Test
    public void testAuditDetail() throws Exception {
        final Long auditId = 1L;
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(auditId);
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_DETAIL);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", auditId.toString());
        when(mockAuditService.findEBookAuditByPrimaryKey(auditId)).thenReturn(audit);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_BOOK_AUDIT_DETAIL, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final EbookAudit actualAudit = (EbookAudit) model.get(WebConstants.KEY_BOOK_AUDIT_DETAIL);
        assertEquals(audit, actualAudit);
        verify(mockAuditService).findEBookAuditByPrimaryKey(auditId);
    }

    @Test
    public void auditList_objectsPerPageParamIsGiven_paginatedListWithGivenOppIsReturned() throws Exception {
        final int expectedObjectsPerPage = 33;
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("objectsPerPage", String.valueOf(expectedObjectsPerPage));
        when(mockAuditService.findEbookAudits(any(EbookAuditFilter.class), any(EbookAuditSort.class)))
                .thenReturn(Collections.emptyList());
        when(mockAuditService.numberEbookAudits(any(EbookAuditFilter.class))).thenReturn(77);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        validateModel(model);
        assertEquals(expectedObjectsPerPage, getPaginatedList(model).getObjectsPerPage());
        verifyMocksInteractions();
    }

    @Test
    public void auditList_noParamsAreGiven_paginatedListIsReturned() throws Exception {
        testAuditListFilterGet();
    }

    @Test
    public void auditList_filteringParamsAreGiven_paginatedListIsReturned() throws Exception {
        final String titleId = "uscl/junit/test/abc";
        final String fromDate = "01/01/2022 00:00:00";
        final String toDate = "03/01/2022 00:00:00";
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("fromDateString", fromDate);
        request.setParameter("toDateString", toDate);

        final Map<String, Object> model = testAuditListFilterGet();

        final BookAuditFilterForm filterForm = (BookAuditFilterForm) model.get(BookAuditFilterForm.FORM_NAME);
        assertEquals(titleId, filterForm.getTitleId());
        assertEquals(fromDate, filterForm.getFromDateString());
        assertEquals(toDate, filterForm.getToDateString());
    }

    private Map<String, Object> testAuditListFilterGet() throws Exception {
        request.setRequestURI(SLASH + WebConstants.MVC_BOOK_AUDIT_LIST);
        request.setMethod(HttpMethod.GET.name());
        when(mockAuditService.findEbookAudits(any(EbookAuditFilter.class), any(EbookAuditSort.class)))
                .thenReturn(Collections.emptyList());
        when(mockAuditService.numberEbookAudits(any(EbookAuditFilter.class))).thenReturn(0);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        validateModel(model);
        verifyMocksInteractions();
        return model;
    }
}
