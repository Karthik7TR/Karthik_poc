package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * In-bound (get method) controller for the Access Denied page.
 * This is where the user is sent to if they try and access a page/protected resource and they are not
 * in the proper role.
 */
@Controller
public class AccessDeniedController
{
    //private static final Logger log = LogManager.getLogger(AccessDeniedController.class);

    @RequestMapping(WebConstants.MVC_SEC_ACCESS_DENIED)
    public ModelAndView inboundGet()
    {
        return new ModelAndView(WebConstants.VIEW_SEC_ACCESS_DENIED);
    }
}

