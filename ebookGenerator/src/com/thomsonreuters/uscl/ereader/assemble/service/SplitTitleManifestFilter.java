package com.thomsonreuters.uscl.ereader.assemble.service;

import java.net.ContentHandler;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SplitTitleManifestFilter extends AbstractTitleManifestFilter {
    // Element names that correspond to the toc.xml to title.xml transformation
    private static final String URI = "";
    private static final String CDATA = "CDATA";
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    private static final String ANCHOR_REFERENCE = "s";

    // ProView element and attribute constants
    private static final String DOC_ELEMENT = "doc";
    private static final String DOCS_ELEMENT = "docs";
    private static final String TITLE_ELEMENT = "title";
    private static final String TOC_ELEMENT = "toc";
    private List<Doc> documentsList;

    public SplitTitleManifestFilter(
        final TitleMetadata titleMetadata,
        final List<Doc> docList,
        final Map<String, String> altIdMap) {
        if (titleMetadata == null) {
            throw new IllegalArgumentException(
                "Cannot instantiate SplitTitleManifestFilter without initialized TitleMetadata");
        }

        this.titleMetadata = titleMetadata;
        documentsList = docList;

        if (titleMetadata.getIsPilotBook()) {
            this.altIdMap = altIdMap;
        }
    }

    /**
     * Returns an anchor reference for a given toc node.
     *
     * @param tocNode
     *            the toc entry containing a toc node guid.
     * @return Attributes the anchor reference in doc_guid/toc_guid format.
     */
    protected Attributes getAttributes(final TocNode tocNode) {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, ANCHOR_REFERENCE, ANCHOR_REFERENCE, CDATA, tocNode.getAnchorReference());
        return attributes;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (!TITLE_ELEMENT.equals(qName)) {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (!TITLE_ELEMENT.equals(qName)) {
            super.endElement(uri, localName, qName);
        }
    }

    /**
     * Emits the table of contents to the configured {@link ContentHandler}.
     *
     * @throws SAXException
     *             the data could not be written.
     */
    protected void writeTableOfContents() throws SAXException {
        if (tableOfContents.getChildren().size() > 0) {
            super.startElement(URI, TOC_ELEMENT, TOC_ELEMENT, EMPTY_ATTRIBUTES);
            for (final TocNode child : tableOfContents.getChildren()) {
                writeTocNode(child);
            }
            super.endElement(URI, TOC_ELEMENT, TOC_ELEMENT);
        }
    }

    /**
     * Writes the documents block to the owning {@link ContentHandler} and ends
     * the title manifest.
     *
     * @throws SAXException
     *             if the data could not be written.
     */
    @Override
    public void endDocument() throws SAXException {
        writeTableOfContents();
        if (!documentsList.isEmpty() && documentsList.size() > 0) {
            writeDocuments();
        }
        writeISBN();
        super.endElement(URI, TITLE_ELEMENT, TITLE_ELEMENT);
        super.endDocument();
    }

    /**
     * Writes the ordered list of documents to the configured
     * {@link ContentHandler}.
     *
     * @throws SAXException
     *             if the data could not be written.
     */
    protected void writeDocuments() throws SAXException {
        super.startElement(URI, DOCS_ELEMENT, DOCS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Doc document : documentsList) {
            super.startElement(URI, DOC_ELEMENT, DOC_ELEMENT, getAttributes(document));
            super.endElement(URI, DOC_ELEMENT, DOC_ELEMENT);
        }
        super.endElement(URI, DOCS_ELEMENT, DOCS_ELEMENT);
    }
}
