package com.thomsonreuters.uscl.ereader.core.job.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.jetbrains.annotations.NotNull;

public interface JobNameProvider
{
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
