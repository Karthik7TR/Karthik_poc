package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureBoolean;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerServiceImpl;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.easymock.Capture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

public final class ProviewTitleListControllerTest {
    private static final String _PT = "_pt";
    private static final String TITLE_ID = "titleId";
    private static final String VERSION = "version";
    private static final String STATUS = "status";
    private static final String COMMAND = "command";
    private static final String ERROR_MESSAGE = "Message";
    private static final String ERR_MESSAGE_KEY = "errMessage";

    private ProviewTitleListController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    private ProviewHandler mockProviewHandler;
    private ManagerService mockManagerService;
    private BookDefinitionService mockBookDefinitionService;
    private ProviewAuditService mockProviewAuditService;
    private ProviewTitleListService mockTitleListService;
    private MessageSourceAccessor mockMessageSourceAccessor;
    private JobRequestService mockJobRequestService;
    private VersionIsbnService mockVersionIsbnService;

    private BookDefinition bookDefinition;
    private String titleId;
    private String versionString;
    private String status;
    private String userName;
    private List<String> splitBookTitleIds;
    private Authentication auth;
    private ProviewTitleForm form;
    private TitleAction titleAction;
    private TitleActionResult titleActionResult;
    private Capture<ProviewTitleForm> formArgument;
    private Capture<TitleAction> titleActionArgument;

    @Before
    public void SetUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        handlerAdapter = new AnnotationMethodHandlerAdapter();
        mockProviewHandler = createMock(ProviewHandler.class);
        mockManagerService = createMock(ManagerServiceImpl.class);
        mockBookDefinitionService = createMock(BookDefinitionService.class);
        mockProviewAuditService = createMock(ProviewAuditService.class);
        mockTitleListService = createMock(ProviewTitleListService.class);
        GroupService mockGroupService = createMock(GroupService.class);
        mockMessageSourceAccessor = createMock(MessageSourceAccessor.class);
        mockJobRequestService = createMock(JobRequestService.class);
        mockVersionIsbnService = createMock(VersionIsbnService.class);
        OutageService mockOutageService = createMock(OutageService.class);
        controller = new ProviewTitleListController(
            mockProviewHandler,
            mockBookDefinitionService,
            mockProviewAuditService,
            mockManagerService,
            mockMessageSourceAccessor,
            mockJobRequestService,
            mockGroupService,
            mockTitleListService,
            mockVersionIsbnService,
            mockOutageService);

        bookDefinition = createMock(BookDefinition.class);
        form = createMock(ProviewTitleForm.class);
        titleAction = createMock(TitleAction.class);
        titleActionResult = createMock(TitleActionResult.class);

        titleId = "anId";
        versionString = "v2.0";
        status = "test";
        userName = "tester";
        splitBookTitleIds = Arrays.asList(titleId, titleId + _PT + 2, titleId + _PT + 3);

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

        expect(mockProviewHandler.getTitlesWithUnitedParts()).andReturn(testAllTitleInfo);
        expect(mockProviewHandler.getAllLatestProviewTitleInfo(testAllTitleInfo))
            .andReturn(testAllLatestTitleInfo);
        replay(mockProviewHandler);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
        final Map<String, Object> model = mav.getModel();
        assertEquals(model.get(WebConstants.KEY_PAGE_SIZE), "20");

        verify(mockProviewHandler);
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

        expect(mockProviewHandler.getTitlesWithUnitedParts()).andReturn(mockAllProviewTitleInfo);
        expect(mockProviewHandler.getAllLatestProviewTitleInfo(mockAllProviewTitleInfo))
            .andReturn(mockAllLatestProviewTitleInfo);
        replay(mockProviewHandler);
        expect(mockProviewAuditService.findMaxRequestDateByTitleIds(Collections.singleton("test")))
            .andReturn(Collections.singletonMap("test", new Date()));
        replay(mockProviewAuditService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
        assertEquals(((List<ProviewTitleInfo>) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST)).get(0).getLastStatusUpdateDate(),
            DateFormatUtils.format(new Date(), "yyyyMMdd"));
        final Map<String, Object> model = mav.getModel();

        assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
        assertEquals("20", model.get("pageSize"));

        verify(mockProviewHandler);
    }

    @Test(expected = ProviewException.class)
    public void testPostSelectionsRefreshProviewExcepton() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.REFRESH.toString());

        expect(mockProviewHandler.getTitlesWithUnitedParts()).andThrow(new ProviewException(""));
        replay(mockProviewHandler);

        handlerAdapter.handle(request, response, controller);
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
        assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
        final Map<String, Object> model = mav.getModel();

        assertEquals(1, model.get("resultSize"));
    }

    @Test
    public void testDownloadProviewListExcel() throws Exception {
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

    @SneakyThrows
    @Test
    public void testProviewTitlePromotePost() {
        setUpProviewTitleActionMocks(false);
        setPromoteRequestParameters();
        prepareArgumentCaptors();
        prepareTitleActionPostMocks();
        replayProviewTitleActionsMocks();

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
        replayProviewTitleActionsMocks();

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
        replayProviewTitleActionsMocks();

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
        replayProviewTitleActionsMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
        validateActionArguments(TitleActionName.DELETE);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareArgumentCaptors() {
        formArgument = new Capture();
        titleActionArgument = new Capture();
        Capture<Boolean> isJobRunningArgument = new Capture();
        expect(mockTitleListService.executeTitleAction(capture(formArgument),
            capture(titleActionArgument), captureBoolean(isJobRunningArgument)))
            .andReturn(titleActionResult);
    }

    private void prepareTitleActionPostMocks() {
        expect(titleActionResult.hasErrorMessage()).andReturn(false);
        expect(titleActionResult.getUpdatedTitles()).andReturn(Collections.singletonList(titleId));
        expect(titleActionResult.getOperationResult()).andReturn(OperationResult.SUCCESSFUL);
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
        expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId))
            .andReturn(bookDefinition);
        mockRunningJobCheck(isJobRunning);
        mockChangeStatusForTitle(isJobRunning);
    }

    private void mockRunningJobCheck(boolean isJobRunning) {
        expect(bookDefinition.getEbookDefinitionId()).andReturn(1L);
        expect(mockJobRequestService.isBookInJobRequest(1L)).andReturn(false);
        if (isJobRunning) {
            expect(mockManagerService.findRunningJob(bookDefinition)).andReturn(new JobExecution(1L));
            expect(bookDefinition.getFullyQualifiedTitleId()).andReturn(TITLE_ID);
            expect(mockMessageSourceAccessor.getMessage(anyObject(String.class), anyObject(Object[].class))).andReturn(ERROR_MESSAGE);
        } else {
            expect(mockManagerService.findRunningJob(bookDefinition)).andReturn(null);
        }
    }

    @SneakyThrows
    private void mockChangeStatusForTitle(boolean isJobRunning) {
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, new HashMap<String, ProviewTitleContainer>());
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, new ArrayList<>());
        if (!isJobRunning) {
            expect(mockProviewHandler.getAllLatestProviewTitleInfo(anyObject()))
                    .andReturn(new ArrayList<>()).times(splitBookTitleIds.size());
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

    private void replayProviewTitleActionsMocks() {
        replay(mockBookDefinitionService, mockTitleListService, mockProviewHandler,
            bookDefinition, mockJobRequestService, mockMessageSourceAccessor, mockManagerService,
            mockVersionIsbnService, form, titleAction, titleActionResult);
    }
}
