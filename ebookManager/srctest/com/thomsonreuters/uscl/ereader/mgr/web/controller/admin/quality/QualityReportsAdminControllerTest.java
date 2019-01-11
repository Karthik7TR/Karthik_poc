package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.quality;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Arrays;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportParams;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RunWith(MockitoJUnitRunner.class)
public final class QualityReportsAdminControllerTest {
    private static final QualityReportParams EXPECTED_PARAMS = new QualityReportParams(true, Arrays.asList("test-mail@tr.com"));

    private QualityReportsAdminController controller;
    @Mock
    private QualityReportsAdminService qualityReportsAdminService;
    @Mock
    private Model model;

    @Before
    public void onTestSetUp() {
        controller = new QualityReportsAdminController(qualityReportsAdminService);
        given(qualityReportsAdminService.getParams()).willReturn(EXPECTED_PARAMS);
    }

    @Test
    public void shouldGetDataAndReturnView() {
        //given
        //when
        final ModelAndView result = controller.qualityRecipients(model, new QualityReportRecipient());

        //then
        performCommonModelCheck(false, result);
    }

    @Test
    public void shouldAddRecipientAndRedirect() {
        //given
        //when
        final ModelAndView result = controller.qualityRecipientsAdd(model, new QualityReportRecipient("test-mail2@tr.com"));

        //then
        performCommonModelCheck(true, result);
        then(qualityReportsAdminService).should().save("test-mail2@tr.com");
    }

    @Test
    public void shouldDeleteRecipientAndRedirect() {
        //given
        //when
        final ModelAndView result = controller.qualityRecipientsDelete(new QualityReportRecipient("test-mail2@tr.com"), model);

        //then
        performCommonModelCheck(true, result);
        then(qualityReportsAdminService).should().delete("test-mail2@tr.com");
    }

    @Test
    public void shouldSwitchEnabledParameterAndRedirect() {
        //given
        //when
        final ModelAndView result = controller.qualityStepSwitchApply(model, new QualityReportParams(false, null));

        //then
        performCommonModelCheck(true, result);
        then(qualityReportsAdminService).should().changeQualityStepEnableParameter(false);
    }

    private void performCommonModelCheck(final boolean isRedirectExpected, final ModelAndView result) {
        then(model).should().addAttribute(WebConstants.KEY_QUALITY_REPORTS_FORM, EXPECTED_PARAMS);
        then(model).should().addAttribute("deletePath", WebConstants.MVC_ADMIN_QUALITY_RECIPIENTS_DELETE);
        if (isRedirectExpected) {
            assertThat(result.getView().getClass(), equalTo(RedirectView.class));
            assertThat(((RedirectView) result.getView()).getUrl(), equalTo(WebConstants.MVC_ADMIN_QUALITY_REPORTS));
        } else {
            assertThat(result.getViewName(), equalTo(WebConstants.VIEW_ADMIN_QUALITY_REPORTS_RECIPIENTS));
        }
    }
}
