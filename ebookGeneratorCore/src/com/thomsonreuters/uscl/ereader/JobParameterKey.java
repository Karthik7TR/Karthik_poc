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
	public static final String USER_EMAIL = "userEmail";
	/** Host name on which Spring Batch is running the job. */
	public static final String HOST_NAME = "hostName";
	
	public static final String BOOK_DEFINITION_ID = "bookDefinitionId";	
	public static final String BOOK_VERISON_SUBMITTED = "bookVerisonSubmitted";
	/**
	 * Serves as a unique serial number to differentiate the job instances.
	 * If you don't have a varying job parameter for the instance, you get an exception when you try an launch the job
	 * since it thinks that the e-book generating job has already run to completion.
	 */
	public static final String JOB_TIMESTAMP = "jobTimestamp";
}
