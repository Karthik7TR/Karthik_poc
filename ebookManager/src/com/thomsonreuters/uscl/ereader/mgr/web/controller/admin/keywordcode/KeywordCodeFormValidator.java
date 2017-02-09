package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("keywordCodeFormValidator")
public class KeywordCodeFormValidator extends BaseFormValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(PubdictionCodeFormValidator.class);
    private static final int MAXIMUM_CHARACTER_1024 = 1024;
    private CodeService codeService;

    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (KeywordCodeForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        final KeywordCodeForm form = (KeywordCodeForm) obj;

        final String name = form.getName();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
        checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name, "name", new Object[] {"Name", MAXIMUM_CHARACTER_1024});

        if (!StringUtils.isBlank(name))
        {
            final KeywordTypeCode code = codeService.getKeywordTypeCodeByName(name);
            if (code != null && code.getId() != form.getCodeId())
            {
                errors.rejectValue("name", "error.exist", new Object[] {"Name"}, "Already exists");
            }
        }
    }

    @Required
    public void setCodeService(final CodeService service)
    {
        codeService = service;
    }
}
