package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode;

import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public final class StateCodeFormValidatorTest {
    private static final String STATE_NAME = "test";
    private StateCodeService mockStateCodeService;
    private StateCodeFormValidator validator;
    private StateCodeForm form;
    private Errors errors;

    @Before
    public void setUp() {
        // Mock up the service
        mockStateCodeService = EasyMock.createMock(StateCodeService.class);

        // Setup Validator
        validator = new StateCodeFormValidator(mockStateCodeService);

        form = new StateCodeForm();
        form.setStateId(1L);
        form.setName(STATE_NAME);

        errors = new BindException(form, "form");
    }

    @Test
    public void testNoName() {
        // Check Valid name entry
        validator.validate(form, errors);
        Assert.assertFalse(errors.hasErrors());

        // Verify name requirement
        form.setName(null);
        validator.validate(form, errors);
        Assert.assertEquals("error.required", errors.getFieldError("name").getCode());
    }

    @Test
    public void testNameExists() {
        final StateCode stateCode = new StateCode();
        stateCode.setId(2L);
        EasyMock.expect(mockStateCodeService.getStateCodeByName(STATE_NAME)).andReturn(stateCode);
        EasyMock.replay(mockStateCodeService);

        // Verify name requirement
        validator.validate(form, errors);
        Assert.assertEquals("error.exist", errors.getFieldError("name").getCode());

        EasyMock.verify(mockStateCodeService);
    }
}
