/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

public class ViewBookDefinitionControllerTest {
	private static final String TITLE_ID = "a/b/c/d";
    private ViewBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CoreService mockCoreService;

   
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	mockCoreService = EasyMock.createMock(CoreService.class);
    	controller = new ViewBookDefinitionController();
    	controller.setCoreService(mockCoreService);
    }
    
    @Test
    public void testBookDefinitionViewGet() throws Exception {
    	// Set up the request URL

    	BookDefinition bookDef = new BookDefinition();
    	bookDef.setTitleId(TITLE_ID);
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_DEFINITION_VIEW_GET);
    	request.setMethod(HttpMethod.GET.name());
    	request.addParameter(WebConstants.KEY_TITLE_ID, TITLE_ID);
    	
    	EasyMock.expect(mockCoreService.findBookDefinitionByTitle(TITLE_ID)).andReturn(bookDef);
    	EasyMock.replay(mockCoreService);

    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	Map<String,Object> model = mav.getModel();
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(TITLE_ID, model.get(WebConstants.KEY_TITLE_ID));
    	Assert.assertEquals(bookDef, model.get(WebConstants.KEY_BOOK_DEFINITION));
    	Assert.assertEquals(WebConstants.VIEW_BOOK_DEFINITION_VIEW, mav.getViewName());
    	
    	EasyMock.verify(mockCoreService);
    }
    
    @Test
    public void testEditPost() throws Exception {
    	testBookDefinitionViewPostCommand(Command.EDIT,
    									  WebConstants.MVC_BOOK_DEFINITION_EDIT);
    }
    @Test
    public void testGeneratePost() throws Exception {
    	testBookDefinitionViewPostCommand(Command.GENERATE,
    									  WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
    }
    
    
    private void testBookDefinitionViewPostCommand(Command command, String viewUri) throws Exception {
    	// Set up the request URL
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_DEFINITION_VIEW_POST);
    	request.setMethod(HttpMethod.POST.name());
    	request.addParameter("command", command.name());
    	request.addParameter(WebConstants.KEY_TITLE_ID, TITLE_ID);
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	View view = mav.getView();
    	Assert.assertTrue(view instanceof RedirectView);
    	RedirectView rView = (RedirectView) view;
    	String queryString = String.format("?%s=%s", WebConstants.KEY_TITLE_ID, TITLE_ID);
    	Assert.assertEquals(viewUri+queryString, rView.getUrl());
    }
}
