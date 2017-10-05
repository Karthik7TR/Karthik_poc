package com.thomsonreuters.uscl.ereader.notification.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public final class SendNotificationEmailBuilderFactoryTest {
    @InjectMocks
    private SendNotificationEmailBuilderFactory factory;
    @Mock
    private ApplicationContext applicationContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SendEmailNotificationStep step;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private EmailBuilder defaultBuilder;
    @Mock
    private EmailBuilder splitBookBuilder;
    @Mock
    private EmailBuilder bigTocBuilder;

    @Before
    public void setUp() {
        given(applicationContext.getBean("defaultGeneratorNotificationEmailBuilder")).willReturn(defaultBuilder);
        given(applicationContext.getBean("splitBookGeneratorNotificationEmailBuilder")).willReturn(splitBookBuilder);
        given(applicationContext.getBean("bigTocGeneratorNotificationEmailBuilder")).willReturn(bigTocBuilder);
    }

    @Test
    public void shouldCreateDefaultBuilderByDefault() {
        //given
        given(step.getTocNodeCount()).willReturn(5);
        given(step.getThresholdValue()).willReturn(10);
        //when
        final EmailBuilder builder = factory.create();
        //then
        assertThat(builder, is(defaultBuilder));
    }

    @Test
    public void shouldCreateSplitBookBuilderByDefault() {
        //given
        given(step.getTocNodeCount()).willReturn(5);
        given(step.getThresholdValue()).willReturn(10);
        given(step.getBookDefinition().isSplitBook()).willReturn(true);
        //when
        final EmailBuilder builder = factory.create();
        //then
        assertThat(builder, is(splitBookBuilder));
    }

    @Test
    public void shouldCreateBigTocBuilderByDefault() {
        //given
        given(step.getTocNodeCount()).willReturn(15);
        given(step.getThresholdValue()).willReturn(10);
        //when
        final EmailBuilder builder = factory.create();
        //then
        assertThat(builder, is(bigTocBuilder));
    }

    @Test
    public void shouldThrowExceptionIfTocDataIsNotSet() {
        //given
        thrown.expect(RuntimeException.class);
        given(step.getTocNodeCount()).willReturn(null);
        //when
        factory.create();
        //then
    }
}
