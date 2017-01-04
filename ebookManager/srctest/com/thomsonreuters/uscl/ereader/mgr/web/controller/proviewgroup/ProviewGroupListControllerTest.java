package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertNotNull;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerServiceImpl;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class ProviewGroupListControllerTest {

	private ProviewGroupListController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private AnnotationMethodHandlerAdapter handlerAdapter;
	private ProviewHandler mockProviewHandler;
	private BookDefinitionService mockBookDefinitionService;
	private ManagerService mockManagerService;
	private ProviewAuditService mockProviewAuditService;
	private MessageSourceAccessor mockMessageSourceAccessor;
	private JobRequestService mockJobRequestService;
	private PublishingStatsService mockPublishingStatsService;

	@Before
	public void setUp() throws Exception {
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();

		handlerAdapter = new AnnotationMethodHandlerAdapter();
		this.mockProviewHandler = EasyMock.createMock(ProviewHandler.class);
		this.mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
		this.mockManagerService = EasyMock.createMock(ManagerServiceImpl.class);
		this.mockProviewAuditService = EasyMock.createMock(ProviewAuditService.class);
		this.mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
		this.mockJobRequestService = EasyMock.createMock(JobRequestService.class);
		this.mockPublishingStatsService = EasyMock.createMock(PublishingStatsService.class);

		this.controller = new ProviewGroupListController();
		controller.setJobRequestService(mockJobRequestService);
		controller.setBookDefinitionService(mockBookDefinitionService);
		controller.setManagerService(mockManagerService);
		controller.setMessageSourceAccessor(mockMessageSourceAccessor);
		controller.setProviewAuditService(mockProviewAuditService);
		controller.setProviewHandler(mockProviewHandler);
		controller.setPublishingStatsService(mockPublishingStatsService);
	}

	@After
	public void reset() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	/**
	 * Test /ebookManager/proviewGroups.mvc command = REFRESH
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostSelectionsForGroupsRefresh() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", ProviewGroupForm.Command.REFRESH.toString());
		Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<String, ProviewGroupContainer>();
		List<ProviewGroup> allLatestProviewGroups = new ArrayList<ProviewGroup>();

		EasyMock.expect(mockProviewHandler.getAllLatestProviewGroupInfo(allProviewGroups))
				.andReturn(allLatestProviewGroups);
		EasyMock.expect(mockProviewHandler.getAllProviewGroupInfo()).andReturn(allProviewGroups);
		EasyMock.replay(mockProviewHandler);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);

		EasyMock.verify(mockProviewHandler);
	}

	/**
	 * Test /ebookManager/proviewGroups.mvc command = PAGESIZE
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostSelectionsForGroupsPagesize() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
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

	/**
	 * Test /ebookManager/proviewGroups.mvc method = GET
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllLatestProviewGroupsList() throws Exception {
		String groupName = "GroupName";
		String groupId = "GroupID";

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
		request.setMethod(HttpMethod.GET.name());
		HttpSession session = request.getSession();

		ProviewGroupListFilterForm filterForm = new ProviewGroupListFilterForm();
		filterForm.setGroupName("%" + groupName);
		filterForm.setProviewGroupID("%" + groupId);
		session.setAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);
		session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchProviewGroupForm(session));

		ProviewTitleForm mockTitleForm = new ProviewTitleForm();
		mockTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
		session.setAttribute(ProviewTitleForm.FORM_NAME, mockTitleForm);
		session.setAttribute(WebConstants.KEY_PAGE_SIZE, mockTitleForm.getObjectsPerPage());
		Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<String, ProviewGroupContainer>();
		List<ProviewGroup> allLatestProviewGroups = new ArrayList<ProviewGroup>();
		ProviewGroup proviewGroup = new ProviewGroup();
		proviewGroup.setGroupName(groupName);
		proviewGroup.setGroupId(groupId);
		allLatestProviewGroups.add(proviewGroup);

		EasyMock.expect(mockProviewHandler.getAllLatestProviewGroupInfo(allProviewGroups))
				.andReturn(allLatestProviewGroups);
		EasyMock.expect(mockProviewHandler.getAllProviewGroupInfo()).andReturn(allProviewGroups);
		EasyMock.replay(mockProviewHandler);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);

		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);
		Map<String, Object> model = mav.getModel();
		Assert.assertEquals(model.get(WebConstants.KEY_PAGE_SIZE), "20");

		EasyMock.verify(mockProviewHandler);
	}

	/**
	 * Test /ebookManager/proviewGroupsAllVersions.mvc?groupIds=<groupID>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingleGroupAllVersions() throws Exception {
		String groupId = "groupID";

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("groupIds", groupId);
		HttpSession session = request.getSession();
		session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchProviewGroupForm(session));

		List<ProviewGroup> groupList = new ArrayList<ProviewGroup>();
		ProviewGroupContainer groupContainer = new ProviewGroupContainer();
		groupContainer.setProviewGroups(groupList);
		Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<String, ProviewGroupContainer>();
		allProviewGroups.put(groupId, groupContainer);

		EasyMock.expect(mockProviewHandler.getAllProviewGroupInfo()).andReturn(allProviewGroups);
		EasyMock.replay(mockProviewHandler);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS);
	}

	/**
	 * Test /ebookManager/proviewGroupSingleVersion.mvc?groupIdByVersion=
	 * <groupIDsbyVersion>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingleGroupTitleSingleVersion() throws Exception {
		String groupId = "a/group_testID";
		String version = "v1";
		String titleId = "a/title/testID";
		String titleIdv = titleId + "/v1";
		String subGroupName = "Test";

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("groupIdByVersion", groupId + "/" + version);
		HttpSession session = request.getSession();
		session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchProviewGroupForm(session));

		Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<String, ProviewGroupContainer>();
		ProviewGroupContainer groupContainer = new ProviewGroupContainer();
		allProviewGroups.put(groupId, groupContainer);

		List<ProviewGroup> groupList = new ArrayList<ProviewGroup>();
		groupContainer.setProviewGroups(groupList);

		ProviewGroup proviewGroup = new ProviewGroup();
		proviewGroup.setHeadTitle(groupId);
		proviewGroup.setGroupId(groupId);
		proviewGroup.setGroupVersion(version);
		proviewGroup.setGroupStatus("OK");
		groupList.add(proviewGroup);

		List<SubgroupInfo> subgroupList = new ArrayList<SubgroupInfo>();
		proviewGroup.setSubgroupInfoList(subgroupList);

		SubgroupInfo subgroup = new SubgroupInfo();
		subgroupList.add(subgroup);

		List<String> titleIdList = new ArrayList<String>();
		subgroup.setTitleIdList(titleIdList);
		titleIdList.add(titleIdv);

		ProviewTitleContainer titleContainer = new ProviewTitleContainer();
		List<ProviewTitleInfo> proviewTitles = new ArrayList<ProviewTitleInfo>();
		titleContainer.setProviewTitleInfos(proviewTitles);

		ProviewTitleInfo title = new ProviewTitleInfo();
		title.setVersion("v1.3");
		title.setTitle(titleId);
		title.setLastupdate("5");
		proviewTitles.add(title);

		List<GroupDetails> groupDetails = new ArrayList<GroupDetails>();
		GroupDetails details = new GroupDetails();
		details.setTitleId(titleId);
		details.setBookVersion(version);
		groupDetails.add(details);
		
		BookDefinition mockBookDefinition = new BookDefinition();
		mockBookDefinition.setPilotBookStatus(PilotBookStatus.IN_PROGRESS);

		EasyMock.expect(mockProviewHandler.getAllProviewGroupInfo()).andReturn(allProviewGroups);
		EasyMock.expect(mockProviewHandler.getProviewTitleContainer(titleId)).andReturn(titleContainer);
		EasyMock.expect(mockProviewHandler.getSingleTitleGroupDetails(titleIdv)).andReturn(groupDetails);
		EasyMock.replay(mockProviewHandler);
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(groupId)).andReturn(mockBookDefinition);
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(groupId)).andReturn(null);
		EasyMock.replay(mockBookDefinitionService);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);

		subgroup.setSubGroupName(subGroupName);

		mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);
		
		EasyMock.verify(mockProviewHandler);
		EasyMock.verify(mockBookDefinitionService);
	}
	
	@Test
	public void testDownloadProviewGroupExcel() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_DOWNLOAD);
		request.setMethod(HttpMethod.GET.name());

		List<ProviewGroup> groups = new ArrayList<ProviewGroup>();
		
		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, groups);
		request.setSession(session);
		handlerAdapter.handle(request, response, controller);

		ServletOutputStream outStream = response.getOutputStream();
		Assert.assertTrue(!outStream.toString().isEmpty());
	}

	@Test
	public void testPerformGroupOperations() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupCmd", GroupCmd.PROMOTE.toString());
		List<String> groupMembers = new ArrayList<>();
		groupMembers.add("test/v1");
		request.setParameter("groupMembers", groupMembers.toString());
		HttpSession session = request.getSession();
		List<GroupDetails> subgroup = new ArrayList<>();
		GroupDetails details = new GroupDetails();
		details.setId("[test");
		details.setTitleId("titleTest");
		details.setBookVersion("v1]");
		String[] aString = { "test1", "test2" };
		details.setTitleIdtWithVersionArray(aString);
		subgroup.add(details);
		session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
		request.setSession(session);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);

		request.setParameter("groupCmd", GroupCmd.REMOVE.toString());
		mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);

		request.setParameter("groupCmd", GroupCmd.DELETE.toString());
		mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
	}

	@Test
	public void testPerformGroupOperationsNoSelection() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_OPERATION);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME.toString());
		request.setParameter("groupCmd", "NONE");

		List<String> groupMembers = new ArrayList<>();
		groupMembers.add("test");
		request.setParameter("groupMembers", groupMembers.toString());

		List<GroupDetails> subgroup = new ArrayList<>();
		GroupDetails details = new GroupDetails();
		details.setId("[test]");
		details.setTitleId("titleTest");
		subgroup.add(details);

		String[] aString = { "test1", "test2" };
		details.setTitleIdtWithVersionArray(aString);

		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
		request.setSession(session);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION);
	}

	/**
	 * Note: runs very slowly due to the "wait 3 seconds" command in
	 * doTitleOperation(..)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProviewTitlePromotePost() throws Exception {
		String uName = "tester";
		String first = "first";
		String last = "last";
		String pWord = "testing";
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		CobaltUser user = new CobaltUser(uName, first, last, pWord, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_PROMOTE);
		request.setMethod(HttpMethod.POST.name());
		List<String> groupIds = new ArrayList<String>();
		String titleId = "anId";
		groupIds.add(titleId);
		request.setParameter("groupIds", groupIds.toString());

		EasyMock.expect(mockProviewHandler.promoteTitle(titleId, "")).andReturn("");

		String groupId = "testId";
		String groupVersion = "2";
		request.setParameter("groupOperation", new Boolean(true).toString());
		request.setParameter("proviewGroupID", groupId);
		request.setParameter("GroupVersion", groupVersion);
		EasyMock.expect(mockProviewHandler.promoteGroup(groupId, "v" + groupVersion)).andReturn("");
		EasyMock.replay(mockProviewHandler);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE);
	}

	/**
	 * Note: runs very slowly due to the "wait 3 seconds" command in
	 * doTitleOperation(..)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProviewTitleRemovePost() throws Exception {
		String uName = "tester";
		String first = "first";
		String last = "last";
		String pWord = "testing";
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		CobaltUser user = new CobaltUser(uName, first, last, pWord, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_REMOVE);
		request.setMethod(HttpMethod.POST.name());
		List<String> groupIds = new ArrayList<String>();
		String titleId = "anId";
		groupIds.add(titleId);
		request.setParameter("groupIds", groupIds.toString());

		EasyMock.expect(mockProviewHandler.removeTitle(titleId, "")).andReturn("");

		String groupId = "testId";
		String groupVersion = "2";
		request.setParameter("groupOperation", new Boolean(true).toString());
		request.setParameter("proviewGroupID", groupId);
		request.setParameter("GroupVersion", groupVersion);
		EasyMock.expect(mockProviewHandler.removeGroup(groupId, "v" + groupVersion)).andReturn("");
		EasyMock.expect(mockProviewHandler.deleteGroup(groupId, "v" + groupVersion)).andReturn("");
		EasyMock.replay(mockProviewHandler);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE);
	}

	/**
	 * Note: runs very slowly due to the "wait 3 seconds" command in
	 * doTitleOperation(..)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProviewTitleDeletePost() throws Exception {
		String uName = "tester";
		String first = "first";
		String last = "last";
		String pWord = "testing";
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		CobaltUser user = new CobaltUser(uName, first, last, pWord, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_DELETE);
		request.setMethod(HttpMethod.POST.name());
		List<String> groupIds = new ArrayList<String>();
		String titleId = "anId";
		groupIds.add(titleId);
		request.setParameter("groupIds", groupIds.toString());

		EasyMock.expect(mockProviewHandler.deleteTitle(titleId, "")).andReturn(true);

		String groupId = "testId";
		String groupVersion = "2";
		request.setParameter("groupOperation", new Boolean(true).toString());
		request.setParameter("proviewGroupID", groupId);
		request.setParameter("GroupVersion", groupVersion);
		EasyMock.expect(mockProviewHandler.deleteGroup(groupId, "v" + groupVersion)).andReturn("").times(2);
		EasyMock.replay(mockProviewHandler);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE);
	}
}