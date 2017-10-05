package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestServiceImpl;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageServiceImpl;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.group.service.GroupServiceImpl;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateEbookController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerServiceImpl;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsServiceImpl;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
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

public final class GenerateEbookControllerTest
{
    private GenerateEbookController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    private BookDefinitionService mockBookDefinitionService;
    private MessageSourceAccessor mockMessageSourceAccessor;
    private ProviewHandler mockProviewHandler;
    private GroupService mockGroupService;
    private JobRequestService mockJobRequestService;
    private PublishingStatsService mockPublishingStatsService;
    private ManagerService mockManagerService;
    private OutageService mockOutageService;
    private MiscConfigSyncService mockMiscConfigService;
    private XppBundleArchiveService mockXppBundleArchiveService;

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up services
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockMessageSourceAccessor = EasyMock.createMock(MessageSourceAccessor.class);
        mockProviewHandler = EasyMock.createMock(ProviewHandler.class);
        mockGroupService = EasyMock.createMock(GroupServiceImpl.class);
        mockJobRequestService = EasyMock.createMock(JobRequestServiceImpl.class);
        mockPublishingStatsService = EasyMock.createMock(PublishingStatsServiceImpl.class);
        mockManagerService = EasyMock.createMock(ManagerServiceImpl.class);
        mockOutageService = EasyMock.createMock(OutageServiceImpl.class);
        mockMiscConfigService = EasyMock.createMock(ManagerMiscConfigSyncService.class);
        mockXppBundleArchiveService = EasyMock.createMock(XppBundleArchiveService.class);

        // Set up the controller
        controller = new GenerateEbookController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "messageSourceAccessor", mockMessageSourceAccessor);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "proviewHandler", mockProviewHandler);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "groupService", mockGroupService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "jobRequestService", mockJobRequestService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "publishingStatsService", mockPublishingStatsService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "managerService", mockManagerService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "outageService", mockOutageService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "xppBundleArchiveService", mockXppBundleArchiveService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "bookDefinitionService", mockBookDefinitionService);
    }

    /**
     * Test the GET of one book selected to generator preview
     *
     * @throws Exception
     */
    @Test
    public void testGenerateEbookPreviewGET() throws Exception
    {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setVersion("v5.3");
        titleInfo.setStatus("test");
        final Long bookDefinitionId = Long.valueOf(127);
        final BookDefinition book = new BookDefinition();
        book.setPublishCutoffDate(new DateTime().toDateMidnight().toDate());
        book.setEbookDefinitionCompleteFlag(true);
        book.setIsSplitBook(false);
        book.setGroupName("groupName");
        final DocumentTypeCode docType = EasyMock.createMock(DocumentTypeCode.class);
        book.setDocumentTypeCodes(docType);

        final GroupDefinition group = new GroupDefinition();

        final MiscConfig miscConfig = new MiscConfig();

        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", bookDefinitionId.toString());
        request.setParameter("newVersion", GenerateBookForm.Version.OVERWRITE.toString());

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.expect(mockProviewHandler.getLatestProviewTitleInfo(null)).andReturn(titleInfo);
        EasyMock.expect(mockGroupService.getLastGroup(book)).andReturn(group);
        EasyMock.expect(mockGroupService.createGroupDefinition(book, "v5", null)).andReturn(group);
        EasyMock.expect(mockGroupService.createGroupDefinition(book, "v6", null)).andReturn(group);
        EasyMock.expect(mockPublishingStatsService.hasIsbnBeenPublished(null, null)).andReturn(false);
        EasyMock.expect(docType.getUsePublishCutoffDateFlag()).andReturn(true);
        EasyMock.replay(docType);
        replayAll();

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);

            Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW, mav.getViewName());

            EasyMock.verify(mockOutageService);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the GET of one book selected to generator preview
     *
     * @throws Exception
     */
    @Test
    public void testGenerateEbookPreviewSplitBookGET() throws Exception
    {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setVersion("v5.3");
        titleInfo.setStatus("test");
        final Long bookDefinitionId = Long.valueOf(127);
        final BookDefinition book = new BookDefinition();
        book.setPublishCutoffDate(new DateTime().toDateMidnight().toDate());
        book.setEbookDefinitionCompleteFlag(true);
        book.setIsSplitBook(true);
        book.setIsSplitTypeAuto(false);
        book.setGroupName("groupName");
        final DocumentTypeCode docType = EasyMock.createMock(DocumentTypeCode.class);
        book.setDocumentTypeCodes(docType);
        final Set<SplitDocument> splitDocs = new HashSet<>();
        final SplitDocument doc = new SplitDocument();
        doc.setBookDefinition(new BookDefinition());
        splitDocs.add(doc);
        splitDocs.add(doc);
        book.setSplitDocuments(splitDocs);

        final GroupDefinition group = new GroupDefinition();

        final MiscConfig miscConfig = new MiscConfig();

        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", bookDefinitionId.toString());
        request.setParameter("newVersion", GenerateBookForm.Version.OVERWRITE.toString());

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.expect(mockMiscConfigService.getMiscConfig()).andReturn(miscConfig);
        EasyMock.expect(mockProviewHandler.getLatestProviewTitleInfo(null)).andReturn(titleInfo);
        EasyMock.expect(mockGroupService.getLastGroup(book)).andReturn(group);
        EasyMock.expect(
            mockGroupService.createGroupDefinition(
                EasyMock.anyObject(BookDefinition.class),
                EasyMock.anyObject(String.class),
                EasyMock.anyObject(List.class)))
            .andReturn(group)
            .times(2);
        EasyMock.expect(mockPublishingStatsService.hasIsbnBeenPublished(null, null)).andReturn(false);
        EasyMock.expect(docType.getUsePublishCutoffDateFlag()).andReturn(true);
        EasyMock.replay(docType);
        replayAll();

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);

            Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW, mav.getViewName());

            EasyMock.verify(mockOutageService);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST of one book selected to generator preview
     */
    @Test
    public void testGenerateEbookPreviewPOST()
    {
        final String message = "";
        final Long bookDefinitionId = Long.valueOf(127);
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(bookDefinitionId);
        book.setIsDeletedFlag(false);
        book.setPublishedOnceFlag(true);
        book.setFullyQualifiedTitleId("");
        book.setProviewDisplayName("");

        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("command", Command.GENERATE.toString());
        request.setParameter("id", bookDefinitionId.toString());
        request.setParameter("isHighPriorityJob", "true");

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.expect(mockManagerService.findRunningJob(book)).andReturn(null);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
        EasyMock.expect(mockJobRequestService.isBookInJobRequest(bookDefinitionId)).andReturn(false);
        EasyMock.expect(mockJobRequestService.saveQueuedJobRequest(book, "", 5, null)).andReturn(null);
        EasyMock.expect(
            mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.anyObject(Object[].class)))
            .andReturn(null);
        EasyMock.expect(mockMessageSourceAccessor.getMessage("label.normal")).andReturn(message);
        EasyMock.expect(mockMessageSourceAccessor.getMessage("mesg.book.deleted")).andReturn(message);
        replayAll();

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);

            Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW, mav.getViewName());
            Assert.assertTrue(mav.getModel().containsKey(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS));

            EasyMock.verify(mockOutageService);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGenerateEbookDisableButton()
    {
        final String message = "";
        final Long bookDefinitionId = Long.valueOf(127);
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(bookDefinitionId);
        book.setIsDeletedFlag(false);
        book.setPublishedOnceFlag(true);
        book.setFullyQualifiedTitleId("");
        book.setProviewDisplayName("");
        book.setIsDeletedFlag(true);

        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("command", Command.GENERATE.toString());
        request.setParameter("id", bookDefinitionId.toString());
        request.setParameter("isHighPriorityJob", "true");

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.expect(mockManagerService.findRunningJob(book)).andReturn(null);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
        EasyMock.expect(mockJobRequestService.isBookInJobRequest(bookDefinitionId)).andReturn(false);
        EasyMock.expect(mockJobRequestService.saveQueuedJobRequest(book, "", 5, null)).andReturn(null);
        EasyMock.expect(
            mockMessageSourceAccessor.getMessage(EasyMock.anyObject(String.class), EasyMock.anyObject(Object[].class)))
            .andReturn(null);
        EasyMock.expect(mockMessageSourceAccessor.getMessage("label.normal")).andReturn(message);
        EasyMock.expect(mockMessageSourceAccessor.getMessage("mesg.book.deleted")).andReturn(message);
        replayAll();

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);

            Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_PREVIEW, mav.getViewName());

            Assert.assertFalse(mav.getModel().containsKey(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS));

            EasyMock.verify(mockOutageService);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldShowErrorMessageWhenPrintComponentsEmpty() throws Exception
    {
        //given
        final String message = "mesg.empty.printcomponents";
        final BookDefinition book = givenBook();
        book.setSourceType(SourceType.XPP);

        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("command", Command.GENERATE.toString());
        request.setParameter("id", book.getEbookDefinitionId().toString());
        request.setParameter("isHighPriorityJob", "true");

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.expect(mockManagerService.findRunningJob(book)).andReturn(null);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(book.getEbookDefinitionId())).andReturn(book);
        EasyMock.expect(mockJobRequestService.isBookInJobRequest(book.getEbookDefinitionId())).andReturn(false);
        EasyMock.expect(mockJobRequestService.saveQueuedJobRequest(book, "", 5, null)).andReturn(null);
        EasyMock.expect(mockMessageSourceAccessor.getMessage("label.normal")).andReturn("");
        EasyMock.expect(mockMessageSourceAccessor.getMessage(message)).andReturn(message);
        replayAll();
        //when
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        //then
        assertNotNull(mav);
        Assert.assertEquals(mav.getModel().get(WebConstants.KEY_ERR_MESSAGE), message);
    }

    @Test
    public void shouldShowErrorMessageWhenBundlesMissed() throws Exception
    {
        //given
        final String message = "mesg.missing.bundle";
        final BookDefinition book = givenBook();
        book.setSourceType(SourceType.XPP);
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setMaterialNumber("123");
        book.setPrintComponents(asList(printComponent));

        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("command", Command.GENERATE.toString());
        request.setParameter("id", book.getEbookDefinitionId().toString());
        request.setParameter("isHighPriorityJob", "true");

        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(new ArrayList<PlannedOutage>());
        EasyMock.expect(mockManagerService.findRunningJob(book)).andReturn(null);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(book.getEbookDefinitionId())).andReturn(book);
        EasyMock.expect(mockJobRequestService.isBookInJobRequest(book.getEbookDefinitionId())).andReturn(false);
        EasyMock.expect(mockJobRequestService.saveQueuedJobRequest(book, "", 5, null)).andReturn(null);
        EasyMock.expect(mockMessageSourceAccessor.getMessage("label.normal")).andReturn("");
        EasyMock.expect(mockXppBundleArchiveService.findByMaterialNumber("123")).andReturn(null);
        EasyMock.expect(mockMessageSourceAccessor.getMessage(message)).andReturn(message);
        replayAll();
        //when
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        //then
        assertNotNull(mav);
        Assert.assertEquals(mav.getModel().get(WebConstants.KEY_ERR_MESSAGE), message);
    }

    /**
     * Test the Get of no book selected to generator preview
     */
    @Test
    public void testGenerateEbookPreviewNoBooks()
    {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.GET.name());

        try
        {
            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            assertNotNull(mav);
        }
        catch (final Exception e)
        {
            assertEquals(e.getClass(), MissingServletRequestParameterException.class);
        }
    }

    /**
     * Test the POST of multiple books selected to generator preview
     */
    @Test
    public void testGenerateBulkEbookPreview()
    {
        final Long bookDefinitionId = Long.valueOf(127);
        final BookDefinition book = new BookDefinition();
        book.setFullyQualifiedTitleId("");
        book.setProviewDisplayName("");
        book.setIsDeletedFlag(false);

        request.setRequestURI("/" + WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW);
        request.setMethod(HttpMethod.GET.name());
        final String[] keys = {bookDefinitionId.toString()};
        request.setParameter("id", keys);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);

            Assert.assertEquals(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW, mav.getViewName());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void replayAll()
    {
        EasyMock.replay(mockBookDefinitionService);
        EasyMock.replay(mockMessageSourceAccessor);
        EasyMock.replay(mockProviewHandler);
        EasyMock.replay(mockGroupService);
        EasyMock.replay(mockJobRequestService);
        EasyMock.replay(mockPublishingStatsService);
        EasyMock.replay(mockManagerService);
        EasyMock.replay(mockOutageService);
        EasyMock.replay(mockMiscConfigService);
        EasyMock.replay(mockXppBundleArchiveService);
    }

    private BookDefinition givenBook()
    {
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(127L);
        book.setIsDeletedFlag(false);
        book.setPublishedOnceFlag(true);
        book.setFullyQualifiedTitleId("");
        book.setProviewDisplayName("");
        return book;
    }
}
