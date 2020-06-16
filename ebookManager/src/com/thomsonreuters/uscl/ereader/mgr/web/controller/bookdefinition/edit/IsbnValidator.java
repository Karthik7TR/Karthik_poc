package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.validator.routines.ISBNValidator;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IsbnValidator {
    private static final int ISBN13_TOTAL_CHARACTER_LENGTH = 17;
    private static final int ISBN13_NUMBER_LENGTH = 13;
    private static final int ISBN10_TOTAL_CHARACTER_LENGTH = 13;
    private static final int ISBN10_NUMBER_LENGTH = 10;
    static final String ISBN_FORMAT_ERROR = "error.isbn.format";
    static final String ISBN_CHECKSUM_ERROR = "error.isbn.checksum";

    public void validateIsbn(final String isbnWithDashes) {
        if (isCorrectFormat(isbnWithDashes)) {
            if (!isValidIsbn(isbnWithDashes)) {
                throw new EBookException(ISBN_CHECKSUM_ERROR);
            }
        } else {
            throw new EBookException(ISBN_FORMAT_ERROR);
        }
    }

    private String removeDashes(final String isbnWithDashes) {
        return isbnWithDashes.replace("-", "");
    }

    private boolean isCorrectFormat(final String isbnWithDashes) {
        final String isbn = removeDashes(isbnWithDashes);
        return isCorrectLength(isbnWithDashes, isbn) && isConsistsOfDigits(isbn);
    }

    private boolean isCorrectLength(final String isbnWithDashes, final String isbn) {
        return isCorrectLength(isbnWithDashes, ISBN13_TOTAL_CHARACTER_LENGTH, isbn, ISBN13_NUMBER_LENGTH) ||
               isCorrectLength(isbnWithDashes, ISBN10_TOTAL_CHARACTER_LENGTH, isbn, ISBN10_NUMBER_LENGTH);
    }

    private boolean isCorrectLength(final String isbnWithDashes, final int isbnWithDashesLength, final String isbn, final int isbnLength) {
        return isbnWithDashes.length() == isbnWithDashesLength && isbn.length() == isbnLength;
    }

    private boolean isConsistsOfDigits(final String isbn) {
        final Pattern pattern = Pattern.compile("^\\d+X?$");
        final Matcher matcher = pattern.matcher(isbn);
        return matcher.find();
    }

    private boolean isValidIsbn(final String isbn) {
        return ISBNValidator.getInstance().isValid(isbn);
    }
}
