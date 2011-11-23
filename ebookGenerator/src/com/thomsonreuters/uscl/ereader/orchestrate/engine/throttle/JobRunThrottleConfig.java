package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encapsulates the day of the week schedule curves that indicate the maximum number of concurrent
 * jobs that can run at any one time.
 */
class JobRunThrottleConfig implements ThrottleConfig {
	
	/** The day-of-week to schedule-name association, like MONDAY(2)=WEEKDAY or SATURDAY(7)=WEEKEND (1..7) but indexed 0..6 */
	private List<ScheduleDayOfWeek> daysOfWeekSchedule;
	
	/** The collection of named curves where the name is the schedule name, like "WEEKDAY" */
	private Collection<ScheduleCurve> curves;
	
	public JobRunThrottleConfig(List<ScheduleDayOfWeek> daysOfWeekSchedule, Collection<ScheduleCurve> curves) {
		this.daysOfWeekSchedule = daysOfWeekSchedule;
		this.curves = curves;
	}
	
	@Override
	public int getMaximum() {
		// First, what is the name of the schedule for today?
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=SUNDAY, 7=SATURDAY
		ScheduleDayOfWeek schedule = null;
		for (ScheduleDayOfWeek sched : daysOfWeekSchedule) {
			if (sched.getDayOfWeek() == dayOfWeek) {
				schedule = sched;
				break;
			}
		}
		String scheduleName = schedule.getScheduleName();  // like "WEEKDAY"
		
		// Second, get the curve (function) for this day of the week
		ScheduleCurve scheduleCurve = null;
		for (ScheduleCurve curve : curves) {
			if (curve.getScheduleName().equals(scheduleName)) {
				scheduleCurve = curve;
				break;
			}
		}
		
		// Lastly, calculate the max concurrent jobs as a function of the current time (minutes since midnight).
		int minutesSinceMidnightNow = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);
		List<ScheduleCurvePoint>  scheduleCurvePoints = scheduleCurve.getPoints();
		ScheduleCurvePoint firstPoint = scheduleCurvePoints.get(0);
		int maxConcurrentJobs = firstPoint.getMaxConcurrentJobs();
		for (ScheduleCurvePoint point : scheduleCurvePoints) {
			if (point.getMinutesSinceMidnight() >= minutesSinceMidnightNow) {
				break;
			} else {
				maxConcurrentJobs = point.getMaxConcurrentJobs();
			}
		}
		return maxConcurrentJobs;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
