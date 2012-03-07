package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.PageAndSort.DisplayTagSortProperty;

public class JobSummaryForm {
	
	public static final String FORM_NAME = "jobSummaryForm";
	
	public enum JobCommand { STOP_JOB, RESTART_JOB, CHANGE_OBJECTS_PER_PAGE};
	
	private JobCommand command;
	private Long[] 	jobExecutionIds;
	private PageAndSort pageAndSort = new PageAndSort();	// sort, page, dir, objectsPerPage

	/** Discriminator between which post action is to be performed. */
	public JobCommand getJobCommand() {
		return command;
	}
	/** Selected Job execution ID for multi-select. */
	public Long[] getJobExecutionIds() {
		return jobExecutionIds;
	}
	public String getDir() {
		return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
	}
	public Integer getPage() {
		return pageAndSort.getPageNumber();
	}
	public DisplayTagSortProperty getSort() {
		return pageAndSort.getSortProperty();
	}
	public Integer getObjectsPerPage() {
		return pageAndSort.getObjectsPerPage();
	}
	public boolean isAscendingSort() {
		return pageAndSort.isAscendingSort();
	}
	public void setJobCommand(JobCommand command) {
		this.command = command;
	}
	public void setJobExecutionIds(Long[] ids) {
		this.jobExecutionIds = ids;
	}
	public void setDir(String direction) {
		pageAndSort.setAscendingSort("asc".equals(direction));
	}
	public void setObjectsPerPage(Integer objectsPerPage) {
		pageAndSort.setObjectsPerPage(objectsPerPage);
	}
	public void setPage(Integer pageNumber) {
		pageAndSort.setPageNumber(pageNumber);
	}
	public void setSort(DisplayTagSortProperty sortProperty) {
		pageAndSort.setSortProperty(sortProperty);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
