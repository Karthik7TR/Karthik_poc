package com.thomsonreuters.uscl.ereader.format.domain;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UnifiedTocElement extends Element {
    public static final String TOC_UNIFIED = "toc";
    public static final String UNIFIED_TOC_TAG_NAME = "entry";
    private static final String REMOVE_BULLETS = "\\([A-Za-z]\\)|\\([ixvlIXVL]+\\)|&apos;";
    private static final String APPENDIX = "appendix";
    private static final String CHAPTER = "chapter";
    private static final String PART = "part";
    private static final String NOT_LETTERS = "[^A-Za-z]";

    private final String originalName;
    private final String name;
    private final String docId;
    private final String uniqueKey;

    public UnifiedTocElement(final String originalName, final String docId, final String tocId) {
        super(UNIFIED_TOC_TAG_NAME);
        this.originalName = originalName;
        this.name = clearKey(originalName);
        this.docId = docId;
        this.uniqueKey = this.name + docId + tocId;
    }

    public List<String> parentNames() {
        return getNames(parentTocElements());
    }

    public List<String> siblingNames() {
        return getNames(siblingTocElements());
    }

    public List<String> childNames() {
        return getNames(childrenTocElements());
    }

    private List<UnifiedTocElement> parentTocElements() {
        return tocElements(super.parents());
    }

    private List<UnifiedTocElement> siblingTocElements() {
        return tocElements(super.siblingElements());
    }

    private List<UnifiedTocElement> childrenTocElements() {
        return tocElements(super.children());
    }

    private List<UnifiedTocElement> tocElements(final Elements elements) {
        return elements.stream()
                .filter(e -> e instanceof UnifiedTocElement)
                .map(e -> (UnifiedTocElement) e)
                .collect(Collectors.toList());
    }

    private List<String> getNames(final List<UnifiedTocElement> collection) {
        return collection.stream()
                .map(UnifiedTocElement::getName)
                .collect(Collectors.toList());
    }

    private String clearKey(String name) {
        name = name.toLowerCase().trim();
        if (name.contains(".") && !name.startsWith(APPENDIX) && !name.startsWith(CHAPTER) && !name.startsWith(PART)) {
            name = name.substring(name.indexOf("."));
        }
        name  = name.replaceAll(REMOVE_BULLETS, StringUtils.EMPTY);
        name  = name.replaceAll(NOT_LETTERS,StringUtils.EMPTY);
        return name.toLowerCase();
    }
}
