package com.thomsonreuters.uscl.ereader.gather.controller;

import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TocController {
    @RequestMapping("/hello")   
    public ModelAndView helloWorld() { 
    	String message = "This where we will start calling Toc service!"; 
    	return new ModelAndView("Toc services are getting called..", "message", message);     } 
}
