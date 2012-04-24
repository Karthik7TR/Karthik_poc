package com.thomsonreuters.uscl.ereader.mgr.web.controller.fmpreview;
import java.lang.reflect.Method;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;


/**
 * Book definition front matter preview controller.
 * Allows for a sneak-peek of the book front matter pages prior to publishing.
 */
@Controller
public class FmPreviewController {
	
	private static final Logger log = Logger.getLogger(FmPreviewController.class);


	private BookDefinitionService bookDefinitionService;
	private CreateFrontMatterService frontMatterService;

	/**
	 * The main selection page where the user can drill down to the static and dynamic front matter data.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW, method = RequestMethod.GET)
	public ModelAndView previewContentSelection(HttpSession httpSession, @RequestParam Long id, Model model) {
		log.debug("bookDefinitionId="+id);
		BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
		if (bookDef == null) {
			log.error("Could not find book definition for id=" + id);
		}
		model.addAttribute(WebConstants.KEY_ID, id);
		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
		return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW);
	}

	/**
	 * Display preview of title front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_TITLE, method = RequestMethod.GET)
	public ModelAndView viewTitleContent(HttpSession httpSession, @RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getTitlePage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "Title", model);
	}
	/**
	 * Display preview of copyright front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_COPYRIGHT, method = RequestMethod.GET)
	public ModelAndView viewCopyrightContent(HttpSession httpSession, @RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getCopyrightPage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "Copyright", model);
	}
	/**
	 * Display preview of research assistance front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_RESEARCH, method = RequestMethod.GET)
	public ModelAndView viewResearchAssistanceContent(HttpSession httpSession, @RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getResearchAssistancePage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "Research Assistance", model);
	}
	/**
	 * Display preview of WestlawNext front matter.
	 * @param id book definition primary key
	 */
	@RequestMapping(value = WebConstants.MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT, method = RequestMethod.GET)
	public ModelAndView viewWestlawNextContent(HttpSession httpSession, @RequestParam Long id, Model model) throws Exception {
		Method method = frontMatterService.getClass().getMethod("getWestlawNextPage", BookDefinition.class);
		return createStaticFrontMatterContentView(method, id, "WestlawNext", model);
	}
	
	private ModelAndView createStaticFrontMatterContentView(Method staticContentGetter, Long id, String label, Model model) {
		try {
			BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);
			String html = (String) staticContentGetter.invoke(frontMatterService, bookDef);
			model.addAttribute(WebConstants.KEY_ID, id);
			model.addAttribute(WebConstants.KEY_FRONT_MATTER_PREVIEW_CONTENT, html);
		} catch (Exception e) {
			log.error(String.format("Could not fetch %s front matter", label), e);
			return new ModelAndView(new RedirectView(createSelectionUrl(id)));
		}
		return new ModelAndView(WebConstants.VIEW_FRONT_MATTER_PREVIEW_CONTENT);
	}

	private static String createSelectionUrl(Long id) {
		return String.format("%s?%s=%d", WebConstants.MVC_FRONT_MATTER_PREVIEW, WebConstants.KEY_ID, id);
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
