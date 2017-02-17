package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class BaseStep implements Tasklet
{
    protected ChunkContext chunkContext;

    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext)
        throws Exception
    {
        this.chunkContext = chunkContext;
        final ExitStatus stepExitStatus = executeStep();
        getStepExecution().setExitStatus(stepExitStatus);
        return RepeatStatus.FINISHED;
    }

    /**
     * Implement this method in the concrete subclass.
     *
     * @return the transition name for the step in the form of an ExitStatus. Return ExitStatus.COMPLETED for a normal
     *         finish. Returning a custom ExitStatus will always result in the step BatchStatus being set to
     *         BatchStatus.COMPLETED.
     */
    protected abstract ExitStatus executeStep() throws Exception;

    public long getJobInstanceId()
    {
        return getStepExecution().getJobExecution().getJobInstance().getId();
    }

    public long getJobExecutionId()
    {
        return getStepExecution().getJobExecutionId();
    }

    @Nullable
    public String getJobParameterString(@NotNull final String key)
    {
        return getJobParameters().getString(key);
    }

    @Nullable
    public Long getJobParameterLong(@NotNull final String key)
    {
        return getJobParameters().getLong(key);
    }

    @Nullable
    public Date getJobParameterDate(@NotNull final String key)
    {
        return getJobParameters().getDate(key);
    }

    public StepExecution getStepExecution()
    {
        return chunkContext.getStepContext().getStepExecution();
    }

    public String getStepName()
    {
        return chunkContext.getStepContext().getStepName();
    }

    public JobParameters getJobParameters()
    {
        return getStepExecution().getJobParameters();
    }

    public ExecutionContext getJobExecutionContext()
    {
        return getStepExecution().getJobExecution().getExecutionContext();
    }
}
