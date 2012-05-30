/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;

public class WebConstants {

	// Date/Time formatting patterns
	public static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy"; // default
																	// date
																	// presentation
	public static final String DATE_TIME_FORMAT_PATTERN = DATE_FORMAT_PATTERN
			+ " HH:mm:ss";
	public static final String DATE_TIME_MS_FORMAT_PATTERN = DATE_TIME_FORMAT_PATTERN
			+ ".SSS";
	public static final String FRONT_MATTER_PREVIEW_WINDOW_SPECS = "height=800,width=768,top=0,resizable=yes,scrollbars=yes";

	public static final String KEY_ENVIRONMENT_NAME = "environmentName";
	public static final String KEY_PROVIEW_DOMAIN = "proviewDomain";
	public static final String KEY_SESSION_LIBRARY_FORM = "sessionLibraryBookForm"; // Library
																					// search
																					// criteria
																					// saved
																					// on
																					// session
	public static final String KEY_PROVIEW_TITLE_INFO_FORM = "proviewTitleInfoForm";
	public static final String KEY_PAGINATED_LIST = "paginatedList";
	public static final String KEY_ALL_LATEST_PROVIEW_TITLES = "allLatestProviewTitleInfo";
	public static final String KEY_ALL_PROVIEW_TITLES = "allProviewTitleInfo";
	public static final String KEY_TOTAL_BOOK_SIZE = "resultSize";
	public static final String KEY_BULK_PUBLISH_LIST = "bulkPublishList";
	public static final String KEY_BULK_PUBLISH_SIZE = "bulkPublishtSize";
	public static final String KEY_BOOK_DEFINITION = "book";
	public static final String KEY_BOOK_AUDIT_DETAIL = "bookAuditDetail";
	public static final String KEY_BOOK_GENERATE_CANCEL = "bookGenerateCancel";
	public static final String KEY_JURIS_TYPE_CODE = "jurisTypeCode";
	public static final String KEY_PUB_TYPE_CODE = "pubTypeCode";
	public static final String KEY_STATE_CODE = "stateCode";
	public static final String KEY_KEYWORD_TYPE_CODE = "keywordTypeCode";
	public static final String KEY_KEYWORD_TYPE_VALUE = "keywordTypeValue";
	public static final String KEY_ID = "id";
	public static final String KEY_TITLE_ID = "titleId";
	public static final String KEY_STATUS = "status";
	public static final String KEY_SORT_BY = "sortBy";
	public static final String KEY_PAGE_NUMBER = "pageNumber";
	public static final String KEY_IS_ASCENDING = "isAscending";
	public static final String KEY_VDO = "vdo";
	public static final String KEY_JOB = "job";
	public static final String KEY_NUMBER_OF_AUTHORS = "numberOfAuthors";
	public static final String KEY_NUMBER_OF_NAME_LINES = "numberOfNameLines";
	public static final String KEY_NUMBER_OF_FRONT_MATTERS = "numberOfFrontMatters";
	public static final String KEY_NUMBER_OF_EXCLUDE_DOCUMENTS = "numberOfExcludeDocuments";
	public static final String KEY_INFO_MESSAGE = "infoMessage";
	public static final String KEY_INFO_MESSAGES = "infoMessages";
	public static final String KEY_ERR_MESSAGE = "errMessage";
	public static final String KEY_CONTENT_TYPES = "contentTypes";
	public static final String KEY_STATES = "states";
	public static final String KEY_YEARS = "years";
	public static final String KEY_PUB_TYPES = "pubTypes";
	public static final String KEY_JURISDICTIONS = "jurisdictions";
	public static final String KEY_PUBLISHERS = "publishers";
	public static final String KEY_OUTAGE = "outage";
	public static final String KEY_IS_PUBLISHED = "isPublished";
	public static final String KEY_SUPER_PUBLISHER_PUBLISHERPLUS = "superPublisherPublisherplusVisibility";
	public static final String KEY_IS_IN_JOB_REQUEST = "isInJobRequest";
	public static final String KEY_VERSION_NUMBER = "versionNumber";
	public static final String KEY_NEW_MAJOR_VERSION_NUMBER = "newMajorVersionNumber";
	public static final String KEY_NEW_MINOR_VERSION_NUMBER = "newMinorVersionNumber";
	public static final String KEY_PUBLISHING_CUT_OFF_DATE = "publishingCutOffDate";
	public static final String KEY_PUBLISHING_CUTOFF_DATE_EQUAL_OR_GREATER_THAN_TODAY = "publishingCutOffDateGreaterOrEqualToday";
	public static final String KEY_USE_PUBLISHING_CUT_OFF_DATE = "usePublishingCutOffDate";
	public static final String KEY_MATERIAL_ID_CHANGED = "materialIdChanged";
	public static final String KEY_ISBN_CHANGED = "isbnChanged";
	public static final String KEY_ISBN = "isbn";
	public static final String KEY_MATERIAL_ID = "materialId";
	public static final String KEY_PUBLISHING_STATS = "publishingStats";
	public static final String KEY_IS_COMPLETE = "isComplete";
	public static final String KEY_IS_NEW_ISBN = "isNewISBN";
	public static final String KEY_IS_NEW_MTERIAL_ID = "isNewMaterialId";
	public static final String KEY_BOOK_DEFINITION_LOCK = "bookDefinitionLock";

	public static final String KEY_FRONT_MATTER_PREVIEW_HTML = "previewHtml";

	public static final String KEY_JOB_EXECUTION = "jobExecution";
	public static final String KEY_JOB_EXECUTION_ID = "jobExecutionId";
	public static final String KEY_JOB_EXECUTION_IDS = "jobExecutionIds";
	public static final String KEY_JOB_EXECUTIONS = "jobExecutions";
	public static final String KEY_JOB_QUEUED_PAGE_AND_SORT = "jobsQueuedPageAndSort";
	public static final String KEY_JOB_STEP_EXECUTION = "jobStepExecution";
	public static final String KEY_JOB_STEP_EXECUTION_CONTEXT_MAP_ENTRIES = "jobStepExecutionContextMapEntries";
	public static final String KEY_JOB_STEP_EXECUTION_ID = "stepExecutionId";
	public static final String KEY_JOB_STEP_EXECUTIONS = "jobStepExecutions";
	public static final String KEY_JOB_INSTANCE = "jobInstance";
	public static final String KEY_JOB_INSTANCE_DURATION = "jobInstanceDuration";
	public static final String KEY_JOB_INSTANCE_ID = "jobInstanceId";
	public static final String KEY_JOB_BOOK_INFO = "bookInfo";

	// Document Type Codes
	public static final String DOCUMENT_TYPE_COURT_RULES = "Court Rules";
	public static final String DOCUMENT_TYPE_COURT_RULES_ABBR = "cr";
	public static final String DOCUMENT_TYPE_ANALYTICAL = "Analytical";
	public static final String DOCUMENT_TYPE_ANALYTICAL_ABBR = "an";
	public static final String DOCUMENT_TYPE_SLICE_CODES = "Slice Codes";
	public static final String DOCUMENT_TYPE_SLICE_CODES_ABBR = "sc";

	// Confirmation codes
	public static final String CONFIRM_CODE_KILL_SWITCH = "Stop all";
	public static final String CONFIRM_CODE_DELETE_BOOK = "DELETE BOOK";

	// NAS locations for files
	public static final String LOCATION_KEY_COVER_IMAGE = "/apps/eBookBuilder/generator/images/cover";
	public static final String LOCATION_FRONT_MATTER_IMAGE = "/apps/eBookBuilder/coreStatic/images";
	public static final String LOCATION_FRONT_MATTER_CSS = "/apps/eBookBuilder/coreStatic/css";
	public static final String LOCATION_PDF = "/apps/eBookBuilder/generator/images/pdf";

	// Page paths
	public static final String MVC_APP_EXCEPTION = "appException.mvc";
	public static final String MVC_COVER_IMAGE = "coverImage.mvc";
	public static final String MVC_FRONT_MATTER_IMAGE = "frontMatterImage.mvc";
	public static final String MVC_FRONT_MATTER_CSS = "frontMatterCss.mvc";
	public static final String MVC_BOOK_BULK_GENERATE_PREVIEW = "generateBulkEbookPreview.mvc";
	public static final String MVC_BOOK_LIBRARY_ICONS = "bookLibraryIcons.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST = "bookLibraryList.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_PAGING = "bookLibraryListPaging.mvc";
	public static final String MVC_BOOK_LIBRARY_FILTERED_POST = "bookLibraryListFiltered.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_SELECTION_POST = "bookLibraryListSelectionPost.mvc";
	public static final String MVC_BOOK_LIBRARY_THUMBNAILS = "bookLibraryThumbnails.mvc";
	public static final String MVC_BOOK_DEFINITION_BULK_PROMOTION = "bookDefinitionBulkPromotion.mvc";
	public static final String MVC_BOOK_DEFINITION_CREATE = "bookDefinitionCreate.mvc";
	public static final String MVC_BOOK_DEFINITION_EDIT = "bookDefinitionEdit.mvc";
	public static final String MVC_BOOK_DEFINITION_COPY = "bookDefinitionCopy.mvc";
	public static final String MVC_BOOK_DEFINITION_IMPORT = "bookDefinitionImport.mvc";
	public static final String MVC_BOOK_DEFINITION_UNLOCK = "bookDefinitionUnlock.mvc";
	public static final String MVC_BOOK_DEFINITION_PROMOTION = "bookDefinitionPromotion.mvc";
	public static final String MVC_BOOK_DEFINITION_DELETE = "bookDefinitionDelete.mvc";
	public static final String MVC_BOOK_DEFINITION_RESTORE = "bookDefinitionRestore.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW_GET = "bookDefinitionViewGet.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW_POST = "bookDefinitionViewPost.mvc";
	public static final String MVC_BOOK_SINGLE_GENERATE_PREVIEW = "generateEbookPreview.mvc";
	public static final String MVC_BOOK_SINGLE_GENERATE_SUBMIT = "generateEbookSubmit.mvc";
	public static final String MVC_GET_CONTENT_TYPE = "getContentType.mvc";
	public static final String MVC_FRONT_MATTER_PREVIEW = "frontMatterPreview.mvc";
	public static final String MVC_FRONT_MATTER_PREVIEW_EDIT = "frontMatterPreviewEdit.mvc";

	public static final String MVC_FRONT_MATTER_PREVIEW_TITLE = "frontMatterPreviewTitle.mvc";
	public static final String MVC_FRONT_MATTER_PREVIEW_COPYRIGHT = "frontMatterPreviewCopyright.mvc";
	public static final String MVC_FRONT_MATTER_PREVIEW_ADDITIONAL = "frontMatterPreviewContentAdditional.mvc";
	public static final String MVC_FRONT_MATTER_PREVIEW_RESEARCH = "frontMatterPreviewResearch.mvc";
	public static final String MVC_FRONT_MATTER_PREVIEW_WESTLAWNEXT = "frontMatterPreviewWestlawNext.mvc";

	public static final String MVC_JOB_EXECUTION_DETAILS = "jobExecutionDetails.mvc";
	public static final String MVC_JOB_EXECUTION_DETAILS_POST = "jobExecutionDetailsPost.mvc";
	public static final String MVC_JOB_EXECUTION_JOB_RESTART = "jobExecutionJobRestart.mvc"; // SECURED:
																								// job
																								// restart
																								// button
																								// on
																								// Job
																								// Execution
																								// Details
																								// page
	public static final String MVC_JOB_EXECUTION_JOB_STOP = "jobExecutionJobStop.mvc"; // SECURED:
																						// job
																						// stop
																						// button
																						// on
																						// Job
																						// Excution
																						// Details
																						// page
	public static final String MVC_JOB_INSTANCE_DETAILS = "jobInstanceDetails.mvc";
	public static final String MVC_JOB_QUEUE = "jobQueue.mvc";
	public static final String MVC_JOB_QUEUE_PAGE_AND_SORT = "jobQueuePageAndSort.mvc";
	public static final String MVC_JOB_SUMMARY = "jobSummary.mvc";
	public static final String MVC_JOB_SUMMARY_PAGE_AND_SORT = "jobSummaryPageAndSort.mvc";
	public static final String MVC_JOB_SUMMARY_JOB_OPERATION = "jobSummaryJobOperation.mvc"; // SECURED:
																								// job
																								// stop
																								// or
																								// restart
																								// button
																								// on
																								// the
																								// Job
																								// Summary
																								// page
	public static final String MVC_JOB_SUMMARY_CHANGE_ROW_COUNT = "jobSummaryChangeRowCount.mvc";
	public static final String MVC_JOB_SUMMARY_FILTER_POST = "jobSummaryFilterPost.mvc";
	public static final String MVC_JOB_STEP_EXECUTION_DETAILS = "jobStepExecutionDetails.mvc";
	public static final String MVC_BOOK_JOB_HISTORY = "eBookJobHistory.mvc";
	public static final String MVC_BOOK_JOB_METRICS = "eBookJobMetrics.mvc";

	public static final String MVC_BOOK_AUDIT_LIST = "bookAuditList.mvc";
	public static final String MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT = "bookAuditListPageAndSort.mvc";
	public static final String MVC_BOOK_AUDIT_LIST_FILTER_POST = "bookAuditListFilterPost.mvc";
	public static final String MVC_BOOK_AUDIT_SPECIFIC = "bookAudit.mvc";
	public static final String MVC_BOOK_AUDIT_DETAIL = "bookAuditDetail.mvc";

	// Administration related URI's
	public static final String MVC_ADMIN_MAIN = "adminMain.mvc";
	public static final String MVC_ADMIN_MISC = "adminMisc.mvc";
	public static final String MVC_ADMIN_BOOK_LOCK_LIST = "adminBookLockList.mvc";
	public static final String MVC_ADMIN_BOOK_LOCK_DELETE = "adminBookLockDelete.mvc";
	public static final String MVC_ADMIN_STOP_GENERATOR = "adminStopGenerator.mvc";
	public static final String MVC_ADMIN_JURIS_CODE_VIEW = "adminJurisCodeView.mvc";
	public static final String MVC_ADMIN_JURIS_CODE_CREATE = "adminJurisCodeCreate.mvc";
	public static final String MVC_ADMIN_JURIS_CODE_DELETE = "adminJurisCodeDelete.mvc";
	public static final String MVC_ADMIN_JURIS_CODE_EDIT = "adminJurisCodeEdit.mvc";
	public static final String MVC_ADMIN_JOB_THROTTLE_CONFIG = "adminJobThrottleConfig.mvc";
	public static final String MVC_ADMIN_KEYWORD_CODE_VIEW = "adminKeywordCodeView.mvc";
	public static final String MVC_ADMIN_KEYWORD_CODE_CREATE = "adminKeywordCodeCreate.mvc";
	public static final String MVC_ADMIN_KEYWORD_CODE_DELETE = "adminKeywordCodeDelete.mvc";
	public static final String MVC_ADMIN_KEYWORD_CODE_EDIT = "adminKeywordCodeEdit.mvc";
	public static final String MVC_ADMIN_KEYWORD_VALUE_CREATE = "adminKeywordValueCreate.mvc";
	public static final String MVC_ADMIN_KEYWORD_VALUE_DELETE = "adminKeywordValueDelete.mvc";
	public static final String MVC_ADMIN_KEYWORD_VALUE_EDIT = "adminKeywordValueEdit.mvc";
	public static final String MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW = "adminPublishTypeCodeView.mvc";
	public static final String MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE = "adminPublishTypeCodeCreate.mvc";
	public static final String MVC_ADMIN_PUBLISH_TYPE_CODE_DELETE = "adminPublishTypeCodeDelete.mvc";
	public static final String MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT = "adminPublishTypeCodeEdit.mvc";
	public static final String MVC_ADMIN_STATE_CODE_VIEW = "adminStateCodeView.mvc";
	public static final String MVC_ADMIN_STATE_CODE_CREATE = "adminStateCodeCreate.mvc";
	public static final String MVC_ADMIN_STATE_CODE_DELETE = "adminStateCodeDelete.mvc";
	public static final String MVC_ADMIN_STATE_CODE_EDIT = "adminStateCodeEdit.mvc";
	public static final String MVC_ADMIN_OUTAGE_ACTIVE_LIST = "adminOutageActiveList.mvc";
	public static final String MVC_ADMIN_OUTAGE_FULL_LIST = "adminOutageFullList.mvc";
	public static final String MVC_ADMIN_OUTAGE_CREATE = "adminOutageCreate.mvc";
	public static final String MVC_ADMIN_OUTAGE_DELETE = "adminOutageDelete.mvc";
	public static final String MVC_ADMIN_OUTAGE_EDIT = "adminOutageEdit.mvc";
	
	// Proview related URI's
	public static final String MVC_PROVIEW_TITLES = "proviewTitles.mvc";
	public static final String MVC_PROVIEW_TITLE_ALL_VERSIONS = "proviewTitleAllVersions.mvc";
	public static final String MVC_PROVIEW_TITLE_REMOVE = "proviewTitleRemove.mvc";
	public static final String MVC_PROVIEW_TITLE_PROMOTE = "proviewTitlePromote.mvc";
	public static final String MVC_PROVIEW_TITLE_DELETE = "proviewTitleDelete.mvc";

	// Security related URI's
	public static final String MVC_SEC_ACCESS_DENIED = "accessDenied.mvc";
	public static final String MVC_SEC_AFTER_AUTHENTICATION = "afterAuthentication.mvc";
	public static final String MVC_SEC_AFTER_LOGOUT = "afterLogout.mvc";
	public static final String MVC_SEC_LOGIN = "login.mvc";
	public static final String MVC_SEC_LOGIN_FAIL = "loginFail.mvc";

	public static final String MVC_SMOKE_TEST = "smokeTest.mvc";
	public static final String MVC_USER_PREFERENCES = "userPreferences.mvc";

	// Error related URI's
	public static final String MVC_ERROR_BOOK_DELETED = "errorBookDeleted.mvc";
	public static final String MVC_ERROR_BOOK_DEFINITION = "errorBookDefinition.mvc";

	// Logical view names
	public static final String VIEW_APP_EXCEPTION = "appException";
	public static final String VIEW_ADMIN_MAIN = "_adminMain";
	public static final String VIEW_ADMIN_MISC = "_adminMisc";
	public static final String VIEW_ADMIN_BOOK_LOCK_LIST = "_adminBookLockList";
	public static final String VIEW_ADMIN_BOOK_LOCK_DELETE = "_adminBookLockDelete";
	public static final String VIEW_ADMIN_STOP_GENERATOR = "_adminStopGenerator";
	public static final String VIEW_ADMIN_JURIS_CODE_VIEW = "_adminJurisCodeView";
	public static final String VIEW_ADMIN_JURIS_CODE_CREATE = "_adminJurisCodeCreate";
	public static final String VIEW_ADMIN_JURIS_CODE_DELETE = "_adminJurisCodeDelete";
	public static final String VIEW_ADMIN_JURIS_CODE_EDIT = "_adminJurisCodeEdit";
	public static final String VIEW_ADMIN_KEYWORD_CODE_VIEW = "_adminKeywordCodeView";
	public static final String VIEW_ADMIN_KEYWORD_CODE_CREATE = "_adminKeywordCodeCreate";
	public static final String VIEW_ADMIN_KEYWORD_CODE_DELETE = "_adminKeywordCodeDelete";
	public static final String VIEW_ADMIN_KEYWORD_CODE_EDIT = "_adminKeywordCodeEdit";
	public static final String VIEW_ADMIN_KEYWORD_VALUE_CREATE = "_adminKeywordValueCreate";
	public static final String VIEW_ADMIN_KEYWORD_VALUE_DELETE = "_adminKeywordValueDelete";
	public static final String VIEW_ADMIN_KEYWORD_VALUE_EDIT = "_adminKeywordValueEdit";
	public static final String VIEW_ADMIN_PUBLISH_TYPE_CODE_VIEW = "_adminPublishTypeCodeView";
	public static final String VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE = "_adminPublishTypeCodeCreate";
	public static final String VIEW_ADMIN_PUBLISH_TYPE_CODE_DELETE = "_adminPublishTypeCodeDelete";
	public static final String VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT = "_adminPublishTypeCodeEdit";
	public static final String VIEW_ADMIN_JOB_THROTTLE_CONFIG = "_adminJobThrottleConfig";
	public static final String VIEW_ADMIN_STATE_CODE_VIEW = "_adminStateCodeView";
	public static final String VIEW_ADMIN_STATE_CODE_CREATE = "_adminStateCodeCreate";
	public static final String VIEW_ADMIN_STATE_CODE_DELETE = "_adminStateCodeDelete";
	public static final String VIEW_ADMIN_STATE_CODE_EDIT = "_adminStateCodeEdit";
	public static final String VIEW_ADMIN_OUTAGE_ACTIVE_LIST = "_adminOutageActiveList";
	public static final String VIEW_ADMIN_OUTAGE_FULL_LIST = "_adminOutageFullList";
	public static final String VIEW_ADMIN_OUTAGE_CREATE = "_adminOutageCreate";
	public static final String VIEW_ADMIN_OUTAGE_DELETE = "_adminOutageDelete";
	public static final String VIEW_ADMIN_OUTAGE_EDIT = "_adminOutageEdit";
	public static final String VIEW_BOOK_DEFINITION_BULK_PROMOTION = "_bookDefinitionBulkPromotion";
	public static final String VIEW_BOOK_DEFINITION_CREATE = "_bookDefinitionCreate";
	public static final String VIEW_BOOK_DEFINITION_EDIT = "_bookDefinitionEdit";
	public static final String VIEW_BOOK_DEFINITION_COPY = "_bookDefinitionCopy";
	public static final String VIEW_BOOK_DEFINITION_ERROR_LOCKED = "_bookDefinitionErrorLocked";
	public static final String VIEW_BOOK_DEFINITION_ERROR_QUEUED = "_bookDefinitionErrorQueued";
	public static final String VIEW_BOOK_DEFINITION_IMPORT = "_bookDefinitionImportView";
	public static final String VIEW_BOOK_DEFINITION_PROMOTION = "_bookDefinitionPromotion";
	public static final String VIEW_BOOK_DEFINITION_DELETE = "_bookDefinitionDelete";
	public static final String VIEW_BOOK_DEFINITION_RESTORE = "_bookDefinitionRestore";
	public static final String VIEW_BOOK_DEFINITION_VIEW = "_bookDefinitionView";
	public static final String VIEW_BOOK_LIBRARY_ICONS = "_bookLibraryIcons";
	public static final String VIEW_BOOK_LIBRARY_LIST = "_bookLibraryList";
	public static final String VIEW_BOOK_LIBRARY_THUMBNAILS = "_bookLibraryThumbnails";
	public static final String VIEW_BOOK_GENERATE_BULK_PREVIEW = "_bookGenerateBulkPreview";
	public static final String VIEW_BOOK_GENERATE_PREVIEW = "_bookGeneratePreview";
	public static final String VIEW_BOOK_AUDIT = "_bookAudit";
	public static final String VIEW_BOOK_AUDIT_DETAIL = "_bookAuditDetail";
	public static final String VIEW_BOOK_AUDIT_LIST = "_bookAuditList";
	public static final String VIEW_FRONT_MATTER_PREVIEW = "_bookDefinitionFrontMatterPreview";
	public static final String VIEW_FRONT_MATTER_PREVIEW_CONTENT = "book/fmpreview/content/preview"; // "_bookDefinitionFrontMatterPreviewContent";
	public static final String VIEW_JOB_EXECUTION_DETAILS = "_jobExecutionDetails";
	public static final String VIEW_JOB_INSTANCE_DETAILS = "_jobInstanceDetails";
	public static final String VIEW_JOB_QUEUE = "_jobQueue";
	public static final String VIEW_JOB_STEP_EXECUTION_DETAILS = "_jobStepDetails";
	public static final String VIEW_JOB_SUMMARY = "_jobSummary";
	public static final String VIEW_BOOK_JOB_HISTORY = "_eBookJobHistory";
	public static final String VIEW_BOOK_JOB_METRICS = "_eBookJobMetrics";
	public static final String VIEW_SEC_ACCESS_DENIED = "accessDenied";
	public static final String VIEW_SEC_LOGIN = "_login";
	public static final String VIEW_SEC_LOGIN_AUTO = "security/autoLogin";
	public static final String VIEW_ERROR_BOOK_DELETED = "_errorBookDeleted";
	public static final String VIEW_ERROR_BOOK_DEFINTION = "_errorBookDefinition";
	public static final String VIEW_SMOKE_TEST = "smokeTest";
	public static final String VIEW_PROVIEW_TITLES = "_proviewTitles";
	public static final String VIEW_PROVIEW_TITLE_ALL_VERSIONS = "_proviewTitleAllVersions";
	public static final String VIEW_PROVIEW_TITLE_REMOVE = "_proviewTitleRemove";
	public static final String VIEW_PROVIEW_TITLE_DELETE = "_proviewTitleDelete";
	public static final String VIEW_PROVIEW_TITLE_PROMOTE = "_proviewTitlePromote";
	public static final String VIEW_USER_PREFERENCES = "_userPreferences";

	// Miscellaneous keys and constants used throughout the application.
	// public enum SortProperty { TITLE_ID, AUTHOR }; // Book Library SortFields
	public static final String TITLE_ID = "fullyQualifiedTitleId";
	public static final String TITLE = "title";
	public static final int NUMBER_BOOK_DEF_SHOWN = 10;

}
