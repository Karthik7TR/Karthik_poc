package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * A view data object (VDO) used to hold data presented in a table of job requests.
 */
public class JobRequestRow {
	
	private JobRequest job;
	private BookDefinition book;

	public JobRequestRow(JobRequest job, BookDefinition book) {
		this.job = job;
		this.book = book;
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

