package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.generatorswitch;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GeneratorSwitchController {
    private final ServerAccessService serverAccessService;
    private final Validator validator;
    private final String serverNames;
    private final String userName;
    private final String password;
    private final String appNames;
    private final String emailGroup;

    @Autowired
    public GeneratorSwitchController(
        final ServerAccessService serverAccessService,
        @Qualifier("killSwitchFormValidator") final Validator validator,
        @Value("${generator.hosts}") final String serverNames,
        @Value("${server.username}") final String userName,
        @Value("${server.password}") final String password,
        @Value("${kill.app.names}") final String appNames,
        @Value("${kill.email.group}") final String emailGroup) {
        this.serverAccessService = serverAccessService;
        this.validator = validator;
        this.serverNames = serverNames;
        this.userName = userName;
        this.password = password;
        this.appNames = appNames;
        this.emailGroup = emailGroup;
    }

    @InitBinder(StopGeneratorForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
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
    public ModelAndView getStopGenerator(
        @ModelAttribute(StopGeneratorForm.FORM_NAME) final StopGeneratorForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception {
        return new ModelAndView(WebConstants.VIEW_ADMIN_STOP_GENERATOR);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_STOP_GENERATOR, method = RequestMethod.POST)
    public ModelAndView postStopGenerator(
        @ModelAttribute(StopGeneratorForm.FORM_NAME) @Valid final StopGeneratorForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            String serviceError = "";
            final List<InfoMessage> infoMessages = new ArrayList<InfoMessage>();

            try {
                String status = serverAccessService.stopServer(serverNames, userName, password, appNames, emailGroup);
                status = StringUtils.replace(status, "\n", "<br />");
                infoMessages.add(new InfoMessage(InfoMessage.Type.INFO, status));
            } catch (final Exception e) {
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
    public ModelAndView getStartGenerator(final Model model) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_START_GENERATOR);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_START_GENERATOR, method = RequestMethod.POST)
    public ModelAndView postStartGenerator(final Model model) throws Exception {
        String serviceError = "";
        final List<InfoMessage> infoMessages = new ArrayList<>();

        try {
            String status = serverAccessService.startServer(serverNames, userName, password, appNames, emailGroup);
            status = StringUtils.replace(status, "\n", "<br />");
            infoMessages.add(new InfoMessage(InfoMessage.Type.INFO, status));
        } catch (final Exception e) {
            serviceError = e.getMessage();
            infoMessages.add(new InfoMessage(InfoMessage.Type.ERROR, serviceError));
        }

        model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);

        return new ModelAndView(WebConstants.VIEW_ADMIN_START_GENERATOR);
    }
}
