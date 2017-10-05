package com.thomsonreuters.uscl.ereader.sap.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

public class SapRequestException extends RuntimeException {
    private static final long serialVersionUID = -5795753097652233409L;

    public SapRequestException(final HttpStatus status, final String materialNumber) {
        super(
            MessageFormat
                .format("SAP request failed for material #: {0}, response status: {1}", materialNumber, status));
    }
}
