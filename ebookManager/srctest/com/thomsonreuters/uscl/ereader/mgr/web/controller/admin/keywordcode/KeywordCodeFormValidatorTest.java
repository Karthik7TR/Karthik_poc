package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.KeywordTypeCodeSevice;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public final class KeywordCodeFormValidatorTest {
    private static final String KEYWORD_CODE_NAME = "test";
    private KeywordTypeCodeSevice keywordTypeCodeSevice;
    private KeywordCodeFormValidator validator;
    private KeywordCodeForm form;
    private Errors errors;

    @Before
    public void setUp() {
        // Mock up the service
        keywordTypeCodeSevice = EasyMock.createMock(KeywordTypeCodeSevice.class);

        // Setup Validator
        validator = new KeywordCodeFormValidator(keywordTypeCodeSevice);

        form = new KeywordCodeForm();
        form.setCodeId(1L);
        form.setName(KEYWORD_CODE_NAME);

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
        final KeywordTypeCode keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setId(2L);
        EasyMock.expect(keywordTypeCodeSevice.getKeywordTypeCodeByName(KEYWORD_CODE_NAME)).andReturn(keywordTypeCode);
        EasyMock.replay(keywordTypeCodeSevice);

        // Verify name requirement
        validator.validate(form, errors);
        Assert.assertEquals("error.exist", errors.getFieldError("name").getCode());

        EasyMock.verify(keywordTypeCodeSevice);
    }
}
