/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;

public class ViewBookDefinitionControllerTest {
	private static final String TITLE_ID = "a/b/c/d";
	private static final Long BOOK_DEFINITION_ID = new Long(1);
    private ViewBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private BookDefinitionService mockBookDefinitionService;
    private JobRequestService mockJobRequestService;

   
    @Before
    public void setUp() throws Exception {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = new AnnotationMethodHandlerAdapter();
    	mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
    	mockJobRequestService = EasyMock.createMock(JobRequestService.class);
    	controller = new ViewBookDefinitionController();
    	controller.setBookDefinitionService(mockBookDefinitionService);
    	controller.setJobRequestService(mockJobRequestService);
    }
    
    @Test
    public void testBookDefinitionViewGet() throws Exception {
    	// Set up the request URL

    	BookDefinition bookDef = new BookDefinition();
    	bookDef.setFullyQualifiedTitleId(TITLE_ID);
    	bookDef.setEbookDefinitionId(BOOK_DEFINITION_ID);
    	request.setRequestURI("/"+WebConstants.MVC_BOOK_DEFINITION_VIEW_GET);
    	request.setMethod(HttpMethod.GET.name());
    	request.addParameter(WebConstants.KEY_ID, Long.toString(BOOK_DEFINITION_ID));
    	
    	EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(bookDef);
    	EasyMock.replay(mockBookDefinitionService);
    	
    	EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
		EasyMock.replay(mockJobRequestService);

    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	
    	Map<String,Object> model = mav.getModel();
    	Assert.assertNotNull(mav);
    	Assert.assertEquals(bookDef, model.get(WebConstants.KEY_BOOK_DEFINITION));
    	Assert.assertEquals(WebConstants.VIEW_BOOK_DEFINITION_VIEW, mav.getViewName());
    	
    	EasyMock.verify(mockBookDefinitionService);
    	EasyMock.verify(mockJobRequestService);
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
    	request.addParameter("id", BOOK_DEFINITION_ID.toString());
    	
    	// Invoke the controller method via the URL
    	ModelAndView mav = handlerAdapter.handle(request, response, controller);
    	Assert.assertNotNull(mav);
    	View view = mav.getView();
    	Assert.assertTrue(view instanceof RedirectView);
    	RedirectView rView = (RedirectView) view;
    	String queryString = String.format("?%s=%s", WebConstants.KEY_ID, BOOK_DEFINITION_ID);
    	Assert.assertEquals(viewUri+queryString, rView.getUrl());
    }
}