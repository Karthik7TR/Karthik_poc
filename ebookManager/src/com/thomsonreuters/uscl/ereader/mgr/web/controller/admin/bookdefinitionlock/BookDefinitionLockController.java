package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookdefinitionlock;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class BookDefinitionLockController {
	//private static final Logger log = Logger.getLogger(BookDefinitionLockController.class);
	
	private BookDefinitionLockService bookLockService;
	
	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 * Only Super users allowed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_ADMIN_BOOK_LOCK_LIST, method = RequestMethod.GET)
	public ModelAndView viewLockList(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, bookLockService.findAllActiveLocks());

		return new ModelAndView(WebConstants.VIEW_ADMIN_BOOK_LOCK_LIST);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_BOOK_LOCK_DELETE, method = RequestMethod.GET)
	public ModelAndView deleteBookLock(@RequestParam("id") Long id,
			@ModelAttribute(BookDefinitionLockForm.FORM_NAME) BookDefinitionLockForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		BookDefinitionLock lock = bookLockService.findBookDefinitionLockByPrimaryKey(id);
		
		if(lock != null) {
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION_LOCK, lock);
			form.initialize(lock);
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_BOOK_LOCK_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_BOOK_LOCK_DELETE, method = RequestMethod.POST)
	public ModelAndView deleteJurisCodePost(@ModelAttribute(BookDefinitionLockForm.FORM_NAME) BookDefinitionLockForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		BookDefinition book = new BookDefinition();
		book.setEbookDefinitionId(form.getBookDefinitionId());
		
		// Remove all locks for the book definition id
		bookLockService.removeLock(book);
		
		// Redirect user
		return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_BOOK_LOCK_LIST));
	}

	@Required
	public void setBookLockService(BookDefinitionLockService service) {
		this.bookLockService = service;
	}
	
}
