/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;

/**
 * A SAX event handler responsible for parsing a gathered TOC (an XML document)
 * and producing a ProView title manifest based on the TOC and
 * {@link TitleMetadata} from the book definition.
 * 
 * <p>
 * <strong>Implementation Comments:</strong> This approach has a number of
 * merits, but also a number of recognized limitations. All of the
 * responsibility for making the input data (the TOC and associated book
 * metadata) conform to ProView format is done in one place (DRY). It is also
 * somewhat complex, requiring some level of familiarity with SAX filters and
 * event-based document parsing. The implementation is broken up into chunks
 * with the bulk of the heavy lifting performed in the startElement, characters,
 * and endElement methods. The primary motivation to put a lot of the logic
 * around conforming to ProView was to prevent these details leaking to the rest
 * of the application and maintain encapsulation in the assembly and delivery
 * steps.
 * </p>
 * 
 * <p>
 * <strong>Current limitations:</strong> ProView requires us to provide unique
 * IDs for each document/asset/etc in the book. This filter tracks document and
 * family identifiers that it has encountered during the TOC parse in two
 * {@link Set}s. If a collision occurs when adding the next guid to one of the
 * sets, a new guid is generated and added to the {@link Set}. A side-effect of
 * this approach is that all notes and bookmarks associated to a family guid
 * will "flow upwards" in the TOC. If multiple documents within a table of
 * contents share the same family guid, the first one is the only one that ships
 * to ProView. Notes and bookmarks taken against the second and further
 * document(s) with duplicate family guids will become displaced when the book
 * is published again (because we generated new identifiers to conform to the
 * uniqueness requirement).
 * </p>
 * 
 * <p>
 * <strong>General Responsibilities:</strong>
 * <ul>
 * <li>Identifying and resolving duplicate documents. Where dupes are found, a
 * new GUID will be generated. The duplicate HTML file itself will be copied and
 * assigned the new GUID.</li>
 * <li>Generating anchor references that correspond to the document the
 * anchor(s) are contained in. This process is termed "Cascading" or
 * "Anchor Mapping" or "Cascading Anchors".</li>
 * <li>Replacing document guids with family guids. It does so in order to
 * facilitate transfer of notes and bookmarks between subsequent publishes of
 * the a document that shares a consistent identifier even though its text may
 * have changed (in the case of Statutes and other regularly-updated material).</li>
 * <li>Invoking the PlaceholderDocumentService to create text in cases where
 * there is no text for headings that follow the last document in the table of
 * contents.</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:lohitha.talatam@thomsonreuters.com">Lohitha
 *         Talatam</a> u0105666
 */
class SplitTocManifestFilter extends XMLFilterImpl {

	private static final String DEFAULT_PUBLISHING_INFORMATION = "PUBLISHING INFORMATION";
	private static final Logger LOG = Logger.getLogger(SplitTocManifestFilter.class);
	private FileUtilsFacade fileUtilsFacade;
	private PlaceholderDocumentService placeholderDocumentService;
	private File transformedDocsDir;
	private String docToSplitBookFileName;
	private TitleMetadata titleMetadata;
	private UuidGenerator uuidGenerator;

	private TableOfContents tableOfContents = new TableOfContents();
	private TocNode currentNode;
	private TocNode previousNode;
	private int currentDepth = 0;
	private int previousDepth = 0;
	private Map<String, String> familyGuidMap;

	private List<TocNode> nodesContainingDocuments = new ArrayList<TocNode>();
	private Set<String> uniqueDocumentIds = new HashSet<String>();
	private Set<String> uniqueFamilyGuids = new HashSet<String>();
	private List<Doc> orderedDocuments = new ArrayList<Doc>();
	private StringBuilder tocGuid = new StringBuilder();
	private StringBuilder docGuid = new StringBuilder();
	private StringBuilder textBuffer = new StringBuilder();
	private boolean bufferingTocGuid = false;
	private boolean bufferingDocGuid = false;
	private boolean bufferingText = false;
	private boolean bufferingSplitTitle = false;
	private StringBuilder titleBreak = new StringBuilder();
	private int titleBreakPart = 0;
	private StringBuilder firstTitleBreak = new StringBuilder();
	private Map<String, List<String>> docImageMap;

	public StringBuilder getTitleBreak() {
		return titleBreak;
	}

	public void setTitleBreak(StringBuilder titleBreak) {
		this.titleBreak = titleBreak;
	}

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
	private static final String ANCHOR_REFERENCE = "s";
	private static final String HTML_EXTENSION = ".html";
	private static final String TITLE_BREAK = "titlebreak";

	// Front Matter Text
	private static final String TITLE_PAGE = "Title Page";
	private static final String COPYRIGHT_PAGE = "Copyright Page";
	private static final String ADDITIONAL_INFORMATION_OR_RESEARCH_ASSISTANCE = "Additional Information or Research Assistance";
	private static final String WESTLAW_NEXT = "WestlawNext";
	
	private static final String TITLE_ELEMENT = "title";
	private static final String TOC_ELEMENT = "toc";

	public SplitTocManifestFilter(final TitleMetadata titleMetadata, final Map<String, String> familyGuidMap,
			final UuidGenerator uuidGenerator, final File transformedDocsDir, final FileUtilsFacade fileUtilsFacade,
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
		this.transformedDocsDir = transformedDocsDir;
		this.fileUtilsFacade = fileUtilsFacade;
		this.placeholderDocumentService = placeholderDocumentService;
		this.previousNode = tableOfContents;
		this.docImageMap = docImageMap;
	}


	/**
	 * Responsible for validating the title metadata is complete prior to
	 * generating the manifest.
	 * 
	 * @param metadata
	 *            the title metadata.
	 */
	private void validateTitleMetadata(TitleMetadata metadata) {
		// TODO: assert additional invariants based on required fields in the
		// manifest.
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		// write TOC in the manifest .
		startManifest();
	}

	public void buildFrontMatterTOCEntries() throws SAXException {
		currentDepth++;
		currentNode = new TocEntry(currentDepth);

		// FrontMatterTOCEntries should get First title Id
		currentNode.setSplitTitle(titleMetadata.getTitleId());

		// Use ebook_definition.front_matter_toc_label
		if (titleMetadata.getFrontMatterTocLabel() != null) {
			currentNode.setText(titleMetadata.getFrontMatterTocLabel());
		} else {
			currentNode.setText(DEFAULT_PUBLISHING_INFORMATION);
		}
		currentNode.setTocNodeUuid(FrontMatterFileName.PUBLISHING_INFORMATION + FrontMatterFileName.ANCHOR);
		TocNode parentNode = determineParent();
		currentNode.setParent(parentNode); // previousNode
		parentNode.addChild(currentNode);
		previousDepth = currentDepth;
		previousNode = currentNode;

		currentDepth++;
		createFrontMatterNode(FrontMatterFileName.FRONT_MATTER_TITLE, TITLE_PAGE);

		createFrontMatterNode(FrontMatterFileName.COPYRIGHT, COPYRIGHT_PAGE);

		List<FrontMatterPage> frontMatterPagesList = titleMetadata.getFrontMatterPages();

		if (frontMatterPagesList != null) {
			// loop through additional frontMatter. PDFs will be taken care of
			// in the assets.
			for (FrontMatterPage front_matter_page : frontMatterPagesList) {
				createFrontMatterNode(FrontMatterFileName.ADDITIONAL_FRONT_MATTER + front_matter_page.getId(),
						front_matter_page.getPageTocLabel());
			}
		}

		createFrontMatterNode(FrontMatterFileName.RESEARCH_ASSISTANCE, ADDITIONAL_INFORMATION_OR_RESEARCH_ASSISTANCE);

		createFrontMatterNode(FrontMatterFileName.WESTLAWNEXT, WESTLAW_NEXT);

		currentDepth--;
		currentDepth--;
	}

	public void createFrontMatterNode(String guidBase, String nodeText) throws SAXException {
		currentNode = new TocEntry(currentDepth);
		currentNode.setDocumentUuid(guidBase);
		currentNode.setText(nodeText);
		// FrontMatterTOCEntries should get First title Id
		currentNode.setSplitTitle(titleMetadata.getTitleId());
		currentNode.setTocNodeUuid(guidBase + FrontMatterFileName.ANCHOR);
		TocNode parentNode = determineParent();
		currentNode.setParent(parentNode);
		parentNode.addChild(currentNode);
		previousDepth = currentDepth;
		previousNode = currentNode;
		orderedDocuments.add(new Doc(guidBase, guidBase + HTML_EXTENSION, titleBreakPart, null));
		nodesContainingDocuments.add(currentNode);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (EBOOK.equals(qName)) {
			currentDepth = 0;
			previousDepth = 0;
			// Add TOC NODES for Front Matter
			buildFrontMatterTOCEntries();
		} else if (EBOOK_TOC.equals(qName)) { // we've reached the next element,
												// time to add a new node to the
												// tree.
			currentDepth++;
			currentNode = new TocEntry(currentDepth);

			// First splitEbook will get the titleId later parts will be
			// appended with _pt and the number increments
			if (titleBreakPart <= 1) {
				currentNode.setSplitTitle(titleMetadata.getTitleId());
			} else {
				currentNode.setSplitTitle(titleMetadata.getTitleId() + "_pt" + titleBreakPart);
			}

			// currentNode.setSplitTitle(titleBreak.toString());
			TocNode parentNode = determineParent();
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
			// this node is missing text, generate a new doc guid and xhtml5
			// content for the heading.
			String missingDocumentGuid = uuidGenerator.generateUuid();
			String missingDocumentFilename = missingDocumentGuid + HTML_EXTENSION;
			File missingDocument = new File(transformedDocsDir, missingDocumentFilename);
			try {
				FileOutputStream missingDocumentOutputStream = new FileOutputStream(missingDocument);
				currentNode.setDocumentUuid(missingDocumentGuid);
				cascadeAnchors();
				List<String> anchors = getAnchorsToBeGenerated(currentNode);
				placeholderDocumentService.generatePlaceholderDocument(new EntityDecodedOutputStream(
						missingDocumentOutputStream), currentNode.getText(), currentNode.getTocGuid(), anchors);
				orderedDocuments.add(new Doc(missingDocumentGuid, missingDocumentFilename, titleBreakPart, null));
				nodesContainingDocuments.add(currentNode); // need to cascade
															// anchors into
															// placeholder
															// document text.
			} catch (FileNotFoundException e) {
				throw new SAXException(
						"A FileNotFoundException occurred when attempting to create an output stream to file: "
								+ missingDocument.getAbsolutePath(), e);
			} catch (PlaceholderDocumentServiceException e) {
				throw new SAXException(
						"An unexpected error occurred while generating a placeholder document for Toc Node: ["
								+ tocGuid.toString() + "] while producing the title manifest.", e);
			}
		} else if (TITLE_BREAK.equals(qName)) {
			titleBreakPart++;
			bufferingSplitTitle = Boolean.TRUE;
		}
	}

	/**
	 * Returns an anchor reference for a given toc node.
	 * 
	 * @param documentGuid
	 *            the document uuid to include in the anchor reference.
	 * @param tocEntry
	 *            the toc entry containing a toc node guid.
	 * @return Attributes the anchor reference in doc_guid/toc_guid format.
	 */
	protected Attributes getAttributes(TocNode tocNode) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, ANCHOR_REFERENCE, ANCHOR_REFERENCE, CDATA, tocNode.getAnchorReference());
		return attributes;
	}

	/**
	 * Buffers toc and doc guids and the corresponding text for each node
	 * encountered during the parse.
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
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
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (TOC_GUID.equals(qName)) {
			bufferingTocGuid = Boolean.FALSE;
			currentNode.setTocNodeUuid(tocGuid.toString());
			tocGuid = new StringBuilder();

			// TitleBreak is not needed for the first node as this is being set
			// at the top of the TOC
			if (bufferingSplitTitle && titleBreakPart > 1) {
				currentNode.setTitleBreakString(titleBreak.toString());
				bufferingSplitTitle = Boolean.FALSE;
			}
		} else if (DOCUMENT_GUID.equals(qName)) {
			bufferingDocGuid = Boolean.FALSE;
			handleDuplicates();
			currentNode.setDocumentUuid(docGuid.toString());
			nodesContainingDocuments.add(currentNode);
			docGuid = new StringBuilder(); // clear the doc guid buffer
											// independently of the other
											// buffers.
		} else if (NAME.equals(qName)) {
			bufferingText = Boolean.FALSE;
			currentNode.setText(textBuffer.toString());
			textBuffer = new StringBuilder();
		} else if (EBOOK_TOC.equals(qName)) {
			currentDepth--;
		} else if (MISSING_DOCUMENT.equals(qName)) {
			// The missing document end element event is eaten, because it isn't
			// used.
		}
		// other elements are also eaten (there shouldn't be any).
	}

	/**
	 * Handles all the decision making related to document or family identifier
	 * collisions.
	 * 
	 * <p>
	 * Delegates to another service to copy html documents.
	 * </p>
	 */
	private void handleDuplicates() throws SAXException {
		String documentGuid = docGuid.toString();
		List<String> image = docImageMap.get(documentGuid);
		if (uniqueDocumentIds.contains(documentGuid)) {
			// We've already seen this document, it's a duplicate.
			// Generate a new guid for it and add that to the list.
			String uniqueGuid = uuidGenerator.generateUuid();
			LOG.debug("encountered a duplicate uuid [" + docGuid.toString() + "]. Generating a new uuid [" + uniqueGuid
					+ "] and copying the HTML file.");
			uniqueDocumentIds.add(uniqueGuid);
			copyHtmlDocument(documentGuid, uniqueGuid); // copy and rename the
														// html file identified
														// by the GUID listed in
														// the gathered toc.
			orderedDocuments.add(new Doc(uniqueGuid, uniqueGuid + HTML_EXTENSION, titleBreakPart, image));
			currentNode.setDocumentUuid(uniqueGuid);
		} else {
			// Replace docGuid with the corresponding family Guid.
			if (familyGuidMap.containsKey(documentGuid)) {
				docGuid = new StringBuilder();
				String familyGuid = familyGuidMap.get(documentGuid);
				if (uniqueFamilyGuids.contains(familyGuid)) { // Have we already
																// come across
																// this family
																// GUID?
					LOG.debug("Duplicate family GUID " + familyGuid + ", generating new uuid.");
					familyGuid = uuidGenerator.generateUuid();
					orderedDocuments.add(new Doc(familyGuid, familyGuid + HTML_EXTENSION, titleBreakPart, image));
					copyHtmlDocument(documentGuid, familyGuid);
				} else {
					orderedDocuments.add(new Doc(familyGuid, documentGuid + HTML_EXTENSION, titleBreakPart, image));
				}
				currentNode.setDocumentUuid(familyGuid);
				docGuid.append(familyGuid); // perform the replacement
				uniqueFamilyGuids.add(familyGuid);
			} else {
				uniqueDocumentIds.add(docGuid.toString());
				orderedDocuments.add(new Doc(documentGuid, documentGuid + HTML_EXTENSION, titleBreakPart, image));
			}
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
	private void copyHtmlDocument(String sourceDocId, String destDocId) throws SAXException {
		LOG.debug("Copying " + sourceDocId + " to " + destDocId);
		File sourceFile = new File(transformedDocsDir, sourceDocId + HTML_EXTENSION);
		File destFile = new File(transformedDocsDir, destDocId + HTML_EXTENSION);
		try {
			fileUtilsFacade.copyFile(sourceFile, destFile);
		} catch (IOException e) {
			throw new SAXException("Could not make copy of duplicate HTML document referenced in the TOC: "
					+ sourceDocId, e);
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
			super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
			String text = firstTitleBreak.toString();
			// text = StringUtils.substring(text,0,text.length()-1);
			super.characters(text.toCharArray(), 0, text.length());
			super.endElement(URI, TITLE_BREAK, TITLE_BREAK);

			for (TocNode child : tableOfContents.getChildren()) {
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
	private void writeTocNode(TocNode node) throws SAXException {
		//
		if (StringUtils.isNotBlank(node.getTitleBreakString())) {
			super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
			String title = node.getTitleBreakString();
			super.characters(title.toCharArray(), 0, title.length());
			super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
		}
		//
		super.startElement(URI, ENTRY, ENTRY, getAttributes(node));

		super.startElement(URI, TEXT, TEXT, EMPTY_ATTRIBUTES);
		String text = node.getText();
		super.characters(text.toCharArray(), 0, text.length());
		super.endElement(URI, TEXT, TEXT);
		for (TocNode child : node.getChildren()) {
			writeTocNode(child);
		}
		super.endElement(URI, ENTRY, ENTRY);
	}

	/**
	 * Determines which parent to add a node to by comparing the depth of the
	 * last added node to the current depth.
	 * 
	 * @return the parent to which the current node should be added.
	 * @throws SAXException
	 *             if an error occurs.
	 */
	protected TocNode determineParent() throws SAXException {
		if (currentDepth > previousDepth) { // the node we are adding is a
											// child, so add it to the current
											// node.
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
			String message = "Could not determine parent when adding node: " + currentNode;
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
	protected TocNode searchForParentInAncestryOfNode(TocNode node, int desiredDepth) throws SAXException {
		if (node.getDepth() == desiredDepth) {
			// LOG.debug("Found parent in the ancestry: " + node);
			return node;
		} else if (node.getDepth() < desiredDepth) {
			String message = "Failed to identify a parent for node: "
					+ node
					+ " because of possibly bad depth assignment.  This is very likely a programming error in the SplitSplitTitleManifestFilter.";
			LOG.error(message);
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
	 * Writes the documents block to the owning {@link ContentHandler} and ends
	 * the title manifest.
	 * 
	 * @throws SAXException
	 *             if the data could not be written.
	 */
	@Override
	public void endDocument() throws SAXException {
		cascadeAnchors();
		writeTableOfContents();
		super.endElement(URI, TITLE_ELEMENT, TITLE_ELEMENT);
		super.endDocument();
	}

	/**
	 * Writes the root of the title manifest to the configured
	 * {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void startManifest() throws SAXException {
		super.startElement(URI, TITLE_ELEMENT, TITLE_ELEMENT, EMPTY_ATTRIBUTES);
	}

	/**
	 * Returns a list of TOC GUIDs that need to be cascaded into the generated
	 * missing document.
	 * 
	 * @param node
	 *            document level node for which anchors need to be generated
	 * @return list of TOC GUIDs for which Anchors need to be generated
	 */
	protected List<String> getAnchorsToBeGenerated(TocNode node) {
		String docGuid = node.getDocumentGuid();
		List<String> anchors = new ArrayList<String>();
		node = node.getParent();
		while (node != null) {
			if (StringUtils.isBlank(node.getDocumentGuid()) || docGuid.equals(node.getDocumentGuid())) {
				anchors.add(node.getTocGuid());
			} else {
				break;
			}
			node = node.getParent();
		}
		return anchors;
	}

	/**
	 * Pushes document guids up through the ancestry into headings that didn't
	 * contain document references in order to satisfy ProView's TOC to text
	 * linking requirement.
	 * 
	 * @param toc
	 *            the table of contents to operate on.
	 */
	protected void cascadeAnchors() {
		for (TocNode document : nodesContainingDocuments) {
			if (StringUtils.isBlank(document.getParent().getDocumentGuid())) {
				cascadeAnchorUpwards(document.getParent(), document.getDocumentGuid());
			}
		}
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
	protected void cascadeAnchorUpwards(TocNode node, String documentUuid) {
		if (StringUtils.isBlank(node.getDocumentGuid())) {
			node.setDocumentUuid(documentUuid);
			if (node.getParent() != null) {
				cascadeAnchorUpwards(node.getParent(), documentUuid);
			}
		}
	}

	
	
	/**
	 * Writes the ordered list of documents to doc-To-SplitBook.txt
	 * {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if the data could not be written.
	 */
	protected void writeDocumentsToFile() throws SAXException, IOException {
		File docToSplitBookFile = new File(docToSplitBookFileName);
		BufferedWriter writer = null;
		
		try
		{
			writer = new BufferedWriter(new FileWriterWithEncoding(docToSplitBookFile, "UTF-8"));
			
			if (orderedDocuments.size() > 0) {
				for (Doc document : orderedDocuments) {									
					writer.append(document.getId());
					writer.append("|");
					writer.append(document.getSrc());

					if (document.getSplitTitlePart() == 0) {
						document.setSplitTitlePart(1);
					}
					writer.append(String.valueOf(document.getSplitTitlePart()));
					writer.append("|");	
					if (document.getImageIdList().size()>0){
					writer.append("|");
					for(String imgId : document.getImageIdList()){
						writer.append(imgId);
						writer.append(",");
					}
					
					}
					writer.append("\n");
				}				
			}
		}
		catch (IOException e)
		{
			String message = "Could not write out ImageMetadata to following file: " + 
					docToSplitBookFile.getAbsolutePath();
			LOG.error(message);
			throw new IOException();  //EBookFormatException(message, e);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close generated ImageMetadata file.", e);
			}
		}
		
	}
	
	protected List<Doc> getOrderedDocuments(){
		return this.orderedDocuments;
	}
	
	protected void setTableOfContents(final TableOfContents tableOfContents) {
		this.tableOfContents = tableOfContents;
	}

}
