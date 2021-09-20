package com.thomsonreuters.uscl.ereader.format.service;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CITE_QUERY;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.COBALT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CODES_SECTION;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.EBOOK_URL_BUILDER_CONTAINER_ATTR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FR_REF_TYPE_ATTR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HEAD;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.LABEL_DESIGNATOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION_BODY;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.W_REF_TYPE_ATTR;
import static java.util.Optional.ofNullable;

@Service
public class InnerDocumentAnchorsMarkerImpl implements InnerDocumentAnchorsMarker {
    private static final String INNER_ANCHOR_ID_TEMPLATE = "codessectionanchor_";
    private static final String INNER_LINK_ID_TEMPLATE = "codessectioncontents_%s_id_%s";
    private static final String TBL_HEAD_HEADTEXT = "tbl>head>headtext";
    private static final String SECTION_CONTENTS = "Section Contents";
    private static final String ID_UPPER_CASE = "ID";
    private static final String HEAD_HEADTEXT = "head>headtext";
    private static final String DOT = ".";

    @Override
    public void markInnerDocumentAnchors(final Document document) {
        if (hasSectionContentsBlock(document)) {
            Map<String, String> anchorNameToAnchorId = new HashMap<>();
            markCiteQueryAnchors(document, anchorNameToAnchorId);
            markCiteQueryLinks(document, anchorNameToAnchorId);
        }
    }

    private boolean hasSectionContentsBlock(final Document document) {
        return SECTION_CONTENTS.equals(ofNullable(document.getElementsByTag(SECTION_BODY).first())
                .map(section -> section.selectFirst(TBL_HEAD_HEADTEXT))
                .map(Element::text)
                .orElse(null));
    }

    private void markCiteQueryLinks(final Document document, final Map<String, String> anchorNameToAnchorId) {
        AtomicInteger idUniquenessCounter = new AtomicInteger(0);
        document.getElementsByTag(SECTION_BODY).stream()
                .flatMap(codesSection -> codesSection.getElementsByTag(CITE_QUERY).stream())
                .forEach(citeQuery -> markCiteQueryLink(citeQuery, idUniquenessCounter, anchorNameToAnchorId));
    }

    private void markCiteQueryLink(final Element citeQuery, final AtomicInteger idUniquenessCounter, final Map<String, String> anchorNameToAnchorId) {
        String anchorName = extractAnchorNameFromLinkText(citeQuery.text());
        if (anchorNameToAnchorId.containsKey(anchorName)) {
            Element anchor = new Element(CITE_QUERY);
            citeQuery.before(anchor);
            setAnchorAttributes(generateAnchorId(idUniquenessCounter, anchorNameToAnchorId.get(anchorName)), anchor);
        }
    }

    private String generateAnchorId(final AtomicInteger idUniquenessCounter, final String codesSectionId) {
        return String.format(INNER_LINK_ID_TEMPLATE, idUniquenessCounter.getAndIncrement(), codesSectionId);
    }

    private void markCiteQueryAnchors(final Document document, final Map<String, String> designatorToCodesSectionId) {
        document.getElementsByTag(CODES_SECTION).forEach(codesSection -> {
            String anchorName = extractAnchorNameFromHeader(codesSection);

            if (StringUtils.isNotEmpty(anchorName)) {
                String codesSectionId = codesSection.attr(ID_UPPER_CASE);
                Element anchor = document.createElement(CITE_QUERY);
                ofNullable(codesSection.selectFirst(HEAD_HEADTEXT))
                        .orElseGet(() -> codesSection.getElementsByTag(LABEL_DESIGNATOR).first())
                        .prependChild(anchor);
                setAnchorAttributes(INNER_ANCHOR_ID_TEMPLATE + codesSectionId, anchor);
                designatorToCodesSectionId.put(anchorName, codesSectionId);
            }
        });
    }

    private void setAnchorAttributes(final String anchorId, final Element anchor) {
        anchor.attr(W_REF_TYPE_ATTR, FR_REF_TYPE_ATTR);
        anchor.attr(EBOOK_URL_BUILDER_CONTAINER_ATTR, COBALT);
        anchor.attr(ID_UPPER_CASE, anchorId);
        anchor.text(DOT);
    }

    private String extractAnchorNameFromHeader(final Element section) {
        return ofNullable(section.selectFirst(HEAD))
                        .map(head -> head.getElementsByTag(LABEL_DESIGNATOR))
                        .map(Elements::first)
                        .map(Element::text)
                        .map(String::trim)
                        .map(name -> StringUtils.removeEnd(name, DOT))
                        .orElse(null);
    }

    private String extractAnchorNameFromLinkText(String citeQueryText) {
        return citeQueryText
                .replaceAll("\\(.*\\)", StringUtils.EMPTY)
                .replaceAll("[^0-9a-zA-Z.]", StringUtils.EMPTY).trim();
    }
}
