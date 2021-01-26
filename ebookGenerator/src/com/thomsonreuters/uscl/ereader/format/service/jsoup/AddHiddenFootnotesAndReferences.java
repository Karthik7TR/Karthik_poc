package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.PAGE_NUMBERS_MAP;
import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.PB;

@Service
public class AddHiddenFootnotesAndReferences implements JsoupTransformation {
    private static final String FTNNAME = "ftnname";
    private static final String TR_FTN = "tr_ftn";
    private static final String SECTION = "section";
    private static final String NAME = "name";
    private static final String SUP_TAG = "sup";
    private static final String A_TAG = "a";
    private static final String ID = "id";
    private static final String HREF = "href";
    private static final String PAGE = "page";
    private static final String TR_FOOTNOTES_CLASS = ".tr_footnotes";
    private static final String TR_FOOTNOTE_CLASS = ".tr_footnote";
    private static final String FOOTNOTE_BODY_CLASS_NAME = "footnote_body";
    private static final String HIDDEN_FOOTNOTE_CLASS_NAME = "hidden_footnote";
    private static final String HIDDEN_FOOTNOTE_REFERENCE_CLASS_NAME = "hidden_footnote_reference";
    private static final String HIDDEN_FOOTNOTE_NAME_SUFFIX = "_hidden";

    @Autowired
    private JsoupService jsoupService;

    @Override
    public void transform(final File currentFile, final Document document, final BookStep step) {
        if (step.getJobExecutionPropertyBoolean(JobExecutionKey.WITH_PAGE_NUMBERS)) {
            BidiMap<String, String> pageNumbers = getPageNumbers(step);
            String firstPage = getFirstPage(document, pageNumbers);
            Element mainSection = document.selectFirst(SECTION);
            Optional<Element> footnotesSection = Optional.ofNullable(document.selectFirst(TR_FOOTNOTES_CLASS));
            if (shouldAddHiddenFootnotesAndReferences(pageNumbers, footnotesSection)) {
                Elements anchorsFromMain = mainSection.getElementsByClass(TR_FTN);
                Elements footnotes = footnotesSection.get().select(TR_FOOTNOTE_CLASS);
                Map<String, Set<String>> pagesToAnchorsMap = getPagesToAnchorsMap(anchorsFromMain, firstPage);
                addHiddenFootnotesAndReferences(footnotes, footnotesSection.get(), mainSection, pageNumbers, firstPage, pagesToAnchorsMap);
            }
        }
    }

    @Override
    public void clear(final BookStep bookStep) {
        bookStep.getJobExecutionContext().remove(PAGE_NUMBERS_MAP);
    }

    private String getFirstPage(final Document document, final BidiMap<String, String> pageNumbers) {
        List<XmlDeclaration> pagebreaks = getProviewPagebreaks(document.selectFirst(SECTION));
        if (pageNumbers != null && !pagebreaks.isEmpty()) {
            String firstPagebreakLabel = PageNumberUtil.getLabel(pagebreaks.get(0));
            String previousPagebreakLabel = pageNumbers.getKey(firstPagebreakLabel);
            return previousPagebreakLabel != null ? previousPagebreakLabel : firstPagebreakLabel;
        }
        return null;
    }

    private boolean shouldAddHiddenFootnotesAndReferences(final BidiMap<String, String> pageNumbers, final Optional<Element> footnotesSection) {
        return pageNumbers != null && footnotesSection.isPresent() && !getProviewPagebreaks(footnotesSection.get()).isEmpty();
    }

    private Map<String, Set<String>> getPagesToAnchorsMap(final Elements anchorsFromMain, final String firstPage) {
        Map<String, Set<String>> pagesToAnchorsMap = new HashMap<>();
        String currentPageNumber = firstPage;
        Set<String> anchors = new HashSet<>();

        for (Element anchor : anchorsFromMain) {
            String pageNumber = anchor.attr(PAGE);
            if (!currentPageNumber.equals(pageNumber)) {
                pagesToAnchorsMap.put(currentPageNumber, anchors);
                currentPageNumber = pageNumber;
                anchors = new HashSet<>();
            }
            anchors.add(anchor.attr(FTNNAME));
        }

        pagesToAnchorsMap.put(currentPageNumber, anchors);
        return pagesToAnchorsMap;
    }

    private void addHiddenFootnotesAndReferences(final Elements footnotes, final Element footnotesSection, final Element mainSection,
        final BidiMap<String, String> pageNumbers, final String firstPage, final Map<String, Set<String>> pagesToAnchorsMap) {
        String currentPage = firstPage;
        Set<String> anchorsOnCurrentPage = pagesToAnchorsMap.get(currentPage);
        for (Element footnote : footnotes) {
            Element anchor = footnote.selectFirst(A_TAG);
            String pageAttrValue = anchor.attr(PAGE);
            if (pageAttrValue != null && !currentPage.equals(pageAttrValue)) {
                currentPage = pageAttrValue;
                anchorsOnCurrentPage = pagesToAnchorsMap.get(currentPage);
            }
            String anchorName = anchor.attr(NAME);
            if (CollectionUtils.isNotEmpty(anchorsOnCurrentPage) && !anchorsOnCurrentPage.contains(anchorName)) {
                String nextPage = pageNumbers.get(currentPage);
                addHiddenFootnoteAndReferenceToPageIfNeeded(nextPage, footnote, anchorName, footnotesSection, mainSection,
                        pagesToAnchorsMap, currentPage, false);
                String previousPage = pageNumbers.getKey(currentPage);
                addHiddenFootnoteAndReferenceToPageIfNeeded(previousPage, footnote, anchorName, footnotesSection, mainSection,
                        pagesToAnchorsMap, currentPage, true);
            }
        }
    }

    private void addHiddenFootnoteAndReferenceToPageIfNeeded(final String page, final Element footnote, final String anchorName,
        final Element footnotesSection, final Element mainSection, final Map<String, Set<String>> pagesToAnchorsMap,
        final String currentPage, final boolean isMovingBack) {
        if (page != null) {
            Set<String> anchorsOnNextPage = pagesToAnchorsMap.get(page);
            if (isAnchorOnPage(anchorsOnNextPage, anchorName)) {
                String pageLabel = isMovingBack ? currentPage : page;
                addHiddenFootnoteToPage(footnote, pageLabel, footnotesSection, isMovingBack);
                addHiddenFootnoteReference(mainSection, anchorName);
            }
        }
    }

    private boolean isAnchorOnPage(final Set<String> anchorsOnPage, final String anchorName) {
        return CollectionUtils.emptyIfNull(anchorsOnPage).stream()
                .anyMatch(anchorName::equals);
    }

    private void addHiddenFootnoteToPage(final Element footnote, final String pageNumber, final Element footnotesSection, final boolean isMovingBack) {
        Element hiddenFootnote = footnote.clone();
        setAttributesOfHiddenFootnote(hiddenFootnote);
        cleanUpAnchorsInHiddenFootnote(hiddenFootnote);
        List<XmlDeclaration> pagebreaks = getProviewPagebreaks(footnotesSection);
        for (XmlDeclaration pagebreak : pagebreaks) {
            if (pageNumber.equals(PageNumberUtil.getLabel(pagebreak))) {
                insertHiddenFootnoteNearPagebreak(hiddenFootnote, pagebreak, isMovingBack);
            }
        }
    }

    private void cleanUpAnchorsInHiddenFootnote(final Element hiddenFootnote) {
        hiddenFootnote.getElementsByClass(FOOTNOTE_BODY_CLASS_NAME).forEach(footnoteBody ->
                footnoteBody.getElementsByTag(A_TAG).forEach(anchor -> anchor.removeAttr(ID)));
    }

    private void insertHiddenFootnoteNearPagebreak(final Element hiddenFootnote, final XmlDeclaration pagebreak, final boolean isMovingBack) {
        if (isMovingBack) {
            pagebreak.previousSibling().before(hiddenFootnote);
        } else {
            pagebreak.after(hiddenFootnote);
        }
    }

    private void addHiddenFootnoteReference(final Element mainSection, final String footnoteName) {
        Elements references = mainSection.select(SUP_TAG + " ." + TR_FTN);
        for (Element reference : references) {
            String name = reference.attr(FTNNAME);
            if (footnoteName.equals(name)) {
                Element supTag = reference.parent();
                Element supTagCopy = createSupTagCopy(supTag, createHiddenReference(reference, name));
                supTag.before(supTagCopy);
            }
        }
    }

    private Element createHiddenReference(final Element reference, final String referenceName) {
        Element hiddenReference = reference.clone();
        hiddenReference.attr(FTNNAME, referenceName + HIDDEN_FOOTNOTE_NAME_SUFFIX);
        hiddenReference.attr(NAME, reference.attr(NAME) + HIDDEN_FOOTNOTE_NAME_SUFFIX);
        hiddenReference.attr(HREF, hiddenReference.attr(HREF) + HIDDEN_FOOTNOTE_NAME_SUFFIX);
        return hiddenReference;
    }

    private Element createSupTagCopy(final Element supTag, final Element hiddenReference) {
        Element supTagCopy = supTag.clone();
        supTag.addClass(HIDDEN_FOOTNOTE_REFERENCE_CLASS_NAME);
        supTagCopy.children().remove();
        supTagCopy.appendChild(hiddenReference);
        supTagCopy.attr(ID, supTag.attr(ID) + HIDDEN_FOOTNOTE_NAME_SUFFIX);
        return supTagCopy;
    }

    private void setAttributesOfHiddenFootnote(final Element hiddenFootnote) {
        hiddenFootnote.addClass(HIDDEN_FOOTNOTE_CLASS_NAME);
        Element anchor = hiddenFootnote.selectFirst(A_TAG);
        String name = anchor.attr(NAME);
        anchor.attr(NAME, name + HIDDEN_FOOTNOTE_NAME_SUFFIX);
        anchor.attr(FTNNAME, anchor.attr(FTNNAME) + HIDDEN_FOOTNOTE_NAME_SUFFIX);
        setSpanTagCloneId(anchor);
    }

    private void setSpanTagCloneId(final Element anchor) {
        Element spanTag = anchor.parent();
        spanTag.attr(ID, spanTag.attr(ID) + HIDDEN_FOOTNOTE_NAME_SUFFIX);
    }

    private List<XmlDeclaration> getProviewPagebreaks(final Element element) {
        return jsoupService.selectXmlProcessingInstructions(element, PB);
    }

    private BidiMap<String, String> getPageNumbers(final BookStep bookStep) {
        return (BidiMap<String, String>) bookStep.getJobExecutionProperty(PAGE_NUMBERS_MAP);
    }
}
