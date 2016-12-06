package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerServiceImpl;
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
		this.mockManagerService = EasyMock.createMock(ManagerServiceImpl.class);
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
	
	@After
	public void reset() {
		SecurityContextHolder.getContext().setAuthentication(null);
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
	public void testDownloadProviewListExcel() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD);
		request.setMethod(HttpMethod.GET.name());

		List<ProviewTitleInfo> titles = new ArrayList<ProviewTitleInfo>();
		
		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, titles);
		request.setSession(session);
		handlerAdapter.handle(request, response, controller);

		ServletOutputStream outStream = response.getOutputStream();
		Assert.assertTrue(!outStream.toString().isEmpty());
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
	
	@Test
	public void testProviewTitlePromotePost() throws Exception {
		String uName = "tester";
		String first = "first";
		String last = "last";
		String pWord = "testing";
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		CobaltUser user = new CobaltUser(uName, first, last, pWord, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user,null);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_PROMOTE);
		request.setMethod(HttpMethod.POST.name());
		String titleId = "anId";
		request.setParameter("titleId", titleId);
		String version = "2";
		request.setParameter("version", version);
		String status = "test";
		request.setParameter("status", status);
		request.setParameter("command", ProviewTitleForm.Command.PROMOTE.toString());
		
		BookDefinition bookDefinition = EasyMock.createNiceMock(BookDefinition.class);
		Long definitionId = new Long(127);
		

		EasyMock.expect(mockProviewClient.promoteTitle(titleId, version)).andReturn("");
		EasyMock.replay(mockProviewClient);
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).andReturn(bookDefinition).times(2);
		EasyMock.expect(mockBookDefinitionService.saveBookDefinition(bookDefinition)).andReturn(null);
		EasyMock.replay(mockBookDefinitionService);
		
		EasyMock.expect(bookDefinition.getEbookDefinitionId()).andReturn(definitionId).times(2);
		EasyMock.expect(bookDefinition.isSplitBook()).andReturn(false);
		EasyMock.replay(bookDefinition);
		
		EasyMock.expect(mockJobRequestService.isBookInJobRequest(definitionId)).andReturn(false);
		EasyMock.replay(mockJobRequestService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE, mav.getViewName());
	}
	
	@Test
	public void testProviewTitleRemovePost() throws Exception {
		String uName = "tester";
		String first = "first";
		String last = "last";
		String pWord = "testing";
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		CobaltUser user = new CobaltUser(uName, first, last, pWord, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user,null);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_REMOVE);
		request.setMethod(HttpMethod.POST.name());
		String titleId = "anId";
		request.setParameter("titleId", titleId);
		String version = "2";
		request.setParameter("version", version);
		String status = "test";
		request.setParameter("status", status);
		request.setParameter("command", ProviewTitleForm.Command.REMOVE.toString());

		EasyMock.expect(mockProviewClient.removeTitle(titleId, version)).andReturn("");
		EasyMock.replay(mockProviewClient);
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(WebConstants.VIEW_PROVIEW_TITLE_REMOVE, mav.getViewName());
	}
	
	@Test
	public void testProviewTitleDeletePost() throws Exception {
		String uName = "tester";
		String first = "first";
		String last = "last";
		String pWord = "testing";
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		CobaltUser user = new CobaltUser(uName, first, last, pWord, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user,null);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_TITLE_DELETE);
		request.setMethod(HttpMethod.POST.name());
		String titleId = "anId";
		request.setParameter("titleId", titleId);
		String version = "2";
		request.setParameter("version", version);
		String status = "test";
		request.setParameter("status", status);
		request.setParameter("command", ProviewTitleForm.Command.DELETE.toString());

		EasyMock.expect(mockProviewClient.deleteTitle(titleId, version)).andReturn("");
		EasyMock.replay(mockProviewClient);
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, mav.getViewName());
	}
}
