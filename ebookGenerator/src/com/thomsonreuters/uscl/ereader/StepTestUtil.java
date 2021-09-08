package com.thomsonreuters.uscl.ereader;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public final class StepTestUtil {
    private StepTestUtil() {
    }

    public static void givenJobInstanceId(final ChunkContext chunkContext, final Long id) {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(id);
    }

    public static void givenJobExecutionId(final ChunkContext chunkContext, final Long id) {
        given(chunkContext.getStepContext().getStepExecution().getJobExecutionId()).willReturn(id);
    }

    public static void givenJobParameter(
        final ChunkContext chunkContext,
        final String parameterName,
        final String value) {
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString(parameterName))
            .willReturn(value);
    }

    public static void givenJobParameter(
        final ChunkContext chunkContext,
        final String parameterName,
        final Long value) {
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getLong(parameterName))
            .willReturn(value);
    }

    public static void givenJobExecutionContext(
        final ChunkContext chunkContext,
        final ExecutionContext executionContext) {
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext())
            .willReturn(executionContext);
    }

    public static void givenStepExecution(final ChunkContext chunkContext, final StepExecution stepExecution) {
        given(chunkContext.getStepContext().getStepExecution()).willReturn(stepExecution);
    }

    public static void givenStepName(final ChunkContext chunkContext, final String value) {
        given(chunkContext.getStepContext().getStepName()).willReturn(value);
    }

    public static void givenBook(final ChunkContext chunkContext, final BookDefinition book) {
        givenExecutionContextParameter(chunkContext, "bookDefn", book);
    }

    public static void givenCombinedBook(final ChunkContext chunkContext, final CombinedBookDefinition combinedBook) {
        givenExecutionContextParameter(chunkContext, JobExecutionKey.COMBINED_BOOK_DEFINITION, combinedBook);
    }

    public static void givenBookDefinitionId(final ChunkContext chunkContext, final Long value) {
        givenJobParameter(chunkContext, JobParameterKey.BOOK_DEFINITION_ID, value);
    }

    public static void givenUserName(final ChunkContext chunkContext, final String value) {
        givenJobParameter(chunkContext, JobParameterKey.USER_NAME, value);
    }

    public static void givenEnvironment(final ChunkContext chunkContext, final String value) {
        givenJobParameter(chunkContext, JobParameterKey.ENVIRONMENT_NAME, value);
    }

    public static void givenBookVersion(final ChunkContext chunkContext, final String value) {
        givenJobParameter(chunkContext, JobParameterKey.BOOK_VERSION_SUBMITTED, value);
    }

    public static void givenHostName(final ChunkContext chunkContext, final String value) {
        givenJobParameter(chunkContext, JobParameterKey.HOST_NAME, value);
    }

    public static void givenBookBundles(final ChunkContext chunkContext, final List<XppBundle> bundles) {
        givenExecutionContextParameter(chunkContext, JobParameterKey.XPP_BUNDLES, bundles);
    }

    public static void givenExecutionContextParameter(final ChunkContext chunkContext, final String parameterName, final Object value) {
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(parameterName)).willReturn(value);
    }

    public static void whenJobExecutionPropertyString(final ExecutionContext jobExecutionContext, final String name, final String value) {
        when(jobExecutionContext.getString(name)).thenReturn(value);
        whenContainsProperty(jobExecutionContext, name);
    }

    public static void whenJobExecutionPropertyInt(final ExecutionContext jobExecutionContext, final String name, final int value) {
        when(jobExecutionContext.getInt(name)).thenReturn(value);
        whenContainsProperty(jobExecutionContext, name);
    }

    public static void whenJobExecutionPropertyBoolean(final ExecutionContext jobExecutionContext, final String name, final Boolean value) {
        when(jobExecutionContext.get(name)).thenReturn(value);
        whenContainsProperty(jobExecutionContext, name);
    }

    private static void whenContainsProperty(final ExecutionContext jobExecutionContext, final String name) {
        when(jobExecutionContext.containsKey(name)).thenReturn(Boolean.TRUE);
    }

    /**
     * If files
     *     expectDirName/dirName/file.xml and
     *     actualDirName/dirName/file.xml
     * are equal
     * then the method return true
     *
     * expectDirName/
     *              dirName/
     *                      file.xml
     * actualDirName/
     *              dirName/
     *                      file.xml
     *                      file12.xml
     *              dirName2/
     *                      file22.xml
     *
     */
    public static void validateDirsOnExpected(final File expectedDir, final File actualDir) {
        Stream.of(Objects.requireNonNull(expectedDir.listFiles()))
        .forEach(expected -> {
            if (expected.isDirectory()) {
                validateDirsOnExpected(expected, new File(actualDir, expected.getName()));
            } else {
                assertThat(expected.getName(), new File(actualDir, expected.getName()), hasSameContentAs(expected));
            }
        });
    }
}
