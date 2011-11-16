package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.batch.core.BatchStatus;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants.SortProperty;

/**
 * Form backing object for the Job Summary/Executions page.
 */
public class JobSummaryForm {
	public static final int DEFAULT_ITEMS_PER_PAGE = 20;
	public static final String FORM_NAME = "jobSummaryForm";
	
	// Filter fields set by user
	private String jobName;
	private String status;		// a BatchStatus enumeration value
	private String startDate;	// Start time in form: mm/dd/yyyy
	private int itemsPerPage;  	// maximum rows shown in table at one time, from the select
	
	// Paging and sorting query string parameters sent by DisplayTag
	private String page;	// page number user wants to see (integer)
	private String sort;	// one of SortProperty enum values: JOB_NAME | BATCH_STATUS | START_TIME | EXECUTION_TIME
	private String dir;		// sort direction, sent by DisplayTag, one of: asc | desc

	public void copyUserFields(JobSummaryForm other) {
		setJobName(other.getJobName());
		setStatus(other.getStatus());
		setStartDate(other.getStartDate());
		setItemsPerPage(other.getItemsPerPage());
	}

	public boolean isAscendingSort() {
		return "asc".equals(dir);
	}
	public String getDir() {
		return dir;
	}
	public BatchStatus getBatchStatus() {
		return (StringUtils.isNotBlank(status) ? BatchStatus.valueOf(status) : null);
	}
	public String getStatus() {
		return status;
	}
	public String getStartDate() {
		return startDate;
	}
	public Date getStartTime() {
		Date date = null;
		try {
			if (StringUtils.isNotBlank(startDate)) {
				String[] parsePatterns = { WebConstants.DATE_FORMAT_PATTERN };
				date = DateUtils.parseDate(startDate, parsePatterns);
			}
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}
	public int getItemsPerPage() {
		return itemsPerPage;
	}
	public String getJobName() {
		return jobName;
	}
	public String getPage() {
		return page;
	}
	public boolean isPagingOperation() {
		return StringUtils.isNotBlank(page);
	}
	public int getPageNumber() {
		int pageNumber = 1;
		if (StringUtils.isNotBlank(page) && StringUtils.isNumeric(page)) {
			pageNumber = Integer.parseInt(page);
		}
		return pageNumber;
	}
	public void setDir(String direction) {
		this.dir = direction;
	}
	public String getSort() {
		return sort;
	}
	public SortProperty getSortProperty() {
		SortProperty property = null;
		try {
			property = SortProperty.valueOf(sort);
		} catch (Exception e) {
			property = null;
		}
		return property;
	}
	
	public void setItemsPerPage(int count) {
		this.itemsPerPage = count;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setStartDate(String start) {
		this.startDate = start;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
