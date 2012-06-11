package com.thomsonreuters.uscl.ereader.core;

public class CoreConstants {
	
	/** The name of the current Novus environment */
	public static enum NovusEnvironment { Client, Prod };
	
	// Data directory path related info used in the InitializeTask (generator) and the cleanup ManagerService (manager).
	public static final String DATA_DIR = "data";
	// Date/Time formatting patterns
	public static final String DIR_DATE_FORMAT = "yyyyMMdd";
	public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy"; // default date presentation
	public static final String DATE_TIME_FORMAT_PATTERN = DATE_FORMAT_PATTERN + " HH:mm:ss";
	public static final String DATE_TIME_MS_FORMAT_PATTERN = DATE_TIME_FORMAT_PATTERN + ".SSS";
	
	/** We are putting the *.mvc suffix on the URL's because the manager app dispatcher servlet expects the URL's to follow this pattern. */
	public static final String URI_SYNC_MISC_CONFIG = "service/sync/misc/config.mvc";
	public static final String URI_SYNC_JOB_THROTTLE_CONFIG = "service/sync/job/throttle/config.mvc";
	public static final String URI_SYNC_PLANNED_OUTAGE = "service/sync/planned/outage.mvc";
	
	public static final String URI_GET_JOB_THROTTLE_CONFIG = "service/get/job/throttle/config";
	public static final String URI_GET_MISC_CONFIG = "service/get/misc/config";
	
	public static final String KEY_SIMPLE_REST_RESPONSE = "simpleResponse";
	
	public static final String KEY_PROVIEW_HOST = "proviewHost";
	
	/** Marshalling view to JiBX marshal the GenerRestServiceResponse into XML that is returned to requesting clients. */
	public static final String VIEW_SIMPLE_REST_RESPONSE = "simpleResponseView";
	
}
