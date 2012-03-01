/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateEbookController;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

public class GenerateEbookControllerTest {
	private GenerateEbookController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerAdapter handlerAdapter;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		handlerAdapter = new AnnotationMethodHandlerAdapter();

		// Mock up services
		CoreService mockCoreService = EasyMock.createMock(CoreService.class);
		ProviewClient proviewClient = EasyMock.createMock(ProviewClient.class);

		// Set up the controller
		this.controller = new GenerateEbookController();
		controller.setCoreService(mockCoreService);
		controller.setProviewClient(proviewClient);
	}

	/**
	 * Test the Get of no book selected to generator preview
	 */
	@Test
	public void testGenerateEbookPreviewNoBooks() {
		request.setRequestURI("/"
				+ WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.GET.name());

		try {
			ModelAndView mav = handlerAdapter.handle(request, response,
					controller);
			assertNotNull(mav);

		} catch (Exception e) {
			assertEquals(e.getClass(),
					MissingServletRequestParameterException.class);
		}

	}

	/**
	 * Test the POST of one book selected to generator preview
	 */
	@Test
	public void testGenerateEbookPreview() {
		request.setRequestURI("/"
				+ WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("titleId", "uscl/imagedoc3");

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);

			Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW,
					mav.getViewName());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Test the POST of multiple books selected to generator preview
	 */
	@Test
	public void testGenerateBulkEbookPreview() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.GET.name());
		String[] keys = { "uscl/imagedoc3", "uscl/imagedoc4" };
		request.setParameter("titleId", keys);

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);

			Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW,
					mav.getViewName());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
