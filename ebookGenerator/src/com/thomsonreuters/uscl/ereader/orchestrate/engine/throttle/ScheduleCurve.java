package com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A unique max concurrent job schedule identified by name.
 * This is the grouping of all the points from the curve table into a single
 * set of points that comprise the curve for a given schedule/curve name.
 */
public class ScheduleCurve {
	
	/** Logical curve name applied to the points that comprise it. */
	private String scheduleName;
	
	/**
	 * x = minutes after midnight (time), y = maximum concurrent jobs.
	 * It is assumed that the points in the curve will be stored in minutesSinceMidnight (x) ascending order.  
	 */
	private List<ScheduleCurvePoint> points;
	
	
	public ScheduleCurve(String schedName) {
		this.scheduleName = schedName;
		this.points = new ArrayList<ScheduleCurvePoint>();
	}
	
	public String getScheduleName() {
		return scheduleName;
	}
	public List<ScheduleCurvePoint> getPoints() {
		return points;
	}
	public void addPoint(ScheduleCurvePoint point) {
		points.add(point);
	}
	public void sort() {
		Collections.sort(points);
	}
}
