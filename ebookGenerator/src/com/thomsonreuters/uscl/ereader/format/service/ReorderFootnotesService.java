package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.Elements;
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
    private static final String CONTENT_METADATA_BLOCK = "content.metadata.block";
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
    private static final String PB = "pb";
    private static final String PAGEBREAK = "pagebreak";
    private static final String LABEL = "label";

    private static final String PAGE_NUMBER_REFERENCE_TEMPLATE = "<a ftnname=\"page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" name=\"page_number_fnRef_PAGE_NUMBER_FOOTNOTE_ID\" href=\"#page_number_footnote_PAGE_NUMBER_FOOTNOTE_ID\" class=\"tr_ftn\" />";
    private static final String PAGE_NUMBER_FOOTNOTE_ID_PLACEHOLDER = "PAGE_NUMBER_FOOTNOTE_ID";
    private static final String PAGE_NUMBER_PLACEHOLDER = "PAGE_NUMBER";

    @Autowired
    private JsoupService jsoup;

    @Value("${page.number.footnote.template}")
    private File pageNumberFootnoteTemplateFile;

    private String pageNumberFootnoteTemplate;

    public void reorderFootnotes(final File srcGatherDir, final File srcDir, final File destDir) throws IOException {
        pageNumberFootnoteTemplate = FileUtils.readFileToString(pageNumberFootnoteTemplateFile);
        final Map<String, File> nameToXmlFile = Stream.of(srcGatherDir.listFiles())
            .collect(Collectors.toMap(file -> FilenameUtils.removeExtension(file.getName()), Function.identity()));

        Stream.of(srcDir.listFiles()).forEach(file -> {
            final Document doc = jsoup.loadDocument(file);
            final String fileUuid = FilenameUtils.removeExtension(file.getName());
            final Element mainSection = doc.selectFirst(SECTION);
            final Element footnotesBlock = mainSection.selectFirst(CO_FOOTNOTE_SECTION_ID);

            convertFootnoteReferencesInMainSection(mainSection);

            if (footnotesBlock != null) {
                footnotesBlock.remove();
                final Map<String, Element> idToFootnote = convertAndMapFootnotes(doc, footnotesBlock);
                final Element footnotesSectionToAppend = constructFootnotesSection(nameToXmlFile, footnotesBlock, idToFootnote, fileUuid);
                addPageLabels(mainSection, footnotesSectionToAppend, fileUuid);
                mainSection.after(footnotesSectionToAppend);
            }

            jsoup.saveDocument(destDir, file, doc);
        });
    }

    private Map<String, Element> convertAndMapFootnotes(final Document doc, final Element footnotesSection) {
        final Elements footnotes = footnotesSection.getElementsByClass(CO_FOOTNOTE_NUMBER);
        return footnotes.stream()
            .map(footnote -> footnote.parent())
            .peek(footnote -> convertFootnote(footnote, doc))
            .collect(Collectors.toMap(footnote -> getFootnoteId(footnote), Function.identity()));
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
        final Element footnotesBlock,
        final Map<String, Element> idToFootnote,
        final String fileUuid) {
        final Document xmlDoc = jsoup.loadDocument(nameToXmlFile.get(fileUuid));
        final Element footnotesTemplate = xmlDoc.getElementsByTag(FOOTNOTE_BLOCK).first();

        if (footnotesTemplate != null) {
            return constructBasedOnTemplate(idToFootnote, footnotesTemplate, xmlDoc);
        } else {
            return constructBasedOnBlock(footnotesBlock, xmlDoc);
        }
    }

    private Element constructBasedOnTemplate(
        final Map<String, Element> idToFootnote,
        final Element footnotesTemplate,
        final Document xmlDoc) {
        convertTopToFootnotesSection(footnotesTemplate);

        fillTemplateWithFootnotes(idToFootnote, footnotesTemplate);
        addAuthorFootnotes(idToFootnote, footnotesTemplate, xmlDoc);

        convertPagebreaksToProviewPbs(footnotesTemplate);

        return footnotesTemplate;
    }

    private Element constructBasedOnBlock(final Element footnotesBlock, final Document xmlDoc) {
        convertTopToFootnotesSection(footnotesBlock);

        footnotesBlock.getElementsByClass(CO_FOOTNOTE_SECTION_TITLE).remove();
        insertFirstPagebreak(footnotesBlock, xmlDoc);

        return footnotesBlock;
    }

    private void insertFirstPagebreak(final Element footnotesBlock, final Document xmlDoc) {
        final Optional<XmlDeclaration> firstPagebreak = getFirstPagebreak(xmlDoc.getElementsByTag(CONTENT_METADATA_BLOCK).first());
        firstPagebreak.ifPresent(pagebreak -> {
            footnotesBlock.getElementsByClass(TR_FOOTNOTE).first().before(convertToProviewPagebreak(pagebreak));
        });
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

    private XmlDeclaration convertToProviewPagebreak(final XmlDeclaration pagebreak) {
        final XmlDeclaration pb = new XmlDeclaration(PB, false);
        pb.attr(LABEL, pagebreak.attr(LABEL));
        return pb;
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
        return getPagebreaks(footnotesSection).stream().findFirst();
    }

    private List<XmlDeclaration> getPagebreaks(final Element element) {
        return jsoup.selectXmlProcessingInstructions(element, PAGEBREAK);
    }

    private List<XmlDeclaration> getProviewPagebreaks(final Element element) {
        return jsoup.selectXmlProcessingInstructions(element, PB);
    }
}
