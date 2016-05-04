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
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

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
	private PublishingStatsService mockPublishingStatsService;
	
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
		this.mockPublishingStatsService = EasyMock.createMock(PublishingStatsService.class);
		
		this.controller = new ProviewGroupListController();
		controller.setBookDefinitionService(mockBookDefinitionService);
		controller.setJobRequestService(mockJobRequestService);
		controller.setManagerService(mockManagerService);
		controller.setMessageSourceAccessor(mockMessageSourceAccessor);
		controller.setProviewAuditService(mockProviewAuditService);
		controller.setProviewClient(mockProviewClient);
		controller.setPublishingStatsService(mockPublishingStatsService);
		
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
	
	@Test
	public void testPostSelectionsForGroupsRefresh() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUPS);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", ProviewGroupForm.Command.REFRESH.toString());
		ArrayList<ProviewGroup> allProviewGroups = new ArrayList<>();
		
		EasyMock.expect(mockProviewClient.getAllProviewGroupInfo()).andReturn(allProviewGroups);
		EasyMock.replay(mockProviewClient);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testPostSelectionsForGroupsPagesize() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUPS);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", ProviewGroupForm.Command.PAGESIZE.toString());
		ArrayList<ProviewGroup> allProviewGroups = new ArrayList<>();
		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, allProviewGroups);
		session.setAttribute("groupSize", WebConstants.KEY_TOTAL_GROUP_SIZE);
		session.setAttribute("pageSize", WebConstants.KEY_PAGE_SIZE);
		request.setSession(session);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);
	}
	
	@Test
	public void testSingleGroupTitleAllVersions() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupIdByVersion", "12131/v1");
		ProviewAudit audit = new ProviewAudit();
		audit.setBookVersion("3");
		SplitNodeInfo splitNode = new SplitNodeInfo();
		BookDefinition definition = new BookDefinition();
		definition.setEbookDefinitionId((long) 3);
		definition.setFullyQualifiedTitleId("34v1");
		splitNode.setBookDefinition(definition);
		splitNode.setBookVersionSubmitted("3");
		splitNode.setSpitBookTitle("split title");
		splitNode.setSplitNodeGuid("01234678901234567890123456789012");
		List<SplitNodeInfo> splitNodes = new ArrayList<>();
		splitNodes.add(splitNode);
		definition.setSplitNodes(splitNodes);
		String titleID = "uscl/an/book_lohisplitnodeinfo";
		List<ProviewAudit> removedAuditList = new ArrayList<>();
		removedAuditList.add(audit);
		String fullyQualifiedTitleId = "34v1";
		
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleID)).andReturn(definition);
		EasyMock.replay(mockBookDefinitionService);
		
		EasyMock.expect(mockProviewAuditService.getRemovedAndDeletedVersions(fullyQualifiedTitleId)).andReturn(removedAuditList);
		EasyMock.replay(mockProviewAuditService);
		
		EasyMock.expect(mockProviewClient.getProviewGroupInfo("12131", "v1")).andReturn(proviewResponse);
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(fullyQualifiedTitleId)).andReturn(proviewResponse);
		EasyMock.replay(mockProviewClient);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_TITLE_ALL_VERSIONS);
	
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockProviewClient);
		EasyMock.verify(mockProviewAuditService);
	}
	
	@Test
	public void testPerformGroupOperationsPromote() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupCmd", GroupCmd.PROMOTE.toString());
		List<String> groupMembers = new ArrayList<>();
		groupMembers.add("test");
		request.setParameter("groupMembers", groupMembers.toString());
		HttpSession session = request.getSession();
		List<GroupDetails> subgroup = new ArrayList<>();
		GroupDetails details = new GroupDetails();
		details.setId("[test]");
		details.setTitleId("titleTest");
		String[] aString = {"test1", "test2"};
		details.setTitleIdtWithVersionArray(aString);
		subgroup.add(details);
		session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
		request.setSession(session);
				
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
	}
	
	@Test
	public void testPerformGroupOperationsRemove() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupCmd", GroupCmd.REMOVE.toString());
		List<String> groupMembers = new ArrayList<>();
		groupMembers.add("test");
		request.setParameter("groupMembers", groupMembers.toString());
		HttpSession session = request.getSession();
		List<GroupDetails> subgroup = new ArrayList<>();
		GroupDetails details = new GroupDetails();
		details.setId("[test]");
		details.setTitleId("titleTest");
		String[] aString = {"test1", "test2"};
		details.setTitleIdtWithVersionArray(aString);
		subgroup.add(details);
		session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
		request.setSession(session);
				
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
	}
	
	@Test
	public void testPerformGroupOperationsDelete() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupCmd", GroupCmd.DELETE.toString());
		List<String> groupMembers = new ArrayList<>();
		groupMembers.add("test");
		request.setParameter("groupMembers", groupMembers.toString());
		HttpSession session = request.getSession();
		List<GroupDetails> subgroup = new ArrayList<>();
		GroupDetails details = new GroupDetails();
		details.setId("[test]");
		details.setTitleId("titleTest");
		String[] aString = {"test1", "test2"};
		details.setTitleIdtWithVersionArray(aString);
		subgroup.add(details);
		session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
		request.setSession(session);
				
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
	}
	
	@Test
	public void testPerformGroupOperations() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupCmd", "NONE");
		List<String> groupMembers = new ArrayList<>();
		groupMembers.add("test");
		request.setParameter("groupMembers", groupMembers.toString());
		HttpSession session = request.getSession();
		List<GroupDetails> subgroup = new ArrayList<>();
		GroupDetails details = new GroupDetails();
		details.setId("[test]");
		details.setTitleId("titleTest");
		String[] aString = {"test1", "test2"};
		details.setTitleIdtWithVersionArray(aString);
		subgroup.add(details);
		session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
		request.setSession(session);
				
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_TITLE_ALL_VERSIONS);
	}
	
	@Test
	public void testGetGroupDetailsWithNoSubgroups() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupIdByVersion", "12131/v1");
		ProviewAudit audit = new ProviewAudit();
		audit.setBookVersion("3");
		SplitNodeInfo splitNode = new SplitNodeInfo();
		BookDefinition definition = new BookDefinition();
		definition.setEbookDefinitionId((long) 3);
		definition.setFullyQualifiedTitleId("34v1");
		splitNode.setBookDefinition(definition);
		splitNode.setBookVersionSubmitted("3");
		splitNode.setSpitBookTitle("split title");
		splitNode.setSplitNodeGuid("01234678901234567890123456789012");
		List<SplitNodeInfo> splitNodes = new ArrayList<>();
		splitNodes.add(splitNode);
		definition.setSplitNodes(splitNodes);
		String titleID = "uscl/an/book_lohisplitnodeinfo";
		String titleID2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>";
		List<ProviewAudit> removedAuditList = new ArrayList<>();
		removedAuditList.add(audit);
		String fullyQualifiedTitleId = "34v1";
		String title = "test";
		
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleID)).andReturn(definition);
		EasyMock.replay(mockBookDefinitionService);
		
		EasyMock.expect(mockProviewAuditService.getRemovedAndDeletedVersions("uscl/an/book_lohisplitnodeinfo/v1")).andReturn(removedAuditList);
		EasyMock.replay(mockProviewAuditService);
		
		EasyMock.expect(mockProviewClient.getProviewGroupInfo("12131", "v1")).andReturn(proviewResponse);
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle("uscl/an/book_lohisplitnodeinfo/v1")).andReturn(titleID2);
		EasyMock.replay(mockProviewClient);
		
		EasyMock.expect(mockPublishingStatsService.findNameByBoofDefAndVersion((long) 3, "3")).andReturn("test");	
		EasyMock.replay(mockPublishingStatsService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_TITLE_ALL_VERSIONS);
	
		EasyMock.verify(mockBookDefinitionService);
		EasyMock.verify(mockProviewClient);
		EasyMock.verify(mockProviewAuditService);
		EasyMock.verify(mockPublishingStatsService);
	}
}










