package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Comparator;

/**
 * Comparators used in the sorting of the JobRequest object properties.
 * We do in-memory sorting of the full list of JobRequest objects for
 * tabular presentation paging/sorting on the Job Queue page.
 */
public class JobRequestComparators
{
    private static int compareStrings(final String str1, final String str2)
    {
        int result = 0;
        if (str1 != null)
        {
            result = (str2 != null) ? str1.compareTo(str2) : 1;
        }
        else
        { // str1 is null
            result = (str2 != null) ? -1 : 0;
        }
        return result;
    }

    public static class PriorityComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest r1, final JobRequest r2)
        {
            int result = (r1.getPriority() - r2.getPriority());
            if (result == 0)
            { // same priority, compare submit time (earliest submit runs first).
                result = r2.getSubmittedAt().compareTo(r1.getSubmittedAt());
            }
            return result;
        }
    }

    public static class BookNameComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest job1, final JobRequest job2)
        {
            return JobRequestComparators.compareStrings(
                job1.getBookDefinition().getProviewDisplayName(),
                job2.getBookDefinition().getProviewDisplayName());
        }
    }

    public static class TitleIdComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest job1, final JobRequest job2)
        {
            return JobRequestComparators
                .compareStrings(job1.getBookDefinition().getTitleId(), job2.getBookDefinition().getTitleId());
        }
    }

    public static class SourceTypeComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest job1, final JobRequest job2)
        {
            return JobRequestComparators.compareStrings(
                job1.getBookDefinition().getSourceType().toString(),
                job2.getBookDefinition().getSourceType().toString());
        }
    }

    public static class BookVersionComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest job1, final JobRequest job2)
        {
            return JobRequestComparators.compareStrings(job1.getBookVersion(), job2.getBookVersion());
        }
    }

    public static class SubmittedByComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest job1, final JobRequest job2)
        {
            return JobRequestComparators.compareStrings(job1.getSubmittedBy(), job2.getSubmittedBy());
        }
    }

    public static class SubmittedAtComparator implements Comparator<JobRequest>
    {
        @Override
        public int compare(final JobRequest job1, final JobRequest job2)
        {
            return job1.getSubmittedAt().compareTo(job2.getSubmittedAt());
        }
    }
}
