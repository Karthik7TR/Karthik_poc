package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.core.FormatConstants.HASH;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ANCHOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BODY;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BODY_BOTTOM;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FTNNAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HIDDEN_FOOTNOTE_CLASS_NAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HIDDEN_FOOTNOTE_REFERENCE_CLASS_NAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HREF;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ID;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.PAGE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.NAME;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.TR_FOOTNOTE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.TR_FOOTNOTES_CLASS_SELECTOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.TR_FTN;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PB;
import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class AddHiddenFootnotesAndReferences implements JsoupTransformation {
    private static final String HIDDEN_FOOTNOTE_NAME_SUFFIX = "_hidden";
    private static final String ERROR_GET_FOOTNOTE = "Footnote is missing tr_footnote parent";
    private static final String ERROR_ADD_HIDDEN_FOOTNOTE = "Unable to add hidden footnotes for document {}. {}";

    @Autowired
    private JsoupService jsoupService;

    @Override
    public void transform(final File currentFile, final Document document, final BookStep step) {
        try {
            if (step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS)) {
                Element mainSection = document.selectFirst(SECTION);

                ofNullable(document.selectFirst(TR_FOOTNOTES_CLASS_SELECTOR))
                        .ifPresent(footnotesSection -> addHiddenFootnoteBodies(mainSection, footnotesSection));
            }
        } catch (EBookException e) {
            log.error(ERROR_ADD_HIDDEN_FOOTNOTE, currentFile.getName(), e.getMessage());
        }
    }

    private void addHiddenFootnoteBodies(final Element mainSection, final Element footnotesSection) {
        Map<String, Element> footnoteNameToFootnoteAnchor = getFootnoteNameToFootnoteAnchorMap(footnotesSection);
        Map<String, Node> pageLabelToPageNodeInFootnoteSection = getPageLabelToPageNodeInFootnoteSectionMap(footnotesSection);

        mainSection.getElementsByClass(TR_FTN).stream()
                .filter(ftnRef -> referenceAndFootnoteOnDifferentPages(ftnRef, footnoteNameToFootnoteAnchor))
                .forEach(ftnRef -> addHiddenFootnoteAndReferenceToPage(ftnRef,
                        footnotesSection,
                        footnoteNameToFootnoteAnchor,
                        pageLabelToPageNodeInFootnoteSection));
    }

    private Map<String, Element> getFootnoteNameToFootnoteAnchorMap(final Element footnotesSection) {
        return footnotesSection.getElementsByClass(TR_FTN).stream()
                .collect(Collectors.toMap(ftn -> ftn.attr(NAME), Function.identity()));
    }

    private Map<String, Node> getPageLabelToPageNodeInFootnoteSectionMap(final Element footnotesSection) {
        return getProviewPagebreaks(footnotesSection).stream()
                .collect(Collectors.toMap(PageNumberUtil::getLabel, Function.identity()));
    }

    private boolean referenceAndFootnoteOnDifferentPages(final Element ftnRef, final Map<String, Element> footnoteNameToFootnoteAnchor) {
        String ftnName = ftnRef.attr(FTNNAME);
        String ftnRefPage = ftnRef.attr(PAGE);

        return ofNullable(footnoteNameToFootnoteAnchor.get(ftnName))
                .map(footnoteAnchor -> footnoteAnchor.attr(PAGE))
                .filter(ftnPage -> !ftnRefPage.equals(ftnPage))
                .isPresent();
    }

    private void addHiddenFootnoteAndReferenceToPage(final Element ftnRef, final Element footnotesSection, final Map<String, Element> footnoteNameToFootnoteAnchor, final Map<String, Node> pageLabelToPageNodeInFootnoteSection) {
        String ftnName = ftnRef.attr(FTNNAME);
        String refName = ftnRef.attr(NAME);
        String refNameHidden = addHiddenSuffix(refName);
        String ftnNameHidden = addHiddenSuffix(ftnName);
        String ftnRefPage = ftnRef.attr(PAGE);
        Element footnote = getFootnote(ftnName, footnoteNameToFootnoteAnchor);

        Element hiddenFootnote = createHiddenFootnote(footnote, refNameHidden, ftnNameHidden, ftnRefPage);
        addHiddenFootnoteToPage(hiddenFootnote, ftnRefPage, footnotesSection, pageLabelToPageNodeInFootnoteSection);

        createHiddenFootnoteRef(ftnRef, refNameHidden, ftnNameHidden);
    }

    private Element getFootnote(final String ftnName, final Map<String, Element> footnoteNameToFootnoteAnchor) {
        return footnoteNameToFootnoteAnchor.get(ftnName).parents().stream()
                .filter(element -> TR_FOOTNOTE.equals(element.className()))
                .findFirst().orElseThrow(() -> new EBookException(ERROR_GET_FOOTNOTE));
    }

    private void createHiddenFootnoteRef(final Element ftnRef, final String refNameHidden, final String ftnNameHidden) {
        Element supFtnRef = ftnRef.parent();
        Element newSupFtnRef = supFtnRef.clone();
        Element newFtnRef = newSupFtnRef.getElementsByTag(ANCHOR).first();

        supFtnRef.addClass(HIDDEN_FOOTNOTE_REFERENCE_CLASS_NAME);
        supFtnRef.before(newSupFtnRef);

        newSupFtnRef.attr(ID, refNameHidden);

        newFtnRef.attr(NAME, refNameHidden);
        newFtnRef.attr(FTNNAME, ftnNameHidden);
        newFtnRef.attr(HREF, HASH + ftnNameHidden);
    }

    private Element createHiddenFootnote(final Element footnote, final String refNameHidden, final String ftnNameHidden, final String ftnRefPage) {
        Element hiddenFootnote = footnote.clone();

        hiddenFootnote.getElementsByClass(FOOTNOTE_BODY_BOTTOM).remove();
        setAttributesOfHiddenFootnote(hiddenFootnote, refNameHidden, ftnNameHidden, ftnRefPage);
        cleanUpAnchorsInHiddenFootnote(hiddenFootnote);

        return hiddenFootnote;
    }

    private void setAttributesOfHiddenFootnote(final Element hiddenFootnote, final String refNameHidden,
                                               final String ftnNameHidden, final String ftnRefPage) {
        hiddenFootnote.addClass(HIDDEN_FOOTNOTE_CLASS_NAME);
        Element anchor = hiddenFootnote.selectFirst(ANCHOR);
        anchor.attr(NAME, ftnNameHidden);
        anchor.attr(FTNNAME, refNameHidden);
        anchor.attr(PAGE, ftnRefPage);
        setSpanTagCloneId(anchor, ftnNameHidden);
    }

    private void setSpanTagCloneId(final Element anchor, final String ftnNameHidden) {
        Element spanTag = anchor.parent();
        spanTag.attr(ID, ftnNameHidden);
    }

    private void cleanUpAnchorsInHiddenFootnote(final Element hiddenFootnote) {
        hiddenFootnote.getElementsByClass(FOOTNOTE_BODY).stream()
                .flatMap(footnoteBody -> footnoteBody.getElementsByTag(ANCHOR).stream())
                .filter(anchor -> StringUtils.isNotEmpty(anchor.attr(ID)))
                .forEach(anchor -> anchor.attr(ID, addHiddenSuffix(anchor.attr(ID))));
    }

    private void addHiddenFootnoteToPage(final Element hiddenFootnote, final String ftnRefPage, final Element footnotesSection,
                                         final Map<String, Node> pageLabelToPageNodeInFootnoteSection) {
        if (pageLabelToPageNodeInFootnoteSection.containsKey(ftnRefPage)) {
            Node pageBreakInFootnoteSection = pageLabelToPageNodeInFootnoteSection.get(ftnRefPage);
            pageBreakInFootnoteSection.after(hiddenFootnote);
        } else {
            footnotesSection.prependChild(hiddenFootnote);
        }
    }

    private String addHiddenSuffix(final String text) {
        return text + HIDDEN_FOOTNOTE_NAME_SUFFIX;
    }

    private List<XmlDeclaration> getProviewPagebreaks(final Element element) {
        return jsoupService.selectXmlProcessingInstructions(element, PB);
    }
}
