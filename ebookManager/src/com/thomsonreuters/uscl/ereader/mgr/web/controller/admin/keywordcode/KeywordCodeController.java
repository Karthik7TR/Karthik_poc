package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Required;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode.KeywordCodeForm;

@Controller
public class KeywordCodeController {
	//private static final Logger log = Logger.getLogger(PubdictionCodeController.class);
	
	private CodeService codeService;
	private BookDefinitionService bookService;
	protected Validator validator;

	@InitBinder(KeywordCodeForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setValidator(validator);
	}
	
	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 * Only Super users allowed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW, method = RequestMethod.GET)
	public ModelAndView viewKeywordsCode(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, codeService.getAllKeywordTypeCodes());

		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_VIEW);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE, method = RequestMethod.GET)
	public ModelAndView createKeywordCode(
			@ModelAttribute(KeywordCodeForm.FORM_NAME) KeywordCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE, method = RequestMethod.POST)
	public ModelAndView createKeywordCodePost(@ModelAttribute(KeywordCodeForm.FORM_NAME) @Valid KeywordCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.saveKeywordTypeCode(form.makeCode());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT, method = RequestMethod.GET)
	public ModelAndView editKeywordCode(@RequestParam("id") Long id,
			@ModelAttribute(KeywordCodeForm.FORM_NAME) KeywordCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		KeywordTypeCode code = codeService.getKeywordTypeCodeById(id);
		
		if (code != null) {
			model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
			form.initialize(code);
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT, method = RequestMethod.POST)
	public ModelAndView editKeywordCodePost(@ModelAttribute(KeywordCodeForm.FORM_NAME) @Valid KeywordCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.saveKeywordTypeCode(form.makeCode());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
		}
		
		KeywordTypeCode code = codeService.getKeywordTypeCodeById(form.getCodeId());
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_DELETE, method = RequestMethod.GET)
	public ModelAndView deleteKeywordCode(@RequestParam("id") Long id,
			@ModelAttribute(KeywordCodeForm.FORM_NAME) KeywordCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		KeywordTypeCode code = codeService.getKeywordTypeCodeById(id);
		
		if(code != null) {
			List<BookDefinition> books = bookService.findAllBookDefinitionsByKeywordCodeId(id);
		
			model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, books);
			form.initialize(code);
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_DELETE, method = RequestMethod.POST)
	public ModelAndView deleteKeywordCodePost(@ModelAttribute(KeywordCodeForm.FORM_NAME) KeywordCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		codeService.deleteKeywordTypeCode(form.makeCode());
		
		// Redirect user
		return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
	}

	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookService = service;
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
