package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ANCHOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_HEADTEXT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_INDEX;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_TITLE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_TOC;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_TOC_HEADING;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.DIV;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.DOC;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.DOC_TITLE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.H3;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HEADTEXT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.INDEX;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.INDEX_ENTRY;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.INLINE_INDEX_HEADER;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.LI;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.OL;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SPAN;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.STYLE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.NAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SUBJECT;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createProviewPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.protectPagebreak;
import static com.thomsonreuters.uscl.ereader.format.service.InlineIndexInternalLinks.THIS_INDEX;

@Component
public class InlineIndexService {
    private static final String INLINE_INDEX_FILE_NAME = "inlineIndex.transformed";
    private static final String UNUSED_TAGS_REGEX = "bop|bos|eos|eop";
    private static final String XML = ".xml";
    private static final String INDEX_PAGE_NAME = "Index";
    private static final String INDEX_HEADER_NAME = "Index";

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
        addHeader(indexSection);
        indexDocGuids.forEach(indexDocGuid -> convertIndexDoc(indexDocGuid, sourceDir, indexSection, internalLinks));
        internalLinks.addThisIndexInternalLinks();

        jsoup.saveDocument(destDir, INLINE_INDEX_FILE_NAME, indexSection);
    }

    private void addFirstPagebreak(final Element indexSection, final boolean pages) {
        if (pages) {
            indexSection.append(protectPagebreak(createProviewPagebreak(INDEX_PAGE_NAME)));
        }
    }

    private void addHeader(final Element indexSection) {
        Element header = new Element(DIV);
        header.addClass(INLINE_INDEX_HEADER);
        header.text(INDEX_HEADER_NAME);
        indexSection.appendChild(header);
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
            final Element header = indexXml.getElementsByTag(HEADTEXT).first();
            String text = header.text();
            String headerId = internalLinks.buildHeaderId(text);
            header.tagName(H3).addClass(CO_HEADTEXT).empty().appendChild(new Element(ANCHOR).attr(NAME, headerId).text(text));
            title.empty().appendChild(header);
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
