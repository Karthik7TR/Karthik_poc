package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JobListForm {
	
	public static final String FORM_NAME = "jobListForm";
	public static final int DEFAULT_ITEMS_PER_PAGE = 20;
	
	private Long[] 	jobId;
	private int 	itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
	
	// Paging and sorting query string parameters sent by DisplayTag are handled in the PageAndSortQueryString class

	/** Selected Job instance ID for multi-select */
	public Long[] getJobId() {
		return jobId;
	}
	/** How many rows at a time are shown */
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	public void setJobId(Long[] jobId) {
		this.jobId = jobId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
