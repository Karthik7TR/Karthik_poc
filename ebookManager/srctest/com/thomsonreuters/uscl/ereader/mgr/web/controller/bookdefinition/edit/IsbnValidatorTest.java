package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.IsbnValidator.ISBN_CHECKSUM_ERROR;
import static com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.IsbnValidator.ISBN_FORMAT_ERROR;

public class IsbnValidatorTest {
    private static final String ISBN13_VALID = "978-173-19-3358-4";
    private static final String ISBN13_X_DIGITS_FORMAT_ERROR = "978-173-19-3358-X";
    private static final String ISBN13_CHECKSUM_ERROR = "978-173-19-3358-5";
    private static final String ISBN13_DASHES_FORMAT_ERROR = "978-17319-3358-4";
    private static final String ISBN13_DIGITS_FORMAT_ERROR = "978-173-19-358-4";
    private static final String ISBN13_NO_DASHES_FORMAT_ERROR = "9781731933584";
    private static final String ISBN10_VALID = "0-88820-317-9";
    private static final String ISBN10_CHECKSUM_ERROR = "0-88820-317-8";
    private static final String ISBN10_DASHES_FORMAT_ERROR = "0-88820-3179";
    private static final String ISBN10_DIGITS_FORMAT_ERROR = "0-88820-31-9";
    private static final String ISBN10_NO_DASHES_FORMAT_ERROR = "0888203179";
    private static final String ISBN10_X_VALID = "0-459-27693-X";
    private static final String ISBN10_x_DIGITS_FORMAT_ERROR = "0-459-27693-x";
    private static final String ISBN10_X_CHECKSUM_ERROR = "0-459-27694-X";
    private static final String ISBN10_X_DASHES_FORMAT_ERROR = "0-45927693-X";
    private static final String ISBN10_X_DIGITS_FORMAT_ERROR = "0-49-27693-X";
    private static final String ISBN10_X_NO_DASHES_FORMAT_ERROR = "045927693X";

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
    public void testIsbn10X() {
        isbnValidator.validateIsbn(ISBN10_X_VALID);
    }

    @Test
    public void testIsbn13XChecksumError() {
        thrown.expectMessage(ISBN_CHECKSUM_ERROR);
        isbnValidator.validateIsbn(ISBN13_X_DIGITS_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10xDigitsFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_x_DIGITS_FORMAT_ERROR);
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
    public void testIsbn10XChecksumError() {
        thrown.expectMessage(ISBN_CHECKSUM_ERROR);
        isbnValidator.validateIsbn(ISBN10_X_CHECKSUM_ERROR);
    }

    @Test
    public void testIsbn13DashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN13_DASHES_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10DashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_DASHES_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10XDashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_X_DASHES_FORMAT_ERROR);
    }

    @Test
    public void testIsbn13DigitsFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN13_DIGITS_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10DigitsFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_DIGITS_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10XDigitsFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_X_DIGITS_FORMAT_ERROR);
    }

    @Test
    public void testIsbn13NoDashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN13_NO_DASHES_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10NoDashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_NO_DASHES_FORMAT_ERROR);
    }

    @Test
    public void testIsbn10XNoDashesFormatError() {
        thrown.expectMessage(ISBN_FORMAT_ERROR);
        isbnValidator.validateIsbn(ISBN10_X_NO_DASHES_FORMAT_ERROR);
    }
}
