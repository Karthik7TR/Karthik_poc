/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.controller;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.util.EBookTocXmlHelper;

@Controller
public class TocController {
	private static Logger log = Logger.getLogger(TocController.class);
	
	@Autowired
	public TocService tocService;

	/**
	 * Fetch the table of contents XML document.
	 */
	@RequestMapping(value = "/toc", method = RequestMethod.POST)
	public ModelAndView getTableOfContents(@RequestBody GatherTocRequest tocRequest, Model model) {
		log.debug(">>> " + tocRequest);
		GatherResponse gatherResponse = new GatherResponse();
		
		// Retrieve TOC structure from Novus
		try {
			List<EBookToc> eBookTocList = tocService.findTableOfContents(tocRequest.getGuid(), tocRequest.getCollectionName());
			// Create EBook TOC file on specified path
			File tocXmlFile = tocRequest.getTocFile();
			try {
				EBookTocXmlHelper.processTocListToCreateEBookTOC(eBookTocList, tocXmlFile);
			} catch (GatherException e) {
				log.error("Error creating TOC XML file: " + tocXmlFile, e);
				gatherResponse = new GatherResponse(GatherResponse.CODE_FILE_ERROR, e.getMessage());
			}
		} catch (GatherException e) {
			log.error("Error fetching TOC from Novus with guid="+tocRequest.getGuid(), e);
			gatherResponse = new GatherResponse(GatherResponse.CODE_NOVUS_ERROR, e.getMessage());
		} 
		
		model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
		return new ModelAndView(EBConstants.VIEW_RESPONSE );
	}
	
	public void setTocService(TocService service) {
		this.tocService = service;
	}
}
