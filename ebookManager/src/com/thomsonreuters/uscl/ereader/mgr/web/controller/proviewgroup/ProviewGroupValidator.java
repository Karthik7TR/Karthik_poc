package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ProviewGroupValidator implements Validator
{
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (ProviewGroupListFilterForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        final ProviewGroupListFilterForm form = (ProviewGroupListFilterForm) obj;

        if ((form.getGroupCmd() == GroupCmd.PROMOTE)
            || (form.getGroupCmd() == GroupCmd.DELETE)
            || (form.getGroupCmd() == GroupCmd.REMOVE))
        {
            if (form.getGroupMembers() == null || form.getGroupMembers().size() == 0)
            {
                if (!form.isGroupOperation())
                {
                    errors.reject("error.required.versionselection");
                }
            }
        }
    }
}
