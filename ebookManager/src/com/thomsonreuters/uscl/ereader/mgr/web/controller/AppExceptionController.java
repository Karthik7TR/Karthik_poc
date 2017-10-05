package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles the web container throwing a bubbled-up application exception and displays
 * the exception stack trace on its own page.
 */
@Controller
public class AppExceptionController {
    // private static final Logger log = LogManager.getLogger(AppExceptionController.class);

    @RequestMapping(value = WebConstants.MVC_APP_EXCEPTION, method = RequestMethod.GET)
    public ModelAndView handleException(final HttpServletRequest request, final HttpServletResponse response) {
        return new ModelAndView(WebConstants.VIEW_APP_EXCEPTION);
    }
}
