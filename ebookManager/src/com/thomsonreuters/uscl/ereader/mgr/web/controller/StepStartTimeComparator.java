package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.util.Comparator;

import org.springframework.batch.core.StepExecution;

/**
 * Sort job step execution times into descending order for presentation.
 */
public class StepStartTimeComparator implements Comparator<StepExecution> {
	public int compare(StepExecution se1, StepExecution se2) {
		int result = 0;
		if (se1.getStartTime() != null) {
			result = se1.getStartTime().compareTo(se2.getStartTime());
		}
		return -result;
	}
}
