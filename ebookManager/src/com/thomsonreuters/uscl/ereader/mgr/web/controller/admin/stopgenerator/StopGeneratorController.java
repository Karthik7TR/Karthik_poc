package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.stopgenerator;

import java.util.Properties;

import javax.validation.Valid;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class StopGeneratorController {
	//private static final Logger log = Logger.getLogger(StopGeneratorController.class);
	
	private ServerAccessService serverAccessService;
	private Properties killSwitchProperties;
	protected Validator validator;

	@InitBinder(StopGeneratorForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 * Only Super users allowed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_ADMIN_STOP_GENERATOR, method = RequestMethod.GET)
    public ModelAndView getStopGenerator(@ModelAttribute(StopGeneratorForm.FORM_NAME) StopGeneratorForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
           return new ModelAndView(WebConstants.VIEW_ADMIN_STOP_GENERATOR);
    }
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_STOP_GENERATOR, method = RequestMethod.POST)
    public ModelAndView postStopGenerator(@ModelAttribute(StopGeneratorForm.FORM_NAME) @Valid StopGeneratorForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
			if(!bindingResult.hasErrors()) {
				serverAccessService.stopServer(killSwitchProperties.getProperty("serverNames"), 
				   killSwitchProperties.getProperty("username"), killSwitchProperties.getProperty("password"), 
				   killSwitchProperties.getProperty("appNames"), killSwitchProperties.getProperty("emailGroup"));
				
				return new ModelAndView(new RedirectView(WebConstants.VIEW_ADMIN_MAIN));
			}
			
			return new ModelAndView(WebConstants.VIEW_ADMIN_STOP_GENERATOR);
    }
	
	@Required
    public void setServerAccessService(ServerAccessService serverAccessService) {
           this.serverAccessService = serverAccessService;
    }
    
    @Required
    public void setKillSwitchProperties(Properties properties) {
           this.killSwitchProperties = properties;
    }
    
    @Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
