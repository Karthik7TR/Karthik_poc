package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class DocTypeMetricControllerTest {
	private static final String BINDING_RESULT_KEY = BindingResult.class.getName()+"."+DocTypeMetricForm.FORM_NAME;
	private static final DocumentTypeCode DOCTYPE_CODE = new DocumentTypeCode();
	private static final Long DOCTYPE_CODE_ID = 1L;
    private DocTypeMetricController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CodeService mockCodeService;
    private DocTypeMetricFormValidator validator;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	
    	// Mock up the Code service
    	this.mockCodeService = EasyMock.createMock(CodeService.class);
    	
    	// Set up the controller
    	this.controller = new DocTypeMetricController();
    	controller.setCodeService(mockCodeService);
    	
    	validator = new DocTypeMetricFormValidator();
    	controller.setValidator(validator);	
    	
    	DOCTYPE_CODE.setId(DOCTYPE_CODE_ID);
    	DOCTYPE_CODE.setName("test");
    	DOCTYPE_CODE.setThresholdValue(new Integer(7500));
    	DOCTYPE_CODE.setThresholdPercent(new Integer(10));
    	
	}

	/**
     * Test the GET to the List page
     */
	@SuppressWarnings("unchecked")
	@Test
	public void testViewDocTypeList() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW);
    	request.setMethod(HttpMethod.GET.name());
    	
    	EasyMock.expect(mockCodeService.getAllDocumentTypeCodes()).andReturn(new ArrayList<DocumentTypeCode>());
    	EasyMock.replay(mockCodeService);
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_VIEW, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        List<DocumentTypeCode> codes = (List<DocumentTypeCode>) model.get(WebConstants.KEY_DOC_TYPE_CODE);
	        assertEquals(0, codes.size());
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockCodeService);
	}
	

	
	
	/**
     * Test the GET to the Edit Page
     */
	@Test
	public void testEditDocTypeGet() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("id", DOCTYPE_CODE_ID.toString());
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(DOCTYPE_CODE_ID)).andReturn(DOCTYPE_CODE);
    	EasyMock.replay(mockCodeService);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT, mav.getViewName());
	        
	        // Check the state of the model
	        Map<String,Object> model = mav.getModel();
	        DocumentTypeCode actual = (DocumentTypeCode) model.get(WebConstants.KEY_DOC_TYPE_CODE);
	        
	        Assert.assertEquals(DOCTYPE_CODE, actual);
	        
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	/**
     * Test the POST to the Edit Page Success
     */
	@Test
	public void testEditDocTypeCodePost() {
		String name = DOCTYPE_CODE.getName();
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	request.setParameter("id", DOCTYPE_CODE_ID.toString());
    	request.setParameter("name", name);
    	request.setParameter("thresholdValue", "10");
    	request.setParameter("thresholdPercent", "10");
    	
    	EasyMock.expect(mockCodeService.getDocumentTypeCodeById(DOCTYPE_CODE_ID)).andReturn(DOCTYPE_CODE);
    	mockCodeService.saveDocumentTypeMetric(DOCTYPE_CODE);
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
	public void testEditKeywordCodePostFail() {
		request.setRequestURI("/"+ WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT);
    	request.setMethod(HttpMethod.POST.name());
    	
    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	        // Verify the returned view name
	        assertEquals(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT, mav.getViewName());
	        
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
