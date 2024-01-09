package com.thomsonreuters.uscl.ereader.request;

public final class XPPConstants {
    public static final String PATTERN_BUDNLE_ARCHIVE_DATE_DIRECTORY = "YYYY/MM";
    public static final String PATTERN_BUNDLE_ARCHIVE_FILE = "/apps/eBookBuilder/%s/xpp/archive/%s";
    public static final String PATTERN_BUDNLE_STAGED_DIRECTORY = "/apps/eBookBuilder/%s/xpp/jobs/%s";

    public static final String FILE_TARBALL_EXTENSION = ".gz"; // TODO identify whether .tar.gz or just .gz will be the extension
    public static final String FILE_ASSETS_DIRECTORY = "assets/";
    public static final String FILE_PDF_DIRECTORY = "PDF/";
    public static final String FILE_XPP_DIRECTORY = "XPP/";
    public static final String FILE_BUNDLE_XML = "bundle.xml";

    public static final String ERROR_TARBALL_NOT_FOUND = "Unable to access tarball:  ";
    public static final String ERROR_EXTRACT_FILES = "Unable to create output file:  ";
    public static final String ERROR_BUNDLE_NOT_FOUND = "Bundle not found at specified location: ";
    public static final String ERROR_INCOMPLETE_REQUEST = "NULL field detected. ";
    public static final String ERROR_CREATE_HASH = "Problem encountered while hashing file. ";
    public static final String ERROR_BAD_HASH = "Verification hash does not match for:  ";
    public static final String ERROR_DUPLICATE_REQUEST = "Request already received: ";

    private XPPConstants() {
        // static content class
    }
}
