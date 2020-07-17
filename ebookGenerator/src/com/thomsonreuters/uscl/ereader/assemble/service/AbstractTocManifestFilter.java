package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

@Slf4j
public abstract class AbstractTocManifestFilter extends XMLFilterImpl {
    private static final String INLINE_TOC_ITEM = "Table of Contents";
    private static final String INLINE_TOC_FILE = "inlineToc";
    private static final String INLINE_INDEX_ITEM = "Index";
    private static final String INLINE_INDEX_FILE = "inlineIndex";
    public static final String URI = "";
    public static final String TITLE_ELEMENT = "title";
    protected List<TocNode> nodesContainingDocuments = new ArrayList<>();
    public static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

    public static final String ANCHOR_REFERENCE = "s";
    public static final String CDATA = "CDATA";

    public static final String HTML_EXTENSION = ".html";
    protected TocNode previousNode;
    protected int currentDepth;
    protected int previousDepth;
    protected TocNode currentNode;

    public static final String ENTRY = "entry";
    public static final String TEXT = "text";

    protected File documentsDirectory;
    protected FileUtilsFacade fileUtilsFacade;

    protected List<Doc> orderedDocuments = new ArrayList<>();

    protected TitleMetadata titleMetadata;
    private static final String DEFAULT_PUBLISHING_INFORMATION = "PUBLISHING INFORMATION";
    // Front Matter Text
    private static final String TITLE_PAGE = "Title Page";
    private static final String COPYRIGHT_PAGE = "Copyright Page";
    private static final String ADDITIONAL_INFORMATION_OR_RESEARCH_ASSISTANCE =
        "Additional Information or Research Assistance";
    private static final String WESTLAW = "Westlaw";

    public void buildFrontMatterTOCEntries(final boolean isSplitBook) throws SAXException {
        currentDepth++;
        createFrontMatterNode(FrontMatterFileName.FRONT_MATTER_TITLE, TITLE_PAGE, isSplitBook);

        createInlineTocNode(isSplitBook);

        currentNode = new TocEntry(currentDepth);

        // FrontMatterTOCEntries should get First title Id
        if (isSplitBook) {
            currentNode.setSplitTitle(titleMetadata.getTitleId());
        }

        // Use ebook_definition.front_matter_toc_label
        if (titleMetadata.getFrontMatterTocLabel() != null) {
            currentNode.setText(titleMetadata.getFrontMatterTocLabel());
        } else {
            currentNode.setText(DEFAULT_PUBLISHING_INFORMATION);
        }
        currentNode.setTocNodeUuid(FrontMatterFileName.PUBLISHING_INFORMATION + FrontMatterFileName.ANCHOR);
        final TocNode parentNode = determineParent();
        currentNode.setParent(parentNode); // previousNode
        parentNode.addChild(currentNode);
        previousDepth = currentDepth;
        previousNode = currentNode;

        currentDepth++;

        createFrontMatterNode(FrontMatterFileName.COPYRIGHT, COPYRIGHT_PAGE, isSplitBook);

        final List<FrontMatterPage> frontMatterPagesList = titleMetadata.getFrontMatterPages();

        if (frontMatterPagesList != null) {
            // loop through additional frontMatter. PDFs will be taken care of
            // in the assets.
            for (final FrontMatterPage front_matter_page : frontMatterPagesList) {
                createFrontMatterNode(
                    FrontMatterFileName.ADDITIONAL_FRONT_MATTER + front_matter_page.getId(),
                    front_matter_page.getPageTocLabel(),
                    isSplitBook);
            }
        }

        createFrontMatterNode(
            FrontMatterFileName.RESEARCH_ASSISTANCE,
            ADDITIONAL_INFORMATION_OR_RESEARCH_ASSISTANCE,
            isSplitBook);

        if (!titleMetadata.isCwBook()) {
            createFrontMatterNode(FrontMatterFileName.WESTLAW, WESTLAW, isSplitBook);
        }

        if (titleMetadata.isIndexIncluded()) {
            createFrontMatterNode(INLINE_INDEX_FILE, INLINE_INDEX_ITEM, isSplitBook);
        }

        currentDepth--;
        currentDepth--;
    }

    public void createFrontMatterNode(final String guidBase, final String nodeText, final boolean isSplitBook)
        throws SAXException {
        currentNode = new TocEntry(currentDepth);
        currentNode.setDocumentUuid(guidBase);
        currentNode.setText(nodeText);
        // FrontMatterTOCEntries should get First title Id
        if (isSplitBook) {
            currentNode.setSplitTitle(titleMetadata.getTitleId());
        }
        currentNode.setTocNodeUuid(guidBase + FrontMatterFileName.ANCHOR);
        final TocNode parentNode = determineParent();
        currentNode.setParent(parentNode);
        parentNode.addChild(currentNode);
        previousDepth = currentDepth;
        previousNode = currentNode;
        orderedDocuments.add(new Doc(guidBase, guidBase + HTML_EXTENSION, 0, null));
        nodesContainingDocuments.add(currentNode);
    }

    private void createInlineTocNode(final boolean isSplitBook) throws SAXException {
        if (titleMetadata.isInlineToc()) {
            createFrontMatterNode(INLINE_TOC_FILE, INLINE_TOC_ITEM, isSplitBook);
        }
    }

    /**
     * Pushes document guids up through the ancestry into headings that didn't
     * contain document references in order to satisfy ProView's TOC to text
     * linking requirement.
     */
    protected void cascadeAnchors() {
        for (final TocNode document : nodesContainingDocuments) {
            if (StringUtils.isBlank(document.getParent().getDocumentGuid())) {
                cascadeAnchorUpwards(document.getParent(), document.getDocumentGuid());
            }
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

    /**
     * Assigns a document uuid to this node and cascades recursively up the
     * ancestry until a null node is found.
     *
     * Exit conditions: stops cascading upwards if a document uuid exists in a
     * parent or if a node does not have a parent.
     *
     * @param node
     *            the current node to be interrogated.
     * @param documentUuid
     *            the document uuid to assign to the node if, and only if, it
     *            doesn't have one already.
     */
    protected void cascadeAnchorUpwards(final TocNode node, final String documentUuid) {
        if (StringUtils.isBlank(node.getDocumentGuid())) {
            node.setDocumentUuid(documentUuid);
            if (node.getParent() != null) {
                cascadeAnchorUpwards(node.getParent(), documentUuid);
            }
        }
    }

    protected abstract void writeTableOfContents() throws SAXException;

    protected abstract void startManifest() throws SAXException;

    /**
     * Determines which parent to add a node to by comparing the depth of the
     * last added node to the current depth.
     *
     * @return the parent to which the current node should be added.
     * @throws SAXException
     *             if an error occurs.
     */
    protected TocNode determineParent() throws SAXException {
        if (currentDepth > previousDepth) {
            // the node we are adding is a child, so add it to the current node.
            return previousNode;
        } else if (currentDepth == previousDepth) {
            return previousNode.getParent();
        } else {
            return searchForParentInAncestryOfNode(previousNode, currentDepth - 1);
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
            return node;
        } else if (node.getDepth() < desiredDepth) {
            final String message = "Failed to identify a parent for node: "
                + node
                + " because of possibly bad depth assignment.  This is very likely a programming error in the SplitSplitTitleManifestFilter.";
            log.error(message);
            throw new SAXException(message); // could not find the parent, so
                                             // bail. This may need to be an
                                             // exception case because it
                                             // means that the way we are
                                             // assigning depth to nodes is
                                             // probably not implemented
                                             // correctly.
        } else {
            return searchForParentInAncestryOfNode(node.getParent(), desiredDepth);
        }
    }

    /**
     * Duplicates the corresponding document in order to conform to ProView
     * structural requirements.
     *
     * @param sourceDocId
     *            the file to be copied.
     * @param destDocId
     *            the "new" copy of the file.
     */
    public void copyHtmlDocument(final String sourceDocId, final String destDocId) throws SAXException {
        log.debug("Copying {} to {}", sourceDocId, destDocId);
        final File sourceFile = new File(documentsDirectory, sourceDocId + HTML_EXTENSION);
        final File destFile = new File(documentsDirectory, destDocId + HTML_EXTENSION);
        try {
            fileUtilsFacade.copyFile(sourceFile, destFile);
        } catch (final IOException e) {
            throw new SAXException(
                "Could not make copy of duplicate HTML document referenced in the TOC: " + sourceDocId,
                e);
        }
    }

    /**
     * Returns a list of TOC GUIDs that need to be cascaded into the generated
     * missing document.
     *
     * @param node
     *            document level node for which anchors need to be generated
     * @return list of TOC GUIDs for which Anchors need to be generated
     */
    protected List<String> getAnchorsToBeGenerated(final TocNode node) {
        final String docGuid = node.getDocumentGuid();
        final List<String> anchors = new ArrayList<>();
        TocNode parent = node.getParent();
        while (parent != null) {
            if (StringUtils.isBlank(parent.getDocumentGuid()) || docGuid.equals(parent.getDocumentGuid())) {
                anchors.add(parent.getTocGuid());
            } else {
                break;
            }
            parent = parent.getParent();
        }
        return anchors;
    }
}
