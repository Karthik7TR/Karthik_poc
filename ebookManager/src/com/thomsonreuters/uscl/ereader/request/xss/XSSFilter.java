package com.thomsonreuters.uscl.ereader.request.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class XSSFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        //use default filterConfig
    }

    @Override
    public void destroy() {
        //no need in destroying
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final String path = ((HttpServletRequest) request).getServletPath();
        if (excludeFromFilter(path)) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
        }
    }

    private boolean excludeFromFilter(final String path) {
        return path.endsWith(WebConstants.MVC_SEC_LOGIN) || path.endsWith(WebConstants.MVC_SEC_CHECK);
    }
}
