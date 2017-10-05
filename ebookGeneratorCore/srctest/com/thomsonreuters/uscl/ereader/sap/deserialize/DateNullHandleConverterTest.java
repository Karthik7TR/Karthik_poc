package com.thomsonreuters.uscl.ereader.sap.deserialize;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public final class DateNullHandleConverterTest {
    private DateNullHandleConverter converter;

    @Before
    public void onTestSetUp() {
        converter = new DateNullHandleConverter();
    }

    @Test
    public void shouldReturnNullWhenInputNull() {
        final Date result = converter.convert(null);
        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenInputNullString() {
        final Date result = converter.convert("null");
        assertNull(result);
    }

    @Test
    public void shouldReturnNullWhenInputEmpty() {
        final Date result = converter.convert(StringUtils.EMPTY);
        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException() {
        final Date result = converter.convert("28.08.2017");
        assertNull(result);
    }

    @Test
    public void shouldReturnDate() {
        final Date result = converter.convert("2017-08-28");
        assertNotNull(result);
    }
}