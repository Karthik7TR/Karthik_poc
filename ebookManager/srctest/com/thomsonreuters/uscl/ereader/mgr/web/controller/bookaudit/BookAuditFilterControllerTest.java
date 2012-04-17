package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class BookAuditFilterControllerTest {
	//private static final Logger log = Logger.getLogger(BookAuditControllerTest.class);
	private BookAuditFilterController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private EBookAuditService mockAuditService;
	private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	this.mockAuditService = EasyMock.createMock(EBookAuditService.class);

    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new BookAuditFilterController();
    	controller.setAuditService(mockAuditService);
    	

    }
    

	@Test
	public void testAuditListFilterPost() throws Exception {
    	// Set up the request URL
		// Filter form values
		String titleId = "uscl/junit/test/abc";
		String fromDate = "01/01/2012";
		String toDate = "03/01/2012";
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_LIST_FILTER_POST);
    	request.setMethod(HttpMethod.POST.name());
    	// The filter values
    	request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
    	request.setParameter("fromDateString", fromDate);
    	request.setParameter("toDateString", toDate);
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class), EasyMock.anyObject(EbookAuditSort.class))).andReturn(new ArrayList<EbookAudit>());
		EasyMock.expect(mockAuditService.numberEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
		EasyMock.replay(mockAuditService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	Map<String,Object> model = mav.getModel();
    	BookAuditControllerTest.validateModel(session, model);
    	
    	// Verify the saved filter form
    	BookAuditFilterForm filterForm = (BookAuditFilterForm) model.get(BookAuditFilterForm.FORM_NAME);
    	Assert.assertEquals(titleId, filterForm.getTitleId());
    	Assert.assertEquals(fromDate, filterForm.getFromDateString());
    	Assert.assertEquals(toDate, filterForm.getToDateString());
    	
    	EasyMock.verify(mockAuditService);
	}
}
