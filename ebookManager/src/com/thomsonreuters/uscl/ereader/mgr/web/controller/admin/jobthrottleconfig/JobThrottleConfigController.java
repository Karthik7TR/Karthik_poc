package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import java.net.InetSocketAddress;
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

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.JobThrottleConfigService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

@Controller
public class JobThrottleConfigController {
	private static final Logger log = Logger.getLogger(JobThrottleConfigController.class);
	
	/** Hosts to push new configuration to, assume a listening REST service to receive the new configuration. */
	private List<InetSocketAddress> socketAddrs;
	private int generatorPort;
	private JobThrottleConfigService jobThrottleConfigService;
	private ManagerService managerService;
	private Validator validator;
	
	public JobThrottleConfigController(int generatorPort) {
		this.generatorPort = generatorPort;
	}

	@InitBinder(JobThrottleConfigForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG, method = RequestMethod.GET)
	public ModelAndView inboundGet(@ModelAttribute(JobThrottleConfigForm.FORM_NAME) JobThrottleConfigForm form,
								   Model model) throws Exception {
		JobThrottleConfig databaseJobThrottleConfig = jobThrottleConfigService.getThrottleConfig();
		form.initialize(databaseJobThrottleConfig);
		return new ModelAndView(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG, method = RequestMethod.POST)
	public ModelAndView submitForm(@ModelAttribute(JobThrottleConfigForm.FORM_NAME) @Valid JobThrottleConfigForm form,
			 					   BindingResult errors, Model model) throws Exception {
		log.debug(form);
		List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
		if (!errors.hasErrors()) {
			boolean anySaveErrors = false;
			JobThrottleConfig jobThrottleConfig = form.getJobThrottleConfig();
			// Persist the changed Throttle configuration
			try {
				jobThrottleConfigService.saveJobThrottleConfig(jobThrottleConfig);
				infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved throttle configuration."));
				
			} catch (Exception e) {
				anySaveErrors = true;
				String errorMessage = String.format("Failed to save new throttle configuration - %s", e.getMessage());
				log.error(errorMessage, e);
				infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
			}
			
			// If no data persistence errors, then 
			// Push the new configuration out to all listening ebookGenerator hosts who care about the change.
			if (!anySaveErrors) {
				InetSocketAddress currentSocketAddr = null;
				String errorMessageTemplate = "Failed to push new job throttle configuration to host socket %s - %s";
				for (InetSocketAddress socketAddr : socketAddrs) {
					try {
						currentSocketAddr = socketAddr;
						JobOperationResponse opResponse = managerService.pushJobThrottleConfiguration(socketAddr, jobThrottleConfig);
						if (opResponse.isSuccess()) {
							infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, String.format("Successfully pushed new throttle configuration to: %s", socketAddr)));
						} else {
							String errorMessage = String.format(errorMessageTemplate, socketAddr, opResponse.getMessage());
							log.error("JobOperationResponse failure: " + errorMessage);
							infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
						}
					} catch (Exception e) {
						String errorMessage = String.format(errorMessageTemplate, currentSocketAddr, e.getMessage());
						log.error("Exception occurred: " + errorMessage, e);
						infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
					}
				}
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
	public void setHosts(String commaSeparatedHostNames) throws UnknownHostException {
		this.socketAddrs = new ArrayList<InetSocketAddress>();
		StringTokenizer hostTokenizer = new StringTokenizer(commaSeparatedHostNames, ", ");
		while (hostTokenizer.hasMoreTokens()) {
			String hostName = hostTokenizer.nextToken();
			InetSocketAddress socketAddr = new InetSocketAddress(hostName, generatorPort);
			if (socketAddr.isUnresolved()) {
				String errorMessage = String.format("Unresolved host socket address <%s>.  Check the environment specific property file and ensure that the CSV generator host names are complete and correct.", socketAddr);
				log.error(errorMessage);
				throw new UnknownHostException(errorMessage);
			}
			this.socketAddrs.add(socketAddr);
		}
	}
	@Required
	public void setJobThrottleConfigService(JobThrottleConfigService service) {
		this.jobThrottleConfigService = service;
	}
	@Required
	public void setManagerService(ManagerService service) {
		this.managerService = service;
	}
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
