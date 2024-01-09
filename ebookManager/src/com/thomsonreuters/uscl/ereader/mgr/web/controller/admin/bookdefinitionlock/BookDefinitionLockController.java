package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookdefinitionlock;

import static com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.MVC_BOOK_DEFINITION_LOCK_EXTEND;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class BookDefinitionLockController {
    private final BookDefinitionLockService bookLockService;
    private BookDefinitionService bookDefinitionService;

    @Autowired
    public BookDefinitionLockController(final BookDefinitionLockService bookLockService, final BookDefinitionService bookDefinitionService) {
        this.bookLockService = bookLockService;
        this.bookDefinitionService = bookDefinitionService;
    }

    /**
     * Handle initial in-bound HTTP get request to the page.
     * No query string parameters are expected.
     * Only Super users allowed
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_ADMIN_BOOK_LOCK_LIST, method = RequestMethod.GET)
    public ModelAndView viewLockList(final Model model) {
        model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, bookLockService.findAllActiveLocks());
        return new ModelAndView(WebConstants.VIEW_ADMIN_BOOK_LOCK_LIST);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_BOOK_LOCK_DELETE, method = RequestMethod.GET)
    public ModelAndView deleteBookLock(
        @RequestParam("id") final Long id,
        @ModelAttribute(BookDefinitionLockForm.FORM_NAME) final BookDefinitionLockForm form,
        final Model model) {
        final BookDefinitionLock lock = bookLockService.findBookDefinitionLockByPrimaryKey(id);

        if (lock != null) {
            model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
            form.initialize(lock);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_BOOK_LOCK_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_BOOK_LOCK_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteJurisCodePost(
        @RequestParam final Long bookDefinitionId) {
        final BookDefinition book = new BookDefinition();
        book.setEbookDefinitionId(bookDefinitionId);
        // Remove all locks for the book definition id
        bookLockService.removeLock(book);
        // Redirect user
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_BOOK_LOCK_LIST));
    }

    @RequestMapping(value = MVC_BOOK_DEFINITION_LOCK_EXTEND, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void extendLock(@RequestParam("id") final Long id) {
        final BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(id);
        bookLockService.extendLock(book);
    }
}
