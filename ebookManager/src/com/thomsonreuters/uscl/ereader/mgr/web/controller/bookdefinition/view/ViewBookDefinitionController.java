package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.util.PrintComponentUtil;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ViewBookDefinitionController {
    private static final Logger log = LogManager.getLogger(ViewBookDefinitionController.class);

    private final BookDefinitionService bookDefinitionService;
    private final JobRequestService jobRequestService;
    private final PrintComponentUtil printComponentUtil;

    @Autowired
    public ViewBookDefinitionController(
        final BookDefinitionService bookDefinitionService,
        final JobRequestService jobRequestService,
        final PrintComponentUtil printComponentUtil) {
        this.bookDefinitionService = bookDefinitionService;
        this.jobRequestService = jobRequestService;
        this.printComponentUtil = printComponentUtil;
    }

    /**
     * Handle the in-bound GET to the Book Definition read-only view page.
     * @param titleId the primary key of the book to be viewed as a required query string parameter.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_VIEW_GET, method = RequestMethod.GET)
    public ModelAndView viewBookDefintion(
        @RequestParam("id") final Long id,
        @ModelAttribute(ViewBookDefinitionForm.FORM_NAME) final ViewBookDefinitionForm form,
        final Model model,
        final HttpSession session) {
        // Lookup the book by its primary key
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
        form.setId(id);
        form.setBookDefinition(bookDef);

        if (bookDef != null) {
            model.addAttribute(
                WebConstants.KEY_IS_IN_JOB_REQUEST,
                jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId()));

            // Check if user canceled from Generate page
            final String generateCanceled = (String) session.getAttribute(WebConstants.KEY_BOOK_GENERATE_CANCEL);
            session.removeAttribute(WebConstants.KEY_BOOK_GENERATE_CANCEL); // Clear the HTML out of the session
            if (generateCanceled != null) {
                model.addAttribute(WebConstants.KEY_INFO_MESSAGE, generateCanceled);
            }
        }
        model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
        model.addAttribute(WebConstants.KEY_FORM, form);

        if (form.getBookDefinition().getSourceType().equals(SourceType.XPP)) {
            final List<PrintComponent> currentPrintComponentsList =
                new ArrayList<>(form.getBookDefinition().getPrintComponents());
            if (currentPrintComponentsList.isEmpty()) {
                form.setGenerateButtonDisabled(true);
            } else {
                form.getBookDefinition().setPrintComponents(
                    printComponentUtil.getAllInitializedPrintComponents(currentPrintComponentsList));
                for (final PrintComponent element : form.getBookDefinition().getPrintComponents()) {
                    if (!element.getComponentInArchive()) {
                        form.setGenerateButtonDisabled(true);
                    }
                }
            }
        }
        return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_VIEW);
    }

    /**
     * Handle press of one of the functional buttons at the bottom of the
     * Book Defintion read-only view page.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_VIEW_POST, method = RequestMethod.POST)
    public ModelAndView doPost(
        @ModelAttribute(ViewBookDefinitionForm.FORM_NAME) final ViewBookDefinitionForm form,
        final Model model) {
        ModelAndView mav = null;
        log.debug(form);
        final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getId());
        final Command command = form.getCommand();
        switch (command) {
        case DELETE:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_DELETE + queryString));
            break;
        case EDIT:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_EDIT + queryString));
            break;
        case GROUP:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_GROUP_DEFINITION_EDIT + queryString));
            break;
        case GENERATE:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW + queryString));
            break;
        case AUDIT_LOG:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_AUDIT_SPECIFIC + queryString));
            break;
        case BOOK_PUBLISH_STATS:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_STATS_SPECIFIC_BOOK + queryString));
            break;
        case COPY:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_COPY + queryString));
            break;
        case RESTORE:
            mav = new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_RESTORE + queryString));
            break;
        default:
            throw new RuntimeException("Unexpected form command: " + command);
        }
        return mav;
    }
}
