package com.thomsonreuters.uscl.ereader.orchestrate.engine.web;

/**
 * Miscellaneous keys and constants used throughout the application.
 */
public final class WebConstants {
    public static final String KEY_JOB_THROTTLE_CONFIG = "jobThrottleConfig";
    public static final String KEY_MISC_CONFIG = "miscConfig";

    public static final String URI_HOME = "/home.mvc";
    public static final String URI_APP_EXCEPTION = "/appException.mvc";

    /** REST service operation URL templates for restart and stop a job */
    public static final String URI_JOB_RESTART = "service/restart/job/{jobExecutionId}";
    public static final String URI_JOB_STOP = "service/stop/job/{jobExecutionId}";

    public static final String URI_GET_STEP_NAMES = "service/get/step/names/{jobName}";

    public static final String URI_GET_BUNDLE_PDFS = "pdfs/{jobInstanceId}/{materialNumber}/{pdfName}.zip";

    public static final String URI_GET_QUALITY_REPORT = "qualityreport/{jobInstanceId}/{material}/{fileName}";

    public static final String VIEW_HOME = "home";
    public static final String VIEW_JOB_THROTTLE_CONFIG_RESPONSE = "jobThrottleConfigResponseView";
    public static final String VIEW_MISC_CONFIG_RESPONSE = "miscConfigResponseView";

    public static final String VIEW_APP_EXCEPTION = "appException";

    private WebConstants() {

    }
}
