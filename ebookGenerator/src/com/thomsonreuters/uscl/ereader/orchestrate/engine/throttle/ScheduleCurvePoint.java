package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A single point (x/y) of a max concurrent job schedule. 
 */
@Entity
@Table(name="BATCH_ER_MAX_JOBS_CURVE")
public class ScheduleCurvePoint implements Comparable<ScheduleCurvePoint>{
	
	private String scheduleName;
	private int minutesSinceMidnight;  	// x-axis, time offset since midnight of a given day (minutes)
	private int maxConcurrentJobs;		// y-axis, upper job run limit
	

	@Column(name="MINS_AFTER_MIDNIGHT", nullable=false)
	public int getMinutesSinceMidnight() {
		return minutesSinceMidnight;
	}
	@Column(name="MINS_AFTER_MIDNIGHT", nullable=false)
	public int getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}
	@Column(name="SCHEDULE_NAME", length=16, nullable=false)
	public String getScheduleName() {
		return scheduleName;
	}
	
	public void setMinutesSinceMidnight(int minutesSinceMidnight) {
		this.minutesSinceMidnight = minutesSinceMidnight;
	}
	public void setMaxConcurrentJobs(int maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	@Override
	public int compareTo(ScheduleCurvePoint that) {
		return (this.minutesSinceMidnight - that.minutesSinceMidnight); 
		
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
