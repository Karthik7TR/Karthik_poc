package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.IsbnValidator.ISBN_CHECKSUM_ERROR;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.IsbnValidator.ISBN_FORMAT_ERROR;

public class IsbnValidatorTest {
    public static final String ISBN13_VALID = "978-173-19-3358-4";
    public static final String ISBN13_CHECKSUM_ERROR = "978-173-19-3358-5";
    public static final String ISBN13_DASHES_FORMAR_ERROR = "978-17319-3358-4";
    public static final String ISBN13_DIGITS_FORMAR_ERROR = "978-173-19-358-4";
    public static final String ISBN10_VALID = "0-88820-317-9";
    public static final String ISBN10_CHECKSUM_ERROR = "0-88820-317-8";
    public static final String ISBN10_DASHES_FORMAR_ERROR = "0-88820-3179";
    public static final String ISBN10_DIGITS_FORMAR_ERROR = "0-88820-31-9";
    private IsbnValidator isbnValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        isbnValidator = new IsbnValidator();
    }

    @Test
    public void testIsbn13() {
        isbnValidator.validateIsbn(ISBN13_VALID);
    }

    @Test
    public void testIsbn10() {
        isbnValidator.validateIsbn(ISBN10_VALID);
    }

    @Test
    public void testIsbn13ChecksumError() {
        thrown.expectMessage(ISBN_CHECKSUM_ERROR);
        isbnValidator.validateIsbn(ISBN13_CHECKSUM_ERROR);
    }

    @Test
    public void testIsbn10ChecksumError() {
        thrown.expectMessage(ISBN_CHECKSUM_ERROR);
        isbnValidator.validateIsbn(ISBN10_CHECKSUM_ERROR);
    }

    @Test
    public void testIsbn13DashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN13_DASHES_FORMAR_ERROR);
    }

    @Test
    public void testIsbn10DashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_DASHES_FORMAR_ERROR);
    }

    @Test
    public void testIsbn13DigitsFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN13_DIGITS_FORMAR_ERROR);
    }

    @Test
    public void testIsbn10DigitsFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_DIGITS_FORMAR_ERROR);
    }
}
