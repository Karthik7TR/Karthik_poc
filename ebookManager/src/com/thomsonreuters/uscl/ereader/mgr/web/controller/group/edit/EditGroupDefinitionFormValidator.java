/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm.Version;

@Component("editGroupDefinitionFormValidator")
public class EditGroupDefinitionFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(EditGroupDefinitionFormValidator.class);
	private static final int MAXIMUM_CHARACTER_1024 = 1024;


	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (EditGroupDefinitionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	EditGroupDefinitionForm form = (EditGroupDefinitionForm) obj;
    	
    	checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getComment(), "comment", new Object[] {"Comment", MAXIMUM_CHARACTER_1024});
    	
    	// Group name validations
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "error.required");
    	checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getGroupName(), "groupName", new Object[] {"Group Name", MAXIMUM_CHARACTER_1024});
    	
    	if(Version.NONE.equals(form.getVersionType())) {
    		errors.rejectValue("versionType", "error.required");
    	}

    	Boolean includeSubgroup = form.getIncludeSubgroup();
    	if(includeSubgroup) {
    		Subgroup notGrouped = form.getNotGrouped();
    		if(notGrouped.getTitles().size() > 0) {
    			errors.rejectValue("notGrouped", "error.group.unassigned");
    		}
    		
    		// Validate each subgroup
    		Set<String> subgroupHeadings = new HashSet<String>();
    		List<Subgroup> subgroups = form.getSubgroups();
    		for(int i = 0; i < subgroups.size(); i++) {
    			Subgroup subgroup = subgroups.get(i);
    			
    			String subgroupHeading = subgroup.getHeading();
    			
    			// Validate subgroup heading
    			if(subgroupHeadings.contains(subgroupHeading)) {
					errors.rejectValue("subgroups[" + i + "].heading", "error.duplicate", new Object[] {"Subgroup heading"}, "Duplicate subgroup heading");
				} else {
					subgroupHeadings.add(subgroupHeading);
				}
    			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subgroups[" + i + "].heading", "error.required");
    			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, subgroup.getHeading(), "subgroups[" + i + "].heading", new Object[] {"Subgroup heading", MAXIMUM_CHARACTER_1024});
    			
    			if(subgroup.getTitles().size() == 0) {
    				errors.rejectValue("subgroups[" + i + "].heading", "error.group.subgroup.empty");
    			}
    		}
    	} else {
    		if(form.getHasSplitTitles()) {
    			errors.rejectValue("includeSubgroup", "error.group.split.titles");
    		}
    	}
	}
}
