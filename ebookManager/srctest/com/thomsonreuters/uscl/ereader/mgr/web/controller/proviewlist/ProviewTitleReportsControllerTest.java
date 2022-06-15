package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleReportInfo;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProviewTitleReportsControllerTest {

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
    private ProviewTitleReportsController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private HandlerAdapter handlerAdapter;

    @Mock
    private VersionIsbnService mockVersionIsbnService;
    @Mock
    private BookDefinitionService mockBookDefinitionService;
    @Mock
    private ProviewTitleListService mockProviewTitleListService;
    private String titleId;
    private String versionString;
    private String status;
    private String userName;
    private Authentication auth;

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

    private void initProviewTitlesReportGetRequest() {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLES_REPORT);
        request.setMethod(HttpMethod.GET.name());
    }

    @Test
    public void getSelections_filteringParamsStartingWithWildcardAreGiven_filteringParamsArePassedToServiceLayerTitlesReport()
            throws Exception {
        initProviewTitlesReportGetRequest();
        final String proviewDisplayName = TEST_TITLE_NAME + PERCENT;
        request.setParameter(PROVIEW_DISPLAY_NAME, proviewDisplayName);
        final String titleId = TEST_TITLE_ID + PERCENT;
        request.setParameter(TITLE_ID, titleId);
        request.setParameter(MIN_VERSIONS_FILTER, TOTAL_NUMBER_OF_VERSIONS.toString());
        request.setParameter(MAX_VERSIONS_FILTER, Integer.toString(3));
        request.setParameter(STATUS_KEY, STATUS_REVIEW);
        final ArgumentCaptor<ProviewTitlesReportFilterForm> formCaptor = ArgumentCaptor.forClass(ProviewTitlesReportFilterForm.class);
        when(mockProviewTitleListService.getSelectedProviewTitleReportInfo(formCaptor.capture()))
                .thenReturn(Collections.singletonList(getProviewTitleReportInfo()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        final HttpSession httpSession = request.getSession();

        List<ProviewTitleReportInfo> lstReportTitles =  (List<ProviewTitleReportInfo>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES_REPORT);
        assertTrue(CollectionUtils.isNotEmpty(lstReportTitles));
        assertEquals(TEST_TITLE_ID, lstReportTitles.get(0).getId());

        final ProviewTitlesReportFilterForm form = formCaptor.getValue();
        assertEquals(proviewDisplayName, form.getProviewDisplayName());
        assertEquals(titleId, form.getTitleId());
        assertEquals(TOTAL_NUMBER_OF_VERSIONS, form.getMinVersionsInt());
        assertEquals(Integer.valueOf(3), form.getMaxVersionsInt());
        assertEquals(STATUS_REVIEW, form.getStatus());
    }

    private ProviewTitleReportInfo getProviewTitleReportInfo() {
        final ProviewTitleReportInfo titleInfo = new ProviewTitleReportInfo();
        titleInfo.setName(TEST_TITLE_NAME);
        titleInfo.setId(TEST_TITLE_ID);
        titleInfo.setTotalNumberOfVersions(TOTAL_NUMBER_OF_VERSIONS);
        return titleInfo;
    }

    @Test
    public void testDownloadPublishingTitleReportExcel() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_TITLE_REPORT_DOWNLOAD);
        request.setMethod(HttpMethod.GET.name());

        final List<ProviewTitleReportInfo> titles = new ArrayList<>();

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES_REPORT, titles);
        request.setSession(session);
        handlerAdapter.handle(request, response, controller);

        final ServletOutputStream outStream = response.getOutputStream();
        assertFalse(outStream.toString().isEmpty());
    }

    private void setUpAuthentication() {
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        final CobaltUser user = new CobaltUser(userName, "first", "last", "testing", authorities);
        auth = new UsernamePasswordAuthenticationToken(user, null);
    }

}
