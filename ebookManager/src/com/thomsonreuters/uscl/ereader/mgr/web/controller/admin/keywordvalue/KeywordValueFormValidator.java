package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("keywordValueFormValidator")
public class KeywordValueFormValidator extends BaseFormValidator implements Validator {
    private static final int MAXIMUM_CHARACTER_1024 = 1024;
    private final CodeService codeService;

    @Autowired
    public KeywordValueFormValidator(final CodeService codeService) {
        this.codeService = codeService;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return (KeywordValueForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final KeywordValueForm form = (KeywordValueForm) obj;

        final String name = form.getName();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
        checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name, "name", new Object[] {"Name", MAXIMUM_CHARACTER_1024});

        final KeywordTypeCode code = codeService.getKeywordTypeCodeById(form.getKeywordTypeCode().getId());
        if (code != null) {
            for (final KeywordTypeValue value : code.getValues()) {
                if (value.getName().equalsIgnoreCase(name)) {
                    errors.rejectValue(
                        "name",
                        "error.exist.keyword",
                        new Object[] {"Name"},
                        "Already exists in this Keyword Code");
                }
            }
        } else {
            errors.rejectValue("name", "error.invalid", new Object[] {"Keyword Code"}, "Invalid Keyword Code");
        }
    }
}
