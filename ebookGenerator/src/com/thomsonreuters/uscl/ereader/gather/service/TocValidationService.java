package com.thomsonreuters.uscl.ereader.gather.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.core.EBConstants.NAME;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_TOC;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TocValidationService {
    private static final String ERROR_MESSAGE_PREFIX = "\n\nTable of Contents has %s %s with empty name.\n%s;\n";
    private static final String CHILD_NAME_SELECTOR = "> " + NAME;
    private static final String MISSING_NAME = "[missing name]";
    private static final String TOC_ROOT = "\t[toc root] -> ";
    private static final String NEXT_LEVEL_ARROW = " -> ";
    private static final String CLOSING_SEMICOLON = ";\n";
    private static final String ITEM = "item";
    private static final String ITEMS = "items";
    private final JsoupService jsoup;

    public void validateToc(final File tocFile) {
        Document document = jsoup.loadDocument(tocFile);

        List<Element> noNameTocElements = document.getElementsByTag(EBOOK_TOC).stream()
                .filter(this::nameIsMissing)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(noNameTocElements)) {
            throw new EBookException(getErrorMessage(noNameTocElements));
        }
    }

    private boolean nameIsMissing(final Element ebookToc) {
        Element name = getNameElement(ebookToc);
        return name == null || StringUtils.isBlank(name.text());
    }

    private Element getNameElement(final Element ebookToc) {
        return ebookToc.selectFirst(CHILD_NAME_SELECTOR);
    }

    private String getErrorMessage(final List<Element> noNameTocElements) {
        return String.format(ERROR_MESSAGE_PREFIX,
                noNameTocElements.size(),
                noNameTocElements.size() == 1 ? ITEM : ITEMS,
                noNameTocElements.stream()
                        .map(this::buildMissingTocNamePath)
                        .collect(Collectors.joining(CLOSING_SEMICOLON))
                );
    }

    private String buildMissingTocNamePath(final Element tocElement) {
        String path =  getPathElements(tocElement).stream()
                .filter(parent -> EBOOK_TOC.equals(parent.tagName()))
                .map(this::getNameElement)
                .map(this::extractTocName)
                .collect(Collectors.joining(NEXT_LEVEL_ARROW));

        return TOC_ROOT + path;
    }

    private List<Element> getPathElements(final Element tocElement) {
        List<Element> parents = tocElement.parents();
        Collections.reverse(parents);
        parents.add(tocElement);
        return parents;
    }

    private String extractTocName(final Element name) {
        return name != null && StringUtils.isNotBlank(name.text()) ? name.text() : MISSING_NAME;
    }
}
