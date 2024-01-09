package com.thomsonreuters.uscl.ereader.common.filesystem.exception;

/**
 * Thrown if XPP stylesheets directory does not contain document.css or document_*.*.*.css file
 */
public class StylesheetsNotFoundException extends RuntimeException {
    public StylesheetsNotFoundException(String message) {
        super(message);
    }
}
