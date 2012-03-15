package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Comparator;

/**
 * Comparator to sort into the natural run order of jobs.
 * Priority first (higher the number, the higher the priority), and then if tie, submit time.
 */
public class JobRequestRunOrderComparator implements Comparator<JobRequest> {
	 public  int compare(JobRequest r1, JobRequest r2) {
		 if (r1.getPriority() < r2.getPriority()) {
			 return 1;
		 } else if (r1.getPriority() > (r2.getPriority())) {
			 return -1;
		 } else {  // same priority, use submit time
			 if (r1.getSubmittedAt().before(r2.getSubmittedAt())) {
				 return 1;
			 } else if (r1.getSubmittedAt().after(r2.getSubmittedAt())) {
				 return -1;
			 }
		 }
		return 0;
	}
}
