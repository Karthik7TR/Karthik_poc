package com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition;

import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.CombinedBookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
public class CombinedBookDefinitionController {
    private static final String PARAM_TEMPLATE = "?%s=%s";
    private final CombinedBookDefinitionService combinedBookDefinitionService;
    private final CombinedBookDefinitionFormValidator combinedBookDefinitionFormValidator;

    @Autowired
    public CombinedBookDefinitionController(final CombinedBookDefinitionService combinedBookDefinitionService,
                                            final CombinedBookDefinitionFormValidator combinedBookDefinitionFormValidator) {
        this.combinedBookDefinitionService = combinedBookDefinitionService;
        this.combinedBookDefinitionFormValidator = combinedBookDefinitionFormValidator;
    }

    @InitBinder(CombinedBookDefinitionForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(combinedBookDefinitionFormValidator);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView viewCombinedBookDefinition(
            @RequestParam("id") final Long id,
            final Model model) {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(id);
        model.addAttribute(WebConstants.KEY_COMBINED_BOOK_DEFINITION, combinedBookDefinition);
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView editCombinedBookDefinitionGet(
            @RequestParam("id") final Long id,
            @ModelAttribute CombinedBookDefinitionForm combinedBookDefinitionForm) {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(id);
        combinedBookDefinitionForm.initialize(combinedBookDefinition);
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_EDIT, method = RequestMethod.POST)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView editCombinedBookDefinitionPost(
            @ModelAttribute @Valid CombinedBookDefinitionForm combinedBookDefinitionForm,
            final BindingResult bindingResult) {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(combinedBookDefinitionForm.getId());
        if (combinedBookDefinition == null) {
            return new ModelAndView(WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND);
        }
        if (!bindingResult.hasErrors()) {
            combinedBookDefinitionForm.loadCombinedBookDefinition(combinedBookDefinition);
            combinedBookDefinitionService.saveCombinedBookDefinition(combinedBookDefinition);
            return new ModelAndView(new RedirectView(WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW + appendIdParam(combinedBookDefinitionForm.getId())));
        }
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_CREATE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView createCombinedBookDefinition(@ModelAttribute CombinedBookDefinitionForm combinedBookDefinitionForm) {
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_CREATE, method = RequestMethod.POST)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView createCombinedBookDefinitionPost(
            @ModelAttribute @Valid CombinedBookDefinitionForm combinedBookDefinitionForm,
            final BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            CombinedBookDefinition combinedBookDefinition = new CombinedBookDefinition();
            combinedBookDefinitionForm.loadCombinedBookDefinition(combinedBookDefinition);
            combinedBookDefinition = combinedBookDefinitionService.saveCombinedBookDefinition(combinedBookDefinition);
            return new ModelAndView(new RedirectView(WebConstants.MVC_COMBINED_BOOK_DEFINITION_VIEW + appendIdParam(combinedBookDefinition.getId())));
        }
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_CREATE);
    }

    private String appendIdParam(final Long id) {
        return String.format(PARAM_TEMPLATE, WebConstants.KEY_ID, id);
    }
}
