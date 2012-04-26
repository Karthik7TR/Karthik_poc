package com.thomsonreuters.uscl.ereader.mgr.web.controller.fmpreview;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;


/**
 * Book definition front matter preview controller.
 * Allows for a sneak-peek of the book front matter pages prior to publishing.
 */
@Controller
public class FmPreviewController {
	
	private static final Logger log = Logger.getLogger(FmPreviewController.class);
	private static final String BOOK_FIND_FAIL_MESG = "Could not find book definition with ID %d";
	private BookDefinitionService bookDefinitionService;
	private CreateFrontMatterService frontMatterService;

	/**
	 * The main selection page where the user can select the static or dynamic (additional) front matter content.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW, method = RequestMethod.GET)
	public ModelAndView previewContentSelection(@RequestParam Long id, Model model) {
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		if (bookDef == null) {
			InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, String.format(BOOK_FIND_FAIL_MESG, id));
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
		}
		// The front matter pages are a property of the book definition
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW);
	}


	/**
	 * Display preview of title front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE, method = RequestMethod.GET)
	public ModelAndView viewTitleContent(@RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getTitlePage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "Title", model);
	}
	/**
	 * Display preview of copyright front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT, method = RequestMethod.GET)
	public ModelAndView viewCopyrightContent(@RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getCopyrightPage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "Copyright", model);
	}

	/**
	 * Display preview of additional front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_ADDITIONAL, method = RequestMethod.GET)
	public ModelAndView viewAdditionalFrontMatterContent(@RequestParam Long bookDefinitionId,
														 @RequestParam Long frontMatterPageId,
														 Model model) {
//log.debug(String.format("bookDefinitionId=%d&frontMatterPageId=%d",bookDefinitionId, frontMatterPageId));
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
		if (bookDef == null) {
			InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, String.format(BOOK_FIND_FAIL_MESG, bookDefinitionId));
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
			return previewContentSelection(bookDefinitionId, model);
		}
		try {
			String html = frontMatterService.getAdditionalFrontPage(bookDef, frontMatterPageId);
			model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_CONTENT, html);
		} catch (EBookFrontMatterGenerationException e) {
			String errMesg = String.format("Could not fetch additional front matter preview content for book definition ID %d, front matter page ID %d", bookDefinitionId, frontMatterPageId);
			log.debug(errMesg, e);
			InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, errMesg);
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
			return previewContentSelection(bookDefinitionId, model);
		}
		return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT);
	}	
	
	/**
	 * Display preview of research assistance front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH, method = RequestMethod.GET)
	public ModelAndView viewResearchAssistanceContent(@RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getResearchAssistancePage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "Research Assistance", model);
	}
	/**
	 * Display preview of WestlawNext front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT, method = RequestMethod.GET)
	public ModelAndView viewWestlawNextContent(@RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getWestlawNextPage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "WestlawNext", model);
	}
	
	private ModelAndView createStaticFrontMatterContentView(Method staticContentGetter, Long id, String label, Model model) {
		try {
			BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
			if (bookDef == null) {
				InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL, String.format(BOOK_FIND_FAIL_MESG, id));
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
				return previewContentSelection(id, model);
			}
			String html = (String) staticContentGetter.invoke(frontMatterService, bookDef);
			model.addAttribute(WebConstants.KEY_ID, id);
			model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_CONTENT, html);
		} catch (Exception e) {
			InfoMessage mesg = new InfoMessage(InfoMessage.Type.FAIL,
					String.format("Could not fetch static front matter for book definition ID %d", id));
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, mesg);
			// Forward them back to the content selection page with the error message
			return previewContentSelection(id, model);
		}
		return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT);
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}
	@Required
	public void setFrontMatterService(CreateFrontMatterService frontMatterService) {
		this.frontMatterService = frontMatterService;
	}
}
