package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class OutageTypeControllerTest
{
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private OutageService outageService;
    private OutageTypeController controller;

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        outageService = EasyMock.createMock(OutageService.class);

        // Set up the controller
        controller = new OutageTypeController();
        controller.setOutageService(outageService);
        controller.setValidator(new OutageTypeFormValidator());
    }

    @Test
    public void testGetList() throws Exception
    {
        final List<OutageType> outages = new ArrayList<OutageType>();

        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(outageService.getAllOutageType()).andReturn(outages);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_LIST, mav.getViewName());

        // Check the state of the model
        final Map<String, Object> model = mav.getModel();
        final List<OutageType> actual = (List<OutageType>) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(outages, actual);

        EasyMock.verify(outageService);
    }

    @Test
    public void testCreateGet() throws Exception
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_CREATE);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_CREATE, mav.getViewName());
    }

    @Test
    public void testCreatePost() throws Exception
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final OutageType outage = setupParametersAndOutage();
        outageService.saveOutageType(outage);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        // Verify mav is a RedirectView
        final View view = mav.getView();
        assertEquals(RedirectView.class, view.getClass());

        EasyMock.verify(outageService);
    }

    @Test
    public void testEditGet() throws Exception
    {
        final Long id = 99L;
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", id.toString());

        final OutageType type = new OutageType();
        type.setId(id);

        EasyMock.expect(outageService.findOutageTypeByPrimaryKey(id)).andReturn(type);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_EDIT, mav.getViewName());

        EasyMock.verify(outageService);
    }

    @Test
    public void testEditPost() throws Exception
    {
        final String id = "99";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("outageTypeId", id);

        setupParametersAndOutage();
        outageService.saveOutageType(EasyMock.anyObject(OutageType.class));
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        // Verify mav is a RedirectView
        final View view = mav.getView();
        assertEquals(RedirectView.class, view.getClass());

        EasyMock.verify(outageService);
    }

    @Test
    public void testDeleteGet() throws Exception
    {
        final Long id = 99L;
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", id.toString());

        final OutageType type = new OutageType();
        type.setId(id);

        final List<PlannedOutage> outage = new ArrayList<>();

        EasyMock.expect(outageService.findOutageTypeByPrimaryKey(id)).andReturn(type);
        EasyMock.expect(outageService.getAllPlannedOutagesForType(id)).andReturn(outage);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_DELETE, mav.getViewName());

        final Map<String, Object> model = mav.getModel();
        final OutageType actual = (OutageType) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(type, actual);

        EasyMock.verify(outageService);
    }

    @Test
    public void testDeletePost() throws Exception
    {
        final Long id = 99L;
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_TYPE_DELETE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("outageTypeId", id.toString());

        final OutageType outage = new OutageType();
        outage.setId(id);

        outageService.deleteOutageType(id);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        // Verify mav is a RedirectView
        final View view = mav.getView();
        assertEquals(RedirectView.class, view.getClass());

        EasyMock.verify(outageService);
    }

    private OutageType setupParametersAndOutage()
    {
        final String system = "system";
        final String subSystem = "subSystem";

        request.setParameter("system", system);
        request.setParameter("subSystem", subSystem);

        final OutageType type = new OutageType();
        type.setSubSystem(subSystem);
        type.setSystem(system);

        return type;
    }
}
