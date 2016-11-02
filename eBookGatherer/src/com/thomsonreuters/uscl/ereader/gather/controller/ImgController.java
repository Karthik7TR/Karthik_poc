/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.services.NovusImgService;

@Controller
public class ImgController {
	private static Logger LOG = LogManager.getLogger(ImgController.class);
	public NovusImgService novusImgService;
	
	public NovusImgService getNovusImgService() {
		return novusImgService;
	}

	@Required
	public void setNovusImgService(NovusImgService novusImgService) {
		this.novusImgService = novusImgService;
	}

	@RequestMapping(value = "/img", method = RequestMethod.POST)
	public ModelAndView fetchImages(@RequestBody GatherImgRequest imgRequest, Model model) {
		LOG.debug(">>> ImgController");
		GatherResponse gatherResponse = new GatherResponse();
		try {
			gatherResponse = novusImgService.getImagesFromNovus(imgRequest.getImgToDocManifestFile(),
					imgRequest.getDynamicImageDirectory(), imgRequest.isFinalStage());
		} catch (Exception e) {
			LOG.error(e);
		}
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		return new ModelAndView(EBConstants.VIEW_RESPONSE);
	}


}
