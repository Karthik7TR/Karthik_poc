package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("stateCodeFormValidator")
public class StateCodeFormValidator extends BaseFormValidator implements Validator
{
    private static final int MAXIMUM_CHARACTER_1024 = 1024;
    private StateCodeService stateCodeService;

    @Override
    public boolean supports(final Class clazz)
    {
        return StateCodeForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        final StateCodeForm form = (StateCodeForm) obj;

        final String name = form.getName();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
        checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name, "name", new Object[] {"Name", MAXIMUM_CHARACTER_1024});
        checkForSpaces(errors, name, "name", "Name");
        checkSpecialCharacters(errors, name, "name", true);

        if (!StringUtils.isBlank(name))
        {
            final StateCode code = stateCodeService.getStateCodeByName(name);
            if (code != null && !code.getId().equals(form.getStateId()))
            {
                errors.rejectValue("name", "error.exist", new Object[] {"Name"}, "Already exists");
            }
        }
    }

    @Required
    public void setStateCodeService(final StateCodeService service)
    {
      stateCodeService = service;
    }
}
