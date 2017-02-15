package com.thomsonreuters.uscl.ereader.core.job.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.jetbrains.annotations.NotNull;

public final class JobRequestMatchers
{
    @NotNull
    public static JobRequest jobRequest(final SourceType sourceType)
    {
        final JobRequest jobRequest = new JobRequest();
        jobRequest.setBookDefinition(book(sourceType));
        return jobRequest;
    }

    @NotNull
    public static BookDefinition book(final SourceType sourceType)
    {
        final BookDefinition book = new BookDefinition();
        book.setSourceType(sourceType);
        return book;
    }
}
