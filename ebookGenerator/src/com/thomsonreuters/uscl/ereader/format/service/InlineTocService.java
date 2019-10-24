package com.thomsonreuters.uscl.ereader.format.service;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createPagebreak;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InlineTocService {
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String EBOOK_TOC = "EBookToc";
    private static final String SUMMARY = "summary";
    private static final String DETAILED = "detailed";
    private static final String TOC = "toc-";
    private static final String DIV = "div";
    private static final String INLINE_TOC_ANCHOR = "inlineTocAnchor";
    private static final String SUMMARY_HEADING = "SummaryHeading";
    private static final String DETAILED_HEADING = "DetailedHeading";
    private static final String SECTION = "section";
    private static final String INLINE_TOC_FILE_NAME = "inlineToc.html";
    private static final String SUM_PG_TEMPLATE = "sum_pg_%s";
    private static final String DET_PG_TEMPLATE = "det_pg_%s";
    private static final String SUM_PRINT_PG = "sum_print_pg";
    private static final String DET_PRINT_PG = "det_print_pg";
    private static final String SUM_SERIAL_PG = "sum_serial_pg";
    private static final String DET_SERIAL_PG = "det_serial_pg";
    private static final String NAME_ATTR = "name";
    private static final String HREF = "href";
    private static final String STYLE = "style";
    private static final String GUID = "Guid";
    private static final String NAME = "Name";

    @Autowired
    private DocMetadataService docMetadataService;

    @Autowired
    private CssStylingService stylingService;

    @Value("${proview.page.html.template}")
    private File proviewHtmlTemplate;

    @Autowired
    private JsoupService jsoup;

    public boolean generateInlineToc(final File tocXmlFile, final File outputDir, final boolean pages, final long jobId) {
        final Document tocXml = jsoup.loadDocument(tocXmlFile);

        if (inlineTocAttributesExist(tocXml)) {
            final TocDocument doc = loadInputElements(jobId, tocXml);

            if (pages) {
                generateWithPages(doc);
            } else {
                generateWithoutPages(doc);
            }

            jsoup.saveDocument(outputDir, INLINE_TOC_FILE_NAME, doc.getDocument());

            return true;
        }
        return false;
    }

    private TocDocument loadInputElements(final long jobId, final Document tocXml) {
        final Document document = jsoup.loadDocument(proviewHtmlTemplate);
        final Element section = document.selectFirst(SECTION);
        section.appendElement("a").attr(NAME_ATTR, INLINE_TOC_ANCHOR);

        final Optional<Element> summaryHeading = Optional.ofNullable(tocXml.selectFirst(SUMMARY_HEADING));
        final Optional<Element> detailedHeading = Optional.ofNullable(tocXml.selectFirst(DETAILED_HEADING));

        final List<Element> summaryTocElements = extractTocElements(tocXml, SUMMARY);
        final List<Element> detailedTocElements = extractTocElements(tocXml, DETAILED);

        final Map<String, String> familyGuidMap = docMetadataService.findDistinctProViewFamGuidsByJobId(jobId);
        return TocDocument.builder()
            .document(document)
            .section(section)
            .summaryHeading(summaryHeading)
            .detailedHeading(detailedHeading)
            .summaryTocElements(summaryTocElements)
            .detailedTocElements(detailedTocElements)
            .familyGuidMap(familyGuidMap)
        .build();
    }

    private List<Element> extractTocElements(final Document tocXml, final String tocElementType) {
        return tocXml.select(EBOOK_TOC).stream().filter(eBookToc -> Boolean.valueOf(eBookToc.attr(tocElementType))).collect(Collectors.toList());
    }

    private void generateWithoutPages(final TocDocument doc) {
        final Element section = doc.getSection();

        doc.getSummaryHeading().ifPresent(heading -> appendHeader(section, heading));
        doc.getSummaryTocElements().forEach(tocItem -> appendSummaryTocItem(section, tocItem));

        doc.getDetailedHeading().ifPresent(heading -> appendHeader(section, heading));
        doc.getDetailedTocElements().forEach(tocItem -> appendDetailedTocItem(section, tocItem, doc.getFamilyGuidMap()));
    }

    private void generateWithPages(final TocDocument doc) {
        final Map<Integer, String> summaryPageLabels = getPageLabelsMap(doc.getSummaryTocElements(), SUM_SERIAL_PG, SUM_PRINT_PG);
        final Map<Integer, Element> summaryPages = createPageSections(summaryPageLabels, SUM_PG_TEMPLATE, doc);
        final Map<Integer, String> detailedPageLabels = getPageLabelsMap(doc.getDetailedTocElements(), DET_SERIAL_PG, DET_PRINT_PG);
        final Map<Integer, Element> detailedPages = createPageSections(detailedPageLabels, DET_PG_TEMPLATE, doc);

        doc.getSummaryHeading().ifPresent(heading -> appendHeader(firstPage(summaryPages, summaryPageLabels), heading));
        doc.getSummaryTocElements().forEach(tocItem -> appendSummaryTocItem(getPage(summaryPages, tocItem, SUM_SERIAL_PG), tocItem));

        insertGapPagesFromSummaryToDetailed(summaryPages, summaryPageLabels, detailedPageLabels);

        doc.getDetailedHeading().ifPresent(heading -> appendHeader(firstPage(detailedPages, detailedPageLabels), heading));
        doc.getDetailedTocElements().forEach(tocItem -> appendDetailedTocItem(getPage(detailedPages, tocItem, DET_SERIAL_PG), tocItem, doc.getFamilyGuidMap()));
    }

    private Map<Integer, String> getPageLabelsMap(final List<Element> tocElements, final String pageAttrName, final String printPageAttrName) {
        return tocElements.stream()
            .filter(eBookToc -> StringUtils.isNoneBlank(eBookToc.attr(pageAttrName)))
            .collect(Collectors.toMap(eBookToc -> Integer.valueOf(eBookToc.attr(pageAttrName)),
                eBookToc -> eBookToc.attr(printPageAttrName), (oldVal, newVal) -> newVal));
    }

    private Map<Integer, Element> createPageSections(final Map<Integer, String> pageLabels, final String pageTemplate, final TocDocument doc) {
        return IntStream.rangeClosed(Collections.min(pageLabels.keySet()), Collections.max(pageLabels.keySet()))
            .mapToObj(Integer::valueOf)
            .collect(Collectors.toMap(Function.identity(),
                pageNum -> {
                    final Element pageSection = doc.getSection().appendElement(DIV).addClass(String.format(pageTemplate, pageNum));
                    pageSection.appendChild(createPagebreak(pageLabels.get(pageNum)));
                    return pageSection;
                }));
    }

    private Element firstPage(final Map<Integer, Element> pageSections, final Map<Integer, String> pageLabels) {
        return pageSections.get(Collections.min(pageLabels.keySet()));
    }

    private Element getPage(final Map<Integer, Element> summaryTocPagesElements, final Element tocItem, final String pageAttrName) {
        return summaryTocPagesElements.get(Integer.valueOf(tocItem.attr(pageAttrName)));
    }

    private void insertGapPagesFromSummaryToDetailed(
        final Map<Integer, Element> summaryTocPageSections,
        final Map<Integer, String> summaryTocPageLabels,
        final Map<Integer, String> detailedTocPageLabels) {
        final Integer maxSummaryTocPage = Collections.max(summaryTocPageLabels.keySet());
        final Integer minDetailedTocPage = Collections.min(detailedTocPageLabels.keySet());

        IntStream.range(maxSummaryTocPage + 1, minDetailedTocPage)
            .forEach(page -> summaryTocPageSections.get(maxSummaryTocPage)
                .appendChild(createPagebreak(TOC + page)));
    }

    private boolean inlineTocAttributesExist(final Document tocXml) {
        return tocXml.select(EBOOK_TOC).stream()
            .anyMatch(eBookToc -> StringUtils.isNoneBlank(eBookToc.attr(SUMMARY))
                               || StringUtils.isNoneBlank(eBookToc.attr(DETAILED)));
    }

    private void appendHeader(final Element section, final Element heading) {
        section.appendElement(DIV)
            .attr(STYLE, stylingService.getStyleByElement(heading))
            .text(heading.text());
    }

    private void appendSummaryTocItem(final Element section, final Element eBookToc) {
        section.appendElement(DIV)
            .attr(STYLE, stylingService.getStyleByElement(eBookToc, "sum_"))
            .appendElement("a")
            .attr(HREF, String.format("er:#inlineToc/sum%s", eBookToc.selectFirst(GUID).text()))
            .text(eBookToc.selectFirst(NAME).text());
    }

    private void appendDetailedTocItem(final Element section, final Element eBookToc, final Map<String, String> familyGuidMap) {
        section.appendElement(DIV)
            .attr(STYLE, stylingService.getStyleByElement(eBookToc, "det_"))
            .appendElement("a")
            .attr(NAME_ATTR, String.format("sum%s", eBookToc.selectFirst(GUID).text()))
            .attr(HREF, String.format("er:#%s/%s", familyGuidMap.get(eBookToc.selectFirst(DOCUMENT_GUID).text()), eBookToc.selectFirst(GUID).text()))
            .text(eBookToc.selectFirst(NAME).text());
    }

    @Getter
    @Builder
    private static class TocDocument {
      private Document document;
      private Element section;
      private List<Element> summaryTocElements;
      private List<Element> detailedTocElements;
      private Optional<Element> summaryHeading;
      private Optional<Element> detailedHeading;
      private Map<String, String> familyGuidMap;
    }
}
