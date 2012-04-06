package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class StateCodeController {
	//private static final Logger log = Logger.getLogger(PubdictionCodeController.class);
	
	private CodeService codeService;
	protected Validator validator;

	@InitBinder(StateCodeForm.FORM_NAME)
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
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_VIEW, method = RequestMethod.GET)
	public ModelAndView viewStateCode(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_STATE_CODE, codeService.getAllStateCodes());

		return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_VIEW);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_CREATE, method = RequestMethod.GET)
	public ModelAndView createStateCode(
			@ModelAttribute(StateCodeForm.FORM_NAME) StateCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_CREATE, method = RequestMethod.POST)
	public ModelAndView createStateCodePost(@ModelAttribute(StateCodeForm.FORM_NAME) @Valid StateCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.saveStateCode(form.makeCode());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_STATE_CODE_VIEW));
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_EDIT, method = RequestMethod.GET)
	public ModelAndView editStateCode(@RequestParam Long id,
			@ModelAttribute(StateCodeForm.FORM_NAME) StateCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		StateCode code = codeService.getStateCodeById(id);
		
		if(code != null) {
			model.addAttribute(WebConstants.KEY_STATE_CODE, code);
			form.initialize(code);
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_EDIT, method = RequestMethod.POST)
	public ModelAndView editStateCodePost(@ModelAttribute(StateCodeForm.FORM_NAME) @Valid StateCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.saveStateCode(form.makeCode());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_STATE_CODE_VIEW));
		}
		
		StateCode code = codeService.getStateCodeById(form.getId());
		model.addAttribute(WebConstants.KEY_STATE_CODE, code);
		return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_DELETE, method = RequestMethod.GET)
	public ModelAndView deleteStateCode(@RequestParam Long id,
			@ModelAttribute(StateCodeForm.FORM_NAME) StateCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		StateCode code = codeService.getStateCodeById(id);
		
		if(code != null) {
			model.addAttribute(WebConstants.KEY_STATE_CODE, code);
			form.initialize(code);
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_STATE_CODE_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STATE_CODE_DELETE, method = RequestMethod.POST)
	public ModelAndView deleteStateCodePost(@ModelAttribute(StateCodeForm.FORM_NAME) StateCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		codeService.deleteStateCode(form.makeCode());
		
		// Redirect user
		return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_STATE_CODE_VIEW));
	}

	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
