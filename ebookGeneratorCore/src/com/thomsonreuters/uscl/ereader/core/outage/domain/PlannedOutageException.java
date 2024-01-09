package com.thomsonreuters.uscl.ereader.core.outage.domain;

public class PlannedOutageException extends Exception {
    private static final long serialVersionUID = -4160175427073669517L;

    public PlannedOutageException(final String mesg) {
        super(mesg);
    }
}
