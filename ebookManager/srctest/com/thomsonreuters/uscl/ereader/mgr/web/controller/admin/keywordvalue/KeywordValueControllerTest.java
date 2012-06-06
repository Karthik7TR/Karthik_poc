/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class KeywordValueControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+KeywordValueForm.FORM_NAME;
	private static final KeywordTypeValue KEYWORD_VALUE = new KeywordTypeValue();
	private static final Long KEYWORD_VALUE_ID = 1L;
	private static final KeywordTypeCode KEYWORD_CODE = new KeywordTypeCode();
	private static final Long KEYWORD_CODE_ID = 1L;
    private KeywordValueController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CodeService mockCodeService;
    private KeywordValueFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Value service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Set up the controller
    	this.controller = new KeywordValueController();
    	controller.setCodeService(mockCodeService);
    	
    	validator = new KeywordValueFormValidator();
    	validator.setCodeService(mockCodeService);
    	controller.setValidator(validator);	
    	
    	KEYWORD_VALUE.setId(KEYWORD_VALUE_ID);
    	KEYWORD_VALUE.setName("test");
    	
    	KEYWORD_CODE.setId(KEYWORD_CODE_ID);
    	KEYWORD_CODE.setName("Code name");
	}
	
	
	/**
     * Test the GET to the Create Page
     */
	@Test
	public void testCreateKeywordValueGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("keywordCodeId", KEYWORD_CODE_ID.toString());
    	
    	EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE);
    	EasyMock.replay(mockCodeService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_CREATE, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        
	        KeywordTypeCode actual = (KeywordTypeCode) model.get(WebConstants.KEY_KEYWORD_TYPE_CODE);
	        
	        Assert.assertEquals(KEYWORD_CODE, actual);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCodeService);
	}
	
	/**
     * Test the POST to the Create Page Success
     */
	@Test
	public void testCreateKeywordValuePost() {
		String name = "test";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("name", name);
    	request.setParameter("keywordTypeCode.id", KEYWORD_CODE_ID.toString());
    	request.setParameter("keywordTypeCode.name", KEYWORD_CODE.getName());
    	
    	KeywordTypeValue code = new KeywordTypeValue();
    	code.setName(name);
    	code.setKeywordTypeCode(KEYWORD_CODE);
    	
    	EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE);
    	mockCodeService.saveKeywordTypeValue(code);
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
	public void testCreateKeywordValuePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("keywordTypeCode.id", KEYWORD_CODE_ID.toString());
    	request.setParameter("keywordTypeCode.name", KEYWORD_CODE.getName());
    	
    	KeywordTypeValue code = new KeywordTypeValue();
    	code.setName(KEYWORD_VALUE.getName());
    	code.setKeywordTypeCode(KEYWORD_CODE);
    	
    	EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE).times(2);
    	mockCodeService.saveKeywordTypeValue(code);
    	EasyMock.replay(mockCodeService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_CREATE, mav.getViewName());
	        
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
	public void testEditKeywordValueGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", KEYWORD_VALUE_ID.toString());
    	
    	EasyMock.expect(mockCodeService.getKeywordTypeValueById(KEYWORD_VALUE_ID)).andReturn(KEYWORD_VALUE);
    	EasyMock.replay(mockCodeService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        KeywordTypeValue actual = (KeywordTypeValue) model.get(WebConstants.KEY_KEYWORD_TYPE_VALUE);
	        
	        Assert.assertEquals(KEYWORD_VALUE, actual);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	/**
     * Test the POST to the Edit Page Success
     */
	@Test
	public void testEditKeywordValuePost() {
		String name = KEYWORD_VALUE.getName();
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("typeId", KEYWORD_VALUE_ID.toString());
    	request.setParameter("name", name);
    	request.setParameter("keywordTypeCode.id", KEYWORD_CODE_ID.toString());
    	request.setParameter("keywordTypeCode.name", KEYWORD_CODE.getName());
    	
    	EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE);
    	mockCodeService.saveKeywordTypeValue(KEYWORD_VALUE);
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
	public void testEditKeywordValuePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("typeId", KEYWORD_VALUE_ID.toString());
    	request.setParameter("keywordTypeCode.id", KEYWORD_CODE_ID.toString());
    	request.setParameter("keywordTypeCode.name", KEYWORD_CODE.getName());
    	
    	EasyMock.expect(mockCodeService.getKeywordTypeCodeById(KEYWORD_CODE_ID)).andReturn(KEYWORD_CODE);
    	EasyMock.expect(mockCodeService.getKeywordTypeValueById(KEYWORD_VALUE_ID)).andReturn(KEYWORD_VALUE);
    	EasyMock.replay(mockCodeService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT, mav.getViewName());
	        
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
		
		EasyMock.verify(mockCodeService);
	}
	
}


	