package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.assemble.exception.PlaceholderDocumentServiceException;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleIdAndProviewName;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.PROVIEW_NAME;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.TITLE_ID;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.DOCUMENT_GUID;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_INLINE_TOC;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_PUBLISHING_INFORMATION;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_TITLE;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.EBOOK_TOC;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.HTML_FILE_EXTENSION;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.MISSING_DOCUMENT;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.NAME;
import static com.thomsonreuters.uscl.ereader.core.EBConstants.TOC_GUID;

/**
 * A SAX event handler responsible for parsing a gathered TOC (an XML document) and producing a ProView title manifest
 * based on the TOC and {@link TitleMetadata} from the book definition.
 *
 * <p>
 * <strong>Implementation Comments:</strong> This approach has a number of merits, but also a number of recognized
 * limitations. All of the responsibility for making the input data (the TOC and associated book metadata) conform to
 * ProView format is done in one place (DRY). It is also somewhat complex, requiring some level of familiarity with SAX
 * filters and event-based document parsing. The implementation is broken up into chunks with the bulk of the heavy
 * lifting performed in the startElement, characters, and endElement methods. The primary motivation to put a lot of the
 * logic around conforming to ProView was to prevent these details leaking to the rest of the application and maintain
 * encapsulation in the assembly and delivery steps.
 * </p>
 *
 * <p>
 * <strong>Current limitations:</strong> ProView requires us to provide unique IDs for each document/asset/etc in the
 * book. This filter tracks document and family identifiers that it has encountered during the TOC parse in two
 * {@link Set}s. If a collision occurs when adding the next guid to one of the sets, a new guid is generated and added
 * to the {@link Set}. A side-effect of this approach is that all notes and bookmarks associated to a family guid will
 * "flow upwards" in the TOC. If multiple documents within a table of contents share the same family guid, the first one
 * is the only one that ships to ProView. Notes and bookmarks taken against the second and further document(s) with
 * duplicate family guids will become displaced when the book is published again (because we generated new identifiers
 * to conform to the uniqueness requirement).
 * </p>
 *
 * <p>
 * <strong>General Responsibilities:</strong>
 * <ul>
 * <li>Identifying and resolving duplicate documents. Where dupes are found, a new GUID will be generated. The duplicate
 * HTML file itself will be copied and assigned the new GUID.</li>
 * <li>Generating anchor references that correspond to the document the anchor(s) are contained in. This process is
 * termed "Cascading" or "Anchor Mapping" or "Cascading Anchors".</li>
 * <li>Replacing document guids with family guids. It does so in order to facilitate transfer of notes and bookmarks
 * between subsequent publishes of the a document that shares a consistent identifier even though its text may have
 * changed (in the case of Statutes and other regularly-updated material).</li>
 * <li>Invoking the PlaceholderDocumentService to create text in cases where there is no text for headings that follow
 * the last document in the table of contents.</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:lohitha.talatam@thomsonreuters.com">Lohitha Talatam</a> u0105666
 */
@Slf4j
public class SplitTocManifestFilter extends AbstractTocManifestFilter {
    private static final String DASH = "-";
    private static final String COLON_WITH_SPACE = ": ";
    private PlaceholderDocumentService placeholderDocumentService;
    private UuidGenerator uuidGenerator;

    private TableOfContents tableOfContents = new TableOfContents();
    private StringBuilder titleBreak = new StringBuilder();
    private TitleIdAndProviewName titleIdAndProviewName;

    private int titleBreakPart;

    private Map<String, String> familyGuidMap;

    private Set<String> uniqueDocumentIds = new HashSet<>();
    private Set<String> uniqueFamilyGuids = new HashSet<>();
    private StringBuilder tocGuid = new StringBuilder();
    private StringBuilder docGuid = new StringBuilder();
    private StringBuilder textBuffer = new StringBuilder();
    private boolean bufferingTocGuid;
    private boolean bufferingDocGuid;
    private boolean bufferingText;
    private boolean bufferingSplitTitle;
    private StringBuilder firstTitleBreak = new StringBuilder();
    private Map<String, List<String>> docImageMap;
    private List<SplitNodeInfo> splitNodeInfoList = new ArrayList<>();

    // Element names that correspond to the toc.xml to title.xml transformation

    private static final String TITLE_BREAK = "titlebreak";
    private static final String TOC_ELEMENT = "toc";

    public SplitTocManifestFilter(
        final TitleMetadata titleMetadata,
        final Map<String, String> familyGuidMap,
        final UuidGenerator uuidGenerator,
        final File transformedDocsDir,
        final FileUtilsFacade fileUtilsFacade,
        final PlaceholderDocumentService placeholderDocumentService,
        final Map<String, List<String>> docImageMap) {
        if (titleMetadata == null) {
            throw new IllegalArgumentException(
                "Cannot instantiate SplitSplitTitleManifestFilter without initialized TitleMetadata");
        }
        if (familyGuidMap == null) {
            throw new IllegalArgumentException(
                "Cannot instantiate SplitSplitTitleManifestFilter without a valid familyGuidMap");
        }
        if (uuidGenerator == null) {
            throw new IllegalArgumentException(
                "Cannot instantiate SplitSplitTitleManifestFilter without a UuidGenerator.");
        }
        if (transformedDocsDir == null || !transformedDocsDir.isDirectory()) {
            throw new IllegalArgumentException("Documents directory must not be null and must be a directory.");
        }
        if (fileUtilsFacade == null) {
            throw new IllegalArgumentException("fileUtilsFacade must not be null.");
        }
        if (placeholderDocumentService == null) {
            throw new IllegalArgumentException("placeholderDocumentService must not be null.");
        }

        validateTitleMetadata(titleMetadata);
        this.titleMetadata = titleMetadata;
        this.familyGuidMap = familyGuidMap;
        this.uuidGenerator = uuidGenerator;
        documentsDirectory = transformedDocsDir;
        this.fileUtilsFacade = fileUtilsFacade;
        this.placeholderDocumentService = placeholderDocumentService;
        previousNode = tableOfContents;
        this.docImageMap = docImageMap;
    }

    /**
     * Responsible for validating the title metadata is complete prior to generating the manifest.
     *
     * @param metadata the title metadata.
     */
    private void validateTitleMetadata(final TitleMetadata metadata) {
        // TODO: assert additional invariants based on required fields in the
        // manifest.
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        // write TOC in the manifest .
        startManifest();
    }

    /**
     * Writes the root of the title manifest to the configured {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    @Override
    protected void startManifest() throws SAXException {
        super.startElement(URI, TITLE_ELEMENT, TITLE_ELEMENT, EMPTY_ATTRIBUTES);
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException {
        if (EBOOK.equals(qName)) {
            currentDepth = 0;
            previousDepth = 0;
            // Add TOC NODES for Front Matter
            if (!titleMetadata.isCombinedBook()) {
                buildFrontMatterTOCEntries(titleMetadata.getTitleId());
            }
        } else if (EBOOK_TITLE.equals(qName)) {
            currentDepth++;
            titleIdAndProviewName = new TitleIdAndProviewName(attributes.getValue(TITLE_ID), attributes.getValue(PROVIEW_NAME));
        } else if (EBOOK_TOC.equals(qName)) { // we've reached the next element, time to add a new node to the tree.
            currentDepth++;
            currentNode = new TocEntry(currentDepth);

            // First splitEbook will get the titleId later parts will be appended with _pt and the number increments
            if (titleBreakPart <= 1) {
                currentNode.setSplitTitle(titleMetadata.getTitleId());
            } else {
                currentNode.setSplitTitle(titleMetadata.getTitleId() + "_pt" + titleBreakPart);
            }

            // currentNode.setSplitTitle(titleBreak.toString());
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
        } else if (MISSING_DOCUMENT.equals(qName)) {
            // this node is missing text, generate a new doc guid and xhtml5 content for the heading.
            final String missingDocumentGuid = currentNode.getTocGuid();
            final String missingDocumentFilename = missingDocumentGuid + HTML_FILE_EXTENSION;
            final File missingDocument = new File(documentsDirectory, missingDocumentFilename);
            try {
                final FileOutputStream missingDocumentOutputStream = new FileOutputStream(missingDocument);
                currentNode.setDocumentUuid(missingDocumentGuid);
                cascadeAnchors();
                final List<String> anchors = getAnchorsToBeGenerated(currentNode);
                placeholderDocumentService.generatePlaceholderDocument(
                    new EntityDecodedOutputStream(missingDocumentOutputStream),
                    currentNode.getText(),
                    currentNode.getTocGuid(),
                    anchors);
                orderedDocuments.add(new Doc(missingDocumentGuid, missingDocumentFilename, titleBreakPart, null));
                nodesContainingDocuments.add(currentNode); // need to cascade anchors into placeholder document text.
            } catch (final FileNotFoundException e) {
                throw new SAXException(
                    "A FileNotFoundException occurred when attempting to create an output stream to file: "
                        + missingDocument.getAbsolutePath(),
                    e);
            } catch (final PlaceholderDocumentServiceException e) {
                throw new SAXException(
                    "An unexpected error occurred while generating a placeholder document for Toc Node: ["
                        + tocGuid.toString()
                        + "] while producing the title manifest.",
                    e);
            }
        } else if (TITLE_BREAK.equals(qName)) {
            titleBreakPart++;
            bufferingSplitTitle = Boolean.TRUE;
        }
    }

    /**
     * Buffers toc and doc guids and the corresponding text for each node encountered during the parse.
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (bufferingTocGuid) {
            tocGuid.append(ch, start, length);
        } else if (bufferingDocGuid) {
            docGuid.append(ch, start, length);
        } else if (bufferingText) {
            textBuffer.append(ch, start, length);
        } else if (bufferingSplitTitle) {
            if (titleBreakPart > 1) {
                titleBreak = new StringBuilder();
                titleBreak.append(ch, start, length);
            } else {
                firstTitleBreak.append(ch, start, length);
            }
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (TOC_GUID.equals(qName)) {
            bufferingTocGuid = Boolean.FALSE;
            currentNode.setTocNodeUuid(tocGuid.toString());
            tocGuid = new StringBuilder();

            // TitleBreak is not needed for the first node as this is being set at the top of the TOC
            if (bufferingSplitTitle && titleBreakPart > 1) {
                currentNode.setTitleBreakString(titleBreak.toString());
                bufferingSplitTitle = Boolean.FALSE;
            }
        } else if (DOCUMENT_GUID.equals(qName)) {
            bufferingDocGuid = Boolean.FALSE;
            handleDuplicates();
            currentNode.setDocumentUuid(docGuid.toString());
            nodesContainingDocuments.add(currentNode);
            docGuid = new StringBuilder(); // clear the doc guid buffer independently of the other buffers.
        } else if (NAME.equals(qName)) {
            bufferingText = Boolean.FALSE;
            currentNode.setText(textBuffer.toString());
            textBuffer = new StringBuilder();
        } else if (EBOOK_TITLE.equals(qName)) {
            createFrontMatterNode(FrontMatterFileName.FRONT_MATTER_TITLE + DASH + titleIdAndProviewName.getTitleId().escapeSlashWithDash(),
                    TITLE_PAGE + COLON_WITH_SPACE + titleIdAndProviewName.getProviewName(),
                    FrontMatterFileName.FRONT_MATTER_TITLE, true, Optional.ofNullable(currentNode)
                            .map(TocNode::getSplitTitle)
                            .orElse(titleMetadata.getTitleId())
            );
            currentDepth--;
        } else if (EBOOK_INLINE_TOC.equals(qName)){
            currentDepth = 1;
            createInlineTocNode(titleMetadata.getTitleId());
            currentDepth = 0;
        }
        else if (EBOOK_PUBLISHING_INFORMATION.equals(qName)) {
            currentDepth = 1;
            String currentSplitTitle = currentNode.getSplitTitle();
            currentNode = new TocEntry(currentDepth);
            currentNode.setSplitTitle(currentSplitTitle);
            createPublishingInformation();
            currentDepth = 0;
        } else if (EBOOK_TOC.equals(qName)) {
            currentDepth--;
        } else if (MISSING_DOCUMENT.equals(qName)) {
            // The missing document end element event is eaten, because it isn't used.
        }
        // other elements are also eaten (there shouldn't be any).
    }

    /**
     * Handles all the decision making related to document or family identifier collisions.
     *
     * <p>
     * Delegates to another service to copy html documents.
     * </p>
     */
    private void handleDuplicates() throws SAXException {
        final String documentGuid = docGuid.toString();
        final List<String> image = docImageMap.get(documentGuid);
        if (uniqueDocumentIds.contains(documentGuid)) {
            // We've already seen this document, it's a duplicate.
            // Generate a new guid for it and add that to the list.
            final String uniqueGuid = uuidGenerator.generateUuid();
            log.debug(
                "encountered a duplicate uuid ["
                    + docGuid.toString()
                    + "]. Generating a new uuid ["
                    + uniqueGuid
                    + "] and copying the HTML file.");
            uniqueDocumentIds.add(uniqueGuid);
            copyHtmlDocument(documentGuid, uniqueGuid); // copy and rename the html file identified by the GUID listed
                                                        // in the gathered toc.
            orderedDocuments.add(new Doc(uniqueGuid, uniqueGuid + HTML_FILE_EXTENSION, titleBreakPart, image));
            currentNode.setDocumentUuid(uniqueGuid);
        } else {
            // Replace docGuid with the corresponding family Guid.
            if (familyGuidMap.containsKey(documentGuid)) {
                docGuid = new StringBuilder();
                String familyGuid = familyGuidMap.get(documentGuid);
                if (uniqueFamilyGuids.contains(familyGuid)) { // Have we already come across this family GUID?
                    log.debug("Duplicate family GUID " + familyGuid + ", generating new uuid.");
                    familyGuid = uuidGenerator.generateUuid();
                    orderedDocuments.add(new Doc(familyGuid, familyGuid + HTML_FILE_EXTENSION, titleBreakPart, image));
                    copyHtmlDocument(documentGuid, familyGuid);
                } else {
                    orderedDocuments.add(new Doc(familyGuid, documentGuid + HTML_FILE_EXTENSION, titleBreakPart, image));
                }
                currentNode.setDocumentUuid(familyGuid);
                docGuid.append(familyGuid); // perform the replacement
                uniqueFamilyGuids.add(familyGuid);
            } else {
                uniqueDocumentIds.add(docGuid.toString());
                orderedDocuments.add(new Doc(documentGuid, documentGuid + HTML_FILE_EXTENSION, titleBreakPart, image));
            }
        }
    }

    /**
     * Writes the documents block to the owning {@link ContentHandler} and ends the title manifest.
     *
     * @throws SAXException if the data could not be written.
     */
    @Override
    public void endDocument() throws SAXException {
        cascadeAnchors();
        writeTableOfContents();
        super.endElement(URI, TITLE_ELEMENT, TITLE_ELEMENT);
        super.endDocument();
    }

    /**
     * Emits the table of contents to the configured {@link ContentHandler}.
     *
     * @throws SAXException the data could not be written.
     */
    @Override
    protected void writeTableOfContents() throws SAXException {
        if (tableOfContents.getChildren().size() > 0) {
            super.startElement(URI, TOC_ELEMENT, TOC_ELEMENT, EMPTY_ATTRIBUTES);
            super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
            final String text = firstTitleBreak.toString();
            // text = StringUtils.substring(text,0,text.length()-1);
            super.characters(text.toCharArray(), 0, text.length());
            super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
            // writeFrontMatterTtitle();

            for (final TocNode child : tableOfContents.getChildren()) {
                writeTocNode(child);
            }
            super.endElement(URI, TOC_ELEMENT, TOC_ELEMENT);
        }
    }

    /**
     * Writes an individual toc node.
     *
     * @throws SAXException
     */
    private void writeTocNode(final TocNode node) throws SAXException {
        //
        if (StringUtils.isNotBlank(node.getTitleBreakString())) {
            super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
            final String title = node.getTitleBreakString();
            super.characters(title.toCharArray(), 0, title.length());
            super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
            final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
            splitNodeInfo.setSplitNodeGuid(node.getTocGuid());
            splitNodeInfo.setSpitBookTitle(node.getSplitTitle());
            splitNodeInfoList.add(splitNodeInfo);
        }
        //
        super.startElement(URI, ENTRY, ENTRY, getAttributes(node));

        super.startElement(URI, TEXT, TEXT, EMPTY_ATTRIBUTES);
        final String text = node.getText();
        super.characters(text.toCharArray(), 0, text.length());
        super.endElement(URI, TEXT, TEXT);
        for (final TocNode child : node.getChildren()) {
            writeTocNode(child);
        }
        super.endElement(URI, ENTRY, ENTRY);
    }

    protected List<Doc> getOrderedDocuments() {
        return orderedDocuments;
    }

    protected void setTableOfContents(final TableOfContents tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    public List<SplitNodeInfo> getSplitNodeInfoList() {
        return splitNodeInfoList;
    }

    public void setSplitNodeInfoList(final List<SplitNodeInfo> splitNodeInfoList) {
        this.splitNodeInfoList = splitNodeInfoList;
    }
}
