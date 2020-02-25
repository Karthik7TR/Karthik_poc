package com.thomsonreuters.uscl.ereader.format.service;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.LABEL;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PB;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.convertToProviewPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createPagebreak;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReorderFootnotesService {
    private static final String PROVIEW_FOOTNOTE_CLASS = "er_rp_search_volume_content_data";
    private static final String ID = "id";
    private static final String DIV = "div";
    private static final String FOOTNOTE = "footnote";
    private static final String FOOTNOTE_BODY = "footnote_body";
    private static final String SECTION = "section";
    private static final String A_TAG = "a";
    private static final String A_HREF = "a[href]";
    private static final String HREF = "href";
    private static final String NAME = "name";
    private static final String SUP = "sup";
    private static final String AUTHOR_FOOTNOTES = "author.footnotes";
    private static final String FOOTNOTE_BLOCK = "footnote.block";
    private static final String FTNNAME = "ftnname";
    private static final String TR_FTN = "tr_ftn";
    private static final String TR_FOOTNOTES = "tr_footnotes";
    private static final String TR_FOOTNOTE = "tr_footnote";
    private static final String TR_FOOTNOTE_CLASS = ".tr_footnote";
    private static final String CO_FOOTNOTE_CLASS_PREFIX = "co_footnote_";
    private static final String CO_FOOTNOTE_REFERENCE = "co_footnoteReference";
    private static final String CO_FOOTNOTE_SECTION_ID = "#co_footnoteSection";
    private static final String CO_FOOTNOTE_SECTION_TITLE = "co_footnoteSectionTitle";
    private static final String CO_FOOTNOTE_NUMBER = "co_footnoteNumber";
    private static final String CO_FOOTNOTE_NUMBER_CLASS = ".co_footnoteNumber";
    private static final String DIV_CO_FOOTNOTE_BODY = "div.co_footnoteBody";
    private static final String DIV_CO_FOOTNOTE_NUMBER = "div.co_footnoteNumber";
    private static final String DOCUMENT_GUID = "DocumentGuid";

    private static final String PAGE_NUMBER_REFERENCE_TEMPLATE = "<a ftnname=\"page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" name=\"page_number_fnRef_PAGE_NUMBER_FOOTNOTE_ID\" href=\"#page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" class=\"tr_ftn\" />";
    private static final String PAGE_NUMBER_FOOTNOTE_ID_PLACEHOLDER = "PAGE_NUMBER_FOOTNOTE_ID";
    private static final String PAGE_NUMBER_PLACEHOLDER = "PAGE_NUMBER";
    public static final String INITIAL_PAGE_LABEL = "extractPageNumbersInitialPageLabel";

    @Autowired
    private JsoupService jsoup;

    @Value("${page.number.footnote.template}")
    private File pageNumberFootnoteTemplateFile;

    private String pageNumberFootnoteTemplate;

    private List<File> orderedDocuments(final File gatherToc, final File srcDir) {
        final Map<String, File> nameToSrcFile = getNameFileMap(srcDir);
        return orderedDocumentIds(gatherToc).stream().map(nameToSrcFile::get).collect(Collectors.toList());
    }

    private List<String> orderedDocumentIds(final File gatherToc) {
        final Document toc = jsoup.loadDocument(gatherToc);
        return toc.select(DOCUMENT_GUID).eachText();
    }

    public void reorderFootnotes(final File gatherToc, final File srcGatherDir, final File srcDir, final File destDir) throws IOException {
        pageNumberFootnoteTemplate = FileUtils.readFileToString(pageNumberFootnoteTemplateFile);
        final Map<String, File> nameToXmlFile = getNameFileMap(srcGatherDir);

        List<File> srcFilesOrdered = orderedDocuments(gatherToc, srcDir);
        Map<String, String> pageNumbers = extractPageNumbers(srcFilesOrdered);
//        convertPageEndsToPageStarts(srcFilesOrdered);
//        final Element placeholder = new Element(DIV);

        String currentPage[] = new String[] {INITIAL_PAGE_LABEL};
        boolean fromTopOfPage[] = new boolean[] {true};


        srcFilesOrdered.forEach(file -> {
            final Document doc = jsoup.loadDocument(file);
            final String fileUuid = FilenameUtils.removeExtension(file.getName());
            final Element mainSection = doc.selectFirst(SECTION);
            final Optional<Element> footnotesBlock = Optional.ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID));
            convertPageEndsToPageStarts(mainSection, fromTopOfPage, currentPage, pageNumbers);

            final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(mainSection);
            if (footnotesBlock.isPresent() || pagebreaksFromMain.size() > 0) {
                footnotesBlock.ifPresent(Element::remove);
                convertFootnoteReferencesInMainSection(mainSection);
                convertFootnotes(doc, footnotesBlock);

                final Element footnotesSectionToAppend = constructFootnotesSection(nameToXmlFile, footnotesBlock, doc, fileUuid);
                addPageLabels(mainSection, footnotesSectionToAppend, fileUuid);
                mainSection.after(footnotesSectionToAppend);
            }

            jsoup.saveDocument(destDir, file.getName(), doc);
        });
    }

    private Map<String, String> extractPageNumbers(List<File> srcFilesOrdered) {
        Map<String, String> pageNumbers = new HashMap<>();
        String pagebreakPrevious = INITIAL_PAGE_LABEL;
        for (File file : srcFilesOrdered) {
            final Document doc = jsoup.loadDocument(file);
            final Element mainSection = doc.selectFirst(SECTION);
            final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(mainSection);
            for (XmlDeclaration pagebreak : pagebreakEnds) {
                if (pagebreakPrevious == null) {
                    pagebreakPrevious = pagebreak.attr("label");
                    continue;
                }
                pageNumbers.put(pagebreakPrevious, pagebreak.attr("label"));
                pagebreakPrevious = pagebreak.attr("label");
            }
        }
        return pageNumbers;
    }

    @NotNull
    private Map<String, File> getNameFileMap(File dir) {
        return Stream.of(dir.listFiles())
                .collect(Collectors.toMap(file -> FilenameUtils.removeExtension(file.getName()), Function.identity()));
    }

    private void convertFootnotes(final Document doc, final Optional<Element> footnotesSection) {
        footnotesSection.ifPresent(element ->
            element.getElementsByClass(CO_FOOTNOTE_NUMBER).stream()
            .map(footnote -> footnote.parent())
            .forEach(footnote -> convertFootnote(footnote, doc))
        );
    }

    private Map<String, Element> getFootnotesMap(final Optional<Element> footnotesSection) {
        return footnotesSection.map(element ->
            element.getElementsByClass(TR_FOOTNOTE).stream()
            .collect(Collectors.toMap(footnote -> getFootnoteId(footnote), Function.identity()))
        ).orElse(new HashMap<>());
    }

    private String getFootnoteId(final Element footnote) {
        return footnote.selectFirst(CO_FOOTNOTE_NUMBER_CLASS)
                       .selectFirst(A_TAG)
                       .attr(NAME)
                       .replaceFirst(CO_FOOTNOTE_CLASS_PREFIX, "");
    }

    private void convertFootnote(final Element footnote, final Document doc) {
        footnote.addClass(TR_FOOTNOTE);

        final Element footnoteNumber = footnote.selectFirst(DIV_CO_FOOTNOTE_NUMBER);
        footnoteNumber.tagName(SUP);

        final Element ref = footnoteNumber.selectFirst(A_HREF);
        convertFootnoteReference(ref, false);

        final Element footnoteBody = footnote.selectFirst(DIV_CO_FOOTNOTE_BODY);
        footnoteBody.addClass(PROVIEW_FOOTNOTE_CLASS);

        wrapContentToDiv(footnoteBody, FOOTNOTE_BODY, doc);
        wrapContentToDiv(footnote, FOOTNOTE, doc);
    }

    private Element constructFootnotesSection(
        final Map<String, File> nameToXmlFile,
        final Optional<Element> footnotesBlock,
        final Document document,
        final String fileUuid) {
        final Optional<File> xmlFile = Optional.ofNullable(nameToXmlFile.get(fileUuid));
        final Optional<Document> xmlDoc = xmlFile.map(file -> jsoup.loadDocument(file));
        final Optional<Element> footnotesTemplate = xmlDoc.map(doc -> doc.getElementsByTag(FOOTNOTE_BLOCK).first());

        if (footnotesTemplate.isPresent()) {
            return constructBasedOnTemplate(getFootnotesMap(footnotesBlock), footnotesTemplate.get(), xmlDoc.get());
        } else {
            return constructBasedOnBlock(footnotesBlock, document);
        }
    }

    private Element constructBasedOnTemplate(
        final Map<String, Element> idToFootnote,
        final Element footnotesTemplate,
        final Document xmlDoc) {
        convertPagebreaksToProviewPbs(footnotesTemplate);
        convertPageEndsToPageStarts(footnotesTemplate);
        convertTopToFootnotesSection(footnotesTemplate);

        fillTemplateWithFootnotes(idToFootnote, footnotesTemplate);
        addAuthorFootnotes(idToFootnote, footnotesTemplate, xmlDoc);

        return footnotesTemplate;
    }

    private Element constructBasedOnBlock(final Optional<Element> block, final Document doc) {
        final Element footnotesBlock = block.orElse(new Element(DIV));
        convertTopToFootnotesSection(footnotesBlock);

        footnotesBlock.getElementsByClass(CO_FOOTNOTE_SECTION_TITLE).remove();
        insertPagebreaksWithContentOnFirstPage(footnotesBlock, doc);

        return footnotesBlock;
    }

    private void insertPagebreaksWithContentOnFirstPage(final Element footnotesBlock, final Document doc) {
        final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(doc);

        for (int i = 0; i < pagebreaksFromMain.size(); i++) {
            final Node pagebreak = pagebreaksFromMain.get(i).clone();
            if (i == 0) {
                footnotesBlock.prependChild(pagebreak);
            } else {
                footnotesBlock.appendChild(pagebreak);
            }
        }
    }

    private void fillTemplateWithFootnotes(final Map<String, Element> idToFootnote, final Element footnotesTemplate) {
        footnotesTemplate.getElementsByTag(FOOTNOTE)
            .forEach(footnote -> {
                final String footnoteId = footnote.attr(ID);
                footnote.replaceWith(idToFootnote.get(footnoteId));
            });
    }

    private void addAuthorFootnotes(
        final Map<String, Element> idToFootnote,
        final Element footnotesTemplate,
        final Document xmlDoc) {
        final Element authorFootnotes = xmlDoc.getElementsByTag(AUTHOR_FOOTNOTES).first();
        if (authorFootnotes != null) {
            final Optional<XmlDeclaration> firstPagebreak = getFirstPagebreak(footnotesTemplate);

            authorFootnotes.getElementsByTag(FOOTNOTE).stream()
            .sorted(Collections.reverseOrder())
            .forEach(footnote -> {
                final String footnoteId = footnote.attr(ID);
                firstPagebreak.ifPresent(pagebreak -> pagebreak.after(idToFootnote.get(footnoteId)));
            });
        }
    }

    private void convertTopToFootnotesSection(final Element footnotesTemplate) {
        footnotesTemplate.tagName(SECTION);
        footnotesTemplate.addClass(TR_FOOTNOTES);
    }

    private void convertFootnoteReferencesInMainSection(final Element mainSection) {
        mainSection.getElementsByClass(CO_FOOTNOTE_REFERENCE)
            .forEach(ftnRef -> {
                convertFootnoteReference(ftnRef, true);
            });
    }

    private void wrapContentToDiv(final Element element, final String className, final Document doc) {
        final Element div = doc.createElement(DIV);
        div.addClass(className);
        div.append(element.html());

        element.children().remove();
        element.appendChild(div);
    }

    private void convertFootnoteReference(final Element innerRef, final boolean addHref) {
        final Element ref = innerRef.parent();

        ref.addClass(TR_FTN);
        final String refName = extractReferenceName(innerRef.attr(HREF));
        ref.attr(FTNNAME, refName);

        ref.attr(HREF, addHref ? "#" + refName : "");
        innerRef.remove();
        ref.append(innerRef.text());
    }

    private String extractReferenceName(final String reference) {
        return reference.substring(reference.lastIndexOf("/") + 1);
    }

    private void addPageLabels(final Element mainSection, final Element footnotesSection, final String fileUuid) {
        addPageReferencesInMainSection(mainSection, fileUuid);
        appendPagenumbersToFootnotesSection(footnotesSection, fileUuid);
    }

    private void appendPagenumbersToFootnotesSection(final Element footnotesSection, final String fileUuid) {
        final List<XmlDeclaration> pagebreaks = getProviewPagebreaks(footnotesSection);

        IntStream.range(1, pagebreaks.size()).forEach(index -> {
            pagebreaks.get(index).before(createPageNumberFootnote(pagebreaks.get(index - 1).attr(LABEL), fileUuid));
        });

        footnotesSection.appendChild(createPageNumberFootnote(pagebreaks.get(pagebreaks.size() - 1).attr(LABEL), fileUuid));
    }


//    private void convertPageEndsToPageStarts(List<File> srcFilesOrdered) {
//        for(int i = 0; i < srcFilesOrdered.size(); i++) {
//
//        }
//    }
    private void convertPageEndsToPageStarts(final Element section, final boolean[] fromTopOfPage, String[] currentPage, Map<String, String> pageNumbers) {
        final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(section);
//        final Element placeholder = new Element(DIV);
        if (fromTopOfPage[0]) {
            section.childNode(0).before(createPagebreak(pageNumbers.get(currentPage[0])));
            fromTopOfPage[0] = false;
        }


        for (int i = 0; i < pagebreakEnds.size(); i++) {
            final XmlDeclaration pagebreakEnd = pagebreakEnds.get(i);
            currentPage[0] = pagebreakEnd.attr("label");

            if (isLastElementInSection(pagebreakEnd)) {
                fromTopOfPage[0] = true;
            } else {
                pagebreakEnd.after(createPagebreak(pageNumbers.get(currentPage[0])));
            }
            pagebreakEnd.remove();
        }
    }

    private boolean isLastElementInSection(XmlDeclaration pagebreakEnd) {
        Node next = pagebreakEnd;
        do {
            if (hasNextSibling(next)) {
                next = next.nextSibling();

                if (next instanceof TextNode && !((TextNode)next).isBlank()) return false;
                if (next instanceof Element) {
                    Element element = (Element) next;
                    if ("br".equalsIgnoreCase(element.tagName())) continue;
                    return element.hasClass("co_copyright");
                }
            } else {
                next = next.parentNode();
                if (isSectionNode(next)) return true;
            }
        } while(true);
    }

    private boolean hasNextSibling(Node node) {
        return node.nextSibling() != null;
    }

    private boolean isSectionNode(Node node) {
        if (node instanceof Element) {
            Element element = (Element) node;
            String tagName = element.tagName().toLowerCase();
            String className = element.className().toLowerCase();
            return tagName.matches("section|body") || className.matches("co_docDisplay|co_footnoteSection");
        }
        return false;
    }

    private void convertPageEndsToPageStarts(final Element section) {
        final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(section);
        final Element placeholder = new Element(DIV);

        section.childNode(0).before(placeholder);

        for (int i = 0; i < pagebreakEnds.size(); i++) {
            final XmlDeclaration pagebreakEnd = pagebreakEnds.get(i);

            placeholder.after(pagebreakEnd.clone());
            placeholder.remove();
            pagebreakEnd.after(placeholder);
            pagebreakEnd.remove();
        }
        placeholder.remove();
    }

    private Element createPageNumberFootnote(final String label, final String fileUuid) {
        return Jsoup.parse(prepareHtmlFromTemplate(pageNumberFootnoteTemplate, label, fileUuid)).selectFirst(TR_FOOTNOTE_CLASS);
    }

    private String prepareHtmlFromTemplate(final String template, final String label, final String fileUuid) {
        return template.replaceAll(PAGE_NUMBER_FOOTNOTE_ID_PLACEHOLDER, fileUuid + "_" + label)
                       .replaceAll(PAGE_NUMBER_PLACEHOLDER, label);
    }

    private void addPageReferencesInMainSection(final Element mainSection, final String fileUuid) {
        getProviewPagebreaks(mainSection)
            .forEach(pagebreak ->
                pagebreak.after(prepareHtmlFromTemplate(PAGE_NUMBER_REFERENCE_TEMPLATE, pagebreak.attr(LABEL), fileUuid))
            );
    }

    private void convertPagebreaksToProviewPbs(final Element footnotesTemplate) {
        getPagebreaks(footnotesTemplate)
            .forEach(pagebreak -> pagebreak.replaceWith(convertToProviewPagebreak(pagebreak)));
    }

    private Optional<XmlDeclaration> getFirstPagebreak(final Element footnotesSection) {
        return getProviewPagebreaks(footnotesSection).stream().findFirst();
    }

    private List<XmlDeclaration> getPagebreaks(final Element element) {
        return jsoup.selectXmlProcessingInstructions(element, PAGEBREAK);
    }

    private List<XmlDeclaration> getProviewPagebreaks(final Element element) {
        return jsoup.selectXmlProcessingInstructions(element, PB);
    }
}
