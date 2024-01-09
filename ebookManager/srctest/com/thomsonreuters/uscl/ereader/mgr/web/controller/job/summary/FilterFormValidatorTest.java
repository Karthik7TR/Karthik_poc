package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public final class FilterFormValidatorTest {

    private FilterFormValidator validator;
    private FilterForm form;
    private Errors errors;

    @Before
    public void setUp() {
        validator = new FilterFormValidator();
        form = new FilterForm();
        errors = new BindException(form, "form");
    }

    @Test
    public void testWrongDateOrder() {
        final Instant now = Instant.now();
        final Date from = Date.from(now);
        final Date to = Date.from(now.minus(1, ChronoUnit.DAYS));
        form.setFromDate(from);
        form.setToDate(to);

        validator.validate(form, errors);

        final Set<String> expectedGlobalErrorCodes =
            Stream.of("error.from.date.after.to.date", "error.to.date.before.from.date").collect(Collectors.toSet());
        final Set<String> actualGlobalErrorCodes = getGlobalErrorCodes();
        Assert.assertTrue(errors.hasErrors());
        Assert.assertEquals(expectedGlobalErrorCodes, actualGlobalErrorCodes);
    }

    private Set<String> getGlobalErrorCodes() {
        return errors.getGlobalErrors().stream()
            .map(DefaultMessageSourceResolvable::getCode)
            .collect(Collectors.toSet());
    }

}
