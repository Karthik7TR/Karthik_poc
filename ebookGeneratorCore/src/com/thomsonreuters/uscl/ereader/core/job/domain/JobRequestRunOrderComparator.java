package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Comparator;
import java.util.Date;

/**
 * Comparator to sort into the natural run order of jobs.
 * Priority first (higher the number, the higher the priority), and then if tie, submit time.
 */
public class JobRequestRunOrderComparator implements Comparator<JobRequest> {
    @Override
    public int compare(final JobRequest r1, final JobRequest r2) {
        return (compareJobPriority(r1.getPriority(), r1.getSubmittedAt(), r2.getPriority(), r2.getSubmittedAt()));
    }

    /**
     * Perform order based calculations based on priority and submit date.
     * We are implementing this as a static method because we need to perform a sort of similiar
     * objects elsewhere and we do not want to repeat ourselves in the logic, so we delegate to this method.
     */
    public static int compareJobPriority(
        final int priority1,
        final Date submittedAt1,
        final int priority2,
        final Date submittedAt2) {
        if (priority1 < priority2) {
            return 1;
        } else if (priority1 > (priority2)) {
            return -1;
        } else { // same priority, use submit time
            if (submittedAt1.before(submittedAt2)) {
                return 1;
            } else if (submittedAt1.after(submittedAt2)) {
                return -1;
            }
        }
        return 0;
    }
}
