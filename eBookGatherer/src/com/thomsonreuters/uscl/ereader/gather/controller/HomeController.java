package com.thomsonreuters.uscl.ereader.gather.controller;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private String environmentName;

    @RequestMapping(value = EBConstants.URI_HOME, method = RequestMethod.GET)
    public ModelAndView home(final Model model) {
        model.addAttribute("environmentName", environmentName);
        return new ModelAndView(EBConstants.VIEW_HOME);
    }

    @Autowired
    public void setEnvironmentName(final String envName) {
        environmentName = envName;
    }
}
