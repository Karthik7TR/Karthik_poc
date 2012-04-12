package com.thomsonreuters.uscl.ereader.orchestrate.engine.web;


/**
 * Miscellaneous keys and constants used throughout the application.
 */
public class WebConstants {
	
	public static final String KEY_JOB_OPERATION_RESPONSE = "jobOperationResponse";
	
	/** REST service operation URL templates for restart and stop a job */
	public static final String URI_JOB_RESTART = "service/restart/job/{jobExecutionId}";
	public static final String URI_JOB_STOP = "service/stop/job/{jobExecutionId}";
	
	public static final String VIEW_HOME = "home";
	
	/** Marshalling view to JiBX marshal the JobOperationResponse into XML that is returned to requesting clients. */
	public static final String VIEW_JOB_OPERATION_RESPONSE = "marshalJobOperationResponseView";

}
