package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class ProviewGroupListFilterControllerTest
{
    private ProviewGroupListFilterController controller;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private AnnotationMethodHandlerAdapter handlerAdapter;

    @Before
    public void setUp()
    {
        controller = new ProviewGroupListFilterController();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        handlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDoFilterPost() throws Exception
    {
        final String groupName = "GroupName";
        final String groupId = "GroupID";

        request.setRequestURI("/" + WebConstants.MVC_PROVIEW_GROUP_LIST_FILTERED_POST);
        request.setMethod(HttpMethod.POST.name());
        final HttpSession session = request.getSession();

        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();
        final ProviewGroup proviewGroup = new ProviewGroup();
        proviewGroup.setGroupName(groupName);
        proviewGroup.setGroupId(groupId);
        allLatestProviewGroups.add(proviewGroup);
        session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, allLatestProviewGroups);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_GROUPS);
    }
}
