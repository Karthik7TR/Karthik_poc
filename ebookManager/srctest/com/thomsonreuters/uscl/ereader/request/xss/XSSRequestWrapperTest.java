package com.thomsonreuters.uscl.ereader.request.xss;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.junit.Test;
import org.mockito.Mockito;

public final class XSSRequestWrapperTest {
    private static final String HEADER_NAME = "headerName";
    private static final String VALUE_HEADER_WITH_XSS = "?var=<script>alert('Hello World');</script>";
    private static final String VALUE_HEADER_WITHOUT_XSS = "?var=&lt;script&gt;alert('Hello World');&lt;/script&gt;";

    private static final String PARAMETER_NAME_1 = "parameterName1";
    private static final String VALUE_1_WITH_XSS = "<script type=\"text/javascript\">alert('Hello World');</script>";
    private static final String VALUE_1_WITHOUT_XSS = "&lt;script type=\"text/javascript\"&gt;alert('Hello World');&lt;/script&gt;";

    private static final String PARAMETER_NAME_2 = "parameterName2";
    private static final String VALUE_2_WITH_XSS = "image1: <img src=\"http://website.com/logo1.png\"/> "
        + "image2: <img src='http://website.com/logo3.png'/>\n "
        + "eval(some eval) "
        + "expression(some expression) "
        + "<a href=\"javascript:alert('');\"> Show Alert </a>";
    private static final String VALUE_2_WITHOUT_XSS = "image1: &lt;img src=\"http://website.com/logo1.png\"/&gt; "
        + "image2: &lt;img src='http://website.com/logo3.png'/&gt;\n "
        + "eval(some eval) "
        + "expression(some expression) "
        + "&lt;a href=\"javascript:alert('');\"&gt; Show Alert &lt;/a&gt;";

    @Test
    public void shouldRemoveScriptTagsFromRequest() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader(HEADER_NAME)).thenReturn(VALUE_HEADER_WITH_XSS);
        when(request.getParameter(PARAMETER_NAME_1)).thenReturn(VALUE_1_WITH_XSS);
        when(request.getParameter(PARAMETER_NAME_2)).thenReturn(VALUE_2_WITH_XSS);

        when(request.getParameter(WebConstants.PROVIEW_DISPLAY_NAME_FIELD)).thenReturn(VALUE_1_WITH_XSS);

        final XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper(request);

        assertEquals(xssRequestWrapper.getHeader(HEADER_NAME), VALUE_HEADER_WITHOUT_XSS);
        assertEquals(xssRequestWrapper.getParameter(PARAMETER_NAME_1), VALUE_1_WITHOUT_XSS);
        assertEquals(xssRequestWrapper.getParameter(PARAMETER_NAME_2), VALUE_2_WITHOUT_XSS);
        assertEquals(xssRequestWrapper.getParameter(WebConstants.PROVIEW_DISPLAY_NAME_FIELD), VALUE_1_WITH_XSS);
    }
}
