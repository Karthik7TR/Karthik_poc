package com.thomsonreuters.uscl.ereader.decider;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class NovusOrFileDecider implements JobExecutionDecider
{
    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution)
    {
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecution.getExecutionContext().get(JobExecutionKey.EBOOK_DEFINITION);
        if (SourceType.FILE.equals(bookDefinition.getSourceType()))
        {
            return new FlowExecutionStatus("NAS_FILE");
        }
        else
        {
            return new FlowExecutionStatus("NOVUS");
        }
    }
}
