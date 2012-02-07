/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.BookLibrarySortProperty;

/**
 * A DisplayTag PaginatedList implementation for paging through the part lists
 * of job executions. Note that the JobExecution business object is wrappered by
 * a JobExecutionVdo to allow for the exposing of presentation related
 * properties.
 */
public class BookLibraryPaginatedList implements PaginatedList {
	private static final Logger log = Logger
			.getLogger(BookLibraryPaginatedList.class);
	private Comparator<BookLibraryVdo> bookTitleIdComparator = new BookTitleIdComparator();
	private Map<BookLibrarySortProperty, Comparator<BookLibraryVdo>> comparatorMap = createComparatorMap(bookTitleIdComparator);
	private List<BookLibraryVdo> partialList;
	private int pageNumber; // Which page number of data is this
	private int fullListSize; // The size of the entire population of elements
								// that are to be displayed in a paginated
								// fashion
	private int itemsPerPage; // How many rows are to be shown on each page
	private BookLibrarySortProperty BookLibrarySortProperty; // Indicated the
																// JobExecution
																// property that
																// we want to
																// sort by
	private boolean ascending; // True if the list is sorted in the ascending
								// direction

	/**
	 * Create the PaginatedList used for paging and sorting operations by
	 * DisplayTag. None the parameters may be null.
	 */
	public BookLibraryPaginatedList(List<BookLibraryVdo> partialList,
			int fullListSize, int pageNumber, int itemsPerPage,
			BookLibrarySortProperty property, boolean ascending) {
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
		this.BookLibrarySortProperty = property;
		this.ascending = ascending;
		this.sortList();
	}

	@Override
	public int getFullListSize() {
		return fullListSize;
	}

	@Override
	public List<BookLibraryVdo> getList() {
		return partialList;
	}

	@Override
	public int getObjectsPerPage() {
		return itemsPerPage;
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public String getSearchId() {
		return null;
	}

	@Override
	/** Returns of of the BookLibrarySortProperty values */
	public String getSortCriterion() {
		return BookLibrarySortProperty.toString();
	}

	@Override
	public SortOrderEnum getSortDirection() {
		return (ascending) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}

	public boolean isAscendingSort() {
		return ascending;
	}

	public void sortList() {
		sortList(BookLibrarySortProperty, ascending);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sortList(BookLibrarySortProperty property, boolean ascendingSort) {
		this.BookLibrarySortProperty = property;
		this.ascending = ascendingSort;
		Comparator comparator = comparatorMap.get(BookLibrarySortProperty);
		Collections.sort(partialList, comparator);
	}

	/**
	 * Creates a map of the sort property key to the comparator value used to
	 * sort the partialList by that property.
	 */
	private Map<BookLibrarySortProperty, Comparator<BookLibraryVdo>> createComparatorMap(
			Comparator<BookLibraryVdo> startTimeComparator) {
		Map<BookLibrarySortProperty, Comparator<BookLibraryVdo>> map = new HashMap<BookLibrarySortProperty, Comparator<BookLibraryVdo>>();
		map.put(BookLibrarySortProperty.TITLE_ID, new BookTitleIdComparator());
		map.put(BookLibrarySortProperty.AUTHOR, new BookAuthorComparator());
		return map;
	}

	class BookComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo booklib1, BookLibraryVdo booklib2) {
			int result = 0;
			String book1 = booklib1.getBookName();
			String book2 = booklib2.getBookName();
			if (book1 != null) {
				if (book2 != null) {
					result = book1.compareTo(book2);
				} else {
					result = 1;
				}
			} else {
				result = -1;
			}
			return ((ascending) ? result : -result);
		}
	}

	class BookTitleIdComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo booklib1, BookLibraryVdo booklib2) {
			int result = 0;
			if (booklib1.getTitleId() != null) {
				result = booklib1.getTitleId().compareTo(booklib2.getTitleId());
			}
			return ((ascending) ? result : -result);
		}
	}

	class BookAuthorComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo booklib1, BookLibraryVdo booklib2) {
			int result = 0;
			if (booklib1.getAuthor() != null) {
				result = booklib1.getAuthor().compareTo(booklib2.getAuthor());
			}
			return ((ascending) ? result : -result);
		}
	}

}
