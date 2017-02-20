package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class BaseStepImpl implements BaseStep
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

    @Override
    public long getJobInstanceId()
    {
        return getStepExecution().getJobExecution().getJobInstance().getId();
    }

    @Override
    public long getJobExecutionId()
    {
        return getStepExecution().getJobExecutionId();
    }

    @Override
    @Nullable
    public String getJobParameterString(@NotNull final String key)
    {
        return getJobParameters().getString(key);
    }

    @Override
    @Nullable
    public Long getJobParameterLong(@NotNull final String key)
    {
        return getJobParameters().getLong(key);
    }

    @Override
    @Nullable
    public Date getJobParameterDate(@NotNull final String key)
    {
        return getJobParameters().getDate(key);
    }

    @Override
    public StepExecution getStepExecution()
    {
        return chunkContext.getStepContext().getStepExecution();
    }

    @Override
    public String getStepName()
    {
        return chunkContext.getStepContext().getStepName();
    }

    @Override
    public JobParameters getJobParameters()
    {
        return getStepExecution().getJobParameters();
    }

    @Override
    public ExecutionContext getJobExecutionContext()
    {
        return getStepExecution().getJobExecution().getExecutionContext();
    }

    @Override
    public String getJobExecutionPropertyString(final String propertyKey)
    {
        assertPropertyExists(propertyKey);
        return getJobExecutionContext().getString(propertyKey);
    }

    private void assertPropertyExists(final String propertyKey)
    {
        if (!getJobExecutionContext().containsKey(propertyKey))
        {
            throw new IllegalArgumentException(
                "The required property '"
                    + propertyKey
                    + "' was not present in the execution context, but should have been. "
                    + "This is considered a programming error, please contact development. The job cannot continue until the '"
                    + propertyKey
                    + "' property is present.");
        }
    }
}
