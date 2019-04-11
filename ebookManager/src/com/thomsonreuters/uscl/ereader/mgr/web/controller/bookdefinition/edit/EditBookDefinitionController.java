package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_SUBJECT_MATTER;
import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.KEY_SUBJECT_MATTER_ID;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.PrintComponentsCompareController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponentsResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class EditBookDefinitionController {
    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private JobRequestService jobRequestService;
    @Autowired
    private EditBookDefinitionService editBookDefinitionService;
    @Autowired
    private EBookAuditService auditService;
    @Autowired
    private BookDefinitionLockService bookLockService;
    @Autowired
    private CreateFrontMatterService frontMatterService;
    @Autowired
    private MiscConfigSyncService miscConfigService;
    @Autowired
    private PrintComponentsCompareController printComponentsCompareController;
    @Autowired
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    @Autowired
    @Qualifier("editBookDefinitionFormValidator")
    private Validator validator;

    @InitBinder(EditBookDefinitionForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setAutoGrowNestedPaths(false);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        final SimpleDateFormat dateFormat = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
        dateFormat.setLenient(false);
        // true passed to CustomDateEditor constructor means convert empty String to null
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

        binder.setValidator(validator);
    }

    /**
     * Handle the in-bound GET to the Book Definition create view page.
     * @param titleId the primary key of the book to be edited as a required query string parameter.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_CREATE, method = RequestMethod.GET)
    public ModelAndView createBookDefintionGet(@ModelAttribute(EditBookDefinitionForm.FORM_NAME) final EditBookDefinitionForm definitionForm,
                                               final Model model, final Authentication authentication) {
        final Collection<String> userAuthorities = Optional.ofNullable(authentication)
            .map(Authentication::getAuthorities)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        final EditBookDefinitionForm form;
        if (userAuthorities.contains(SecurityRole.ROLE_SUPERUSER.name())) {
            form = definitionForm;
        } else {
            form = new EditBookDefinitionForm();
            model.addAttribute(EditBookDefinitionForm.FORM_NAME, form);
        }
        initializeModel(model, form);
        return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
    }

    /**
     * Handle the in-bound POST to the Book Definition create view page.
     * @param form
     * @param bindingResult
     * @param model
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_CREATE, method = RequestMethod.POST)
    public ModelAndView createBookDefintionPost(
        final HttpSession httpSession,
        @ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid final EditBookDefinitionForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        setUpFrontMatterPreviewModel(httpSession, form, bindingResult);

        if (!bindingResult.hasErrors()) {
            BookDefinition book = new BookDefinition();
            form.loadBookDefinition(book);
            book = bookDefinitionService.saveBookDefinition(book);

            // Save in Audit
            final EbookAudit audit = new EbookAudit();
            audit.loadBookDefinition(
                book,
                EbookAudit.AUDIT_TYPE.CREATE,
                UserUtils.getAuthenticatedUserName(),
                form.getComment());
            auditService.saveEBookAudit(audit);

            // Redirect user
            final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, book.getEbookDefinitionId());
            return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET + queryString));
        }

        initializeModel(model, form);

        return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_CREATE);
    }

    /**
     * Handle the in-bound GET to the Book Definition edit view page.
     * @param titleId the primary key of the book to be edited as a required query string parameter.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView editBookDefintionGet(
        @RequestParam("id") final Long id,
        @ModelAttribute(EditBookDefinitionForm.FORM_NAME) final EditBookDefinitionForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        boolean isPublished = false;
        final String username = UserUtils.getAuthenticatedUserName();

        // Lookup the book by its primary key
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);

        // model used in VIEW_BOOK_DEFINITION_LOCKED and VIEW_BOOK_DEFINITION_EDIT
        model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);

        // Check if user needs to be shown an error
        if (bookDef != null) {
            // Check if book is soft deleted
            if (bookDef.isDeletedFlag()) {
                return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
            }

            // Check if book is being edited by another user
            final BookDefinitionLock lock = bookLockService.findActiveBookLock(bookDef);
            if (lock != null && !lock.getUsername().equalsIgnoreCase(username)) {
                model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
                return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED);
            }

            // Check if book is in queue to be generated
            if (jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId())) {
                return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_QUEUED);
            }

            // Lock book definition
            bookLockService.lockBookDefinition(bookDef, username, UserUtils.getAuthenticatedUserFullName());

            isPublished = bookDef.getPublishedOnceFlag();
        }

        form.initialize(bookDef, editBookDefinitionService.getKeywordCodes());
        model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
        model.addAttribute(WebConstants.KEY_MAX_SPLIT_PARTS, miscConfigService.getMiscConfig().getMaxSplitParts());

        initializeModel(model, form);

        return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
    }

    private void setUpFrontMatterPreviewModel(
        final HttpSession httpSession,
        final EditBookDefinitionForm form,
        final BindingResult bindingResult) throws Exception {
        // The one error is the message indicating that the form was validated, any more than this indicates other problems
        final Long frontMatterPreviewPageId = form.getSelectedFrontMatterPreviewPage();
        if ((frontMatterPreviewPageId != null) && (bindingResult.getErrorCount() == 1)) {
            final BookDefinition fmBookDef = createFrontMatterPreviewBookDefinitionFromForm(form);
            final String html = frontMatterService.getAdditionalFrontPage(fmBookDef, frontMatterPreviewPageId);
            //model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, html);
            // Place the preview content on the session so that it can be fetched and used when the popup preview window is opened
            httpSession.setAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_HTML, html);
        }
    }

    /**
     * Handle the in-bound POST to the Book Definition edit view page.
     * @param titleId
     * @param form
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_EDIT, method = RequestMethod.POST)
    public ModelAndView editBookDefintionPost(
        final HttpSession httpSession,
        @ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid final EditBookDefinitionForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        setUpFrontMatterPreviewModel(httpSession, form, bindingResult);

        final boolean isPublished = false;
        final Long bookDefinitionId = form.getBookdefinitionId();
        final String username = UserUtils.getAuthenticatedUserName();

        BookDefinition bookDef = null;
        try {
            // Lookup the book by its primary key
            bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
        } catch (final Exception e) {
            // Error happens when POST of form is over Tomcat post limit. // Default is set at 2 mb.
            // The processed form is empty.
            return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DEFINITION));
        }

        // model used in VIEW_BOOK_DEFINITION_LOCKED and VIEW_BOOK_DEFINITION_EDIT
        model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);

        // Check if user needs to be shown an error
        if (bookDef != null) {
            // Check if book is soft deleted
            if (bookDef.isDeletedFlag()) {
                return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
            }

            // Check if book is being edited by another user
            final BookDefinitionLock lock = bookLockService.findActiveBookLock(bookDef);
            if (lock != null && !lock.getUsername().equalsIgnoreCase(username)) {
                model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
                return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_LOCKED);
            }

            // Check if book is in queue to be generated
            if (jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId())) {
                return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_ERROR_QUEUED);
            }

            model.addAttribute(WebConstants.KEY_MAX_SPLIT_PARTS, miscConfigService.getMiscConfig().getMaxSplitParts());
        } else {
            // Book Definition has been deleted from the database when user saved the book.
            // Show error page
            return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
        }

        if (!bindingResult.hasErrors()) {
            form.loadBookDefinition(bookDef);
            bookDef = bookDefinitionService.saveBookDefinition(bookDef);

            // Save in Audit
            final EbookAudit audit = new EbookAudit();
            audit.loadBookDefinition(
                bookDef,
                EbookAudit.AUDIT_TYPE.EDIT,
                UserUtils.getAuthenticatedUserName(),
                form.getComment());
            auditService.saveEBookAudit(audit);

            // Remove lock from BookDefinition
            bookLockService.removeLock(bookDef);

            // Redirect user
            final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, bookDefinitionId);
            return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET + queryString));
        }

        model.addAttribute(WebConstants.KEY_IS_PUBLISHED, isPublished);
        model.addAttribute(WebConstants.KEY_MAX_SPLIT_PARTS, miscConfigService.getMiscConfig().getMaxSplitParts());
        initializeModel(model, form);

        return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_EDIT);
    }

    /**
     * AJAX call to remove lock on book definition
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_UNLOCK, method = RequestMethod.POST)
    public @ResponseBody String unlockBookDefinition(@RequestParam("id") final Long id) {
        final String username = UserUtils.getAuthenticatedUserName();

        final BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(id);

        if (book != null) {
            // Check if current user is the one with the lock
            final BookDefinitionLock lock = bookLockService.findActiveBookLock(book);
            if (lock != null && lock.getUsername().equalsIgnoreCase(username)) {
                bookLockService.removeLock(book);
            }
        }
        return "success";
    }

    /**
     * Handle the in-bound GET to the Book Definition copy view page.
     * @param titleId the primary key of the book to be edited as a required query string parameter.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_COPY, method = RequestMethod.GET)
    public ModelAndView copyBookDefintionGet(
        @RequestParam("id") final Long id,
        @ModelAttribute(EditBookDefinitionForm.FORM_NAME) final EditBookDefinitionForm form,
        final BindingResult bindingResult,
        final Model model) {
        // Lookup the book by its primary key
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);

        if (bookDef.isDeletedFlag()) {
            return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
        } else {
            form.copyBookDefinition(bookDef, editBookDefinitionService.getKeywordCodes());
            initializeModel(model, form);
            return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_COPY);
        }
    }

    /**
     * Handle the in-bound POST to the Book Definition copy view page.
     * @param form
     * @param bindingResult
     * @param model
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_COPY, method = RequestMethod.POST)
    public ModelAndView copyBookDefintionPost(
        final HttpSession httpSession,
        @ModelAttribute(EditBookDefinitionForm.FORM_NAME) @Valid final EditBookDefinitionForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        setUpFrontMatterPreviewModel(httpSession, form, bindingResult);

        if (!bindingResult.hasErrors()) {
            BookDefinition book = new BookDefinition();
            form.loadBookDefinition(book);
            book = bookDefinitionService.saveBookDefinition(book);

            // Save in Audit
            final EbookAudit audit = new EbookAudit();
            audit.loadBookDefinition(
                book,
                EbookAudit.AUDIT_TYPE.CREATE,
                UserUtils.getAuthenticatedUserName(),
                form.getComment());
            auditService.saveEBookAudit(audit);

            // Redirect user
            final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, book.getEbookDefinitionId());
            return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET + queryString));
        }

        initializeModel(model, form);

        return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_COPY);
    }

    @RequestMapping(value = WebConstants.MVC_CODES_WORKBENCH_FOLDERS, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getCodesWorkbenchFolders(@RequestParam("folderName") final String folderName) {
        return editBookDefinitionService.getCodesWorkbenchDirectory(folderName);
    }

    @RequestMapping(value = "getDataFromSap.mvc", method = RequestMethod.POST)
    @ResponseBody
    @NotNull
    public MaterialComponentsResponse getDataFromSap(
        @NotNull @RequestParam("subNumber") final String subNumber,
        @Nullable @RequestParam("setNumber") final String setNumber,
        @NotNull @RequestParam(value = "titleId", required = false) final String titleId) {
        return editBookDefinitionService.getMaterialBySubNumber(subNumber, setNumber, titleId);
    }

    /**
     * Initializes the model for the Create/Edit Book Definition View
     * @param model
     * @param form
     */
    private void initializeModel(final Model model, final EditBookDefinitionForm form) {
        // Get Collection sizes to display on form
        model.addAttribute(WebConstants.KEY_NUMBER_OF_AUTHORS, form.getAuthorInfo().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_PILOT_BOOKS, form.getPilotBookInfo().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_FRONT_MATTERS, form.getFrontMatters().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_EXCLUDE_DOCUMENTS, form.getExcludeDocuments().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_RENAME_TOC_ENTRIES, form.getRenameTocEntries().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_TABLE_VIEWERS, form.getTableViewers().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_DOCUMENT_COPYRIGHTS, form.getDocumentCopyrights().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_SPLIT_DOCUMENTS, form.getSplitDocuments().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_DOCUMENT_CURRENCIES, form.getDocumentCurrencies().size());
        model.addAttribute(WebConstants.KEY_NUMBER_OF_NORT_FILE_LOCATIONS, form.getNortFileLocations().size());

        // Set drop down lists
        model.addAttribute(WebConstants.KEY_STATES, editBookDefinitionService.getStates());
        model.addAttribute(WebConstants.KEY_CONTENT_TYPES, editBookDefinitionService.getDocumentTypes());
        model.addAttribute(WebConstants.KEY_PUB_TYPES, editBookDefinitionService.getPubTypes());
        model.addAttribute(WebConstants.KEY_JURISDICTIONS, editBookDefinitionService.getJurisdictions());
        model.addAttribute(WebConstants.KEY_FRONT_MATTER_THEMES, editBookDefinitionService.getFrontMatterThemes());
        model.addAttribute(WebConstants.KEY_PUBLISHERS, editBookDefinitionService.getPublishers());
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, editBookDefinitionService.getKeywordCodes());
        model.addAttribute(WebConstants.KEY_MAX_SPLIT_PARTS, miscConfigService.getMiscConfig().getMaxSplitParts());
        model.addAttribute(WebConstants.KEY_FORM, form);

        model.addAttribute(KEY_SUBJECT_MATTER_ID, keywordTypeCodeSevice.getKeywordTypeCodeByName(KEY_SUBJECT_MATTER).getId());

        printComponentsCompareController.setPrintComponentHistoryAttributes(form.getBookdefinitionId(), model);
    }

    /**
     * Create a book definition suitable for providing to the front matter preview service.
     * @param form assumed to have been previously validated correctly
     * @return
     */
    public static BookDefinition createFrontMatterPreviewBookDefinitionFromForm(final EditBookDefinitionForm form)
        throws ParseException {
        final BookDefinition book = new BookDefinition();
        form.loadBookDefinition(book);
        final List<FrontMatterPage> pages = book.getFrontMatterPages();
        for (final FrontMatterPage page : pages) {
            final Long pk = Long.valueOf(page.getSequenceNum());
            page.setId(pk);
        }
        return book;
    }
}
