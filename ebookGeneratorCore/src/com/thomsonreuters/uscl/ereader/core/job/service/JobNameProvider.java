/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.service;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobNameProvider {
	
  /**
   * Returns job name for given job request
   * @param jobRequest job request
   * @return job name
   */
	@NotNull
	String getJobName(@NotNull JobRequest jobRequest);
	
	/**
	 * Returns job name for given book
	 * @param book book
	 * @return job name
	 */
	@NotNull
	String getJobName(@NotNull BookDefinition book);
}
