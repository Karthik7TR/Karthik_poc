package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;


@Controller
public class BookLibraryController {
	//private static final Logger log = Logger.getLogger(BookLibraryController.class);

	@RequestMapping(value=WebConstants.MVC_BOOK_LIBRARY_LIST, method = RequestMethod.GET)
	public ModelAndView bookList(Model model) throws Exception {
// TODO: implement this		
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
}
