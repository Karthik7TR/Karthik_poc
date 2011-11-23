package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name="BATCH_ER_DAY_SCHEDULE")
public class ScheduleDayOfWeek {
	
	private int dayOfWeek;		// like 1=SUNDAY ... 7=SATURDAY
	private String scheduleName;	// like "WEEKDAY" or "WEEKEND"
	
	public ScheduleDayOfWeek() {
		super();
	}
	
	@Column(name="DAY_OF_WEEK", nullable=false)
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	@Column(name="SCHEDULE_NAME", length=16, nullable=false)
	public String getScheduleName() {
		return scheduleName;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
