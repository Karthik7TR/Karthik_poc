package com.thomsonreuters.uscl.ereader.notification.service;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SplitBookEmailBuilderTest {
    @InjectMocks
    private SplitBookEmailBuilder splitBookEmailBuilder;
    @Mock
    private SendEmailNotificationStep step;
    @Mock
    private DocMetadataService docMetadataService;
    @Mock
    private BookDefinition book;

    @Test
    public void additionalSubjectPartIsCorrect() {
        //given
        //when
        final String subjectPart = splitBookEmailBuilder.getAdditionalSubjectPart();
        //then
        assertThat(subjectPart, is(" (Split Book)"));
    }

    @Test
    public void additionalBodyPartIsCorrect() {
        //given
        given(step.getBookDefinition()).willReturn(book);
        given(book.getFullyQualifiedTitleId()).willReturn("id");
        given(book.getProviewDisplayName()).willReturn("name");
        given(step.getJobInstanceId()).willReturn(1L);
        given(docMetadataService.findDistinctSplitTitlesByJobId(1L)).willReturn(asList("title1", "title2"));
        //when
        final String bodyPart = splitBookEmailBuilder.getAdditionalBodyPart();
        //then
        assertThat(bodyPart, containsString("Proview display name : name"));
        assertThat(bodyPart, containsString("Fully Qualified Title : id"));
        assertThat(bodyPart, containsString("Total parts : 2"));
        assertThat(bodyPart, containsString("title1"));
        assertThat(bodyPart, containsString("title2"));
    }
}
