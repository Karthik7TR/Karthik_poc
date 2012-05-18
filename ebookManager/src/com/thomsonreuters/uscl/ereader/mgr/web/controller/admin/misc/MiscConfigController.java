package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

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

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

@Controller
public class MiscConfigController {
	private static final Logger log = Logger.getLogger(MiscConfigController.class);
	/** Hosts to push new configuration to, assume a listening REST service to receive the new configuration. */
	private List<InetSocketAddress> generatorSocketAddrs;
	private List<InetSocketAddress> gathererSocketAddrs;
	private String generatorContextName;
	private String gathererContextName;
	private int generatorPort;
	private int gathererPort;
	private ManagerService managerService;
	private AppConfigService appConfigService;
	private Validator validator;

	@InitBinder(MiscConfigForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_MISC, method = RequestMethod.GET)
	public ModelAndView inboundGet(@ModelAttribute(MiscConfigForm.FORM_NAME) MiscConfigForm form,
								   Model model) throws Exception {
		MiscConfig miscConfig = appConfigService.loadMiscConfig();
		form.initialize(miscConfig);
		return new ModelAndView(WebConstants.VIEW_ADMIN_MISC);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_MISC, method = RequestMethod.POST)
	public ModelAndView submitMiscConfigForm(
					@ModelAttribute(MiscConfigForm.FORM_NAME) @Valid MiscConfigForm form,
					BindingResult errors, Model model) throws Exception {
		List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
		if (!errors.hasErrors()) {
			boolean anySaveErrors = false;
			// Persist the changed configuration
			try {
				MiscConfig miscConfig = form.getMiscConfig();
				appConfigService.saveMiscConfig(miscConfig);  // Persist the changed configuration
				infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved configuration."));
			} catch (Exception e) {
				anySaveErrors = true;
				String errorMessage = String.format("Failed to save new misc configuration - %s", e.getMessage());
				log.error(errorMessage, e);
				infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
			}
			
			// If no data persistence errors, then 
			// Sync the new configuration out to all listening ebookGenerator hosts who care about the change.
			if (!anySaveErrors) {
				// Fetch the complete current state of the application configuration
				MiscConfig miscConfig = appConfigService.loadMiscConfig();
				// Push the config to all generator, gatherer, and manager applications
				for (int i = 0; i < generatorSocketAddrs.size(); i++) {
					InetSocketAddress generatorSocketAddr = generatorSocketAddrs.get(i);
					InetSocketAddress gathererSocketAddr = gathererSocketAddrs.get(i);
					
					pushMiscConfiguration(miscConfig, generatorSocketAddr, generatorContextName, infoMessages);
					pushMiscConfiguration(miscConfig, gathererSocketAddr, gathererContextName, infoMessages);
				}
			}
		}
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
		return new ModelAndView(WebConstants.VIEW_ADMIN_MISC);
	}
	
	private void pushMiscConfiguration(final MiscConfig miscConfig, InetSocketAddress socketAddr, String contextName, List<InfoMessage> infoMessages) {
		String errorMessageTemplate = "Failed to push new misc configuration to %s on host %s - %s";
		try {
			SimpleRestServiceResponse opResponse = managerService.pushMiscConfiguration(miscConfig, contextName, socketAddr);
			if (opResponse.isSuccess()) {
				infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, String.format("Successfully synchronized misc configuration to %s on host %s", contextName, socketAddr)));
			} else {
				String errorMessage = String.format(errorMessageTemplate,
													contextName, socketAddr, opResponse.getMessage());
				log.error("Response failure: " + errorMessage);
				infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = String.format(errorMessageTemplate, contextName, socketAddr, e.getMessage());
			log.error(errorMessage, e);
			infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
		}
	}
	
	/**
	 * Create a list of sockets based on a list of hosts, and a single port number.
	 * Note that this is used by more than just this controller as other controllers also create a socket list to push configurations out to.
	 * @param commaSeparatedHostNames hosts names that should resolve
	 * @param port the application listen port number
	 * @return a list of InetSocketAddress
	 * @throws UnknownHostException if a host name cannot resolve
	 */
	public static List<InetSocketAddress> createSocketAddressList(
					String commaSeparatedHostNames, int port) throws UnknownHostException {
		List<InetSocketAddress> socketAddrs = new ArrayList<InetSocketAddress>();
		StringTokenizer hostTokenizer = new StringTokenizer(commaSeparatedHostNames, ", ");
		while (hostTokenizer.hasMoreTokens()) {
			String hostName = hostTokenizer.nextToken();
			InetSocketAddress socketAddr = new InetSocketAddress(hostName, port);
			if (socketAddr.isUnresolved()) {
				String errorMessage = String.format("Unresolved host socket address <%s>.  Check the environment specific property file and ensure that the CSV generator host names are complete and correct.", socketAddr);
				log.error(errorMessage);
				throw new UnknownHostException(errorMessage);
			}
			socketAddrs.add(socketAddr);
		}	
		return socketAddrs;
	}

	@Required
	public void setGeneratorPort(int port) {
		this.generatorPort = port;
	}
	@Required
	public void setGathererPort(int port) {
		this.gathererPort = port;
	}
	/**
	 * Hosts that receive the REST service push notification that the job throttle configuration has changed.
	 * @param commaSeparatedHostNames a CSV list of valid host names
	 */
	@Required
	public void setHosts(String commaSeparatedHostNames) throws UnknownHostException {
		this.generatorSocketAddrs = createSocketAddressList(commaSeparatedHostNames, generatorPort);
		this.gathererSocketAddrs = createSocketAddressList(commaSeparatedHostNames, gathererPort);
	}
	@Required
	public void setAppConfigService(AppConfigService service) {
		this.appConfigService = service;
	}
	@Required
	public void setManagerService(ManagerService service) {
		this.managerService = service;
	}
	@Required
	public void setGeneratorContextName(String name) {
		this.generatorContextName = name;
	}
	@Required
	public void setGathererContextName(String name) {
		this.gathererContextName = name;
	}
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
