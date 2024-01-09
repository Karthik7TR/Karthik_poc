package com.thomsonreuters.uscl.ereader.stub;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

public class StubTasklet extends AbstractSbTasklet {
//	private static final Logger log = LogManager.getLogger(StubTasklet.class);

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final StepContext stepContext = chunkContext.getStepContext();
//		Map<String, Object> jobParams = stepContext.getJobParameters();
        final StepExecution stepExecution = stepContext.getStepExecution();
        final ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

        // Add an execution context key/value pair
        final Date date = new Date();
        final long time = date.getTime();
        jobExecutionContext.put("jec_key-" + time, time);
        stepExecutionContext.put("sec_key-" + time, time);

//		if (time > 0) throw new RuntimeException("Some bogus problem occured on " + date);  // TESTING

        return ExitStatus.COMPLETED;
    }
}
