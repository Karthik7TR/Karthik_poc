package com.thomsonreuters.uscl.ereader.mgr.web.controller.fmpreview;

import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import com.thomsonreuters.uscl.ereader.frontmatter.service.FrontMatterPreviewService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class FmPreviewControllerTest {
    private static final Long BOOK_DEF_ID = Long.valueOf(1234);
    private static final String HTML = "<html><body>Some bogus junit testing content</body></html>";
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private BookDefinitionService mockBookDefinitionService;
    private FrontMatterPreviewService mockFrontMatterService;
    private BookDefinition mockBookDef;
    private FmPreviewController controller;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockBookDefinitionService = EasyMock.createMock(BookDefinitionService.class);
        mockFrontMatterService = EasyMock.createMock(FrontMatterPreviewService.class);
        mockBookDef = EasyMock.createMock(BookDefinition.class);
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        controller = new FmPreviewController(mockBookDefinitionService, mockFrontMatterService);
    }

    @Test
    public void testPreviewContentSelectionFromEdit() {
        final String frontMatterPreviewHtml = "something";

        request.setRequestURI("/" + WebConstants.MVC_FRONT_MATTER_PREVIEW_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_ID, BOOK_DEF_ID.toString());
        // Set the HttpSession attribute
        request.getSession().setAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, frontMatterPreviewHtml);

        try {
            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            Assert.assertNotNull(mav);
            Assert.assertEquals(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT, mav.getViewName());
            // Verify the model
            final Map<String, Object> model = mav.getModel();
            // retrieve the text set in attribute WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML
            Assert.assertEquals(frontMatterPreviewHtml, model.get(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML));
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPreviewContentSelection() {
        request.setRequestURI("/" + WebConstants.MVC_FRONT_MATTER_PREVIEW);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_ID, BOOK_DEF_ID.toString());

        EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID)).andReturn(mockBookDef);
        EasyMock.replay(mockBookDefinitionService);

        try {
            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            Assert.assertNotNull(mav);
            Assert.assertEquals(WebConstants.VIEW_FRONT_MATTER_PREVIEW, mav.getViewName());
            // Verify the model
            final Map<String, Object> model = mav.getModel();
            Assert.assertEquals(mockBookDef, model.get(WebConstants.KEY_BOOK_DEFINITION));
            EasyMock.verify(mockBookDefinitionService);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTitleStaticContent() throws Exception {
        EasyMock.expect(mockFrontMatterService.getTitlePagePreview(mockBookDef)).andReturn(HTML);
        testStaticFrontMatterContent(WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE);
    }

    @Test
    public void testCopyrightStaticContent() throws Exception {
        EasyMock.expect(mockFrontMatterService.getCopyrightPagePreview(mockBookDef)).andReturn(HTML);
        testStaticFrontMatterContent(WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT);
    }

    @Test
    public void testResearchStaticContent() throws Exception {
        EasyMock.expect(mockFrontMatterService.getResearchAssistancePagePreview(mockBookDef)).andReturn(HTML);
        testStaticFrontMatterContent(WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH);
    }

    @Test
    public void testWestlawNextStaticContent() throws Exception {
        EasyMock.expect(mockFrontMatterService.getWestlawNextPagePreview(mockBookDef)).andReturn(HTML);
        testStaticFrontMatterContent(WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT);
    }

    /**
     * Tests a failure in fetching the static preview front matter HTML.
     */
    @Test
    public void testStaticContentFailure() {
        request.setRequestURI("/" + WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_ID, BOOK_DEF_ID.toString());
        try {
            EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID))
                .andReturn(mockBookDef)
                .times(2);
            EasyMock.expect(mockFrontMatterService.getTitlePagePreview(mockBookDef))
                .andThrow(new EBookFrontMatterGenerationException("Bogus junit exception"));
            EasyMock.replay(mockBookDefinitionService);
            EasyMock.replay(mockFrontMatterService);

            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            Assert.assertNotNull(mav);
            Assert.assertEquals(WebConstants.VIEW_FRONT_MATTER_PREVIEW, mav.getViewName());
            // Verify the model
            final Map<String, Object> model = mav.getModel();
            final InfoMessage errMesg = (InfoMessage) model.get(WebConstants.KEY_ERR_MESSAGE);
            Assert.assertNotNull(errMesg);
            EasyMock.verify(mockBookDefinitionService);
            EasyMock.verify(mockFrontMatterService);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testAdditionalFrontMatterContent() {
        final Long frontMatterPageId = Long.valueOf(9998);
        request.setRequestURI("/" + WebConstants.MVC_FRONT_MATTER_PREVIEW_ADDITIONAL);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("bookDefinitionId", BOOK_DEF_ID.toString());
        request.setParameter("frontMatterPageId", frontMatterPageId.toString());

        try {
            EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID))
                .andReturn(mockBookDef);
            EasyMock.expect(mockFrontMatterService.getAdditionalFrontPagePreview(mockBookDef, frontMatterPageId))
                .andReturn(HTML);
            EasyMock.replay(mockBookDefinitionService);
            EasyMock.replay(mockFrontMatterService);

            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            Assert.assertNotNull(mav);
            Assert.assertEquals(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT, mav.getViewName());
            // Verify the model
            final Map<String, Object> model = mav.getModel();
            Assert.assertEquals(HTML, model.get(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML));
            EasyMock.verify(mockBookDefinitionService);
            EasyMock.verify(mockFrontMatterService);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private void testStaticFrontMatterContent(final String url) {
        request.setRequestURI("/" + url);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(WebConstants.KEY_ID, BOOK_DEF_ID.toString());

        try {
            EasyMock.expect(mockBookDefinitionService.findBookDefinitionByEbookDefId(BOOK_DEF_ID))
                .andReturn(mockBookDef);
            EasyMock.replay(mockBookDefinitionService);
            EasyMock.replay(mockFrontMatterService);

            final ModelAndView mav = handlerAdapter.handle(request, response, controller);
            Assert.assertNotNull(mav);
            Assert.assertEquals(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT, mav.getViewName());
            // Verify the model
            final Map<String, Object> model = mav.getModel();
            Assert.assertEquals(BOOK_DEF_ID, model.get(WebConstants.KEY_ID));
            Assert.assertEquals(HTML, model.get(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML));
            EasyMock.verify(mockBookDefinitionService);
            EasyMock.verify(mockFrontMatterService);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
