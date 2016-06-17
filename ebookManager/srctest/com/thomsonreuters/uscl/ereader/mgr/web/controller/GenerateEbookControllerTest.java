/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestServiceImpl;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageServiceImpl;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.group.service.GroupServiceImpl;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateEbookController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerServiceImpl;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsServiceImpl;

public class GenerateEbookControllerTest {
	private GenerateEbookController controller;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerAdapter handlerAdapter;
	
	private BookDefinitionService mockBookDefinitionService;
	private MessageSourceAccessor mockMessageSourceAccessor;
	private ProviewClient mockProviewClient;
	private GroupService mockGroupService;
	private JobRequestService mockJobRequestService;
	private PublishingStatsService mockPublishingStatsService;
	private ManagerService mockManagerService;
	private OutageService mockOutageService;
	private MiscConfigSyncService mockMiscConfigService;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		handlerAdapter = new AnnotationMethodHandlerAdapter();

		// Mock up services
		mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
		mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
		mockProviewClient = EasyMock.createMock(ProviewClient.class);
		mockGroupService = EasyMock.createMock(GroupServiceImpl.class);
		mockJobRequestService = EasyMock.createMock(JobRequestServiceImpl.class);
		mockPublishingStatsService = EasyMock.createMock(PublishingStatsServiceImpl.class);
		mockManagerService = EasyMock.createMock(ManagerServiceImpl.class);
		mockOutageService = EasyMock.createMock(OutageServiceImpl.class);
		mockMiscConfigService = EasyMock.createMock(ManagerMiscConfigSyncService.class);

		// Set up the controller
		this.controller = new GenerateEbookController();
		controller.setEnvironmentName("");
		controller.setBookDefinitionService(mockBookDefinitionService);
		controller.setMessageSourceAccessor(mockMessageSourceAccessor);
		controller.setProviewClient(mockProviewClient);
		controller.setGroupService(mockGroupService);
		controller.setJobRequestService(mockJobRequestService);
		controller.setPublishingStatsService(mockPublishingStatsService);
		controller.setManagerService(mockManagerService);
		controller.setOutageService(mockOutageService);
	}

	/**
	 * Test the GET of one book selected to generator preview
	 */
	@Test
	public void testGenerateEbookPreviewGET() {
		Long bookDefinitionId = new Long(127);
		BookDefinition book = null;/*new BookDefinition();
		book.setPublishCutoffDate(new DateTime().toDateMidnight().toDate());
		book.setEbookDefinitionCompleteFlag(true);
		book.setIsSplitBook(false);
		DocumentTypeCode docType = EasyMock.createMock(DocumentTypeCode.class);
		book.setDocumentTypeCodes(docType);
		
		MiscConfig miscConfig = new MiscConfig(); // having trouble working with this class
		miscConfig.setDisableExistingSingleTitleSplit(true);
	*/	
		request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.GET.name());
		request.setParameter("id", bookDefinitionId.toString());

		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
		EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
	/*	EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(null);
		EasyMock.expect(miscConfig.getDisableExistingSingleTitleSplit()).andReturn(true);
		EasyMock.expect(docType.getUsePublishCutoffDateFlag()).andReturn(true);
		EasyMock.replay(docType);
	*/	replayAll();
	
		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);

			Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW, mav.getViewName());

			EasyMock.verify(mockOutageService);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Test the POST of one book selected to generator preview
	 */
	@Test
	public void testGenerateEbookPreviewPOST() {
	//	String version = "";
		String message = "";
		Long bookDefinitionId = new Long(127);
		BookDefinition book = new BookDefinition();
		book.setEbookDefinitionId(bookDefinitionId);
		book.setIsDeletedFlag(true);
		book.setFullyQualifiedTitleId("");
		book.setProviewDisplayName("");
	//	Object[] args = { book.getFullyQualifiedTitleId(), message };
		
		request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("command", Command.GENERATE.toString());
		request.setParameter("id", bookDefinitionId.toString());
		request.setParameter("isHighPriorityJob", "true");

		EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
		EasyMock.expect(mockJobRequestService.isBookInJobRequest(bookDefinitionId)).andReturn(false);
	//	EasyMock.expect(mockJobRequestService.saveQueuedJobRequest(book, version, 5, null)).andReturn((long) 127);
	//	EasyMock.expect(mockManagerService.findRunningJob(bookDefinitionId)).andReturn(null);
		EasyMock.expect(mockMessageSourceAccessor.getMessage("label.normal")).andReturn(message);
		EasyMock.expect(mockMessageSourceAccessor.getMessage("mesg.book.deleted")).andReturn(message);
		replayAll();
		
		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);

			Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW, mav.getViewName());

			EasyMock.verify(mockOutageService);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * Test the Get of no book selected to generator preview
	 */
	@Test
	public void testGenerateEbookPreviewNoBooks() {
		request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.GET.name());

		try {
			ModelAndView mav = handlerAdapter.handle(request, response, controller);
			assertNotNull(mav);

		} catch (Exception e) {
			assertEquals(e.getClass(), MissingServletRequestParameterException.class);
		}

	}

	/**
	 * Test the POST of multiple books selected to generator preview
	 */
	@Test
	public void testGenerateBulkEbookPreview() {
		Long bookDefinitionId = new Long(127);
		BookDefinition book = new BookDefinition();
		book.setFullyQualifiedTitleId("");
		book.setProviewDisplayName("");
		book.setIsDeletedFlag(false);
		
		request.setRequestURI("/" + WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW);
		request.setMethod(HttpMethod.GET.name());
		String[] keys = { bookDefinitionId.toString() };
		request.setParameter("id", keys);
		
		EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);

		ModelAndView mav;
		try {
			mav = handlerAdapter.handle(request, response, controller);

			assertNotNull(mav);

			Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW, mav.getViewName());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private void replayAll() {
		EasyMock.replay(mockBookDefinitionService);
		EasyMock.replay(mockMessageSourceAccessor);
		EasyMock.replay(mockProviewClient);
		EasyMock.replay(mockGroupService);
		EasyMock.replay(mockJobRequestService);
		EasyMock.replay(mockPublishingStatsService);
		EasyMock.replay(mockManagerService);
		EasyMock.replay(mockOutageService);
		EasyMock.replay(mockMiscConfigService);
	}
}
