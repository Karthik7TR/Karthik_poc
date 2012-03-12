/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;



public class WebConstants {
	
	// Date/Time formatting patterns
	public static final String DATE_FORMAT_PATTERN = "MM/dd/yy";  // default date presentation
	public static final String DATE_TIME_FORMAT_PATTERN=DATE_FORMAT_PATTERN + " HH:mm:ss";
	public static final String DATE_TIME_MS_FORMAT_PATTERN=DATE_TIME_FORMAT_PATTERN + ".SSS";
	
	public static final String KEY_SESSION_LIBRARY_FORM = "sessionLibraryBookForm";	// Library search criteria saved on session
	public static final String KEY_PAGINATED_LIST = "paginatedList";
	public static final String KEY_TOTAL_BOOK_SIZE = "resultSize";
	public static final String KEY_BOOK_DEFINITION = "book";
	public static final String KEY_BOOK_DEFINITION_ID = "id";
	public static final String KEY_TITLE_ID = "titleId";
	public static final String KEY_SORT_BY = "sortBy";
	public static final String KEY_PAGE_NUMBER = "pageNumber";
	public static final String KEY_IS_ASCENDING = "isAscending";
	public static final String KEY_VDO = "vdo";
	public static final String KEY_NUMBER_OF_AUTHORS = "numberOfAuthors";
	public static final String KEY_NUMBER_OF_NAME_LINES = "numberOfNameLines";
	public static final String KEY_NUMBER_OF_FRONT_MATTERS = "numberOfFrontMatters";
	public static final String KEY_INFO_MESSAGE = "infoMessage";
	public static final String KEY_INFO_MESSAGES = "infoMessages";
	public static final String KEY_ERR_MESSAGE = "errMessage";
	public static final String KEY_CONTENT_TYPES = "contentTypes";
	public static final String KEY_STATES = "states";
	public static final String KEY_YEARS = "years";
	public static final String KEY_PUB_TYPES = "pubTypes";
	public static final String KEY_JURISDICTIONS = "jurisdictions";
	public static final String KEY_PUBLISHERS = "publishers";
	public static final String KEY_KEYWORDS_TYPE = "typeKeywords";
	public static final String KEY_KEYWORDS_SUBJECT = "subjectKeywords";
	public static final String KEY_KEYWORDS_PUBLISHER = "publisherKeywords";
	public static final String KEY_KEYWORDS_JURISDICTION = "jurisdictionKeywords";
	public static final String KEY_IS_PUBLISHED = "isPublished";
	public static final String KEY_COURT_RULES = "Court Rules";
	public static final String KEY_COURT_RULES_ABBR = "cr";
	public static final String KEY_ANALYTICAL = "Analytical";
	public static final String KEY_ANALYTICAL_ABBR = "an";
	public static final String KEY_SLICE_CODES = "Slice Codes";
	public static final String KEY_SLICE_CODES_ABBR = "sc";
	public static final String KEY_GENERATE_BUTTON_VISIBILITY = "generateButtonVisibility";
	public static final String KEY_IS_IN_JOB_REQUEST="isInJobRequest";
	public static final String KEY_VERSION_NUMBER="versionNumber";
	public static final String KEY_NEW_MAJOR_VERSION_NUMBER="newMajorVersionNumber";
	public static final String KEY_NEW_MINOR_VERSION_NUMBER="newMinorVersionNumber";
	public static final String KEY_PUBLISHING_CUT_OFF_DATE="publishingCutOffDate";
	public static final String KEY_ISBN="isbn";
	public static final String KEY_MATERIAL_ID="materialId";
	
	
	public static final String KEY_JOB_EXECUTION = "jobExecution";
	public static final String KEY_JOB_EXECUTION_ID = "jobExecutionId";
	public static final String KEY_JOB_EXECUTION_IDS = "jobExecutionIds";
	public static final String KEY_JOB_EXECUTIONS = "jobExecutions";
	public static final String KEY_JOB_STEP_EXECUTION = "jobStepExecution";
	public static final String KEY_JOB_STEP_EXECUTION_CONTEXT_MAP_ENTRIES = "jobStepExecutionContextMapEntries";
	public static final String KEY_JOB_STEP_EXECUTION_ID = "stepExecutionId";
	public static final String KEY_JOB_STEP_EXECUTIONS = "jobStepExecutions";
	public static final String KEY_JOB_INSTANCE = "jobInstance";
	public static final String KEY_JOB_INSTANCE_BOOK_INFO = "bookInfo";
	public static final String KEY_JOB_INSTANCE_ID = "jobInstanceId";
	
	// Page paths
	public static final String MVC_ACCESS_DENIED = "accessDenied.mvc";
	public static final String MVC_AFTER_LOGOUT = "afterLogout.mvc";
	public static final String MVC_APP_EXCEPTION = "appException.mvc";
	public static final String MVC_BOOK_BULK_GENERATE_PREVIEW = "generateBulkEbookPreview.mvc";
	public static final String MVC_BOOK_LIBRARY_ICONS = "bookLibraryIcons.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST = "bookLibraryList.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_PAGING = "bookLibraryListPaging.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_SELECTION_POST = "bookLibraryListSelectionPost.mvc";
	public static final String MVC_BOOK_LIBRARY_THUMBNAILS = "bookLibraryThumbnails.mvc";
	public static final String MVC_BOOK_DEFINITION_BULK_PROMOTION = "bookDefinitionBulkPromotion.mvc";
	public static final String MVC_BOOK_DEFINITION_CREATE = "bookDefinitionCreate.mvc";
	public static final String MVC_BOOK_DEFINITION_EDIT = "bookDefinitionEdit.mvc";
	public static final String MVC_BOOK_DEFINITION_IMPORT = "bookDefinitionImport.mvc";
	public static final String MVC_BOOK_DEFINITION_PROMOTION = "bookDefinitionPromotion.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW_GET = "bookDefinitionViewGet.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW_POST = "bookDefinitionViewPost.mvc";
	public static final String MVC_BOOK_SINGLE_GENERATE_PREVIEW = "generateEbookPreview.mvc";
	public static final String MVC_BOOK_SINGLE_GENERATE_SUBMIT = "generateEbookSubmit.mvc";
	public static final String MVC_GET_CONTENT_TYPE_ABBR = "getContentTypeAbbr.mvc";
	
	public static final String MVC_JOB_EXECUTION_DETAILS = "jobExecutionDetails.mvc";
	public static final String MVC_JOB_EXECUTION_DETAILS_POST = "jobExecutionDetailsPost.mvc";
	public static final String MVC_JOB_EXECUTION_JOB_RESTART = "jobExecutionJobRestart.mvc";	// SECURED: job restart button on Job Execution Details page
	public static final String MVC_JOB_EXECUTION_JOB_STOP = "jobExecutionJobStop.mvc";	// SECURED: job stop button on Job Excution Details page
	public static final String MVC_JOB_INSTANCE_DETAILS = "jobInstanceDetails.mvc";
	public static final String MVC_JOB_SUMMARY = "jobSummary.mvc";
	public static final String MVC_JOB_SUMMARY_PAGE_AND_SORT = "jobSummaryPageAndSort.mvc";
	public static final String MVC_JOB_SUMMARY_JOB_OPERATION = "jobSummaryJobOperation.mvc";	// SECURED: job stop or restart button on the Job Summary page
	public static final String MVC_JOB_SUMMARY_CHANGE_ROW_COUNT = "jobSummaryChangeRowCount.mvc";
	public static final String MVC_JOB_SUMMARY_FILTER_POST = "jobSummaryFilterPost.mvc";
	public static final String MVC_JOB_STEP_EXECUTION_DETAILS = "jobStepExecutionDetails.mvc";
	
	public static final String MVC_PREFERENCES = "preferences.mvc";
	public static final String MVC_SUPPORT = "support.mvc";

	// Logical view names
	public static final String VIEW_ACCESS_DENIED = "accessDenied";
	public static final String VIEW_APP_EXCEPTION = "appException";
	public static final String VIEW_BOOK_DEFINITION_BULK_PROMOTION = "_bookDefinitionBulkPromotion";
	public static final String VIEW_BOOK_DEFINITION_CREATE = "_bookDefinitionCreate";
	public static final String VIEW_BOOK_DEFINITION_EDIT = "_bookDefinitionEdit";
	public static final String VIEW_BOOK_DEFINITION_IMPORT = "_bookDefinitionImportView";
	public static final String VIEW_BOOK_DEFINITION_PROMOTION = "_bookDefinitionPromotion";
	public static final String VIEW_BOOK_DEFINITION_VIEW = "_bookDefinitionView";
	public static final String VIEW_BOOK_LIBRARY_ICONS = "_bookLibraryIcons";
	public static final String VIEW_BOOK_LIBRARY_LIST = "_bookLibraryList";
	public static final String VIEW_BOOK_LIBRARY_THUMBNAILS = "_bookLibraryThumbnails";
	public static final String VIEW_BOOK_GENERATE_BULK_PREVIEW = "_bookGenerateBulkPreview";
	public static final String VIEW_BOOK_GENERATE_PREVIEW = "_bookGeneratePreview";
	public static final String VIEW_JOB_EXECUTION_DETAILS = "_jobExecutionDetails";
	public static final String VIEW_JOB_INSTANCE_DETAILS = "_jobInstanceDetails";
	public static final String VIEW_JOB_STEP_EXECUTION_DETAILS = "_jobStepDetails";
	public static final String VIEW_JOB_SUMMARY = "_jobSummary";
	
	// Miscellaneous keys and constants used throughout the application.
//	public enum SortProperty { TITLE_ID, AUTHOR };  // Book Library SortFields
	public static final String TITLE_ID = "fullyQualifiedTitleId";
	public static final String TITLE = "title";

}
