package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;



@Controller
public class BookDefintionController {
	//private static final Logger log = Logger.getLogger(BookLibraryController.class);

	
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_IMPORT, method = RequestMethod.GET)
	public ModelAndView bookDefintionImport(HttpServletRequest request,
		    HttpServletResponse response, Model model) throws ServletRequestBindingException {

		
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_IMPORT);
	}
	
	
}
