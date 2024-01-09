package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import java.util.List;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
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
public class KeywordCodeController {
    private final KeywordTypeCodeSevice keywordTypeCodeService;
    private final BookDefinitionService bookService;
    private final Validator validator;

    @Autowired
    public KeywordCodeController(
        final KeywordTypeCodeSevice keywordTypeCodeService,
        final BookDefinitionService bookService,
        @Qualifier("keywordCodeFormValidator") final Validator validator) {
        this.keywordTypeCodeService = keywordTypeCodeService;
        this.bookService = bookService;
        this.validator = validator;
    }

    @InitBinder(KeywordCodeForm.FORM_NAME)
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
    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW, method = RequestMethod.GET)
    public ModelAndView viewKeywordsCode(final Model model) {
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, keywordTypeCodeService.getAllKeywordTypeCodes());
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_VIEW);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE, method = RequestMethod.GET)
    public ModelAndView createKeywordCode(
        @ModelAttribute(KeywordCodeForm.FORM_NAME) final KeywordCodeForm form) {
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE, method = RequestMethod.POST)
    public ModelAndView createKeywordCodePost(
        @ModelAttribute(KeywordCodeForm.FORM_NAME) @Valid final KeywordCodeForm form,
        final BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            keywordTypeCodeService.saveKeywordTypeCode(form.makeCode());
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT)
    public ModelAndView editKeywordCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(KeywordCodeForm.FORM_NAME) final KeywordCodeForm form,
        final Model model) {
        final KeywordTypeCode code = keywordTypeCodeService.getKeywordTypeCodeById(id);
        if (code != null) {
            model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT, method = RequestMethod.POST)
    public ModelAndView editKeywordCodePost(
        @ModelAttribute(KeywordCodeForm.FORM_NAME) @Valid final KeywordCodeForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            keywordTypeCodeService.saveKeywordTypeCode(form.makeCode());
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
        }

        final KeywordTypeCode code = keywordTypeCodeService.getKeywordTypeCodeById(form.getCodeId());
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_DELETE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_KEYWORD_CODE_DELETE)
    public ModelAndView deleteKeywordCode(
        @RequestParam("id") final Long id,
        @ModelAttribute(KeywordCodeForm.FORM_NAME) final KeywordCodeForm form,
        final Model model) {
        final KeywordTypeCode code = keywordTypeCodeService.getKeywordTypeCodeById(id);
        if (code != null) {
            final List<BookDefinition> books = bookService.findAllBookDefinitionsByKeywordCodeId(id);
            model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
            model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, books);
            form.initialize(code);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_CODE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_CODE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteKeywordCodePost(@ModelAttribute(KeywordCodeForm.FORM_NAME) final KeywordCodeForm form) {
        keywordTypeCodeService.deleteKeywordTypeCode(form.makeCode().getId());
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
    }
}
