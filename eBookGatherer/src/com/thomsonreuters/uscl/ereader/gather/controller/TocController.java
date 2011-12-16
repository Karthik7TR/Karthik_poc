/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.EbookRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.Toc;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;

@Controller
public class TocController {
	
	@Autowired
	public TocService tocService;
	
	/**
	 * This method would not get called as of now.its ment to handle 
	 * @return
	 */
	@RequestMapping(value = "/getTocData", method = RequestMethod.GET)
	public ModelAndView setUpFormData() 
	{
		String message = "Dummy method nothing will get done here";

		return new ModelAndView("getTocData", "message", message);
	}
	
	/**
	 * This method assist in processing form data ,  
	 * @param ebookRequest
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/getTocData", method = RequestMethod.POST)
	public String getTocData(@ModelAttribute("ebookRequest") EbookRequest ebookRequest,
							BindingResult result) 
	{

		List<Toc> tocGuidList = tocService.getTocData(ebookRequest.getGuid(),ebookRequest.getCollection());
		System.out.println("tocGuidList being return in Controller :"+tocGuidList);
		return "redirect:tocData.html";
	}
	
	/**
	 * Index.jsp is the default starting point for the web application , which will forward request to /tocData url. 
	 * which internally mapped to this method.This method forwards populates empty form object/domain object 
	 * EbookRequest to populate form on tocData.jsp  
	 * @return
	 */
	@RequestMapping(value ="/tocRequest",method = RequestMethod.GET)
	public ModelAndView showTocDataForm() 
	{
		EbookRequest firstEbookRequest = new EbookRequest();
		firstEbookRequest.setCollection("w_codesstawip");
		firstEbookRequest.setGuid("N04767C6077B911DAA16E8D4AC7636430");
		return new ModelAndView("tocRequest", "command", firstEbookRequest);
	}
}
