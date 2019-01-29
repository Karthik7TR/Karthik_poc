package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

/**
 * Provides method which handles ProviewExcepiton.
 */
@Slf4j
public abstract class BaseProviewListController {
    public static final String PROVIEW_ERROR_MESSAGE = "Proview Exception occured. Please contact your administrator.";

    public <F> ModelAndView wrapToProviewExceptionHandler(final FunctionThrowing<F> requestHandler, final F form, final HttpSession session, final Model model, final String viewName) {
        try {
            requestHandler.apply(form, session, model);
        } catch (final ProviewException e) {
            log.error(e.getMessage(), e);
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, PROVIEW_ERROR_MESSAGE);
        }
        return new ModelAndView(viewName);
    }

    @FunctionalInterface
    public interface FunctionThrowing<F> {
        void apply(F form, HttpSession httpSession, Model model) throws ProviewException;
    }
}
