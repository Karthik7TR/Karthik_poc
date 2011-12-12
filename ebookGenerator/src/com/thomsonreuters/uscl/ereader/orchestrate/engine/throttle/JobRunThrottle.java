package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

/**
 * Encapsulates the number of currently executing jobs along with the maximum number of concurrent jobs
 * that can run at any point in time.
 */
public class JobRunThrottle implements Throttle, InitializingBean {
	
	private EngineDao dao;
	private ThrottleConfig throttleConfig;
	
	public JobRunThrottle() {
		super();
	}
	
	public void afterPropertiesSet() {
		loadThrottleConfig();
	}
	
	@Override
	public boolean isAtMaximum() {
		return (getCount() >= throttleConfig.getMaximum());
	}
	
	/**
	 * Returns the number of currently executing jobs.
	 */
	@Override
	public int getCount() {
		return dao.getRunningJobExecutionCount();
	}

	/**
	 * Load from the database tables the function curves that define the maximum number
	 * of concurrent jobs that may run at a given point in time.  There can be several curves, each
	 * with their own name (schedule name) like "WEEKDAY" or "WEEKEND".
	 */
	@Transactional(readOnly = true)
	public void loadThrottleConfig() {
		// The collection of named curves where the name is the schedule name, like "WEEKDAY"
		Collection<ScheduleCurve> curves = new ArrayList<ScheduleCurve>();

		// Gather the mapping of day of the week to schedule name
		List<ScheduleDayOfWeek> dayOfWeekSchedule = dao.findDayOfWeekSchedule();
		
		// Group all the curve points by their schedule curve name, i.e. each curve has a unique name
		List<ScheduleCurvePoint> curvePoints = dao.findAllThrottleScheduleCurvePoints();
		for (ScheduleCurvePoint point : curvePoints) {
			ScheduleCurve curve = findCurveByScheduleName(curves, point.getScheduleName());
			if (curve == null) {
				curve = new ScheduleCurve(point.getScheduleName());
				curves.add(curve);
			}
			curve.addPoint(point);
		}
		
		// Sort all the points that comprise the curve into ascending time (x) order
		for (ScheduleCurve curve : curves) {
			curve.sort();
		}
		this.throttleConfig = new JobRunThrottleConfig(dayOfWeekSchedule, curves);
	}
	
	private static ScheduleCurve findCurveByScheduleName(
				Collection<ScheduleCurve> curves, String schedName) {
		for (ScheduleCurve curve : curves) {
			if (curve.getScheduleName().equals(schedName)) {
				return curve;
			}
		}
		return null;
	}
	@Required
	public void setEngineDao(EngineDao dao) {
		this.dao = dao;
	}
}
