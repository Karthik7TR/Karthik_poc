package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobThrottleConfigService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;

@Controller
public class JobThrottleConfigController {
	private static final Logger log = Logger.getLogger(JobThrottleConfigController.class);
	
	/** Hosts to push new configuration to, assume a listening REST service to receive the new configuration. */
	private List<InetAddress> hosts;  
	private JobThrottleConfigService jobThrottleConfigService;
	private Validator validator;

	@InitBinder(JobThrottleConfigForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG, method = RequestMethod.GET)
	public ModelAndView inboundGet(@ModelAttribute(JobThrottleConfigForm.FORM_NAME) JobThrottleConfigForm form,
								   Model model) throws Exception {
		JobThrottleConfig jobThrottleConfig = jobThrottleConfigService.getThrottleConfig();
		form.initialize(jobThrottleConfig);
		model.addAttribute(WebConstants.KEY_JOB_THROTTLE_CONFIG, jobThrottleConfig);
		return new ModelAndView(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG, method = RequestMethod.POST)
	public ModelAndView submitForm(@ModelAttribute(JobThrottleConfigForm.FORM_NAME) @Valid JobThrottleConfigForm form,
			 					   BindingResult errors, Model model) throws Exception {
		log.debug(form);
		List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
		if (!errors.hasErrors()) {
			JobThrottleConfig jobThrottleConfig = form.createJobThrottleConfig();
			// Persist the changed Throttle configuration
			try {
				jobThrottleConfigService.saveJobThrottleConfig(jobThrottleConfig);
				infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved throttle configuration."));
				
				// Push the new configuration out to all listening ebookGenerator hosts who care about the update
				for (InetAddress host : hosts) {
					try {
						jobThrottleConfigService.pushConfiguration(host, jobThrottleConfig);
						infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, String.format("Successfully pushed new throttle configuration to host: %s", host)));
					} catch (Exception e) {
						String errorMessage = String.format("Failed to push new throttle configuration to host: %s", host);
						log.error(errorMessage, e);
						infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
					}
				}
			} catch (Exception e) {
				String errorMessage = String.format("Failed to save new throttle configuration.  %s", e.getMessage());
				log.error(errorMessage, e);
				infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
			}
		}
		
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
		return new ModelAndView(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG);
	}

	/**
	 * Hosts that receive the REST service push notification that the job throttle configuration has changed.
	 * @param commaSeparatedHostNames a CSV list of valid host names
	 */
	@Required
	public void setHostNames(String commaSeparatedHostNames) throws UnknownHostException {
		this.hosts = new ArrayList<InetAddress>();
		StringTokenizer tokenizer = new StringTokenizer(commaSeparatedHostNames, ", ");
		while (tokenizer.hasMoreTokens()) {
			String hostName = tokenizer.nextToken();
			try {
				InetAddress host = InetAddress.getByName(hostName);
				hosts.add(host);
			} catch (UnknownHostException e) {
				log.error(String.format("Unknown host <%s> in the job throttle configuration host name list.  Check the environment specific Spring properties file and ensure that the list of generator instance host names is correct and complete.", hostName), e);
				throw e;
			}
		}
	}
	@Required
	public void setJobThrottleConfigService(JobThrottleConfigService service) {
		this.jobThrottleConfigService = service;
	}
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
