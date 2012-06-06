/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class JurisdictionCodeControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+JurisdictionCodeForm.FORM_NAME;
	private static final JurisTypeCode JURIS_CODE = new JurisTypeCode();
	private static final Long JURIS_ID = 1L;
    private JurisdictionCodeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CodeService mockCodeService;
    private JurisdictionCodeFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Code service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Set up the controller
    	this.controller = new JurisdictionCodeController();
    	controller.setCodeService(mockCodeService);
    	
    	validator = new JurisdictionCodeFormValidator();
    	validator.setCodeService(mockCodeService);
    	controller.setValidator(validator);	
    	
    	JURIS_CODE.setId(JURIS_ID);
    	JURIS_CODE.setName("test");
	}

	/**
     * Test the GET to the List page
     */
	@SuppressWarnings("unchecked")
	@Test
	public void testViewJurisCodeList() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_VIEW);
    	request.setMethod(HttpMethod.GET.name());
    	
    	EasyMock.expect(mockCodeService.getAllJurisTypeCodes()).andReturn(new ArrayList<JurisTypeCode>());
    	EasyMock.replay(mockCodeService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_JURIS_CODE_VIEW, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        List<JurisTypeCode> codes = (List<JurisTypeCode>) model.get(WebConstants.KEY_JURIS_TYPE_CODE);
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
	public void testCreateJurisCodeGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_CREATE);
    	request.setMethod(HttpMethod.GET.name());
    	
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_JURIS_CODE_CREATE, mav.getViewName());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
     * Test the POST to the Create Page Success
     */
	@Test
	public void testCreateJurisCodePost() {
		String name = "test";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("name", name);
    	
    	JurisTypeCode code = new JurisTypeCode();
    	code.setName(name);
    	
    	EasyMock.expect(mockCodeService.getJurisTypeCodeByName(name)).andReturn(null);
    	mockCodeService.saveJurisTypeCode(code);
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
	public void testCreateJurisCodePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_JURIS_CODE_CREATE, mav.getViewName());
	        
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
	public void testEditJurisCodeGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_EDIT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", JURIS_ID.toString());
    	
    	EasyMock.expect(mockCodeService.getJurisTypeCodeById(JURIS_ID)).andReturn(JURIS_CODE);
    	EasyMock.replay(mockCodeService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_JURIS_CODE_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        JurisTypeCode actual = (JurisTypeCode) model.get(WebConstants.KEY_JURIS_TYPE_CODE);
	        
	        Assert.assertEquals(JURIS_CODE, actual);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	/**
     * Test the POST to the Edit Page Success
     */
	@Test
	public void testEditJurisCodePost() {
		String name = JURIS_CODE.getName();
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("jurisId", JURIS_ID.toString());
    	request.setParameter("name", name);
    	
    	EasyMock.expect(mockCodeService.getJurisTypeCodeByName(name)).andReturn(null);
    	mockCodeService.saveJurisTypeCode(JURIS_CODE);
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
	public void testEditJurisCodePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_JURIS_CODE_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_JURIS_CODE_EDIT, mav.getViewName());
	        
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


	