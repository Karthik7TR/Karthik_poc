/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage.Operation;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

public class OutageControllerTest {
	private static final String HOST_NAME = "localhost";
	private static final int PORT_NUM = 1289;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerAdapter handlerAdapter;
	private OutageService outageService;
	private ManagerService mockManagerService;
	private OutageController controller;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		handlerAdapter = new AnnotationMethodHandlerAdapter();
		this.outageService = EasyMock.createMock(OutageService.class);
		this.mockManagerService = EasyMock.createMock(ManagerService.class);

		// Set up the controller
		this.controller = new OutageController(PORT_NUM);
		controller.setOutageService(outageService);
		controller.setManagerService(mockManagerService);
		controller.setValidator(new OutageFormValidator());
		controller.setGeneratorHosts(HOST_NAME+","+HOST_NAME);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllActiveAndScheduledOutages() throws Exception {
		List<PlannedOutage> outages = new ArrayList<PlannedOutage>();
		
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST);
		request.setMethod(HttpMethod.GET.name());
		
		EasyMock.expect(outageService.getAllActiveAndScheduledPlannedOutages()).andReturn(outages);
		EasyMock.replay(outageService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_ACTIVE_LIST, mav.getViewName());
		
		// Check the state of the model
        Map<String,Object> model = mav.getModel();
        List<PlannedOutage> actual = (List<PlannedOutage>) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(outages, actual);

		EasyMock.verify(outageService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllOutages() throws Exception {
		List<PlannedOutage> outages = new ArrayList<PlannedOutage>();
		
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_FULL_LIST);
		request.setMethod(HttpMethod.GET.name());
		
		EasyMock.expect(outageService.getAllPlannedOutages()).andReturn(outages);
		EasyMock.replay(outageService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_FULL_LIST, mav.getViewName());
		
		// Check the state of the model
        Map<String,Object> model = mav.getModel();
        List<PlannedOutage> actual = (List<PlannedOutage>) model.get(WebConstants.KEY_OUTAGE);
        Assert.assertEquals(outages, actual);

		EasyMock.verify(outageService);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateGet() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_CREATE);
		request.setMethod(HttpMethod.GET.name());
		
		EasyMock.expect(outageService.getAllOutageType()).andReturn(new ArrayList<OutageType>());
		EasyMock.replay(outageService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_CREATE, mav.getViewName());
		
		// Check the state of the model
        Map<String,Object> model = mav.getModel();
        List<OutageType> types = (List<OutageType>) model.get("outageType");
        Assert.assertEquals(types, new ArrayList<OutageType>());
		
		EasyMock.verify(outageService);
	}
	
	@Test
	public void testCreatePost() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_CREATE);
		request.setMethod(HttpMethod.POST.name());
		
		InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);
		
		PlannedOutage outage = setupParametersAndOutage();
		outage.setId(null);
		outageService.savePlannedOutage(outage);
		EasyMock.expect(outageService.getAllOutageType()).andReturn(new ArrayList<OutageType>());
		EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(EasyMock.anyLong())).andReturn(outage);
		EasyMock.expect(mockManagerService.pushPlannedOutage(outage, socketAddr)).andReturn(new SimpleRestServiceResponse()).times(2);
		EasyMock.replay(outageService);
		EasyMock.replay(mockManagerService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_CREATE, mav.getViewName());
		Map<String,Object> model = mav.getModel();
		@SuppressWarnings("unchecked")
		List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
		// Expect 3 info messages 1 success, and 2 failures for inability to push to host
		assertEquals(3, mesgs.size());
		assertEquals(InfoMessage.Type.SUCCESS, mesgs.get(0).getType());
		assertEquals(InfoMessage.Type.FAIL, mesgs.get(1).getType());
		assertEquals(InfoMessage.Type.FAIL, mesgs.get(2).getType());
		
		EasyMock.verify(outageService);
		EasyMock.verify(mockManagerService);	
	}
	
	@Test
	public void testEditGet() throws Exception {
		Long id = 99L;
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_EDIT);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("id", id.toString());
		
		PlannedOutage outage = new PlannedOutage();
		OutageType type = new OutageType();
		type.setId(id);
		outage.setId(id);
		outage.setOutageType(type);
		
		EasyMock.expect(outageService.getAllOutageType()).andReturn(new ArrayList<OutageType>());
		EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
		EasyMock.replay(outageService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_EDIT, mav.getViewName());
		
		EasyMock.verify(outageService);
	}
	
	@Test
	public void testEditPost() throws Exception {
		String id = "99";
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_EDIT);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("plannedOutageId", id);
		
		InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);
		
		PlannedOutage outage = setupParametersAndOutage();
		outage.setId(Long.valueOf(id));
		outageService.savePlannedOutage(outage);
		EasyMock.expect(outageService.getAllOutageType()).andReturn(new ArrayList<OutageType>());
		EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(Long.valueOf(id))).andReturn(outage).times(2);
		EasyMock.expect(mockManagerService.pushPlannedOutage(outage, socketAddr)).andReturn(new SimpleRestServiceResponse()).times(2);
		EasyMock.replay(outageService);
		EasyMock.replay(mockManagerService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_EDIT, mav.getViewName());
		Map<String,Object> model = mav.getModel();
		@SuppressWarnings("unchecked")
		List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
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
		Long id = 99L;
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_DELETE);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("id", id.toString());
		
		PlannedOutage outage = new PlannedOutage();
		OutageType type = new OutageType();
		type.setId(id);
		outage.setId(id);
		outage.setOutageType(type);
		
		EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
		EasyMock.replay(outageService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_DELETE, mav.getViewName());
		
		Map<String,Object> model = mav.getModel();
		PlannedOutage actual = (PlannedOutage) model.get(WebConstants.KEY_OUTAGE);
		Assert.assertEquals(outage, actual);
		
		EasyMock.verify(outageService);
	}
	
	@Test
	public void testDeletePost() throws Exception {
		Long id = 99L;
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_OUTAGE_DELETE);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("plannedOutageId", id.toString());
		InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);
		
		PlannedOutage outage = new PlannedOutage();
		outage.setId(id);
		
		outageService.deletePlannedOutage(id);
		EasyMock.expect(outageService.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
		EasyMock.replay(outageService);
		EasyMock.expect(mockManagerService.pushPlannedOutage(outage, socketAddr)).andReturn(new SimpleRestServiceResponse()).times(2);
		EasyMock.replay(mockManagerService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_OUTAGE_DELETE, mav.getViewName());
		Map<String,Object> model = mav.getModel();
		@SuppressWarnings("unchecked")
		List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
		// Expect 3 info messages 1 success, and 2 failures for inability to push to host
		assertEquals(3, mesgs.size());
		assertEquals(InfoMessage.Type.SUCCESS, mesgs.get(0).getType());
		assertEquals(InfoMessage.Type.FAIL, mesgs.get(1).getType());
		assertEquals(InfoMessage.Type.FAIL, mesgs.get(2).getType());
		
		EasyMock.verify(outageService);
		EasyMock.verify(mockManagerService);	
	}
	
	private PlannedOutage setupParametersAndOutage() throws Exception {
		String outageTypeId = "1";
		String startTimeString = "05/30/2012 15:41:55";
		String endTimeString = "05/30/2012 15:41:56";
		String reason = "test";

		request.setParameter("outageTypeId", outageTypeId);
		request.setParameter("startTimeString", startTimeString);
		request.setParameter("endTimeString", endTimeString);
		request.setParameter("reason", reason);
		
		PlannedOutage outage = new PlannedOutage();
		OutageType type = new OutageType();
		type.setId(Long.valueOf(outageTypeId));
		outage.setOutageType(type);
		String[] parsePatterns = { CoreConstants.DATE_TIME_FORMAT_PATTERN };
		outage.setStartTime(DateUtils.parseDate(startTimeString, parsePatterns));
		outage.setEndTime(DateUtils.parseDate(endTimeString, parsePatterns));
		outage.setReason(reason);
		outage.setOperation(Operation.SAVE);
		
		return outage;
	}
}
