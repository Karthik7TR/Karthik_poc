package com.thomsonreuters.uscl.ereader.mgr.web.controller.smoketest;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SmokeTestController
{
    private static final Logger log = LogManager.getLogger(SmokeTestController.class);

    private MiscConfigSyncService miscConfigSyncService;
    private SmokeTestService smokeTestService;
    private String environmentName;
    private String imageVertical;

    /**
     * Handle the inbound GET to the smoke test page.
     */
    @RequestMapping(value = WebConstants.MVC_SMOKE_TEST, method = RequestMethod.GET)
    public ModelAndView inboundGet(final HttpSession httpSession, final Model model)
    {
        final InetAddress proviewHost = miscConfigSyncService.getProviewHost();
        try
        {
            model.addAttribute("localHost", InetAddress.getLocalHost().getHostName());
        }
        catch (final UnknownHostException e)
        {
            log.error(e);
        }

        // Proview and Image vertical
        final List<SmokeTest> statuses = new ArrayList<>();
        final URI uri = URI.create(imageVertical);

        statuses.add(
            smokeTestService.getApplicationStatus(
                "Image Vertical",
                String.format("http://%s/image/v1/StatusCheck", uri.getAuthority())));
        statuses.add(
            smokeTestService
                .getApplicationStatus("ProView", String.format("http://%s/v1/statuscheck", proviewHost.getHostName())));
        statuses.add(smokeTestService.testConnection());
        model.addAttribute("currentProperties", statuses);

        model.addAttribute("date", new Date());
        model.addAttribute("applications", smokeTestService.getRunningApplications());
        model.addAttribute("environmentName", environmentName);
        model.addAttribute("proviewHost", proviewHost.getHostName());
        model.addAttribute("novusEnvironment", miscConfigSyncService.getNovusEnvironment().toString());
        model.addAttribute("ci", smokeTestService.getCIServerStatuses());
        model.addAttribute("ciApps", smokeTestService.getCIApplicationStatuses());
        model.addAttribute("test", smokeTestService.getTestServerStatuses());
        model.addAttribute("testApps", smokeTestService.getTestApplicationStatuses());
        model.addAttribute("qa", smokeTestService.getQAServerStatuses());
        model.addAttribute("qaApps", smokeTestService.getQAApplicationStatuses());
        model.addAttribute("qaNew", smokeTestService.getQANewServerStatuses());
        model.addAttribute("qaNewApps", smokeTestService.getQANewApplicationStatuses());
        model.addAttribute("lowerEnvDatabase", smokeTestService.getLowerEnvDatabaseServerStatuses());
        model.addAttribute("prod", smokeTestService.getProdServerStatuses());
        model.addAttribute("prodApps", smokeTestService.getProdApplicationStatuses());
        model.addAttribute("prodNew", smokeTestService.getProdNewServerStatuses());
        model.addAttribute("prodNewApps", smokeTestService.getProdNewApplicationStatuses());
        model.addAttribute("prodDatabase", smokeTestService.getProdDatabaseServerStatuses());

        return new ModelAndView(WebConstants.VIEW_SMOKE_TEST);
    }

    @Required
    public void setEnvironmentName(final String name)
    {
        environmentName = name;
    }

    @Required
    public void setImageVertical(final String imageVertical)
    {
        this.imageVertical = imageVertical;
    }

    @Required
    public void setSmokeTestService(final SmokeTestService service)
    {
        smokeTestService = service;
    }

    @Required
    public void setMiscConfigSyncService(final MiscConfigSyncService service)
    {
        miscConfigSyncService = service;
    }
}
