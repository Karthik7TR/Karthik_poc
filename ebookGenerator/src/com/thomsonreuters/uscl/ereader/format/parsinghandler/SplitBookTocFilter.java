package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import static com.thomsonreuters.uscl.ereader.core.EBConstants.DOCUMENT_GUID;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_INLINE_TOC;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_PUBLISHING_INFORMATION;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_TITLE;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_TOC;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.MISSING_DOCUMENT;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.NAME;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.TITLE_BREAK;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.TOC_GUID;

@RequiredArgsConstructor
@Slf4j
public class SplitBookTocFilter extends XMLFilterImpl {

    private static final String URI = StringUtils.EMPTY;
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

    private final String splitTilteId;
    private final List<String> splitTocGuidList;
    @Getter
    private final Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

    private int number;
    private EBookToc currentNode;
    private String previousTitleBreakUuid;

    @Override
    public void startElement(final String uri, final String localName,
                             final String qName, final Attributes atts) throws SAXException {
        switch (qName) {
            case EBOOK:
                super.startElement(URI, EBOOK, EBOOK, EMPTY_ATTRIBUTES);
                placeTitleBreak();
                break;
            case EBOOK_TITLE:
            case EBOOK_INLINE_TOC:
            case EBOOK_PUBLISHING_INFORMATION:
                super.startElement(URI, localName, qName, atts);
                break;
            case EBOOK_TOC:
                currentNode = new EBookToc(currentNode);
                break;
            default:
                Optional.ofNullable(currentNode).ifPresent(node -> node.handleStartElement(qName));
                break;
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        Optional.ofNullable(currentNode).ifPresent(node -> node.readValue(ch, start, length));
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        switch (qName) {
            case DOCUMENT_GUID:
            case MISSING_DOCUMENT:
                Optional.ofNullable(currentNode).ifPresent(node -> node.handleEndElement(qName));
                outputXmlPart();
                break;
            case NAME:
            case TOC_GUID:
                Optional.ofNullable(currentNode).ifPresent(node -> node.handleEndElement(qName));
                break;
            case EBOOK:
            case EBOOK_TOC:
            case EBOOK_TITLE:
            case EBOOK_INLINE_TOC:
            case EBOOK_PUBLISHING_INFORMATION:
                super.endElement(uri, localName, qName);
                break;
            default:
                break;
        }
    }

    private void placeTitleBreak() {
        placeTextElement(TITLE_BREAK, String.format("eBook %s of %s", ++number, splitTocGuidList.size() + 1));
    }

    @SneakyThrows
    private void placeTextElement(final String elementName, final String elementValue) {
        super.startElement(URI, elementName, elementName, EMPTY_ATTRIBUTES);
        super.characters(elementValue.toCharArray(), 0, elementValue.length());
        super.endElement(URI, elementName, elementName);
    }

    private void saveDocumentInfo(final String documentGuid) {
        final DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setSplitTitleId(number > 1 ? String.format("%s_pt%s", splitTilteId, number) : splitTilteId);
        documentInfoMap.put(documentGuid, documentInfo);
    }

    private void defineSplitNode() {
        final String currentNodeGuid = StringUtils.substring(currentNode.getGuid(), 0, 33);
        if (splitTocGuidList.contains(StringUtils.substring(currentNodeGuid, 0, 33))) {
            EBookToc node = currentNode;
            while (!isAvailableForSplit(node)) {
                node = node.getPreviousNode();
            }

            if (previousTitleBreakUuid == null || node.isSplit) {
                throw new RuntimeException(String.format(
                    "Redundant split TOC uuid found: %s, it should be removed since it may cause the empty book part", currentNodeGuid));
            }
            node.setSplit(true);

            final String nodeGuid = StringUtils.substring(node.getGuid(), 0, 33);
            if (!currentNodeGuid.equals(nodeGuid)) {
                log.debug(String.format("Guid %s is non leaf, split point moved to %s", currentNodeGuid, nodeGuid));
            }
        }
    }

    private boolean isAvailableForSplit(final EBookToc node) {
        final EBookToc parentNode = node.getPreviousNode();
        return parentNode == null || parentNode.isMissingDocument()
            || !parentNode.getDocumentGuid().isEmpty() || parentNode.isWritten();
    }

    private void outputXmlPart() {
        final Deque<EBookToc> nodesToPrintStack = new LinkedList<>();
        EBookToc node = currentNode;
        while (node != null && !node.isWritten()) {
            nodesToPrintStack.push(node);
            node = node.getPreviousNode();
        }

        while (!nodesToPrintStack.isEmpty()) {
            nodesToPrintStack.pop().outputNodeContent();
        }
        //We don't wont to store all chain in memory so we make current node null after output in order that let GC remove objects
        currentNode = null;
    }

    @RequiredArgsConstructor
    private class EBookToc {
        private final StringBuilder name = new StringBuilder();
        private final StringBuilder guid = new StringBuilder();
        private final StringBuilder documentGuid = new StringBuilder();
        private final StringBuilder missingDocument = new StringBuilder();
        @Getter
        private final EBookToc previousNode;

        private Optional<StringBuilder> currentElementBuilder = Optional.empty();
        @Setter
        private boolean isSplit;
        @Getter
        private boolean isWritten;
        @Getter
        private boolean isMissingDocument;

        private void handleStartElement(final String elementName) {
            switch (elementName) {
                case NAME:
                    currentElementBuilder = Optional.of(name);
                    break;
                case TOC_GUID:
                    currentElementBuilder = Optional.of(guid);
                    break;
                case DOCUMENT_GUID:
                    currentElementBuilder = Optional.of(documentGuid);
                    break;
                case MISSING_DOCUMENT:
                    isMissingDocument = true;
                    currentElementBuilder = Optional.of(missingDocument);
                    break;
                default:
                    currentElementBuilder = Optional.empty();
                    break;
            }
        }

        private void readValue(final char[] text, final int offset, final int length) {
            currentElementBuilder.ifPresent(builder -> builder.append(text, offset, length));
        }

        private void handleEndElement(final String elementName) {
            switch (elementName) {
                case MISSING_DOCUMENT:
                    isMissingDocument = true;
                    break;
                case DOCUMENT_GUID:

                    break;
                case TOC_GUID:
                    defineSplitNode();
                    break;
                default:
                    break;
            }
            currentElementBuilder = Optional.empty();
        }

        @SneakyThrows
        private void outputNodeContent() {
            final String tocGuid = getGuid();
            if (previousTitleBreakUuid == null) {
                previousTitleBreakUuid = tocGuid;
            }

            if (isSplit) {
                placeTitleBreak();
                log.debug(String.format("TitleBreak has been added at %s", tocGuid));
            }

            SplitBookTocFilter.super.startElement(URI, EBOOK_TOC, EBOOK_TOC, EMPTY_ATTRIBUTES);
            placeTextElement(NAME, name.toString());
            placeTextElement(TOC_GUID, tocGuid);

            Optional.of(getDocumentGuid())
                .filter(StringUtils::isNotBlank)
                .ifPresent(value -> {
                    saveDocumentInfo(getDocumentGuid());
                    placeTextElement(DOCUMENT_GUID, value);
                });
            if (isMissingDocument) {
                placeTextElement(MISSING_DOCUMENT, missingDocument.toString());
            }

            isWritten = true;
        }

        private String getDocumentGuid() {
            return documentGuid.toString();
        }

        private String getGuid() {
            return guid.toString();
        }
    }
}
