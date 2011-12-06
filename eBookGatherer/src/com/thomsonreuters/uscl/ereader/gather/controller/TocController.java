package com.thomsonreuters.uscl.ereader.gather.controller;

import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TocController {
    @RequestMapping("/SubmitToc")   
    public ModelAndView getTocData() { 
    	String message = "This where we will start calling Toc service!";
    	System.out.println("Request is reaching controller");
    	return new ModelAndView("SubmitToc", "message", message);     } 
}
