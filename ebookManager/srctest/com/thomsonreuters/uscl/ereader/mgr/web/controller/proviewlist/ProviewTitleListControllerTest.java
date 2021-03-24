package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListControllerTest {
    private static final String TITLE_ID = "titleId";
    private static final String VERSION = "version";
    private static final String STATUS = "status";
    private static final String COMMAND = "command";
    private static final String ERROR_MESSAGE = "Message";
    private static final String ERR_MESSAGE_KEY = "errMessage";

    @InjectMocks
    private ProviewTitleListController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    @Mock
    private ProviewHandler mockProviewHandler;
    @Mock
    private ManagerService mockManagerService;
    @Mock
    private BookDefinitionService mockBookDefinitionService;
    @Mock
    private ProviewAuditService mockProviewAuditService;
    @Mock
    private ProviewTitleListService mockProviewTitleListService;
    @Mock
    private MessageSourceAccessor mockMessageSourceAccessor;
    @Mock
    private JobRequestService mockJobRequestService;
    @Mock
    private VersionIsbnService mockVersionIsbnService;
    @SuppressWarnings("unused")
    @Mock
    private GroupService mockGroupService;
    @SuppressWarnings("unused")
    @Mock
    private OutageService mockOutageService;
    @Mock
    private BookDefinition bookDefinition;
    private String titleId;
    private String versionString;
    private String status;
    private String userName;
    private Authentication auth;
    @Mock
    private ProviewTitleContainer proviewTitleContainer;
    @Mock
    private TitleActionResult titleActionResult;
    private ArgumentCaptor<ProviewTitleForm> formArgument;
    private ArgumentCaptor<TitleAction> titleActionArgument;

    @Before
    public void SetUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        titleId = "anId";
        versionString = "v2.0";
        status = "test";
        userName = "tester";

        setUpAuthentication();
    }

    @After
    public void reset() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testSelectedLatestProviewTitleInfo() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();
        session.setAttribute(ProviewListFilterForm.FORM_NAME, controller.fetchSavedProviewListFilterForm(session));
        final ProviewTitleForm mockTitleForm = new ProviewTitleForm();
        mockTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
        session.setAttribute(ProviewTitleForm.FORM_NAME, mockTitleForm);
        session.setAttribute(WebConstants.KEY_PAGE_SIZE, mockTitleForm.getObjectsPerPage());
        final Map<String, ProviewTitleContainer> testAllTitleInfo = new HashMap<>();
        final List<ProviewTitleInfo> testAllLatestTitleInfo = new ArrayList<>();
        when(mockProviewHandler.getTitlesWithUnitedParts()).thenReturn(testAllTitleInfo);
        when(mockProviewHandler.getAllLatestProviewTitleInfo(testAllTitleInfo)).thenReturn(testAllLatestTitleInfo);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals(WebConstants.DEFAULT_PAGE_SIZE, model.get(WebConstants.KEY_PAGE_SIZE));
        assertNull(model.get(WebConstants.KEY_ERROR_OCCURRED));
        assertNotNull(model.get(ProviewListFilterForm.FORM_NAME));
        verify(mockProviewHandler).getTitlesWithUnitedParts();
        verify(mockProviewHandler).getAllLatestProviewTitleInfo(testAllTitleInfo);
        verifyNoMoreInteractions(mockProviewHandler);
    }

    @Test
    public void allLatestProviewTitleInfo_proviewIssueIsGiven_proviewExceptionIsCaught() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();

        when(mockProviewHandler.getTitlesWithUnitedParts()).thenThrow(new ProviewException(""));

        final ProviewTitleForm proviewTitleForm = new ProviewTitleForm();
        final String objectsPerPage = "42";
        proviewTitleForm.setObjectsPerPage(objectsPerPage);
        session.setAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals(Boolean.TRUE, model.get(WebConstants.KEY_ERROR_OCCURRED));
        assertNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNull(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
        assertEquals(proviewTitleForm, model.get(ProviewTitleForm.FORM_NAME));
        assertEquals(objectsPerPage, model.get(WebConstants.KEY_PAGE_SIZE));
        verify(mockProviewHandler).getTitlesWithUnitedParts();
        verifyNoMoreInteractions(mockProviewHandler);
    }

    @Test
    public void allLatestProviewTitleInfo_selectedProviewTitleInfoIsGiven_modelAttributesAreSet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.GET.name());
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, Collections.emptyList());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertNull(model.get(WebConstants.KEY_ERROR_OCCURRED));
        assertEquals(Collections.emptyList(), model.get(WebConstants.KEY_PAGINATED_LIST));
        assertEquals(0, model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
        assertNotNull(model.get(ProviewListFilterForm.FORM_NAME));
        assertEquals(WebConstants.DEFAULT_PAGE_SIZE, model.get(WebConstants.KEY_PAGE_SIZE));
        verifyNoMoreInteractions(mockProviewHandler);
    }

    @Test
    public void testPostSelectionsRefresh() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.REFRESH.toString());
        final ProviewTitleForm mockTitleForm = new ProviewTitleForm();
        mockTitleForm.setCommand(Command.REFRESH);

        final Map<String, ProviewTitleContainer> mockAllProviewTitleInfo = new HashMap<>();
        mockAllProviewTitleInfo.put("test", new ProviewTitleContainer());
        final List<ProviewTitleInfo> mockAllLatestProviewTitleInfo = new ArrayList<>();
        final ProviewTitleInfo testInfo = new ProviewTitleInfo();
        testInfo.setTitle("test");
        testInfo.setTitleId("test");
        mockAllLatestProviewTitleInfo.add(testInfo);

        when(mockProviewHandler.getTitlesWithUnitedParts()).thenReturn(mockAllProviewTitleInfo);
        when(mockProviewHandler.getAllLatestProviewTitleInfo(mockAllProviewTitleInfo))
            .thenReturn(mockAllLatestProviewTitleInfo);
        when(mockProviewAuditService.findMaxRequestDateByTitleIds(Collections.singleton("test")))
            .thenReturn(Collections.singletonMap("test", new Date()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            ((List<ProviewTitleInfo>) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST)).get(0).getLastStatusUpdateDate());
        final Map<String, Object> model = mav.getModel();

        assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
        assertEquals("20", model.get("pageSize"));

        verify(mockProviewHandler).getTitlesWithUnitedParts();
        verify(mockProviewHandler).getAllLatestProviewTitleInfo(mockAllProviewTitleInfo);
        verifyNoMoreInteractions(mockProviewHandler);
    }

    @Test(expected = ProviewException.class)
    public void testPostSelectionsRefreshProviewExcepton() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.REFRESH.toString());

        when(mockProviewHandler.getTitlesWithUnitedParts()).thenThrow(new ProviewException(""));

        handlerAdapter.handle(request, response, controller);
    }

    @Test(expected = ProviewException.class)
    public void testPostSelectionsUnhandledCommandProviewExcepton() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, Command.DELETE.toString());

        handlerAdapter.handle(request, response, controller);
    }

    @Test
    public void testPostSelectionsAllProviewTitleInfoIsNull() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.REFRESH.toString());
        final Map<String, ProviewTitleContainer> mockAllProviewTitleInfo = new HashMap<>();
        mockAllProviewTitleInfo.put("test", proviewTitleContainer);
        when(mockProviewHandler.getTitlesWithUnitedParts()).thenReturn(mockAllProviewTitleInfo);
        when(mockProviewHandler.getAllLatestProviewTitleInfo(mockAllProviewTitleInfo)).thenReturn(null);
        when(mockProviewAuditService.findMaxRequestDateByTitleIds(Collections.singleton("test")))
            .thenReturn(Collections.singletonMap("test", new Date()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        assertNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNull(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
        assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
        assertEquals(WebConstants.DEFAULT_PAGE_SIZE, model.get(WebConstants.KEY_PAGE_SIZE));
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
    }

    @Test
    public void testPostSelectionsProviewTitleFormIsNotNull() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.REFRESH.toString());
        final Map<String, ProviewTitleContainer> mockAllProviewTitleInfo = new HashMap<>();
        mockAllProviewTitleInfo.put("test", proviewTitleContainer);
        when(mockProviewHandler.getTitlesWithUnitedParts()).thenReturn(mockAllProviewTitleInfo);
        when(mockProviewHandler.getAllLatestProviewTitleInfo(mockAllProviewTitleInfo)).thenReturn(new ArrayList<>());
        when(mockProviewAuditService.findMaxRequestDateByTitleIds(Collections.singleton("test")))
            .thenReturn(Collections.singletonMap("test", new Date()));

        final ProviewTitleForm proviewTitleForm = new ProviewTitleForm();
        proviewTitleForm.setObjectsPerPage("42");
        final HttpSession testSession = request.getSession();
        testSession.setAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNotNull(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
        assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
        assertEquals("42", model.get(WebConstants.KEY_PAGE_SIZE));
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
    }

    @Test
    public void testPostSelectionsPageSize() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.PAGESIZE.toString());
        final HttpSession testSession = request.getSession();
        final List<ProviewTitleInfo> testTitleInfo = new ArrayList<>();
        final ProviewTitleInfo testInfo = new ProviewTitleInfo();
        testTitleInfo.add(testInfo);
        testSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, testTitleInfo);
        testSession.setAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, "5");
        testSession.setAttribute(WebConstants.KEY_PAGE_SIZE, WebConstants.DEFAULT_PAGE_SIZE);
        request.setSession(testSession);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        final Map<String, Object> model = mav.getModel();

        assertEquals(1, model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
    }

    @Test
    public void testDownloadPublishingStatsExcel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD);
        request.setMethod(HttpMethod.GET.name());

        final List<ProviewTitleInfo> titles = new ArrayList<>();

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, titles);
        request.setSession(session);
        handlerAdapter.handle(request, response, controller);

        final ServletOutputStream outStream = response.getOutputStream();
        assertFalse(outStream.toString().isEmpty());
    }

    @Test
    public void proviewTitleDeleteTestModel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(TITLE_ID, WebConstants.KEY_TITLE_ID);
        request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
        request.setParameter(STATUS, WebConstants.KEY_STATUS);
        request.setParameter("lastUpdate", "test");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();

        assertEquals(WebConstants.KEY_TITLE_ID, model.get(TITLE_ID));
        assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
        assertEquals(WebConstants.KEY_STATUS, model.get(STATUS));
    }

    @Test
    public void proviewTitleRemoveTestModel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_REMOVE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(TITLE_ID, WebConstants.KEY_TITLE_ID);
        request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
        request.setParameter(STATUS, WebConstants.KEY_STATUS);
        request.setParameter("lastUpdate", "test");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();

        assertEquals(WebConstants.KEY_TITLE_ID, model.get(TITLE_ID));
        assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
        assertEquals(WebConstants.KEY_STATUS, model.get(STATUS));
    }

    @Test
    public void proviewTitleMarkSuperseded_titleIdIsGiven_titleMarkedAsSuperseded() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_MARK_SUPERSEDED);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_MARK_SUPERSEDED_SUCCESS, mav.getViewName());
        verify(mockProviewHandler).markTitleSuperseded(titleId);
    }

    @Test
    public void proviewTitlePromoteTestModel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_PROMOTE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(TITLE_ID, WebConstants.KEY_TITLE_ID);
        request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
        request.setParameter(STATUS, WebConstants.KEY_STATUS);
        request.setParameter("lastUpdate", "test");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();

        assertEquals(WebConstants.KEY_TITLE_ID, model.get(TITLE_ID));
        assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
        assertEquals(WebConstants.KEY_STATUS, model.get(STATUS));
    }

    @Test
    public void proviewTitlePromote_groupInfoIsNotNull_modelAttributesAreSet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_PROMOTE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);
        request.setParameter(WebConstants.KEY_VERSION_NUMBER, "versionNumber");
        request.setParameter(WebConstants.KEY_STATUS, STATUS);
        request.setParameter("lastUpdate", "test");

        final String groupName = "groupName";
        final GroupDefinition groupDefinition = new GroupDefinition();
        groupDefinition.setStatus(status);
        groupDefinition.setName(groupName);
        when(mockGroupService.getGroupOfTitle(titleId)).thenReturn(groupDefinition);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        assertEquals(Boolean.FALSE, model.get(WebConstants.KEY_IS_GROUP_FINAL));
        assertEquals(groupName, model.get(WebConstants.KEY_GROUP_NAME));
    }

    @SneakyThrows
    @Test
    public void testProviewTitlePromotePost() {
        setUpProviewTitleActionMocks(false);
        setPromoteRequestParameters();
        prepareArgumentCaptors();
        prepareTitleActionPostMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE, modelAndView.getViewName());
        validateActionArguments(TitleActionName.PROMOTE);
    }

    @SneakyThrows
    @Test
    public void testProviewTitlePromotePostWhenJobIsRunning() {
        setUpProviewTitleActionMocks(true);
        setPromoteRequestParameters();
        prepareArgumentCaptors();
        prepareTitleActionPostMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE, modelAndView.getViewName());
        assertEquals(ERROR_MESSAGE, modelAndView.getModel().get(ERR_MESSAGE_KEY));
        validateActionArguments(TitleActionName.PROMOTE);
    }

    @SneakyThrows
    @Test
    public void testProviewTitleRemovePost() {
        setUpProviewTitleActionMocks(false);
        setRemoveRequestParameters();
        prepareArgumentCaptors();
        prepareTitleActionPostMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_REMOVE, modelAndView.getViewName());
        validateActionArguments(TitleActionName.REMOVE);
    }

    @SneakyThrows
    @Test
    public void testProviewTitleDeletePost() {
        setUpProviewTitleActionMocks(false);
        setDeleteRequestParameters();
        prepareArgumentCaptors();
        prepareTitleActionPostMocks();
        mockVersionIsbnService.deleteIsbn(titleId, versionString);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
        validateActionArguments(TitleActionName.DELETE);
    }

    @SneakyThrows
    @Test
    public void proviewTitleDeletePost_bookIsInJobRequest_errMessageIsSet() {
        setDeleteRequestParameters();

        final String version = "4.2";
        request.setParameter(VERSION, version);

        when(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).thenReturn(bookDefinition);
        final long ebookDefinitionId = 1L;
        when(bookDefinition.getEbookDefinitionId()).thenReturn(ebookDefinitionId);
        when(mockJobRequestService.isBookInJobRequest(ebookDefinitionId)).thenReturn(Boolean.TRUE);
        when(mockMessageSourceAccessor.getMessage(eq("mesg.job.enqueued.fail"), any(Object[].class))).thenReturn(ERROR_MESSAGE);

        ArgumentCaptor<Boolean> isJobRunningArgument = ArgumentCaptor.forClass(Boolean.class);
        when(mockProviewTitleListService.executeTitleAction(any(), any(), isJobRunningArgument.capture())).thenReturn(null);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
        assertEquals(Boolean.TRUE, isJobRunningArgument.getValue());
        final Map<String, Object> model = modelAndView.getModel();
        assertEquals(version, ((ProviewTitleForm)model.get(ProviewTitleForm.FORM_NAME)).getVersion());
        assertEquals(version, model.get(WebConstants.KEY_VERSION_NUMBER));
        assertNull(model.get(WebConstants.KEY_INFO_MESSAGE));
        assertEquals(ERROR_MESSAGE, model.get(WebConstants.KEY_ERR_MESSAGE));
    }

    @SneakyThrows
    @Test
    public void proviewTitleDeletePost_jobIsNotRunningAndTitleActionExecutionErrorOccurs_errMessageIsSet() {
        setDeleteRequestParameters();
        setUpProviewTitleActionMocks(Boolean.FALSE);
        prepareArgumentCaptors();
        when(titleActionResult.hasErrorMessage()).thenReturn(Boolean.TRUE);
        when(titleActionResult.getUpdatedTitles()).thenReturn(Collections.emptyList());
        when(titleActionResult.getOperationResult()).thenReturn(OperationResult.UNSUCCESSFUL);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
        final Map<String, Object> model = modelAndView.getModel();
        assertNull(model.get(WebConstants.KEY_INFO_MESSAGE));
        assertNotNull(model.get(WebConstants.KEY_ERR_MESSAGE));
    }

    private void prepareArgumentCaptors() {
        formArgument = ArgumentCaptor.forClass(ProviewTitleForm.class);
        titleActionArgument = ArgumentCaptor.forClass(TitleAction.class);
        ArgumentCaptor<Boolean> isJobRunningArgument = ArgumentCaptor.forClass(Boolean.class);
        when(mockProviewTitleListService.executeTitleAction(formArgument.capture(),
            titleActionArgument.capture(), isJobRunningArgument.capture()))
            .thenReturn(titleActionResult);
    }

    private void prepareTitleActionPostMocks() {
        when(titleActionResult.hasErrorMessage()).thenReturn(false);
        when(titleActionResult.getUpdatedTitles()).thenReturn(Collections.singletonList(titleId));
        when(titleActionResult.getOperationResult()).thenReturn(OperationResult.SUCCESSFUL);
    }

    private void validateActionArguments(TitleActionName actionName) {
        ProviewTitleForm titleFormValue = formArgument.getValue();
        assertEquals(titleId, titleFormValue.getTitleId());
        assertEquals(versionString, titleFormValue.getVersion());
        assertEquals(status, titleFormValue.getStatus());
        TitleAction titleActionValue = titleActionArgument.getValue();
        assertEquals(actionName, titleActionValue.getActionName());
    }

    private void setUpAuthentication() {
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        final CobaltUser user = new CobaltUser(userName, "first", "last", "testing", authorities);
        auth = new UsernamePasswordAuthenticationToken(user, null);
    }

    private void setUpProviewTitleActionMocks(boolean isJobRunning) {
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).thenReturn(bookDefinition);
        mockRunningJobCheck(isJobRunning);
        mockChangeStatusForTitle(isJobRunning);
    }

    private void mockRunningJobCheck(boolean isJobRunning) {
        when(bookDefinition.getEbookDefinitionId()).thenReturn(1L);
        when(mockJobRequestService.isBookInJobRequest(1L)).thenReturn(false);
        if (isJobRunning) {
            when(mockManagerService.findRunningJob(bookDefinition)).thenReturn(new JobExecution(1L));
            when(bookDefinition.getFullyQualifiedTitleId()).thenReturn(TITLE_ID);
            when(mockMessageSourceAccessor.getMessage(anyString(), any(Object[].class))).thenReturn(ERROR_MESSAGE);
        } else {
            when(mockManagerService.findRunningJob(bookDefinition)).thenReturn(null);
        }
    }

    @SneakyThrows
    private void mockChangeStatusForTitle(boolean isJobRunning) {
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, new HashMap<String, ProviewTitleContainer>());
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, new ArrayList<>());
        if (!isJobRunning) {
            when(mockProviewHandler.getAllLatestProviewTitleInfo(anyObject())).thenReturn(new ArrayList<>());
        }
    }

    private void setPromoteRequestParameters() {
        setTitleActionRequestParameters(WebConstants.MVC_PROVIEW_TITLE_PROMOTE, Command.PROMOTE);
    }

    private void setRemoveRequestParameters() {
        setTitleActionRequestParameters(WebConstants.MVC_PROVIEW_TITLE_REMOVE, Command.REMOVE);
    }

    private void setDeleteRequestParameters() {
        setTitleActionRequestParameters(WebConstants.MVC_PROVIEW_TITLE_DELETE, Command.DELETE);
    }

    private void setTitleActionRequestParameters(final String path, final Command command) {
        request.setRequestURI("/" + path);
        request.setMethod(HttpMethod.POST.name());
        setRequestParameters(request);
        request.setParameter(COMMAND, command.toString());
    }

    private void setRequestParameters(final MockHttpServletRequest request) {
        request.setParameter(TITLE_ID, titleId);
        request.setParameter(VERSION, versionString);
        request.setParameter(STATUS, status);
    }
}
