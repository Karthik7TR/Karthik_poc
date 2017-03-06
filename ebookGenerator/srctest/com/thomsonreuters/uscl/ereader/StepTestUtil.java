package com.thomsonreuters.uscl.ereader;

import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public final class StepTestUtil
{
    private StepTestUtil()
    {
    }

    public static void givenJobInstanceId(final ChunkContext chunkContext, final Long id)
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(id);
    }

    public static void givenJobExecutionId(final ChunkContext chunkContext, final Long id)
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecutionId()).willReturn(id);
    }

    public static void givenJobParameter(
        final ChunkContext chunkContext,
        final String parameterName,
        final String value)
    {
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString(parameterName))
            .willReturn(value);
    }

    public static void givenJobParameter(final ChunkContext chunkContext, final String parameterName, final Long value)
    {
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getLong(parameterName))
            .willReturn(value);
    }

    public static void givenJobExecutionContext(
        final ChunkContext chunkContext,
        final ExecutionContext executionContext)
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext())
            .willReturn(executionContext);
    }

    public static void givenStepExecution(final ChunkContext chunkContext, final StepExecution stepExecution)
    {
        given(chunkContext.getStepContext().getStepExecution()).willReturn(stepExecution);
    }

    public static void givenStepName(final ChunkContext chunkContext, final String value)
    {
        given(chunkContext.getStepContext().getStepName()).willReturn(value);
    }

    public static void givenBook(final ChunkContext chunkContext, final BookDefinition book)
    {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("bookDefn"))
            .willReturn(book);
    }

    public static void givenBookDefinitionId(final ChunkContext chunkContext, final Long value)
    {
        givenJobParameter(chunkContext, JobParameterKey.BOOK_DEFINITION_ID, value);
    }

    public static void givenUserName(final ChunkContext chunkContext, final String value)
    {
        givenJobParameter(chunkContext, JobParameterKey.USER_NAME, value);
    }

    public static void givenEnvironment(final ChunkContext chunkContext, final String value)
    {
        givenJobParameter(chunkContext, JobParameterKey.ENVIRONMENT_NAME, value);
    }

    public static void givenBookVersion(final ChunkContext chunkContext, final String value)
    {
        givenJobParameter(chunkContext, JobParameterKey.BOOK_VERSION_SUBMITTED, value);
    }
}
