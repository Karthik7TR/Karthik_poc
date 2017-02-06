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

/**
 * Finds job name
 * 
 * @author Ilia Bochkarev UC220946
 *
 */
public class JobNameProviderImpl implements JobNameProvider {

	@NotNull
	private static final String GENERATOR_JOB_NAME = "ebookGeneratorJob";
	@NotNull
	private static final String XPP_GENERATOR_JOB_NAME = "ebookGeneratorXppJob";

	@Override
	@NotNull
	public String getJobName(@NotNull JobRequest jobRequest) {
		BookDefinition book = jobRequest.getBookDefinition();
		if (book == null) {
			throw new IllegalStateException(
					"No Book Definition found for Job Request ID=" + jobRequest.getJobRequestId());
		}
		return getJobName(book);
	}

	@Override
	@NotNull
	public String getJobName(@NotNull BookDefinition book) {
		return book.getSourceType() == SourceType.XPP ? XPP_GENERATOR_JOB_NAME : GENERATOR_JOB_NAME;
	}

}
