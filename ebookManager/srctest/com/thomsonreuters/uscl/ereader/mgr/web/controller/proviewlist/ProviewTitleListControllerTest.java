package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;

public class ProviewTitleListControllerTest {
	private ProviewTitleListController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private HandlerAdapter handlerAdapter;
	private ProviewClient mockProviewClient;
	private ManagerService mockManagerService;
	private BookDefinitionService mockBookDefinitionService;
	private ProviewAuditService mockProviewAuditService;
	private MessageSourceAccessor mockMessageSourceAccessor;
	private JobRequestService mockJobRequestService;
			
	MockHttpSession session;
	
	@Before
	public void SetUp() throws Exception {
		this.request = new MockHttpServletRequest();
		this.response =new MockHttpServletResponse();
		
		handlerAdapter = new AnnotationMethodHandlerAdapter();
		this.mockProviewClient = EasyMock.createMock(ProviewClient.class);
		this.mockManagerService = EasyMock.createMock(ManagerService.class);
		this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
		this.mockProviewAuditService = EasyMock.createMock(ProviewAuditService.class);
		this.mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
		this.mockJobRequestService = EasyMock.createMock(JobRequestService.class);
		
		this.controller = new ProviewTitleListController();
		controller.setBookDefinitionService(mockBookDefinitionService);
		controller.setJobRequestService(mockJobRequestService);
		controller.setManagerService(mockManagerService);
		controller.setMessageSourceAccessor(mockMessageSourceAccessor);
		controller.setProviewAuditService(mockProviewAuditService);
		controller.setProviewClient(mockProviewClient);
	}
	
	@Test
	public void testSelectedLatestProviewTitleInfo() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLES);
		request.setMethod(HttpMethod.GET.name());
		HttpSession session = request.getSession();
		session.setAttribute(ProviewListFilterForm.FORM_NAME, controller.fetchSavedProviewListFilterForm(session));
		ProviewTitleForm mockTitleForm = new ProviewTitleForm();
		mockTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
		session.setAttribute(ProviewTitleForm.FORM_NAME, mockTitleForm);
		session.setAttribute(WebConstants.KEY_PAGE_SIZE, mockTitleForm.getObjectsPerPage());
		Map<String, ProviewTitleContainer> testAllTitleInfo = new HashMap<>();
		ArrayList<ProviewTitleInfo> testAllLatestTitleInfo = new ArrayList<>();
		
		EasyMock.expect(mockProviewClient.getAllProviewTitleInfo()).andReturn(testAllTitleInfo);
		EasyMock.expect(mockProviewClient.getAllLatestProviewTitleInfo(testAllTitleInfo)).andReturn(testAllLatestTitleInfo);
		EasyMock.replay(mockProviewClient);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
		Map<String,Object> model = mav.getModel();
		Assert.assertEquals(model.get(WebConstants.KEY_PAGE_SIZE), "20");
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSingleTitleAllVersions() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
		HttpSession session = request.getSession();
		session.setAttribute(ProviewListFilterForm.FORM_NAME, controller.fetchSavedProviewListFilterForm(session));
		ProviewTitleForm mockTitleForm = new ProviewTitleForm();
		mockTitleForm.setTitleId(WebConstants.KEY_TITLE_ID);
		Map<String, ProviewTitleContainer> testAllTitleInfo = new HashMap<>();
		testAllTitleInfo.put(mockTitleForm.getTitleId(), new ProviewTitleContainer());
		
		EasyMock.expect(mockProviewClient.getAllProviewTitleInfo()).andReturn(testAllTitleInfo);
		EasyMock.replay(mockProviewClient);

		BookDefinition mockBookDefinition = new BookDefinition();
		mockBookDefinition.setIsSplitBook(false);
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(WebConstants.KEY_TITLE_ID)).andReturn(mockBookDefinition);
		EasyMock.replay(mockBookDefinitionService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLE_ALL_VERSIONS);
		Map<String,Object> model = mav.getModel();
		
		Assert.assertEquals(false, model.get("isSplitBook"));
		
		EasyMock.verify(mockProviewClient);
		EasyMock.verify(mockBookDefinitionService);
	}
	
	@Test
	public void testPostSelectionsRefresh() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLES);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", ProviewTitleForm.Command.REFRESH.toString());
		ProviewTitleForm mockTitleForm = new ProviewTitleForm();
		mockTitleForm.setCommand(Command.REFRESH);
		
		Map<String, ProviewTitleContainer> mockAllProviewTitleInfo = new HashMap<>();
		mockAllProviewTitleInfo.put(mockTitleForm.getCommand().toString(), new ProviewTitleContainer());
		ArrayList<ProviewTitleInfo> mockAllLatestProviewTitleInfo = new ArrayList<>();
		ProviewTitleInfo testInfo = new ProviewTitleInfo();
		testInfo.setTitle("test");
		mockAllLatestProviewTitleInfo.add(testInfo);
		
		EasyMock.expect(mockProviewClient.getAllProviewTitleInfo()).andReturn(mockAllProviewTitleInfo);
		EasyMock.expect(mockProviewClient.getAllLatestProviewTitleInfo(mockAllProviewTitleInfo)).andReturn(mockAllLatestProviewTitleInfo);
		EasyMock.replay(mockProviewClient);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
		Map<String,Object> model = mav.getModel();
		
		Assert.assertEquals("20", model.get("pageSize"));
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testPostSelectionsPageSize() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLES);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", ProviewTitleForm.Command.PAGESIZE.toString());
		HttpSession testSession = request.getSession();
		List<ProviewTitleInfo> testTitleInfo = new ArrayList<ProviewTitleInfo>();
		ProviewTitleInfo testInfo = new ProviewTitleInfo();
		testTitleInfo.add(testInfo);
		testSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, testTitleInfo);
		testSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "5");
		testSession.setAttribute(WebConstants.KEY_PAGE_SIZE, WebConstants.DEFAULT_PAGE_SIZE);
		request.setSession(testSession);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
		Map<String,Object> model = mav.getModel();
		
		Assert.assertEquals(1, model.get("resultSize"));
		
	}
	
	@Test
	public void testProviewTitleDelete() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_DELETE);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
		request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
		request.setParameter("status", WebConstants.KEY_STATUS);
		request.setParameter("lastUpdate", "test");
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Map<String, Object> model = mav.getModel();
		
		Assert.assertEquals(WebConstants.KEY_TITLE_ID, model.get("titleId"));
		Assert.assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
		Assert.assertEquals(WebConstants.KEY_STATUS, model.get("status"));
	}
	
	@Test
	public void testProviewTitleRemove() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_REMOVE);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
		request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
		request.setParameter("status", WebConstants.KEY_STATUS);
		request.setParameter("lastUpdate", "test");
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Map<String, Object> model = mav.getModel();
		
		Assert.assertEquals(WebConstants.KEY_TITLE_ID, model.get("titleId"));
		Assert.assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
		Assert.assertEquals(WebConstants.KEY_STATUS, model.get("status"));
	}
	
	@Test
	public void testProviewTitlePromote() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_PROMOTE);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
		request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
		request.setParameter("status", WebConstants.KEY_STATUS);
		request.setParameter("lastUpdate", "test");
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Map<String,Object> model = mav.getModel();
		
		Assert.assertEquals(WebConstants.KEY_TITLE_ID, model.get("titleId"));
		Assert.assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
		Assert.assertEquals(WebConstants.KEY_STATUS, model.get("status"));
	}

	/* TODO: May need to refactor and create a User Service to finish this test */
	/*@Test
	public void testProviewTitleRemovePost() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_REMOVE);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("titleId", ProviewTitleForm.FORM_NAME.toString());
		request.setParameter("version", WebConstants.KEY_VERSION_NUMBER.toString());
		SimpleGrantedAuthority gAuth = new SimpleGrantedAuthority("User");
		List<GrantedAuthority> auth = new ArrayList<>();
		auth.add(gAuth);
		CobaltUser user = new CobaltUser("test", "test", "test", "test", auth);
		
		//EasyMock.expect(UserUtils.getAuthenticatedUserEmail()).andReturn("test");
		//EasyMock.replay(UserUtils);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
	}*/
}
