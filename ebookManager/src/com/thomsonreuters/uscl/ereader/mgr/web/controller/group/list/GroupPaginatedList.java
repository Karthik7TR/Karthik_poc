package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.util.List;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupForm.DisplayGroupSortProperty;

public class GroupPaginatedList implements PaginatedList {
	
	private List<EbookGroup> partialList;
	private int pageNumber;		// Which page number of data is this
	private int fullListSize;   // The size of the entire population of elements that are to be displayed in a paginated fashion
	private int itemsPerPage;	// How many rows are to be shown on each page
	private DisplayGroupSortProperty sortProperty;	// Indicates the property that we want to sort by
	private boolean ascending;	// True if the list is sorted in the ascending direction
	
	/**
	 * Create the PaginatedList used for paging and sorting operations by DisplayTag.
	 * None the parameters may be null.
	 */
	public GroupPaginatedList(List<EbookGroup> partialList, int fullListSize,
							int pageNumber, int itemsPerPage,
							DisplayGroupSortProperty sortProperty, boolean ascending) {
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
	public List<EbookGroup> getList() {
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

