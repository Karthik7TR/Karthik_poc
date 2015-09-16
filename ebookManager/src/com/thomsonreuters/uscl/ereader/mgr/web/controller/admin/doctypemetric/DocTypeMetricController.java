package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

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

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class DocTypeMetricController {
	private CodeService codeService;
	protected Validator validator;

	@InitBinder(DocTypeMetricForm.FORM_NAME)
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
	@RequestMapping(value = WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW, method = RequestMethod.GET)
	public ModelAndView viewKeywordsCode(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_DOC_TYPE_CODE, codeService.getAllDocumentTypeCodes());

		return new ModelAndView(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_VIEW);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT, method = RequestMethod.GET)
	public ModelAndView editDocTypeMetric(@RequestParam("id") Long id,
			@ModelAttribute(DocTypeMetricForm.FORM_NAME) DocTypeMetricForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		DocumentTypeCode code = codeService.getDocumentTypeCodeById(id);
		
		if (code != null) {
			model.addAttribute(WebConstants.KEY_DOC_TYPE_CODE, code);
			form.initialize(code);
		}
		
		return new ModelAndView(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT, method = RequestMethod.POST)
	public ModelAndView editDocTypeMetricPost(@ModelAttribute(DocTypeMetricForm.FORM_NAME) @Valid DocTypeMetricForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		if(!bindingResult.hasErrors()) {
			DocumentTypeCode code = codeService.getDocumentTypeCodeById(form.getId());
			
			codeService.saveDocumentTypeMetric(form.makeCode(code));
			
			// Redirect user
			return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW));
		}
		
		DocumentTypeCode code = codeService.getDocumentTypeCodeById(form.getId());
		model.addAttribute(WebConstants.KEY_DOC_TYPE_CODE, code);
		return new ModelAndView(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT);
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
