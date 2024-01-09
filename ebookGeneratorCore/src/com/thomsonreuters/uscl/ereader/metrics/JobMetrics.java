package com.thomsonreuters.uscl.ereader.metrics;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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

    private Map<String, JobParameter> metrics;

    public JobMetrics() {
        metrics = new HashMap<>();
    }

    public Date getDate(final String key) {
        return (Date) getValue(key);
    }

    public Double getDouble(final String key) {
        return (Double) getValue(key);
    }

    public Long getLong(final String key) {
        return (Long) getValue(key);
    }

    public String getString(final String key) {
        return (String) getValue(key);
    }

    /**
     * Add a specified amount to the Long value with the given key.
     * @param longKey the name of the metric (assumed to be a long)
     * @param amount add this much to the existing value or create a new key/value pair under the key if the keys does not already exist.
     */
    public void addLong(final String longKey, final long amount) {
        final Long existingvalue = getLong(longKey);
        final Long newValue =
            (existingvalue != null) ? Long.valueOf(existingvalue.longValue() + amount) : Long.valueOf(amount);
        setLong(longKey, newValue);
    }

    public void setDate(final String key, final Date date) {
        metrics.put(key, new JobParameter(date));
    }

    public void setDouble(final String key, final Double doub) {
        metrics.put(key, new JobParameter(doub));
    }

    public void setLong(final String key, final Long lng) {
        metrics.put(key, new JobParameter(lng));
    }

    public void setString(final String key, final String str) {
        metrics.put(key, new JobParameter(str));
    }

    private Object getValue(final String key) {
        Object value = null;
        final JobParameter param = metrics.get(key);
        if (param != null) {
            value = param.getValue();
        }
        return value;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
