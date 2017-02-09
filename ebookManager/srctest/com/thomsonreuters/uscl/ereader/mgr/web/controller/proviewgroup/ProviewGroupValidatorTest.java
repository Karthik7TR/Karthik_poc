package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public final class ProviewGroupValidatorTest
{
    private ProviewGroupValidator validator;
    private ProviewGroupListFilterForm filterForm;
    private Errors errors;

    @Before
    public void setUp()
    {
        validator = new ProviewGroupValidator();
        filterForm = new ProviewGroupListFilterForm();
        errors = new BindException(filterForm, "form");
    }

    @Test
    public void testVerifyRemoveReject()
    {
        filterForm.setGroupCmd(GroupCmd.REMOVE);
        validator.validate(filterForm, errors);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertTrue(validator.supports(filterForm.getClass()));
    }
}
