package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import static org.junit.Assert.assertNotNull;

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

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;

public class ProviewAuditControllerTest {
	//private static final Logger log = Logger.getLogger(ProviewAuditControllerTest.class);
	private ProviewAuditController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ProviewAuditService mockAuditService;
	private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() throws Exception {
    	this.request = new MockHttpServletRequest();
    	this.response = new MockHttpServletResponse();
    	this.mockAuditService = EasyMock.createMock(ProviewAuditService.class);

    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	controller = new ProviewAuditController();
    	controller.setAuditService(mockAuditService);
    }
    
	@SuppressWarnings("unchecked")
	@Test
	public void testAuditListInboundGet() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_PROVIEW_AUDIT_LIST);
    	request.setMethod(HttpMethod.GET.name());
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findProviewAudits(
    				EasyMock.anyObject(ProviewAuditFilter.class), EasyMock.anyObject(ProviewAuditSort.class))).andReturn(new ArrayList<ProviewAudit>());
    	EasyMock.expect(mockAuditService.numberProviewAudits(
				EasyMock.anyObject(ProviewAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_PROVIEW_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(false, pageAndSort.isAscendingSort());
    	Assert.assertEquals(DisplayTagSortProperty.REQUEST_DATE, pageAndSort.getSortProperty());
    	
    	EasyMock.verify(mockAuditService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAuditListPaging() throws Exception {
    	// Set up the request URL
		int newPageNumber = 2;
    	request.setRequestURI("/"+WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("page", String.valueOf(newPageNumber));
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findProviewAudits(
    				EasyMock.anyObject(ProviewAuditFilter.class), EasyMock.anyObject(ProviewAuditSort.class))).andReturn(new ArrayList<ProviewAudit>());
    	EasyMock.expect(mockAuditService.numberProviewAudits(
				EasyMock.anyObject(ProviewAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);
    	
       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_PROVIEW_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
       	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(newPageNumber, pageAndSort.getPageNumber().intValue());

    	EasyMock.verify(mockAuditService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAuditListSorting() throws Exception {
		String direction = "asc";
		String sort = DisplayTagSortProperty.TITLE_ID.toString();
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("sort", sort);
    	request.setParameter("dir", direction);
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findProviewAudits(
    				EasyMock.anyObject(ProviewAuditFilter.class), EasyMock.anyObject(ProviewAuditSort.class))).andReturn(new ArrayList<ProviewAudit>());
    	EasyMock.expect(mockAuditService.numberProviewAudits(
				EasyMock.anyObject(ProviewAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_PROVIEW_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(sort, pageAndSort.getSortProperty().toString());
    	Assert.assertEquals(true, pageAndSort.isAscendingSort());
    	
    	EasyMock.verify(mockAuditService);
	}
	
	/**
	 * Verify the state of the session and request (model) as expected before the
	 * rendering of the Audit List page.
	 */
	public static void validateModel(HttpSession session, Map<String,Object> model) {
    	Assert.assertNotNull(session.getAttribute(ProviewAuditFilterForm.FORM_NAME));
    	Assert.assertNotNull(session.getAttribute(BaseProviewAuditController.PAGE_AND_SORT_NAME));
    	Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    	Assert.assertNotNull(model.get(ProviewAuditFilterForm.FORM_NAME));
    	Assert.assertNotNull(model.get(ProviewAuditForm.FORM_NAME));
	}
}