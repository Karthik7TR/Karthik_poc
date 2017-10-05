package com.thomsonreuters.uscl.ereader.core.job.service;

import static com.thomsonreuters.uscl.ereader.core.job.service.JobRequestMatchers.book;
import static com.thomsonreuters.uscl.ereader.core.job.service.JobRequestMatchers.jobRequest;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class JobNameProviderImplTest {
    @InjectMocks
    private JobNameProviderImpl provider;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnCorrentJobNameForNortSourceType() {
        //given
        final BookDefinition book = book(SourceType.NORT);
        //when
        final String jobName = provider.getJobName(book);
        //then
        assertThat(jobName, is("ebookGeneratorJob"));
    }

    @Test
    public void shouldReturnCorrentJobNameForXppSourceType() {
        //given
        final BookDefinition book = book(SourceType.XPP);
        //when
        final String jobName = provider.getJobName(book);
        //then
        assertThat(jobName, is("ebookGeneratorXppJob"));
    }

    @Test
    public void shouldReturnCorrentJobNameForXppJobRequest() {
        //given
        final JobRequest jobRequest = jobRequest(SourceType.XPP);
        //when
        final String jobName = provider.getJobName(jobRequest);
        //then
        assertThat(jobName, is("ebookGeneratorXppJob"));
    }

    @Test
    public void shouldThrowExceptionIfIncorrectJobRequest() {
        //given
        thrown.expect(IllegalStateException.class);
        final JobRequest jobRequest = new JobRequest();
        //when //then
        provider.getJobName(jobRequest);
    }
}
