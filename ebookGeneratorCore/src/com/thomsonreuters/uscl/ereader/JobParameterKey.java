package com.thomsonreuters.uscl.ereader;


/**
 * These are the keys used to access job launch parameters from the JobParameters object.
 * These key/value pairs have been loaded prior to the launch of the job
 *
 */
public class JobParameterKey {
	
	// ===== Standard values from job request and misc =====
	/** Who started the job. */
	public static final String USER_NAME = "userName";

	/** Host name on which Spring Batch is running the job. */
	public static final String HOST_NAME = "hostName";
	
	public static final String BOOK_DEFINITION_ID = "bookDefinitionId";	
	public static final String BOOK_VERSION_SUBMITTED = "bookVersionSubmitted";
	
	public static final String ENVIRONMENT_NAME = "environmentName";
	
	public static final String PROVIEW_HOST_NAME = "proviewHost";	
	public static final String TIMESTAMP = "timestamp";
	public static final String IMAGESVC_DOMAIN_NAME = "imageService";
	
	public static final String NOVUS_ENV = "novusEnvironment";
	
	public static final String DATABASE_SERVICE_NAME = "dbServiceName";
}
