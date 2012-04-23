package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.main;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.service.ServerAccessService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class AdminController {
	//private static final Logger log = Logger.getLogger(AdminController.class);
	
	private ServerAccessService serverAccessService; 
	private String serverNames = "c111jffctasdx";//"c111heyctasqx";
	private String userName = "asadmin";
	private String password = "east";
	private String appNames = "eBookGatherer,eBookGenerator";
	private String emailGroup = "Thomson.eBookGenerator-Dev@thomsonreuters.com";

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 * Only Super users allowed
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_ADMIN_MAIN, method = RequestMethod.GET)
	public ModelAndView admin() throws Exception {

		return new ModelAndView(WebConstants.VIEW_ADMIN_MAIN);
	}

	@RequestMapping(value = "KillServer", method = RequestMethod.GET)
	public ModelAndView killServer() throws Exception {
		System.out.println(" Admin Controller killServer() ");
		serverAccessService.stopServer(serverNames, userName, password, appNames, emailGroup);
		System.out.println(" Admin Controller killServer() done ");
		return new ModelAndView(WebConstants.VIEW_ADMIN_MAIN);
	}

	
	@Required
	public void setServerAccessService(ServerAccessService serverAccessService) {
		this.serverAccessService = serverAccessService;
	}

	
}
