package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.Subgroup;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm.Version;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionFormValidator;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.Title;

public class EditGroupDefinitionFormValidatorTest {
	private static final String CHARACTER_1025 = "1234567890123456789012345678901234567890123456789012345678901234567890"
			+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
			+ "678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
			+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
			+ "678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
			+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
			+ "678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
			+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
			+ "678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
			+ "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345"
			+ "6789012345";
	
	private EditGroupDefinitionForm form;
	private EditGroupDefinitionFormValidator validator;
	private Errors errors;
	
	@Before
	public void setup() {
		validator= new EditGroupDefinitionFormValidator();
		form = new EditGroupDefinitionForm();
		errors = new BindException(form, "form");
	}

	@Test
	public void groupNameTest() {
		// verify errors
		validator.validate(form, errors);
		Assert.assertEquals("error.required", errors.getFieldError("groupName").getCode());
	}
	
	@Test
	public void maxLengthTest() {
		form.setGroupName(CHARACTER_1025);
		form.setComment(CHARACTER_1025);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.max.length", errors.getFieldError("groupName").getCode());
		Assert.assertEquals("error.max.length", errors.getFieldError("comment").getCode());
	}
	
	@Test
	public void splitTitleTest() {
		form.setIncludeSubgroup(false);
		form.setHasSplitTitles(true);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.group.split.titles", errors.getFieldError("includeSubgroup").getCode());
	}
	
	@Test
	public void noVersionTypeTest() {
		form.setVersionType(Version.NONE);
		form.setHasSplitTitles(true);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required", errors.getFieldError("versionType").getCode());
	}
	
	@Test
	public void titlesNotAssignedTest() {
		form.setIncludeSubgroup(true);
		
		Subgroup subgroup = new Subgroup();
		subgroup.addTitle(new Title());
		form.setNotGrouped(subgroup);
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.group.unassigned", errors.getFieldError("notGrouped").getCode());
	}
	
	@Test
	public void emptySubgroupTest() {
		form.setIncludeSubgroup(true);
		
		Subgroup subgroup = new Subgroup();
		subgroup.setHeading("1");
		Subgroup subgroup2 = new Subgroup();
		subgroup2.setHeading("2");
		form.setSubgroups(Arrays.asList(subgroup, subgroup2));
		
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.group.subgroup.empty", errors.getFieldError("subgroups[0].heading").getCode());
		Assert.assertEquals("error.group.subgroup.empty", errors.getFieldError("subgroups[1].heading").getCode());
	}
	
	@Test
	public void duplicateSubgroupHeadingTest() {
		String heading = "heading";
		form.setIncludeSubgroup(true);
		
		Subgroup subgroup = new Subgroup();
		subgroup.setHeading(heading);
		subgroup.addTitle(new Title());
		
		Subgroup subgroup2 = new Subgroup();
		subgroup2.setHeading(heading);
		subgroup2.addTitle(new Title());
		form.setSubgroups(Arrays.asList(subgroup, subgroup2));
		
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.duplicate", errors.getFieldError("subgroups[1].heading").getCode());
	}
	
	@Test
	public void maxLengthSubgroupHeadingTest() {
		form.setIncludeSubgroup(true);
		
		Subgroup subgroup = new Subgroup();
		subgroup.setHeading(CHARACTER_1025);
		subgroup.addTitle(new Title());
		form.setSubgroups(Arrays.asList(subgroup));
		
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.max.length", errors.getFieldError("subgroups[0].heading").getCode());
	}
	
	@Test
	public void emptySubgroupHeadingTest() {
		form.setIncludeSubgroup(true);
		
		Subgroup subgroup = new Subgroup();
		subgroup.addTitle(new Title());
		form.setSubgroups(Arrays.asList(subgroup));
		
		validator.validate(form, errors);
		Assert.assertTrue(errors.hasErrors());
		Assert.assertEquals("error.required", errors.getFieldError("subgroups[0].heading").getCode());
	}
	
}
