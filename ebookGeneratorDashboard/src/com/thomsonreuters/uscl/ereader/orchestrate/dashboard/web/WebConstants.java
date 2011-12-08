/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web;

import java.text.SimpleDateFormat;

/**
 * Miscellaneous keys and constants used throughout the application.
 */
public class WebConstants {
	
	public enum SortProperty { BOOK, INSTANCE_ID, BATCH_STATUS, START_TIME, EXECUTION_TIME };  // Job Summary page
	public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
	public static final String JVM_PROPERTY_ENVIRONMENT = "environment";
	public static final String MVC_SUFFIX = ".mvc";
	
	public static final String KEY_JOB_INSTANCE_ID = "jobInstanceId";
	public static final String KEY_JOB_EXECUTION_ID = "jobExecutionId";
	public static final String KEY_STEP_EXECUTION_ID = "stepExecutionId";
	public static final String KEY_USER = "user";
	public static final String KEY_PAGE = "page";
	public static final String KEY_ENVIRONMENT = "environment";
	public static final String KEY_SESSION_SUMMARY_FORM = "sessionJobSummaryForm";	// Summary search criteria saved on session
	public static final String KEY_FILTERED_EXECUTION_IDS = "filteredExecutionIds";  // List<Long>: the list of Execution ID's that match the current search filter as defined on the Summary page
	public static final String KEY_PAGINATED_LIST = "paginatedList";
	public static final String KEY_BOOK_CODE_OPTIONS = "bookCodeOptions";	// Which book to generate
	public static final String KEY_JOB_NAMES = "jobNames";
	public static final String KEY_JOB_INSTANCE = "jobInstance";
	public static final String KEY_JOB_EXECUTION = "jobExecution";
	public static final String KEY_JOB_EXECUTIONS = "jobExecutions";
	public static final String KEY_STEP_EXECUTION = "stepExecution";
	public static final String KEY_STEP_EXECUTIONS = "stepExecutions";
	public static final String KEY_STEP_EXECUTION_CONTEXT_MAP_ENTRIES = "stepExecutionContextMapEntries";  // A list of Map.Entry from the current step execution context
	public static final String KEY_VDO = "vdo";
	public static final String KEY_INFO_MESSAGE = "infoMessage";
	public static final String KEY_ERR_MESSAGE = "errMessage";
	
	public static final String VIEW_JOB_SUMMARY = "jobSummary";
	public static final String VIEW_JOB_EXECUTION_DETAILS = "jobExecutionDetails";
	public static final String VIEW_STEP_EXECUTION_DETAILS = "stepExecutionDetails";
	public static final String VIEW_JOB_INSTANCE_DETAILS = "jobInstanceDetails";
	public static final String VIEW_CREATE_BOOK = "createBook";
	
	public static final String URL_JOB_SUMMARY = VIEW_JOB_SUMMARY+".mvc";
	public static final String URL_JOB_EXECUTION_DETAILS_GET = VIEW_JOB_EXECUTION_DETAILS+".mvc";
	public static final String URL_JOB_EXECUTION_DETAILS_POST = VIEW_JOB_EXECUTION_DETAILS+"Submit.mvc";
	public static final String URL_STEP_EXECUTION_DETAILS = VIEW_STEP_EXECUTION_DETAILS+".mvc";
	public static final String URL_JOB_INSTANCE_DETAILS = VIEW_JOB_INSTANCE_DETAILS+".mvc";
	public static final String URL_JOB_SUMMARY_PAGING = "jobSummaryPaging.mvc";
	public static final String URL_JOB_RESTART = "restartJob.mvc";  // redirects to sbEngine restart job
	public static final String URL_CREATE_BOOK = VIEW_CREATE_BOOK+".mvc";
	public static final String URL_JOB_STOP = "stopJob.mvc";	// redirects to sbEngine stop job
	


}
