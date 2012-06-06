package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class PublishTypeCodeController {
	//private static final Logger log = Logger.getLogger(PubdictionCodeController.class);
	
	private CodeService codeService;
	protected Validator validator;

	@InitBinder(PublishTypeCodeForm.FORM_NAME)
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
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW, method = RequestMethod.GET)
	public ModelAndView viewPublishTypeCode(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, codeService.getAllPubTypeCodes());

		return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_VIEW);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE, method = RequestMethod.GET)
	public ModelAndView createPublishTypeCode(
			@ModelAttribute(PublishTypeCodeForm.FORM_NAME) PublishTypeCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE, method = RequestMethod.POST)
	public ModelAndView createPublishTypeCodePost(@ModelAttribute(PublishTypeCodeForm.FORM_NAME) @Valid PublishTypeCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.savePubTypeCode(form.makeCode());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW));
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT, method = RequestMethod.GET)
	public ModelAndView editPublishTypeCode(@RequestParam Long id,
			@ModelAttribute(PublishTypeCodeForm.FORM_NAME) PublishTypeCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PubTypeCode code = codeService.getPubTypeCodeById(id);
		
		if(code != null) {
			model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, code);
			form.initialize(code);
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT, method = RequestMethod.POST)
	public ModelAndView editPublishTypeCodePost(@ModelAttribute(PublishTypeCodeForm.FORM_NAME) @Valid PublishTypeCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			codeService.savePubTypeCode(form.makeCode());
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW));
		}
		
		PubTypeCode code = codeService.getPubTypeCodeById(form.getPubTypeId());
		model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, code);
		return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_DELETE, method = RequestMethod.GET)
	public ModelAndView deletePublishTypeCode(@RequestParam Long id,
			@ModelAttribute(PublishTypeCodeForm.FORM_NAME) PublishTypeCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PubTypeCode code = codeService.getPubTypeCodeById(id);
		
		if(code != null) {
			model.addAttribute(WebConstants.KEY_PUB_TYPE_CODE, code);
			form.initialize(code);
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_DELETE, method = RequestMethod.POST)
	public ModelAndView deletePublishTypeCodePost(@ModelAttribute(PublishTypeCodeForm.FORM_NAME) PublishTypeCodeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		codeService.deletePubTypeCode(form.makeCode());
		
		// Redirect user
		return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW));
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
