/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jibx.binding.model.ModelVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.EbookRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.util.EBookTocXmlHelper;

@Controller
public class TocController {

	@Autowired
	public TocService tocService;

	@Autowired
	public DocService docService;

	@Autowired
	public EBookTocXmlHelper eBookTocXmlHelper;

	/**
	 * This method would not get called as of now.its ment to handle
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTocData", method = RequestMethod.GET)
	public ModelAndView setUpFormData() {
		String message = "Dummy method nothing will get done here";

		return new ModelAndView("getTocData", "message", message);
	}

	/**
	 * This method assist in processing form data ,
	 * 
	 * @param ebookRequest
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/getTocData", method = RequestMethod.POST)
	public ModelAndView getTocData(@ModelAttribute("ebookRequest") EbookRequest ebookRequest,BindingResult result) {
		 HttpServletResponse httpResponce;
		
		List<EBookToc> eBookTocList = new ArrayList<EBookToc>();
		
		
		/*** retrieve TOC structure form Novus ***/
		try {
			
			eBookTocList = tocService.getTocDataFromNovus(ebookRequest.getGuid(), ebookRequest.getCollection());
			
		} catch (GatherException e1) {
			/**
			 * TODO: communicate exception to response 1) Toc not found 2)
			 * Failed to retrieve TOC child element.
			 */
			System.out.println("Exception in controller ="+e1);
			e1.printStackTrace();

		} 

		
		
		/*** retrieve actual document temporary  ***/
		if (ebookRequest.getDocFilePath() != "") {
			docService.getDocFromNovus(tocService.getDocuments(),ebookRequest.getCollection(),ebookRequest.getDocFilePath());
		}
		
		
		
		/*** Create EBook TOC file on specified path  ***/
		try {
			eBookTocXmlHelper.processTocListToCreateEBookTOC(eBookTocList,ebookRequest.getTocFilePath());
		} catch (GatherException e) {
			/**
			 * TODO communicate exception to response 1)Failed to create DOM
			 * object ... 2)Failed while printing DOM to specified path 3) Failed to find file path...
			 */
			System.out.println("Exception in controller ="+e);
			e.printStackTrace();
		}
//		httpResponce.sendError(HttpServletResponse.SC_ACCEPTED); //
//		httpResponce.setStatus(404);
//		httpResponce.
		
		 
		// HTTP Status code 503
		return new ModelAndView();
		//return new ModelAndView("tocRequest", "command", ebookRequest);

	}

	/**
	 * Index.jsp is the default starting point for the web application , which
	 * will forward request to /tocData url. which internally mapped to this
	 * method.This method forwards populates empty form object/domain object
	 * EbookRequest to populate form on tocData.jsp
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tocRequest", method = RequestMethod.GET)
	public ModelAndView showTocDataForm() {
		EbookRequest firstEbookRequest = new EbookRequest();
		firstEbookRequest.setCollection("w_an_rcc_cajur_toc"); 
		firstEbookRequest.setGuid("I7b3ec600675a11da90ebf04471783734");
		firstEbookRequest.setTocFilePath(EBConstants.OUTPUT_TOC_FILE);
		return new ModelAndView("tocRequest", "command", firstEbookRequest);
	}
}
