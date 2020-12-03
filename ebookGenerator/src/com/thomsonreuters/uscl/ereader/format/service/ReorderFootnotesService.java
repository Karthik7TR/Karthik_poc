package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PB;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.convertToProviewPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.getLabel;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.parents;

@Component
public class ReorderFootnotesService {
    private static final String PROVIEW_FOOTNOTE_CLASS = "er_rp_search_volume_content_data";
    private static final String ID = "id";
    private static final String DIV = "div";
    private static final String FOOTNOTE = "footnote";
    private static final String FOOTNOTE_BODY = "footnote_body";
    private static final String FOOTNOTE_BODY_POPUP_BOX = "footnote_body_box";
    private static final String FOOTNOTE_BODY_BOTTOM = "footnote_body_bottom";
    private static final String FOOTNOTE_BODY_TAG = "footnote.body";
    private static final String SECTION = "section";
    private static final String A_TAG = "a";
    private static final String A_HREF = "a[href]";
    private static final String HREF = "href";
    private static final String NAME = "name";
    private static final String SUP = "sup";
    private static final String AUTHOR_FOOTNOTES = "author.footnotes";
    private static final String FOOTNOTE_BLOCK = "footnote.block";
    private static final String SECTION_FRONT = "section.front";
    private static final String LABEL_DESIGNATOR = "label.designator";
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
    private static final String SECTION_LABEL_CLASS = "section-label";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String PAGE_NUMBER_REFERENCE_TEMPLATE = "<a ftnname=\"page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" name=\"page_number_fnRef_PAGE_NUMBER_FOOTNOTE_ID\" href=\"#page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" class=\"tr_ftn\" />";
    private static final String PAGE_NUMBER_FOOTNOTE_ID_PLACEHOLDER = "PAGE_NUMBER_FOOTNOTE_ID";
    private static final String PAGE_NUMBER_PLACEHOLDER = "PAGE_NUMBER";
    private static final String INITIAL_PAGE_LABEL = "extractPageNumbersInitialPageLabel";
    private static final String PARA = "para";
    private static final String PARATEXT = "paratext";
    private static final String BOP = "bop";
    private static final String BOS = "bos";
    private static final String EOP = "eop";
    private static final String EOS = "eos";
    private static final String SECTION_LABEL = "[Section %s]";
    private static final String DOT = ".";
    private static final String UNDERSCORE = "_";
    private static final String TR_FOOTNOTE_CLASS_REG = ".*\\btr_footnote\\b.*";
    private static final String FOOTNOTE_IN_CLASS_REG = ".*footnote.*";
    private static final String SECTION_LABEL_REG = "\\[Section \\d+(\\s*|.)(\\s*|\\d+)\\]";
    private static final String INLINE_TOC = "inlineToc.html";
    private static final String INLINE_INDEX = "inlineIndex.html";
    private static final List<String> EXCLUDED_FROM_PROCESSING = Arrays.asList(INLINE_TOC, INLINE_INDEX);
    private static final String ITAL = "ital";
    private static final String CO_ITALIC = "co_italic";
    private static final String CSC = "csc";
    private static final String CO_SMALL_CAPS = "co_smallCaps";
    private static final String CO_INLINE = "co_inline";

    @Autowired
    private JsoupService jsoup;

    @Value("${page.number.footnote.template}")
    private File pageNumberFootnoteTemplateFile;

    @Autowired
    private CiteQueryService citeQueryService;

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
        String firstFileName = getFirstFileName(srcFilesOrdered);
        Map<String, String> pageNumbers = extractPageNumbers(srcFilesOrdered);

        final PagePointer mainPage = new PagePointer(pageNumbers);
        final PagePointer footnotePage = new PagePointer(pageNumbers);

        srcFilesOrdered.forEach(file ->
            processDocument(destDir, nameToXmlFile, firstFileName, mainPage, footnotePage, file)
        );
        processAuxiliaryFiles(srcDir, srcFilesOrdered, destDir);
    }

    private String getFirstFileName(final List<File> srcFilesOrdered) {
        File firstFile = srcFilesOrdered.get(0);
        return firstFile != null ? firstFile.getName() : null;
    }

    private void processDocument(final File destDir, final Map<String, File> nameToXmlFile, final String firstFileName,
    final PagePointer mainPage, final PagePointer footnotePage, final File file) {
        final Document doc = jsoup.loadDocument(file);
        final String fileUuid = FilenameUtils.removeExtension(file.getName());
        final Element mainSection = doc.selectFirst(SECTION);
        final Optional<Element> footnotesBlock = Optional.ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID));
        boolean isFirstFile = StringUtils.isNotEmpty(firstFileName) && firstFileName.equals(file.getName());
        mainPage.nextDocument();
        footnotePage.nextDocument();

        footnotesBlock.ifPresent(Element::remove);

        final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(mainSection);
        if (footnotesBlock.isPresent() || pagebreaksFromMain.size() > 0 || isFirstFile) {
            convertFootnoteReferencesInMainSection(mainSection);
            convertFootnotes(doc, footnotesBlock);

            final Element footnotesSectionToAppend = constructFootnotesSection(nameToXmlFile, footnotesBlock, doc, fileUuid, footnotePage);
            citeQueryService.transformCiteQueries(footnotesSectionToAppend, fileUuid);
            movePagebreakOutOfFootnotes(footnotesSectionToAppend);
            addPageLabels(mainSection, footnotesSectionToAppend, fileUuid);
            convertPageEndsToPageStarts(mainSection, mainPage, isFirstFile);
            convertPageEndsToPageStarts(footnotesSectionToAppend, footnotePage, isFirstFile);
            mainSection.after(footnotesSectionToAppend);
        }

        doc.getElementsByClass(CO_DIVIDER).remove();
        doc.getElementsByClass(CO_COPYRIGHT).remove();
        remove(doc.getElementById(CO_END_OF_DOCUMENT));

        jsoup.saveDocument(destDir, file.getName(), doc);
    }

    private void processAuxiliaryFiles(final File srcDir, final List<File> processedFiles, final File destDir) {
        List<File> auxiliaryFiles = getAuxiliaryFiles(srcDir, processedFiles);
        auxiliaryFiles.forEach(file -> {
            if (EXCLUDED_FROM_PROCESSING.contains(file.getName())) {
                com.thomsonreuters.uscl.ereader.core.book.util.FileUtils.copyFileToDirectory(file, destDir);
            } else {
                Map<String, String> pageNumbers = extractPageNumbers(Collections.singletonList(file));
                final PagePointer mainPage = new PagePointer(pageNumbers);
                final PagePointer footnotePage = new PagePointer(pageNumbers);
                processDocument(destDir, Collections.emptyMap(), null, mainPage, footnotePage, file);
            }
        });
    }

    private List<File> getAuxiliaryFiles(final File srcDir, final List<File> processedFiles) {
        Set<File> processed = new HashSet<>(processedFiles);
        return Arrays.stream(Objects.requireNonNull(srcDir.listFiles()))
                .filter(file -> !processed.contains(file))
                .collect(Collectors.toList());
    }

    private Map<String, String> extractPageNumbers(final List<File> srcFilesOrdered) {
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
    private Map<String, File> getNameFileMap(final File dir) {
        return Stream.of(Objects.requireNonNull(dir.listFiles()))
                .collect(Collectors.toMap(file -> FilenameUtils.removeExtension(file.getName()), Function.identity()));
    }

    private void convertFootnotes(final Document doc, final Optional<Element> footnotesSection) {
        footnotesSection.ifPresent(element ->
            element.getElementsByClass(CO_FOOTNOTE_NUMBER).stream()
            .map(Element::parent)
            .forEach(footnote -> convertFootnote(footnote, doc))
        );
    }

    private String getSectionLabel(final Optional<Document> xmlDoc) {
        return xmlDoc.flatMap(document -> Optional.ofNullable(document.getElementsByTag(SECTION_FRONT).first())
                .map(sectionFront -> sectionFront.getElementsByTag(LABEL_DESIGNATOR).first())
                .map(Element::html))
                .orElse(null);
    }

    private Map<String, Element> getFootnotesMap(final Optional<Element> footnotesSection) {
        return footnotesSection.map(element ->
            element.getElementsByClass(TR_FOOTNOTE).stream()
            .collect(Collectors.toMap(this::getFootnoteId, Function.identity()))
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
        String sectionLabel = getSectionLabel(xmlDoc);
        final Optional<Element> footnotesTemplate = xmlDoc.map(doc -> doc.getElementsByTag(FOOTNOTE_BLOCK).first());

        if (footnotesTemplate.isPresent()) {
            return constructBasedOnTemplate(getFootnotesMap(footnotesBlock), footnotesTemplate.get(), xmlDoc.get(), pagePointer, sectionLabel);
        } else {
            return constructBasedOnBlock(footnotesBlock, document, pagePointer, sectionLabel);
        }
    }

    private Element constructBasedOnTemplate(
        final Map<String, Element> idToFootnote,
        final Element footnotesTemplate,
        final Document xmlDoc,
        final PagePointer pagePointer,
        final String sectionLabel) {
        convertPagebreaksToProviewPbs(footnotesTemplate);
        convertTopToFootnotesSection(footnotesTemplate);
        addSectionLabel(footnotesTemplate, sectionLabel);

        fillTemplateWithFootnotes(idToFootnote, footnotesTemplate);
        addAuthorFootnotes(idToFootnote, footnotesTemplate, xmlDoc);

        return footnotesTemplate;
    }

    private Element constructBasedOnBlock(final Optional<Element> block, final Document doc, final PagePointer pagePointer, final String sectionLabel) {
        final Element footnotesBlock = block.orElse(new Element(DIV));
        convertTopToFootnotesSection(footnotesBlock);
        addSectionLabel(footnotesBlock, sectionLabel);

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

    private Node convertToHtml(final Element element) {
        boolean converted = true;
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
            case ITAL:
                element.addClass(CO_ITALIC);
                element.addClass(CO_INLINE);
                break;
            case CSC:
                element.addClass(CO_SMALL_CAPS);
                element.addClass(CO_INLINE);
                break;
            case BOP:
            case BOS:
            case EOP:
            case EOS:
                break;
            default:
                converted = false;
        }
        if (converted) {
            element.tagName(DIV);
        }

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

    private void addSectionLabel(final Element footnotesTemplate, String sectionLabel) {
        if (shouldSectionLabelBeAdded(footnotesTemplate, sectionLabel)) {
            sectionLabel = formatSectionLabel(sectionLabel);
            String html = getFootnotesSectionWithSectionLabel(footnotesTemplate, sectionLabel);
            footnotesTemplate.html(html);
        }
    }

    private boolean shouldSectionLabelBeAdded(final Element footnotesTemplate, final String sectionLabel) {
        return footnotesTemplate.children().size() > 0 && StringUtils.isNotEmpty(sectionLabel);
    }

    private String getFootnotesSectionWithSectionLabel(final Element footnotesTemplate, final String sectionLabel) {
        String html = footnotesTemplate.html();
        if (Pattern.compile(SECTION_LABEL_REG).matcher(html).find()) {
            return html.replaceFirst(SECTION_LABEL_REG, sectionLabel);
        }
        return sectionLabel + html;
    }

    private String formatSectionLabel(String sectionLabel) {
        sectionLabel = StringUtils.removeEnd(sectionLabel, DOT);
        sectionLabel = String.format(SECTION_LABEL, sectionLabel);
        return new Element(DIV).text(sectionLabel)
                .addClass(SECTION_LABEL_CLASS)
                .toString();
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
        element.empty();
        element.appendChild(div);
    }

    private void convertFootnoteReference(final Element innerRef, final boolean addHref) {
        final Element ref = innerRef.parent();
        removePagebreaksFromFootnoteReference(innerRef, ref.parent());

        ref.addClass(TR_FTN);
        final String refName = extractReferenceName(innerRef.attr(HREF));
        ref.attr(FTNNAME, refName);

        ref.attr(HREF, addHref ? "#" + refName : "");
        innerRef.remove();
        ref.append(innerRef.text());
    }

    private void removePagebreaksFromFootnoteReference(final Element innerReference, final Element referenceParent) {
        List<XmlDeclaration> pagebreaks = getProviewPagebreaks(innerReference);
        Collections.reverse(pagebreaks);
        pagebreaks.forEach(pagebreak -> {
            referenceParent.after(createPagebreak(getLabel(pagebreak)));
            pagebreak.remove();
        });
    }

    private String extractReferenceName(final String reference) {
        return reference.substring(reference.lastIndexOf("/") + 1);
    }

    private void addPageLabels(final Element mainSection, final Element footnotesSection, final String fileUuid) {
        addPageReferencesInMainSection(mainSection, fileUuid);
        appendPagenumbersToFootnotesSection(footnotesSection, fileUuid, mainSection);
        addMissingPageLabelsToFootnotesSection(mainSection, footnotesSection, fileUuid);
    }

    private void appendPagenumbersToFootnotesSection(final Element footnotesSection, final String fileUuid, final Element mainSection) {
        getProviewPagebreaks(footnotesSection).forEach(pagebreak -> {
            Element pageNumberFootnote = createPageNumberFootnote(pagebreak, fileUuid, mainSection);
            if (pageNumberFootnote != null) {
                pagebreak.before(pageNumberFootnote);
            } else {
                pagebreak.remove();
            }
        });
    }

    private void convertPageEndsToPageStarts(final Element section, final PagePointer pagePointer, final boolean isFirstFile) {
        final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(section);

        if (isFirstFile) {
            section.prependChild(createPagebreak(pagePointer.firstPage()));
        }

        pagebreakEnds.forEach(pagebreakEnd -> {
            pagePointer.setCurrentPage(getLabel(pagebreakEnd));
            if (pagePointer.hasNextPage()) {
                pagebreakEnd.after(createPagebreak(pagePointer.nextPage()));
            }
            pagebreakEnd.remove();
        });
    }

    private void movePagebreakOutOfFootnotes(final Element footnoteSection) {
        protectFootnoteBodyForPopupBox(footnoteSection);
        final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(footnoteSection);
        pagebreakEnds.forEach(this::movePagebreakOutOfFootnote);
    }

    private void protectFootnoteBodyForPopupBox(final Element footnoteSection) {
        footnoteSection.getElementsByClass(FOOTNOTE_BODY).stream()
                .filter(footnoteBody -> CollectionUtils.isNotEmpty(getProviewPagebreaks(footnoteBody)))
                .forEach(footnoteBody -> {
                    Element popupFootnoteBody = footnoteBody.clone();
                    cleanupPopupFootnoteBody(popupFootnoteBody);

                    popupFootnoteBody.addClass(FOOTNOTE_BODY_POPUP_BOX);
                    footnoteBody.addClass(FOOTNOTE_BODY_BOTTOM);

                    footnoteBody.before(popupFootnoteBody);
                });
    }

    private void cleanupPopupFootnoteBody(final Element popupFootnoteBody) {
        getProviewPagebreaks(popupFootnoteBody).forEach(Node::remove);
        popupFootnoteBody.getElementsByTag(A_TAG).forEach(this::cleanUpAnchor);
    }

    private Element cleanUpAnchor(final Element anchor) {
        if (A_TAG.equalsIgnoreCase(anchor.tagName())) {
            anchor.removeAttr(ID);
        }
        return anchor;
    }

    private void movePagebreakOutOfFootnote(final XmlDeclaration pagebreak) {
        if (isInsideFootnote(pagebreak)) {
            Element previousLevel;
            do {
                previousLevel = movePagebreakOnUpperLevel(pagebreak);
            } while (!isFootnote(previousLevel));
        }
    }

    private Element movePagebreakOnUpperLevel(final XmlDeclaration pagebreak) {
        Element parent = (Element) pagebreak.parent();

        List<Node> childrenAfterPagebreak = getChildrenAfterPagebreak(pagebreak, parent);

        pagebreak.remove();
        childrenAfterPagebreak.forEach(Node::remove);

        Element after = generateFootnoteContentTag(parent);

        parent.after(pagebreak);
        pagebreak.after(after);

        childrenAfterPagebreak.forEach(after::appendChild);
        return parent;
    }

    private Element generateFootnoteContentTag(final Element base) {
        if (base.tagName().matches(FOOTNOTE + "|" + FOOTNOTE_BODY_TAG) || base.className().matches(FOOTNOTE_IN_CLASS_REG)) {
            return new Element(DIV);
        }
        return cleanUpAnchor(base.shallowClone());
    }

    @NotNull
    private List<Node> getChildrenAfterPagebreak(final XmlDeclaration pagebreak, final Element parent) {
        List<Node> childrenAfterPagebreak = new ArrayList<>();

        boolean beforePagebreak = true;
        for (Node child : parent.childNodes()) {
            if (child.equals(pagebreak)) {
                beforePagebreak = false;
                continue;
            }
            if (!beforePagebreak) {
                childrenAfterPagebreak.add(child);
            }
        }
        return childrenAfterPagebreak;
    }

    private boolean isInsideFootnote(final XmlDeclaration pagebreak) {
        return parents(pagebreak).stream().anyMatch(this::isFootnote);
    }

    private boolean isFootnote(final Element element) {
        return FOOTNOTE.equals(element.tagName()) || element.className().matches(TR_FOOTNOTE_CLASS_REG);
    }

    private Element createPageNumberFootnote(final XmlDeclaration pagebreak, final String fileUuid, final Element mainSection) {
        Elements mainSectionReferences = mainSection.getElementsByClass(TR_FTN);
        boolean hasReference = isPagebreakReferenceInMainSection(mainSectionReferences, pagebreak);
        if (hasReference) {
            return Jsoup.parse(prepareHtmlFromTemplate(pageNumberFootnoteTemplate, getLabel(pagebreak), fileUuid))
                    .selectFirst(TR_FOOTNOTE_CLASS);
        }
        return null;
    }

    private String prepareHtmlFromTemplate(final String template, final String label, final String fileUuid) {
        return template.replaceAll(PAGE_NUMBER_FOOTNOTE_ID_PLACEHOLDER, fileUuid + UNDERSCORE + label)
                       .replaceAll(PAGE_NUMBER_PLACEHOLDER, label);
    }

    private void addPageReferencesInMainSection(final Element mainSection, final String fileUuid) {
        getProviewPagebreaks(mainSection)
            .forEach(pagebreak ->
                pagebreak.before(prepareHtmlFromTemplate(PAGE_NUMBER_REFERENCE_TEMPLATE, getLabel(pagebreak), fileUuid))
            );
    }

    private boolean isPagebreakReferenceInMainSection(final Elements mainSectionReferences, final XmlDeclaration pagebreak) {
        for (Element reference : mainSectionReferences) {
            String name = reference.attr(NAME);
            int indexOfUnderscoreBeforePageNumber = name.lastIndexOf(UNDERSCORE);
            String pageNumberFromRef = name.substring(indexOfUnderscoreBeforePageNumber + 1);
            if (pageNumberFromRef.equals(getLabel(pagebreak))) {
                return true;
            }
        }
        return false;
    }

    private void addMissingPageLabelsToFootnotesSection(final Element mainSection, final Element footnotesSection, final String fileUuid) {
        List<XmlDeclaration> mainSectionPagebreaks = getProviewPagebreaks(mainSection);
        List<XmlDeclaration> footnotesPagebreaks = getProviewPagebreaks(footnotesSection);
        List<XmlDeclaration> missingPagebreaks = mainSectionPagebreaks.stream()
                .filter(mainSectionPagebreak -> !isPagebreakLabelInFootnotesSection(footnotesPagebreaks, getLabel(mainSectionPagebreak)))
                .collect(Collectors.toList());
        addPageLabelsToSection(missingPagebreaks, footnotesSection, fileUuid);
    }

    private void addPageLabelsToSection(final List<XmlDeclaration> missingPagebreaks, final Element section, final String fileUuid) {
        for (XmlDeclaration pagebreak : missingPagebreaks) {
            String pageNumberReference = prepareHtmlFromTemplate(pageNumberFootnoteTemplate, getLabel(pagebreak), fileUuid);
            section.html(pageNumberReference + pagebreak.toString() + section.html());
        }
    }

    private boolean isPagebreakLabelInFootnotesSection(final List<XmlDeclaration> footnotesPagebreaks, final String pagebreakLabel) {
        return footnotesPagebreaks.stream()
                .anyMatch(footnotePagebreak -> pagebreakLabel.equals(getLabel(footnotePagebreak)));
    }

    private void convertPagebreaksToProviewPbs(final Element footnotesTemplate) {
        getPagebreaks(footnotesTemplate)
            .forEach(pagebreak -> pagebreak.replaceWith(convertToProviewPagebreak(pagebreak)));
    }

    private Optional<XmlDeclaration> getFirstPagebreak(final Element footnotesSection) {
        return getProviewPagebreaks(footnotesSection).stream().findFirst();
    }

    private Elements getPagebreaks(final Element element) {
        return element.getElementsByTag(PAGEBREAK);
    }

    private List<XmlDeclaration> getProviewPagebreaks(final Element element) {
        return jsoup.selectXmlProcessingInstructions(element, PB);
    }
    
    private void remove(final Element element) {
        if (Objects.nonNull(element)) {
            element.remove();
        }
    }

    @Data
    @RequiredArgsConstructor
    private static class PagePointer {
        private final Map<String, String> pageNumbers;
        private String currentPage = INITIAL_PAGE_LABEL;
        private String initialPageInCurrentDocument = INITIAL_PAGE_LABEL;

        public String nextPage() {
            return pageNumbers.get(currentPage);
        }

        public String firstPage() {
            return pageNumbers.get(INITIAL_PAGE_LABEL);
        }

        public boolean hasNextPage() {
            return pageNumbers.containsKey(currentPage);
        }

        public void nextDocument() {
            initialPageInCurrentDocument = currentPage;
        }

        public boolean isStartDocument() {
            return INITIAL_PAGE_LABEL.equals(initialPageInCurrentDocument);
        }
    }
}
