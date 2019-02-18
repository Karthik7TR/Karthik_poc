package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;

/**
 * Provides method which handles ProviewExcepiton.
 */
@Slf4j
public final class ControllerUtils {
    public static final String PROVIEW_ERROR_MESSAGE = "Proview Exception occured. Please contact your administrator.";

    private ControllerUtils() { }

    public static ModelAndView handleRequest(final FunctionThrowing requestHandler, final Runnable onExceptionOccurred, final String viewName) {
        return handleRequestCommon(requestHandler, onExceptionOccurred, viewName);
    }

    public static ModelAndView handleRequest(final FunctionThrowing requestHandler, final String viewName) {
        return handleRequestCommon(requestHandler, () -> { }, viewName);
    }

    private static ModelAndView handleRequestCommon(final FunctionThrowing requestHandler, final Runnable onExceptionOccurred, final String viewName) {
        try {
            requestHandler.run();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            onExceptionOccurred.run();
        }
        return new ModelAndView(viewName);
    }

    @FunctionalInterface
    public interface FunctionThrowing {
        void run() throws Exception;
    }
}
