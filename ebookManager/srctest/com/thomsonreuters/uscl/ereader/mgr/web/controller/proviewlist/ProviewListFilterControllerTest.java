package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.FilterCommand;
import org.apache.commons.collections4.CollectionUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class ProviewListFilterControllerTest {
    private static final String TITLE_NAME = "testTitle";
    private static final String TEST_TITLE_ID = "testId".toLowerCase();
    private final Integer TOTAL_NUMBER_OF_VERSIONS = 1;
    private ProviewListFilterController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private OutageService mockOutageService;
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        mockOutageService = EasyMock.createMock(OutageService.class);
        controller = new ProviewListFilterController(mockOutageService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    private void initRequest() {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
        request.setMethod(HttpMethod.POST.name());
    }

    private void initRequest(final String proviewDisplayName, final String titleId) {
        initRequest();
        request.setParameter("FilterCommand", FilterCommand.SEARCH.toString());
        request.setParameter("proviewDisplayName", proviewDisplayName);
        request.setParameter("titleId", titleId);
    }

    private void initResetRequest() {
        initRequest();
        request.setParameter("FilterCommand", FilterCommand.RESET.toString());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testStartWildcard() throws Exception {
        initRequest("%" + TITLE_NAME, "%" + TEST_TITLE_ID.toUpperCase());
        request.setParameter("minVersions", TOTAL_NUMBER_OF_VERSIONS.toString());
        request.setParameter("maxVersions", TOTAL_NUMBER_OF_VERSIONS.toString());
        request.getSession().setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES,
                Collections.singletonList(getProviewTitleInfo()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        validateModel(mav);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testEndWildcard() throws Exception {
        initRequest(TITLE_NAME + "%", TEST_TITLE_ID + "%");
        request.setParameter("minVersions", "1.1");
        request.setParameter("maxVersions", "1.2");
        request.getSession().setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES,
                Collections.singletonList(getProviewTitleInfo()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        validateModel(mav);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testAllWildcard() throws Exception {
        initRequest( "%" + TITLE_NAME + "%", "%" + TEST_TITLE_ID + "%");
        request.setParameter("minVersionsInt", TOTAL_NUMBER_OF_VERSIONS.toString());
        request.setParameter("maxVersionsInt", TOTAL_NUMBER_OF_VERSIONS.toString());
        request.getSession().setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES,
                Collections.singletonList(getProviewTitleInfo()));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        validateModel(mav);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testNoWildcard() throws Exception {
        initRequest( TITLE_NAME, TEST_TITLE_ID);
        request.setParameter("minVersionsInt", TOTAL_NUMBER_OF_VERSIONS.toString());
        request.setParameter("maxVersionsInt", TOTAL_NUMBER_OF_VERSIONS.toString());
        request.getSession().setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES,
                Collections.singletonList(getProviewTitleInfo("a")));

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        Assert.assertNotNull(mav);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
    }

    @Test
    public void testReset() throws Exception {
        initResetRequest();
        final ProviewTitleForm titleForm = new ProviewTitleForm();
        titleForm.setObjectsPerPage("50");

        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, new ArrayList<ProviewTitleInfo>());
        session.setAttribute(ProviewTitleForm.FORM_NAME, titleForm);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        Assert.assertNotNull(mav);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
    }

    @Test
    public void testDoFilterGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        Assert.assertEquals(((RedirectView) mav.getView()).getUrl(), WebConstants.MVC_PROVIEW_TITLES);
    }

    private ProviewTitleInfo getProviewTitleInfo() {
        return getProviewTitleInfo("");
    }

    private ProviewTitleInfo getProviewTitleInfo(final String suffix) {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setTitle(TITLE_NAME + suffix);
        titleInfo.setTitleId(TEST_TITLE_ID + suffix);
        titleInfo.setTotalNumberOfVersions(TOTAL_NUMBER_OF_VERSIONS);
        return titleInfo;
    }

    private void validateModel(final ModelAndView mav) {
        Assert.assertNotNull(mav);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
        List<ProviewTitleInfo> list = (List<ProviewTitleInfo>) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST);
        Assert.assertTrue(CollectionUtils.isNotEmpty(list));
        Assert.assertEquals(list.get(0).getTitleId(), TEST_TITLE_ID);
    }
}
