/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.SortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryVdo;

/**
 * A DisplayTag PaginatedList implementation for paging through the part lists of job executions.
 * Note that the JobExecution business object is wrappered by a JobExecutionVdo to allow for the exposing
 * of presentation related properties.
 */
public class BookLibraryPaginatedList implements PaginatedList {
	//private static final Logger log = Logger.getLogger(JobExecutionPaginatedList.class);
	private Comparator<BookLibraryVdo> startTimeComparator = new StartTimeComparator();
	private Map<SortProperty, Comparator<BookLibraryVdo>> comparatorMap = createComparatorMap(startTimeComparator);
	
	private List<BookLibraryVdo> partialList;
	private int pageNumber;		// Which page number of data is this
	private int fullListSize;   // The size of the entire population of elements that are to be displayed in a paginated fashion
	private int itemsPerPage;	// How many rows are to be shown on each page
	private SortProperty sortProperty;	// Indicated the JobExecution property that we want to sort by
	private boolean ascending;	// True if the list is sorted in the ascending direction
	
	/**
	 * Create the PaginatedList used for paging and sorting operations by DisplayTag.
	 * None the parameters may be null.
	 */
	public BookLibraryPaginatedList(List<BookLibraryVdo> partialList, int fullListSize, 
									 int pageNumber, int itemsPerPage,
									 SortProperty property, boolean ascending) {
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
		this.sortProperty = property;
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
	/** Returns of of the SortProperty values */
	public String getSortCriterion() {
		return sortProperty.toString();
	}

	@Override
	public SortOrderEnum getSortDirection() {
		return (ascending) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}
	public boolean isAscendingSort() {
		return ascending;
	}
	
	public void sortList() {
		sortList(sortProperty, ascending);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void sortList(SortProperty property, boolean ascendingSort) {
		this.sortProperty = property;
		this.ascending = ascendingSort;
		Comparator comparator = comparatorMap.get(sortProperty);
		Collections.sort(partialList, comparator);
	}
	
	/**
	 * Creates a map of the sort property key to the comparator value used to sort the partialList by that property.
	 */
	private Map<SortProperty, Comparator<BookLibraryVdo>> createComparatorMap(Comparator<BookLibraryVdo> startTimeComparator) {
		Map<SortProperty, Comparator<BookLibraryVdo>> map = new HashMap<SortProperty, Comparator<BookLibraryVdo>>();
		map.put(SortProperty.BOOK, new BookComparator());
		map.put(SortProperty.INSTANCE_ID, new InstanceIdComparator());
		map.put(SortProperty.BATCH_STATUS, new BatchStatusComparator());
		map.put(SortProperty.START_TIME, startTimeComparator);
		map.put(SortProperty.EXECUTION_TIME, new ExecutionTimeComparator());
		return map;
	}
	
	class BookComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo je1, BookLibraryVdo je2) {
			int result = 0;
					String book1 = je1.getBookName();
					String book2 = je2.getBookName();
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
	
	class InstanceIdComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo je1, BookLibraryVdo je2) {
			int result = 0;
			if (je1.getJobExecution().getJobInstance().getId() != null) {
				result = je1.getJobExecution().getJobInstance().getId().compareTo(je2.getJobExecution().getJobInstance().getId());
			}
			return ((ascending) ? result : -result);
		}
	}

	class BatchStatusComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo je1, BookLibraryVdo je2) {
			int result = 0;
			if (je1.getJobExecution().getStartTime() != null) {
				result = je1.getJobExecution().getStatus().toString().compareTo(je2.getJobExecution().getStatus().toString());
			}
			return ((ascending) ? result : -result);
		}
	}
	
	class StartTimeComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo je1, BookLibraryVdo je2) {
			int result = 0;
			if (je1.getJobExecution().getStartTime() != null) {
				result = je1.getJobExecution().getStartTime().compareTo(je2.getJobExecution().getStartTime());
			}
			return ((ascending) ? result : -result);
		}
	}
	
	class ExecutionTimeComparator implements Comparator<BookLibraryVdo> {
		public int compare(BookLibraryVdo je1, BookLibraryVdo je2) {
			int result = 0;
			if (je1.getExecutionDurationMs() > -1) {
				result = (int) (je1.getExecutionDurationMs() - je2.getExecutionDurationMs());
			}
			return ((ascending) ? result : -result);
		}
	}
}	
