package com.thomsonreuters.uscl.ereader.core.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Search criteria to be applied to searches for jobs with specific job parameters.
 *
 */
@Deprecated
public class ParameterFilter {
	
	private String titleId;
	
	/**
	 * Returns true if the specified job execution matches the filter.
	 * @param execution the job execution to compare against.
	 * @return true if match
	 */
//	public boolean matches(JobExecution execution) {
//		if (!hasAnyValues()) {
//			return true;
//		}
//		JobInstance instance = execution.getJobInstance();
//		JobParameters parameters = instance.getJobParameters();
//		parameters.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
//		if (titleId.equals(parameters.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED))) {
//			return true;
//		}
//		return false;
//	}
	
	public String getTitleId() {
		return titleId;
	}
	
	public boolean hasAnyValues() {
		return (StringUtils.isNotBlank(titleId));
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
