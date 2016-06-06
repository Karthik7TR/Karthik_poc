/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;

public class UserPreferencesControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+UserPreferencesForm.FORM_NAME;

    private UserPreferencesController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private UserPreferenceService mockService;
    private UserPreferencesFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Code service
    	this.mockService = EasyMock.createMock(UserPreferenceService.class);
    	
    	// Set up the controller
    	this.controller = new UserPreferencesController();
    	controller.setUserPreferenceService(mockService);
    	
    	validator = new UserPreferencesFormValidator();
    	controller.setValidator(validator);	
	}
	
	
	/**
     * Test the GET to the User Preferences Page
     */
	@Test
	public void testGetPreferences() {
		request.setRequestURI("/"+ WebConstants.MVC_USER_PREFERENCES);
    	request.setMethod(HttpMethod.GET.name());
    	
    	UserPreference preference = new UserPreference();
    	preference.setEmails("a@a.com,b@b.com");
    	
    	EasyMock.expect(mockService.findByUsername(EasyMock.anyObject(String.class))).andReturn(preference);
    	EasyMock.replay(mockService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_USER_PREFERENCES, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        Integer emailSize = (Integer) model.get("numberOfEmails");
	        
	        Assert.assertTrue(2 == emailSize);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockService);
	}
	
	/**
     * Test the POST to the User Preferences Success
     */
	@Test
	public void testPostPreferences() {
		request.setRequestURI("/"+ WebConstants.MVC_USER_PREFERENCES);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("emails", new String[] {"a@a.com"});
    	
    	UserPreference preference = new UserPreference();
    	mockService.save(preference);
    	EasyMock.replay(mockService);
    	
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
		EasyMock.verify(mockService);
	}
	
	/**
     * Test the POST to the User Preferences with validation error
     */
	@Test
	public void testPostPreferencesFail() {
		request.setRequestURI("/"+ WebConstants.MVC_USER_PREFERENCES);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("emails", new String[] {"acom"});
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
			assertEquals(WebConstants.VIEW_USER_PREFERENCES, mav.getViewName());
	        
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


	