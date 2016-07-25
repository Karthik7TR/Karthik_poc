/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.generatorswitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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

import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;

@Controller
public class GeneratorSwitchController {
	//private static final Logger log = LogManager.getLogger(StopGeneratorController.class);
	
	private ServerAccessService serverAccessService;
	private Properties generatorProperties;
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
				String serviceError = "";
				List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
				
				try {
				String status = serverAccessService.stopServer(generatorProperties.getProperty("serverNames"), 
								   generatorProperties.getProperty("username"), generatorProperties.getProperty("password"), 
								   generatorProperties.getProperty("appNames"), generatorProperties.getProperty("emailGroup"));
				status = StringUtils.replace(status, "\n", "<br />");
				infoMessages.add(new InfoMessage(InfoMessage.Type.INFO, status));
				} catch(Exception e) {
					serviceError = e.getMessage();
					infoMessages.add(new InfoMessage(InfoMessage.Type.ERROR, serviceError));
				}
				
				model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
				
				// Clear out the code field
				form.setCode("");
			}
			
			return new ModelAndView(WebConstants.VIEW_ADMIN_STOP_GENERATOR);
    }
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_START_GENERATOR, method = RequestMethod.GET)
    public ModelAndView getStartGenerator(
			Model model) throws Exception {
		
           return new ModelAndView(WebConstants.VIEW_ADMIN_START_GENERATOR);
    }
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_START_GENERATOR, method = RequestMethod.POST)
    public ModelAndView postStartGenerator(Model model) throws Exception {
			String serviceError = "";
			List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
			
			try {
			String status = serverAccessService.startServer(generatorProperties.getProperty("serverNames"), 
							   generatorProperties.getProperty("username"), generatorProperties.getProperty("password"), 
							   generatorProperties.getProperty("appNames"), generatorProperties.getProperty("emailGroup"));
			status = StringUtils.replace(status, "\n", "<br />");
			infoMessages.add(new InfoMessage(InfoMessage.Type.INFO, status));
			} catch(Exception e) {
				serviceError = e.getMessage();
				infoMessages.add(new InfoMessage(InfoMessage.Type.ERROR, serviceError));
			}
			
			model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);

			return new ModelAndView(WebConstants.VIEW_ADMIN_START_GENERATOR);
    }
	
	@Required
    public void setServerAccessService(ServerAccessService serverAccessService) {
           this.serverAccessService = serverAccessService;
    }
    
    @Required
    public void setGeneratorProperties(Properties properties) {
           this.generatorProperties = properties;
    }
    
    @Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
