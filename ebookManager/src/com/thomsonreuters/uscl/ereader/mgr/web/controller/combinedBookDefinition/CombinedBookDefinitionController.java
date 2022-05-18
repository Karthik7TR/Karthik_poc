package com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.service.CombinedBookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
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

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_DELETE_ERROR, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_DELETE)
    public ModelAndView viewCombinedBookDefinitionDeleteError(
            @RequestParam("id") final Long id,
            final Model model) {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(id);
        model.addAttribute(WebConstants.KEY_COMBINED_BOOK_DEFINITION, combinedBookDefinition);
        return new ModelAndView(WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_DELETE);
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

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_DELETE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView deleteCombinedBookDefinition(
            @RequestParam("id") final Long id,
            final Model model) {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(id);
        model.addAttribute(WebConstants.KEY_COMBINED_BOOK_DEFINITION, combinedBookDefinition);
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_DELETE, method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteCombinedBookDefinitionDelete(
            @RequestParam("id") final Long id)  {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(id);
        if (combinedBookDefinition == null) {
            return new ResponseEntity<>("Combined book definition does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        CombinedBookDefinitionSource primaryBookDefinitionSource = combinedBookDefinition.getPrimaryTitle();
        BookDefinition primaryBook = primaryBookDefinitionSource.getBookDefinition();
        //Primary Book should not be published to Proview
        if (combinedBookDefinition != null && "Ready".equalsIgnoreCase(primaryBook.getBookStatus()) && primaryBook.getPublishedOnceFlag()) {
            return new ResponseEntity<>("Combined book definition primary title is published to Proview. Cannot delete Combined Book.",
                    HttpStatus.NOT_FOUND);
        }
        //Instead of soft delete, delete CombinedBookDefinition and CASCADE ALL CombinedBookDefinitionSource
        combinedBookDefinitionService.deleteCombinedBookDefinition(combinedBookDefinition);
        return new ResponseEntity<>("Combined book definition deleted successfully.",
                HttpStatus.OK);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_RESTORE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_COMBINED_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView restoreCombinedBookDefinition(
            @RequestParam("id") final Long id,
            final Model model) {
        CombinedBookDefinition combinedBookDefinition = combinedBookDefinitionService.findCombinedBookDefinitionById(id);
        model.addAttribute(WebConstants.KEY_COMBINED_BOOK_DEFINITION, combinedBookDefinition);
        return new ModelAndView(WebConstants.VIEW_COMBINED_BOOK_DEFINITION_RESTORE);
    }

    @RequestMapping(value = WebConstants.MVC_COMBINED_BOOK_DEFINITION_RESTORE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> restoreCombinedBookDefinitionPost(
            @RequestParam("id") final Long id) {
        combinedBookDefinitionService.updateDeletedStatus(id, false);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String appendIdParam(final Long id) {
        return String.format(PARAM_TEMPLATE, WebConstants.KEY_ID, id);
    }
}
