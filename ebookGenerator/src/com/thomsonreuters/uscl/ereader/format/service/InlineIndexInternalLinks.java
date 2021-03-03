package com.thomsonreuters.uscl.ereader.format.service;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InlineIndexInternalLinks {
    public static final String THIS_INDEX = "this index";
    private static final String HEADER_NAME_GROUP = "headerName";
    private static final String HREF = "href";
    private static final String HASH = "#";
    private static final String ANCHOR = "a";
    private static final Pattern INTERNAL_REFERENCE_PATTERN = Pattern.compile(String.format("(.+\\.)?\\s*" +
            "(see also |for detailed treatment see |see |)(?<%s>.+),\\s*%s", HEADER_NAME_GROUP, THIS_INDEX));

    private final Set<String> headerIds = new HashSet<>();
    private final Set<Element> thisIndexReferences = new HashSet<>();

    public void addThisIndexInternalLinks() {
        for (Element thisIndexReference : thisIndexReferences) {
            String text = thisIndexReference.text();
            String headerId = buildHeaderId(getHeaderName(text));
            if (headerIds.contains(headerId)) {
                thisIndexReference.text(text.replaceAll(THIS_INDEX, StringUtils.EMPTY));
                thisIndexReference.appendChild(buildThisIndexLink(headerId));
            }
        }
    }

    public String buildHeaderId(final String text) {
        return text.toLowerCase().replaceAll("\\s+", "_");
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
