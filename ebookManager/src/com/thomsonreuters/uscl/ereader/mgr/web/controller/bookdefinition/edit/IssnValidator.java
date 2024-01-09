package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.validator.routines.ISSNValidator;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class IssnValidator {
    static final String ISSN_FORMAT_ERROR = "error.issn.format";
    static final String ISSN_CHECKSUM_ERROR = "error.issn.checksum";
    private static final Pattern ISSN_PATTERN = Pattern.compile("^[0-9]{4}-[0-9]{3}[0-9X]$");

    public void validateIssn(final String issn) {
        if (isCorrectFormat(issn)) {
            if (!isValidIssn(issn)) {
                throw new EBookException(ISSN_CHECKSUM_ERROR);
            }
        } else {
            throw new EBookException(ISSN_FORMAT_ERROR);
        }
    }

    private boolean isCorrectFormat(final String issn) {
        return ISSN_PATTERN.matcher(issn).matches();
    }

    private boolean isValidIssn(final String issn) {
        return ISSNValidator.getInstance().isValid(issn);
    }
}
