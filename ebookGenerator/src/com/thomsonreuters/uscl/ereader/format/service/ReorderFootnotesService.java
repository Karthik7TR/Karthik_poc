package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
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

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.util.FileUtils;
import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.PAGE_NUMBERS_MAP;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.LABEL;
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
    private static final String PAGE = "page";
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
    private static final String CO_FOOTNOTE_CLASS_PREFIX = "co_footnote_";
    private static final String CO_FOOTNOTE_REFERENCE = "co_footnoteReference";
    private static final String CO_FOOTNOTE_SECTION_ID = "#co_footnoteSection";
    private static final String CO_FOOTNOTE_SECTION_TITLE = "co_footnoteSectionTitle";
    private static final String CO_FOOTNOTE_NUMBER_OR_LARGE_SELECTOR = "[class~=(co_footnoteNumber|co_footnoteNumberLarge)]";
    private static final String DIV_CO_FOOTNOTE_BODY = "div.co_footnoteBody";
    private static final String CO_DIVIDER = "co_divider";
    private static final String CO_COPYRIGHT = "co_copyright";
    private static final String CO_END_OF_DOCUMENT = "co_endOfDocument";
    private static final String CO_PAGE_NUMBER = "co_page_number";
    private static final String CO_PARAGRAPH = "co_paragraph";
    private static final String CO_PARAGRAPH_TEXT = "co_paragraphText";
    private static final String SECTION_LABEL_CLASS = "section-label";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String INITIAL_PAGE_LABEL = "extractPageNumbersInitialPageLabel";
    private static final String PARA = "para";
    private static final String PARATEXT = "paratext";
    private static final String BOP = "bop";
    private static final String BOS = "bos";
    private static final String EOP = "eop";
    private static final String EOS = "eos";
    private static final String SECTION_LABEL = "[Section %s]";
    private static final String DOT = ".";
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
    private static final String BEGIN_QUOTE = "begin.quote";
    private static final String END_QUOTE = "end.quote";

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private LinksResolverService linksResolverService;

    private List<File> orderedDocuments(final File gatherToc, final File srcDir) {
        final Map<String, File> nameToSrcFile = getNameFileMap(srcDir);
        return orderedDocumentIds(gatherToc).stream().map(nameToSrcFile::get).collect(Collectors.toList());
    }

    private List<String> orderedDocumentIds(final File gatherToc) {
        final Document toc = jsoup.loadDocument(gatherToc);
        return toc.select(DOCUMENT_GUID).eachText();
    }

    public void reorderFootnotes(final File gatherToc, final File srcGatherDir, final File srcDir, final File destDir,
                                 final BookStep step) {
        final Map<String, File> nameToXmlFile = getNameFileMap(srcGatherDir);

        List<File> srcFilesOrdered = orderedDocuments(gatherToc, srcDir);
        String firstFileName = getFirstFileName(srcFilesOrdered);
        String lastFileName = getLastFileName(srcFilesOrdered);
        BidiMap<String, String> pageNumbers = extractPageNumbers(srcFilesOrdered);
        step.setJobExecutionProperty(PAGE_NUMBERS_MAP, pageNumbers);

        final PagePointer mainPage = new PagePointer(pageNumbers);
        final PagePointer footnotePage = new PagePointer(pageNumbers);
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument = new PagebreakToMoveToNextDocument();

        srcFilesOrdered.forEach(file ->
            processDocument(destDir, nameToXmlFile, firstFileName, lastFileName, mainPage, footnotePage,
                    pagebreakToMoveToNextDocument, file, step)
        );
        processAuxiliaryFiles(srcDir, srcFilesOrdered, destDir, step);
    }

    private String getFirstFileName(final List<File> srcFilesOrdered) {
        return getFileName(srcFilesOrdered, 0);
    }

    private String getLastFileName(final List<File> srcFilesOrdered) {
        return getFileName(srcFilesOrdered, srcFilesOrdered.size() - 1);
    }

    private String getFileName(final List<File> srcFilesOrdered, final int index) {
        File file = srcFilesOrdered.get(index);
        return file != null ? file.getName() : null;
    }

    private boolean fileNameEquals(final String fileName, final File file) {
        return StringUtils.isNotEmpty(fileName) && fileName.equals(file.getName());
    }

    private void processDocument(final File destDir, final Map<String, File> nameToXmlFile,
        final String firstFileName, final String lastFileName, final PagePointer mainPage, final PagePointer footnotePage,
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument, final File file, final BookStep step) {
        final Document doc = jsoup.loadDocument(file);
        final String fileUuid = FilenameUtils.removeExtension(file.getName());
        final Element mainSection = doc.selectFirst(SECTION);
        final Optional<Element> footnotesBlock = Optional.ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID));
        boolean isFirstFile = fileNameEquals(firstFileName, file);
        boolean isLastFile = fileNameEquals(lastFileName, file);
        mainPage.nextDocument();
        footnotePage.nextDocument();

        footnotesBlock.ifPresent(Element::remove);

        final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(mainSection);
        if (footnotesBlock.isPresent() || pagebreaksFromMain.size() > 0 || isFirstFile) {
            convertFootnoteReferencesInMainSection(mainSection);
            convertFootnotes(doc, footnotesBlock);

            final Element footnotesSectionToAppend = constructFootnotesSection(nameToXmlFile, footnotesBlock, doc, fileUuid, footnotePage);
            linksResolverService.transformCiteQueries(footnotesSectionToAppend, fileUuid, step);
            fixPagebreaks(mainSection, footnotesSectionToAppend, isLastFile);
            movePagebreakOutOfFootnotes(footnotesSectionToAppend);
            addPageLabels(footnotesSectionToAppend);
            convertPageEndsToPageStarts(mainSection, mainPage, isFirstFile);
            convertPageEndsToPageStarts(footnotesSectionToAppend, footnotePage, isFirstFile);
            setPageAttrInReferencesInMainSectionAndFootnotes(mainSection, footnotesSectionToAppend, mainPage, footnotePage);
            movePagebreakToNextDocument(pagebreakToMoveToNextDocument, mainSection, footnotesSectionToAppend);
            mainSection.after(footnotesSectionToAppend);
        }

        doc.getElementsByClass(CO_DIVIDER).remove();
        doc.getElementsByClass(CO_COPYRIGHT).remove();
        remove(doc.getElementById(CO_END_OF_DOCUMENT));

        jsoup.saveDocument(destDir, file.getName(), doc);
    }

    private void processAuxiliaryFiles(final File srcDir, final List<File> processedFiles, final File destDir, final BookStep step) {
        List<File> auxiliaryFiles = getAuxiliaryFiles(srcDir, processedFiles);
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument = new PagebreakToMoveToNextDocument();
        auxiliaryFiles.forEach(file -> {
            if (EXCLUDED_FROM_PROCESSING.contains(file.getName())) {
                FileUtils.copyFileToDirectory(file, destDir);
            } else {
                Map<String, String> pageNumbers = extractPageNumbers(Collections.singletonList(file));
                final PagePointer mainPage = new PagePointer(pageNumbers);
                final PagePointer footnotePage = new PagePointer(pageNumbers);
                processDocument(destDir, Collections.emptyMap(), null, null, mainPage, footnotePage,
                        pagebreakToMoveToNextDocument, file, step);
            }
        });
    }

    private List<File> getAuxiliaryFiles(final File srcDir, final List<File> processedFiles) {
        Set<File> processed = new HashSet<>(processedFiles);
        return Arrays.stream(Objects.requireNonNull(srcDir.listFiles()))
                .filter(file -> !processed.contains(file))
                .collect(Collectors.toList());
    }

    private BidiMap<String, String> extractPageNumbers(final List<File> srcFilesOrdered) {
        BidiMap<String, String> pageNumbers = new DualHashBidiMap<>();
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
            element.select(CO_FOOTNOTE_NUMBER_OR_LARGE_SELECTOR).stream()
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
        return footnote.selectFirst(CO_FOOTNOTE_NUMBER_OR_LARGE_SELECTOR)
                       .selectFirst(A_TAG)
                       .attr(NAME)
                       .replaceFirst(CO_FOOTNOTE_CLASS_PREFIX, "");
    }

    private void convertFootnote(final Element footnote, final Document doc) {
        footnote.addClass(TR_FOOTNOTE);

        final Element footnoteNumber = footnote.selectFirst(CO_FOOTNOTE_NUMBER_OR_LARGE_SELECTOR);
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
            case BEGIN_QUOTE:
            case END_QUOTE:
                remove(element);
                return new TextNode(StringUtils.EMPTY);
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

    private void fixPagebreaks(final Element mainSection, final Element footnotesSectionToAppend, final boolean isLastFile) {
        removeExtraPagebreaksFromFootnotesSection(mainSection, footnotesSectionToAppend);
        addMissingPageLabelsToFootnotesSection(mainSection, footnotesSectionToAppend);
        fixLastDocumentPagebreaks(mainSection, footnotesSectionToAppend, isLastFile);
    }

    private void removeExtraPagebreaksFromFootnotesSection(final Element mainSection, final Element footnotesSection) {
        List<XmlDeclaration> mainSectionPagebreaks = getProviewPagebreaks(mainSection);
        getProviewPagebreaks(footnotesSection).stream()
                .filter(pagebreak -> !isPagebreakLabelInSection(mainSectionPagebreaks, getLabel(pagebreak)))
                .forEach(Node::remove);
    }

    private void fixLastDocumentPagebreaks(final Element mainSection, final Element footnotesSection, final boolean isLastFile) {
        if (isLastFile) {
            movePagebreakToTheEndOfDocument(mainSection);
            movePagebreakToTheEndOfDocument(footnotesSection);
        }
    }

    private void movePagebreakToTheEndOfDocument(final Element section) {
        List<XmlDeclaration> pagebreaks = getProviewPagebreaks(section);
        if (CollectionUtils.isNotEmpty(pagebreaks)) {
            XmlDeclaration lastPagebreak = pagebreaks.get(pagebreaks.size() - 1);
            lastPagebreak.remove();
            section.appendChild(lastPagebreak);
        }
    }

    private void addPageLabels(final Element footnotesSection) {
        appendPagenumbersToFootnotesSection(footnotesSection);
    }

    private void appendPagenumbersToFootnotesSection(final Element footnotesSection) {
        getProviewPagebreaks(footnotesSection).forEach(pagebreak -> {
            Element pageNumber = createPageNumber(getLabel(pagebreak));
            pagebreak.before(pageNumber);
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

    private void setPageAttrInReferencesInMainSectionAndFootnotes(final Element mainSection, final Element footnotesSectionToAppend,
        final PagePointer mainPage, final PagePointer footnotePage) {
        mainPage.setCurrentPage(mainPage.getInitialPageInCurrentDocument());
        footnotePage.setCurrentPage(footnotePage.getInitialPageInCurrentDocument());
        setPageAttrInReferences(mainSection, mainPage);
        setPageAttrInReferences(footnotesSectionToAppend, footnotePage);
    }

    private void setPageAttrInReferences(final Element section, final PagePointer pagePointer) {
        for (Node node : getFootnoteReferencesAndPagebreaks(section)) {
            if (node instanceof XmlDeclaration) {
                pagePointer.setCurrentPage(PageNumberUtil.getLabel(node));
            } else {
                node.attr(PAGE, pagePointer.getCurrentPage());
            }
        }
    }

    private List<Node> getFootnoteReferencesAndPagebreaks(final Element section) {
        return jsoup.selectNodes(section, node ->
                (node instanceof XmlDeclaration && PB.equals(((XmlDeclaration) node).name()))
                        || (node instanceof Element && A_TAG.equals(node.nodeName()) && ((Element) node).hasClass(TR_FTN)));
    }

    private void movePagebreakOutOfFootnotes(final Element footnoteSection) {
        protectFootnoteBodyForPopupBox(footnoteSection);
        final List<XmlDeclaration> pagebreakEnds = getProviewPagebreaks(footnoteSection);
        pagebreakEnds.forEach(this::movePagebreakOutOfFootnote);
    }

    private void movePagebreakToNextDocument(final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument,
        final Element mainSection, final Element footnoteSection) {
        prependPagebreakFromPreviousDocument(mainSection, footnoteSection, pagebreakToMoveToNextDocument);
        pagebreakToMoveToNextDocument.clear();
        saveLastPagebreakAndTrailingFootnoteNodesToPrependToNextDocument(mainSection, footnoteSection, pagebreakToMoveToNextDocument);
        pagebreakToMoveToNextDocument.removeElementsIfAvailable();
    }

    private void prependPagebreakFromPreviousDocument(final Element mainSection, final Element footnoteSection,
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument) {
        if (pagebreakToMoveToNextDocument.isAvailable()) {
            prependMainSectionPagebreakFromPreviousDocument(mainSection, pagebreakToMoveToNextDocument);
            prependFootnotesFromPreviousDocument(footnoteSection, pagebreakToMoveToNextDocument);
        }
    }

    private void prependMainSectionPagebreakFromPreviousDocument(final Element mainSection,
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument) {
        Node pagebreakFromMainSection = pagebreakToMoveToNextDocument.getPagebreakFromMainSection();
        mainSection.prependChild(pagebreakFromMainSection);
    }

    private void prependFootnotesFromPreviousDocument(final Element footnoteSection,
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument) {
        List<Node> pagebreakSiblingsFromFootnoteSection = pagebreakToMoveToNextDocument.getPagebreakSiblingsInFootnotesSection();
        Node pagebreakFromFootnoteSection = pagebreakToMoveToNextDocument.getPagebreakFromFootnotesSection();

        if (pagebreakSiblingsFromFootnoteSection != null) {
            ReverseListIterator<Node> it = new ReverseListIterator<>(pagebreakSiblingsFromFootnoteSection);
            while (it.hasNext()) {
                footnoteSection.prependChild(it.next());
            }
        }

        footnoteSection.prependChild(pagebreakFromFootnoteSection);
    }

    private void saveLastPagebreakAndTrailingFootnoteNodesToPrependToNextDocument(final Element mainSection, final Element footnoteSection,
        PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument) {
        savePagebreakAfterText(mainSection.childNodes(), pagebreakToMoveToNextDocument);
        Node pagebreakFromMainSection = pagebreakToMoveToNextDocument.getPagebreakFromMainSection();
        if (pagebreakFromMainSection != null) {
            String pageNumber = PageNumberUtil.getLabel(pagebreakFromMainSection);
            saveFootnoteNodesAfterText(pagebreakToMoveToNextDocument,
                    footnoteSection, pageNumber);
        }
    }

    private void savePagebreakAfterText(final List<Node> children,
        final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument) {
        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            if (child instanceof XmlDeclaration) {
                XmlDeclaration xmlDeclaration = (XmlDeclaration) child;
                if (PageNumberUtil.PB.equals(xmlDeclaration.name())) {
                    pagebreakToMoveToNextDocument.setPagebreakFromMainSection(xmlDeclaration);
                    break;
                }
            } else {
                if (isTextNodeAndNotEmpty(child)) {
                    break;
                }
                if (isElementWithPossibleText(child)) {
                    savePagebreakAfterText(child.childNodes(), pagebreakToMoveToNextDocument);
                    break;
                }
            }
        }
    }

    private boolean isTextNodeAndNotEmpty(final Node child) {
        return child instanceof TextNode && StringUtils.isNotBlank(((TextNode) child).text());
    }

    private boolean isElementWithPossibleText(final Node child) {
        return child instanceof Element && !((Element) child).hasClass(CO_COPYRIGHT)
                && !CO_END_OF_DOCUMENT.equals(child.attr(ID));
    }

    private void saveFootnoteNodesAfterText(final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument,
        final Element footnoteSection, final String pageNumber) {
        List<XmlDeclaration> pagebreaks = getProviewPagebreaks(footnoteSection);
        Collections.reverse(pagebreaks);
        for (XmlDeclaration pagebreak : pagebreaks) {
            if (pageNumber.equals(PageNumberUtil.getLabel(pagebreak))) {
                saveFootnoteAfterText(pagebreakToMoveToNextDocument, pagebreak);
                break;
            }
        }
    }

    private void saveFootnoteAfterText(final PagebreakToMoveToNextDocument pagebreakToMoveToNextDocument,
                                       final XmlDeclaration pagebreak) {
        List<Node> nextSiblings = getNextSiblings(pagebreak);
        if (hasNoFootnotesAfterPagebreak(nextSiblings)) {
            pagebreakToMoveToNextDocument.setPagebreakFromFootnotesSection(pagebreak);
            pagebreakToMoveToNextDocument.setPagebreakSiblingsInFootnotesSection(nextSiblings);
        }
    }

    private List<Node> getNextSiblings(final XmlDeclaration pagebreak) {
        List<Node> siblings = new ArrayList<>();
        Node sibling = pagebreak.nextSibling();
        while (sibling != null) {
            siblings.add(sibling);
            sibling = sibling.nextSibling();
        }
        return siblings;
    }

    private boolean hasNoFootnotesAfterPagebreak(final List<Node> nextSiblings) {
        return nextSiblings.stream().noneMatch(node -> node instanceof Element && isFootnote((Element)node));
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

    private Element createPageNumber(final String label) {
        return new Element(DIV).addClass(CO_PAGE_NUMBER)
                .text(label);
    }

    private void addMissingPageLabelsToFootnotesSection(final Element mainSection, final Element footnotesSection) {
        List<XmlDeclaration> mainSectionPagebreaks = getProviewPagebreaks(mainSection);
        List<XmlDeclaration> footnotesPagebreaks = getProviewPagebreaks(footnotesSection);
        List<XmlDeclaration> missingPagebreaks = mainSectionPagebreaks.stream()
                .filter(mainSectionPagebreak -> !isPagebreakLabelInSection(footnotesPagebreaks, getLabel(mainSectionPagebreak)))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(missingPagebreaks)) {
            addPageLabelsToSection(missingPagebreaks, mainSectionPagebreaks.get(0), footnotesSection);
        }
    }

    private boolean isPagebreakLabelInSection(final List<XmlDeclaration> pagebreaks, final String pagebreakLabel) {
        return pagebreaks.stream()
                .anyMatch(pagebreak -> pagebreakLabel.equals(getLabel(pagebreak)));
    }

    private void addPageLabelsToSection(final List<XmlDeclaration> missingPagebreaks, final XmlDeclaration firstPagebreakFromMain,
        final Element section) {
        XmlDeclaration firstMissingPagebreak = missingPagebreaks.get(0);
        boolean shouldPrependMissingPagebreaks = shouldPrependMissingPagebreaks(firstMissingPagebreak, firstPagebreakFromMain);
        if (shouldPrependMissingPagebreaks) {
            Collections.reverse(missingPagebreaks);
        }
        for (XmlDeclaration pagebreak : missingPagebreaks) {
            addPageLabelToSection(pagebreak, section, shouldPrependMissingPagebreaks);
        }
    }

    private boolean shouldPrependMissingPagebreaks(final XmlDeclaration firstMissingPagebreak, final XmlDeclaration firstPagebreakFromMain) {
        return firstPagebreakFromMain.attr(LABEL).equals(firstMissingPagebreak.attr(LABEL));
    }

    private void addPageLabelToSection(final XmlDeclaration pagebreak, final Element section, final boolean shouldPrependMissingPagebreaks) {
        if (shouldPrependMissingPagebreaks) {
            section.prependChild(pagebreak.clone());
        } else {
            section.appendChild(pagebreak.clone());
        }
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
    private static class PagebreakToMoveToNextDocument {
        private Node pagebreakFromMainSection;
        private Node pagebreakFromFootnotesSection;
        private List<Node> pagebreakSiblingsInFootnotesSection;

        public void removeElementsIfAvailable() {
            if (isAvailable()) {
                remove();
            } else {
                clear();
            }
        }

        private boolean isAvailable() {
            return pagebreakFromMainSection != null && pagebreakFromFootnotesSection != null;
        }

        private void remove() {
            pagebreakFromMainSection.remove();
            pagebreakFromFootnotesSection.remove();
            pagebreakSiblingsInFootnotesSection.forEach(Node::remove);
        }

        public void clear() {
            pagebreakFromMainSection = null;
            pagebreakFromFootnotesSection = null;
            pagebreakSiblingsInFootnotesSection = null;
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
