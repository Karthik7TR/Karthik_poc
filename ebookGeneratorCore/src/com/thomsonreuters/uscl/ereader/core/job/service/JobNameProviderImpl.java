package com.thomsonreuters.uscl.ereader.core.job.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.jetbrains.annotations.NotNull;

/**
 * Finds job name
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public class JobNameProviderImpl implements JobNameProvider
{
    @NotNull
    private static final String GENERATOR_JOB_NAME = "ebookGeneratorJob";
    @NotNull
    private static final String XPP_GENERATOR_JOB_NAME = "ebookGeneratorXppJob";

    @Override
    @NotNull
    public String getJobName(@NotNull final JobRequest jobRequest)
    {
        final BookDefinition book = jobRequest.getBookDefinition();
        if (book == null)
        {
            throw new IllegalStateException(
                "No Book Definition found for Job Request ID=" + jobRequest.getJobRequestId());
        }
        return getJobName(book);
    }

    @Override
    @NotNull
    public String getJobName(@NotNull final BookDefinition book)
    {
        return book.getSourceType() == SourceType.XPP ? XPP_GENERATOR_JOB_NAME : GENERATOR_JOB_NAME;
    }
}
