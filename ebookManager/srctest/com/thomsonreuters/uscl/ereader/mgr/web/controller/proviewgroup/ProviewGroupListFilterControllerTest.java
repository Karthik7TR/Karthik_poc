package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class ProviewGroupListFilterControllerTest {
    private ProviewGroupListFilterController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private AnnotationMethodHandlerAdapter handlerAdapter;
    private OutageService outageService;

    @Before
    public void setUp() {
        outageService = EasyMock.createMock(OutageService.class);
        controller = new ProviewGroupListFilterController(outageService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    @Test
    public void testDoFilterGetWithoutParams() throws Exception {
        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, allLatestProviewGroups);

        testDoFilterGet(false);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDoFilterGetWithParams() throws Exception {
        final String groupName = "GroupName";
        final String groupId = "GroupID";
        final HttpSession session = request.getSession();

        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();
        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setGroupName(groupName);
        proviewGroup.setGroupId(groupId);
        allLatestProviewGroups.add(proviewGroup);
        session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, allLatestProviewGroups);

        testDoFilterGet(false);
    }

    @Test
    public void testDoFilterGetProviewDown() throws Exception {
        final HttpSession session = request.getSession();
        session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, null);

        testDoFilterGet(true);
    }

    @Test
    public void testDoFilterGetResetProviewDown() throws Exception {
        final HttpSession session = request.getSession();
        request.setParameter("FilterCommand", ProviewListFilterForm.FilterCommand.RESET.toString());
        session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, null);

        testDoFilterGet(true);
    }

    public void testDoFilterGet(boolean errorOccurred) throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_LIST_FILTERED);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);

        if (errorOccurred) {
            Assert.assertEquals(Boolean.TRUE, mav.getModel().get(WebConstants.KEY_ERROR_OCCURRED));
        } else {
            Assert.assertNotNull(mav.getModel().get(WebConstants.KEY_PAGINATED_LIST));
        }
    }
}
