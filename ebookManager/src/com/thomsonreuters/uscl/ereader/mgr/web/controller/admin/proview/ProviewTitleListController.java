/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.proview;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Controller
public class ProviewTitleListController {

	private ProviewClient proviewClient;

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLES, method = RequestMethod.GET)
	public ModelAndView allLatestProviewTitleInfo(Model model)
			throws Exception {

		List<ProviewTitleInfo> allLatestProviewTitleInfo = proviewClient.getAllLatestProviewTitleInfo();
		
		if (allLatestProviewTitleInfo!=null){
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewTitleInfo);
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allLatestProviewTitleInfo.size());
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLES);
	}

	public ProviewClient getProviewClient() {
		return proviewClient;
	}

	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
}
