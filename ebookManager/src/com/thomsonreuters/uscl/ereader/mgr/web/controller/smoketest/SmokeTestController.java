package com.thomsonreuters.uscl.ereader.mgr.web.controller.smoketest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class SmokeTestController {
    private final MiscConfigSyncService miscConfigSyncService;
    private final SmokeTestService smokeTestService;
    private final String environmentName;

    @Autowired
    public SmokeTestController(
        final MiscConfigSyncService miscConfigSyncService,
        final SmokeTestService smokeTestService,
        @Qualifier("environmentName") final String environmentName) {
        this.miscConfigSyncService = miscConfigSyncService;
        this.smokeTestService = smokeTestService;
        this.environmentName = environmentName;
    }

    /**
     * Handle the inbound GET to the smoke test page.
     */
    @RequestMapping(value = WebConstants.MVC_SMOKE_TEST, method = RequestMethod.GET)
    public ModelAndView inboundGet(final HttpSession httpSession, final Model model) {
        final InetAddress proviewHost = miscConfigSyncService.getProviewHost();
        try {
            model.addAttribute("localHost", InetAddress.getLocalHost().getHostName());
        } catch (final UnknownHostException e) {
            log.error(e.getMessage());
        }

        model.addAttribute("date", new Date());
        model.addAttribute("environmentName", environmentName);
        model.addAttribute("proviewHost", proviewHost.getHostName());
        model.addAttribute("novusEnvironment", miscConfigSyncService.getNovusEnvironment().toString());
        model.addAttribute("applications", smokeTestService.getRunningApplications());
        model.addAllAttributes(smokeTestService.getServerStatuses());
        model.addAllAttributes(smokeTestService.getApplicationStatuses());
        model.addAllAttributes(smokeTestService.getDatabaseServerStatuses());
        model.addAttribute("currentProperties", smokeTestService.getExternalSystemsStatuses());

        return new ModelAndView(WebConstants.VIEW_SMOKE_TEST);
    }
}
