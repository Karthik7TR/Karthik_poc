package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.protectPagebreak;

@Component
public class InlineIndexService {
    private static final String DIV = "div";
    private static final String SECTION = "section";
    private static final String DOCUMENT = "n-document";
    private static final String MD_UUID = "md.uuid";
    private static final String STYLE = "style";
    private static final String CO_INDEX = "co_index";
    private static final String INLINE_INDEX_FILE_NAME = "inlineIndex.transformed";
    private static final String INDEX = "index";
    private static final String DOC_TITLE = "doc.title";
    private static final String CO_TITLE = "co_title";
    private static final String HEADTEXT = "headtext";
    private static final String CO_HEADTEXT = "co_headtext";
    private static final String OL = "ol";
    private static final String CO_TOC = "co_toc";
    private static final String INDEX_ENTRY = "index.entry";
    private static final String SUBJECT = ">subject";
    private static final String SPAN = "span";
    private static final String LI = "li";
    private static final String CO_TOC_HEADING = "co_tocHeading";
    private static final String UNUSED_TAGS_REGEX = "bop|bos|eos|eop";
    private static final String CITE_QUERY = "cite.query";
    private static final String CITE_QUERY_SOURCE_CITE = "ebook";
    private static final String ANCHOR = "a";
    private static final String ID = "id";
    private static final String CO_LINK = "co_link_";
    private static final String CO_LINK2 = "co_link";
    private static final String CO_DRAG = "co_drag";
    private static final String UI_DRAGGABLE = "ui-draggable";
    private static final String HREF = "href";

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private CssStylingService stylingService;

    @Autowired
    private CiteQueryAdapter citeQueryAdapter;

    public boolean generateInlineIndex(final File source, final File destDir, final boolean pages) {
        final Document gatheredIndexXml = jsoup.loadDocument(source);

        final Element indexSection = new Element(DIV).addClass(SECTION);

        Elements documents = gatheredIndexXml.select(DOCUMENT);
        if (!documents.isEmpty()) {
            documents.forEach(indexDoc -> {
                final String originatingDoc = indexDoc.getElementsByTag(MD_UUID).text();
                final Element index = indexDoc.selectFirst(INDEX);

                moveFirstPagebreakBeforeIndexItem(index, indexSection, pages);
                index.attr(STYLE, getStyle(index, true));
                index.tagName(DIV).addClass(CO_INDEX);
                processTitle(index);
                processIndexEntries(index);
                transformCiteQueries(index, originatingDoc);

                indexSection.appendChild(index);
            });

            jsoup.saveDocument(destDir, INLINE_INDEX_FILE_NAME, indexSection);
            return true;
        }
        return false;
    }

    private void processTitle(final Element indexXml) {
        indexXml.getElementsByTag(DOC_TITLE).forEach(title -> {
            title.tagName(DIV).addClass(CO_TITLE).attr(STYLE, stylingService.fontWeightBold());
            final Element headtext = indexXml.getElementsByTag(HEADTEXT).first().tagName(DIV).addClass(CO_HEADTEXT);
            title.empty();
            title.appendChild(headtext);
        });
    }

    private void processIndexEntries(final Element indexXml) {
        final Element list = new Element(OL).addClass(CO_TOC);

        indexXml.childNodes().stream()
            .filter(Objects::nonNull)
            .filter(node -> INDEX_ENTRY.equals(node.nodeName()))
            .collect(Collectors.toList())
            .forEach(entry -> processIndexEntry(list, (Element) entry));

        if (list.childNodeSize() > 0) {
            indexXml.appendChild(list);
        }
    }

    private void processIndexEntry(final Element list, final Element indexEntry) {
        removeTags(indexEntry, UNUSED_TAGS_REGEX);
        indexEntry.tagName(DIV).attr(STYLE, getStyle(indexEntry, false));
        indexEntry.select(SUBJECT).tagName(SPAN);

        processIndexEntries(indexEntry);

        indexEntry.remove();

        final Element item = new Element(LI).addClass(CO_TOC_HEADING).attr(STYLE, stylingService.listTypeNone());
        item.appendChild(indexEntry);
        list.appendChild(item);
    }

    private void removeTags(final Element indexEntry, final String regex) {
        indexEntry.childNodes().stream()
            .filter(Objects::nonNull)
            .filter(child -> child.nodeName().matches(regex))
            .collect(Collectors.toList())
            .forEach(Node::remove);
    }

    private String getStyle(final Element indexXml, final boolean isHeader) {
        String style;
        if (stylingService.hasStylingAttribute(indexXml)) {
            style = stylingService.getStyleByElement(indexXml);
            indexXml.clearAttributes();
        } else {
            style = stylingService.getDefaultIndexStyle(isHeader);
        }
        return style;
    }

    private void moveFirstPagebreakBeforeIndexItem(final Element indexXml, final Element indexHtml, final boolean pages) {
        jsoup.firstChild(indexXml)
            .filter(PageNumberUtil::isPagebreak)
            .ifPresent(node -> {
                node.remove();
                if (pages) {
                    indexHtml.append(protectPagebreak(node));
                }
            });
    }

    private void transformCiteQueries(final Element indexSection, final String originatingDoc) {
        indexSection.getElementsByTag(CITE_QUERY).forEach(citeQuery -> transformCiteQuery(citeQuery, originatingDoc));
    }

    private void transformCiteQuery(final Element citeQuery, final String originatingDoc) {
        final String urlString = citeQueryAdapter.GetCiteQueryLink(citeQuery.outerHtml(), originatingDoc, StringUtils.EMPTY, CITE_QUERY_SOURCE_CITE);
        citeQuery.replaceWith(createCiteQueryAnchor(citeQuery, urlString));
    }

    private Element createCiteQueryAnchor(final Element citeQuery, final String urlString) {
        return new Element(ANCHOR)
            .attr(ID, CO_LINK + citeQuery.attr(ID))
            .addClass(CO_LINK2)
            .addClass(CO_DRAG)
            .addClass(UI_DRAGGABLE)
            .attr(HREF, urlString)
            .append(citeQuery.text());
    }
}
