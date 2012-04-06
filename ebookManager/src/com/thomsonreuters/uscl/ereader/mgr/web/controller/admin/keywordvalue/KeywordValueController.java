package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

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
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class KeywordValueController {
	//private static final Logger log = Logger.getLogger(PubdictionCodeController.class);
	
	private CodeService codeService;
	private BookDefinitionService bookService;
	protected Validator validator;

	@InitBinder(KeywordValueForm.FORM_NAME)
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
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE, method = RequestMethod.GET)
	public ModelAndView createKeywordValue(
			@RequestParam Long keywordCodeId,
			@ModelAttribute(KeywordValueForm.FORM_NAME) KeywordValueForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		KeywordTypeCode code = codeService.getKeywordTypeCodeById(keywordCodeId);
		form.setKeywordTypeCode(code);
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE, method = RequestMethod.POST)
	public ModelAndView createKeywordValuePost(@ModelAttribute(KeywordValueForm.FORM_NAME) @Valid KeywordValueForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.saveKeywordTypeValue(form.makeKeywordTypeValue());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
		}

		KeywordTypeCode code = codeService.getKeywordTypeCodeById(form.getKeywordTypeCode().getId());
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT, method = RequestMethod.GET)
	public ModelAndView editKeywordValue(@RequestParam Long id,
			@ModelAttribute(KeywordValueForm.FORM_NAME) KeywordValueForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		KeywordTypeValue value = codeService.getKeywordTypeValueById(id);
		
		if(value != null){
			model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, value.getKeywordTypeCode());
			model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_VALUE, value);
			form.initialize(value);
		}
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT, method = RequestMethod.POST)
	public ModelAndView editKeywordValuePost(@ModelAttribute(KeywordValueForm.FORM_NAME) @Valid KeywordValueForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.saveKeywordTypeValue(form.makeKeywordTypeValue());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
		}
		
		KeywordTypeValue value = codeService.getKeywordTypeValueById(form.getId());
		
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, value.getKeywordTypeCode());
		model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_VALUE, value);
		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_DELETE, method = RequestMethod.GET)
	public ModelAndView deleteKeywordValue(@RequestParam Long id,
			@ModelAttribute(KeywordValueForm.FORM_NAME) KeywordValueForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		KeywordTypeValue value = codeService.getKeywordTypeValueById(id);
		
		if(value != null) {
			List<BookDefinition> books = bookService.findAllBookDefinitionsByKeywordValueId(id);
			
			model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, value.getKeywordTypeCode());
			model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_VALUE, value);
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, books);
			form.initialize(value);
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_DELETE, method = RequestMethod.POST)
	public ModelAndView deleteKeywordValuePost(@ModelAttribute(KeywordValueForm.FORM_NAME) KeywordValueForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		KeywordTypeValue value = form.makeKeywordTypeValue();
		codeService.deleteKeywordTypeValue(value);
		
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
