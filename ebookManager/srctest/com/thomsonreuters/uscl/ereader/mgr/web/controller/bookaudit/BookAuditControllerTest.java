package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm.DisplayTagSortProperty;

public class BookAuditControllerTest {
	//private static final Logger log = Logger.getLogger(BookAuditControllerTest.class);
	private BookAuditController controller;
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
    	
    	controller = new BookAuditController();
    	controller.setAuditService(mockAuditService);
    	

    }
    
	@SuppressWarnings("unchecked")
	@Test
	public void testAuditListInboundGet() throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_LIST);
    	request.setMethod(HttpMethod.GET.name());
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEbookAudits(
    				EasyMock.anyObject(EbookAuditFilter.class), EasyMock.anyObject(EbookAuditSort.class))).andReturn(new ArrayList<EbookAudit>());
    	EasyMock.expect(mockAuditService.numberEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(false, pageAndSort.isAscendingSort());
    	Assert.assertEquals(DisplayTagSortProperty.SUBMITTED_DATE, pageAndSort.getSortProperty());
    	
    	EasyMock.verify(mockAuditService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSpecificAuditListInboundGet() throws Exception {
		Long bookDefinitionId = 1L;
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_SPECIFIC);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", bookDefinitionId.toString());
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEbookAudits(
    				EasyMock.anyObject(EbookAuditFilter.class), EasyMock.anyObject(EbookAuditSort.class))).andReturn(new ArrayList<EbookAudit>());
    	EasyMock.expect(mockAuditService.numberEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(false, pageAndSort.isAscendingSort());
    	Assert.assertEquals(DisplayTagSortProperty.SUBMITTED_DATE, pageAndSort.getSortProperty());
    	
    	BookAuditFilterForm form = (BookAuditFilterForm) session.getAttribute(BookAuditFilterForm.FORM_NAME);
    	Assert.assertEquals(bookDefinitionId, form.getBookDefinitionId());
    	
    	EasyMock.verify(mockAuditService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAuditListPaging() throws Exception {
    	// Set up the request URL
		int newPageNumber = 2;
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("page", String.valueOf(newPageNumber));
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEbookAudits(
    				EasyMock.anyObject(EbookAuditFilter.class), EasyMock.anyObject(EbookAuditSort.class))).andReturn(new ArrayList<EbookAudit>());
    	EasyMock.expect(mockAuditService.numberEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);
    	
       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
       	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(newPageNumber, pageAndSort.getPageNumber().intValue());

    	EasyMock.verify(mockAuditService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAuditListSorting() throws Exception {
		String direction = "asc";
		String sort = DisplayTagSortProperty.ACTION.toString();
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("sort", sort);
    	request.setParameter("dir", direction);
    	HttpSession session = request.getSession();
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEbookAudits(
    				EasyMock.anyObject(EbookAuditFilter.class), EasyMock.anyObject(EbookAuditSort.class))).andReturn(new ArrayList<EbookAudit>());
    	EasyMock.expect(mockAuditService.numberEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class))).andReturn(0);
    	EasyMock.replay(mockAuditService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(sort, pageAndSort.getSortProperty().toString());
    	Assert.assertEquals(true, pageAndSort.isAscendingSort());
    	
    	EasyMock.verify(mockAuditService);
	}
	
	@Test
	public void testAuditDetail() throws Exception {
		Long auditId = 1L;
		EbookAudit audit = new EbookAudit();
		audit.setAuditId(auditId);
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_DETAIL);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", auditId.toString());
    	
    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEBookAuditByPrimaryKey(auditId)).andReturn(audit);
    	EasyMock.replay(mockAuditService);

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_DETAIL, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	
    	EbookAudit actualAudit = (EbookAudit) model.get(WebConstants.KEY_BOOK_AUDIT_DETAIL);
    	Assert.assertEquals(audit, actualAudit);
    	
    	EasyMock.verify(mockAuditService);
	}
	
	/**
	 * Test the submission of the multi-selected rows, or changing the number of objects displayed per page.
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testChangeDisplayedRowsPerPage() throws Exception {
		int EXPECTED_OBJECTS_PER_PAGE = 33;
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_AUDIT_CHANGE_ROW_COUNT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("objectsPerPage", String.valueOf(EXPECTED_OBJECTS_PER_PAGE));
    	HttpSession session = request.getSession();

    	// Record expected service calls
    	EasyMock.expect(mockAuditService.findEbookAudits(
    				EasyMock.anyObject(EbookAuditFilter.class), EasyMock.anyObject(EbookAuditSort.class))).andReturn(new ArrayList<EbookAudit>());
    	EasyMock.expect(mockAuditService.numberEbookAudits(
				EasyMock.anyObject(EbookAuditFilter.class))).andReturn(33);
    	EasyMock.replay(mockAuditService);
    	

       	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	// Verify
    	assertNotNull(mav);
    	Assert.assertEquals(WebConstants.VIEW_BOOK_AUDIT_LIST, mav.getViewName());
    	Map<String,Object> model = mav.getModel();
    	validateModel(session, model);
    	// Ensure the number of rows was changed
    	PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME);
    	Assert.assertEquals(EXPECTED_OBJECTS_PER_PAGE, pageAndSort.getObjectsPerPage().intValue());
    	
    	EasyMock.verify(mockAuditService);
	}
	
	/**
	 * Verify the state of the session and request (model) as expected before the
	 * rendering of the Audit List page.
	 */
	public static void validateModel(HttpSession session, Map<String,Object> model) {
    	Assert.assertNotNull(session.getAttribute(BookAuditFilterForm.FORM_NAME));
    	Assert.assertNotNull(session.getAttribute(BaseBookAuditController.PAGE_AND_SORT_NAME));
    	Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
    	Assert.assertNotNull(model.get(BookAuditFilterForm.FORM_NAME));
    	Assert.assertNotNull(model.get(BookAuditForm.FORM_NAME));
	}
}
