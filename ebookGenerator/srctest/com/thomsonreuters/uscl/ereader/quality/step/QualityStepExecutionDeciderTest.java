package com.thomsonreuters.uscl.ereader.quality.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;

@RunWith(MockitoJUnitRunner.class)
public final class QualityStepExecutionDeciderTest {
    @InjectMocks
    private QualityStepExecutionDecider sut;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QualityReportsAdminService qualityReportsAdminService;

    @Test
    public void shouldReturnEnabledStatus() {
        //given
        given(qualityReportsAdminService.getParams().isQualityStepEnabled()).willReturn(true);

        //when
        final FlowExecutionStatus status = sut.decide(null, null);

        //then
        assertThat(status.getName(), equalTo("ENABLED"));
    }

    @Test
    public void shouldReturnDisabledStatus() {
        //given
        given(qualityReportsAdminService.getParams().isQualityStepEnabled()).willReturn(false);

        //when
        final FlowExecutionStatus status = sut.decide(null, null);

        //then
        assertThat(status.getName(), equalTo("DISABLED"));
    }
}
