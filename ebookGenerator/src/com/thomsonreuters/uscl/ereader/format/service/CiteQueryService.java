package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CiteQueryService {
    private static final String ID = "id";
    private static final String CITE_QUERY = "cite.query";
    private static final String CITE_QUERY_SOURCE_CITE = "ebook";
    private static final String A_TAG = "a";
    private static final String HREF = "href";
    private static final String CO_LINK = "co_link_";
    private static final String CO_LINK2 = "co_link";
    private static final String CO_DRAG = "co_drag";
    private static final String UI_DRAGGABLE = "ui-draggable";

    @Autowired
    private CiteQueryAdapter citeQueryAdapter;

    public Collection<Element> transformCiteQueries(final Element section, final String originatingDoc) {
        return section.getElementsByTag(CITE_QUERY).stream()
                .map(citeQuery -> transformCiteQuery(citeQuery, originatingDoc))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Element transformCiteQuery(final Element citeQuery, final String originatingDoc) {
        final String urlString = citeQueryAdapter.GetCiteQueryLink(citeQuery.outerHtml(), originatingDoc, StringUtils.EMPTY, CITE_QUERY_SOURCE_CITE);
        if (StringUtils.isNotEmpty(urlString)) {
            Element anchor = createCiteQueryAnchor(citeQuery, urlString);
            citeQuery.replaceWith(anchor);
            anchor.append(citeQuery.html());
            return anchor;
        } else {
            citeQuery.after(citeQuery.html());
            citeQuery.remove();
            return null;
        }
    }

    private Element createCiteQueryAnchor(final Element citeQuery, final String urlString) {
        return new Element(A_TAG)
                .attr(ID, CO_LINK + citeQuery.attr(ID))
                .addClass(CO_LINK2)
                .addClass(CO_DRAG)
                .addClass(UI_DRAGGABLE)
                .attr(HREF, urlString);
    }
}
