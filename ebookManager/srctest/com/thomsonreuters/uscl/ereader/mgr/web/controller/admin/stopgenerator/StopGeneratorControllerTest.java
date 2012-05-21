/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.stopgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;

public class StopGeneratorControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+StopGeneratorForm.FORM_NAME;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    
    private StopGeneratorController controller;
    private StopGeneratorFormValidator validator;
    private ServerAccessService mockServerAccessService;
	private Properties mockKillSwitchProperties;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	mockServerAccessService = EasyMock.createMock(ServerAccessService.class);
    	mockKillSwitchProperties = EasyMock.createMock(Properties.class);
    	
    	// Set up the controller
    	this.controller = new StopGeneratorController();
    	
    	validator = new StopGeneratorFormValidator();
    	controller.setValidator(validator);	
    	controller.setKillSwitchProperties(mockKillSwitchProperties);
    	controller.setServerAccessService(mockServerAccessService);
	}

	/**
     * Test the GET to the Stop Generator page
     */
	@Test
	public void testGetStopGenerator() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STOP_GENERATOR);
    	request.setMethod(HttpMethod.GET.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STOP_GENERATOR, mav.getViewName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
     * Test the POST to the Stop Generator page
     */
	@Test
	public void testCreateJurisCodePost() {
		String code = "Stop all";
		String property = "test";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STOP_GENERATOR);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("code", code);
    	
    	EasyMock.expect(mockKillSwitchProperties.getProperty(EasyMock.anyObject(String.class))).andReturn(property).times(5);
    	try {
			mockServerAccessService.stopServer(property, property, property, property, property);
		} catch (EBookServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	EasyMock.replay(mockKillSwitchProperties);
    	EasyMock.replay(mockServerAccessService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			// Verify mav is a RedirectView
			View view = mav.getView();
	        assertEquals(RedirectView.class, view.getClass());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	Assert.assertFalse(bindingResult.hasErrors());
	    	
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockKillSwitchProperties);
    	EasyMock.verify(mockServerAccessService);
	}
	
	/**
     * Test the POST to the Stop Generator page validation failure
     */
	@Test
	public void testCreateJurisCodePostValidationFail() {
		String code = "random";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STOP_GENERATOR);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("code", code);
    	
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			 // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STOP_GENERATOR, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        // Check binding state
	        BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
	    	assertNotNull(bindingResult);
	    	Assert.assertTrue(bindingResult.hasErrors());
	    	
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}
}


	