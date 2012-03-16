package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * A view data object (VDO) used to hold data presented in a table of job requests.
 */
public class JobRequestRow {
	
	private int sequence;
	private JobRequest job;
	private BookDefinition book;

	public JobRequestRow(int sequence, JobRequest job, BookDefinition book) {
		this.sequence = sequence;
		this.job = job;
		this.book = book;
	}

	/**
	 * Returns the position in line the job is for running, 1=first n=last.
	 */
	public int getSequence() {
		return sequence;
	}
	public JobRequest getJob() {
		return job;
	}
	public BookDefinition getBook() {
		return book;
	}
	public void setBookDefinition(BookDefinition book) {
		this.book = book;
	}
}

class SequenceComparator implements Comparator<JobRequestRow> {
	private boolean ascending;
	public SequenceComparator(boolean ascending) {
		this.ascending = ascending;
	}
	public int compare(JobRequestRow job1, JobRequestRow job2) {
		Integer seq1 = new Integer(job1.getSequence());
		Integer seq2 = new Integer(job2.getSequence());
		int result = seq1.compareTo(seq2);
		return (ascending) ? result : -result;
	 }
}
//class BookNameComparator implements Comparator<JobRequestRow> {
//	private boolean ascending;
//	public BookNameComparator(boolean ascending) {
//		this.ascending = ascending;
//	}
//	public int compare(JobRequestRow job1, JobRequestRow job2) {
//		Integer b1 = new Integer(job1.getBook().getBookName());
//		if (b1 != null) {
//			Integer b2 = new Integer(job2.getBook().getBookName());
//			int result = b1.compareTo(b2);
//		} 
//		return (ascending) ? result : -result;
//	 }
//}
