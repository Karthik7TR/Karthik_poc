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
	
	// Page paths
	public static final String MVC_APP_EXCEPTION = "appException.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST = "bookLibraryList.mvc";
	public static final String MVC_BOOK_LIBRARY_THUMBNAILS = "book/library/thumbnails.mvc";
	public static final String MVC_BOOK_LIBRARY_ICONS = "book/library/icons.mvc";
	public static final String MVC_BOOK_DEFINITION_VIEW = "book/definition/view.mvc";
	public static final String MVC_BOOK_LIBRARY_LIST_PAGING = "bookLibraryListPaging.mvc";

	// Logical view names
	public static final String VIEW_HOME = "home";
	public static final String VIEW_APP_EXCEPTION = "appException";
	public static final String VIEW_BOOK_LIBRARY_LIST = "_bookLibraryList";
	public static final String VIEW_BOOK_LIBRARY_THUMBNAILS = "_bookLibraryThumbnails";
	public static final String VIEW_BOOK_LIBRARY_ICONS = "_bookLibraryIcons";
	public static final String VIEW_BOOK_DEFINITION_VIEW = "_bookDefinitionView";
	
	// Miscellaneous keys and constants used throughout the application.
	public enum SortProperty { TITLE_ID, AUTHOR };  // Book Library SortFields

}
