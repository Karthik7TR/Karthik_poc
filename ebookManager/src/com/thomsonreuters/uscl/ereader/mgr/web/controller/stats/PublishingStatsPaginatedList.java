/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.List;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;


/**
 * A DisplayTag PaginatedList implementation for paging through the part lists of publishing stats.
 */
public class PublishingStatsPaginatedList implements PaginatedList {
	//private static final Logger log = Logger.getLogger(PublishingStatsPaginatedList.class);
	
	private List<PublishingStats> partialList;
	private int pageNumber;		// Which page number of data is this
	private int fullListSize;   // The size of the entire population of elements that are to be displayed in a paginated fashion
	private int itemsPerPage;	// How many rows are to be shown on each page
	private DisplayTagSortProperty sortProperty;	// Indicates the property that we want to sort by
	private boolean ascending;	// True if the list is sorted in the ascending direction
	
	/**
	 * Create the PaginatedList used for paging and sorting operations by DisplayTag.
	 * None the parameters may be null.
	 */
	public PublishingStatsPaginatedList(List<PublishingStats> partialList, int fullListSize,
							int pageNumber, int itemsPerPage,
							DisplayTagSortProperty sortProperty, boolean ascending) {
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
		this.sortProperty = sortProperty;
		this.ascending = ascending;
	}
	
	@Override
	public int getFullListSize() {
		return fullListSize;
	}

	@Override
	public List<PublishingStats> getList() {
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
}	