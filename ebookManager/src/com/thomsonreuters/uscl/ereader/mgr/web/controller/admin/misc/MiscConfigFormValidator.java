package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("miscConfigFormValidator")
public class MiscConfigFormValidator extends BaseFormValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return (MiscConfigForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final MiscConfigForm form = (MiscConfigForm) obj;
        try {
            InetAddress.getByName(form.getProviewHostname());
        } catch (final UnknownHostException e) {
            final Object[] args = {form.getProviewHostname()};
            errors.reject("error.unknown.proview.host", args, "Unknown/Invalid Proview Host");
        }
    }
}
