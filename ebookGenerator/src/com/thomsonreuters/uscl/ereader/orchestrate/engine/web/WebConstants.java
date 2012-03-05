package com.thomsonreuters.uscl.ereader.orchestrate.engine.web;


/**
 * Miscellaneous keys and constants used throughout the application.
 */
public class WebConstants {
	
	public static final String KEY_JOB_EXECUTION_ID = "jobExecutionId";
	public static final String KEY_ERROR_MESSAGE = "errorMessage";
	public static final String KEY_STACK_TRACE = "stackTrace";
	public static final String KEY_ACTION = "action";
	public static final String KEY_JOB_OPERATION_RESPONSE = "jobOperationResponse";
	
	public static final String URI_JOB_RESTART = "service/restart/job/{jobExecutionId}";
	public static final String URI_JOB_STOP = "service/stop/job/{jobExecutionId}";
	
	public static final String URL_JOB_RESTART = "restartJob.mvc";
	public static final String URL_JOB_STOP = "stopJob.mvc";
	public static final String URL_ADMIN_GET = "admin.mvc";
	public static final String URL_ADMIN_POST = "adminSubmit.mvc";
	
	public static final String VIEW_HOME = "home";
	public static final String VIEW_ADMIN = "admin";
	public static final String VIEW_JOB_OPERATION_FAILURE = "operationFailure";
	
	/** Marshalling view to JiBX marshal the JobOperationResponse into XML that is returned to requesting clients. */
	public static final String VIEW_JOB_OPERATION_RESPONSE = "marshalJobOperationResponseView";

}
