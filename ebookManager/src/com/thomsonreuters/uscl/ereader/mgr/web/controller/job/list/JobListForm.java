package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty;

public class JobListForm {
	
	public static final String FORM_NAME = "jobListForm";
	
	public enum Command { STOP_JOB, RESTART_JOB, CHANGE_OBJECTS_PER_PAGE};
	
	private Command command;
	private Long[] 	jobExecutionIds;
	
	private PageAndSort pageAndSort = new PageAndSort();	// sort, page, dir, objectsPerPage

	/** Discriminator between which post action is to be performed. */
	public Command getCommand() {
		return command;
	}
	public String getCommandString() {
		return (command != null) ? command.toString() : null;
	}
	/** Selected Job execution ID for multi-select. */
	public Long[] getJobExecutionIds() {
		return jobExecutionIds;
	}
	public String getDir() {
		return pageAndSort.getDir();
	}
	public Integer getPage() {
		return pageAndSort.getPage();
	}
	public DisplayTagSortProperty getSort() {
		return pageAndSort.getSort();
	}
	public Integer getObjectsPerPage() {
		return pageAndSort.getObjectsPerPage();
	}
	public PageAndSort getPageAndSort() {
		return pageAndSort;
	}
	public void setCommand(Command command) {
		this.command = command;
	}
	public void setCommandString(String command) {
		this.command = Command.valueOf(command);
	}
	public void setJobExecutionIds(Long[] ids) {
		this.jobExecutionIds = ids;
	}
	public void setDir(String direction) {
		pageAndSort.setDir(direction);
	}
	public void setObjectsPerPage(Integer objectsPerPage) {
		pageAndSort.setObjectsPerPage(objectsPerPage);
	}
	public void setPage(Integer pageNumber) {
		pageAndSort.setPage(pageNumber);
	}
	public void setSort(DisplayTagSortProperty sortProperty) {
		pageAndSort.setSort(sortProperty);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
