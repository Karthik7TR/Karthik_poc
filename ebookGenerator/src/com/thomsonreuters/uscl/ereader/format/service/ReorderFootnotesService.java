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

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
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
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ANCHOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.AUTHOR_FOOTNOTES;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.BEGIN_QUOTE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.BOP;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.BOS;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_FOOTNOTE_REFERENCE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_FOOTNOTE_SECTION_TITLE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_INLINE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_ITALIC;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_PAGE_NUMBER;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_PARAGRAPH;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_PARAGRAPH_TEXT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_SMALL_CAPS;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CSC;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.DIV;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.DIV_CO_FOOTNOTE_BODY;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.END_QUOTE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.EOP;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.EOS;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BLOCK;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BODY;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BODY_BOTTOM;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BODY_POPUP_BOX;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BODY_TAG;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FTNNAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HREF;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ITAL;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.NAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_COPYRIGHT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_DIVIDER;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_END_OF_DOCUMENT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CO_FOOTNOTE_CLASS_PREFIX;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.LABEL_DESIGNATOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.PAGE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.PARA;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.PARATEXT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.PROVIEW_FOOTNOTE_CLASS;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION_FRONT;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION_LABEL_CLASS;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SUP;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ID;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.TR_FOOTNOTE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.TR_FOOTNOTES;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.TR_FTN;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PAGEBREAK;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PB;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.addVolumeToPageNumber;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.convertToProviewPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.createPagebreak;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.getLabel;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.parents;
import static java.util.Optional.ofNullable;

@Component
public class ReorderFootnotesService {
    private static final String A_HREF = "a[href]";
    private static final String CO_FOOTNOTE_SECTION_ID = "#co_footnoteSection";
    private static final String CO_FOOTNOTE_NUMBER_OR_LARGE_SELECTOR = "[class~=(co_footnoteNumber|co_footnoteNumberLarge)]";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String INITIAL_PAGE_LABEL = "extractPageNumbersInitialPageLabel";
    private static final String SECTION_LABEL = "[Section %s]";
    private static final String DOT = ".";
    private static final String TR_FOOTNOTE_CLASS_REG = ".*\\btr_footnote\\b.*";
    private static final String FOOTNOTE_IN_CLASS_REG = ".*footnote.*";
    private static final Pattern SECTION_LABEL_REG = Pattern.compile("\\[Section \\d+(\\s*|.)(\\s*|\\d+)\\]");
    private static final String INLINE_TOC = "inlineToc.html";
    private static final String INLINE_INDEX = "inlineIndex.html";
    private static final List<String> EXCLUDED_FROM_PROCESSING = Arrays.asList(INLINE_TOC, INLINE_INDEX);

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private LinksResolverService linksResolverService;

    @Autowired
    private DuplicatedPagebreaksResolver duplicatedPagebreaksResolver;

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
        final Optional<Element> footnotesBlock = ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID));
        boolean isFirstFile = fileNameEquals(firstFileName, file);
        boolean isLastFile = fileNameEquals(lastFileName, file);
        boolean pageVolumesSet = step.getJobExecutionPropertyBoolean(JobExecutionKey.PAGE_VOLUMES_SET);
        mainPage.nextDocument();
        footnotePage.nextDocument();

        footnotesBlock.ifPresent(Element::remove);

        final List<XmlDeclaration> pagebreaksFromMain = getProviewPagebreaks(mainSection);
        if (footnotesBlock.isPresent() || pagebreaksFromMain.size() > 0 || isFirstFile || pagebreakToMoveToNextDocument.isAvailable()) {
            convertFootnoteReferencesInMainSection(mainSection);
            convertFootnotes(doc, footnotesBlock);

            final Element footnotesSectionToAppend = constructFootnotesSection(nameToXmlFile, footnotesBlock, doc, fileUuid, footnotePage, pageVolumesSet);
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
            ofNullable(mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID)).ifPresent(Element::remove);
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
        return xmlDoc.flatMap(document -> ofNullable(getElementByTag(document, SECTION_FRONT))
                .map(sectionFront -> getElementByTag(sectionFront, LABEL_DESIGNATOR))
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
                       .selectFirst(ANCHOR)
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
        final PagePointer pagePointer,
        final boolean pageVolumesSet) {
        final Optional<File> xmlFile = ofNullable(nameToXmlFile.get(fileUuid));
        final Optional<Document> xmlDoc = xmlFile.map(file -> jsoup.loadDocument(file));
        String sectionLabel = getSectionLabel(xmlDoc);
        final Optional<Element> footnotesTemplate = xmlDoc.map(doc -> getElementByTag(doc, FOOTNOTE_BLOCK));

        return footnotesTemplate
                .map(element -> constructBasedOnTemplate(getFootnotesMap(footnotesBlock), element, xmlDoc.get(), pagePointer, sectionLabel, pageVolumesSet))
                .orElseGet(() -> constructBasedOnBlock(footnotesBlock, document, pagePointer, sectionLabel));
    }

    private Element constructBasedOnTemplate(
        final Map<String, Element> idToFootnote,
        final Element footnotesTemplate,
        final Document xmlDoc,
        final PagePointer pagePointer,
        final String sectionLabel,
        final boolean pageVolumesSet) {
        removeDuplicatedPagebreaks(footnotesTemplate);
        convertPagebreaksToProviewPbs(footnotesTemplate, pageVolumesSet);
        convertTopToFootnotesSection(footnotesTemplate);
        addSectionLabel(footnotesTemplate, sectionLabel);

        fillTemplateWithFootnotes(idToFootnote, footnotesTemplate);
        addAuthorFootnotes(idToFootnote, footnotesTemplate, xmlDoc);

        return footnotesTemplate;
    }

    private void removeDuplicatedPagebreaks(final Element footnotesXml) {
        duplicatedPagebreaksResolver.fixPagebreaksInFootnotes(footnotesXml);
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
        footnotesTemplate.getElementsByTag(FOOTNOTE).forEach(footnote -> {
                Element footnoteHtml = idToFootnote.get(footnote.attr(ID));
                if (footnoteHtml != null) {
                    processFootnoteBody(footnote, footnoteHtml);
                } else {
                    footnote.remove();
                }
            });
    }

    private void processFootnoteBody(final Element footnoteXml, final Element footnoteHtml) {
        Element bodyXml = getElementByTag(footnoteXml, FOOTNOTE_BODY_TAG);
        Element bodyHtml = getElementByClass(footnoteHtml, FOOTNOTE_BODY);
        bodyHtml.replaceWith(convertToHtml(bodyXml));
        footnoteXml.replaceWith(footnoteHtml);
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
        final Element authorFootnotes = getElementByTag(xmlDoc, AUTHOR_FOOTNOTES);
        if (authorFootnotes != null) {
            final Node placeholder = getAuthorFootnotePlaceholder(footnotesTemplate);

            authorFootnotes.getElementsByTag(FOOTNOTE).stream()
            .sorted(Collections.reverseOrder())
            .forEach(footnote -> placeholder.after(idToFootnote.get(footnote.attr(ID))));
        }
    }

    private Node getAuthorFootnotePlaceholder(final Element footnotesTemplate) {
        return ofNullable((Node) getElementByClass(footnotesTemplate, SECTION_LABEL_CLASS))
                    .orElseGet(() -> createSectionStartPointer(footnotesTemplate));
    }

    private Node createSectionStartPointer(final Element footnotesTemplate) {
        Node text = new TextNode(StringUtils.EMPTY);
        footnotesTemplate.prependChild(text);
        return text;
    }

    private void convertTopToFootnotesSection(final Element footnotesTemplate) {
        footnotesTemplate.tagName(SECTION);
        footnotesTemplate.addClass(TR_FOOTNOTES);
    }

    private void addSectionLabel(final Element footnotesTemplate, final String sectionLabel) {
        if (shouldSectionLabelBeAdded(footnotesTemplate, sectionLabel)) {
            addSectionLabel(footnotesTemplate, formatSectionLabel(sectionLabel));
        }
    }

    private void addSectionLabel(final Element footnotesTemplate, final Element sectionLabel) {
        TextNode sectionLabelNode = getSectionLabelNode(footnotesTemplate);
        if (sectionLabelNode != null) {
            sectionLabelNode.replaceWith(sectionLabel);
        } else {
            footnotesTemplate.prependChild(sectionLabel);
        }
    }

    private boolean shouldSectionLabelBeAdded(final Element footnotesTemplate, final String sectionLabel) {
        return StringUtils.isNotEmpty(sectionLabel) && footnotesTemplate.children().size() > 0;
    }

    @Nullable
    private TextNode getSectionLabelNode(final Element footnotesTemplate) {
        return footnotesTemplate.childNodes().stream()
                .filter(node -> node instanceof TextNode).map(node -> (TextNode)node)
                .filter(node -> SECTION_LABEL_REG.matcher(node.text().trim()).matches())
                .findFirst().orElse(null);
    }

    private Element formatSectionLabel(final String sectionLabel) {
        String formattedSectionName = String.format(SECTION_LABEL, StringUtils.removeEnd(sectionLabel, DOT));
        return new Element(DIV).text(formattedSectionName)
                .addClass(SECTION_LABEL_CLASS);
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
            XmlDeclaration lastPagebreak = lastElement(pagebreaks);
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
                        || (node instanceof Element && ANCHOR.equals(node.nodeName()) && ((Element) node).hasClass(TR_FTN)));
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
        popupFootnoteBody.getElementsByTag(ANCHOR).forEach(this::cleanUpAnchor);
    }

    private Element cleanUpAnchor(final Element anchor) {
        if (ANCHOR.equalsIgnoreCase(anchor.tagName())) {
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
        Set<XmlDeclaration> missingPagebreaks = mainSectionPagebreaks.stream()
                .filter(mainSectionPagebreak -> !isPagebreakLabelInSection(footnotesPagebreaks, getLabel(mainSectionPagebreak)))
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(missingPagebreaks)) {
            addMissingPagebreaks(missingPagebreaks, mainSectionPagebreaks, footnotesPagebreaks, footnotesSection);
        }
    }

    private void addMissingPagebreaks(final Set<XmlDeclaration> missingPagebreaks, final List<XmlDeclaration> mainSectionPagebreaks,
                                      final List<XmlDeclaration> footnotesPagebreaks, final Element footnotesSection) {
        final MissingPagebreaksPointer pointer = new MissingPagebreaksPointer();
        final Map<String, XmlDeclaration> labelToFootnotePagebreak = getLabelToPagebreakMap(footnotesPagebreaks);
        final Node lastFootnotesPagebreak = lastElement(footnotesPagebreaks);

        mainSectionPagebreaks.forEach(mainPagebreak -> {
            if (missingPagebreaks.contains(mainPagebreak)) {
                addMissingPagebreak(footnotesSection, pointer, mainPagebreak);
            } else {
                moveMissingPbsPointer(pointer, footnotesSection, labelToFootnotePagebreak, lastFootnotesPagebreak, mainPagebreak);
            }
        });

    }

    private void addMissingPagebreak(final Element footnotesSection, final MissingPagebreaksPointer pointer, final XmlDeclaration mainPagebreak) {
        Node newFootnotePagebreak = mainPagebreak.clone();
        if (pointer.isEmpty()) {
            footnotesSection.prependChild(newFootnotePagebreak);
        } else {
            pointer.get().after(newFootnotePagebreak);
        }
        pointer.set(newFootnotePagebreak);
    }

    private void moveMissingPbsPointer(final MissingPagebreaksPointer pointer, final Element footnotesSection,
                                       final Map<String, XmlDeclaration> labelToFootnotePagebreak,
                                       final Node lastFootnotesPagebreak, final XmlDeclaration mainPagebreak) {
        Node footnotesPagebreak = labelToFootnotePagebreak.get(getLabel(mainPagebreak));
        if (footnotesPagebreak == lastFootnotesPagebreak) {
            pointer.set(lastElement(footnotesSection.childNodes()));
        } else {
            pointer.set(footnotesPagebreak);
        }
    }

    private Map<String, XmlDeclaration> getLabelToPagebreakMap(final List<XmlDeclaration> footnotesPagebreaks) {
        return footnotesPagebreaks.stream().collect(
                Collectors.toMap(PageNumberUtil::getLabel, Function.identity()));
    }

    private boolean isPagebreakLabelInSection(final List<XmlDeclaration> pagebreaks, final String pagebreakLabel) {
        return pagebreaks.stream()
                .anyMatch(pagebreak -> pagebreakLabel.equals(getLabel(pagebreak)));
    }

    private void convertPagebreaksToProviewPbs(final Element footnotesTemplate, final boolean pageVolumesSet) {
        getPagebreaks(footnotesTemplate).stream()
            .peek(pagebreak -> addVolToPageNumber(pagebreak, pageVolumesSet))
            .forEach(pagebreak -> pagebreak.replaceWith(convertToProviewPagebreak(pagebreak)));
    }

    private void addVolToPageNumber(final Element pagebreak, final boolean pageVolumesSet) {
        if (pageVolumesSet) {
            addVolumeToPageNumber(pagebreak);
        }
    }

    private Elements getPagebreaks(final Element element) {
        return element.getElementsByTag(PAGEBREAK);
    }

    private List<XmlDeclaration> getProviewPagebreaks(final Element element) {
        return jsoup.selectXmlProcessingInstructions(element, PB);
    }

    private Element getElementByTag(final Element parentElement, final String tagName) {
        return parentElement.getElementsByTag(tagName).first();
    }

    private Element getElementByClass(final Element parentElement, final String className) {
        return parentElement.getElementsByClass(className).first();
    }

    private void remove(final Element element) {
        if (Objects.nonNull(element)) {
            element.remove();
        }
    }

    private <T> T lastElement(final List<T> list) {
        return CollectionUtils.isNotEmpty(list) ? list.get(list.size() - 1) : null;
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

    private static class MissingPagebreaksPointer {
        private Node placeHolder;

        public Node get() {
            return placeHolder;
        }

        public void set(final Node placeHolder) {
            this.placeHolder = placeHolder;
        }

        public boolean isEmpty() {
            return placeHolder == null;
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
