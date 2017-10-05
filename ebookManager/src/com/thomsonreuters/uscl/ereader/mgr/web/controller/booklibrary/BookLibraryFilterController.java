package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;
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

@Controller
public class BookLibraryFilterController extends BaseBookLibraryController {
    private final Validator validator;

    @Autowired
    public BookLibraryFilterController(
        final LibraryListService libraryService,
        final CodeService codeService,
        final OutageService outageService,
        @Qualifier("bookLibraryFilterFormValidator") final Validator validator) {
        super(libraryService, codeService, outageService);
        this.validator = validator;
    }

    @InitBinder(BookLibraryFilterForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle submit/post of a new set of filter criteria.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST, method = RequestMethod.POST)
    public ModelAndView doFilterPost(
        final HttpSession httpSession,
        @ModelAttribute(BookLibraryFilterForm.FORM_NAME) @Valid final BookLibraryFilterForm filterForm,
        final BindingResult errors,
        final Model model) {
        // Restore state of paging and sorting
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        final BookLibrarySelectionForm librarySelectionForm = new BookLibrarySelectionForm();
        librarySelectionForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());

        if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
            filterForm.initialize();
        }

        pageAndSort.setPageNumber(1);

        setUpModel(filterForm, pageAndSort, httpSession, model);
        model.addAttribute(BookLibrarySelectionForm.FORM_NAME, librarySelectionForm);

        return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
    }
}
