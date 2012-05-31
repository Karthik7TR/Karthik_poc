package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage.Operation;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc.MiscConfigController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

@Controller
public class OutageController {
	private static final Logger log = Logger.getLogger(OutageController.class);
	
	/** Hosts to push new configuration to, assume a listening REST service that receives outages. */
	private List<InetSocketAddress> generatorSocketAddrs;
	private int generatorPort;
	
	private ManagerService managerService;
	private OutageService outageService;
	protected Validator validator;
	
	public OutageController(int generatorPort) {
		this.generatorPort = generatorPort;
	}

	@InitBinder(OutageForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST, method = RequestMethod.GET)
	public ModelAndView getActiveList(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_OUTAGE, outageService.getAllActiveAndScheduledPlannedOutages());

		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_ACTIVE_LIST);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_FULL_LIST, method = RequestMethod.GET)
	public ModelAndView getFullList(Model model) throws Exception {
		
		model.addAttribute(WebConstants.KEY_OUTAGE, outageService.getAllPlannedOutages());

		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_FULL_LIST);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_CREATE, method = RequestMethod.GET)
	public ModelAndView createOutage(
			@ModelAttribute(OutageForm.FORM_NAME) OutageForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		model.addAttribute("outageType", outageService.getAllOutageType());
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_CREATE, method = RequestMethod.POST)
	public ModelAndView createOutagePost(@ModelAttribute(OutageForm.FORM_NAME) @Valid OutageForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PlannedOutage outage = form.createPlannedOutage();
		outage.setOperation(Operation.SAVE);
		submitPlannedOutage(bindingResult, model, outage);
		model.addAttribute("outageType", outageService.getAllOutageType());
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_CREATE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_EDIT, method = RequestMethod.GET)
	public ModelAndView editOutage(@RequestParam Long id,
			@ModelAttribute(OutageForm.FORM_NAME) OutageForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PlannedOutage outage = outageService.findPlannedOutageByPrimaryKey(id);
		
		if(outage != null) {
			model.addAttribute(WebConstants.KEY_OUTAGE, outage);
			form.initialize(outage);
		}
		model.addAttribute("outageType", outageService.getAllOutageType());
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_EDIT, method = RequestMethod.POST)
	public ModelAndView editOutagePost(@ModelAttribute(OutageForm.FORM_NAME) @Valid OutageForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PlannedOutage outage = form.createPlannedOutage();
		outage.setOperation(Operation.SAVE);
		submitPlannedOutage(bindingResult, model, outage);
		model.addAttribute(WebConstants.KEY_OUTAGE, outage);
		model.addAttribute("outageType", outageService.getAllOutageType());
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_EDIT);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_DELETE, method = RequestMethod.GET)
	public ModelAndView deleteOutage(@RequestParam Long id,
			@ModelAttribute(OutageForm.FORM_NAME) OutageForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PlannedOutage outage = outageService.findPlannedOutageByPrimaryKey(id);
		
		if(outage != null) {
			model.addAttribute(WebConstants.KEY_OUTAGE, outage);
			form.initialize(outage);
		}
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_DELETE);
	}
	
	@RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_DELETE, method = RequestMethod.POST)
	public ModelAndView deleteOutagePost(@ModelAttribute(OutageForm.FORM_NAME) OutageForm form,
			BindingResult bindingResult,
			Model model) throws Exception {
		
		PlannedOutage outage = form.createPlannedOutage();
		outage.setOperation(Operation.REMOVE);
		submitPlannedOutage(bindingResult, model, outage);

		model.addAttribute(WebConstants.KEY_OUTAGE, outage);
		return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_DELETE);
	}
	
	public void submitPlannedOutage(BindingResult bindingResult, Model model, PlannedOutage outage) throws Exception {
		List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
		if (!bindingResult.hasErrors()) {
			boolean anySaveErrors = false;
			// Persist/delete the outage
			try {
				if(outage.getOperation() == Operation.SAVE) {
					// Retrieve the outage from database to get the status of emails sent if already present
					if(outage.getId() != null) {
						PlannedOutage persistentOutage = outageService.findPlannedOutageByPrimaryKey(outage.getId());
						outage.setAllClearEmailSent(persistentOutage.isAllClearEmailSent());
						outage.setNotificationEmailSent(persistentOutage.isNotificationEmailSent());
					}
					
					outageService.savePlannedOutage(outage);
					
					// Get latest outage to marshall to generator
					outage = outageService.findPlannedOutageByPrimaryKey(outage.getId());
					infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved outage."));
				} else {
					// Get latest outage to marshall to generator
					outage = outageService.findPlannedOutageByPrimaryKey(outage.getId());
					outageService.deletePlannedOutage(outage.getId());
					infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully deleted outage."));
				}
			} catch (Exception e) {
				anySaveErrors = true;
				String errorMessage;
				if(outage.getOperation() == Operation.SAVE) {
					errorMessage = String.format("Failed to save outage - %s", e.getMessage());
				} else {
					errorMessage = String.format("Failed to delete outage - %s", e.getMessage());
				}
				log.error(errorMessage, e);
				infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
			}
			
			// If no data persistence errors, then 
			// Push/synchronize the outage out to all listening ebookGenerator hosts who care about the change.
			if (!anySaveErrors) {
				// Push the config to all generator applications
				for (InetSocketAddress generatorSocketAddr: generatorSocketAddrs) {
					pushPlannedOutage(outage, generatorSocketAddr, infoMessages);
				}
			}
		}
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
	}
	
	private void pushPlannedOutage(final PlannedOutage outage, InetSocketAddress socketAddr, List<InfoMessage> infoMessages) {
		String errorMessageTemplate = "Failed to push outage to host %s - %s";
		try {
			SimpleRestServiceResponse opResponse = managerService.pushPlannedOutage(outage, socketAddr);
			if (opResponse.isSuccess()) {
				infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, String.format("Successfully pushed outage to host %s", socketAddr)));
			} else {
				String errorMessage = String.format(errorMessageTemplate, socketAddr, opResponse.getMessage());
				log.error("Response failure: " + errorMessage);
				infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
			}
		} catch (Exception e) {
			String errorMessage = String.format(errorMessageTemplate, socketAddr, e.getMessage());
			log.error(errorMessage, e);
			infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
		}
	}
	
	/**
	 * Generator hosts that receive the REST service push notification when outage schedule has changed.
	 * @param commaSeparatedHostNames a CSV list of valid host names
	 */
	@Required
	public void setGeneratorHosts(String commaSeparatedHostNames) throws UnknownHostException {
		this.generatorSocketAddrs = MiscConfigController.createSocketAddressList(commaSeparatedHostNames, generatorPort);
	}

	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
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
