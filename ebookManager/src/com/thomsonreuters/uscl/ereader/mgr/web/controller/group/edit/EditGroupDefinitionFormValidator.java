package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm.Version;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("editGroupDefinitionFormValidator")
public class EditGroupDefinitionFormValidator extends BaseFormValidator implements Validator {
    //private static final Logger log = LogManager.getLogger(EditGroupDefinitionFormValidator.class);
    private static final int MAXIMUM_CHARACTER_1024 = 1024;

    @Override
    public boolean supports(final Class<?> clazz) {
        return (EditGroupDefinitionForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final EditGroupDefinitionForm form = (EditGroupDefinitionForm) obj;

        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_1024,
            form.getComment(),
            "comment",
            new Object[] {"Comment", MAXIMUM_CHARACTER_1024});

        // Group name validations
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "error.required");
        checkMaxLength(
            errors,
            MAXIMUM_CHARACTER_1024,
            form.getGroupName(),
            "groupName",
            new Object[] {"Group Name", MAXIMUM_CHARACTER_1024});

        if (Version.NONE.equals(form.getVersionType())) {
            errors.rejectValue("versionType", "error.required");
        }

        final Boolean includeSubgroup = form.getIncludeSubgroup();
        if (includeSubgroup) {
            final Subgroup notGrouped = form.getNotGrouped();
            if (notGrouped.getTitles().size() > 0) {
                errors.rejectValue("notGrouped", "error.group.unassigned");
            }

            // Validate each subgroup
            final Set<String> subgroupHeadings = new HashSet<String>();
            final List<Subgroup> subgroups = form.getSubgroups();
            for (int i = 0; i < subgroups.size(); i++) {
                final Subgroup subgroup = subgroups.get(i);

                final String subgroupHeading = subgroup.getHeading();

                // Validate subgroup heading
                if (subgroupHeadings.contains(subgroupHeading)) {
                    errors.rejectValue(
                        "subgroups[" + i + "].heading",
                        "error.duplicate",
                        new Object[] {"Subgroup heading"},
                        "Duplicate subgroup heading");
                } else {
                    subgroupHeadings.add(subgroupHeading);
                }
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subgroups[" + i + "].heading", "error.required");
                checkMaxLength(
                    errors,
                    MAXIMUM_CHARACTER_1024,
                    subgroup.getHeading(),
                    "subgroups[" + i + "].heading",
                    new Object[] {"Subgroup heading", MAXIMUM_CHARACTER_1024});

                if (subgroup.getTitles().size() == 0) {
                    errors.rejectValue("subgroups[" + i + "].heading", "error.group.subgroup.empty");
                }
            }
        } else {
            if (form.getHasSplitTitles()) {
                errors.rejectValue("includeSubgroup", "error.group.split.titles");
            }
        }
    }
}
