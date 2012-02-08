package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;


@Controller
public class BookLibraryController {
	//private static final Logger log = Logger.getLogger(BookLibraryController.class);

	private BookLibraryService bookLibraryService;
	
	@RequestMapping(value=WebConstants.MVC_BOOK_LIBRARY_LIST, method = RequestMethod.GET)
	public ModelAndView bookList(Model model) throws Exception {

		List<BookDefinitionVdo> books = bookLibraryService.getAllBooks();
		
		model.addAttribute("books", books);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}
	
	@RequestMapping(value=WebConstants.MVC_BOOK_LIBRARY_THUMBNAILS, method = RequestMethod.GET)
	public ModelAndView bookThumbnails(Model model) throws Exception {
// TODO: implement this
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_THUMBNAILS);
	}
	
	@RequestMapping(value=WebConstants.MVC_BOOK_LIBRARY_ICONS, method = RequestMethod.GET)
	public ModelAndView bookIcons(Model model) throws Exception {
// TODO: implement this		
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_ICONS);
	}

	public BookLibraryService getBookLibraryService() {
		return bookLibraryService;
	}

	public void setBookLibraryService(BookLibraryService bookLibraryService) {
		this.bookLibraryService = bookLibraryService;
	}
	
	
}
