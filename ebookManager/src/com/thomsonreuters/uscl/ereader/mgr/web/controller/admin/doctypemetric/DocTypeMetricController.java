package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DocTypeMetricController {
    private final DocumentTypeCodeService documentTypeCodeService;
    private final Validator validator;

    @Autowired
    public DocTypeMetricController(
        final DocumentTypeCodeService documentTypeCodeService,
        @Qualifier("docTypeMetricFormValidator") final Validator validator) {
        this.documentTypeCodeService = documentTypeCodeService;
        this.validator = validator;
    }

    @InitBinder(DocTypeMetricForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setValidator(validator);
    }

    /**
     * Handle initial in-bound HTTP get request to the page.
     * No query string parameters are expected.
     * Only Super users allowed
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW, method = RequestMethod.GET)
    public ModelAndView viewKeywordsCode(final Model model) {
        model.addAttribute(WebConstants.KEY_DOC_TYPE_CODE, documentTypeCodeService.getAllDocumentTypeCodes());
        return new ModelAndView(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT)
    public ModelAndView editDocTypeMetric(
        @RequestParam("id") final Long id,
        @ModelAttribute(DocTypeMetricForm.FORM_NAME) final DocTypeMetricForm form,
        final Model model) {
        final DocumentTypeCode code = documentTypeCodeService.getDocumentTypeCodeById(id);

        if (code != null) {
            model.addAttribute(WebConstants.KEY_DOC_TYPE_CODE, code);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT, method = RequestMethod.POST)
    public ModelAndView editDocTypeMetricPost(
        @ModelAttribute(DocTypeMetricForm.FORM_NAME) @Valid final DocTypeMetricForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            final DocumentTypeCode code = documentTypeCodeService.getDocumentTypeCodeById(form.getId());
            documentTypeCodeService.saveDocumentTypeCode(form.makeCode(code));
            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_DOCTYPE_METRIC_VIEW));
        }

        final DocumentTypeCode code = documentTypeCodeService.getDocumentTypeCodeById(form.getId());
        model.addAttribute(WebConstants.KEY_DOC_TYPE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_DOCTYPE_METRIC_EDIT);
    }
}
