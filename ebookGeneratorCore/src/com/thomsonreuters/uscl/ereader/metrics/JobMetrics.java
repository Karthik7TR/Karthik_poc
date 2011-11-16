package com.thomsonreuters.uscl.ereader.metrics;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.batch.core.JobParameter;

/**
 * Extensible job metrics container used to hold typed key/value pairs representing the various job/step execution metrics we are interested in collecting.
 * At job start, this class is instantiated and added to the the Job ExecutionContext under the key String: JobMetrics.class.getName().
 * This class and the values contained therein will then be persisted along with the rest of the Job ExecutionContext at the end of each job step.
 * For reporting, this container can be fetched from the JobRespository database by using the JobExplorer to find the specific JobExecution(s) we are interested in.
 * Thus this persisted object is at: JobExplorer.getJobExecution(someId).getExecutionContext().get(JobMetrics.class.getName());
 *
 */
public class JobMetrics implements Serializable {
	private static final long serialVersionUID = -7337752980500875000L;
	
	private Map<String,JobParameter> metrics;
	
	public JobMetrics() {
		this.metrics = new HashMap<String,JobParameter>();
	}
	
	public Date getDate(String key) {
		return (Date) getValue(key);
	}
	public Double getDouble(String key) {
		return (Double) getValue(key);
	}
	public Long getLong(String key) {
		return (Long) getValue(key);
	}
	public String getString(String key) {
		return (String) getValue(key);
	}
	
	/**
	 * Add a specified amount to the Long value with the given key.
	 * @param longKey the name of the metric (assumed to be a long)
	 * @param amount add this much to the existing value or create a new key/value pair under the key if the keys does not already exist.
	 */
	public void addLong(String longKey, long amount) {
		Long existingvalue = getLong(longKey);
		Long newValue = (existingvalue != null) ? new Long(existingvalue.longValue() + amount) : new Long(amount);
		setLong(longKey, newValue);
	}

	public void setDate(String key, Date date) {
		metrics.put(key, new JobParameter(date));
	}
	public void setDouble(String key, Double doub) {
		metrics.put(key, new JobParameter(doub));
	}
	public void setLong(String key, Long lng) {
		metrics.put(key, new JobParameter(lng));
	}
	public void setString(String key, String str) {
		metrics.put(key, new JobParameter(str));
	}
	
	private Object getValue(String key) {
		Object value = null;
		JobParameter param = metrics.get(key);
		if (param != null) {
			value = param.getValue();
		}
		return value;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
