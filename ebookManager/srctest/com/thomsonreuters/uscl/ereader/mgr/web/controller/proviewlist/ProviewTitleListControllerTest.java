package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

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
    private String versionAsString;
    private String status;
    private String userName;
    private String password;
    private String first;
    private String last;
    private List<String> splitBookTitleIds;
    private Collection<GrantedAuthority> authorities;
    private CobaltUser user;
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
        version = new Version("v2.0");
        versionAsString = "2";
        status = "test";
        userName = "tester";
        password = "testing";
        first = "first";
        last = "last";
        splitBookTitleIds = Arrays.asList("splitBookTitle1", "splitBookTitle2", "splitBookTitle3");

        authorities = new HashSet<>();
        user = new CobaltUser(userName, first, last, password, authorities);
        auth = new UsernamePasswordAuthenticationToken(user, null);
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
        request.setParameter("command", ProviewTitleForm.Command.REFRESH.toString());
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
        request.setParameter("command", ProviewTitleForm.Command.REFRESH.toString());

        EasyMock.expect(mockProviewHandler.getAllProviewTitleInfo()).andThrow(new ProviewException(""));
        EasyMock.replay(mockProviewHandler);

        handlerAdapter.handle(request, response, controller);
    }

    @Test
    public void testPostSelectionsPageSize() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("command", ProviewTitleForm.Command.PAGESIZE.toString());
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
        request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
        request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
        request.setParameter("status", WebConstants.KEY_STATUS);
        request.setParameter("lastUpdate", "test");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();

        assertEquals(WebConstants.KEY_TITLE_ID, model.get("titleId"));
        assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
        assertEquals(WebConstants.KEY_STATUS, model.get("status"));
    }

    @Test
    public void testProviewTitleRemove() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_REMOVE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
        request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
        request.setParameter("status", WebConstants.KEY_STATUS);
        request.setParameter("lastUpdate", "test");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();

        assertEquals(WebConstants.KEY_TITLE_ID, model.get("titleId"));
        assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
        assertEquals(WebConstants.KEY_STATUS, model.get("status"));
    }

    @Test
    public void testProviewTitlePromote() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_PROMOTE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("titleId", WebConstants.KEY_TITLE_ID);
        request.setParameter("versionNumber", WebConstants.KEY_VERSION_NUMBER);
        request.setParameter("status", WebConstants.KEY_STATUS);
        request.setParameter("lastUpdate", "test");

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        final Map<String, Object> model = mav.getModel();

        assertEquals(WebConstants.KEY_TITLE_ID, model.get("titleId"));
        assertEquals(WebConstants.KEY_VERSION_NUMBER, model.get("versionNumber"));
        assertEquals(WebConstants.KEY_STATUS, model.get("status"));
    }

    @Test
    public void testProviewTitlePromotePost() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
        setPromoteRequestParameters();

        final BookDefinition bookDefinition = EasyMock.createNiceMock(BookDefinition.class);
        final long definitionId = 127L;

        EasyMock.expect(mockProviewHandler.promoteTitle(titleId, versionAsString)).andReturn(true);
        EasyMock.replay(mockProviewHandler);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId))
            .andReturn(bookDefinition)
            .times(2);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(bookDefinition)).andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(bookDefinition.getEbookDefinitionId()).andReturn(definitionId).times(2);
        EasyMock.expect(bookDefinition.isSplitBook()).andReturn(false);
        EasyMock.replay(bookDefinition);

        EasyMock.expect(mockJobRequestService.isBookInJobRequest(definitionId)).andReturn(false);
        setUpEmailMocks();
        EasyMock.replay(mockEmailService);
        EasyMock.replay(mockJobRequestService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE, mav.getViewName());
    }

    @SneakyThrows
    @Test
    public void testProviewTitleRemovePostSimpleBook() {
        SecurityContextHolder.getContext().setAuthentication(auth);
        setRemoveRequestParameters();
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).andReturn(bookDefinition);
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIds(bookDefinition, version))
            .andReturn(Collections.singletonList(titleId));
        setUpRemoveTitleMocks(titleId);
        setUpEmailMocks();
        EasyMock.replay(mockBookDefinitionService, mockTitleListService, mockEmailService,
            mockProviewHandler, emailUtil);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_REMOVE, modelAndView.getViewName());
    }

    @SneakyThrows
    @Test
    public void testProviewTitleDeletePostSimpleBook() {
        SecurityContextHolder.getContext().setAuthentication(auth);
        setDeleteRequestParameters();
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).andReturn(bookDefinition);
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIds(bookDefinition, version))
            .andReturn(Collections.singletonList(titleId));
        EasyMock.expect(mockProviewHandler.deleteTitle(titleId, version)).andReturn(true);
        setUpEmailMocks();
        EasyMock.replay(mockBookDefinitionService, mockTitleListService, mockEmailService,
            mockProviewHandler, emailUtil);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
    }

    @SneakyThrows
    @Test
    public void testProviewTitleRemovePostSplitBook() {
        SecurityContextHolder.getContext().setAuthentication(auth);
        setRemoveRequestParameters();
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).andReturn(bookDefinition);
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIds(bookDefinition, version))
            .andReturn(splitBookTitleIds);
        for (String title : splitBookTitleIds) {
            setUpRemoveTitleMocks(title);
        }
        setUpEmailMocks();
        EasyMock.replay(mockBookDefinitionService, mockTitleListService, mockEmailService,
            mockProviewHandler, emailUtil);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_REMOVE, modelAndView.getViewName());
    }

    @SneakyThrows
    @Test
    public void testProviewTitleDeletePostSplitBook() {
        SecurityContextHolder.getContext().setAuthentication(auth);
        setDeleteRequestParameters();
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByTitle(titleId)).andReturn(bookDefinition);
        EasyMock.expect(mockTitleListService.getAllSplitBookTitleIds(bookDefinition, version))
            .andReturn(Collections.singletonList(titleId));
        for (String title : splitBookTitleIds) {
            EasyMock.expect(mockProviewHandler.deleteTitle(title, version)).andReturn(true);
        }
        setUpEmailMocks();
        EasyMock.replay(mockBookDefinitionService, mockTitleListService, mockEmailService,
            mockProviewHandler, emailUtil);

        final ModelAndView modelAndView = handlerAdapter.handle(request, response, controller);

        assertEquals(WebConstants.VIEW_PROVIEW_TITLE_DELETE, modelAndView.getViewName());
    }

    @SneakyThrows
    private void setUpRemoveTitleMocks(String titleId) {
        EasyMock.expect(mockProviewHandler.isTitleInProview(titleId)).andReturn(true);
        EasyMock.expect(mockProviewHandler.removeTitle(titleId, version)).andReturn(true);
    }

    @SneakyThrows
    private void setUpEmailMocks() {
        EasyMock.expect(emailUtil.getEmailRecipientsByUsername(userName))
            .andReturn(Collections.singletonList(new InternetAddress("a@mail.com")));
        mockEmailService.send(EasyMock.anyObject());
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

    private void setTitleActionRequestParameters(String path, Command command) {
        request.setRequestURI("/" + path);
        request.setMethod(HttpMethod.POST.name());
        setRequestParameters(request);
        request.setParameter("command", command.toString());
    }

    private void setRequestParameters(MockHttpServletRequest request) {
        request.setParameter("titleId", titleId);
        request.setParameter("version", versionAsString);
        request.setParameter("status", status);
    }
}
