/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;

@Controller
public class DocController {
	private static Logger LOG = Logger.getLogger(DocController.class);

	public DocService docService;

	@RequestMapping(value = "/doc", method = RequestMethod.POST)
	public ModelAndView fetchDocuments(@RequestBody GatherDocRequest docRequest, Model model) {
		LOG.debug(">>> " + docRequest);
		GatherResponse gatherResponse = new GatherResponse();
		try {
			gatherResponse = docService.fetchDocuments(docRequest.getGuids(), docRequest.getCollectionName(),
					   				  docRequest.getContentDestinationDirectory(),
					   				  docRequest.getMetadataDestinationDirectory());
		} catch (GatherException e) {
			String errorMessage = e.getMessage();
			Throwable cause = e.getCause();
			if (cause != null) {
				errorMessage = errorMessage + " - " + cause.getMessage();
			}
			LOG.error(errorMessage);
			gatherResponse.setErrorCode(e.getErrorCode());
			gatherResponse.setErrorMessage(errorMessage);
		}
		catch (Exception e) {
			String errorMessage = e.getMessage();
			Throwable cause = e.getCause();
			if (cause != null) {
				errorMessage = errorMessage + " - " + cause.getMessage();
			}
			LOG.error(errorMessage);
			gatherResponse.setErrorCode(GatherResponse.CODE_UNHANDLED_ERROR);
			gatherResponse.setErrorMessage(errorMessage);
			} 
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		return new ModelAndView(EBConstants.VIEW_RESPONSE );
	}

	@Required
	public void setDocService(DocService service) {
		this.docService = service;
	}
}
