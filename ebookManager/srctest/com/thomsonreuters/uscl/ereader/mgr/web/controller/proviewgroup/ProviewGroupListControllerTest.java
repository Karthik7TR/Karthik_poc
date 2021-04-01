package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewGroupListControllerTest {
    private static final String TITLE_ID = "titleId";
    private static final String TITLE_VERSION = "v1.1";
    private static final String TITLE_VERSION_2 = "v2.1";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_VERSION = "2";
    private static final String GROUP_NAME = "GroupName";
    private static final String REVIEW_STATUS = "Review";
    private static final String REMOVE_STATUS = "Remove";
    private static final String DELETE_STATUS = "Delete";
    private static final String DATE_FORMAT = "yyyyMMdd";

    @InjectMocks
    private ProviewGroupListController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    @Mock
    private ProviewHandler mockProviewHandler;
    @Mock
    private BookDefinitionService mockBookDefinitionService;
    @Mock
    private ProviewAuditService mockProviewAuditService;
    @Mock
    private EmailUtil emailUtil;
    @SuppressWarnings("unused")
    @Mock
    private EmailService mockEmailService;
    @SuppressWarnings("unused")
    @Mock
    private OutageService mockOutageService;
    private Map<String, ProviewGroupContainer> proviewGroups;
    private List<ProviewGroup> selectedProviewGroups;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        proviewGroups = new HashMap<>();
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
        final Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<>();
        final SubgroupInfo subGroupInfo = new SubgroupInfo();
        subGroupInfo.setTitleIdList(Collections.singletonList(TITLE_ID));
        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setSubgroupInfoList(Collections.singletonList(subGroupInfo));
        final ProviewGroupContainer proviewGroupContainer = new ProviewGroupContainer();
        proviewGroupContainer.setProviewGroups(Collections.singletonList(proviewGroup));
        allProviewGroups.put(TITLE_ID, proviewGroupContainer);

        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();
        allLatestProviewGroups.add(proviewGroup);

        when(mockProviewHandler.getAllLatestProviewGroupInfo(allProviewGroups)).thenReturn(allLatestProviewGroups);
        when(mockProviewHandler.getAllProviewGroupInfo()).thenReturn(allProviewGroups);
        when(mockProviewAuditService.findMaxRequestDateByTitleIds(Collections.singleton(TITLE_ID)))
            .thenReturn(Collections.singletonMap(TITLE_ID, new Date()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_GROUPS, mav.getViewName());
        assertEquals(DateFormatUtils.format(new Date(), DATE_FORMAT),
            ((List<ProviewGroup>) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST)).get(0).getLatestUpdateDate());

        verify(mockProviewHandler).getAllProviewGroupInfo();
        verify(mockProviewHandler).getAllLatestProviewGroupInfo(allProviewGroups);
        verify(mockProviewAuditService).findMaxRequestDateByTitleIds(any());
    }

    /**
     * Test /ebookManager/proviewGroups.mvc command = REFRESH
     * with simulating Proview outage.
     *
     * @throws Exception
     */
    @Test(expected = ProviewException.class)
    public void testPostSelectionsForGroupsRefreshProviewException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("command", ProviewGroupForm.Command.REFRESH.toString());

        when(mockProviewHandler.getAllProviewGroupInfo()).thenThrow(new ProviewException(""));

        handlerAdapter.handle(request, response, controller);
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
        final List<ProviewGroup> allProviewGroups = new ArrayList<>();
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, allProviewGroups);
        session.setAttribute("groupSize", WebConstants.KEY_TOTAL_GROUP_SIZE);
        session.setAttribute("pageSize", WebConstants.KEY_PAGE_SIZE);
        request.setSession(session);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_GROUPS, mav.getViewName());
    }

    /**
     * Test /ebookManager/proviewGroups.mvc method = GET
     *
     * @throws Exception
     */
    @Test
    public void testAllLatestProviewGroupsList() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        final ProviewGroupListFilterForm filterForm = new ProviewGroupListFilterForm();
        filterForm.setGroupName("%" + GROUP_NAME);
        filterForm.setProviewGroupID("%" + GROUP_ID);
        session.setAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);
        session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchProviewGroupForm(session));

        final ProviewTitleForm mockTitleForm = new ProviewTitleForm();
        mockTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
        session.setAttribute(ProviewTitleForm.FORM_NAME, mockTitleForm);
        session.setAttribute(WebConstants.KEY_PAGE_SIZE, mockTitleForm.getObjectsPerPage());
        final Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<>();
        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();
        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setGroupName(GROUP_NAME);
        proviewGroup.setGroupId(GROUP_ID);
        allLatestProviewGroups.add(proviewGroup);

        when(mockProviewHandler.getAllLatestProviewGroupInfo(allProviewGroups)).thenReturn(allLatestProviewGroups);
        when(mockProviewHandler.getAllProviewGroupInfo()).thenReturn(allProviewGroups);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUPS, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals("20", model.get(WebConstants.KEY_PAGE_SIZE));

        verify(mockProviewHandler).getAllLatestProviewGroupInfo(allProviewGroups);
        verify(mockProviewHandler).getAllProviewGroupInfo();
    }

    @Test
    public void allLatestProviewGroupsList_exceptionIsThrownByProview_keyErrorOccurredIsSetToTrue() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
        request.setMethod(HttpMethod.GET.name());
        when(mockProviewHandler.getAllProviewGroupInfo()).thenThrow(new ProviewException(""));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        final Map<String, Object> model = mav.getModel();
        assertEquals(Boolean.TRUE, model.get(WebConstants.KEY_ERROR_OCCURRED));
    }

    @Test
    public void allLatestProviewGroupsList_selectedProviewGroupsAttributeIsSet_attributesAreSet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUPS);
        request.setMethod(HttpMethod.GET.name());

        final HttpSession session = request.getSession();
        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setGroupId(GROUP_ID);
        proviewGroup.setGroupName(GROUP_NAME);
        selectedProviewGroups = Collections.singletonList(proviewGroup);
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroups);

        final ProviewGroupForm proviewGroupForm = new ProviewGroupForm();
        final String opp = "42";
        proviewGroupForm.setObjectsPerPage(opp);
        session.setAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        final Map<String, Object> model = mav.getModel();
        assertEquals(selectedProviewGroups, model.get(WebConstants.KEY_PAGINATED_LIST));
        assertEquals(selectedProviewGroups.size(), model.get(WebConstants.KEY_TOTAL_GROUP_SIZE));
        assertNotNull(model.get(ProviewGroupListFilterForm.FORM_NAME));
        final ProviewGroupForm fetchedProviewGroupForm = (ProviewGroupForm) model.get(ProviewGroupForm.FORM_NAME);
        assertEquals(opp, fetchedProviewGroupForm.getObjectsPerPage());
    }

    /**
     * Test /ebookManager/proviewGroupsAllVersions.mvc?groupIds=<groupID>
     *
     * @throws Exception
     */
    @Test
    public void testSingleGroupAllVersions() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("groupIds", GROUP_ID);
        final HttpSession session = request.getSession();
        session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchProviewGroupForm(session));

        final List<ProviewGroup> groupList = new ArrayList<>();
        final ProviewGroupContainer groupContainer = new ProviewGroupContainer();
        groupContainer.setProviewGroups(groupList);
        final Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<>();
        allProviewGroups.put(GROUP_ID, groupContainer);

        when(mockProviewHandler.getAllProviewGroupInfo()).thenReturn(allProviewGroups);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS, mav.getViewName());
        verify(mockProviewHandler).getAllProviewGroupInfo();
    }

    @SneakyThrows
    @Test
    public void singleGroupAllVersions_allProviewGroupsAttributeIsSetButEmpty_bookSizeAttributeIsSetToZero() {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("groupIds", GROUP_ID);

        final HttpSession session = request.getSession();
        final Map<String, ProviewGroupContainer> allProviewGroups = Collections.emptyMap();
        session.setAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS, allProviewGroups);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_ALL_VERSIONS, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals(0, model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
        verifyZeroInteractions(mockProviewHandler);
    }

    /**
     * Test /ebookManager/proviewGroupSingleVersion.mvc?groupIdByVersion=
     * <groupIDsbyVersion>
     *
     * @throws Exception
     */
    @Test
    public void testSingleGroupTitleSingleVersion() throws Exception {
        final String groupId = "a/group_testID";
        final String version = "v1";
        final String titleId = "a/title/testID";
        final String subGroupName = "Test";

        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("groupIdByVersion", groupId + "/" + version);
        final HttpSession session = request.getSession();
        session.setAttribute(ProviewGroupForm.FORM_NAME, controller.fetchProviewGroupForm(session));

        final Map<String, ProviewGroupContainer> allProviewGroups = new HashMap<>();
        final ProviewGroupContainer groupContainer = new ProviewGroupContainer();
        allProviewGroups.put(groupId, groupContainer);

        final List<ProviewGroup> groupList = new ArrayList<>();
        groupContainer.setProviewGroups(groupList);

        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setHeadTitle(groupId);
        proviewGroup.setGroupId(groupId);
        proviewGroup.setGroupVersion(version);
        proviewGroup.setGroupStatus("OK");
        groupList.add(proviewGroup);

        final List<SubgroupInfo> subgroupList = new ArrayList<>();
        proviewGroup.setSubgroupInfoList(subgroupList);

        final SubgroupInfo subgroup = new SubgroupInfo();
        subgroupList.add(subgroup);

        final List<String> titleIdList = new ArrayList<>();
        subgroup.setTitleIdList(titleIdList);
        titleIdList.add(titleId);

        final ProviewTitleContainer titleContainer = new ProviewTitleContainer();
        final List<ProviewTitleInfo> proviewTitles = new ArrayList<>();
        titleContainer.setProviewTitleInfos(proviewTitles);

        final ProviewTitleInfo title = new ProviewTitleInfo();
        title.setVersion("v1.3");
        title.setTitle(titleId);
        title.setLastupdate("5");
        proviewTitles.add(title);

        final List<GroupDetails> groupDetails = new ArrayList<>();
        final GroupDetails details = new GroupDetails();
        details.setTitleId(titleId);
        details.setBookVersion(version);
        groupDetails.add(details);

        final BookDefinition mockBookDefinition = new BookDefinition();
        mockBookDefinition.setPilotBookStatus(PilotBookStatus.IN_PROGRESS);

        when(mockProviewHandler.getAllProviewGroupInfo()).thenReturn(allProviewGroups);
        when(mockProviewHandler.getProviewTitleContainer(titleId)).thenReturn(titleContainer);
        when(mockProviewHandler.getSingleTitleGroupDetails(titleId)).thenReturn(groupDetails);
        when(mockBookDefinitionService.findBookDefinitionByTitle(groupId)).thenReturn(mockBookDefinition);

        ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION, mav.getViewName());

        subgroup.setSubGroupName(subGroupName);

        mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals(PilotBookStatus.IN_PROGRESS, model.get(WebConstants.KEY_PILOT_BOOK_STATUS));

        verify(mockProviewHandler).getAllProviewGroupInfo();
        verify(mockProviewHandler).getProviewTitleContainer(titleId);
        verify(mockProviewHandler).getSingleTitleGroupDetails(titleId);
    }

    @Test
    public void testDownloadProviewGroupExcel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_DOWNLOAD);
        request.setMethod(HttpMethod.GET.name());

        final List<ProviewGroup> groups = new ArrayList<>();

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, groups);
        request.setSession(session);
        handlerAdapter.handle(request, response, controller);

        final ServletOutputStream outStream = response.getOutputStream();
        assertFalse(outStream.toString().isEmpty());
    }

    @Test
    public void testPerformGroupOperations() throws Exception {
        setUser();
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_OPERATION);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME);
        request.setParameter("groupCmd", GroupCmd.PROMOTE.toString());
        final List<String> groupMembers = new ArrayList<>();
        groupMembers.add("test/v1");
        request.setParameter("groupMembers", groupMembers.toString());
        final HttpSession session = request.getSession();
        final List<GroupDetails> subgroup = new ArrayList<>();
        final GroupDetails details = new GroupDetails();
        details.setId("[test");
        details.setTitleId("titleTest");
        details.setBookVersion("v1]");
        final String[] aString = {"test1", "test2"};
        details.setTitleIdWithVersionArray(aString);
        subgroup.add(details);
        session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
        request.setSession(session);

        when(emailUtil.getEmailRecipientsByUsername("tester"))
            .thenReturn(Collections.singletonList(new InternetAddress("a@mail.com")));

        ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE, mav.getViewName());

        request.setParameter("groupCmd", GroupCmd.REMOVE.toString());
        mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE, mav.getViewName());

        request.setParameter("groupCmd", GroupCmd.DELETE.toString());
        mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE, mav.getViewName());
    }

    private void setUser() {
        final CobaltUser user = new CobaltUser("tester", "first", "last", "testing", new HashSet<>());
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testPerformGroupOperationsNoSelection() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_OPERATION);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("formName", ProviewGroupListFilterForm.FORM_NAME);
        request.setParameter("groupCmd", "NONE");

        final List<String> groupMembers = new ArrayList<>();
        groupMembers.add("test");
        request.setParameter("groupMembers", groupMembers.toString());

        final List<GroupDetails> subgroup = new ArrayList<>();
        final GroupDetails details = new GroupDetails();
        details.setId("[test]");
        details.setTitleId("titleTest");
        subgroup.add(details);

        final String[] aString = {"test1", "test2"};
        details.setTitleIdWithVersionArray(aString);

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_PAGINATED_LIST, subgroup);
        request.setSession(session);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_SINGLE_VERSION, mav.getViewName());
    }

    @Test
    public void testProviewTitlePromotePost() throws Exception {
        setUser();
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_PROMOTE);
        request.setMethod(HttpMethod.POST.name());
        setUpGroupIds();

        when(mockProviewHandler.promoteTitle(TITLE_ID, TITLE_VERSION)).thenReturn(true);

        setGroupRequestParameters();
        final HttpSession httpSession = request.getSession();
        prepareGroupAttributes(httpSession, GROUP_ID, GROUP_VERSION);
        when(mockProviewHandler.promoteGroup(GROUP_ID, Version.VERSION_PREFIX + GROUP_VERSION)).thenReturn("");

        request.setParameter("groupOperation", Boolean.valueOf(false).toString());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        checkGroupStatus(httpSession, GROUP_ID, CoreConstants.REVIEW_BOOK_STATUS);
        checkModelAttributes(mav);
    }

    /**
     * Note: runs very slowly due to the "wait 3 seconds" command in
     * doTitleOperation(..)
     *
     * @throws Exception
     */
    @Test
    public void testProviewTitlePromotePostGroupOperation() throws Exception {
        setUser();
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_PROMOTE);
        request.setMethod(HttpMethod.POST.name());
        setUpGroupIds();

        when(mockProviewHandler.promoteTitle(TITLE_ID, TITLE_VERSION)).thenReturn(true);

        setGroupRequestParameters();
        final HttpSession httpSession = request.getSession();
        prepareGroupAttributes(httpSession, GROUP_ID, GROUP_VERSION);
        when(mockProviewHandler.promoteGroup(GROUP_ID, Version.VERSION_PREFIX + GROUP_VERSION)).thenReturn("");

        request.setParameter("groupOperation", Boolean.valueOf(true).toString());
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_PROMOTE, mav.getViewName());
        checkGroupStatus(httpSession, GROUP_ID, CoreConstants.FINAL_BOOK_STATUS);
        checkModelAttributes(mav);
    }

    /**
     * Note: runs very slowly due to the "wait 3 seconds" command in
     * doTitleOperation(..)
     *
     * @throws Exception
     */
    @Test
    public void testProviewTitleRemovePost() throws Exception {
        setUser();
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_REMOVE);
        request.setMethod(HttpMethod.POST.name());
        setUpGroupIds();

        when(mockProviewHandler.removeTitle(TITLE_ID, new Version(TITLE_VERSION))).thenReturn(true);

        final HttpSession httpSession = request.getSession();
        prepareGroupAttributes(httpSession, GROUP_ID, GROUP_VERSION);
        setGroupRequestParameters();
        when(mockProviewHandler.removeTitle(TITLE_ID, new Version(TITLE_VERSION))).thenReturn(Boolean.TRUE);

        request.setParameter("groupOperation", Boolean.valueOf(false).toString());
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE, mav.getViewName());
        checkGroupStatus(httpSession, GROUP_ID, CoreConstants.REVIEW_BOOK_STATUS);
        checkModelAttributes(mav);
        verify(mockProviewHandler).removeTitle(eq(TITLE_ID), any());
        verifyNoMoreInteractions(mockProviewHandler);
    }

    @Test
    public void testProviewTitleRemovePostGroupOperation() throws Exception {
        setUser();
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_REMOVE);
        request.setMethod(HttpMethod.POST.name());
        setUpGroupIds();

        when(mockProviewHandler.removeTitle(TITLE_ID, new Version(TITLE_VERSION))).thenReturn(true);

        final HttpSession httpSession = request.getSession();
        prepareGroupAttributes(httpSession, GROUP_ID, GROUP_VERSION);
        setGroupRequestParameters();
        when(mockProviewHandler.removeGroup(GROUP_ID, Version.VERSION_PREFIX + GROUP_VERSION)).thenReturn("");
        when(mockProviewHandler.deleteGroup(GROUP_ID, Version.VERSION_PREFIX + GROUP_VERSION)).thenReturn("");

        request.setParameter("groupOperation", Boolean.valueOf(true).toString());
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_REMOVE, mav.getViewName());
        checkGroupStatus(httpSession, GROUP_ID, REMOVE_STATUS);
        checkModelAttributes(mav);
        verify(mockProviewHandler).removeTitle(eq(TITLE_ID), any());
        verify(mockProviewHandler).removeGroup(GROUP_ID, Version.VERSION_PREFIX + GROUP_VERSION);
        verify(mockProviewHandler).deleteGroup(GROUP_ID, Version.VERSION_PREFIX + GROUP_VERSION);
        verifyNoMoreInteractions(mockProviewHandler);
    }

    private void checkModelAttributes(final ModelAndView mav) {
        final List<GroupDetails> subgroups = (List<GroupDetails>) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        assertEquals(2, subgroups.size());
        assertEquals(TITLE_ID + "/" + TITLE_VERSION, subgroups.get(0).getTitleIdListWithVersion().get(0));
        assertEquals(TITLE_ID + "/" + TITLE_VERSION_2, subgroups.get(1).getTitleIdListWithVersion().get(0));
    }

    /**
     * Note: runs very slowly due to the "wait 3 seconds" command in
     * doTitleOperation(..)
     *
     * @throws Exception
     */
    @Test
    public void proviewGroupDeletePost_groupOperationIsTrue_titleAndGroupAreSetToDeleteInProview() throws Exception {
        setUser();

        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_BOOK_DELETE);
        request.setMethod(HttpMethod.POST.name());

        setUpGroupIds();
        when(mockProviewHandler.deleteTitle(TITLE_ID, new Version(TITLE_VERSION))).thenReturn(Boolean.TRUE);

        request.setParameter("groupOperation", Boolean.TRUE.toString());
        final HttpSession httpSession = request.getSession();
        prepareGroupAttributes(httpSession, GROUP_ID, GROUP_VERSION);
        setGroupRequestParameters();
        when(mockProviewHandler.deleteGroup(GROUP_ID, "v" + GROUP_VERSION)).thenReturn("");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_GROUP_BOOK_DELETE, mav.getViewName());
        checkGroupStatus(httpSession, GROUP_ID, DELETE_STATUS);
        verify(mockProviewHandler).deleteGroup(any(), any());
        verify(mockProviewHandler).deleteTitle(any(), any());
        verifyNoMoreInteractions(mockProviewHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupAllVersionsGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("groupIds", "1");

        when(mockProviewHandler.getAllProviewGroupInfo()).thenThrow(new IllegalArgumentException());

        handlerAdapter.handle(request, response, controller);
    }

    @SuppressWarnings("SameParameterValue")
    private void prepareGroupAttributes(final HttpSession httpSession, final String groupId, final String groupVersion) {
        final ProviewGroupContainer proviewGroupContainer = new ProviewGroupContainer();
        final ProviewGroup group = getGroup(groupId, groupVersion);
        proviewGroupContainer.setProviewGroups(Collections.singletonList(group));
        proviewGroups.put(groupId, proviewGroupContainer);
        selectedProviewGroups = Collections.singletonList(group);
        httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS, proviewGroups);
        httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroups);

        httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, getGroupDetails());
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private void checkGroupStatus(final HttpSession httpSession, final String groupId, final String groupStatus) {
        final Map<String, ProviewGroupContainer> groupContainerMap = (Map<String, ProviewGroupContainer>)
                httpSession.getAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS);
        assertEquals(groupStatus, groupContainerMap.get(groupId).getProviewGroups().get(0).getGroupStatus());
        final List<ProviewGroup> selectedProviewGroups = (List<ProviewGroup>)
                httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS);
        assertEquals(groupStatus, selectedProviewGroups.get(0).getGroupStatus());
    }

    @SuppressWarnings("SameParameterValue")
    private ProviewGroup getGroup(final String groupId, final String groupVersion) {
        final ProviewGroup group = new ProviewGroup();
        group.setGroupId(groupId);
        group.setGroupVersion(Version.VERSION_PREFIX + groupVersion);
        group.setGroupStatus(CoreConstants.REVIEW_BOOK_STATUS);
        return group;
    }

    private void setUpGroupIds() {
        final List<String> groupIds = new ArrayList<>();
        groupIds.add(TITLE_ID + "/" + TITLE_VERSION);
        request.setParameter("groupIds", groupIds.toString());
    }

    private void setGroupRequestParameters() {
        request.setParameter("proviewGroupID", GROUP_ID);
        request.setParameter("GroupVersion", GROUP_VERSION);

        request.addParameter("groupMembers", TITLE_ID + "/" + TITLE_VERSION);
        request.addParameter("groupMembers", TITLE_ID + "/" + TITLE_VERSION_2);
    }

    @NotNull
    private List<GroupDetails> getGroupDetails() {
        GroupDetails subgroup = getSubgroup(TITLE_VERSION);
        GroupDetails subgroup2 = getSubgroup(TITLE_VERSION_2);
        return Arrays.asList(subgroup, subgroup2);
    }

    @NotNull
    private GroupDetails getSubgroup(final String titleVersion) {
        GroupDetails subgroup = new GroupDetails();
        subgroup.setId(TITLE_ID);
        subgroup.setBookVersion(titleVersion);
        subgroup.setTitleIdList(Collections.singletonList(getProviewTitleInfo(titleVersion)));
        return subgroup;
    }

    @NotNull
    private ProviewTitleInfo getProviewTitleInfo(final String titleVersion) {
        ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setTitleId(TITLE_ID);
        proviewTitleInfo.setVersion(titleVersion);
        proviewTitleInfo.setStatus(REVIEW_STATUS);
        return proviewTitleInfo;
    }
}
