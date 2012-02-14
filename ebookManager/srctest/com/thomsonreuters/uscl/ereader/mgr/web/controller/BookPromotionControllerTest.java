/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.promotion.BookPromotionController;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class BookPromotionControllerTest {
    private BookPromotionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
	}

	
	/**
     * Test the Get of no book selected to promote
     */
	@Test
	public void testPromoteNoBooks() {
		request.setRequestURI("/"+WebConstants.MVC_BOOK_DEFINITION_PROMOTION);
    	request.setMethod(HttpMethod.GET.name());

		try {
			ModelAndView mav = handlerAdapter.handle(request, response, controller);
			assertNotNull(mav);
		} catch (Exception e) {
			assertTrue(e.getClass() == MissingServletRequestParameterException.class);
		}
    	
	}
	
	/**
     * Test the GET of one book selected to promote
     */
	@Test
	public void testpromoteOneEbook() {
		request.setRequestURI("/"+WebConstants.MVC_BOOK_DEFINITION_PROMOTION);
    	request.setMethod(HttpMethod.GET.name());
    	request.setParameter("titleId", "uscl/imagedoc3");

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	    	
	    	Assert.assertEquals(WebConstants.VIEW_BOOK_DEFINITION_PROMOTION, mav.getViewName());
	    	
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
    	
	}

	/**
     * Test the GET of multiple books selected to promote
     */
	@Test
	public void testpromoteBulkEbook() {
		request.setRequestURI("/"+WebConstants.MVC_BOOK_DEFINITION_BULK_PROMOTION);
    	request.setMethod(HttpMethod.GET.name());
    	String[] keys = {"uscl/imagedoc3", "uscl/imagedoc4"};
    	request.setParameter("titleId", keys);

    	ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);
			
			assertNotNull(mav);
	    	
	    	Assert.assertEquals(WebConstants.VIEW_BOOK_DEFINITION_BULK_PROMOTION, mav.getViewName());
	    	
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
