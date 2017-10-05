package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
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

public final class ProviewAuditFilterControllerTest {
    //private static final Logger log = LogManager.getLogger(ProviewAuditFilterControllerTest.class);
    private ProviewAuditFilterController controller;
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

        controller = new ProviewAuditFilterController(mockAuditService, null);
    }

    @Test
    public void testAuditListFilterPost() throws Exception {
        // Set up the request URL
        // Filter form values
        final String titleId = "uscl/junit/test/abc";
        final String fromDate = "01/01/2012";
        final String toDate = "03/01/2012";
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_AUDIT_LIST_FILTER_POST);
        request.setMethod(HttpMethod.POST.name());
        // The filter values
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter("requestFromDateString", fromDate);
        request.setParameter("requestToDateString", toDate);
        final HttpSession session = request.getSession();

        // Record expected service calls
        EasyMock
            .expect(
                mockAuditService.findProviewAudits(
                    EasyMock.anyObject(ProviewAuditFilter.class),
                    EasyMock.anyObject(ProviewAuditSort.class)))
            .andReturn(new ArrayList<ProviewAudit>());
        EasyMock.expect(mockAuditService.numberProviewAudits(EasyMock.anyObject(ProviewAuditFilter.class)))
            .andReturn(0);
        EasyMock.replay(mockAuditService);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        ProviewAuditControllerTest.validateModel(session, model);

        // Verify the saved filter form
        final ProviewAuditFilterForm filterForm = (ProviewAuditFilterForm) model.get(ProviewAuditFilterForm.FORM_NAME);
        Assert.assertEquals(titleId, filterForm.getTitleId());
        Assert.assertEquals(fromDate, filterForm.getRequestFromDateString());
        Assert.assertEquals(toDate, filterForm.getRequestToDateString());

        EasyMock.verify(mockAuditService);
    }
}
