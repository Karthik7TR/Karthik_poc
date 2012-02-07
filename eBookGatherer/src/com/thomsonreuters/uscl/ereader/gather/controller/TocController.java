/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.controller;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;

@Controller
public class TocController {
	private static Logger LOG = Logger.getLogger(TocController.class);
	
	@Autowired
	public TocService tocService;

	/**
	 * Fetch the table of contents XML document.
	 */
	@RequestMapping(value = "/toc", method = RequestMethod.POST)
	public ModelAndView getTableOfContents(@RequestBody GatherTocRequest tocRequest, Model model) {
		LOG.debug(">>> " + tocRequest);
		GatherResponse gatherResponse = new GatherResponse();
		
		// Retrieve TOC structure from Novus
		try {
			File tocXmlFile = tocRequest.getTocFile();

			tocService.findTableOfContents(tocRequest.getGuid(), tocRequest.getCollectionName(), tocXmlFile);
			// Create EBook TOC file on specified path
		} catch (GatherException e) {
			String errorMessage = e.getMessage();
			Throwable cause = e.getCause();
			if (cause != null) {
				errorMessage = errorMessage + " - " + cause.getMessage();
			}
			LOG.error(errorMessage);
			gatherResponse = new GatherResponse(e.getErrorCode(), errorMessage);
			} 
		catch (Exception e) {
			String errorMessage = e.getMessage();
			Throwable cause = e.getCause();
			if (cause != null) {
				errorMessage = errorMessage + " - " + cause.getMessage();
			}
			LOG.error(errorMessage);
			gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, errorMessage);
			} 
		
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		return new ModelAndView(EBConstants.VIEW_RESPONSE );
	}
	
	public void setTocService(TocService service) {
		this.tocService = service;
	}
}
