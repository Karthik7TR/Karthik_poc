/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;


public class WebConstants {
	
	public static final String KEY_SESSION_LIBRARY_FORM = "sessionLibraryBookForm";	// Library search criteria saved on session
	public static final String KEY_PAGINATED_LIST = "paginatedList";
	public static final String KEY_TOTAL_BOOK_SIZE = "resultSize";
	public static final String KEY_BOOK_DEFINITION = "book";
	public static final String KEY_TITLE_ID = "titleId";
	public static final String KEY_SORT_BY = "sortBy";
	public static final String KEY_PAGE_NUMBER = "pageNumber";
	public static final String KEY_IS_ASCENDING = "isAscending";
	public static final String KEY_VDO = "vdo";
	public static final int KEY_NUMBER_BOOK_DEF_SHOWN = 10;
	public static final String KEY_INFO_MESSAGE = "infoMessage";
	public static final String KEY_ERR_MESSAGE = "errMessage";
	public static final String KEY_CONTENT_TYPES = "contentTypes";
	public static final String KEY_STATES = "states";
	public static final String KEY_YEARS = "years";
	public static final String KEY_PUB_TYPES = "pubTypes";
	public static final String KEY_JURISDICTIONS = "jurisdictions";
	public static final String KEY_KEYWORDS_TYPE = "typeKeywords";
	public static final String KEY_KEYWORDS_SUBJECT = "subjectKeywords";
	public static final String KEY_KEYWORDS_PUBLISHER = "publisherKeywords";
	public static final String KEY_KEYWORDS_JURISDICTION = "jurisdictionKeywords";
	public static final String KEY_COURT_RULES = "Court Rules";
	public static final String KEY_COURT_RULES_ABBR = "cr";
	public static final String KEY_ANALYTICAL = "Analytical";
	public static final String KEY_ANALYTICAL_ABBR = "an";
	public static final String KEY_SLICE_CODES = "Slice Codes";
	public static final String KEY_SLICE_CODES_ABBR = "sc";
	public static final String KEY_GENERATE_BUTTON_VISIBILITY = "generateButtonVisibility";
	public static final String KEY_GENERATE_BUTTON_ROLE="ROLE_SUPERUSER";
	
	// Page paths
	public static final String MVC_AFTER_LOGOUT = "afterLogout.mvc";
	public static final String MVC_APP_EXCEPTION = "appException.mvc";
	public static final String MVC_BOOK_LIBRARY_ICONS = "bookLibraryIcons.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST = "bookLibraryList.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_PAGING = "bookLibraryListPaging.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_SELECTION_POST = "bookLibraryListSelectionPost.mvc";
	public static final String MVC_BOOK_LIBRARY_THUMBNAILS = "bookLibraryThumbnails.mvc";
	public static final String MVC_BOOK_DEFINITION_IMPORT = "bookDefinitionImport.mvc";
	public static final String MVC_BOOK_DEFINITION_PROMOTION = "bookDefinitionPromotion.mvc";
	public static final String MVC_BOOK_DEFINITION_BULK_PROMOTION = "bookDefinitionBulkPromotion.mvc";
	public static final String MVC_BOOK_DEFINITION_CREATE = "bookDefinitionCreate.mvc";
	public static final String MVC_BOOK_DEFINITION_EDIT = "bookDefinitionEdit.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW_GET = "bookDefinitionViewGet.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW_POST = "bookDefinitionViewPost.mvc";
	public static final String MVC_BOOK_SINGLE_GENERATE_PREVIEW = "generateEbookPreview.mvc";
	public static final String MVC_BOOK_SINGLE_GENERATE_SUBMIT = "generateEbookSubmit.mvc";
	public static final String MVC_BOOK_BULK_GENERATE_PREVIEW = "generateBulkEbookPreview.mvc";
	
	public static final String MVC_JOB_LIST_GET = "jobListGet.mvc";
	public static final String MVC_JOB_LIST_PAGE_SORT = "jobListPageSort.mvc";
	public static final String MVC_JOB_LIST_POST = "jobListPost.mvc";
	
	public static final String MVC_PREFERENCES = "preferences.mvc";
	public static final String MVC_SUPPORT = "support.mvc";

	// Logical view names
	public static final String VIEW_APP_EXCEPTION = "appException";
	public static final String VIEW_BOOK_LIBRARY_LIST = "_bookLibraryList";
	public static final String VIEW_BOOK_LIBRARY_THUMBNAILS = "_bookLibraryThumbnails";
	public static final String VIEW_BOOK_LIBRARY_ICONS = "_bookLibraryIcons";
	public static final String VIEW_BOOK_DEFINITION_VIEW = "_bookDefinitionView";
	public static final String VIEW_BOOK_DEFINITION_EDIT = "_bookDefinitionEdit";
	public static final String VIEW_BOOK_DEFINITION_CREATE = "_bookDefinitionCreate";
	public static final String VIEW_BOOK_DEFINITION_IMPORT = "_bookDefinitionImportView";
	public static final String VIEW_BOOK_GENERATE_PREVIEW = "_bookGeneratePreview";
	public static final String VIEW_BOOK_GENERATE_BULK_PREVIEW = "_bookGenerateBulkPreview";
	public static final String VIEW_BOOK_DEFINITION_PROMOTION = "_bookDefinitionPromotion";
	public static final String VIEW_BOOK_DEFINITION_BULK_PROMOTION = "_bookDefinitionBulkPromotion";
	
	public static final String VIEW_JOB_LIST = "_jobList";
	
	// Miscellaneous keys and constants used throughout the application.
	public enum SortProperty { TITLE_ID, AUTHOR };  // Book Library SortFields
	public static final String TITLE_ID="fullyQualifiedTitleId";
	public static final String TITLE="title";

}
