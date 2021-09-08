package com.thomsonreuters.uscl.ereader.decider;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Optional;

public class SingleOrCombinedBookDefinitionDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {
        return Optional.ofNullable((CombinedBookDefinition) jobExecution.getExecutionContext().get(JobExecutionKey.COMBINED_BOOK_DEFINITION))
                .map(book -> new FlowExecutionStatus("COMBINED"))
                .orElseGet(() -> new FlowExecutionStatus("SINGLE"));
    }
}
