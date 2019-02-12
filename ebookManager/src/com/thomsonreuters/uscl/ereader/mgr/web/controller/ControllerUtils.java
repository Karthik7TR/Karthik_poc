package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

/**
 * Provides method which handles ProviewExcepiton.
 */
@Slf4j
public final class ControllerUtils {
    public static final String PROVIEW_ERROR_MESSAGE = "Proview Exception occured. Please contact your administrator.";

    private ControllerUtils() { }

    public static ModelAndView handleRequestWithProviewMessage(final Model model, final String viewName, final FunctionThrowing requestHandler) {
        return handleRequest(viewName, requestHandler, model);
    }

    public static ModelAndView handleRequest(final FunctionThrowing requestHandler, final String viewName) {
        return handleRequest(viewName, requestHandler, null);
    }

    private static ModelAndView handleRequest(final String viewName, final FunctionThrowing requestHandler, final Model model) {
        try {
            requestHandler.run();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            if (model != null) {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, PROVIEW_ERROR_MESSAGE);
            }
        }
        return new ModelAndView(viewName);
    }

    @FunctionalInterface
    public interface FunctionThrowing {
        void run() throws Exception;
    }
}
