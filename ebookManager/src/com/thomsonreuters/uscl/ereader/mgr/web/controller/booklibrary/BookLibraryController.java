package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import static java.util.Optional.ofNullable;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort.SortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.CurrentSessionUserPreferences;
import lombok.extern.slf4j.Slf4j;
import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
public class BookLibraryController {
    private final LibraryListService libraryService;
    private final KeywordTypeCodeSevice keywordTypeCodeSevice;
    private final OutageService outageService;
    private final Validator validator;

    @Autowired
    public BookLibraryController(
        final LibraryListService libraryService,
        final KeywordTypeCodeSevice keywordTypeCodeSevice,
        final OutageService outageService,
        @Qualifier("bookLibraryFilterFormValidator") final Validator validator) {
        this.libraryService = libraryService;
        this.keywordTypeCodeSevice = keywordTypeCodeSevice;
        this.outageService = outageService;
        this.validator = validator;
    }

    @InitBinder(BookLibraryFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST, method = RequestMethod.GET)
    public ModelAndView inboundGet(final HttpSession httpSession, final Model model,
            @ModelAttribute(BookLibraryFilterForm.FORM_NAME) @Valid BookLibraryFilterForm form,
            final BindingResult bindingResult) {
        log.debug(">>>");
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        if (bindingResult.hasErrors()) {
            log.debug("Incorrect parameters passed to BookLibraryFilterForm");
            form = getUserPreferencesForCurrentSession(httpSession);
        } else if (Command.GENERATE.equals(form.getCommand())) {
            return redirectToGeneratePage(form);
        }
        updateUserPreferencesForCurrentSession(form, httpSession);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, createPaginatedList(form));
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, keywordTypeCodeSevice.getAllKeywordTypeCodes());
        model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());

        return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
    }

    private ModelAndView redirectToGeneratePage(final BookLibraryFilterForm form) {
        final String[] bookKeys = form.getSelectedEbookKeys();
        final StringBuilder parameters = new StringBuilder();
        parameters.append("?");
        for (final String key : bookKeys) {
            parameters.append("id=" + key + "&");
        }
        parameters.deleteCharAt(parameters.length() - 1);
        if (bookKeys.length > 1) {
            return new ModelAndView(
                    new RedirectView(WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW + parameters.toString()));
        } else {
            parameters.append("&"+ WebConstants.KEY_IS_COMBINED + "=false");
            return new ModelAndView(
                    new RedirectView(WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW + parameters.toString()));
        }
    }

    private void updateUserPreferencesForCurrentSession(final BookLibraryFilterForm form, final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            sessionPreferences.setBookLibraryPreferences(form);
        }
    }

    private BookLibraryFilterForm getUserPreferencesForCurrentSession(final HttpSession httpSession) {
        final Object preferencesSessionAttribute = httpSession.getAttribute(CurrentSessionUserPreferences.NAME);
        if (preferencesSessionAttribute instanceof CurrentSessionUserPreferences) {
            final CurrentSessionUserPreferences sessionPreferences =
                    (CurrentSessionUserPreferences) preferencesSessionAttribute;
            return sessionPreferences.getBookLibraryPreferences();
        }
        return new BookLibraryFilterForm();
    }

    private PaginatedList createPaginatedList(final BookLibraryFilterForm filterForm) {
        final String action = ofNullable(filterForm.getAction()).map(BookLibraryFilterForm.Action::toString).orElse(null);
        final LibraryListFilter libraryListFilter = new LibraryListFilter(
                filterForm.getFrom(),
                filterForm.getTo(),
                action,
                filterForm.getTitleId(),
                filterForm.getProviewDisplayName(),
                filterForm.getSourceType(),
                filterForm.getIsbn(),
                filterForm.getMaterialId(),
                filterForm.getProviewKeyword());
        final LibraryListSort libraryListSort = createLibraryListSort(filterForm);
        // Lookup all the EbookAudit objects by their primary key
        final List<LibraryList> bookDefinitions =
                libraryService.findBookDefinitions(libraryListFilter, libraryListSort);
        final Integer numberOfBooks = libraryService.numberOfBookDefinitions(libraryListFilter);

        return new BookLibraryPaginatedList(
                bookDefinitions,
                numberOfBooks,
                filterForm.getPage(),
                filterForm.getObjectsPerPage(),
                filterForm.getSort(),
                filterForm.isAscendingSort());
    }

    protected LibraryListSort createLibraryListSort(final BookLibraryFilterForm filterForm) {
        return new LibraryListSort(
                SortProperty.valueOf(filterForm.getSort().toString()),
                filterForm.isAscendingSort(),
                filterForm.getPage(),
                filterForm.getObjectsPerPage());
    }
}
