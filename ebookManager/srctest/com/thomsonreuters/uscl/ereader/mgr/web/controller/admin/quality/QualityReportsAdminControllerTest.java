package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.quality;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportParams;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

@RunWith(MockitoJUnitRunner.class)
public final class QualityReportsAdminControllerTest {
    private static final String GOOD_EMAIL = "test-mail2@tr.com";
    private static final String MALFORMED_EMAIL = "malformed";
    private static final QualityReportParams EXPECTED_PARAMS = new QualityReportParams(true, Arrays.asList(GOOD_EMAIL));
    private static final String BINDING_RESULT_KEY = BindingResult.class.getName() + ".recipient";

    @InjectMocks
    private QualityReportsAdminController controller;
    @Mock
    private QualityReportsAdminService qualityReportsAdminService;
    @Mock
    private Model model;
    @Spy
    private QualityReportsAdminValidator validator;
    private HandlerAdapter handlerAdapter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void onTestSetUp() {
        given(qualityReportsAdminService.getParams()).willReturn(EXPECTED_PARAMS);
        handlerAdapter = new AnnotationMethodHandlerAdapter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
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
    public void testAddRecipientSuccess() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_QUALITY_REPORTS);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("email", GOOD_EMAIL);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        assertNotNull(mav);
        checkRedirection(true, mav);

        // Check binding state
        final BindingResult bindingResult = (BindingResult) mav.getModel().get(BINDING_RESULT_KEY);
        assertNotNull(bindingResult);
        Assert.assertFalse(bindingResult.hasErrors());

        then(qualityReportsAdminService).should().save(GOOD_EMAIL);
    }

    @Test
    public void testAddRecipientFail() throws Exception {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_QUALITY_REPORTS);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("email", MALFORMED_EMAIL);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);
        checkRedirection(false, mav);

        // Check binding state
        final BindingResult bindingResult = (BindingResult) mav.getModel().get(BINDING_RESULT_KEY);
        Assert.assertTrue(bindingResult.hasErrors());

        verify(qualityReportsAdminService, never()).save(MALFORMED_EMAIL);
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
        checkRedirection(isRedirectExpected, result);
    }

    private void checkRedirection(final boolean isRedirectExpected, final ModelAndView result) {
        if (isRedirectExpected) {
            assertThat(result.getView().getClass(), equalTo(RedirectView.class));
            assertThat(((RedirectView) result.getView()).getUrl(), equalTo(WebConstants.MVC_ADMIN_QUALITY_REPORTS));
        } else {
            assertThat(result.getViewName(), equalTo(WebConstants.VIEW_ADMIN_QUALITY_REPORTS_RECIPIENTS));
        }
    }
}
