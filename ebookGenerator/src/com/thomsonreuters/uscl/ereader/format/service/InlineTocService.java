package com.thomsonreuters.uscl.ereader.format.service;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createPagebreak;
import static java.util.Optional.ofNullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
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
    private static final String MISSING_DOCUMENT = "MissingDocument";
    private static final String EBOOK_TOC = "EBookToc";
    private static final String SUMMARY = "summary";
    private static final String DETAILED = "detailed";
    private static final String TOC = "toc-";
    private static final String DIV = "div";
    private static final String A_TAG = "a";
    private static final String INLINE_TOC_ANCHOR = "inlineTocAnchor";
    private static final String SUMMARY_TOC_ANCHOR = "summaryTocAnchor";
    private static final String DETAILED_TOC_ANCHOR = "detailedTocAnchor";
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
    private static final String DEFAULT_SUMMARY_HEADING = "<SummaryHeading align=\"left\" fv=\"1\" size=\"0.25i\" prelead=\"0.51i\" cm=\"normal\">Summary of Contents</SummaryHeading>";
    private static final String DEFAULT_DETAILED_HEADING = "<DetailedHeading align=\"left\" fv=\"1\" size=\"0.25i\" prelead=\"0.51i\" cm=\"normal\">Table of Contents</DetailedHeading>";
    private static final String E_BOOK = "EBook";
    private static final String SUM_ALIGN = "sum_align";
    private static final String SUM_LINDENT = "sum_lindent";
    private static final String SUMMARY_OF_CONTENTS = "Summary of Contents";
    private static final String DET_PRELEAD = "det_prelead";
    private static final String DET_CM = "det_cm";
    private static final String DET_FV = "det_fv";
    private static final String SUM_PRELEAD = "sum_prelead";
    private static final String DET_ALIGN = "det_align";
    private static final String DET_LINDENT = "det_lindent";
    private static final String ALIGN_LEFT = "left";
    private static final String TABLE_OF_CONTENTS = "Table of Contents";
    private static final double BASE_INDENT = 0.2;
    private static final String MAJOR_PRELEAD = "0.34i";
    private static final String MINOR_PRELEAD = "0.17i";
    private static final String UNIT = "i";
    private static final String SUMMARY_PAGE = "1";
    private static final String DETAILED_PAGE = "2";
    private static final String CM_UPPER = "upper";
    private static final String DETAILED_FV = "1";
    private static final int SUMMARY_LEVEL = 2;

    @Autowired
    private DocMetadataService docMetadataService;

    @Autowired
    private CssStylingService stylingService;

    @Value("${proview.page.html.template}")
    private File proviewHtmlTemplate;

    @Autowired
    private JsoupService jsoup;

    public void generateInlineToc(final File tocXmlFile, final File outputDir, final boolean pages, final long jobId, final String titleId, final Version version) {
        final Document tocXml = jsoup.loadDocument(tocXmlFile);

        if (!inlineTocAttributesExist(tocXml)) {
            assignDefaultInlineTocAttributes(tocXml);
        }

        final TocDocument doc = loadInputElements(tocXml, jobId, titleId, version);

        if (pages) {
            generateWithPages(doc);
        } else {
            generateWithoutPages(doc);
        }

        jsoup.saveDocument(outputDir, INLINE_TOC_FILE_NAME, doc.getDocument());
    }

    private void assignDefaultInlineTocAttributes(final Document tocXml) {
        Element root = tocXml.select(E_BOOK).first();
        root.append(DEFAULT_SUMMARY_HEADING);
        root.append(DEFAULT_DETAILED_HEADING);
        assignToChildren(root, 0);
    }

    private void assignToChildren(final Element toc, final int level) {
        toc.children().stream()
                .filter(el -> el.nodeName().equals(EBOOK_TOC))
                .forEach(el -> assignDefaultInlineTocAttributes(el, level));
    }

    private void assignDefaultInlineTocAttributes(final Element toc, final int level) {
        if (level < SUMMARY_LEVEL) {
            toc.attr(SUMMARY, Boolean.TRUE.toString());
            toc.attr(SUM_ALIGN, ALIGN_LEFT);
            toc.attr(SUM_LINDENT, (BASE_INDENT * level) + UNIT);
            toc.attr(SUM_SERIAL_PG, SUMMARY_PAGE);
            toc.attr(SUM_PRINT_PG, SUMMARY_OF_CONTENTS);

            toc.attr(DET_PRELEAD, MAJOR_PRELEAD);
            toc.attr(DET_CM, CM_UPPER);
            toc.attr(DET_FV, DETAILED_FV);
        } else {
            toc.attr(DET_PRELEAD, MINOR_PRELEAD);
        }

        toc.attr(SUM_PRELEAD, MINOR_PRELEAD);
        toc.attr(DETAILED, Boolean.TRUE.toString());
        toc.attr(DET_ALIGN, ALIGN_LEFT);
        toc.attr(DET_LINDENT, (BASE_INDENT * level) + UNIT);
        toc.attr(DET_SERIAL_PG, DETAILED_PAGE);
        toc.attr(DET_PRINT_PG, TABLE_OF_CONTENTS);

        assignToChildren(toc, level + 1);
    }

    private TocDocument loadInputElements(final Document tocXml, final long jobId, final String titleId, final Version version) {
        final Document document = jsoup.loadDocument(proviewHtmlTemplate);
        final Element section = document.selectFirst(SECTION);

        final Optional<Element> summaryHeading = ofNullable(tocXml.selectFirst(SUMMARY_HEADING));
        final Optional<Element> detailedHeading = ofNullable(tocXml.selectFirst(DETAILED_HEADING));

        final List<Element> summaryTocElements = extractTocElements(tocXml, SUMMARY);
        final List<Element> detailedTocElements = extractTocElements(tocXml, DETAILED);

        final Map<String, String> familyGuidMap = docMetadataService.findDistinctProViewFamGuidsByJobId(jobId);
        return TocDocument.builder()
            .jobIdentifier(jobId)
            .titleId(titleId)
            .version(version)
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
        return tocXml.select(EBOOK_TOC).stream().filter(eBookToc -> Boolean.parseBoolean(eBookToc.attr(tocElementType))).collect(Collectors.toList());
    }

    private void generateWithoutPages(final TocDocument doc) {
        final Element section = doc.getSection();

        doc.getSummaryHeading().ifPresent(heading -> {
            appendAnchor(section, INLINE_TOC_ANCHOR);
            appendHeader(section, heading);
        });
        doc.getSummaryTocElements().forEach(tocItem -> appendSummaryTocItem(section, tocItem));

        doc.getDetailedHeading().ifPresent(heading -> appendHeader(section, heading));
        doc.getDetailedTocElements().forEach(tocItem -> appendDetailedTocItem(section, tocItem, doc));
    }

    private void generateWithPages(final TocDocument doc) {
        final Map<Integer, String> summaryPageLabels = getPageLabelsMap(doc.getSummaryTocElements(), SUM_SERIAL_PG, SUM_PRINT_PG);
        final Map<Integer, Element> summaryPages = createPageSections(summaryPageLabels, SUM_PG_TEMPLATE, doc);
        final Map<Integer, String> detailedPageLabels = getPageLabelsMap(doc.getDetailedTocElements(), DET_SERIAL_PG, DET_PRINT_PG);
        final Map<Integer, Element> detailedPages = createPageSections(detailedPageLabels, DET_PG_TEMPLATE, doc);

        doc.getSummaryHeading().ifPresent(heading -> {
            final Element firstPage = firstPage(summaryPages, summaryPageLabels);
            appendAnchor(firstPage, SUMMARY_TOC_ANCHOR);
            appendHeader(firstPage, heading);
        });
        doc.getSummaryTocElements().forEach(tocItem -> appendSummaryTocItem(getPage(summaryPages, tocItem, SUM_SERIAL_PG), tocItem));

        insertGapPagesFromSummaryToDetailed(summaryPages, summaryPageLabels, detailedPageLabels);

        doc.getDetailedHeading().ifPresent(heading -> {
            final Element firstPage = firstPage(detailedPages, detailedPageLabels);
            appendAnchor(firstPage, DETAILED_TOC_ANCHOR);
            appendHeader(firstPage, heading);
        });
        doc.getDetailedTocElements().forEach(tocItem -> appendDetailedTocItem(getPage(detailedPages, tocItem, DET_SERIAL_PG), tocItem, doc));
    }

    private Map<Integer, String> getPageLabelsMap(final List<Element> tocElements, final String pageAttrName, final String printPageAttrName) {
        return tocElements.stream()
            .filter(eBookToc -> StringUtils.isNoneBlank(eBookToc.attr(pageAttrName)))
            .collect(Collectors.toMap(eBookToc -> Integer.valueOf(eBookToc.attr(pageAttrName)),
                eBookToc -> eBookToc.attr(printPageAttrName), (oldVal, newVal) -> newVal));
    }

    private Map<Integer, Element> createPageSections(final Map<Integer, String> pageLabels, final String pageTemplate, final TocDocument doc) {
        return IntStream.rangeClosed(Collections.min(pageLabels.keySet()), Collections.max(pageLabels.keySet()))
            .boxed()
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

    private void appendAnchor(final Element section, final String anchorName) {
        section.appendElement(A_TAG).attr(NAME_ATTR, anchorName);
    }

    private void appendSummaryTocItem(final Element section, final Element eBookToc) {
        section.appendElement(DIV)
            .attr(STYLE, stylingService.getStyleByElement(eBookToc, "sum_"))
            .appendElement(A_TAG)
            .attr(HREF, String.format("er:#inlineToc/sum%s", eBookToc.selectFirst(GUID).text()))
            .text(eBookToc.selectFirst(NAME).text());
    }

    private void appendDetailedTocItem(final Element section, final Element eBookToc, final TocDocument doc) {
        String guid = eBookToc.selectFirst(GUID).text();
        String documentGuid = getDocumentGuid(eBookToc);
        String familyGuid = getFamilyGuid(doc, documentGuid);
        String splitTitleId = getSplitTitleId(eBookToc, doc, documentGuid);

        section.appendElement(DIV)
            .attr(STYLE, stylingService.getStyleByElement(eBookToc, "det_"))
            .appendElement(A_TAG)
            .attr(NAME_ATTR, String.format("sum%s", guid))
            .attr(HREF, String.format("er:%s#%s/%s",
                    splitTitleId,
                    familyGuid,
                    guid))
            .text(eBookToc.selectFirst(NAME).text());
    }

    private String getDocumentGuid(final Element eBookToc) {
        return ofNullable(eBookToc.selectFirst(DOCUMENT_GUID))
                .orElseGet(() -> getSiblingGuid(eBookToc.selectFirst(MISSING_DOCUMENT)))
                .text();
    }

    private Element getSiblingGuid(final Element element) {
        return element.parent().selectFirst(GUID);
    }

    private String getFamilyGuid(final TocDocument doc, final String documentGuid) {
        return ofNullable(doc.getFamilyGuidMap().get(documentGuid)).orElse(documentGuid);
    }

    private String getSplitTitleId(final Element eBookToc, final TocDocument doc, final String documentGuid) {
        DocMetadata docMetadata = getDocMetadata(eBookToc, doc, documentGuid);
        return docMetadata == null || docMetadata.isFirstSplitTitle() ? StringUtils.EMPTY : docMetadata.getSplitBookTitle() + "/" + doc.getVersion().getMajorVersion();
    }

    private DocMetadata getDocMetadata(final Element eBookToc, final TocDocument doc, final String documentGuid) {
        DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(doc.getTitleId(), doc.getJobIdentifier(), documentGuid);
        if (docMetadata == null) {
            String siblingDocumentGuid = getAnyClosestDocumentGuid(eBookToc);
            docMetadata = docMetadataService.findDocMetadataByPrimaryKey(doc.getTitleId(), doc.getJobIdentifier(), siblingDocumentGuid);
        }
        return docMetadata;
    }

    private String getAnyClosestDocumentGuid(final Element eBookToc) {
        Element current = eBookToc;
        String documentGuid = null;
        while(current != null && documentGuid == null) {
            documentGuid = getAnySiblingDocumentGuid(current);
            current = current.parent();
        }
        return documentGuid;
    }

    private String getAnySiblingDocumentGuid(final Element eBookToc) {
        return ofNullable(getSiblingDocumentGuid(eBookToc.nextElementSiblings()))
                .orElse(getSiblingDocumentGuid(eBookToc.previousElementSiblings()));
    }

    private String getSiblingDocumentGuid(final Collection<Element> siblings) {
        return siblings.stream().map(siblingToc -> siblingToc.selectFirst(DOCUMENT_GUID))
                .filter(Objects::nonNull).findFirst()
                .map(Element::text).orElse(null);
    }

    @Getter
    @Builder
    private static class TocDocument {
      private long jobIdentifier;
      private String titleId;
      private Version version;
      private Document document;
      private Element section;
      private List<Element> summaryTocElements;
      private List<Element> detailedTocElements;
      private Optional<Element> summaryHeading;
      private Optional<Element> detailedHeading;
      private Map<String, String> familyGuidMap;
    }
}
