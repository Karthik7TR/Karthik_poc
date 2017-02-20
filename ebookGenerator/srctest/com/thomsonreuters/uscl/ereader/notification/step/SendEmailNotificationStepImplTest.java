package com.thomsonreuters.uscl.ereader.notification.step;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenUserName;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class SendEmailNotificationStepImplTest
{
    @InjectMocks
    private SendEmailNotificationStepImpl step;
    @Mock
    private EmailService emailService;
    @Mock
    private CoreService coreService;
    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private EmailBuilderFactory emailBuilderFactory;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Mock
    private PublishingStats stats;
    @Mock
    private EmailBuilder emailBuilder;

    @Test
    public void recipientsShouldBeCorrect() throws Exception
    {
        //given
        givenAll();
        final InternetAddress[] expectedRecipients = new InternetAddress[] {new InternetAddress("address")};
        given(coreService.getEmailRecipientsByUsername("user")).willReturn(asList(expectedRecipients));
        //when
        step.executeStep();
        //then
        then(emailService).should().send(asList(expectedRecipients), null, null);
    }

    @Test
    public void bodyAndSubjectShouldBeCorrect() throws Exception
    {
        //given
        givenAll();
        given(emailBuilder.getSubject()).willReturn("subject");
        given(emailBuilder.getBody()).willReturn("body");
        //when
        step.executeStep();
        //then
        then(emailService).should().send(any(Collection.class), eq("subject"), eq("body"));
    }

    private void givenAll()
    {
        givenUserName(chunkContext, "user");
        givenJobInstanceId(chunkContext, 1L);
        given(publishingStatsService.findPublishingStatsByJobId(1L)).willReturn(stats);
        given(publishingStatsService.getPreviousPublishingStatsForSameBook(1L)).willReturn(stats);
        given(emailBuilderFactory.create()).willReturn(emailBuilder);
    }
}
