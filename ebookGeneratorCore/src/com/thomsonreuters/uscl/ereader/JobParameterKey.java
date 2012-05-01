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
	/** E-mail address of the person who started the job. */
	public static final String USER_EMAIL = "west.ebookGenerationSupport@thomsonreuters.com";
	/** Host name on which Spring Batch is running the job. */
	public static final String HOST_NAME = "hostName";
	
	public static final String BOOK_DEFINITION_ID = "bookDefinitionId";	
	public static final String BOOK_VERSION_SUBMITTED = "bookVersionSubmitted";
	
	public static final String ENVIRONMENT_NAME = "environmentName";
	
	public static final String PROVIEW_DOMAIN_NAME = "proviewDomain";	
	public static final String TIMESTAMP = "timestamp";	
}
