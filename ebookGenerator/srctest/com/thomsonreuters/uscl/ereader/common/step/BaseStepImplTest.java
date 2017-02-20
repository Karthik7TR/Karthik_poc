package com.thomsonreuters.uscl.ereader.common.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenStepExecution;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

@RunWith(MockitoJUnitRunner.class)
public final class BaseStepImplTest
{
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BaseStepImpl step;
    @Mock
    private StepContribution stepContribution;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private StepExecution stepExecution;

    @Before
    public void setUp()
    {
        //given
        givenStepExecution(chunkContext, stepExecution);
    }

    @Test
    public void shouldAlwaysReturnFinished() throws Exception
    {
        //when
        final RepeatStatus status = step.execute(stepContribution, chunkContext);
        //then
        assertThat(status, is(RepeatStatus.FINISHED));
    }

    @Test
    public void shouldCallExecuteStep() throws Exception
    {
        //given
        given(step.executeStep()).willReturn(ExitStatus.COMPLETED);
        //when
        step.execute(stepContribution, chunkContext);
        //then
        then(stepExecution).should().setExitStatus(ExitStatus.COMPLETED);
    }
}
