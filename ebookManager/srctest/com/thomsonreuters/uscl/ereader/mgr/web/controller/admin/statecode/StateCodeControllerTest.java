/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class StateCodeControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+StateCodeForm.FORM_NAME;
	private static final StateCode STATE_CODE = new StateCode();
	private static final Long STATE_ID = 1L;
    private StateCodeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CodeService mockCodeService;
    private StateCodeFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Code service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Set up the controller
    	this.controller = new StateCodeController();
    	controller.setCodeService(mockCodeService);
    	
    	validator = new StateCodeFormValidator();
    	validator.setCodeService(mockCodeService);
    	controller.setValidator(validator);	
    	
    	STATE_CODE.setId(STATE_ID);
    	STATE_CODE.setName("test");
	}

	/**
     * Test the GET to the List page
     */
	@SuppressWarnings("unchecked")
	@Test
	public void testViewStateCodeList() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_VIEW);
    	request.setMethod(HttpMethod.GET.name());
    	
    	EasyMock.expect(mockCodeService.getAllStateCodes()).andReturn(new ArrayList<StateCode>());
    	EasyMock.replay(mockCodeService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_VIEW, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        List<StateCode> codes = (List<StateCode>) model.get(WebConstants.KEY_STATE_CODE);
	        assertEquals(0, codes.size());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCodeService);
	}
	
	
	/**
     * Test the GET to the Create Page
     */
	@Test
	public void testCreateStateCodeGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_CREATE);
    	request.setMethod(HttpMethod.GET.name());
    	
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE, mav.getViewName());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
     * Test the POST to the Create Page Success
     */
	@Test
	public void testCreateStateCodePost() {
		String name = "test";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("name", name);
    	
    	StateCode code = new StateCode();
    	code.setName(name);
    	
    	EasyMock.expect(mockCodeService.getStateCodeByName(name)).andReturn(null);
    	mockCodeService.saveStateCode(code);
    	EasyMock.replay(mockCodeService);
    	
    	
    	
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
		EasyMock.verify(mockCodeService);
	}
	
	/**
     * Test the POST to the Create Page Fail
     */
	@Test
	public void testCreateStateCodePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE, mav.getViewName());
	        
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
	
	/**
     * Test the GET to the Edit Page
     */
	@Test
	public void testEditStateCodeGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", STATE_ID.toString());
    	
    	EasyMock.expect(mockCodeService.getStateCodeById(STATE_ID)).andReturn(STATE_CODE);
    	EasyMock.replay(mockCodeService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        StateCode actual = (StateCode) model.get(WebConstants.KEY_STATE_CODE);
	        
	        Assert.assertEquals(STATE_CODE, actual);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	/**
     * Test the POST to the Edit Page Success
     */
	@Test
	public void testEditStateCodePost() {
		String name = STATE_CODE.getName();
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("stateId", STATE_ID.toString());
    	request.setParameter("name", name);
    	
    	EasyMock.expect(mockCodeService.getStateCodeByName(name)).andReturn(null);
    	mockCodeService.saveStateCode(STATE_CODE);
    	EasyMock.replay(mockCodeService);
    	
    	
    	
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
		EasyMock.verify(mockCodeService);
	}
	
	/**
     * Test the POST to the Edit Page Fail
     */
	@Test
	public void testEditStateCodePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_STATE_CODE_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT, mav.getViewName());
	        
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


	