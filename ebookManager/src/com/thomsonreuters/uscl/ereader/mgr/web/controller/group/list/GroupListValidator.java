package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupListFilterForm.GroupCmd;

public class GroupListValidator implements Validator {
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (GroupListFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		GroupListFilterForm form = (GroupListFilterForm) obj;
		
		if ((form.getGroupCmd() == GroupCmd.PROMOTE) ||
			(form.getGroupCmd() == GroupCmd.DELETE) || 
			(form.getGroupCmd() == GroupCmd.REMOVE)) {
			if (form.getGroupIds() == null || form.getGroupIds().size() == 0) {
				errors.reject("error.required.versionselection");
			}
		}
	}
}

