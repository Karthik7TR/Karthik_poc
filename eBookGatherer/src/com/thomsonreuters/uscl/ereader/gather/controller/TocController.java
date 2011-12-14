/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.EbookRequest;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;

@Controller
public class TocController {
	
	@Autowired
	public TocService tocService;
	
	/**
	 * this method would not get called as of now.
	 * @return
	 */
	@RequestMapping(value = "/getTocData", method = RequestMethod.GET)
	public ModelAndView setUpFormData() {
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
			BindingResult result) {
		System.out.println("Content Name:" + ebookRequest.getContentType());
		System.out.println("Guid:" + ebookRequest.getGuid());
		System.out.println("collection:" + ebookRequest.getCollection());
		// TODO: start calling toc service for retrieving 		
		return "redirect:tocData.html";
	}
	/**
	 * starting point of the application is index.jsp this jsp will forward control to this method 
	 * which will be forwarded to tocData form.  
	 * @return
	 */
	@RequestMapping(value ="/tocData",method = RequestMethod.GET)
	public ModelAndView showTocDataForm() {
		return new ModelAndView("tocData", "command", new EbookRequest());
	}
}
