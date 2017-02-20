package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;

public interface BaseStep extends Tasklet
{
    /**
     * Implement this method in the concrete subclass.
     *
     * @return the transition name for the step in the form of an ExitStatus. Return ExitStatus.COMPLETED for a normal
     *         finish. Returning a custom ExitStatus will always result in the step BatchStatus being set to
     *         BatchStatus.COMPLETED.
     */
    ExitStatus executeStep() throws Exception;

    long getJobInstanceId();

    long getJobExecutionId();

    @Nullable
    String getJobParameterString(@NotNull String key);

    @Nullable
    Long getJobParameterLong(@NotNull String key);

    @Nullable
    Date getJobParameterDate(@NotNull String key);

    StepExecution getStepExecution();

    String getStepName();

    JobParameters getJobParameters();

    ExecutionContext getJobExecutionContext();

    String getJobExecutionPropertyString(String propertyKey);
}
