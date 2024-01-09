package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    //private static final Logger log = LogManager.getLogger(HomeController.class);

    private String environmentName;

    public HomeController(final String envName) {
        environmentName = envName;
    }

    @RequestMapping(value = WebConstants.URI_HOME, method = RequestMethod.GET)
    public ModelAndView home(final Model model) {
        //log.debug(">>> environment=" + environmentName);
        model.addAttribute("environmentName", environmentName);
        return new ModelAndView(WebConstants.VIEW_HOME);
    }
}
