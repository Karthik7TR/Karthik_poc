package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ContentHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.assemble.exception.PlaceholderDocumentServiceException;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

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
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
class TitleManifestFilter extends AbstractTocManifestFilter
{
    private static final Logger LOG = LogManager.getLogger(TitleManifestFilter.class);
    private PlaceholderDocumentService placeholderDocumentService;
    private UuidGenerator uuidGenerator;
    private Map<String, String> altIdMap = new HashMap<>();

    private TableOfContents tableOfContents = new TableOfContents();
    private Map<String, String> familyGuidMap;
    private Set<String> uniqueDocumentIds = new HashSet<>();
    private Set<String> uniqueFamilyGuids = new HashSet<>();
    private StringBuilder tocGuid = new StringBuilder();
    private StringBuilder docGuid = new StringBuilder();
    private StringBuilder textBuffer = new StringBuilder();
    private boolean bufferingTocGuid;
    private boolean bufferingDocGuid;
    private boolean bufferingText;

    // Element names that correspond to the toc.xml to title.xml transformation
    private static final String URI = "";
    private static final String CDATA = "CDATA";
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    private static final String EBOOK = "EBook";
    private static final String EBOOK_TOC = "EBookToc";
    private static final String NAME = "Name";
    private static final String TOC_GUID = "Guid";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String ENTRY = "entry";
    private static final String TEXT = "text";
    private static final String MISSING_DOCUMENT = "MissingDocument";
    private static final String HTML_EXTENSION = ".html";

    // ProView element and attribute constants
    private static final String ARTWORK_ELEMENT = "artwork";
    private static final String MATERIAL_ELEMENT = "material";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String FEATURE_ELEMENT = "feature";
    private static final String FEATURES_ELEMENT = "features";
    private static final String ISBN_ELEMENT = "isbn";
    private static final String NAME_ELEMENT = "name";
    private static final String STATUS_ATTRIBUTE = "status";
    private static final String ONLINEEXPIRATION_ATTRIBUTE = "onlineexpiration";
    private static final String LANGUAGE_ATTRIBUTE = "language";
    private static final String LASTUPDATED_ATTRIBUTE = "lastupdated";
    private static final String TITLEVERSION_ATTRIBUTE = "titleversion";
    private static final String APIVERSION_ATTRIBUTE = "apiversion";
    private static final String COPYRIGHT_ELEMENT = "copyright";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String KEYWORD_ELEMENT = "keyword";
    private static final String KEYWORDS_ELEMENT = "keywords";
    private static final String ASSET_ELEMENT = "asset";
    private static final String ASSETS_ELEMENT = "assets";
    private static final String AUTHOR_ELEMENT = "author";
    private static final String AUTHORS_ELEMENT = "authors";
    private static final String DOC_ELEMENT = "doc";
    private static final String DOCS_ELEMENT = "docs";
    private static final String TITLE_ELEMENT = "title";
    private static final String SRC_ATTRIBUTE = "src";
    private static final String ID_ATTRIBUTE = "id";
    private static final String TOC_ELEMENT = "toc";
    private static final String ALT_ID_ATTRIBUTE = "altid";

    TitleManifestFilter(
        final TitleMetadata titleMetadata,
        final Map<String, String> familyGuidMap,
        final UuidGenerator uuidGenerator,
        final File documentsDirectory,
        final FileUtilsFacade fileUtilsFacade,
        final PlaceholderDocumentService placeholderDocumentService,
        final Map<String, String> altIdMap)
    {
        if (titleMetadata == null)
        {
            throw new IllegalArgumentException(
                "Cannot instantiate TitleManifestFilter without initialized TitleMetadata");
        }
        if (familyGuidMap == null)
        {
            throw new IllegalArgumentException("Cannot instantiate TitleManifestFilter without a valid familyGuidMap");
        }
        if (uuidGenerator == null)
        {
            throw new IllegalArgumentException("Cannot instantiate TitleManifestFilter without a UuidGenerator.");
        }
        if (documentsDirectory == null || !documentsDirectory.isDirectory())
        {
            throw new IllegalArgumentException("Documents directory must not be null and must be a directory.");
        }
        if (fileUtilsFacade == null)
        {
            throw new IllegalArgumentException("fileUtilsFacade must not be null.");
        }
        if (placeholderDocumentService == null)
        {
            throw new IllegalArgumentException("placeholderDocumentService must not be null.");
        }

        validateTitleMetadata(titleMetadata);
        this.titleMetadata = titleMetadata;
        this.familyGuidMap = familyGuidMap;
        this.uuidGenerator = uuidGenerator;
        this.documentsDirectory = documentsDirectory;
        this.fileUtilsFacade = fileUtilsFacade;
        this.placeholderDocumentService = placeholderDocumentService;
        previousNode = tableOfContents;

        if (titleMetadata.getIsPilotBook())
        {
            this.altIdMap = altIdMap;
        }
    }

    /**
     * Responsible for validating the title metadata is complete prior to generating the manifest.
     *
     * @param metadata the title metadata.
     */
    private void validateTitleMetadata(final TitleMetadata metadata)
    {
        // TODO: assert additional invariants based on required fields in the manifest.
    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        // write everything in the manifest up to the TOC and DOCs sections.
        startManifest();
        writeFeatures();
        writeMaterialId();
        writeCoverArt();
        writeAssets();
        writeDisplayName();
        writeAuthors();
        writeKeywords();
        writeCopyright();
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException
    {
        if (EBOOK.equals(qName))
        {
            currentDepth = 0;
            previousDepth = 0;
            // Add TOC NODES for Front Matter
            buildFrontMatterTOCEntries(false);
        }
        else if (EBOOK_TOC.equals(qName))
        { // we've reached the next element, add a new node to the tree.
            currentDepth++;
            currentNode = new TocEntry(currentDepth);
            final TocNode parentNode = determineParent();
            currentNode.setParent(parentNode);
            parentNode.addChild(currentNode);
            previousDepth = currentDepth;
            previousNode = currentNode;
        }
        else if (TOC_GUID.equals(qName))
        {
            bufferingTocGuid = Boolean.TRUE;
        }
        else if (DOCUMENT_GUID.equals(qName))
        {
            bufferingDocGuid = Boolean.TRUE;
        }
        else if (NAME.equals(qName))
        {
            bufferingText = Boolean.TRUE;
        }
        else if (MISSING_DOCUMENT.equals(qName))
        {
            // this node is missing text, generate a new doc guid and xhtml5
            // content for the heading.
            final String missingDocumentGuid = uuidGenerator.generateUuid();
            final String missingDocumentFilename = missingDocumentGuid + HTML_EXTENSION;
            final File missingDocument = new File(documentsDirectory, missingDocumentFilename);
            try
            {
                final FileOutputStream missingDocumentOutputStream = new FileOutputStream(missingDocument);
                currentNode.setDocumentUuid(missingDocumentGuid);
                cascadeAnchors();
                final List<String> anchors = getAnchorsToBeGenerated(currentNode);
                placeholderDocumentService.generatePlaceholderDocument(
                    new EntityDecodedOutputStream(missingDocumentOutputStream),
                    currentNode.getText(),
                    currentNode.getTocGuid(),
                    anchors);
                orderedDocuments.add(new Doc(missingDocumentGuid, missingDocumentFilename, 0, null));
                nodesContainingDocuments.add(currentNode); // need to cascade anchors into placeholder document text.
            }
            catch (final FileNotFoundException e)
            {
                throw new SAXException(
                    "A FileNotFoundException occurred when attempting to create an output stream to file: "
                        + missingDocument.getAbsolutePath(),
                    e);
            }
            catch (final PlaceholderDocumentServiceException e)
            {
                throw new SAXException(
                    "An unexpected error occurred while generating a placeholder document for Toc Node: ["
                        + tocGuid.toString()
                        + "] while producing the title manifest.",
                    e);
            }
        }
    }

    /**
     * Buffers toc and doc guids and the corresponding text for each node encountered during the parse.
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException
    {
        if (bufferingTocGuid)
        {
            tocGuid.append(ch, start, length);
        }
        else if (bufferingDocGuid)
        {
            docGuid.append(ch, start, length);
        }
        else if (bufferingText)
        {
            textBuffer.append(ch, start, length);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException
    {
        if (TOC_GUID.equals(qName))
        {
            bufferingTocGuid = Boolean.FALSE;
            currentNode.setTocNodeUuid(tocGuid.toString());
            tocGuid = new StringBuilder();
        }
        else if (DOCUMENT_GUID.equals(qName))
        {
            bufferingDocGuid = Boolean.FALSE;
            handleDuplicates();
            currentNode.setDocumentUuid(docGuid.toString());
            nodesContainingDocuments.add(currentNode);
            docGuid = new StringBuilder(); // clear the doc guid buffer independently of the other buffers.
        }
        else if (NAME.equals(qName))
        {
            bufferingText = Boolean.FALSE;
            currentNode.setText(textBuffer.toString());
            textBuffer = new StringBuilder();
        }
        else if (EBOOK_TOC.equals(qName))
        {
            currentDepth--;
        }
        else if (MISSING_DOCUMENT.equals(qName))
        {
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
    private void handleDuplicates() throws SAXException
    {
        final String documentGuid = docGuid.toString();
        if (uniqueDocumentIds.contains(documentGuid))
        {
            // We've already seen this document, it's a duplicate. Generate a new guid for it and add that to the list.
            final String uniqueGuid = uuidGenerator.generateUuid();
            LOG.debug(
                "encountered a duplicate uuid ["
                    + docGuid.toString()
                    + "]. Generating a new uuid ["
                    + uniqueGuid
                    + "] and copying the HTML file.");
            uniqueDocumentIds.add(uniqueGuid);
            copyHtmlDocument(documentGuid, uniqueGuid); // copy and rename the html file identified by the GUID listed
                                                        // in the gathered toc.
            orderedDocuments.add(new Doc(uniqueGuid, uniqueGuid + HTML_EXTENSION, 0, null));
            currentNode.setDocumentUuid(uniqueGuid);
        }
        else
        {
            // Replace docGuid with the corresponding family Guid.
            if (familyGuidMap.containsKey(documentGuid))
            {
                docGuid = new StringBuilder();
                String familyGuid = familyGuidMap.get(documentGuid);
                if (uniqueFamilyGuids.contains(familyGuid))
                { // Have we already come across this family GUID?
                    LOG.debug("Duplicate family GUID " + familyGuid + ", generating new uuid.");
                    familyGuid = uuidGenerator.generateUuid();
                    orderedDocuments.add(new Doc(familyGuid, familyGuid + HTML_EXTENSION, 0, null));
                    copyHtmlDocument(documentGuid, familyGuid);
                }
                else
                {
                    orderedDocuments.add(new Doc(familyGuid, documentGuid + HTML_EXTENSION, 0, null));
                }
                currentNode.setDocumentUuid(familyGuid);
                docGuid.append(familyGuid); // perform the replacement
                uniqueFamilyGuids.add(familyGuid);
            }
            else
            {
                uniqueDocumentIds.add(docGuid.toString());
                orderedDocuments.add(new Doc(documentGuid, documentGuid + HTML_EXTENSION, 0, null));
            }
        }
    }

    /**
     * Emits the table of contents to the configured {@link ContentHandler}.
     *
     * @throws SAXException the data could not be written.
     */
    @Override
    protected void writeTableOfContents() throws SAXException
    {
        if (tableOfContents.getChildren().size() > 0)
        {
            super.startElement(URI, TOC_ELEMENT, TOC_ELEMENT, EMPTY_ATTRIBUTES);
            for (final TocNode child : tableOfContents.getChildren())
            {
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
    private void writeTocNode(final TocNode node) throws SAXException
    {
        super.startElement(URI, ENTRY, ENTRY, getAttributes(node));
        super.startElement(URI, TEXT, TEXT, EMPTY_ATTRIBUTES);
        final String text = node.getText();
        super.characters(text.toCharArray(), 0, text.length());
        super.endElement(URI, TEXT, TEXT);
        for (final TocNode child : node.getChildren())
        {
            writeTocNode(child);
        }
        super.endElement(URI, ENTRY, ENTRY);
    }

    /**
     * Writes the documents block to the owning {@link ContentHandler} and ends the title manifest.
     *
     * @throws SAXException if the data could not be written.
     */
    @Override
    public void endDocument() throws SAXException
    {
        cascadeAnchors();
        writeTableOfContents();
        writeDocuments();
        writeISBN();
        super.endElement(URI, TITLE_ELEMENT, TITLE_ELEMENT);
        super.endDocument();
    }

    /**
     * Writes the ordered list of documents to the configured {@link ContentHandler}.
     *
     * @throws SAXException if the data could not be written.
     */
    protected void writeDocuments() throws SAXException
    {
        if (orderedDocuments.size() > 0)
        {
            super.startElement(URI, DOCS_ELEMENT, DOCS_ELEMENT, EMPTY_ATTRIBUTES);
            for (final Doc document : orderedDocuments)
            {
                super.startElement(URI, DOC_ELEMENT, DOC_ELEMENT, getAttributes(document));
                super.endElement(URI, DOC_ELEMENT, DOC_ELEMENT);
            }
            super.endElement(URI, DOCS_ELEMENT, DOCS_ELEMENT);
        }
    }

    /**
     * Writes the ISBN to the title.xml {@link ContentHandler}.
     *
     * @throws SAXException if the data could not be written.
     */
    protected void writeISBN() throws SAXException
    {
        super.startElement(URI, ISBN_ELEMENT, ISBN_ELEMENT, EMPTY_ATTRIBUTES);
        super.characters(titleMetadata.getIsbn().toCharArray(), 0, titleMetadata.getIsbn().length());
        super.endElement(URI, ISBN_ELEMENT, ISBN_ELEMENT);
    }

    /**
     * Writes an authors block to the {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeAuthors() throws SAXException
    {
        super.startElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final String authorName : titleMetadata.getAuthorNames())
        {
            super.startElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT, EMPTY_ATTRIBUTES);
            super.characters(authorName.toCharArray(), 0, authorName.length());
            super.endElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT);
        }
        // if (titleMetadata.getAuthors().size() == 0)
        super.endElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT);
    }

    /**
     * Writes an assets block to the {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeAssets() throws SAXException
    {
        super.startElement(URI, ASSETS_ELEMENT, ASSETS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Asset asset : titleMetadata.getAssets())
        {
            super.startElement(URI, ASSET_ELEMENT, ASSET_ELEMENT, getAttributes(asset));
            super.endElement(URI, ASSET_ELEMENT, ASSET_ELEMENT);
        }
        super.endElement(URI, ASSETS_ELEMENT, ASSETS_ELEMENT);
    }

    /**
     * Writes an authors block to the {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeKeywords() throws SAXException
    {
        super.startElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Keyword keyword : titleMetadata.getKeywords())
        {
            super.startElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT, getAttributes(keyword));
            final String text = keyword.getText();
            super.characters(text.toCharArray(), 0, text.length());
            super.endElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT);
        }
        super.endElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT);
    }

    /**
     * Returns a new instance of {@link Attributes} to be used within an asset element.
     *
     * @param asset the asset to create attributes from.
     * @return the attributes for the asset.
     */
    protected Attributes getAttributes(final Asset asset)
    {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, asset.getId());
        attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, asset.getSrc());
        return attributes;
    }

    /**
     * Returns a new instance of {@link Attributes} to be used within a keyword element.
     *
     * @param asset the asset to create attributes from.
     * @return the attributes for the asset.
     */
    protected Attributes getAttributes(final Keyword keyword)
    {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, keyword.getType());
        return attributes;
    }

    /**
     * Returns a new instance of {@link Attributes} to be used within a doc element.
     *
     * @param doc the doc to create attributes from.
     * @return the attributes for the doc.
     * @throws IOException
     */
    protected Attributes getAttributes(final Doc doc)
    {
        final AttributesImpl attributes = new AttributesImpl();
        final String guid = doc.getId();
        attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, guid);

        if (titleMetadata.getIsPilotBook())
        {
            final String altId = altIdMap.get(guid);
            if (altId != null)
            {
                attributes.addAttribute(URI, ALT_ID_ATTRIBUTE, ALT_ID_ATTRIBUTE, CDATA, altId);
            }
        }

        attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, doc.getSrc());
        return attributes;
    }

    /**
     * Writes a copyright block to the configured {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeCopyright() throws SAXException
    {
        super.startElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT, EMPTY_ATTRIBUTES);
        String copyright = titleMetadata.getCopyright();
        if (copyright != null)
        {
            copyright = copyright.replace("\r\n", " ");
        }
        super.characters(copyright.toCharArray(), 0, copyright.length());
        super.endElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT);
    }

    /**
     * Writes the root of the title manifest to the configured {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    @Override
    protected void startManifest() throws SAXException
    {
        super.startElement(URI, TITLE_ELEMENT, TITLE_ELEMENT, getTitleAttributes());
    }

    /**
     * Returns the attributes for the title manifest.
     *
     * @return an {@link Attributes} instance.
     */
    protected Attributes getTitleAttributes()
    {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, APIVERSION_ATTRIBUTE, APIVERSION_ATTRIBUTE, CDATA, titleMetadata.getApiVersion());
        attributes
            .addAttribute(URI, TITLEVERSION_ATTRIBUTE, TITLEVERSION_ATTRIBUTE, CDATA, titleMetadata.getTitleVersion());
        attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, titleMetadata.getTitleId());
        attributes
            .addAttribute(URI, LASTUPDATED_ATTRIBUTE, LASTUPDATED_ATTRIBUTE, CDATA, titleMetadata.getLastUpdated());
        attributes.addAttribute(URI, LANGUAGE_ATTRIBUTE, LANGUAGE_ATTRIBUTE, CDATA, titleMetadata.getLanguage());
        attributes.addAttribute(URI, STATUS_ATTRIBUTE, STATUS_ATTRIBUTE, CDATA, titleMetadata.getStatus());
        attributes.addAttribute(
            URI,
            ONLINEEXPIRATION_ATTRIBUTE,
            ONLINEEXPIRATION_ATTRIBUTE,
            CDATA,
            titleMetadata.getOnlineexpiration());
        return attributes;
    }

    /**
     * Writes the displayname to the configured {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeDisplayName() throws SAXException
    {
        super.startElement(URI, NAME_ELEMENT, NAME_ELEMENT, EMPTY_ATTRIBUTES);
        final String displayName = titleMetadata.getDisplayName();
        super.characters(displayName.toCharArray(), 0, displayName.length());
        super.endElement(URI, NAME_ELEMENT, NAME_ELEMENT);
    }

    /**
     * Writes a features block to the configured {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeFeatures() throws SAXException
    {
        super.startElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Feature feature : titleMetadata.getProviewFeatures())
        {
            super.startElement(URI, FEATURE_ELEMENT, FEATURE_ELEMENT, getAttributes(feature));
            super.endElement(URI, FEATURE_ELEMENT, FEATURE_ELEMENT);
        }
        super.endElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT);
    }

    /**
     * Returns the attributes associated to a given feature element.
     *
     * @param feature the feature whose attributes to get.
     * @return the attributes of the feature.
     */
    protected Attributes getAttributes(final Feature feature)
    {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, NAME_ELEMENT, NAME_ELEMENT, CDATA, feature.getName());
        if (StringUtils.isNotBlank(feature.getValue()))
        {
            attributes.addAttribute(URI, VALUE_ATTRIBUTE, VALUE_ATTRIBUTE, CDATA, feature.getValue());
        }
        return attributes;
    }

    /**
     * Writes a material id block to the configured {@link ContentHandler}.
     *
     * @throws SAXException if data could not be written.
     */
    protected void writeMaterialId() throws SAXException
    {
        super.startElement(URI, MATERIAL_ELEMENT, MATERIAL_ELEMENT, EMPTY_ATTRIBUTES);
        final String materialId = titleMetadata.getMaterialId();
        if (StringUtils.isNotBlank(materialId))
        {
            super.characters(materialId.toCharArray(), 0, materialId.length());
        }
        super.endElement(URI, MATERIAL_ELEMENT, MATERIAL_ELEMENT);
    }

    /**
     * Writes an artwork element to the configured {@link ContentHandler}.
     *
     * @throws SAXException if the data could not be written.
     */
    protected void writeCoverArt() throws SAXException
    {
        super.startElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT, getAttributes(titleMetadata.getArtwork()));
        super.endElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT);
    }

    /**
     * Retrieves the attributes for a given artwork object.
     *
     * @param artwork the artwork to create attributes for.
     * @return the {@link Attributes} for the artwork.
     */
    protected Attributes getAttributes(final Artwork artwork)
    {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, artwork.getSrc());
        attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, artwork.getType());
        return attributes;
    }

    protected void setTableOfContents(final TableOfContents tableOfContents)
    {
        this.tableOfContents = tableOfContents;
    }
}
