package com.thomsonreuters.uscl.ereader;

/**
 * These are the keys used to access job launch parameters from the JobParameters object.
 * These key/value pairs have been loaded prior to the launch of the job
 *
 */
public final class JobParameterKey {
    private JobParameterKey() {
    }

    // ===== Standard values from job request and misc =====
    /** Who started the job. */
    public static final String USER_NAME = "userName";

    /** Host name on which Spring Batch is running the job. */
    public static final String HOST_NAME = "hostName";

    public static final String BOOK_DEFINITION_ID = "bookDefinitionId";
    public static final String COMBINED_BOOK_DEFINITION_ID = "combinedBookDefinitionId";
    public static final String BOOK_VERSION_SUBMITTED = "bookVersionSubmitted";

    public static final String ENVIRONMENT_NAME = "environmentName";

    public static final String PROVIEW_HOST_NAME = "proviewHost";
    public static final String TIMESTAMP = "timestamp";
    public static final String IMAGESVC_DOMAIN_NAME = "imageService";

    public static final String NOVUS_ENV = "novusEnvironment";

    public static final String DATABASE_SERVICE_NAME = "dbServiceName";

    public static final String EBOOK_DEFINITON = "bookDefn";
    public static final String IMAGE_MISSING_GUIDS_FILE = "imageMissingGuidsFile";
    public static final String DOCS_MISSING_GUIDS_FILE = "docsMissingGuidsFile";
    public static final String GATHER_DOCS_DIR = "gatherDocsDir";

    public static final String KEY_REQUEST_XML = "ebookrequestXml";
    public static final String KEY_XPP_BUNDLE = "xppBundle";
    public static final String JOB_NAME_PROCESS_BUNDLE = "ebookBundleJob";
    public static final String KEY_JOB_NAME = "jobName";
    public static final String XPP_BUNDLES = "xppBundles";

    public static final String QUALITY_REPORTS = "qualityReports";
}
