package com.thomsonreuters.uscl.ereader.decider;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;

import static org.junit.Assert.assertEquals;

public class SingleOrCombinedBookDefinitionDeciderTest {
    private static final long ID = 1L;
    private SingleOrCombinedBookDefinitionDecider decider;
    private JobExecution jobExecution;

    @Before
    public void setUp() throws Exception {
        decider = new SingleOrCombinedBookDefinitionDecider();
        jobExecution = new JobExecution(ID);
    }

    @Test
    public void shouldReturnCombined() {
        jobExecution.getExecutionContext().put(JobExecutionKey.COMBINED_BOOK_DEFINITION, new CombinedBookDefinition());
        FlowExecutionStatus actual = decider.decide(jobExecution, null);
        assertEquals("COMBINED", actual.getName());
    }

    @Test
    public void shouldReturnSingle() {
        FlowExecutionStatus actual = decider.decide(jobExecution, null);
        assertEquals("SINGLE", actual.getName());
    }
}
