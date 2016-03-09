package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertNotNull;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;

public class ProviewGroupListControllerTest {
	
	private ProviewGroupListController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private OutageService mockOutageService;
	private AnnotationMethodHandlerAdapter handlerAdapter;
	private ProviewClient mockProviewClient;
	private ManagerService mockManagerService;
	private BookDefinitionService mockBookDefinitionService;
	private ProviewAuditService mockProviewAuditService;
	private MessageSourceAccessor mockMessageSourceAccessor;
	private JobRequestService mockJobRequestService;
	private ArrayList<ProviewTitleInfo> mockProviewTitleInfo;
	
	@Before
	public void SetUp() throws Exception {
		this.request = new MockHttpServletRequest();
		this.response =new MockHttpServletResponse();
		
		handlerAdapter = new AnnotationMethodHandlerAdapter();
		this.mockOutageService = EasyMock.createMock(OutageService.class);
		this.mockProviewClient = EasyMock.createMock(ProviewClient.class);
		this.mockManagerService = EasyMock.createMock(ManagerService.class);
		this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
		this.mockProviewAuditService = EasyMock.createMock(ProviewAuditService.class);
		this.mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
		this.mockJobRequestService = EasyMock.createMock(JobRequestService.class);
		
		this.controller = new ProviewGroupListController();
		controller.setBookDefinitionService(mockBookDefinitionService);
		controller.setJobRequestService(mockJobRequestService);
		controller.setManagerService(mockManagerService);
		controller.setMessageSourceAccessor(mockMessageSourceAccessor);
		controller.setProviewAuditService(mockProviewAuditService);
		controller.setProviewClient(mockProviewClient);
		
		this.mockProviewTitleInfo = new ArrayList<ProviewTitleInfo>();
	}
	
	@Test
	public void testSelectedLatestProviewGroupInfo() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUPS);
		request.setMethod(HttpMethod.GET.name());
		HttpSession session = request.getSession();
		session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchSavedProviewGroupForm(session));
		ProviewTitleForm mockTitleForm = new ProviewTitleForm();
		mockTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
		session.setAttribute(ProviewTitleForm.FORM_NAME, mockTitleForm);
		session.setAttribute(WebConstants.KEY_PAGE_SIZE, mockTitleForm.getObjectsPerPage());
		List<ProviewGroup> allProviewGroups = new ArrayList<ProviewGroup>();
		ArrayList<ProviewTitleInfo> testAllLatestTitleInfo = new ArrayList<>();
		
		EasyMock.expect(mockProviewClient.getAllProviewGroupInfo()).andReturn(allProviewGroups);
		//EasyMock.expect(mockProviewClient.getAllLatestProviewTitleInfo(testAllTitleInfo)).andReturn(testAllLatestTitleInfo);
		EasyMock.replay(mockProviewClient);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);
		Map<String,Object> model = mav.getModel();
		Assert.assertEquals(model.get(WebConstants.KEY_PAGE_SIZE), "20");
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSingleGroupAllTitles() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("groupIdByVersion", "groupId/v1");
		HttpSession session = request.getSession();
		session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchSavedProviewGroupForm(session));
		ProviewTitleForm mockTitleForm = new ProviewTitleForm();
		mockTitleForm.setTitleId(WebConstants.KEY_TITLE_ID);
		Map<String, ProviewTitleContainer> testAllTitleInfo = new HashMap<>();
		testAllTitleInfo.put(mockTitleForm.getTitleId(), new ProviewTitleContainer());
		
		EasyMock.expect(controller.getGroupInfoByVersion("groupId", new Long(1))).andReturn(null);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_TITLE_ALL_VERSIONS);
		
	}

}
