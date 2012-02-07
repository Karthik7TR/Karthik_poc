/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;

import java.text.SimpleDateFormat;

public class WebConstants {
	
	// Page paths
	public static final String MVC_APP_EXCEPTION = "appException.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST = "book/library/list.mvc";
	public static final String MVC_BOOK_LIBRARY_THUMBNAILS = "book/library/thumbnails.mvc";
	public static final String MVC_BOOK_LIBRARY_ICONS = "book/library/icons.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW = "book/definition/view.mvc";

	// Logical view names
	public static final String VIEW_HOME = "home";
	public static final String VIEW_APP_EXCEPTION = "appException";
	public static final String VIEW_BOOK_LIBRARY_LIST = "_bookLibraryList";
	public static final String VIEW_BOOK_LIBRARY_THUMBNAILS = "_bookLibraryThumbnails";
	public static final String VIEW_BOOK_LIBRARY_ICONS = "_bookLibraryIcons";
	public static final String VIEW_BOOK_DEFINITION_VIEW = "_bookDefinitionView";
	
	
	//Miscellaneous keys and constants used throughout the application.
	 
	
	public enum BookLibrarySortProperty { TITLE_ID, AUTHOR };  // Book Library SortFields
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
	public static final String KEY_BOOK_OPTIONS = "bookOptions";	// Which book to generate
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
