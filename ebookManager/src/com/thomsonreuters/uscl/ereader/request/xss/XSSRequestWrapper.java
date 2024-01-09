package com.thomsonreuters.uscl.ereader.request.xss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.UnicodeUnpairedSurrogateRemover;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
    public static final CharSequenceTranslator ESCAPE_LT_GT;
    static {
        final Map<CharSequence, CharSequence> escapeMap = new HashMap<>();
        escapeMap.put("<", "&lt;");
        escapeMap.put(">", "&gt;");
        ESCAPE_LT_GT = new AggregateTranslator(
                new LookupTranslator(Collections.unmodifiableMap(escapeMap)),
                new UnicodeUnpairedSurrogateRemover()
        );
    }

    public XSSRequestWrapper(final HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String[] getParameterValues(final String parameter) {
        final String [] values = super.getParameterValues(parameter);
        if (isExcluded(parameter)) {
            return values;
        }
        return Optional.ofNullable(values)
            .map(Stream::of)
            .orElseGet(Stream::empty)
            .map(this::stripXSS)
            .toArray(String[]::new);
    }

    @Override
    public String getParameter(final String parameter) {
        final String value = super.getParameter(parameter);
        if (isExcluded(parameter)) {
            return value;
        }
        return stripXSS(value);
    }

    @Override
    public String getHeader(final String name) {
        return stripXSS(super.getHeader(name));
    }

    private boolean isExcluded(final String parameter) {
        return WebConstants.PROVIEW_DISPLAY_NAME_FIELD.equals(parameter);
    }

    private String stripXSS(final String value) {
        return ESCAPE_LT_GT.translate(value);
    }
}
