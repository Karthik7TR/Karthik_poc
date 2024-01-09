package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppExceptionController {
    //private static final Logger log = LogManager.getLogger(AppExceptionController.class);

    @RequestMapping(value = WebConstants.URI_APP_EXCEPTION, method = RequestMethod.GET)
    public ModelAndView handleAppException() {
        return new ModelAndView(WebConstants.VIEW_APP_EXCEPTION);
    }
}
