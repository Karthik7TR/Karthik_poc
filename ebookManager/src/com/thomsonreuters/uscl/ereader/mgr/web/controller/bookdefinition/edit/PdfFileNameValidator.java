package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.thomsonreuters.uscl.ereader.mgr.web.ErrorMessageCodes.FORBIDDEN_CHARACTERS_IN_PDF_NAME;
import static com.thomsonreuters.uscl.ereader.mgr.web.ErrorMessageCodes.WRONG_PDF_FILE_EXTENSION;

@Service
public class PdfFileNameValidator {
    private static final String PDF_NAME_ALLOWED_CHARACTERS = "^.*[^_\\-!A-Za-z0-9].*$";
    private static final String PDF = "pdf";
    @Getter
    @Value(WRONG_PDF_FILE_EXTENSION)
    private String wrongPdfFileExtensionErrorMessage;
    @Getter
    @Value(FORBIDDEN_CHARACTERS_IN_PDF_NAME)
    private String forbiddenCharactersErrorMessage;

    public boolean isFileExtensionNotPdf(final String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return !PDF.equalsIgnoreCase(extension);
    }

    public boolean isFileNameContainsForbiddenCharacters(final String fileName) {
        String baseName = FilenameUtils.getBaseName(fileName);
        return baseName.matches(PDF_NAME_ALLOWED_CHARACTERS);
    }
}
