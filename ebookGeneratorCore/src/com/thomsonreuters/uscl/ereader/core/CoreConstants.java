package com.thomsonreuters.uscl.ereader.core;

public class CoreConstants {
	// Data directory path related info used in the InitializeTask (generator) and the cleanup ManagerService (manager).
	public static final String DATA_DIR = "data";
	public static final String DIR_DATE_FORMAT = "yyyyMMdd";
	
	public static final String URI_SYNC_MISC_CONFIG = "service/sync/misc/config";
	public static final String URI_SYNC_JOB_THROTTLE_CONFIG = "service/sync/job/throttle/config";
	
	public static final String KEY_SIMPLE_REST_RESPONSE = "simpleResponse";
	
	/** Marshalling view to JiBX marshal the GenerRestServiceResponse into XML that is returned to requesting clients. */
	public static final String VIEW_SIMPLE_REST_RESPONSE = "simpleResponseView";
	
}
