/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.controller;

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
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.service.NovusImageService;

@Controller
public class ImgController {
	private static Logger LOG = LogManager.getLogger(ImgController.class);

	private NovusImageService novusImageService;
	private ImageRequestParameters parameters;

	@RequestMapping(value = "/img", method = RequestMethod.POST)
	public ModelAndView fetchImages(@RequestBody GatherImgRequest imgRequest, Model model) {
		LOG.debug(">>> ImgController");

		parameters.setDocToImageManifestFile(imgRequest.getImgToDocManifestFile());
		parameters.setDynamicImageDirectory(imgRequest.getDynamicImageDirectory());
		parameters.setFinalStage(imgRequest.isFinalStage());

		GatherResponse gatherResponse;
		try {
			gatherResponse = novusImageService.getImagesFromNovus(parameters);
		} catch (GatherException e) {
			gatherResponse = new GatherResponse();
			LOG.error(e);
		}
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		return new ModelAndView(EBConstants.VIEW_RESPONSE);
	}

	@Required
	public void setNovusImageService(NovusImageService novusImageService) {
		this.novusImageService = novusImageService;
	}

	@Required
	public void setParameters(ImageRequestParameters parameters) {
		this.parameters = parameters;
	}

}
