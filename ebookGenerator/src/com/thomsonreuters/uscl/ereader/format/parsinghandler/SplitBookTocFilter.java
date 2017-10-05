package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class SplitBookTocFilter extends XMLFilterImpl {
    private List<String> splitTocGuidList;
    private static final Logger LOG = LogManager.getLogger(SplitBookTocFilter.class);

    private boolean bufferingTocGuid;
    private boolean leafNode;
    private boolean foundMatch;
    private boolean isEbook;

    private static final String URI = "";
    private static final String TITLE_BREAK = "titlebreak";
    private static final String TOC_GUID = "Guid";
    private static final String EBOOK = "EBook";
    private static final String EBOOK_TOC = "EBookToc";
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String MISSING_DOCUMENT = "MissingDocument";

    private int number = 1;
    private int total;

    private String splitTilteId;
    private StringBuffer tmpValue = new StringBuffer();
    private Map<String, DocumentInfo> documentInfoMap = new HashMap<>();
    private Map<String, String> elementValueMap = new LinkedHashMap<>();
    private List<String> wrongSplitTocNodes = new ArrayList<>();
    private String splitNode = "";

    public List<String> getWrongSplitTocNode() {
        return wrongSplitTocNodes;
    }

    public void setWrongSplitTocNode(final List<String> wrongSplitTocNode) {
        wrongSplitTocNodes = wrongSplitTocNode;
    }

    public String getSplitTilteId() {
        return splitTilteId;
    }

    public void setSplitTilteId(final String splitTilteId) {
        this.splitTilteId = splitTilteId;
    }

    public Map<String, DocumentInfo> getDocumentInfoMap() {
        return documentInfoMap;
    }

    public void setDocumentInfoMap(final Map<String, DocumentInfo> documentInfoMap) {
        this.documentInfoMap = documentInfoMap;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public List<String> getSplitTocGuidList() {
        return splitTocGuidList;
    }

    public void setSplitTocGuidList(final List<String> splitDocumentList) {
        splitTocGuidList = splitDocumentList;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (EBOOK.equals(qName)) {
            isEbook = Boolean.TRUE;
        }
        if (EBOOK_TOC.equals(qName)) {
            //This title break is to write at the top after the <EBook>
            if (isEbook) {
                super.startElement(URI, EBOOK, EBOOK, EMPTY_ATTRIBUTES);
                final StringBuffer titleBreakBuffer = new StringBuffer();
                titleBreakBuffer.append("eBook 1 of ");
                titleBreakBuffer.append(splitTocGuidList.size() + 1);
                final String text = titleBreakBuffer.toString();
                super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
                super.characters(text.toCharArray(), 0, text.length());
                super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
                isEbook = false;
            } else {
                if (elementValueMap.size() > 0) {
                    decideToWrite();
                }
            }
        } else if (TOC_GUID.equals(qName)) {
            bufferingTocGuid = Boolean.TRUE;
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (tmpValue.length() > 0) {
            for (int i = start; i < start + length; i++) {
                tmpValue.append(ch[i]);
            }
        } else {
            tmpValue.append(new String(ch, start, length));
        }

        if (bufferingTocGuid) {
            splitNode = StringUtils.substring(tmpValue.toString(), 0, 33);
            if (splitTocGuidList.contains(splitNode)) {
                foundMatch = true;
            }
            bufferingTocGuid = Boolean.FALSE;
        }
    }

    private void writeSplitToc(final boolean isSplit) throws SAXException {
        total++;
        if (isSplit) {
            super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);

            LOG.debug("TitleBreak has been added at " + splitNode + ", count " + total + " and title " + splitTilteId);
            total = 0;
            number++;
            final StringBuffer proviewDisplayName = new StringBuffer();
            proviewDisplayName.append("eBook ");
            proviewDisplayName.append(number);
            proviewDisplayName.append(" of " + (splitTocGuidList.size() + 1));
            final String text = proviewDisplayName.toString();
            super.characters(text.toCharArray(), 0, text.length());
            super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
            if (!leafNode) {
                LOG.error("Split at TOC node GUID " + splitNode + " is at an incorrect level");
                wrongSplitTocNodes.add(splitNode);
            }
        }

        leafNode = Boolean.FALSE;

        super.startElement(URI, EBOOK_TOC, EBOOK_TOC, EMPTY_ATTRIBUTES);

        for (final Map.Entry<String, String> entry : elementValueMap.entrySet()) {
            //Adding Document Info
            if (entry.getKey().equals(DOCUMENT_GUID)) {
                leafNode = Boolean.TRUE;
                final DocumentInfo documentInfo = new DocumentInfo();
                if (number > 1) {
                    documentInfo.setSplitTitleId(splitTilteId + "_pt" + number);
                } else {
                    documentInfo.setSplitTitleId(splitTilteId);
                }
                documentInfoMap.put(entry.getValue(), documentInfo);
            } else if (entry.getKey().equals(MISSING_DOCUMENT)) {
                leafNode = Boolean.TRUE;
            }
            super.startElement(URI, entry.getKey(), entry.getKey(), EMPTY_ATTRIBUTES);
            super.characters(entry.getValue().toCharArray(), 0, entry.getValue().length());
            super.endElement(URI, entry.getKey(), entry.getKey());
        }

        elementValueMap.clear();
    }

    private void decideToWrite() throws SAXException {
        if (!foundMatch) {
            writeSplitToc(false);
        } else {
            writeSplitToc(true);
            foundMatch = Boolean.FALSE;
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (EBOOK_TOC.equals(qName)) {
            if (elementValueMap.size() > 0) {
                decideToWrite();
            }
            super.endElement(uri, localName, qName);
        } else if (EBOOK.equals(qName)) {
            super.endElement(uri, localName, qName);
        } else {
            elementValueMap.put(qName, tmpValue.toString());
            tmpValue = new StringBuffer();
        }
    }
}
