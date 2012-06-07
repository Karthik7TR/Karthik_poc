package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
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

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class OutageTypeController {
	private static final Logger log = Logger.getLogger(OutageTypeController.class);
	
	private OutageService outageService;
	protected Validator validator;
	

	@InitBinder(OutageTypeForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST, method = RequestMethod.GET)
	public ModelAndView getOutageTypeList(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_OUTAGE, outageService.getAllOutageType());

		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_LIST);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_CREATE, method = RequestMethod.GET)
	public ModelAndView createOutageType(
			@ModelAttribute(OutageTypeForm.FORM_NAME) OutageTypeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_CREATE, method = RequestMethod.POST)
	public ModelAndView createOutageTypePost(@ModelAttribute(OutageTypeForm.FORM_NAME) @Valid OutageTypeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		log.debug(form);
		
		if(!bindingResult.hasErrors()){
			OutageType outageType = form.createOutageType();
			outageService.saveOutageType(outageType);
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST));
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_EDIT, method = RequestMethod.GET)
	public ModelAndView editOutageType(@RequestParam Long id,
			@ModelAttribute(OutageTypeForm.FORM_NAME) OutageTypeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		OutageType outageType = outageService.findOutageTypeByPrimaryKey(id);
		
		if(outageType != null) {
			model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
			form.initialize(outageType);
		}
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_EDIT, method = RequestMethod.POST)
	public ModelAndView editOutageTypePost(@ModelAttribute(OutageTypeForm.FORM_NAME) @Valid OutageTypeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		log.debug(form);
		
		OutageType outageType = form.createOutageType();
		
		if(!bindingResult.hasErrors()){
			outageService.saveOutageType(outageType);
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST));
		}

		model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_DELETE, method = RequestMethod.GET)
	public ModelAndView deleteOutageType(@RequestParam Long id,
			@ModelAttribute(OutageTypeForm.FORM_NAME) OutageTypeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		OutageType outageType = outageService.findOutageTypeByPrimaryKey(id);
		
		if(outageType != null) {
			model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
			model.addAttribute("numberOfPlannedOutages", outageType.getPlannedOutage().size());
			form.initialize(outageType);
		}
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_TYPE_DELETE, method = RequestMethod.POST)
	public ModelAndView deleteOutageTypePost(@ModelAttribute(OutageTypeForm.FORM_NAME) OutageTypeForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		log.debug(form);
		
		OutageType outageType = form.createOutageType();
		if(!bindingResult.hasErrors()){
			outageService.deleteOutageType(outageType.getId());
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST));
		}

		model.addAttribute(WebConstants.KEY_OUTAGE, outageType);
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_TYPE_DELETE);
	}

	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}

	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
