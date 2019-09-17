package com.thomsonreuters.uscl.ereader.notification.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class DefaultEmailBuilderTest {
    private static final String PRINT_PAGE_NUMBERS_WARNING = "WARNING: printPageNumbers";
    private static final String INLINE_TOC_WARNING = "WARNING: inlineToc";
    @InjectMocks
    private DefaultEmailBuilder defaultEmailBuilder;
    @Mock
    private SendEmailNotificationStep step;
    @Mock
    private BookDefinition book;

    @Test
    public void subjectShouldBeCorrect() {
        //given
        givenAll();
        //when
        final String subject = defaultEmailBuilder.getSubject();
        //then
        assertThat(subject, is("eBook Publishing Successful - id"));
    }

    @Test
    public void bodyShouldBeCorrect() {
        //given
        givenAll();
        givenPages(false, false);
        givenInlineToc(false, false);
        //when
        final String body = defaultEmailBuilder.getBody();
        //then
        assertThat(body, containsString("eBook Publishing Successful - id"));
        assertThat(body, containsString("Proview Display Name: name"));
        assertThat(body, containsString("Title ID: id"));
        assertThat(body, containsString("Job Instance ID: 1"));
        assertThat(body, containsString("Job Execution ID: 2"));
        assertThat(body, containsString("Environment: env"));
        assertThat(body, containsString("No Previous Version."));
        assertThat(body, not(containsString(PRINT_PAGE_NUMBERS_WARNING)));
        assertThat(body, not(containsString(INLINE_TOC_WARNING)));
    }

    @Test
    public void testPagebreaksNotWantedButFound() {
        //given
        givenAll();
        givenPages(false, true);
        givenInlineToc(false, true);
        //when
        final String body = defaultEmailBuilder.getBody();
        //then
        assertThat(body, not(containsString(PRINT_PAGE_NUMBERS_WARNING)));
        assertThat(body, not(containsString(INLINE_TOC_WARNING)));
    }

    @Test
    public void testPagebreaksWantedButNotFound() {
        //given
        givenAll();
        givenPages(true, false);
        givenInlineToc(true, false);
        //when
        final String body = defaultEmailBuilder.getBody();
        //then
        assertThat(body, containsString(PRINT_PAGE_NUMBERS_WARNING));
        assertThat(body, containsString(INLINE_TOC_WARNING));
    }

    @Test
    public void testPagebreaksWantedAndFound() {
        //given
        givenAll();
        givenPages(true, true);
        givenInlineToc(true, true);
        //when
        final String body = defaultEmailBuilder.getBody();
        //then
        assertThat(body, not(containsString(PRINT_PAGE_NUMBERS_WARNING)));
        assertThat(body, not(containsString(INLINE_TOC_WARNING)));
    }

    @Test
    public void currentVersionInfoIsCorrect() {
        // given
        givenAll();
        final PublishingStats stats = mock(PublishingStats.class);
        given(step.getCurrentsStats()).willReturn(stats);
        given(stats.getGatherDocRetrievedCount()).willReturn(10);
        given(stats.getBookSize()).willReturn(null);
        given(stats.getJobInstanceId()).willReturn(1L);
        // when
        final String body = defaultEmailBuilder.getBody();
        // then
        assertThat(body, containsString("Current Version:"));
        assertThat(body, containsString("Job Instance ID: 1"));
        assertThat(body, containsString("Gather Doc Retrieved Count: 10"));
        assertThat(body, containsString("Book Size: -"));
    }

    @Test
    public void previousVersionInfoIsCorrect() {
        // given
        givenAll();
        final PublishingStats stats = mock(PublishingStats.class);
        given(step.getPreviousStats()).willReturn(stats);
        given(stats.getGatherDocRetrievedCount()).willReturn(null);
        given(stats.getBookSize()).willReturn(10L);
        // when
        final String body = defaultEmailBuilder.getBody();
        // then
        assertThat(body, containsString("Previous Version:"));
        assertThat(body, containsString("Gather Doc Retrieved Count: -"));
        assertThat(body, containsString("Book Size: 10"));
    }

    private void givenAll() {
        given(step.getBookDefinition()).willReturn(book);
        given(book.getFullyQualifiedTitleId()).willReturn("id");
        given(book.getProviewDisplayName()).willReturn("name");
        given(step.getEnvironment()).willReturn("env");
        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getJobExecutionId()).willReturn(2L);
    }

    private void givenPages(final boolean printPageNumbersInUI, final boolean pagebreaksActuallyAdded) {
        given(book.isPrintPageNumbers()).willReturn(printPageNumbersInUI);
        given(step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS)).willReturn(pagebreaksActuallyAdded);
    }

    private void givenInlineToc(final boolean inlineTocInUI, final boolean inlineTocActuallyAdded) {
        given(book.isInlineTocIncluded()).willReturn(inlineTocInUI);
        given(step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_INLINE_TOC)).willReturn(inlineTocActuallyAdded);
    }
}
