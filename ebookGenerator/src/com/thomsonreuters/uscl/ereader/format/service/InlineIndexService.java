package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createProviewPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.protectPagebreak;
import static com.thomsonreuters.uscl.ereader.format.service.InlineIndexInternalLinks.THIS_INDEX;

@Component
public class InlineIndexService {
    private static final String DIV = "div";
    private static final String SECTION = "section";
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
    private static final String XML = ".xml";
    private static final String DOC = "doc";
    private static final String INDEX_PAGE_NAME = "Index";
    private static final String H3 = "h3";
    private static final String ID = "id";

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private CssStylingService stylingService;

    @Autowired
    private CiteQueryService citeQueryService;

    public void generateInlineIndex(final List<String> indexDocGuids, final File sourceDir, final File destDir, final boolean pages) {
        final Element indexSection = new Element(DIV).addClass(SECTION);
        final InlineIndexInternalLinks internalLinks = new InlineIndexInternalLinks();

        addFirstPagebreak(indexSection, pages);
        indexDocGuids.forEach(indexDocGuid -> convertIndexDoc(indexDocGuid, sourceDir, indexSection, internalLinks));
        internalLinks.addThisIndexInternalLinks();

        jsoup.saveDocument(destDir, INLINE_INDEX_FILE_NAME, indexSection);
    }

    private void addFirstPagebreak(final Element indexSection, final boolean pages) {
        if (pages) {
            indexSection.append(protectPagebreak(createProviewPagebreak(INDEX_PAGE_NAME)));
        }
    }

    private void convertIndexDoc(final String indexDocGuid, final File sourceDir, final Element indexSection, final InlineIndexInternalLinks internalLinks) {
        File indexFile = new File(sourceDir, indexDocGuid + XML);

        final Document indexXml = jsoup.loadDocument(indexFile);
        Element indexDoc = indexXml.selectFirst(DOC);
        final Element index = indexDoc.selectFirst(INDEX);

        index.attr(STYLE, getStyle(index, true));
        index.tagName(DIV).addClass(CO_INDEX);
        processTitle(index, internalLinks);
        processIndexEntries(index, internalLinks);
        citeQueryService.transformCiteQueries(index, indexDocGuid);

        indexSection.appendChild(index);
    }

    private void processTitle(final Element indexXml, final InlineIndexInternalLinks internalLinks) {
        indexXml.getElementsByTag(DOC_TITLE).forEach(title -> {
            title.tagName(DIV).addClass(CO_TITLE).attr(STYLE, stylingService.fontWeightBold());
            final Element headtext = indexXml.getElementsByTag(HEADTEXT).first().tagName(H3).addClass(CO_HEADTEXT);
            String headerId = internalLinks.buildHeaderId(headtext.text());
            headtext.attr(ID, headerId);
            title.empty();
            title.appendChild(headtext);
            internalLinks.addHeaderId(headerId);
        });
    }

    private void processIndexEntries(final Element indexXml, final InlineIndexInternalLinks internalLinks) {
        final Element list = new Element(OL).addClass(CO_TOC);

        indexXml.childNodes().stream()
            .filter(Objects::nonNull)
            .filter(node -> INDEX_ENTRY.equals(node.nodeName()))
            .collect(Collectors.toList())
            .forEach(entry -> processIndexEntry(list, (Element) entry, internalLinks));

        if (list.childNodeSize() > 0) {
            indexXml.appendChild(list);
        }
    }

    private void processIndexEntry(final Element list, final Element indexEntry, final InlineIndexInternalLinks internalLinks) {
        removeTags(indexEntry, UNUSED_TAGS_REGEX);
        indexEntry.tagName(DIV).attr(STYLE, getStyle(indexEntry, false));
        indexEntry.select(SUBJECT).tagName(SPAN).stream()
                .filter(subject -> subject.text().contains(THIS_INDEX))
                .forEach(internalLinks::addThisIndex);

        processIndexEntries(indexEntry, internalLinks);
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
}
