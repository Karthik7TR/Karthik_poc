package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
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
	PublishingStatsService mockPublishingStatsService;
	
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
	}
	
	protected BookDefinition mockBookDefinition(){
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		bookDefinition.setFullyQualifiedTitleId("uscl/an/abc");
		List<SplitNodeInfo> splitNodes = new ArrayList<SplitNodeInfo>();
		SplitNodeInfo splitNodeInfo1 = new SplitNodeInfo();
		splitNodeInfo1.setBookDefinition(bookDefinition);
		splitNodeInfo1.setBookVersionSubmitted("1");
		splitNodeInfo1.setSpitBookTitle("part1");
		splitNodeInfo1.setSplitNodeGuid("tocGuid1");
		splitNodes.add(splitNodeInfo1);
		SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
		splitNodeInfo2.setBookDefinition(bookDefinition);
		splitNodeInfo2.setBookVersionSubmitted("1");
		splitNodeInfo2.setSpitBookTitle("part2");
		splitNodeInfo2.setSplitNodeGuid("tocGuid2");
		splitNodes.add(splitNodeInfo2);
		
		
		bookDefinition.setSplitNodes(splitNodes);
		Date date = Calendar.getInstance().getTime();
		bookDefinition.setLastUpdated(date);
		
		return bookDefinition; 
	}
	
	@Test
	public void testSelectedLatestProviewGroupInfo() throws Exception {
		request.setRequestURI("/"+WebConstants.MVC_PROVIEW_GROUPS);
		request.setMethod(HttpMethod.GET.name());
		HttpSession session = request.getSession();
		session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchSavedProviewGroupForm(session));
		List<ProviewGroup> allProviewGroups = new ArrayList<ProviewGroup>();
		ArrayList<ProviewTitleInfo> testAllLatestTitleInfo = new ArrayList<>();
		
		EasyMock.expect(mockProviewClient.getAllProviewGroupInfo()).andReturn(allProviewGroups);
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
	public void testgetGroupDetailsWithSubGroupsNull() throws Exception {
		BookDefinition bookDefinition = mockBookDefinition();
		List<SplitNodeInfo> splitNodes = new ArrayList<SplitNodeInfo>();
		bookDefinition.setSplitNodes(splitNodes);
		List<ProviewAudit> removedAuditList = new ArrayList<ProviewAudit>();
		Map<String, List<String>> subGroupVersionMap = new HashMap<String, List<String>>();
		ProviewException proviewException = new ProviewException("404 does not exist");
		
		EasyMock.expect(mockProviewAuditService.getRemovedAndDeletedVersions(bookDefinition.getFullyQualifiedTitleId())).andReturn(removedAuditList);
		EasyMock.replay(mockProviewAuditService);
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(bookDefinition.getFullyQualifiedTitleId())).andThrow(proviewException);
		
		EasyMock.replay(mockProviewClient);
		List<GroupDetails> groupDetails = controller.getGroupDetailsWithSubGroups(bookDefinition, subGroupVersionMap, "1");
		EasyMock.verify(mockProviewAuditService);
		EasyMock.verify(mockProviewClient);
		Assert.assertTrue(groupDetails.isEmpty());
	}
	
	@Test
	public void testgetGroupDetailsWithSubGroupsAllRemoved() throws Exception {
		BookDefinition bookDefinition = mockBookDefinition();
		List<SplitNodeInfo> splitNodes = new ArrayList<SplitNodeInfo>();
		bookDefinition.setSplitNodes(splitNodes);
		List<ProviewAudit> removedAuditList = new ArrayList<ProviewAudit>();
		ProviewAudit audit = new ProviewAudit();
		audit.setBookVersion("v1");
		audit.setProviewRequest("Remove");
		audit.setTitleId(bookDefinition.getFullyQualifiedTitleId());
		removedAuditList.add(audit);
		
		Map<String, List<String>> subGroupVersionMap = new HashMap<String, List<String>>();
		List<String> versions = new ArrayList<String>();
		versions.add("1");
		subGroupVersionMap.put("subgroup name",versions);
		
		ProviewException proviewException = new ProviewException("404 does not exist");
		
		EasyMock.expect(mockProviewAuditService.getRemovedAndDeletedVersions(bookDefinition.getFullyQualifiedTitleId())).andReturn(removedAuditList);
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(bookDefinition.getFullyQualifiedTitleId())).andThrow(proviewException);
		EasyMock.expect(mockProviewAuditService.getBookStatus(bookDefinition.getFullyQualifiedTitleId(), "v1")).andReturn("Remove");
		EasyMock.expect(mockPublishingStatsService.findNameByBoofDefAndVersion(new Long (1), "v1")).andReturn("Proview Display Name");
		EasyMock.replay(mockProviewAuditService);
		EasyMock.replay(mockPublishingStatsService);
		
		EasyMock.replay(mockProviewClient);
		List<GroupDetails> groupDetails = controller.getGroupDetailsWithSubGroups(bookDefinition, subGroupVersionMap, "1");
		EasyMock.verify(mockProviewAuditService);
		EasyMock.verify(mockProviewClient);
		EasyMock.verify(mockPublishingStatsService);
		Assert.assertFalse(groupDetails.isEmpty());
		Assert.assertEquals(1, groupDetails.size());
		Assert.assertEquals("Remove", groupDetails.get(0).getBookStatus());
		Assert.assertEquals("v1", groupDetails.get(0).getBookVersion());
		Assert.assertEquals("subgroup name", groupDetails.get(0).getSubGroupName());
		
	}
	
	@Test
	public void testFilterSplitTitles(){
		Map<String,List<String>> versionSplitTitleMap = new HashMap<String,List<String>>();
		List<String> titles = new ArrayList<String>();
		titles.add("title1");
		versionSplitTitleMap.put("v1.1", titles);
		versionSplitTitleMap.put("v2.0", titles);
		versionSplitTitleMap.put("v2.1", titles);
		versionSplitTitleMap.put("v2.3", titles);
		String version = "1"; 
		Map<String, List<String>> filter = controller.filterSplitTitles(versionSplitTitleMap, version);
		Assert.assertEquals(1,filter.size());
		Assert.assertEquals(titles, filter.get("v1.1"));
		Assert.assertTrue(filter.get("v2.0")==null);
		
		version = "2"; 
		filter = controller.filterSplitTitles(versionSplitTitleMap, version);
		Assert.assertEquals(3,filter.size());
		Assert.assertTrue(filter.get("v2.0")!=null);
		Assert.assertTrue(filter.get("v2.1")!=null);
		Assert.assertTrue(filter.get("v2.3")!=null);
		Assert.assertTrue(filter.get("v1.0")==null);
	}
	
	@Test
	public void testgetGroupDetailsWithNoSubGroupsNull() throws Exception {
		BookDefinition bookDefinition = mockBookDefinition();
		List<SplitNodeInfo> splitNodes = new ArrayList<SplitNodeInfo>();
		bookDefinition.setSplitNodes(splitNodes);
		List<ProviewAudit> removedAuditList = new ArrayList<ProviewAudit>();
		
		ProviewException proviewException = new ProviewException("404 does not exist");
		
		EasyMock.expect(mockProviewAuditService.getRemovedAndDeletedVersions(bookDefinition.getFullyQualifiedTitleId())).andReturn(removedAuditList);
		EasyMock.replay(mockProviewAuditService);
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(bookDefinition.getFullyQualifiedTitleId())).andThrow(proviewException);
		
		EasyMock.replay(mockProviewClient);
		List<String> splitVersions = new ArrayList<String>();
		List<GroupDetails> groupDetails = controller.getGroupDetailsWithNoSubgroups(bookDefinition.getFullyQualifiedTitleId(), new Long(1),splitVersions);
		EasyMock.verify(mockProviewAuditService);
		EasyMock.verify(mockProviewClient);
		Assert.assertTrue(groupDetails.isEmpty());
	}
	
	@Test
	public void testgetGroupDetailsWithNoSubGroups() throws Exception {
		BookDefinition bookDefinition = mockBookDefinition();
		List<SplitNodeInfo> splitNodes = new ArrayList<SplitNodeInfo>();
		bookDefinition.setSplitNodes(splitNodes);
		List<ProviewAudit> removedAuditList = new ArrayList<ProviewAudit>();
		ProviewAudit audit = new ProviewAudit();
		audit.setBookVersion("v1");
		audit.setProviewRequest("Remove");
		audit.setTitleId(bookDefinition.getFullyQualifiedTitleId());
		removedAuditList.add(audit);
		ProviewException proviewException = new ProviewException("404 does not exist");
		
		EasyMock.expect(mockProviewAuditService.getRemovedAndDeletedVersions(bookDefinition.getFullyQualifiedTitleId())).andReturn(removedAuditList);
		EasyMock.replay(mockProviewAuditService);
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(bookDefinition.getFullyQualifiedTitleId())).andThrow(proviewException);
		
		EasyMock.replay(mockProviewClient);
		List<String> splitVersions = new ArrayList<String>();
		List<GroupDetails> groupDetails = controller.getGroupDetailsWithNoSubgroups(bookDefinition.getFullyQualifiedTitleId(), new Long(1),splitVersions);
		EasyMock.verify(mockProviewAuditService);
		EasyMock.verify(mockProviewClient);
		Assert.assertFalse(groupDetails.isEmpty());
		Assert.assertEquals(1, groupDetails.size());
		Assert.assertEquals("Remove", groupDetails.get(0).getBookStatus());
		Assert.assertEquals("v1", groupDetails.get(0).getBookVersion());
	}
	
	
}
