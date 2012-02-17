package com.thomsonreuters.uscl.ereader.core.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.JobParameterKey;

/**
 * Holds sorting and paging data used in queries.
 */
public class JobSort {
	
	public enum SortProperty { bookName, titleId, jobInstanceId, startTime, batchStatus, executionDuration, stringVal }; // TODO: add others
	
	public static Map<SortProperty, String> map = new HashMap<SortProperty,String>();
	static {
		map.put(SortProperty.titleId, JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
		map.put(SortProperty.bookName, JobParameterKey.BOOK_NAME);
	}
	
	/**
	 * Java bean property on which sorting should occur - entity maps this to the phsical database column.
	 * For a job execution the value set would be one of the properties of class JobExecutionEntity;
	 * For job parameters the value set would be one of the value properties of class JobParameterEntity. 
	 */
	private SortProperty sortProperty;
	
	/**
	 * If sorting on one of the job parameters, what is the key name for the value to sort on.
	 * This is one of the constant key values within the JobParameterKey class.
	 * Works in concert with sortProperty which indicates which column in the JOB_PARAMS table or order by.
	 */
	private String jobParameterKeyName;  // like "titleIdFullyQualified"

	/** true if ascending sort, false if descending sort */
	private boolean ascending;
	
	public JobSort(SortProperty sortProperty, boolean ascending) {
		this.sortProperty = sortProperty;
		this.jobParameterKeyName = map.get(sortProperty);
		this.ascending = ascending;
	}

	public String getJobParameterKeyName() {
		return jobParameterKeyName;
	}
	public SortProperty getSortProperty() {
		return sortProperty;
	}
	public boolean isAscending() {
		return ascending;
	}
	public String getSortDirectionString() {
		return (ascending) ? "asc" : "desc";
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
