package com.thomsonreuters.uscl.ereader.mgr.web.controller.smoketest;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTest;
import com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestService;


@Controller
public class SmokeTestController {
	//private static final Logger log = Logger.getLogger(SmokeTestController.class);

	private SmokeTestService smokeTestService;
	private String environmentName;
	private String imageVertical;
	private String proviewDomain;
	
	/**
	 * Handle the inbound GET to the smoke test page.
	 */
	@RequestMapping(value = WebConstants.MVC_SMOKE_TEST, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpSession httpSession, Model model) {
		
		try {
			model.addAttribute("localHost", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		// Proview and Image vertical
		List<SmokeTest> statuses = new ArrayList<SmokeTest>();
		URI uri = URI.create(imageVertical);
		
		statuses.add(smokeTestService.getApplicationStatus("Image Vertical", String.format("http://%s/CheckStatus", uri.getAuthority() )));
		statuses.add(smokeTestService.getApplicationStatus("ProView", String.format("http://%s/v1/statuscheck", proviewDomain)));
		statuses.add(smokeTestService.testConnection());
		model.addAttribute("currentProperties", statuses);
		
		model.addAttribute("date", new Date());
		model.addAttribute("applications", smokeTestService.getRunningApplications());
		model.addAttribute("environmentName", environmentName);
		model.addAttribute("proviewDomain", proviewDomain);
		model.addAttribute("ci", smokeTestService.getCIServerStatuses());
		model.addAttribute("ciApps", smokeTestService.getCIApplicationStatuses());
		model.addAttribute("test", smokeTestService.getTestServerStatuses());
		model.addAttribute("testApps", smokeTestService.getTestApplicationStatuses());
		model.addAttribute("qa", smokeTestService.getQAServerStatuses());
		model.addAttribute("qaApps", smokeTestService.getQAApplicationStatuses());
		model.addAttribute("lowerEnvDatabase", smokeTestService.getLowerEnvDatabaseServerStatuses());
		model.addAttribute("prod", smokeTestService.getProdServerStatuses());
		model.addAttribute("prodApps", smokeTestService.getProdApplicationStatuses());
		model.addAttribute("prodDatabase", smokeTestService.getProdDatabaseServerStatuses());

		return new ModelAndView(WebConstants.VIEW_SMOKE_TEST);
	}
	
	@Required
	public void setEnvironmentName(String name) {
		this.environmentName = name;
	}
	
	@Required
	public void setProviewDomain(String domain) {
		this.proviewDomain = domain;
	}
	
	@Required
	public void setImageVertical(String imageVertical) {
		this.imageVertical = imageVertical;
	}
	
	@Required
	public void setSmokeTestService(SmokeTestService service) {
		this.smokeTestService = service;
	}
	
	
}
