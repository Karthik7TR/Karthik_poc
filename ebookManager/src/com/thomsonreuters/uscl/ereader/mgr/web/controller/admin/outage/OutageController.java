package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage.Operation;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc.MiscConfigController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class OutageController
{
    private static final Logger log = LogManager.getLogger(OutageController.class);

    private final ManagerService managerService;
    private final OutageService outageService;
    private final Validator validator;
    private final List<InetSocketAddress> generatorSocketAddrs;

    @Autowired
    public OutageController(final ManagerService managerService,
                            final OutageService outageService,
                            @Qualifier("outageFormValidator") final Validator validator,
                            @Value("${generator.hosts}") final String commaSeparatedHostNames,
                            @Value("${generator.port}") final int generatorPort) throws UnknownHostException
    {
        this.managerService = managerService;
        this.outageService = outageService;
        this.validator = validator;
        generatorSocketAddrs = MiscConfigController.createSocketAddressList(commaSeparatedHostNames, generatorPort);
    }

    @InitBinder(OutageForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST, method = RequestMethod.GET)
    public ModelAndView getActiveList(final Model model, final HttpSession session)
    {
        // Get InfoMessages from session
        final List<InfoMessage> infoMessages =
            (List<InfoMessage>) session.getAttribute(WebConstants.KEY_PLANNED_OUTAGE_INFO_MESSAGES);
        session.removeAttribute(WebConstants.KEY_PLANNED_OUTAGE_INFO_MESSAGES); // Clear message out of the session
        if (infoMessages != null)
        {
            model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
        }

        model.addAttribute(WebConstants.KEY_OUTAGE, outageService.getAllActiveAndScheduledPlannedOutages());

        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_ACTIVE_LIST);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_FULL_LIST, method = RequestMethod.GET)
    public ModelAndView getFullList(final Model model)
    {
        model.addAttribute(WebConstants.KEY_OUTAGE, outageService.getAllPlannedOutages());

        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_FULL_LIST);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_CREATE, method = RequestMethod.GET)
    public ModelAndView createOutage(
        @ModelAttribute(OutageForm.FORM_NAME) final OutageForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        model.addAttribute("outageType", outageService.getAllOutageType());
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_CREATE, method = RequestMethod.POST)
    public ModelAndView createOutagePost(
        @ModelAttribute(OutageForm.FORM_NAME) @Valid final OutageForm form,
        final BindingResult bindingResult,
        final Model model,
        final HttpSession session) throws Exception
    {
        if (!bindingResult.hasErrors())
        {
            final PlannedOutage outage = form.createPlannedOutage();
            outage.setOperation(Operation.SAVE);
            session.setAttribute(
                WebConstants.KEY_PLANNED_OUTAGE_INFO_MESSAGES,
                submitPlannedOutage(bindingResult, model, outage));
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST));
        }

        model.addAttribute("outageType", outageService.getAllOutageType());
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_EDIT, method = RequestMethod.GET)
    public ModelAndView editOutage(
        @RequestParam("id") final Long id,
        @ModelAttribute(OutageForm.FORM_NAME) final OutageForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception
    {
        final PlannedOutage outage = outageService.findPlannedOutageByPrimaryKey(id);

        if (outage != null)
        {
            model.addAttribute(WebConstants.KEY_OUTAGE, outage);
            form.initialize(outage);
        }
        model.addAttribute("outageType", outageService.getAllOutageType());
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_EDIT, method = RequestMethod.POST)
    public ModelAndView editOutagePost(
        @ModelAttribute(OutageForm.FORM_NAME) @Valid final OutageForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception
    {
        final PlannedOutage outage = form.createPlannedOutage();
        outage.setOperation(Operation.SAVE);
        model.addAttribute(WebConstants.KEY_INFO_MESSAGES, submitPlannedOutage(bindingResult, model, outage));
        model.addAttribute(WebConstants.KEY_OUTAGE, outage);
        model.addAttribute("outageType", outageService.getAllOutageType());
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_DELETE, method = RequestMethod.GET)
    public ModelAndView deleteOutage(
        @RequestParam("id") final Long id,
        @ModelAttribute(OutageForm.FORM_NAME) final OutageForm form,
        final BindingResult bindingResult,
        final Model model)
    {
        final PlannedOutage outage = outageService.findPlannedOutageByPrimaryKey(id);

        if (outage != null)
        {
            model.addAttribute(WebConstants.KEY_OUTAGE, outage);
            form.initialize(outage);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_OUTAGE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteOutagePost(
        @ModelAttribute(OutageForm.FORM_NAME) final OutageForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception
    {
        final PlannedOutage outage = form.createPlannedOutage();
        outage.setOperation(Operation.REMOVE);

        model.addAttribute(WebConstants.KEY_INFO_MESSAGES, submitPlannedOutage(bindingResult, model, outage));
        model.addAttribute(WebConstants.KEY_OUTAGE, outage);
        return new ModelAndView(WebConstants.VIEW_ADMIN_OUTAGE_DELETE);
    }

    public List<InfoMessage> submitPlannedOutage(
        final BindingResult bindingResult,
        final Model model,
        PlannedOutage outage)
    {
        final List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();
        if (!bindingResult.hasErrors())
        {
            boolean anySaveErrors = false;
            // Persist/delete the outage
            try
            {
                if (outage.getOperation() == Operation.SAVE)
                {
                    // Retrieve the outage from database to get the status of emails sent if already present
                    if (outage.getId() != null)
                    {
                        final PlannedOutage persistentOutage =
                            outageService.findPlannedOutageByPrimaryKey(outage.getId());
                        outage.setAllClearEmailSent(persistentOutage.isAllClearEmailSent());
                        outage.setNotificationEmailSent(persistentOutage.isNotificationEmailSent());
                    }

                    outageService.savePlannedOutage(outage);

                    // Get latest outage to marshall to generator
                    outage = outageService.findPlannedOutageByPrimaryKey(outage.getId());
                    outage.setOperation(Operation.SAVE);
                    infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved planned outage."));
                }
                else
                {
                    // Get latest outage to marshall to generator
                    final PlannedOutage latestOutage = outageService.findPlannedOutageByPrimaryKey(outage.getId());
                    // Only replace if the outage entity exists.
                    if (latestOutage != null)
                    {
                        outage = latestOutage;
                    }
                    outage.setOperation(Operation.REMOVE);
                    outageService.deletePlannedOutage(outage.getId());
                    infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully deleted planned outage."));
                }
            }
            catch (final Exception e)
            {
                anySaveErrors = true;
                final String errorMessage;
                if (outage.getOperation() == Operation.SAVE)
                {
                    errorMessage = String.format("Failed to save planned outage - %s", e.getMessage());
                }
                else
                {
                    errorMessage = String.format("Failed to delete planned outage - %s", e.getMessage());
                }
                log.error(errorMessage, e);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }

            // If no data persistence errors, then
            // Push/synchronize the outage out to all listening ebookGenerator hosts who care about the change.
            if (!anySaveErrors)
            {
                // Push the config to all generator applications
                for (final InetSocketAddress generatorSocketAddr : generatorSocketAddrs)
                {
                    pushPlannedOutage(outage, generatorSocketAddr, infoMessages);
                }
            }
        }
        return infoMessages;
    }

    private void pushPlannedOutage(
        final PlannedOutage outage,
        final InetSocketAddress socketAddr,
        final List<InfoMessage> infoMessages)
    {
        final String errorMessageTemplate = "Failed to push outage to host %s - %s";
        try
        {
            final SimpleRestServiceResponse opResponse = managerService.pushPlannedOutage(outage, socketAddr);
            if (opResponse.isSuccess())
            {
                infoMessages.add(
                    new InfoMessage(
                        InfoMessage.Type.SUCCESS,
                        String.format("Successfully pushed planned outage to host %s", socketAddr)));
            }
            else
            {
                final String errorMessage = String.format(errorMessageTemplate, socketAddr, opResponse.getMessage());
                log.error("Response failure: " + errorMessage);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }
        }
        catch (final Exception e)
        {
            final String errorMessage = String.format(errorMessageTemplate, socketAddr, e.getMessage());
            log.error(errorMessage, e);
            infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
        }
    }

    /* AJAX call to dismiss the outage warning. Puts a boolean in session to not show
     * the outage.
     * @return boolean
     */
    @RequestMapping(value = WebConstants.MVC_DISMISS_OUTAGE, method = RequestMethod.POST)
    @ResponseBody
    public void dismissOutage(final HttpSession session)
    {
        log.debug("<<< Ajax call for outage");
        session.setAttribute(WebConstants.KEY_DISMISS_OUTAGE, true);
    }
}
