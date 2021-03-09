package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
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

public final class BookAuditFilterControllerTest {
    //private static final Logger log = LogManager.getLogger(BookAuditControllerTest.class);
    private BookAuditFilterController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private EBookAuditService mockAuditService;
    private OutageService outageService;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockAuditService = EasyMock.createMock(EBookAuditService.class);
        outageService = EasyMock.createMock(OutageService.class);
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new BookAuditFilterController(mockAuditService, outageService, null);
    }

    @Test
    public void testAuditListFilterGetWithoutParams() throws Exception {
        testAuditListFilterGet();
    }

    @Test
    public void testAuditListFilterGetWithParams() throws Exception {
        // Set up the request URL
        // Filter form values
        final String titleId = "uscl/junit/test/abc";
        final String fromDate = "01/01/2012";
        final String toDate = "03/01/2012";
        // The filter values
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("fromDateString", fromDate);
        request.setParameter("toDateString", toDate);

        Map<String, Object> model = testAuditListFilterGet();

        // Verify the saved filter form
        final BookAuditFilterForm filterForm = (BookAuditFilterForm) model.get(BookAuditFilterForm.FORM_NAME);
        Assert.assertEquals(titleId, filterForm.getTitleId());
        Assert.assertEquals(fromDate, filterForm.getFromDateString());
        Assert.assertEquals(toDate, filterForm.getToDateString());
    }

    public Map<String, Object> testAuditListFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_AUDIT_LIST_FILTER);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        EasyMock
                .expect(
                        mockAuditService.findEbookAudits(
                                EasyMock.anyObject(EbookAuditFilter.class),
                                EasyMock.anyObject(EbookAuditSort.class)))
                .andReturn(new ArrayList<>());
        EasyMock.expect(mockAuditService.numberEbookAudits(EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
        EasyMock.replay(mockAuditService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        Assert.assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        BookAuditControllerTest.validateModel(session, model);

        EasyMock.verify(mockAuditService);

        return model;
    }
}
