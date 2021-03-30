package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DuplicatedPagebreaksResolver {
    private static final String SECTION = "section";
    private static final String RIGHT_SQUARE_BRACKET = "]";
    private static final String FOOTNOTE_REFERENCE = "footnote.reference";

    public void fixDuplicatedPagebreaks(final Document document) {
        Elements pagebreaks = getMainSectionPagebreaks(document);
        getLabelToPagebreaksMap(pagebreaks).forEach((k, samePagebreaks) -> {
            Element pagebreakToLeave = choosePagebreakToLeave(samePagebreaks);
            removeOtherPagebreaks(samePagebreaks, pagebreakToLeave);

            if (pagebreakIsInFootnoteReference(pagebreakToLeave)) {
                extractPagebreakFromFootnoteReference(pagebreakToLeave);
            }
        });
    }

    private Elements getMainSectionPagebreaks(final Document document) {
        return document.getElementsByTag(SECTION).stream()
                .map(section -> section.getElementsByTag(PageNumberUtil.PAGEBREAK))
                .findFirst().orElseGet(Elements::new);
    }

    private Map<String, List<Element>> getLabelToPagebreaksMap(final Elements pagebreaks) {
        return pagebreaks.stream().collect(
                Collectors.groupingBy(
                        PageNumberUtil::getLabelNo,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ));
    }

    private Element choosePagebreakToLeave(final List<Element> samePagebreaks) {
        return samePagebreaks.stream()
                .filter(this::pagebreakIsNotInFootnoteReference)
                .findFirst().orElseGet(() -> samePagebreaks.iterator().next());
    }

    private void removeOtherPagebreaks(final List<Element> samePagebreaks, final Element pagebreakToLeave) {
        samePagebreaks.stream().filter(p -> !Objects.equals(p, pagebreakToLeave)).forEach(Element::remove);
    }

    private boolean pagebreakIsNotInFootnoteReference(final Element pagebreak) {
        return !pagebreakIsInFootnoteReference(pagebreak);
    }

    private boolean pagebreakIsInFootnoteReference(final Element pagebreak) {
        return pagebreak.parent().tagName().equals(FOOTNOTE_REFERENCE);
    }

    private void extractPagebreakFromFootnoteReference(final Element pagebreakToLeave) {
        Element ref = pagebreakToLeave.parent();
        pagebreakToLeave.remove();
        Node nextSibling = ref.nextSibling();
        ref.after(pagebreakToLeave);
        if (startsWithSquareBracket(nextSibling)) {
            removeRightSquareBracket((TextNode) nextSibling);
            ref.after(new TextNode(RIGHT_SQUARE_BRACKET));
        }
    }

    private boolean startsWithSquareBracket(final Node nextSibling) {
        return nextSibling instanceof TextNode && ((TextNode) nextSibling).text().startsWith(RIGHT_SQUARE_BRACKET);
    }

    private void removeRightSquareBracket(final TextNode textAfter) {
        textAfter.text(textAfter.text().replace(RIGHT_SQUARE_BRACKET, StringUtils.EMPTY));
    }
}
