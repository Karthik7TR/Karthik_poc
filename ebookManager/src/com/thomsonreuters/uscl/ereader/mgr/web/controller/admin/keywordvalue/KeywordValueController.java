package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import java.util.List;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeValueService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class KeywordValueController {

    private final KeywordTypeCodeSevice keywordTypeCodeService;
    private final KeywordTypeValueService keywordTypeValueService;
    private final BookDefinitionService bookService;
    private final Validator validator;

    @Autowired
    public KeywordValueController(
        final KeywordTypeCodeSevice keywordTypeCodeService,
        final KeywordTypeValueService keywordTypeValueService,
        final BookDefinitionService bookService,
        @Qualifier("keywordValueFormValidator") final Validator validator) {
        this.keywordTypeCodeService = keywordTypeCodeService;
        this.keywordTypeValueService = keywordTypeValueService;
        this.bookService = bookService;
        this.validator = validator;
    }

    @InitBinder(KeywordValueForm.FORM_NAME)
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
    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE, method = RequestMethod.GET)
    public ModelAndView createKeywordValue(
        @RequestParam("keywordCodeId") final Long keywordCodeId,
        @ModelAttribute(KeywordValueForm.FORM_NAME) final KeywordValueForm form,
        final Model model) {
        final KeywordTypeCode code = keywordTypeCodeService.getKeywordTypeCodeById(keywordCodeId);
        form.setKeywordTypeCode(code);
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE, method = RequestMethod.POST)
    public ModelAndView createKeywordValuePost(
        @ModelAttribute(KeywordValueForm.FORM_NAME) @Valid final KeywordValueForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            keywordTypeValueService.saveKeywordTypeValue(form.makeKeywordTypeValue());

            // Redirect user
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
        }

        final KeywordTypeCode code = keywordTypeCodeService.getKeywordTypeCodeById(form.getKeywordTypeCode().getId());
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, code);
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_CREATE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT)
    public ModelAndView editKeywordValue(
        @RequestParam("id") final Long id,
        @ModelAttribute(KeywordValueForm.FORM_NAME) final KeywordValueForm form,
        final Model model) {
        final KeywordTypeValue value = keywordTypeValueService.getKeywordTypeValueById(id);

        if (value != null) {
            model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, value.getKeywordTypeCode());
            model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_VALUE, value);
            form.initialize(value);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT, method = RequestMethod.POST)
    public ModelAndView editKeywordValuePost(
        @ModelAttribute(KeywordValueForm.FORM_NAME) @Valid final KeywordValueForm form,
        final BindingResult bindingResult,
        final Model model) {
        if (!bindingResult.hasErrors()) {
            keywordTypeValueService.saveKeywordTypeValue(form.makeKeywordTypeValue());
            return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
        }

        final KeywordTypeValue value = keywordTypeValueService.getKeywordTypeValueById(form.getTypeId());
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, value.getKeywordTypeCode());
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_VALUE, value);
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_EDIT);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_DELETE, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ADMIN_KEYWORD_VALUE_DELETE)
    public ModelAndView deleteKeywordValue(
        @RequestParam("id") final Long id,
        @ModelAttribute(KeywordValueForm.FORM_NAME) final KeywordValueForm form,
        final Model model) {
        log.debug(form.toString());
        final KeywordTypeValue value = keywordTypeValueService.getKeywordTypeValueById(id);
        if (value != null) {
            final List<BookDefinition> books = bookService.findAllBookDefinitionsByKeywordValueId(id);
            model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, value.getKeywordTypeCode());
            model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_VALUE, value);
            model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, books);
            form.initialize(value);
        }
        return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_VALUE_DELETE);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_VALUE_DELETE, method = RequestMethod.POST)
    public ModelAndView deleteKeywordValuePost(
        @ModelAttribute(KeywordValueForm.FORM_NAME) final KeywordValueForm form) {
        final KeywordTypeValue value = form.makeKeywordTypeValue();
        log.debug(form.toString());
        keywordTypeValueService.deleteKeywordTypeValue(value.getId());
        return new ModelAndView(new RedirectView(WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW));
    }
}
