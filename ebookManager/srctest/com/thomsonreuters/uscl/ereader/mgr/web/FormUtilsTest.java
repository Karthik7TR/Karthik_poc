package com.thomsonreuters.uscl.ereader.mgr.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.format.DateTimeParseException;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class FormUtilsTest {
    private String dateString = "2019-02-25T14:52:57Z";
    private long time = 1551106377000L;
    private Date date;

    private String dateStringNoSeconds = "2019-02-23T00:41Z";
    private long timeNoSeconds = 1550882460000L;
    private Date dateNoSeconds;

    private String uxpectedFormatDate = "30 February";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        date = new Date(time);
        dateNoSeconds = new Date(timeNoSeconds);
    }

    @Test
    public void shouldConvertDateToString() {
        assertEquals(dateString, FormUtils.parseDate(date));
    }

    @Test
    public void shouldConvertStringToDate() {
        assertEquals(time, FormUtils.parseDate(dateString).getTime());
    }

    @Test
    public void shouldConvertDateToStringWhenNoSecondsSpecified() {
        assertEquals(dateStringNoSeconds, FormUtils.parseDate(dateNoSeconds));
    }

    @Test
    public void shouldConvertStringToDateWhenNoSecondsSpecified() {
        assertEquals(timeNoSeconds, FormUtils.parseDate(dateStringNoSeconds).getTime());
    }

    @Test
    public void shouldHandleEmptyString() {
        assertNull(FormUtils.parseDate(""));
    }

    @Test
    public void shouldHandleNullDate() {
        final Date dateNull = null;
        assertNull(FormUtils.parseDate(dateNull));
    }

    @Test
    public void shouldHandleNullString() {
        final String stringNull = null;
        assertNull(FormUtils.parseDate(stringNull));
    }

    @Test
    public void shouldFailOnUnexpectedString() {
        thrown.expect(DateTimeParseException.class);
        FormUtils.parseDate(uxpectedFormatDate);
    }
}
