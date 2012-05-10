/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobThrottleConfigService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

public class JobThrottleConfigControllerTest {
	private static final String HOST_NAME = "localhost";
	private static final int PORT_NUM = 1289;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerAdapter handlerAdapter;
	private JobThrottleConfigService mockJobThrottleConfigService;
	private ManagerService mockManagerService;
	private JobThrottleConfigController controller;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		handlerAdapter = new AnnotationMethodHandlerAdapter();
		this.mockJobThrottleConfigService = EasyMock.createMock(JobThrottleConfigService.class);
		this.mockManagerService = EasyMock.createMock(ManagerService.class);

		// Set up the controller
		this.controller = new JobThrottleConfigController(PORT_NUM);
		controller.setJobThrottleConfigService(mockJobThrottleConfigService);
		controller.setManagerService(mockManagerService);
		controller.setValidator(new JobThrottleConfigFormValidator());
		controller.setHosts(HOST_NAME+","+HOST_NAME);
	}

	@Test
	public void testInboundGet() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG);
		request.setMethod(HttpMethod.GET.name());
		JobThrottleConfig config = new JobThrottleConfig();
		EasyMock.expect(mockJobThrottleConfigService.getThrottleConfig()).andReturn(config);
		EasyMock.replay(mockJobThrottleConfigService);
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG, mav.getViewName());
		EasyMock.verify(mockJobThrottleConfigService);	
	}
	
	@Test
	public void testSubmitForm() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG);
		request.setMethod(HttpMethod.POST.name());
		
		JobThrottleConfig config = new JobThrottleConfig(8, true, "foobar", 6);
		
		request.setParameter(JobThrottleConfig.Key.coreThreadPoolSize.toString(), String.valueOf(config.getCoreThreadPoolSize()));
		request.setParameter(JobThrottleConfig.Key.stepThrottleEnabled.toString(), String.valueOf(config.isStepThrottleEnabled()));
		request.setParameter(JobThrottleConfig.Key.throttleStepName.toString(), config.getThrottleStepName());
		request.setParameter(JobThrottleConfig.Key.throtttleStepMaxJobs.toString(), String.valueOf(config.getThrotttleStepMaxJobs()));
		
		InetSocketAddress socketAddr = new InetSocketAddress(HOST_NAME, PORT_NUM);
		List<String> stepNames = Arrays.asList("a", "b", "c");
		mockJobThrottleConfigService.saveJobThrottleConfig(config);
		EasyMock.expect(mockManagerService.pushJobThrottleConfiguration(socketAddr, config)).andReturn(new JobOperationResponse()).times(2);
		EasyMock.expect(mockManagerService.getStepNames()).andReturn(stepNames);
		EasyMock.replay(mockJobThrottleConfigService);
		EasyMock.replay(mockManagerService);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);
		
		assertNotNull(mav);
		assertEquals(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG, mav.getViewName());
		Map<String,Object> model = mav.getModel();
		@SuppressWarnings("unchecked")
		List<InfoMessage> mesgs = (List<InfoMessage>) model.get(WebConstants.KEY_INFO_MESSAGES);
		// Expect 3 info messages 1 success, and 2 failures for inability to push to hosts
		assertEquals(3, mesgs.size());
		assertEquals(InfoMessage.Type.SUCCESS, mesgs.get(0).getType());
		assertEquals(InfoMessage.Type.FAIL, mesgs.get(1).getType());
		assertEquals(InfoMessage.Type.FAIL, mesgs.get(2).getType());
		assertNotNull(model.get(JobThrottleConfigController.KEY_STEP_NAMES));
		
		EasyMock.verify(mockJobThrottleConfigService);
		EasyMock.verify(mockManagerService);	
	}
}
