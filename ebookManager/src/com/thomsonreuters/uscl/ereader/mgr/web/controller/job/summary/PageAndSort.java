package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BasePageAndSort;

/**
 * Holds the sorting, paging, and displayed row count presentation information.
 */
public class PageAndSort extends BasePageAndSort {
	
	public enum DisplayTagSortProperty { TITLE_ID, BOOK_NAME, START_TIME, BATCH_STATUS, JOB_INSTANCE_ID, JOB_EXECUTION_ID }

	public PageAndSort() {
		super();
	}

	public PageAndSort(Integer pageNumber, Integer itemsPerPage,
					   DisplayTagSortProperty sortProperty, boolean ascendingSort) {
		super(pageNumber, itemsPerPage, sortProperty.toString(), ascendingSort);
	}
	
	public DisplayTagSortProperty getSortProperty() {
		return DisplayTagSortProperty.valueOf(getColumnId());
	}
	public void setSortProperty(DisplayTagSortProperty property) {
		setColumnId(property.toString());
	}
}
