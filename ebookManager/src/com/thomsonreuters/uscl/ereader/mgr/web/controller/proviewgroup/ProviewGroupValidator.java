package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;



public class ProviewGroupValidator implements Validator {
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (ProviewGroupListFilterForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		ProviewGroupListFilterForm form = (ProviewGroupListFilterForm) obj;
		
		if ((form.getGroupCmd() == GroupCmd.PROMOTE) ||
			(form.getGroupCmd() == GroupCmd.DELETE) || 
			(form.getGroupCmd() == GroupCmd.REMOVE) ) {
			if (form.getGroupIds() == null || form.getGroupIds().size() == 0)  {
				if (!form.isGroupOperation()){
					errors.reject("error.required.versionselection");
				}
			}
		}
	}
}
