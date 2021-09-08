package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ANCHOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HREF;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.INNER;

public class InlineIndexInternalLinks {
    public static final String THIS_INDEX = "this index";
    private static final String HEADER_NAME_GROUP = "headerName";
    private static final String HASH = "#";
    private static final String UTF8 = "UTF-8";
    private static final String WHITESPACE_REPLACEMENT = "_";
    private static final String PERCENT_SIGN = "%";
    private static final Pattern INTERNAL_REFERENCE_PATTERN = Pattern.compile(String.format("(.+\\.)?\\s*" +
            "(generally, see |see also |for detailed treatment see |see |)(?<%s>.+)(,\\s*%2$s|\\s\\(%2$s\\))", HEADER_NAME_GROUP, THIS_INDEX));
    
    private final Set<String> headerIds = new HashSet<>();
    private final Set<Element> thisIndexReferences = new HashSet<>();

    public void addThisIndexInternalLinks() {
        for (Element thisIndexReference : thisIndexReferences) {
            String text = thisIndexReference.text();
            String headerId = buildHeaderId(getHeaderName(text));
            if (headerIds.contains(headerId)) {

                Element link = buildThisIndexLink(headerId);
                thisIndexReference.html(text.replaceAll(THIS_INDEX, link.outerHtml()));
            }
        }
    }

    public String buildHeaderId(final String text) {
        return encode(text.toLowerCase()
                .replaceAll("\\s+", WHITESPACE_REPLACEMENT)
                .replaceAll("'", StringUtils.EMPTY));
    }

    private String encode(final String url) {
        try {
            return URLEncoder.encode(url, UTF8).replaceAll(PERCENT_SIGN, WHITESPACE_REPLACEMENT);
        } catch (final UnsupportedEncodingException e) {
            throw new EBookException("Exception on attempt to encode string: " + url, e);
        }
    }

    private String getHeaderName(final String text) {
        Matcher matcher = INTERNAL_REFERENCE_PATTERN.matcher(text.toLowerCase());
        if (matcher.matches()) {
            return matcher.group(HEADER_NAME_GROUP);
        }
        return StringUtils.EMPTY;
    }

    private Element buildThisIndexLink(final String id) {
        Element link = new Element(ANCHOR);
        link.attr(HREF, HASH + id);
        link.attr(INNER, Boolean.TRUE.toString());
        link.text(THIS_INDEX);
        return link;
    }

    public void addHeaderId(final String headerId) {
        headerIds.add(headerId);
    }

    public void addThisIndex(final Element thisIndexElement) {
        thisIndexReferences.add(thisIndexElement);
    }
}
