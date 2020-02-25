package com.thomsonreuters.uscl.ereader.format.service;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PB;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.convertToProviewPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.getLabel;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private static final String FOOTNOTE_BODY_TAG = "footnote.body";
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
    private static final String CO_DIVIDER = "co_divider";
    private static final String CO_COPYRIGHT = "co_copyright";
    private static final String CO_END_OF_DOCUMENT = "co_endOfDocument";
    private static final String CO_PARAGRAPH = "co_paragraph";
    private static final String CO_PARAGRAPH_TEXT = "co_paragraphText";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String PAGE_NUMBER_REFERENCE_TEMPLATE = "<a ftnname=\"page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" name=\"page_number_fnRef_PAGE_NUMBER_FOOTNOTE_ID\" href=\"#page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" class=\"tr_ftn\" />";
    private static final String PAGE_NUMBER_FOOTNOTE_ID_PLACEHOLDER = "PAGE_NUMBER_FOOTNOTE_ID";
    private static final String PAGE_NUMBER_PLACEHOLDER = "PAGE_NUMBER";
    private static final String INITIAL_PAGE_LABEL = "extractPageNumbersInitialPageLabel";
    private static final String PARA = "para";
    private static final String PARATEXT = "paratext";

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

        final PagePointer mainPage = new PagePointer(pageNumbers);
        final PagePointer footnotePage = new PagePointer(pageNumbers);

        srcFilesOrdered.forEach(file -> {
            final Document doc = jsoup.loadDocument(file);
            final String fileUuid = FilenameUtils.removeExtension(file.getName());
            final Element mainSection = doc.selectFirst(SECTION);
            final Optional<Element> footnotesBlock = Optional.ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID));
            footnotesBlock.ifPresent(Element::remove);

            convertPageEndsToPageStarts(mainSection, mainPage);

            final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(mainSection);
            if (footnotesBlock.isPresent() || pagebreaksFromMain.size() > 0) {
                convertFootnoteReferencesInMainSection(mainSection);
                convertFootnotes(doc, footnotesBlock);

                final Element footnotesSectionToAppend = constructFootnotesSection(nameToXmlFile, footnotesBlock, doc, fileUuid, footnotePage);
//                addPageLabels(mainSection, footnotesSectionToAppend, fileUuid);
                mainSection.after(footnotesSectionToAppend);
            }

            doc.getElementsByClass(CO_DIVIDER).remove();
            doc.getElementsByClass(CO_COPYRIGHT).remove();
            doc.getElementById(CO_END_OF_DOCUMENT).remove();

            jsoup.saveDocument(destDir, file.getName(), doc);
        });

        copyAuxiliaryFiles(srcDir, srcFilesOrdered, destDir);
    }

    private void copyAuxiliaryFiles(final File srcDir, final List<File> processedFiles, final File destDir) throws IOException {
        Set<File> processed = new HashSet<>(processedFiles);
        Arrays.stream(Objects.requireNonNull(srcDir.listFiles()))
                .filter(file -> !processed.contains(file))
                .forEach(file -> copy(file, destDir));
    }

    @SneakyThrows
    private void copy(final File srcFile, final File destDir) {
        FileUtils.copyFileToDirectory(srcFile, destDir);
    }

    private Map<String, String> extractPageNumbers(List<File> srcFilesOrdered) {
        Map<String, String> pageNumbers = new HashMap<>();
        String pagebreakPrevious = INITIAL_PAGE_LABEL;
        for (File file : srcFilesOrdered) {
            final Document doc = jsoup.loadDocument(file);
            final Element mainSection = doc.selectFirst(SECTION);
            Optional.ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID)).ifPresent(Element::remove);
            final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(mainSection);
            for (XmlDeclaration pagebreak : pagebreakEnds) {
                if (pagebreakPrevious == null) {
                    pagebreakPrevious = getLabel(pagebreak);
                    continue;
                }
                pageNumbers.put(pagebreakPrevious, getLabel(pagebreak));
                pagebreakPrevious = getLabel(pagebreak);
            }
        }
        return pageNumbers;
    }

    @NotNull
    private Map<String, File> getNameFileMap(File dir) {
        return Stream.of(Objects.requireNonNull(dir.listFiles()))
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
        final String fileUuid,
        final PagePointer pagePointer) {
        final Optional<File> xmlFile = Optional.ofNullable(nameToXmlFile.get(fileUuid));
        final Optional<Document> xmlDoc = xmlFile.map(file -> jsoup.loadDocument(file));
        final Optional<Element> footnotesTemplate = xmlDoc.map(doc -> doc.getElementsByTag(FOOTNOTE_BLOCK).first());

        if (footnotesTemplate.isPresent()) {
            return constructBasedOnTemplate(getFootnotesMap(footnotesBlock), footnotesTemplate.get(), xmlDoc.get(), pagePointer);
        } else {
            return constructBasedOnBlock(footnotesBlock, document, pagePointer);
        }
    }

    private Element constructBasedOnTemplate(
        final Map<String, Element> idToFootnote,
        final Element footnotesTemplate,
        final Document xmlDoc,
        final PagePointer pagePointer) {
        convertPagebreaksToProviewPbs(footnotesTemplate);
        convertPageEndsToPageStarts(footnotesTemplate, pagePointer);
        convertTopToFootnotesSection(footnotesTemplate);

        fillTemplateWithFootnotes(idToFootnote, footnotesTemplate);
        addAuthorFootnotes(idToFootnote, footnotesTemplate, xmlDoc);

        return footnotesTemplate;
    }

    private Element constructBasedOnBlock(final Optional<Element> block, final Document doc, final PagePointer pagePointer) {
        final Element footnotesBlock = block.orElse(new Element(DIV));
        convertTopToFootnotesSection(footnotesBlock);

        footnotesBlock.getElementsByClass(CO_FOOTNOTE_SECTION_TITLE).remove();
        insertPagebreaksWithContentOnFirstPage(footnotesBlock, doc, pagePointer);

        return footnotesBlock;
    }

    private void insertPagebreaksWithContentOnFirstPage(final Element footnotesBlock, final Document doc, final PagePointer pagePointer) {
        final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(doc);

        for (int i = 0; i < pagebreaksFromMain.size(); i++) {
            final Node pagebreak = pagebreaksFromMain.get(i).clone();
            pagePointer.setCurrentPage(getLabel(pagebreak));
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

                Element footnoteHtml = idToFootnote.get(footnoteId);

                Element bodyTemplate = footnote.getElementsByTag(FOOTNOTE_BODY_TAG).first();
                Element bodyHtml = footnoteHtml.getElementsByClass(FOOTNOTE_BODY).first();

                bodyHtml.replaceWith(convertToHtml(bodyTemplate));
                footnote.replaceWith(footnoteHtml);
            });
    }

    private Node convertToHtml(Element element) {
        switch(element.tagName()) {
            case FOOTNOTE_BODY_TAG:
                element.addClass(FOOTNOTE_BODY);
                break;
            case PARA:
                element.addClass(CO_PARAGRAPH);
                break;
            case PARATEXT:
                element.addClass(CO_PARAGRAPH_TEXT);
                break;
        }
        element.tagName(DIV);

        element.children().forEach(this::convertToHtml);

        return element;
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
            pagebreaks.get(index).before(createPageNumberFootnote(getLabel(pagebreaks.get(index - 1)), fileUuid));
        });

        footnotesSection.appendChild(createPageNumberFootnote(getLabel(pagebreaks.get(pagebreaks.size() - 1)), fileUuid));
    }

    private void convertPageEndsToPageStarts(final Element section, PagePointer pagePointer) {
        final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(section);

        pagebreakEnds.forEach(pagebreakEnd -> {
            pagePointer.setCurrentPage(getLabel(pagebreakEnd));
            if (pagePointer.hasNext()) {
                pagebreakEnd.after(createPagebreak(pagePointer.nextPage()));
            }
            pagebreakEnd.remove();
        });
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
                pagebreak.after(prepareHtmlFromTemplate(PAGE_NUMBER_REFERENCE_TEMPLATE, getLabel(pagebreak), fileUuid))
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

    @Data
    @RequiredArgsConstructor
    private static class PagePointer {
        private final Map<String, String> pageNumbers;
        private String currentPage = INITIAL_PAGE_LABEL;

        public String nextPage() {
            return pageNumbers.get(currentPage);
        }

        public boolean hasNext() {
            return pageNumbers.containsKey(currentPage);
        }
    }
}
