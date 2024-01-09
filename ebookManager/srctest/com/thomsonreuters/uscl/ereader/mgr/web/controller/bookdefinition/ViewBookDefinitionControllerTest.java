package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
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

public final class ViewBookDefinitionControllerTest {
    private static final String TITLE_ID = "a/b/c/d";
    private static final Long BOOK_DEFINITION_ID = Long.valueOf(1);
    private static final String printComponentsJson =
        "[{&quot;printComponentId&quot;:&quot;1&quot;,&quot;componentOrder&quot;:1,&quot;materialNumber&quot;:&quot;123&quot;,&quot;componentName&quot;:&quot;c1&quot;,&quot;splitter&quot;:false,&quot;componentInArchive&quot;:false,&quot;supplement&quot;:false}]";
    private ViewBookDefinitionController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private BookDefinitionService mockBookDefinitionService;
    private JobRequestService mockJobRequestService;
    private PrintComponentsCompareController mockPrintComponentsCompareController;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockJobRequestService = EasyMock.createMock(JobRequestService.class);
        controller = new ViewBookDefinitionController(mockBookDefinitionService, mockJobRequestService, null);
        mockPrintComponentsCompareController = EasyMock.createMock(PrintComponentsCompareController.class);

        org.springframework.test.util.ReflectionTestUtils
            .setField(controller, "printComponentsCompareController", mockPrintComponentsCompareController);
    }

    @Test
    public void textBookDefinitionNull() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_VIEW_GET);
        request.setMethod(HttpMethod.GET.name());
        request.addParameter(WebConstants.KEY_ID, Long.toString(BOOK_DEFINITION_ID));

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID)).andReturn(null);
        EasyMock.replay(mockBookDefinitionService);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        final RedirectView views = (RedirectView) mav.getView();
        Assert.assertEquals(WebConstants.MVC_ERROR_BOOK_DELETED, views.getUrl());
    }

    @Test
    public void testBookDefinitionViewGet() throws Exception {
        // Set up the request URL

        final BookDefinition bookDef = new BookDefinition();
        bookDef.setFullyQualifiedTitleId(TITLE_ID);
        bookDef.setEbookDefinitionId(BOOK_DEFINITION_ID);
        bookDef.setPrintComponents(getPrintComponents());
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_VIEW_GET);
        request.setMethod(HttpMethod.GET.name());
        request.addParameter(WebConstants.KEY_ID, Long.toString(BOOK_DEFINITION_ID));

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEFINITION_ID))
            .andReturn(bookDef);
        EasyMock.replay(mockBookDefinitionService);

        EasyMock.expect(mockJobRequestService.isBookInJobRequest(BOOK_DEFINITION_ID)).andReturn(false);
        EasyMock.replay(mockJobRequestService);

        mockPrintComponentsCompareController.setPrintComponentHistoryAttributes(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.replay(mockPrintComponentsCompareController);

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        final Map<String, Object> model = mav.getModel();
        Assert.assertNotNull(mav);
        Assert.assertEquals(bookDef, model.get(WebConstants.KEY_BOOK_DEFINITION));
        Assert.assertEquals(WebConstants.VIEW_BOOK_DEFINITION_VIEW, mav.getViewName());

        final ViewBookDefinitionForm form = (ViewBookDefinitionForm) model.get(WebConstants.KEY_FORM);
        Assert.assertNotNull(form);
        Assert.assertEquals(printComponentsJson, form.getPrintComponents());

        EasyMock.verify(mockBookDefinitionService);
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockPrintComponentsCompareController);
    }

    private List<PrintComponent> getPrintComponents() {
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setPrintComponentId("1");
        printComponent.setComponentOrder(1);
        printComponent.setMaterialNumber("123");
        printComponent.setComponentName("c1");
        return Collections.singletonList(printComponent);
    }

    @Test
    public void testEditPost() throws Exception {
        testBookDefinitionViewPostCommand(Command.EDIT, WebConstants.MVC_BOOK_DEFINITION_EDIT, false);
    }

    @Test
    public void testGeneratePost() throws Exception {
        testBookDefinitionViewPostCommand(Command.GENERATE, WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, true);
    }

    private void testBookDefinitionViewPostCommand(final Command command, final String viewUri, boolean shouldAddIsCombined) throws Exception {
        // Set up the request URL
        request.setRequestURI("/" + WebConstants.MVC_BOOK_DEFINITION_VIEW_POST);
        request.setMethod(HttpMethod.POST.name());
        request.addParameter("command", command.name());
        request.addParameter("id", BOOK_DEFINITION_ID.toString());

        // Invoke the controller method via the URL
        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Assert.assertNotNull(mav);
        final View view = mav.getView();
        Assert.assertTrue(view instanceof RedirectView);
        final RedirectView rView = (RedirectView) view;
        final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, BOOK_DEFINITION_ID) + (shouldAddIsCombined ? "&isCombined=false" : "");
        Assert.assertEquals(viewUri + queryString, rView.getUrl());
    }
}
