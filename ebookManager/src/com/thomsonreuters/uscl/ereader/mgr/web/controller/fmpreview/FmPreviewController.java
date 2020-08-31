package com.thomsonreuters.uscl.ereader.mgr.web.controller.fmpreview;

import java.lang.reflect.Method;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import com.thomsonreuters.uscl.ereader.frontmatter.service.FrontMatterPreviewService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Book definition front matter preview controller. Allows for a sneak-peek of
 * the book front matter pages prior to publishing.
 */
@Controller
public class FmPreviewController {
    private static final Logger log = LogManager.getLogger(FmPreviewController.class);
    private static final String BOOK_FIND_FAIL_MESG = "Could not find book definition with ID %d";

    private final BookDefinitionService bookDefinitionService;
    private final FrontMatterPreviewService frontMatterService;

    @Autowired
    public FmPreviewController(
        final BookDefinitionService bookDefinitionService,
        final FrontMatterPreviewService frontMatterService) {
        this.bookDefinitionService = bookDefinitionService;
        this.frontMatterService = frontMatterService;
    }

    /**
     * The main selection page where the user can select the static or dynamic
     * (additional) front matter content.
     *
     * @param id
     *            book definition primary key
     */
    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView previewContentSelection(@RequestParam("id") final Long id, final Model model) {
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
        if (bookDef == null) {
            final InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, String.format(BOOK_FIND_FAIL_MESG, id));
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
        }
        // The front matter pages are a property of the book definition
        model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
        return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW);
    }

    /**
     * Invoked from the window.open() of the book def. editor. Assumes preview
     * HTML content was put on the session by the previous form submit.
     */
    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_EDIT, method = RequestMethod.GET)
    public ModelAndView previewContentSelectionFromEdit(final HttpSession httpSession, final Model model) {
        String frontMatterPreviewHtml = (String) httpSession.getAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML);
        httpSession.removeAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML); // Clear
                                                                                 // the
                                                                                 // HTML
                                                                                 // out
                                                                                 // of
                                                                                 // the
                                                                                 // session

        if (frontMatterPreviewHtml == null) {
            frontMatterPreviewHtml =
                "The front matter preview cannot be refreshed.  Click the Preview button for the Front Matter page you with to view.";
        }
        // The front matter pages are a property of the book definition
        model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, frontMatterPreviewHtml);

        // Content for the pop-up front matter preview window
        return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT);
    }

    /**
     * Display preview of title front matter.
     *
     * @param id
     *            book definition primary key
     */
    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView viewTitleContent(@RequestParam("id") final Long id, final Model model) throws Exception {
        final Method method = frontMatterService.getClass().getMethod("getTitlePagePreview", BookDefinition.class);
        return createStaticFrontMatterContentView(method, id, "Title", model);
    }

    /**
     * Display preview of copyright front matter.
     *
     * @param id
     *            book definition primary key
     */
    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView viewCopyrightContent(@RequestParam("id") final Long id, final Model model) throws Exception {
        final Method method = frontMatterService.getClass().getMethod("getCopyrightPagePreview", BookDefinition.class);
        return createStaticFrontMatterContentView(method, id, "Copyright", model);
    }


    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_ADDITIONAL, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView viewAdditionalFrontMatterContent(
        @RequestParam("bookDefinitionId") final Long bookDefinitionId,
        @RequestParam("frontMatterPageId") final Long frontMatterPageId,
        final Model model) {
        // log.debug(String.format("bookDefinitionId=%d&frontMatterPageId=%d",bookDefinitionId,
        // frontMatterPageId));
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
        if (bookDef == null) {
            final InfoMessage mesg =
                new InfoMessage(InfoMessage.Type.FAIL, String.format(BOOK_FIND_FAIL_MESG, bookDefinitionId));
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
            return previewContentSelection(bookDefinitionId, model);
        }
        try {
            final String html = frontMatterService.getAdditionalFrontPagePreview(bookDef, frontMatterPageId);
            model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, html);
        } catch (final EBookFrontMatterGenerationException e) {
            final String errMesg = String.format(
                "Could not fetch additional front matter preview content for book definition ID %d, front matter page ID %d",
                bookDefinitionId,
                frontMatterPageId);
            log.debug(errMesg, e);
            final InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, errMesg);
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
            return previewContentSelection(bookDefinitionId, model);
        }
        return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT);
    }

    /**
     * Display preview of research assistance front matter.
     *
     * @param id
     *            book definition primary key
     */
    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView viewResearchAssistanceContent(@RequestParam("id") final Long id, final Model model)
        throws Exception {
        final Method method =
            frontMatterService.getClass().getMethod("getResearchAssistancePagePreview", BookDefinition.class);
        return createStaticFrontMatterContentView(method, id, "Research Assistance", model);
    }

    /**
     * Display preview of WestlawNext front matter.
     *
     * @param id
     *            book definition primary key
     */
    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView viewWestlawNextContent(@RequestParam("id") final Long id, final Model model) throws Exception {
        final Method method = frontMatterService.getClass().getMethod("getWestlawNextPagePreview", BookDefinition.class);
        return createStaticFrontMatterContentView(method, id, "WestlawNext", model);
    }

    private ModelAndView createStaticFrontMatterContentView(
        final Method staticContentGetter,
        final Long id,
        final String label,
        final Model model) {
        try {
            final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
            if (bookDef == null) {
                final InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, String.format(BOOK_FIND_FAIL_MESG, id));
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
                return previewContentSelection(id, model);
            }
            final String html = (String) staticContentGetter.invoke(frontMatterService, bookDef);
            model.addAttribute(WebConstants.KEY_ID, id);
            model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, html);
        } catch (final Exception e) {
            final InfoMessage mesg = new InfoMessage(
                InfoMessage.Type.FAIL,
                String.format("Could not fetch static front matter for book definition ID %d", id));
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
            // Forward them back to the content selection page with the error
            // message
            return previewContentSelection(id, model);
        }
        return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT);
    }
}
