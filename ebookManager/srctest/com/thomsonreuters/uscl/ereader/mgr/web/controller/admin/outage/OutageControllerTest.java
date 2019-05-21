package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage.Operation;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
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

public final class OutageControllerTest {
    private static final String HOST_NAME = "localhost";
    private static final int PORT_NUM = 1289;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private OutageService outageService;
    private ManagerService mockManagerService;
    private OutageController controller;

    private String uxpectedFormatDate = "30 February";

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        outageService = EasyMock.createMock(OutageService.class);
        mockManagerService = EasyMock.createMock(ManagerService.class);

        // Set up the controller
        controller = new OutageController(
            mockManagerService,
            outageService,
            new OutageFormValidator(),
            HOST_NAME + "," + HOST_NAME,
            PORT_NUM);
    }

    @Test
    public void testGetAllActiveAndScheduledOutages() throws Exception {
        final List<PlannedOutage> outages = new ArrayList<>();

        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(outageService.getAllActiveAndScheduledPlannedOutages()).andReturn(outages);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_ACTIVE_LIST, mav.getViewName());

        // Check the state of the model
        final Map<String, Object> model = mav.getModel();
        final List<PlannedOutage> actual = (List<PlannedOutage>) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(outages, actual);

        EasyMock.verify(outageService);
    }

    @Test
    public void testGetAllOutages() throws Exception {
        final List<PlannedOutage> outages = new ArrayList<>();

        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_FULL_LIST);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(outageService.getAllPlannedOutages()).andReturn(outages);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_FULL_LIST, mav.getViewName());

        // Check the state of the model
        final Map<String, Object> model = mav.getModel();
        final List<PlannedOutage> actual = (List<PlannedOutage>) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(outages, actual);

        EasyMock.verify(outageService);
    }

    @Test
    public void testCreateGet() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_CREATE);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(outageService.getAllActiveOutageTypes()).andReturn(new ArrayList<OutageType>());
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_CREATE, mav.getViewName());

        // Check the state of the model
        final Map<String, Object> model = mav.getModel();
        final List<OutageType> types = (List<OutageType>) model.get("outageType");
        Assert.assertEquals(types, new ArrayList<OutageType>());

        EasyMock.verify(outageService);
    }

    @Test
    public void testCreatePost() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);

        final PlannedOutage outage = setupParametersAndOutage();
        outage.setId(null);
        outageService.savePlannedOutage(outage);
        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(EasyMock.anyLong())).andReturn(outage);
        EasyMock.expect(mockManagerService.pushPlannedOutage(outage, socketAddr))
            .andReturn(new SimpleRestServiceResponse())
            .times(2);
        EasyMock.replay(outageService);
        EasyMock.replay(mockManagerService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        // Verify mav is a RedirectView
        final View view = mav.getView();
        assertEquals(RedirectView.class, view.getClass());

        EasyMock.verify(outageService);
        EasyMock.verify(mockManagerService);
    }

    @Test
    public void testEditGet() throws Exception {
        final Long id = 99L;
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", id.toString());

        final PlannedOutage outage = new PlannedOutage();
        final OutageType type = new OutageType();
        type.setId(id);
        outage.setId(id);
        outage.setOutageType(type);

        EasyMock.expect(outageService.getAllActiveOutageTypes()).andReturn(new ArrayList<OutageType>());
        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_EDIT, mav.getViewName());

        EasyMock.verify(outageService);
    }

    @Test
    public void testEditPost() throws Exception {
        final String id = "99";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("plannedOutageId", id);

        final InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);

        final PlannedOutage outage = setupParametersAndOutage();
        outage.setId(Long.valueOf(id));
        outageService.savePlannedOutage(outage);
        EasyMock.expect(outageService.getAllActiveOutageTypes()).andReturn(new ArrayList<OutageType>());
        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(Long.valueOf(id))).andReturn(outage).times(2);
        EasyMock.expect(mockManagerService.pushPlannedOutage(outage, socketAddr))
            .andReturn(new SimpleRestServiceResponse())
            .times(2);
        EasyMock.replay(outageService);
        EasyMock.replay(mockManagerService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_EDIT, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
        // Expect 3 info messages 1 success, and 2 failures for inability to push to host
        assertEquals(3, mesgs.size());
        assertEquals(InfoMessage.Type.SUCCESS, mesgs.get(0).getType());
        assertEquals(InfoMessage.Type.FAIL, mesgs.get(1).getType());
        assertEquals(InfoMessage.Type.FAIL, mesgs.get(2).getType());

        EasyMock.verify(outageService);
        EasyMock.verify(mockManagerService);
    }

    @Test
    public void testDeleteGet() throws Exception {
        final Long id = 99L;
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", id.toString());

        final PlannedOutage outage = new PlannedOutage();
        final OutageType type = new OutageType();
        type.setId(id);
        outage.setId(id);
        outage.setOutageType(type);

        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
        EasyMock.replay(outageService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_DELETE, mav.getViewName());

        final Map<String, Object> model = mav.getModel();
        final PlannedOutage actual = (PlannedOutage) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(outage, actual);

        EasyMock.verify(outageService);
    }

    @Test
    public void testDeletePost() throws Exception {
        final Long id = 99L;
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_DELETE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("plannedOutageId", id.toString());
        request.setParameter("startTimeString", uxpectedFormatDate);
        final InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);

        final PlannedOutage outage = new PlannedOutage();
        outage.setId(id);

        final List<PlannedOutage> otherOutages = new ArrayList<>();

        outageService.deletePlannedOutage(id);
        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
        EasyMock.expect(outageService.getAllActiveAndScheduledPlannedOutages()).andReturn(otherOutages);
        EasyMock.replay(outageService);
        EasyMock.expect(mockManagerService.pushPlannedOutage(outage, socketAddr))
            .andReturn(new SimpleRestServiceResponse())
            .times(2);
        EasyMock.replay(mockManagerService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        assertNotNull(mav);
        assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_ACTIVE_LIST, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
        // Expect 3 info messages 1 success, and 2 failures for inability to push to host
        assertEquals(3, mesgs.size());
        assertEquals(InfoMessage.Type.SUCCESS, mesgs.get(0).getType());
        assertEquals(InfoMessage.Type.FAIL, mesgs.get(1).getType());
        assertEquals(InfoMessage.Type.FAIL, mesgs.get(2).getType());

        assertEquals(otherOutages, model.get(WebConstants.KEY_OUTAGE));

        EasyMock.verify(outageService);
        EasyMock.verify(mockManagerService);
    }

    private PlannedOutage setupParametersAndOutage() throws Exception {
        final String outageTypeId = "1";
        final String startTimeString = "2028-07-04T12:03:29.000Z";
        final String endTimeString = "2028-07-04T12:10:29.000Z";
        final String reason = "test";

        request.setParameter("outageTypeId", outageTypeId);
        request.setParameter("startTimeString", startTimeString);
        request.setParameter("endTimeString", endTimeString);
        request.setParameter("reason", reason);

        final PlannedOutage outage = new PlannedOutage();
        final OutageType type = new OutageType();
        type.setId(Long.valueOf(outageTypeId));
        outage.setOutageType(type);
        outage.setStartTime(Date.from(Instant.parse(startTimeString)));
        outage.setEndTime(Date.from(Instant.parse(endTimeString)));
        outage.setReason(reason);
        outage.setOperation(Operation.SAVE);

        return outage;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditOutageGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(outageService);

        handlerAdapter.handle(request, response, controller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteOutageGetException() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_DELETE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", "1");
        EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(EasyMock.anyLong())).andThrow(new IllegalArgumentException());
        EasyMock.replay(outageService);

        handlerAdapter.handle(request, response, controller);
    }
}
