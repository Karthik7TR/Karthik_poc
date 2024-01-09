package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.doctypemetric;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public final class DocTypeMetricFormValidatorTest {
    private static final String DOCTYPE_CODE_NAME = "test";
    private DocTypeMetricFormValidator validator;
    private DocTypeMetricForm form;
    private Errors errors;

    @Before
    public void setUp() {
        // Setup Validator
        validator = new DocTypeMetricFormValidator();

        form = new DocTypeMetricForm();
        form.setId(Long.valueOf(1));
        form.setName(DOCTYPE_CODE_NAME);
        form.setThresholdPercent(Integer.valueOf(10));
        form.setThresholdValue(Integer.valueOf(10));
        errors = new BindException(form, "form");
    }

    @Test
    public void testNoName() {
        // Check Valid name
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify name requirement
        form.setName(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("name").getCode());
    }

    @Test
    public void testNoThresholdValue() {
        // Check Valid name
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify name requirement
        form.setThresholdValue(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("thresholdValue").getCode());
    }

    @Test
    public void testNoThresholdPercent() {
        // Check Valid name
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify name requirement
        form.setThresholdPercent(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("thresholdPercent").getCode());
    }
}
