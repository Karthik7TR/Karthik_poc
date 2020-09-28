package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.*;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import lombok.SneakyThrows;
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

public final class EditGroupControllerTest {
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + EditGroupDefinitionForm.FORM_NAME;
    private static final Long BOOK_DEFINITION_ID = 1L;
    private static final String GROUP_ID = "uscl/an_abcd";
    public static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/abcd";
    public static final String GROUP_NAME = "Group Name";

    private EditGroupController controller;
    private EditGroupDefinitionFormValidator validator;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;

    private ProviewHandler mockProviewHandler;
    private EBookAuditService mockAuditService;
    private BookDefinitionService mockBookDefinitionService;
    private GroupService mockGroupService;

    private DocumentTypeCode documentTypeCode;
    private PublisherCode publisherCode;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the services
        mockProviewHandler = EasyMock.createMock(ProviewHandler.class);
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockAuditService = EasyMock.createMock(EBookAuditService.class);
        mockGroupService = EasyMock.createMock(GroupService.class);
        validator = new EditGroupDefinitionFormValidator();

        // Set up the controller
        controller = new EditGroupController(mockBookDefinitionService, mockGroupService, mockProviewHandler, mockAuditService, validator);

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
    public void testEditGroupDefinitionGet() throws Exception {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        final String groupId = "uscl/an_abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final List<ProviewTitleInfo> proviewTitleList = createProviewTitleList(FULLY_QUALIFIED_TITLE_ID);
        initProviewHandler(fullyQualifiedTitleId, proviewTitleList);
        EasyMock.expect(mockGroupService.getGroupId(book)).andReturn(groupId);
        EasyMock.expect(mockGroupService.getLastGroup(book)).andReturn(null);
        EasyMock.expect(mockGroupService.getPilotBooksForGroup(book))
            .andReturn(new LinkedHashMap<String, ProviewTitleInfo>());
        EasyMock.expect(mockGroupService.getPilotBooksNotFound()).andReturn(new ArrayList<String>());

        EasyMock.replay(mockGroupService);

        final ModelAndView mav;
        try {
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
            Assert.assertEquals(proviewTitleList.size(), actualProviewTitles);
        } catch (final Exception e) {
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
    public void testEditGroupDefinitionGetDeletedBook() throws Exception {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("id", Long.toString(BOOK_DEFINITION_ID));
        request.setMethod(HttpMethod.GET.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        book.setIsDeletedFlag(true);

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        final ModelAndView mav;
        try {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());
        } catch (final Exception e) {
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
    public void testEditGroupDefinitionPost() throws Exception {
        createRequestPost();
        mockAll(null);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        verifyAll(mav);
    }

    @Test
    public void testEditGroupDefinitionAddSubgroupsPost() throws Exception {
        createRequestPost();
        GroupDefinition groupDefinition = createGroupDefinition();
        mockAll(groupDefinition);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        verifyAll(mav);
    }

    /**
     * Test the POST to the Edit Group Definition page
     * @throws Exception
     */
    @Test
    public void testEditGroupDefintionPostError() throws Exception {
        final String fullyQualifiedTitleId = "uscl/an/abcd";
        final String groupId = "uscl/an_abcd";
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("bookDefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("groupId", groupId);
        request.setParameter("groupType", "standard");
        request.setParameter("versionType", EditGroupDefinitionForm.VersionType.OVERWRITE.toString());
        request.setParameter("hasSplitTitles", "false");
        request.setParameter("includeSubgroup", "false");
        request.setParameter("includePilotBook", "true");

        request.setMethod(HttpMethod.POST.name());

        final BookDefinition book = createBookDef(fullyQualifiedTitleId);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        initProviewHandler(fullyQualifiedTitleId, createProviewTitleList(fullyQualifiedTitleId));
        EasyMock.expect(mockGroupService.getPilotBooksForGroup(book))
            .andReturn(new LinkedHashMap<String, ProviewTitleInfo>());
        EasyMock.expect(mockGroupService.getLastGroup(book)).andReturn(null);
        EasyMock.replay(mockGroupService);

        final ModelAndView mav;
        try {
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
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockGroupService);
        EasyMock.verify(mockBookDefinitionService);
    }

    private List<ProviewTitleInfo> createProviewTitleList(final String fullyQualifiedTitleId) {
        final ProviewTitleInfo info = new ProviewTitleInfo();
        info.setLastupdate("date");
        info.setPublisher(publisherCode.getName());
        info.setStatus("Review");
        info.setTitle("book");
        info.setTitleId(fullyQualifiedTitleId);
        info.setTotalNumberOfVersions(1);
        info.setVersion("v1.0");
        info.setSplitParts(Collections.singletonList(fullyQualifiedTitleId));
        return Arrays.asList(info);
    }

    private BookDefinition createBookDef(final String fullyQualifiedTitleId) {
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

    private void verifyAll(ModelAndView mav) {
        assertNotNull(mav);
        final View view = mav.getView();
        assertEquals(RedirectView.class, view.getClass());

        final Map<String, Object> model = mav.getModel();

        final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
        assertNotNull(bindingResult);
        Assert.assertFalse(bindingResult.hasErrors());

        EasyMock.verify(mockAuditService);
        EasyMock.verify(mockGroupService);
        EasyMock.verify(mockBookDefinitionService);
    }

    private void mockAll(GroupDefinition groupDefinition) throws Exception {
        final BookDefinition book = createBookDef(FULLY_QUALIFIED_TITLE_ID);
        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(book);
        EasyMock.expect(mockBookDefinitionService.saveBookDefinition(EasyMock.anyObject(BookDefinition.class)))
                .andReturn(book);
        EasyMock.replay(mockBookDefinitionService);

        initProviewHandler(FULLY_QUALIFIED_TITLE_ID, createProviewTitleList(FULLY_QUALIFIED_TITLE_ID));

        EasyMock.expect(mockGroupService.getLastGroup(GROUP_ID)).andReturn(groupDefinition);
        mockGroupService.createGroup(EasyMock.anyObject(GroupDefinition.class));
        EasyMock.replay(mockGroupService);

        mockAuditService.saveEBookAudit(EasyMock.anyObject(EbookAudit.class));
        EasyMock.replay(mockAuditService);
    }

    @SneakyThrows
    private void initProviewHandler(String fullyQualifiedTitleId, List<ProviewTitleInfo> proviewTitleInfos) {
        final ProviewTitleContainer container = new ProviewTitleContainer(proviewTitleInfos);
        final Map<String, ProviewTitleContainer> proviewTitleContainerMap = new HashMap<>();
        proviewTitleContainerMap.put(fullyQualifiedTitleId, container);
        EasyMock.expect(mockProviewHandler.getTitlesWithUnitedParts()).andReturn(proviewTitleContainerMap);
        EasyMock.replay(mockProviewHandler);
    }

    private void createRequestPost() {
        request.setRequestURI("/" + WebConstants.MVC_GROUP_DEFINITION_EDIT);
        request.setParameter("bookDefinitionId", Long.toString(BOOK_DEFINITION_ID));
        request.setParameter("groupId", GROUP_ID);
        request.setParameter("groupType", "standard");
        request.setParameter("versionType", EditGroupDefinitionForm.VersionType.OVERWRITE.toString());
        request.setParameter("hasSplitTitles", "false");
        request.setParameter("includeSubgroup", "true");
        request.setParameter("includePilotBook", "false");
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("groupName", GROUP_NAME);
    }

    private GroupDefinition createGroupDefinition() {
        GroupDefinition groupDefinition = new GroupDefinition();
        groupDefinition.setGroupId(GROUP_ID);
        groupDefinition.setName(GROUP_NAME);
        groupDefinition.setStatus(GroupDefinition.REVIEW_STATUS);
        groupDefinition.setHeadTitle(FULLY_QUALIFIED_TITLE_ID);
        groupDefinition.setGroupVersion(2L);
        GroupDefinition.SubGroupInfo subGroupInfo = new GroupDefinition.SubGroupInfo();
        subGroupInfo.setHeading("");
        subGroupInfo.setTitles(Collections.singletonList(FULLY_QUALIFIED_TITLE_ID));
        groupDefinition.setSubGroupInfoList(Collections.singletonList(subGroupInfo));
        return groupDefinition;
    }
}
