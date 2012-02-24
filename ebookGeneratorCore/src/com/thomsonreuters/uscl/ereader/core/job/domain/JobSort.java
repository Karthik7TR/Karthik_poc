/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Holds sorting and paging data used in queries.
 */
public class JobSort {
	
	public enum SortProperty { jobInstanceId, jobExecutionId, startTime, batchStatus, stringVal };
	public enum SortParmeterKeyName { bookName, titleIdFullyQualified };
	
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
	private SortParmeterKeyName jobParameterKeyName;  // like "titleIdFullyQualified"
	
	/** true if ascending sort, false if descending sort */
	private boolean ascending;
	
	/**
	 * Default sort is by job start time descending.
	 */
	public JobSort() {
		this(SortProperty.startTime, false);
	}
	
	/**
	 * Used to indicate that we are sorting on a job parameter
	 * @param keyName key_name in the job_params table
	 * @param ascending true for an ascending direction sort
	 */
	public JobSort(SortParmeterKeyName keyName, boolean ascending) {
		this(SortProperty.stringVal, ascending);
		this.jobParameterKeyName = keyName;
	}
	
	/**
	 * Used to indicate that we are sorting on a job execution property.
	 * @param sortProperty which property in the job execution
	 * @param ascending true for an ascending direction sort
	 */
	public JobSort(SortProperty sortProperty, boolean ascending) {
		this.sortProperty = sortProperty;
		this.ascending = ascending;
	}
	
	public boolean isJobParameterSort() {
		return (jobParameterKeyName != null); 
	}

	public String getSortProperty() {
		return sortProperty.toString();
	}
	public SortParmeterKeyName getJobParameterKeyName() {
		return jobParameterKeyName;
	}
	public boolean isAscending() {
		return ascending;
	}
	public String getSortDirection() {
		return getSortDirection(ascending);
	}
	public static String getSortDirection(boolean anAsendingSort) {
		return (anAsendingSort) ? "asc" : "desc";
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
