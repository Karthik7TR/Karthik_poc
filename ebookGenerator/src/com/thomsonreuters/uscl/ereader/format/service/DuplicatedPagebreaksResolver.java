package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_BLOCK;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.FOOTNOTE_REFERENCE;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.LABEL_DESIGNATOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.SECTION;
import static java.util.Optional.ofNullable;

@Service
public class DuplicatedPagebreaksResolver {
    private static final String LEFT_SQUARE_BRACKET = "[";
    private static final String RIGHT_SQUARE_BRACKET = "]";

    public void fixDuplicatedPagebreaks(final Document document) {
        fixPagebreaksInMain(document);
        removePagebreaksFromLabelDesignatorFootnotes(document);
    }

    private void fixPagebreaksInMain(final Document document) {
        ofNullable(document.getElementsByTag(SECTION).first()).ifPresent(mainSection -> {
            Elements pagebreaks = getPagebreaks(mainSection);
            extractPagebreaksFromElement(pagebreaks, true,
                    pagebreak -> pagebreakIsInElementToExtract(pagebreak, FOOTNOTE_REFERENCE),
                    this::extractPagebreakFromFootnoteReference);
            movePagebreaksAfterReferences(pagebreaks);
        });
    }

    public void fixPagebreaksInFootnotes(final Element footnotesSection) {
        Elements pagebreaks = getPagebreaks(footnotesSection);
        extractPagebreaksFromElement(pagebreaks, false,
                pagebreak -> pagebreakIsInElementToExtract(pagebreak, LABEL_DESIGNATOR),
                this::extractPagebreakFromLabelDesignator);
    }

    private void extractPagebreaksFromElement(final Elements pagebreaks,
                                              final boolean movePagebreaksForward,
                                              final Predicate<Element> isInElementToExtract,
                                              final Consumer<Element> extractor) {
        Map<String, List<Element>> labelToPagebreaks = getLabelToPagebreaksMap(pagebreaks);
        if (movePagebreaksForward) {
            Collections.reverse(pagebreaks);
        }

        pagebreaks.stream()
                .map(PageNumberUtil::getLabelNo)
                .distinct()
                .map(labelToPagebreaks::get)
                .forEach(samePagebreaks -> fixSameLabelPagebreaks(samePagebreaks, isInElementToExtract, extractor));
    }

    private Elements getPagebreaks(final Element section) {
        return section.getElementsByTag(PageNumberUtil.PAGEBREAK);
    }

    private Map<String, List<Element>> getLabelToPagebreaksMap(final Elements pagebreaks) {
        return pagebreaks.stream().collect(
                Collectors.groupingBy(
                        PageNumberUtil::getLabelNo,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ));
    }

    private void fixSameLabelPagebreaks(final List<Element> samePagebreaks,
                                        final Predicate<Element> isInElementToExtract,
                                        final Consumer<Element> extractor) {
        Element pagebreakToLeave = choosePagebreakToLeave(samePagebreaks, isInElementToExtract);
        removeOtherPagebreaks(samePagebreaks, pagebreakToLeave);

        if (isInElementToExtract.test(pagebreakToLeave)) {
            extractor.accept(pagebreakToLeave);
        }
    }

    private Element choosePagebreakToLeave(final List<Element> samePagebreaks, final Predicate<Element> isInElementToExtract) {
        return samePagebreaks.stream()
                .filter(isInElementToExtract.negate())
                .findFirst().orElseGet(() -> samePagebreaks.iterator().next());
    }

    private void removeOtherPagebreaks(final List<Element> samePagebreaks, final Element pagebreakToLeave) {
        samePagebreaks.stream().filter(p -> !Objects.equals(p, pagebreakToLeave)).forEach(Element::remove);
    }

    private boolean pagebreakIsInElementToExtract(final Element pagebreak, final String extractTagName) {
        return pagebreak.parent().tagName().equals(extractTagName);
    }

    private void extractPagebreakFromFootnoteReference(final Element pagebreakToLeave) {
        Element ref = pagebreakToLeave.parent();
        movePagebreakAfterFootnoteReference(pagebreakToLeave, ref);
    }

    private void movePagebreakAfterFootnoteReference(final Element pagebreak, final Element footnoteReference) {
        pagebreak.remove();
        Node nextSibling = footnoteReference.nextSibling();
        footnoteReference.after(pagebreak);
        if (startsWithSquareBracket(nextSibling)) {
            removeRightSquareBracket((TextNode) nextSibling);
            footnoteReference.after(new TextNode(RIGHT_SQUARE_BRACKET));
        }
    }

    private void extractPagebreakFromLabelDesignator(final Element pagebreakToLeave) {
        Element footnote = pagebreakToLeave.parent().parent();
        pagebreakToLeave.remove();
        footnote.before(pagebreakToLeave);
    }

    private boolean startsWithSquareBracket(final Node nextSibling) {
        return nextSibling instanceof TextNode && ((TextNode) nextSibling).text().startsWith(RIGHT_SQUARE_BRACKET);
    }

    private void removeRightSquareBracket(final TextNode textAfter) {
        textAfter.text(textAfter.text().replace(RIGHT_SQUARE_BRACKET, StringUtils.EMPTY));
    }

    private void removePagebreaksFromLabelDesignatorFootnotes(final Document document) {
        document.getElementsByTag(FOOTNOTE_BLOCK).stream()
                .map(footnote -> footnote.getElementsByTag(LABEL_DESIGNATOR))
                .flatMap(List::stream)
                .map(section -> section.getElementsByTag(PageNumberUtil.PAGEBREAK))
                .flatMap(List::stream)
                .forEach(Element::remove);
    }

    private void movePagebreaksAfterReferences(final Elements pagebreaks) {
        pagebreaks.stream().filter(this::isPagebreakBeforeFootnoteReference).forEach(pagebreak -> {
            Element footnoteReference = pagebreak.nextElementSibling();
            movePagebreakAfterFootnoteReference(pagebreak, footnoteReference);
        });
    }

    private boolean isPagebreakBeforeFootnoteReference(final Element pagebreak) {
        Node nextNode = pagebreak.nextSibling();
        Element nextElement = pagebreak.nextElementSibling();
        return nextElement != null
                && FOOTNOTE_REFERENCE.equals(nextElement.tagName())
                && (nextNode == nextElement || hasNoText(nextNode));
    }

    private boolean hasNoText(final Node nextNode) {
        if (nextNode instanceof TextNode) {
            String text = ((TextNode) nextNode).text().trim();
            return StringUtils.isEmpty(text) || LEFT_SQUARE_BRACKET.equals(text);
        }
        return false;
    }
}
