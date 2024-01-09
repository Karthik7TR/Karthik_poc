package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.JurisTypeCodeService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public final class JurisdictionCodeFormValidatorTest {
    private static final String JURIS_NAME = "test";
    private JurisTypeCodeService mockJurisTypeCodeService;
    private JurisdictionCodeFormValidator validator;
    private JurisdictionCodeForm form;
    private Errors errors;

    @Before
    public void setUp() {
        // Mock up the dashboard service
        mockJurisTypeCodeService = EasyMock.createMock(JurisTypeCodeService.class);

        // Setup Validator
        validator = new JurisdictionCodeFormValidator(mockJurisTypeCodeService);

        form = new JurisdictionCodeForm();
        form.setJurisId(1L);
        form.setName(JURIS_NAME);

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
    public void testNameExists() {
        final JurisTypeCode jurisTypeCode = new JurisTypeCode();
        jurisTypeCode.setId(2L);
        EasyMock.expect(mockJurisTypeCodeService.getJurisTypeCodeByName(JURIS_NAME)).andReturn(jurisTypeCode);
        EasyMock.replay(mockJurisTypeCodeService);

        // Verify name requirement
        validator.validate(form, errors);
        Assert.assertEquals("error.exist", errors.getFieldError("name").getCode());

        EasyMock.verify(mockJurisTypeCodeService);
    }
}
