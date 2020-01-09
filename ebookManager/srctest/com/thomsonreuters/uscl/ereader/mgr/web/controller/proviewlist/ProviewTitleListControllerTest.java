package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CLEANUP_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.FINAL_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REMOVED_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REVIEW_BOOK_STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
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
import org.easymock.EasyMock;
import org.junit.After;
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
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class ProviewTitleListControllerTest {
    private static final String EMAIL = "a@mail.com";
    private static final String _PT = "_pt";
    private static final String SUCCESS = "Success";
    private static final String TITLE_ID = "titleId";
    private static final String VERSION = "version";
    private static final String STATUS = "status";
    private static final String COMMAND = "command";

    private ProviewTitleListController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;
    private ProviewHandler mockProviewHandler;
    private ManagerService mockManagerService;
    private BookDefinitionService mockBookDefinitionService;
    private ProviewAuditService mockProviewAuditService;
    private GroupService mockGroupService;
    private ProviewTitleListService mockTitleListService;
    private MessageSourceAccessor mockMessageSourceAccessor;
    private JobRequestService mockJobRequestService;
    private VersionIsbnService mockVersionIsbnService;
    private EmailUtil emailUtil;
    private EmailService mockEmailService;
    private OutageService mockOutageService;

    private BookDefinition bookDefinition;
    private String titleId;
    private Version version;
    private String versionString;
    private String status;
    private String userName;
    private List<String> splitBookTitleIds;
    private Authentication auth;

    @Before
    public void SetUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        handlerAdapter = new AnnotationMethodHandlerAdapter();
        mockProviewHandler = EasyMock.createMock(ProviewHandler.class);
        mockManagerService = EasyMock.createMock(ManagerServiceImpl.class);
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockProviewAuditService = EasyMock.createMock(ProviewAuditService.class);
        mockTitleListService = EasyMock.createMock(ProviewTitleListService.class);
        mockGroupService = EasyMock.createMock(GroupService.class);
        mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
        mockJobRequestService = EasyMock.createMock(JobRequestService.class);
        mockVersionIsbnService = EasyMock.createMock(VersionIsbnService.class);
        emailUtil = EasyMock.createMock(EmailUtil.class);
        mockEmailService = EasyMock.createMock(EmailService.class);
        mockOutageService = EasyMock.createMock(OutageService.class);
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
            emailUtil,
            mockEmailService,
            mockOutageService,
            "");

        bookDefinition = EasyMock.createMock(BookDefinition.class);

        titleId = "anId";
        versionString = "v2.0";
        version = new Version(versionString);
        status = "test";
        userName = "tester";
        splitBookTitleIds = Arrays.asList(titleId + _PT + 2, titleId + _PT + 3);

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

        EasyMock.expect(mockProviewHandler.getAllProviewTitleInfo()).andReturn(testAllTitleInfo);
        EasyMock.expect(mockProviewHandler.getAllLatestProviewTitleInfo(testAllTitleInfo))
            .andReturn(testAllLatestTitleInfo);
        EasyMock.replay(mockProviewHandler);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
        final Map<String, Object> model = mav.getModel();
        assertEquals(model.get(WebConstants.KEY_PAGE_SIZE), "20");

        EasyMock.verify(mockProviewHandler);
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

        EasyMock.expect(mockProviewHandler.getAllProviewTitleInfo()).andReturn(mockAllProviewTitleInfo);
        EasyMock.expect(mockProviewHandler.getAllLatestProviewTitleInfo(mockAllProviewTitleInfo))
            .andReturn(mockAllLatestProviewTitleInfo);
        EasyMock.replay(mockProviewHandler);
        EasyMock.expect(mockProviewAuditService.findMaxRequestDateByTitleIds(Collections.singleton("test")))
            .andReturn(Collections.singletonMap("test", new Date()));
        EasyMock.replay(mockProviewAuditService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
        assertEquals(((List<ProviewTitleInfo>) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST)).get(0).getLastStatusUpdateDate(),
            DateFormatUtils.format(new Date(), "yyyyMMdd"));
        final Map<String, Object> model = mav.getModel();

        assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
        assertEquals("20", model.get("pageSize"));

        EasyMock.verify(mockProviewHandler);
    }

    @Test(expected = ProviewException.class)
    public void testPostSelectionsRefreshProviewExcepton() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter(COMMAND, ProviewTitleForm.Command.REFRESH.toString());

        EasyMock.expect(mockProviewHandler.getAllProviewTitleInfo()).andThrow(new ProviewException(""));
        EasyMock.replay(mockProviewHandler);

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

        final List<ProviewTitleInfo> titles = new ArrayList<ProviewTitleInfo>();

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, titles);
        request.setSession(session);
        handlerAdapter.handle(request, response, controller);

        final ServletOutputStream outStream = response.getOutputStream();
        assertTrue(!outStream.toString().isEmpty());
    }

    @Test
    public void testProviewTitleDelete() throws Exception {
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
    public void testProviewTitleRemove() throws Exception {
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
    public void testProviewTitlePromote() throws Exception {
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
        setUpProviewTitleActionMocks();
        setPromoteRequestParameters();
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIdsOnProview(titleId, version,
            REVIEW_BOOK_STATUS)).andReturn(splitBookTitleIds);
        splitBookTitleIds.forEach(this::setUpPromoteTitleMocks);
        final Capture<NotificationEmail> email = getEmailCapture();
        replayProviewTitleActionsMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE, modelAndView.getViewName());
        assertSuccessEmail(email);
    }

    @SneakyThrows
    @Test
    public void testProviewTitleRemovePost() {
        setUpProviewTitleActionMocks();
        setRemoveRequestParameters();
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIdsOnProview(titleId, version,
            REVIEW_BOOK_STATUS, FINAL_BOOK_STATUS)).andReturn(splitBookTitleIds);
        splitBookTitleIds.forEach(this::setUpRemoveTitleMocks);
        final Capture<NotificationEmail> email = getEmailCapture();
        replayProviewTitleActionsMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_REMOVE, modelAndView.getViewName());
        assertSuccessEmail(email);
    }

    @SneakyThrows
    @Test
    public void testProviewTitleDeletePost() {
        setUpProviewTitleActionMocks();
        setDeleteRequestParameters();
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIdsOnProview(titleId, version,
            REMOVED_BOOK_STATUS, CLEANUP_BOOK_STATUS)).andReturn(splitBookTitleIds);
        setUpSplitBooksDelete();
        mockVersionIsbnService.deleteIsbn(titleId, versionString);
        final Capture<NotificationEmail> email = getEmailCapture();
        replayProviewTitleActionsMocks();

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
        assertSuccessEmail(email);
    }

    private void setUpAuthentication() {
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        final CobaltUser user = new CobaltUser(userName, "first", "last", "testing", authorities);
        auth = new UsernamePasswordAuthenticationToken(user, null);
    }

    private void setUpProviewTitleActionMocks() {
        SecurityContextHolder.getContext().setAuthentication(auth);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId))
            .andReturn(bookDefinition);
        mockRunningJobCheck();
        mockChangeStatusForTitle();
    }

    private void mockRunningJobCheck() {
        EasyMock.expect(bookDefinition.getEbookDefinitionId()).andReturn(1L);
        EasyMock.expect(mockJobRequestService.isBookInJobRequest(1L)).andReturn(false);
        EasyMock.expect(mockManagerService.findRunningJob(bookDefinition)).andReturn(null);
    }

    @SneakyThrows
    private void mockChangeStatusForTitle() {
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, new HashMap<String, ProviewTitleContainer>());
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, new ArrayList<>());
        EasyMock.expect(mockProviewHandler.getAllLatestProviewTitleInfo(EasyMock.anyObject()))
            .andReturn(new ArrayList<>()).times(splitBookTitleIds.size());
    }

    @SneakyThrows
    private void setUpSplitBooksDelete() {
        for (final String title : splitBookTitleIds) {
            EasyMock.expect(mockProviewHandler.deleteTitle(title, version)).andReturn(true);
        }
    }

    @SneakyThrows
    private void setUpPromoteTitleMocks(final String titleId) {
        EasyMock.expect(mockProviewHandler.promoteTitle(titleId, versionString)).andReturn(true);
    }

    @SneakyThrows
    private void setUpRemoveTitleMocks(final String titleId) {
        EasyMock.expect(mockProviewHandler.removeTitle(titleId, version)).andReturn(true);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @SneakyThrows
    private Capture<NotificationEmail> getEmailCapture() {
        EasyMock.expect(emailUtil.getEmailRecipientsByUsername(userName))
            .andReturn(Collections.singletonList(new InternetAddress(EMAIL)));
        final Capture<NotificationEmail> email = new Capture();
        mockEmailService.send(EasyMock.capture(email));
        return email;
    }

    private void assertSuccessEmail(final Capture<NotificationEmail> email) {
        final NotificationEmail emailValue = email.getValue();
        assertTrue(emailValue.getSubject().contains(SUCCESS));
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
        EasyMock.replay(mockBookDefinitionService, mockTitleListService, mockEmailService, mockProviewHandler,
            emailUtil, bookDefinition, mockJobRequestService, mockMessageSourceAccessor, mockManagerService,
            mockVersionIsbnService);
    }
}
