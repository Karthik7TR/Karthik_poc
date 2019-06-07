package com.thomsonreuters.uscl.ereader.core.quality;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.quality.dao.QualityReportRecipientDao;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportParams;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminService;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class QualityReportsAdminServiceImplTest {
    private QualityReportsAdminService sut;
    @Mock
    private AppParameter appParameter;
    @Mock
    private AppParameterDao appParameterDao;
    @Mock
    private QualityReportRecipientDao qualityReportRecipientDao;
    @Captor
    private ArgumentCaptor<AppParameter> appParamCaptor;

    @Before
    public void onTestSetUp() {
        sut = new QualityReportsAdminServiceImpl(appParameterDao, qualityReportRecipientDao);
        given(qualityReportRecipientDao.findAll()).willReturn(Arrays.asList(new QualityReportRecipient("test-mail@tr.com")));
    }

    @Test
    public void shouldReturnEnabledFlagParamIsNull() {
        //given
        //when
        final QualityReportParams params = sut.getParams();

        //then
        assertTrue(params.isQualityStepEnabled());
        performCommonRecipientsCheck(params.getRecipients());
    }

    @Test
    public void shouldReturnEnabledFlagParamIsTrue() {
        //given
        given(appParameter.getValue()).willReturn("true");
        given(appParameterDao.findOne("QualityStepEnabled")).willReturn(appParameter);

        //when
        final QualityReportParams params = sut.getParams();

        //then
        assertTrue(params.isQualityStepEnabled());
        performCommonRecipientsCheck(params.getRecipients());
    }

    @Test
    public void shouldReturnDisabledFlagParamIsFalse() {
        //given
        given(appParameter.getValue()).willReturn("false");
        given(appParameterDao.findOne("QualityStepEnabled")).willReturn(appParameter);

        //when
        final QualityReportParams params = sut.getParams();

        //then
        assertFalse(params.isQualityStepEnabled());
        performCommonRecipientsCheck(params.getRecipients());
    }

    private void performCommonRecipientsCheck(final List<String> recipients) {
        assertThat(recipients, hasSize(1));
        assertThat(recipients, contains("test-mail@tr.com"));
    }

    @Test
    public void shouldSaveEnabledAppParam() {
        //given
        //when
        sut.changeQualityStepEnableParameter(true);

        //then
        performAppParamCheck("true");
    }

    @Test
    public void shouldSaveDisabledAppParam() {
        //given
        //when
        sut.changeQualityStepEnableParameter(false);

        //then
        performAppParamCheck("false");
    }

    private void performAppParamCheck(final String expectedValue) {
        then(appParameterDao).should().save(appParamCaptor.capture());
        assertThat(appParamCaptor.getValue().getKey(), equalTo("QualityStepEnabled"));
        assertThat(appParamCaptor.getValue().getValue(), equalTo(expectedValue));
    }
}
