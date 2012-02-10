package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

@Controller
public class ViewBookDefinitionController {
	private static final Logger log = Logger.getLogger(ViewBookDefinitionController.class);

	private CoreService coreService;
	
	@RequestMapping(value=WebConstants.MVC_BOOK_DEFINITION_VIEW, method = RequestMethod.GET)
	public ModelAndView viewBookDefintion(@RequestParam String titleId, Model model) {
log.debug("titleId="+titleId);

		// TODO: replace with data driven key
BookDefinitionKey bookDefKey = new BookDefinitionKey(titleId);

		// Lookup the book by its primary key
		BookDefinition bookDef = coreService.findBookDefinition(bookDefKey);
log.debug(bookDef);
		
		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_VIEW);
	}
	
	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
}
