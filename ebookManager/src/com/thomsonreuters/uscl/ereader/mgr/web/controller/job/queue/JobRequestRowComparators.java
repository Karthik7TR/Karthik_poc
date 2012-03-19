package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;


/**
 * Comparators used in the sorting of the JobRequestRow view data object (VDO).
 * The list of queue jobs to run can be sorted by book name and title ID as well as job related parameters.
 *
 */
@Deprecated
public class JobRequestRowComparators {
//	
//	public static class BookNameComparator implements Comparator<JobRequestRow> {
//		public int compare(JobRequestRow job1, JobRequestRow job2) {
//			return JobRequestRowComparators.compareStrings(job1.getBook().getFullBookName(), job2.getBook().getFullBookName());
//		}
//	}
//	public static class TitleIdComparator implements Comparator<JobRequestRow> {
//		public int compare(JobRequestRow job1, JobRequestRow job2) {
//			return JobRequestRowComparators.compareStrings(job1.getBook().getTitleId(), job2.getBook().getTitleId());
//		}
//	}
//	public static class BookVersionComparator implements Comparator<JobRequestRow> {
//		public int compare(JobRequestRow job1, JobRequestRow job2) {
//			return JobRequestRowComparators.compareStrings(job1.getJob().getBookVersion(), job2.getJob().getBookVersion());
//		}
//	}
//	public static class PriorityComparator implements Comparator<JobRequestRow> {
//		public int compare(JobRequestRow job1, JobRequestRow job2) {
//			return(JobRequestRunOrderComparator.compareJobPriority(
//						job1.getJob().getPriority(), job1.getJob().getSubmittedAt(),
//						job2.getJob().getPriority(), job2.getJob().getSubmittedAt()));
//		}
//	}
//	public static class SubmittedByComparator implements Comparator<JobRequestRow> {
//		public int compare(JobRequestRow job1, JobRequestRow job2) {
//			return JobRequestRowComparators.compareStrings(job1.getJob().getSubmittedBy(), job2.getJob().getSubmittedBy());
//		}
//	}
//	public static class SubmittedAtComparator implements Comparator<JobRequestRow> {
//		public int compare(JobRequestRow job1, JobRequestRow job2) {
//			return job1.getJob().getSubmittedAt().compareTo(job2.getJob().getSubmittedAt());
//		}
//	}
//	private static int compareStrings(String str1, String str2) {
//		int result = 0;
//		if (str1 != null) {
//			result = (str2 != null) ? str1.compareTo(str2) : 1;
//		} else {  // str1 is null
//			result = (str2 != null) ? -1 : 0;
//		}
//		return result;
//	}
}
