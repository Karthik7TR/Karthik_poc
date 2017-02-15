package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
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

public final class EditGroupControllerTest
{
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + EditGroupDefinitionForm.FORM_NAME;
    private static final Long BOOK_DEFINITION_ID = 1L;
    private EditGroupController controller;
    private EditGroupDefinitionFormValidator validator;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    private EBookAuditService mockAuditService;
    private BookDefinitionService mockBookDefinitionService;
    private GroupService mockGroupService;

    private DocumentTypeCode documentTypeCode;
    private PublisherCode publisherCode;

    @Before
    public void setUp() throws Exception
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the services
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockAuditService = EasyMock.createMock(EBookAuditService.class);
        mockGroupService = EasyMock.createMock(GroupService.class);

        // Set up the controller
        controller = new EditGroupController();
        controller.setAuditService(mockAuditService);
        controller.setBookDefinitionService(mockBookDefinitionService);
        controller.setGroupService(mockGroupService);

        validator = new EditGroupDefinitionFormValidator();
        controller.setValidator(validator);

        documentTypeCode = new DocumentTypeCode();
        documentTypeCode.setId(Long.parseLong("1"));
        documentTypeCode.setAbbreviation(WebConstants.DOCUMENT_TYPE_ANALYTICAL_ABBR);
        documentTypeCode.setName(WebConstants.DOCUMENT_TYPE_ANALYTICAL);

        publisherCode = new PublisherCode();
        publisherCode.setId(1L);
        publisherCode.setName("uscl");
    }

    /**
     * Test the GET to the Edit Group Definition page
     * @throws Exception
     */
    @Test
    public void testEditGroupDefinitionGet() throws Exception
    {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        final String groupId = "uscl/an_abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final Map<String, ProviewTitleInfo> proviewTitleMap = createProviewTitleMap(fullyQualifiedTitleId);
        EasyMock.expect(mockGroupService.getGroupId(book)).andReturn(groupId);
        EasyMock.expect(mockGroupService.getLastGroup(book)).andReturn(null);
        EasyMock.expect(mockGroupService.getProViewTitlesForGroup(book)).andReturn(proviewTitleMap);
        EasyMock.expect(mockGroupService.getPilotBooksForGroup(book))
            .andReturn(new LinkedHashMap<String, ProviewTitleInfo>());
        EasyMock.expect(mockGroupService.getPilotBooksNotFound()).andReturn(new ArrayList<String>());

        EasyMock.replay(mockGroupService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_GROUP_DEFINITION_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            Assert.assertNull(model.get(WebConstants.KEY_ERR_MESSAGE));
            final BookDefinition actualBook = (BookDefinition) model.get(WebConstants.KEY_BOOK_DEFINITION);
            Assert.assertEquals(book, actualBook);
            final int actualProviewTitles = Integer.valueOf(model.get(WebConstants.KEY_ALL_PROVIEW_TITLES).toString());
            Assert.assertEquals(proviewTitleMap.size(), actualProviewTitles);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockGroupService);
    }

    /**
     * Test the GET to the Edit Group Definition page
     * @throws Exception
     */
    @Test
    public void testEditGroupDefinitionGetDeletedBook() throws Exception
    {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        book.setIsDeletedFlag(true);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test the POST to the Edit Group Definition page
     * @throws Exception
     */
    @Test
    public void testEditGroupDefintionPost() throws Exception
    {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        final String groupId = "uscl/an_abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("bookDefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("groupId", groupId);
        request.setParameter("groupType", "standard");
        request.setParameter("versionType", EditGroupDefinitionForm.Version.OVERWRITE.toString());
        request.setParameter("hasSplitTitles", "false");
        request.setParameter("includeSubgroup", "false");
        request.setParameter("groupName", "Group Name");
        request.setParameter("includePilotBook", "true");

        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
            .andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final Map<String, ProviewTitleInfo> proviewTitleMap = createProviewTitleMap(fullyQualifiedTitleId);
        EasyMock.expect(mockGroupService.getProViewTitlesForGroup(book)).andReturn(proviewTitleMap);
        EasyMock.expect(mockGroupService.getLastGroup(groupId)).andReturn(null);
        EasyMock.expect(mockGroupService.getPilotBooksForGroup(book))
            .andReturn(new LinkedHashMap<String, ProviewTitleInfo>());
        mockGroupService.createGroup(EasyMock.anyObject(GroupDefinition.class));
        EasyMock.replay(mockGroupService);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertFalse(bindingResult.hasErrors());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockGroupService);
        EasyMock.verify(mockBookDefinitionService);
    }

    /**
     * Test the POST to the Edit Group Definition page
     * @throws Exception
     */
    @Test
    public void testEditGroupDefintionPostError() throws Exception
    {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        final String groupId = "uscl/an_abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("bookDefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("groupId", groupId);
        request.setParameter("groupType", "standard");
        request.setParameter("versionType", EditGroupDefinitionForm.Version.OVERWRITE.toString());
        request.setParameter("hasSplitTitles", "false");
        request.setParameter("includeSubgroup", "false");
        request.setParameter("includePilotBook", "true");

        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final Map<String, ProviewTitleInfo> proviewTitleMap = createProviewTitleMap(fullyQualifiedTitleId);
        EasyMock.expect(mockGroupService.getProViewTitlesForGroup(book)).andReturn(proviewTitleMap);
        EasyMock.expect(mockGroupService.getPilotBooksForGroup(book))
            .andReturn(new LinkedHashMap<String, ProviewTitleInfo>());
        EasyMock.expect(mockGroupService.getLastGroup(book)).andReturn(null);
        EasyMock.replay(mockGroupService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_GROUP_DEFINITION_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertTrue(bindingResult.hasErrors());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockGroupService);
        EasyMock.verify(mockBookDefinitionService);
    }

    private Map<String, ProviewTitleInfo> createProviewTitleMap(final String fullyQualifiedTitleId)
    {
        final ProviewTitleInfo info = new ProviewTitleInfo();
        info.setLastupdate("date");
        info.setPublisher(publisherCode.getName());
        info.setStatus("Review");
        info.setTitle("book");
        info.setTitleId(fullyQualifiedTitleId);
        info.setTotalNumberOfVersions(1);
        info.setVersion("v1.0");

        final Map<String, ProviewTitleInfo> map = new LinkedHashMap<>();
        map.put(fullyQualifiedTitleId + "/v" + info.getMajorVersion(), info);
        return map;
    }

    private BookDefinition createBookDef(final String fullyQualifiedTitleId)
    {
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(BOOK_DEFINITION_ID);
        book.setFullyQualifiedTitleId(fullyQualifiedTitleId);
        book.setDocumentTypeCodes(documentTypeCode);
        book.setPublisherCodes(publisherCode);

        book.setIsDeletedFlag(false);
        book.setEbookDefinitionCompleteFlag(true);
        book.setAutoUpdateSupportFlag(true);
        book.setSearchIndexFlag(true);
        book.setPublishedOnceFlag(false);
        book.setOnePassSsoLinkFlag(true);
        book.setKeyciteToplineFlag(true);
        book.setIsAuthorDisplayVertical(true);
        book.setEnableCopyFeatureFlag(false);
        book.setIsSplitBook(false);
        book.setIsSplitTypeAuto(true);
        return book;
    }
}
