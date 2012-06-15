/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

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

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import com.thomsonreuters.uscl.ereader.support.service.SupportPageLinkService;

public class SupportControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+SupportForm.FORM_NAME;
	private static final SupportPageLink SUPPORT_PAGE_LINK = new SupportPageLink();
	private static final Long SUPPORT_PAGE_LINK_ID = 1L;
    private SupportController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private SupportPageLinkService mockService;
    private SupportFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Code service
    	this.mockService = EasyMock.createMock(SupportPageLinkService.class);
    	
    	// Set up the controller
    	this.controller = new SupportController();
    	controller.setSupportPageLinkService(mockService);
    	
    	validator = new SupportFormValidator();
    	controller.setValidator(validator);	
    	
    	SUPPORT_PAGE_LINK.setId(SUPPORT_PAGE_LINK_ID);
	}

	/**
     * Test the GET to the List page
     */
	@SuppressWarnings("unchecked")
	@Test
	public void testViewAdminSupportLinkList() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_VIEW);
    	request.setMethod(HttpMethod.GET.name());
    	
    	EasyMock.expect(mockService.findAllSupportPageLink()).andReturn(new ArrayList<SupportPageLink>());
    	EasyMock.replay(mockService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_VIEW, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        List<SupportPageLink> supportPageLink = (List<SupportPageLink>) model.get(WebConstants.KEY_SUPPORT);
	        assertEquals(0, supportPageLink.size());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockService);
	}
	
	/**
     * Test the GET to the List page
     */
	@SuppressWarnings("unchecked")
	@Test
	public void testViewSupportLinkList() {
		request.setRequestURI("/"+ WebConstants.MVC_SUPPORT_PAGE_VIEW);
    	request.setMethod(HttpMethod.GET.name());
    	
    	EasyMock.expect(mockService.findAllSupportPageLink()).andReturn(new ArrayList<SupportPageLink>());
    	EasyMock.replay(mockService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_SUPPORT_PAGE_VIEW, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        List<SupportPageLink> supportPageLink = (List<SupportPageLink>) model.get(WebConstants.KEY_SUPPORT);
	        assertEquals(0, supportPageLink.size());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockService);
	}
	
	
	/**
     * Test the GET to the Create Page
     */
	@Test
	public void testCreateSupportPageLinkGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_CREATE);
    	request.setMethod(HttpMethod.GET.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_CREATE, mav.getViewName());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	/**
     * Test the POST to the Create Page Success
     */
	@Test
	public void testCreateSupportPageLinkPost() {
		String description = "test";
		String url = "http://www.google.com";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("linkDescription", description);
    	request.setParameter("linkAddress", url);
    	
    	SupportPageLink spl = new SupportPageLink();
    	spl.setLinkAddress(url);
    	spl.setLinkDescription(description);
    	
    	mockService.save(spl);
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
     * Test the POST to the Create Page Fail
     */
	@Test
	public void testCreateSupportPageLinkPostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_CREATE);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_CREATE, mav.getViewName());
	        
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
	public void testEditSupportPageLinkGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_EDIT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", SUPPORT_PAGE_LINK_ID.toString());
    	
    	EasyMock.expect(mockService.findByPrimaryKey(SUPPORT_PAGE_LINK_ID)).andReturn(SUPPORT_PAGE_LINK);
    	EasyMock.replay(mockService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        SupportPageLink actual = (SupportPageLink) model.get(WebConstants.KEY_SUPPORT);
	        
	        Assert.assertEquals(SUPPORT_PAGE_LINK, actual);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	/**
     * Test the POST to the Edit Page Success
     */
	@Test
	public void testEditSupportPageLinkPost() {
		String description = "description";
		String address = "http://www.google.com";
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("supportPageLinkId", SUPPORT_PAGE_LINK_ID.toString());
    	request.setParameter("linkAddress", address);
    	request.setParameter("linkDescription", description);
    	
    	SUPPORT_PAGE_LINK.setLinkAddress(address);
    	SUPPORT_PAGE_LINK.setLinkDescription(description);

    	mockService.save(SUPPORT_PAGE_LINK);
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
     * Test the POST to the Edit Page Fail
     */
	@Test
	public void testEditSupportPageLinkPostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_SUPPORT_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_SUPPORT_EDIT, mav.getViewName());
	        
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


	