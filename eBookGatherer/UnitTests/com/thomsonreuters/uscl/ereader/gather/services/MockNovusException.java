package com.thomsonreuters.uscl.ereader.gather.services;

import com.westgroup.novus.productapi.NovusException;

public class MockNovusException extends NovusException {
    private static final long serialVersionUID = 5137352032343138922L;

    @Override
    public String toString() {
        return "MockNovusException";
    }
}
