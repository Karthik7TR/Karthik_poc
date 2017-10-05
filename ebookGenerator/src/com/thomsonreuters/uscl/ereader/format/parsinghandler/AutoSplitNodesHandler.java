package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AutoSplitNodesHandler extends DefaultHandler {
    private int percentage;
    private int level;
    private Map<String, String> splitTocTextMap = new HashMap<String, String>();
    private int determinedPartSize;
    private int margin;

    private Integer splitMargin = 0;
    private String splitGuid;
    private int splitDepth;
    private TocNode currentNode;
    private TocNode previousNode;
    private int currentDepth;
    private int previousDepth;
    private StringBuilder tocGuid = new StringBuilder();
    private StringBuilder docGuid = new StringBuilder();
    private boolean bufferingTocGuid;
    private boolean bufferingDocGuid;
    private static final String EBOOK = "EBook";
    private static final String EBOOK_TOC = "EBookToc";
    private static final String TOC_GUID = "Guid";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String NAME = "Name";
    private boolean bufferingText;
    private StringBuilder textBuffer = new StringBuilder();

    private String splitText;
    private String forwardText;

    private int splitSize;
    private List<String> splitTocGuidList = new ArrayList<>();

    public List<String> getSplitTocGuidList() {
        return splitTocGuidList;
    }

    public void setSplitTocGuidList(final List<String> splitTocGuidList) {
        this.splitTocGuidList = splitTocGuidList;
    }

    public AutoSplitNodesHandler(final Integer partSize, final Integer thresholdPercent) {
        determinedPartSize = partSize;
        percentage = thresholdPercent;
        previousNode = tableOfContents;
    }

    public void parseInputStream(final InputStream tocStream)
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        final Reader reader = new InputStreamReader(tocStream, "UTF-8");
        final InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, this);
    }

    public int getDeterminedPartSize() {
        return determinedPartSize;
    }

    public void setDeterminedPartSize(final int determinedPartSize) {
        this.determinedPartSize = determinedPartSize;
    }

    private static final Logger LOG = LogManager.getLogger(AutoSplitNodesHandler.class);

    private TableOfContents tableOfContents = new TableOfContents();

    public TableOfContents getTableOfContents() {
        return tableOfContents;
    }

    public void setTableOfContents(final TableOfContents tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    public void setSplitSize(final int splitSize) {
        this.splitSize = splitSize;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException {
        if (EBOOK.equals(qName)) {
            currentDepth = 0;
            previousDepth = 0;
        } else if (EBOOK_TOC.equals(qName)) {
            currentDepth++;
            currentNode = new TocEntry(currentDepth);
            final TocNode parentNode = determineParent();
            currentNode.setParent(parentNode);
            parentNode.addChild(currentNode);
            previousDepth = currentDepth;
            previousNode = currentNode;
        } else if (TOC_GUID.equals(qName)) {
            bufferingTocGuid = Boolean.TRUE;
        } else if (DOCUMENT_GUID.equals(qName)) {
            bufferingDocGuid = Boolean.TRUE;
        } else if (NAME.equals(qName)) {
            bufferingText = Boolean.TRUE;
        }
    }

    /**
     * Buffers toc and doc guids and the corresponding text for each node
     * encountered during the parse.
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (bufferingTocGuid) {
            tocGuid.append(ch, start, length);
        } else if (bufferingDocGuid) {
            docGuid.append(ch, start, length);
        } else if (bufferingText) {
            textBuffer.append(ch, start, length);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (TOC_GUID.equals(qName)) {
            bufferingTocGuid = Boolean.FALSE;
            currentNode.setTocNodeUuid(tocGuid.toString());
            tocGuid = new StringBuilder();
        } else if (DOCUMENT_GUID.equals(qName)) {
            bufferingDocGuid = Boolean.FALSE;
            currentNode.setDocumentUuid(docGuid.toString());
            docGuid = new StringBuilder();
        } else if (NAME.equals(qName)) {
            bufferingText = Boolean.FALSE;
            currentNode.setText(textBuffer.toString());
            textBuffer = new StringBuilder();
        } else if (EBOOK_TOC.equals(qName)) {
            currentDepth--;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        getTableOfContentsforSplits();
    }

    protected void getTableOfContentsforSplits() throws SAXException {
        splitSize = determinedPartSize;
        margin = getMargin(splitSize);
        if (tableOfContents.getChildren().size() > 0) {
            for (final TocNode child : tableOfContents.getChildren()) {
                getSplitTocNode(child);
            }
        }
    }

    private void getSplitTocNode(final TocNode node) throws SAXException {
        //Intialize values
        if (level == 0) {
            splitMargin = 0;
            splitGuid = null;
            splitDepth = 0;
        }
        level = level + 1;

        // First Child of EBOOK_TOC should not be part of the split nodes
        int firstChild = 0;
        for (final TocNode child : node.getChildren()) {
            firstChild++;

            //Traveling from backward margin to forward margin
            if (level >= splitSize - margin && level <= splitSize + margin) {
                if ((splitDepth == 0 || splitDepth >= child.getDepth()) && firstChild != 1) {
                    splitDepth = child.getDepth();
                    splitGuid = child.getTocGuid();
                    splitText = child.getText();
                    splitMargin = level;
                }
            }

            //Decide to go backward or forward based on the depth of the node.
            if (level == splitSize + margin) {
                //Based on the depth get the split TocGuid
                if (splitDepth != 0) {
                    splitGuid = StringUtils.substring(splitGuid.toString(), 0, 33);
                    //Remove special characters
                    splitText = splitText.replaceAll("[^\\x20-\\x7e-\\n]", "");
                    splitTocTextMap.put(splitGuid, splitText);
                    splitTocGuidList.add(splitGuid);
                    margin = getMargin(determinedPartSize - (splitMargin - determinedPartSize));
                    splitSize = determinedPartSize - (level - splitMargin);
                }
                level = 0;
            }

            getSplitTocNode(child);
        }
    }

    public Integer getMargin(final int size) {
        Integer marginBasedOnSize = 0;
        try {
            marginBasedOnSize = (int) (size * percentage / 100);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        LOG.debug("Split size " + size + " and margin " + marginBasedOnSize);
        return marginBasedOnSize;
    }

    protected TocNode determineParent() throws SAXException {
        if (currentDepth > previousDepth) {
            // LOG.debug("Determined parent of " + currentNode + " to be " +
            // previousNode);
            return previousNode;
        } else if (currentDepth == previousDepth) {
            // LOG.debug("Determined parent of " + currentNode + " to be " +
            // previousNode.getParent());
            return previousNode.getParent();
        } else if (currentDepth < previousDepth) {
            return searchForParentInAncestryOfNode(previousNode, currentDepth - 1);
        } else {
            final String message = "Could not determine parent when adding node: " + currentNode;
            LOG.error(message);
            throw new SAXException(message);
        }
    }

    /**
     * Travels up a node's ancestry until reaching the desired depth.
     *
     * @param node
     *            the node whose ancestry to interrogate.
     * @param desiredDepth
     *            the depth to terminate the search. This could eventually be
     *            changed, if needed, to be a node ID if all nodes know who
     *            their immediate parent is (by identifier).
     */
    protected TocNode searchForParentInAncestryOfNode(final TocNode node, final int desiredDepth) throws SAXException {
        if (node.getDepth() == desiredDepth) {
            // LOG.debug("Found parent in the ancestry: " + node);
            return node;
        } else if (node.getDepth() < desiredDepth) {
            final String message = "Failed to identify a parent for node: "
                + node
                + " because of possibly bad depth assignment.  This is very likely a programming error in the TitleManifestFilter.";
            LOG.error(message);
            throw new SAXException(message);
        } else {
            return searchForParentInAncestryOfNode(node.getParent(), desiredDepth);
        }
    }

    public Map<String, String> getSplitTocTextMap() {
        return splitTocTextMap;
    }
}
