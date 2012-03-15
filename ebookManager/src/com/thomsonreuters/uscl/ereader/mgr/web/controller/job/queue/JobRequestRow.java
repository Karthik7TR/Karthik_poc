package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.Comparator;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

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
	public int compare(JobRequestRow row1, JobRequestRow row2) {
		Integer r1 = new Integer(row1.getSequence());
		Integer r2 = new Integer(row2.getSequence());
		int result = r1.compareTo(r2);
		return (ascending) ? result : -result;
	 }
}
