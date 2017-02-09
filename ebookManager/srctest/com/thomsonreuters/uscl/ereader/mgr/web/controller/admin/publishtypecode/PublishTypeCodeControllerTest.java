package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class PublishTypeCodeControllerTest
{
    private static final String BINDING_RESULT_KEY =
        BindingResult.class.getName() + "." + PublishTypeCodeForm.FORM_NAME;
    private static final PubTypeCode PUBLISH_TYPE_CODE = new PubTypeCode();
    private static final Long PUBLISH_TYPE_ID = 1L;
    private PublishTypeCodeController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    private CodeService mockCodeService;
    private PublishTypeCodeFormValidator validator;

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handlerAdapter = new AnnotationMethodHandlerAdapter();

        // Mock up the Code service
        mockCodeService = EasyMock.createMock(CodeService.class);

        // Set up the controller
        controller = new PublishTypeCodeController();
        controller.setCodeService(mockCodeService);

        validator = new PublishTypeCodeFormValidator();
        validator.setCodeService(mockCodeService);
        controller.setValidator(validator);

        PUBLISH_TYPE_CODE.setId(PUBLISH_TYPE_ID);
        PUBLISH_TYPE_CODE.setName("test");
    }

    /**
     * Test the GET to the List page
     */
    @Test
    public void testViewPubTypeCodeList()
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW);
        request.setMethod(HttpMethod.GET.name());

        EasyMock.expect(mockCodeService.getAllPubTypeCodes()).andReturn(new ArrayList<PubTypeCode>());
        EasyMock.replay(mockCodeService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_VIEW, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final List<PubTypeCode> codes = (List<PubTypeCode>) model.get(WebConstants.KEY_PUB_TYPE_CODE);
            assertEquals(0, codes.size());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        EasyMock.verify(mockCodeService);
    }

    /**
     * Test the GET to the Create Page
     */
    @Test
    public void testCreatePubTypeCodeGet()
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE, mav.getViewName());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Create Page Success
     */
    @Test
    public void testCreatePubTypeCodePost()
    {
        final String name = "test";
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("name", name);

        final PubTypeCode code = new PubTypeCode();
        code.setName(name);

        EasyMock.expect(mockCodeService.getPubTypeCodeByName(name)).andReturn(null);
        mockCodeService.savePubTypeCode(code);
        EasyMock.replay(mockCodeService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertFalse(bindingResult.hasErrors());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockCodeService);
    }

    /**
     * Test the POST to the Create Page Fail
     */
    @Test
    public void testCreatePubTypeCodePostFail()
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_CREATE, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertTrue(bindingResult.hasErrors());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the GET to the Edit Page
     */
    @Test
    public void testEditPubTypeCodeGet()
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("id", PUBLISH_TYPE_ID.toString());

        EasyMock.expect(mockCodeService.getPubTypeCodeById(PUBLISH_TYPE_ID)).andReturn(PUBLISH_TYPE_CODE);
        EasyMock.replay(mockCodeService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();
            final PubTypeCode actual = (PubTypeCode) model.get(WebConstants.KEY_PUB_TYPE_CODE);

            Assert.assertEquals(PUBLISH_TYPE_CODE, actual);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test the POST to the Edit Page Success
     */
    @Test
    public void testEditPubTypeCodePost()
    {
        final String name = PUBLISH_TYPE_CODE.getName();
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT);
        request.setMethod(HttpMethod.POST.name());
        request.setParameter("pubTypeId", PUBLISH_TYPE_ID.toString());
        request.setParameter("name", name);

        EasyMock.expect(mockCodeService.getPubTypeCodeByName(name)).andReturn(null);
        mockCodeService.savePubTypeCode(PUBLISH_TYPE_CODE);
        EasyMock.replay(mockCodeService);

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify mav is a RedirectView
            final View view = mav.getView();
            assertEquals(RedirectView.class, view.getClass());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertFalse(bindingResult.hasErrors());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        EasyMock.verify(mockCodeService);
    }

    /**
     * Test the POST to the Edit Page Fail
     */
    @Test
    public void testEditPubTypeCodePostFail()
    {
        request.setRequestURI("/" + WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT);
        request.setMethod(HttpMethod.POST.name());

        final ModelAndView mav;
        try
        {
            mav = handlerAdapter.handle(request, response, controller);

            assertNotNull(mav);
            // Verify the returned view name
            assertEquals(WebConstants.VIEW_ADMIN_PUBLISH_TYPE_CODE_EDIT, mav.getViewName());

            // Check the state of the model
            final Map<String, Object> model = mav.getModel();

            // Check binding state
            final BindingResult bindingResult = (BindingResult) model.get(BINDING_RESULT_KEY);
            assertNotNull(bindingResult);
            Assert.assertTrue(bindingResult.hasErrors());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
