package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.Comparator;

import org.springframework.batch.core.StepExecution;

/**
 * Sort job step execution times into descending order for presentation.
 */
public class StepStartTimeComparator implements Comparator<StepExecution> {
    @Override
    public int compare(final StepExecution se1, final StepExecution se2) {
        int result = 0;
        if (se1.getStartTime() != null) {
            result = se1.getStartTime().compareTo(se2.getStartTime());
        }
        return -result;
    }
}
