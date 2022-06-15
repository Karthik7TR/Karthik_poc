package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleReportInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
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
    private static final String PROVIEW_DISPLAY_NAME = "proviewDisplayName";
    private static final String VERSION = "version";
    private static final String STATUS = "status";
    private static final String COMMAND = "command";
    private static final String ERROR_MESSAGE = "Message";
    private static final String ERR_MESSAGE_KEY = "errMessage";
    private static final String OBJECTS_PER_PAGE = "objectsPerPage";
    private static final String TEST_TITLE_NAME = "testTitle";
    private static final String TEST_TITLE_ID = "testId".toLowerCase();
    private static final Integer TOTAL_NUMBER_OF_VERSIONS = 1;
    private static final String PERCENT = "%";
    private static final String MIN_VERSIONS_FILTER = "minVersions";
    private static final String MAX_VERSIONS_FILTER = "maxVersions";
    private static final String STATUS_REVIEW = "Review";
    private static final String STATUS_KEY = "status";

    @InjectMocks
    private ProviewTitleListController controller;
    @Mock
    private VersionIsbnService mockVersionIsbnService;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    @Mock
    private ManagerService mockManagerService;
    @Mock
    private BookDefinitionService mockBookDefinitionService;
    @Mock
    private ProviewTitleListService mockProviewTitleListService;
    @Mock
    private MessageSourceAccessor mockMessageSourceAccessor;
    @Mock
    private JobRequestService mockJobRequestService;
    @SuppressWarnings("unused")
    @Mock
    private GroupService mockGroupService;
    @SuppressWarnings("unused")
    @Mock
    private OutageService mockOutageService;

    private BookDefinition bookDefinition;
    private String titleId;
    private String versionString;
    private String status;
    private String userName;
    private Authentication auth;
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
    public void getSelections_proviewIssueIsGiven_proviewExceptionIsCaught() throws Exception {
        initProviewTitlesGetRequest();
        final String objectsPerPage = "42";
        request.setParameter(OBJECTS_PER_PAGE, objectsPerPage);
        when(mockProviewTitleListService.getSelectedProviewTitleInfo(any(ProviewListFilterForm.class)))
                .thenThrow(new ProviewException(""));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals(Boolean.TRUE, model.get(WebConstants.KEY_ERROR_OCCURRED));
        assertEquals(objectsPerPage,""+ model.get(WebConstants.KEY_PAGE_SIZE));
        verify(mockProviewTitleListService).getSelectedProviewTitleInfo(any());
        verifyNoMoreInteractions(mockProviewTitleListService);
    }

    private void initProviewTitlesGetRequest() {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.GET.name());
    }

    @Test
    public void getSelections_proviewTitlesAreGiven_proviewTitlesReturnedWithDefaultPageSize() throws Exception {
        initProviewTitlesGetRequest();
        final List<ProviewTitleInfo> selectedProviewTitleInfo = Collections.emptyList();
        when(mockProviewTitleListService.getSelectedProviewTitleInfo(any(ProviewListFilterForm.class)))
                .thenReturn(selectedProviewTitleInfo);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        assertEquals(WebConstants.DEFAULT_PAGE_SIZE,""+ model.get(WebConstants.KEY_PAGE_SIZE));
        assertNull(model.get(WebConstants.KEY_ERROR_OCCURRED));
        assertNotNull(model.get(ProviewListFilterForm.FORM_NAME));
        verify(mockProviewTitleListService).getSelectedProviewTitleInfo(any());
        verifyNoMoreInteractions(mockProviewTitleListService);
    }

    @Test
    public void getSelections_pageSizeParameterIsGiven_proviewTitlesReturnedWithGivenPageSize() throws Exception {
        initProviewTitlesGetRequest();
        final String pageSize = "42";
        request.setParameter(OBJECTS_PER_PAGE, pageSize);
        final List<ProviewTitleInfo> selectedProviewTitleInfo = Collections.emptyList();
        when(mockProviewTitleListService.getSelectedProviewTitleInfo(any(ProviewListFilterForm.class)))
                .thenReturn(selectedProviewTitleInfo);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();
        assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        assertNotNull(model.get(WebConstants.KEY_TOTAL_BOOK_SIZE));
        assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
        assertEquals(pageSize,""+ model.get(WebConstants.KEY_PAGE_SIZE));
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
    }

    @Test
    public void getSelections_filteringParamsStartingWithWildcardAreGiven_filteringParamsArePassedToServiceLayer()
            throws Exception {
        initProviewTitlesGetRequest();
        final String proviewDisplayName = TEST_TITLE_NAME + PERCENT;
        request.setParameter(PROVIEW_DISPLAY_NAME, proviewDisplayName);
        final String titleId = TEST_TITLE_ID + PERCENT;
        request.setParameter(TITLE_ID, titleId);
        request.setParameter(MIN_VERSIONS_FILTER, TOTAL_NUMBER_OF_VERSIONS.toString());
        request.setParameter(MAX_VERSIONS_FILTER, Integer.toString(3));
        request.setParameter(STATUS_KEY, STATUS_REVIEW);
        final ArgumentCaptor<ProviewListFilterForm> formCaptor = ArgumentCaptor.forClass(ProviewListFilterForm.class);
        when(mockProviewTitleListService.getSelectedProviewTitleInfo(formCaptor.capture()))
                .thenReturn(Collections.singletonList(getProviewTitleInfo()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        validateModel(mav);
        final ProviewListFilterForm form = formCaptor.getValue();
        assertEquals(proviewDisplayName, form.getProviewDisplayName());
        assertEquals(titleId, form.getTitleId());
        assertEquals(TOTAL_NUMBER_OF_VERSIONS, form.getMinVersionsInt());
        assertEquals(Integer.valueOf(3), form.getMaxVersionsInt());
        assertEquals(STATUS_REVIEW, form.getStatus());
    }


    private ProviewTitleInfo getProviewTitleInfo() {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setTitle(TEST_TITLE_NAME);
        titleInfo.setTitleId(TEST_TITLE_ID);
        titleInfo.setTotalNumberOfVersions(TOTAL_NUMBER_OF_VERSIONS);
        return titleInfo;
    }


    private void validateModel(final ModelAndView mav) {
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLES, mav.getViewName());
        ProviewTitlePaginatedList  paginatedList = (ProviewTitlePaginatedList) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        assertEquals(TEST_TITLE_ID, paginatedList.getList().get(0).getTitleId());
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
    public void proviewTitleMarkSuperseded_titleIdIsGiven_serviceReceivedTitleIdAndCorrectViewNameIsReturned() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_MARK_SUPERSEDED);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_TITLE_ID, titleId);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_MARK_SUPERSEDED_SUCCESS, mav.getViewName());
        verify(mockProviewTitleListService).markTitleSuperseded(titleId);
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
        prepareTitleActionPostMocks();
        prepareArgumentCaptors();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE, modelAndView.getViewName());
        validateActionArguments(TitleActionName.PROMOTE);
    }

    @SneakyThrows
    @Test
    public void testProviewTitlePromotePostWhenJobIsRunning() {
        setUpProviewTitleActionMocks(true);
        setPromoteRequestParameters();
        prepareTitleActionPostMocks();
        prepareArgumentCaptors();

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
        prepareTitleActionPostMocks();
        prepareArgumentCaptors();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_REMOVE, modelAndView.getViewName());
        validateActionArguments(TitleActionName.REMOVE);
    }

    @SneakyThrows
    @Test
    public void testProviewTitleDeletePost() {
        setUpProviewTitleActionMocks(false);
        setDeleteRequestParameters();
        prepareTitleActionPostMocks();
        prepareArgumentCaptors();
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
        bookDefinition = new BookDefinition();
        final long ebookDefinitionId = 1L;
        bookDefinition.setEbookDefinitionId(ebookDefinitionId);
        when(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).thenReturn(bookDefinition);
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
        titleActionResult = new TitleActionResult();
        titleActionResult.setErrorMessage("error");
        titleActionResult.setUpdatedTitles(Collections.emptyList());
        titleActionResult.setTitlesToUpdate(Collections.singletonList(""));
        assertEquals(OperationResult.UNSUCCESSFUL, titleActionResult.getOperationResult());
        prepareArgumentCaptors();

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
        titleActionResult = new TitleActionResult();
        titleActionResult.setErrorMessage(null);
        titleActionResult.setUpdatedTitles(Collections.singletonList(titleId));
        titleActionResult.setTitlesToUpdate(Collections.emptyList());
        assertEquals(OperationResult.SUCCESSFUL, titleActionResult.getOperationResult());
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
        bookDefinition = new BookDefinition();
        when(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).thenReturn(bookDefinition);
        mockRunningJobCheck(isJobRunning);
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, new ArrayList<>());
    }

    private void mockRunningJobCheck(boolean isJobRunning) {
        bookDefinition.setEbookDefinitionId(1L);
        when(mockJobRequestService.isBookInJobRequest(1L)).thenReturn(false);
        if (isJobRunning) {
            when(mockManagerService.findRunningJob(bookDefinition)).thenReturn(new JobExecution(1L));
            bookDefinition.setFullyQualifiedTitleId(TITLE_ID);
            when(mockMessageSourceAccessor.getMessage(anyString(), any(Object[].class))).thenReturn(ERROR_MESSAGE);
        } else {
            when(mockManagerService.findRunningJob(bookDefinition)).thenReturn(null);
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


    private ProviewTitlePaginatedList createPaginatedList(List<ProviewTitleInfo> selectedProviewTitleInfo, Integer allLatestProviewTitleInfoSize,
                                                          Integer pageNo, Integer objectsPerPage, String sortColumn, boolean isAscendingSort) {
        ProviewTitlePaginatedList proviewTitlePaginatedList = new ProviewTitlePaginatedList(
                selectedProviewTitleInfo,
                allLatestProviewTitleInfoSize,
                pageNo,
                objectsPerPage,
                null,
                isAscendingSort);

        return proviewTitlePaginatedList;
    }

}
