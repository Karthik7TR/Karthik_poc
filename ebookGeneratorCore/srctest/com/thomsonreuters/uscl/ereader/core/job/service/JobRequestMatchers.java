/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.service;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public class JobRequestMatchers {

	@NotNull
	public static JobRequest jobRequest(SourceType sourceType) {
		JobRequest jobRequest = new JobRequest();
		jobRequest.setBookDefinition(book(sourceType));
		return jobRequest;
	}
	
	@NotNull
	public static BookDefinition book(SourceType sourceType) {
		BookDefinition book = new BookDefinition();
		book.setSourceType(sourceType);
		return book;
	}
}
