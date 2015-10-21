/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
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


/**
 * A SAX event handler responsible for parsing a gathered TOC (an XML document) and producing a ProView title manifest based on the TOC and {@link TitleMetadata} from the book definition.
 * 
 * <p><strong>Implementation Comments:</strong> This approach has a number of merits, but also a number of recognized limitations.  All of the responsibility for making the input data (the TOC and associated book metadata) conform to ProView format is done in one place (DRY).
 * It is also somewhat complex, requiring some level of familiarity with SAX filters and event-based document parsing.  The implementation is broken up into chunks with the bulk of the heavy lifting performed in the startElement, characters, and endElement methods.  The primary motivation to put a lot of the logic around conforming to ProView was to prevent these details leaking to the rest of the application and maintain encapsulation in the assembly and delivery steps.
 * </p>
 * 
 * <p><strong>Current limitations:</strong> ProView requires us to provide unique IDs for each document/asset/etc in the book. This filter tracks document and family identifiers that it has encountered during the TOC parse in two {@link Set}s.  If a collision occurs when adding the next guid to one of the sets, a new guid is generated and added to the {@link Set}.
 * A side-effect of this approach is that all notes and bookmarks associated to a family guid will "flow upwards" in the TOC.  If multiple documents within a table of contents share the same family guid, the first one is the only one that ships to ProView.  Notes and bookmarks taken against the second and further document(s) with duplicate family guids will become displaced when the book is published again (because we generated new identifiers to conform to the uniqueness requirement).</p>
 * 
 * <p><strong>General Responsibilities:</strong>
 * <ul>
 * <li>Identifying and resolving duplicate documents. Where dupes are found, a new GUID will be generated. The duplicate HTML file itself will be copied and assigned the new GUID.</li>
 * <li>Generating anchor references that correspond to the document the anchor(s) are contained in. This process is termed "Cascading" or "Anchor Mapping" or "Cascading Anchors".</li>
 * <li>Replacing document guids with family guids. It does so in order to facilitate transfer of notes and bookmarks between subsequent publishes of the a document that shares a consistent identifier even though its text may have changed (in the case of Statutes and other regularly-updated material).</li>
 * <li>Invoking the PlaceholderDocumentService to create text in cases where there is no text for headings that follow the last document in the table of contents.</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
class TitleManifestFilter extends XMLFilterImpl {


	private static final String DEFAULT_PUBLISHING_INFORMATION = "PUBLISHING INFORMATION";
	private static final Logger LOG = Logger.getLogger(TitleManifestFilter.class);
	private FileUtilsFacade fileUtilsFacade;
	private PlaceholderDocumentService placeholderDocumentService;
	private File documentsDirectory;
	private TitleMetadata titleMetadata;
	private UuidGenerator uuidGenerator;
	Map<String,String> altIdMap = new HashMap<String,String>();
		
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
	
	//Element names that correspond to the toc.xml to title.xml transformation
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
	
	//Front Matter Text
	private static final String TITLE_PAGE = "Title Page";
	private static final String COPYRIGHT_PAGE = "Copyright Page";
	private static final String ADDITIONAL_INFORMATION_OR_RESEARCH_ASSISTANCE = "Additional Information or Research Assistance";
	private static final String WESTLAW_NEXT = "WestlawNext";

	//ProView element and attribute constants
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
	
	
	public TitleManifestFilter(final TitleMetadata titleMetadata, final Map<String, String> familyGuidMap, final UuidGenerator uuidGenerator, final File documentsDirectory, final FileUtilsFacade fileUtilsFacade, final PlaceholderDocumentService placeholderDocumentService, final Map<String,String> altIdMap) {
		if (titleMetadata == null) {
			throw new IllegalArgumentException("Cannot instantiate TitleManifestFilter without initialized TitleMetadata");
		}
		if (familyGuidMap == null) {
			throw new IllegalArgumentException("Cannot instantiate TitleManifestFilter without a valid familyGuidMap");
		}
		if (uuidGenerator == null) {
			throw new IllegalArgumentException("Cannot instantiate TitleManifestFilter without a UuidGenerator.");
		}
		if (documentsDirectory == null || !documentsDirectory.isDirectory()) {
			throw new IllegalArgumentException("Documents directory must not be null and must be a directory.");
		}
		if (fileUtilsFacade == null) {
			throw new IllegalArgumentException("fileUtilsFacade must not be null.");
		}
		if (placeholderDocumentService == null){
			throw new IllegalArgumentException("placeholderDocumentService must not be null.");
		}
		
		
		validateTitleMetadata(titleMetadata);
		this.titleMetadata = titleMetadata;
		this.familyGuidMap = familyGuidMap;
		this.uuidGenerator = uuidGenerator;
		this.documentsDirectory = documentsDirectory;
		this.fileUtilsFacade = fileUtilsFacade;
		this.placeholderDocumentService = placeholderDocumentService;
		this.previousNode = tableOfContents;
				
		if (titleMetadata.getIsPilotBook())
		{
			this.altIdMap = altIdMap;			
		}
	}
	
	
	/**
	 * Responsible for validating the title metadata is complete prior to generating the manifest.
	 * @param metadata the title metadata.
	 */
	private void validateTitleMetadata(TitleMetadata metadata) {
		//TODO: assert additional invariants based on required fields in the manifest.
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		//write everything in the manifest up to the TOC and DOCs sections.
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
	
	public void buildFrontMatterTOCEntries() throws SAXException
	{
		currentDepth++;
		currentNode = new TocEntry(currentDepth);
		//Use ebook_definition.front_matter_toc_label
		if (titleMetadata.getFrontMatterTocLabel() != null)
		{		
			currentNode.setText(titleMetadata.getFrontMatterTocLabel());
		}
		else
		{		
			currentNode.setText(DEFAULT_PUBLISHING_INFORMATION);
		}
		currentNode.setTocNodeUuid(FrontMatterFileName.PUBLISHING_INFORMATION+FrontMatterFileName.ANCHOR);
		TocNode parentNode = determineParent();
		currentNode.setParent(parentNode); // previousNode
		parentNode.addChild(currentNode);
		previousDepth = currentDepth;
		previousNode = currentNode;

		currentDepth++;
		createFrontMatterNode(FrontMatterFileName.FRONT_MATTER_TITLE, TITLE_PAGE);

		createFrontMatterNode(FrontMatterFileName.COPYRIGHT, COPYRIGHT_PAGE);
		
		List<FrontMatterPage> frontMatterPagesList =  titleMetadata.getFrontMatterPages();

		if (frontMatterPagesList != null)
		{
			// loop through additional frontMatter. PDFs will be taken care of in the assets.
			for (FrontMatterPage front_matter_page : frontMatterPagesList)
			{
				createFrontMatterNode(FrontMatterFileName.ADDITIONAL_FRONT_MATTER + front_matter_page.getId(), front_matter_page.getPageTocLabel());
			}
		}
		
		
		createFrontMatterNode(FrontMatterFileName.RESEARCH_ASSISTANCE, ADDITIONAL_INFORMATION_OR_RESEARCH_ASSISTANCE);
		
		createFrontMatterNode(FrontMatterFileName.WESTLAWNEXT, WESTLAW_NEXT);
		
		currentDepth--;
		currentDepth--;
	}


	public void createFrontMatterNode(String guidBase, String nodeText) throws SAXException
	{
		currentNode = new TocEntry(currentDepth);
		currentNode.setDocumentUuid(guidBase);
		currentNode.setText(nodeText);
		currentNode.setTocNodeUuid(guidBase+FrontMatterFileName.ANCHOR);
		TocNode parentNode = determineParent();
		currentNode.setParent(parentNode); 
		parentNode.addChild(currentNode);
		previousDepth = currentDepth;
		previousNode = currentNode;
		orderedDocuments.add(new Doc(guidBase, guidBase+HTML_EXTENSION, 0, null));
		nodesContainingDocuments.add(currentNode);  
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (EBOOK.equals(qName)){
			currentDepth = 0;
			previousDepth = 0;
			// Add TOC NODES for Front Matter
			buildFrontMatterTOCEntries();
		}
		else if (EBOOK_TOC.equals(qName)) { //we've reached the next element, time to add a new node to the tree.
			currentDepth++;
			currentNode = new TocEntry(currentDepth);
			TocNode parentNode = determineParent();
			currentNode.setParent(parentNode);
			parentNode.addChild(currentNode);
			previousDepth = currentDepth;
			previousNode = currentNode;
		}
		else if (TOC_GUID.equals(qName)){
			bufferingTocGuid = Boolean.TRUE;
		}
		else if (DOCUMENT_GUID.equals(qName)){
			bufferingDocGuid = Boolean.TRUE;
		}
		else if (NAME.equals(qName)) {
			bufferingText = Boolean.TRUE;
		}
		else if (MISSING_DOCUMENT.equals(qName)) {
			//this node is missing text, generate a new doc guid and xhtml5 content for the heading.
			String missingDocumentGuid = uuidGenerator.generateUuid();
			String missingDocumentFilename = missingDocumentGuid + HTML_EXTENSION;
			File missingDocument = new File(documentsDirectory, missingDocumentFilename);
			try {
				FileOutputStream missingDocumentOutputStream = new FileOutputStream(missingDocument);
				currentNode.setDocumentUuid(missingDocumentGuid);
				cascadeAnchors();
				List<String> anchors = getAnchorsToBeGenerated(currentNode);
				placeholderDocumentService.generatePlaceholderDocument(new EntityDecodedOutputStream(missingDocumentOutputStream), currentNode.getText(), currentNode.getTocGuid(), anchors);
				orderedDocuments.add(new Doc(missingDocumentGuid, missingDocumentFilename, 0, null));
				nodesContainingDocuments.add(currentNode);  //need to cascade anchors into placeholder document text.
			}
			catch (FileNotFoundException e) {
				throw new SAXException("A FileNotFoundException occurred when attempting to create an output stream to file: " + missingDocument.getAbsolutePath(), e);
			}
			catch (PlaceholderDocumentServiceException e) {
				throw new SAXException("An unexpected error occurred while generating a placeholder document for Toc Node: [" + tocGuid.toString() + "] while producing the title manifest.", e);
			}
		}
	}

	/**
	 * Returns an anchor reference for a given toc node.
	 * 
	 * @param documentGuid the document uuid to include in the anchor reference.
	 * @param tocEntry the toc entry containing a toc node guid.
	 * @return Attributes the anchor reference in doc_guid/toc_guid format.
	 */
	protected Attributes getAttributes(TocNode tocNode) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, ANCHOR_REFERENCE, ANCHOR_REFERENCE, CDATA,  tocNode.getAnchorReference());
		return attributes;
	}

	/**
	 * Buffers toc and doc guids and the corresponding text for each node encountered during the parse.
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (bufferingTocGuid) {
			tocGuid.append(ch, start, length);
		}
		else if (bufferingDocGuid) {
			docGuid.append(ch, start, length);
		}
		else if (bufferingText) {
			textBuffer.append(ch, start, length);
		}		
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (TOC_GUID.equals(qName)) {
			bufferingTocGuid = Boolean.FALSE;
			currentNode.setTocNodeUuid(tocGuid.toString());
			tocGuid = new StringBuilder();
		}
		else if (DOCUMENT_GUID.equals(qName)) {
			bufferingDocGuid = Boolean.FALSE;
			handleDuplicates();
			currentNode.setDocumentUuid(docGuid.toString());
			nodesContainingDocuments.add(currentNode);
			docGuid = new StringBuilder(); //clear the doc guid buffer independently of the other buffers.
		}
		else if (NAME.equals(qName)) {
			bufferingText = Boolean.FALSE;
			currentNode.setText(textBuffer.toString());
			textBuffer = new StringBuilder();
		}
		else if (EBOOK_TOC.equals(qName)){
			currentDepth--;
		}
		else if (MISSING_DOCUMENT.equals(qName)){
			//The missing document end element event is eaten, because it isn't used.			
		}
		//other elements are also eaten (there shouldn't be any).
	}

	/**
	 * Handles all the decision making related to document or family identifier collisions.
	 * 
	 * <p>Delegates to another service to copy html documents.</p>
	 */
	private void handleDuplicates() throws SAXException {
		String documentGuid = docGuid.toString();
		if (uniqueDocumentIds.contains(documentGuid)) {
			// We've already seen this document, it's a duplicate.
			// Generate a new guid for it and add that to the list.
			String uniqueGuid = uuidGenerator.generateUuid();
			LOG.debug("encountered a duplicate uuid [" + docGuid.toString() + "]. Generating a new uuid [" + uniqueGuid + "] and copying the HTML file.");
			uniqueDocumentIds.add(uniqueGuid);
			copyHtmlDocument(documentGuid, uniqueGuid); //copy and rename the html file identified by the GUID listed in the gathered toc.
			orderedDocuments.add(new Doc(uniqueGuid, uniqueGuid + HTML_EXTENSION, 0, null));
			currentNode.setDocumentUuid(uniqueGuid);
		}
		else {
			//Replace docGuid with the corresponding family Guid.
			if (familyGuidMap.containsKey(documentGuid)) {
				docGuid = new StringBuilder();
				String familyGuid = familyGuidMap.get(documentGuid);
				if (uniqueFamilyGuids.contains(familyGuid)) { //Have we already come across this family GUID?
					LOG.debug("Duplicate family GUID " + familyGuid + ", generating new uuid.");
					familyGuid = uuidGenerator.generateUuid();
					orderedDocuments.add(new Doc(familyGuid, familyGuid + HTML_EXTENSION, 0, null));
					copyHtmlDocument(documentGuid, familyGuid);
				}
				else {
					orderedDocuments.add(new Doc(familyGuid, documentGuid + HTML_EXTENSION, 0, null));
				}
				currentNode.setDocumentUuid(familyGuid);
				docGuid.append(familyGuid); //perform the replacement
				uniqueFamilyGuids.add(familyGuid);
			}
			else {
				uniqueDocumentIds.add(docGuid.toString());
				orderedDocuments.add(new Doc(documentGuid, documentGuid + HTML_EXTENSION, 0, null));
			}
		}
	}

	/**
	 * Duplicates the corresponding document in order to conform to ProView structural requirements.
	 * 
	 * @param sourceDocId the file to be copied.
	 * @param destDocId the "new" copy of the file.
	 */
	private void copyHtmlDocument(String sourceDocId, String destDocId) throws SAXException {
		LOG.debug("Copying " + sourceDocId + " to " + destDocId);
		File sourceFile = new File(documentsDirectory, sourceDocId + HTML_EXTENSION);
		File destFile = new File(documentsDirectory, destDocId + HTML_EXTENSION);
		try {
			fileUtilsFacade.copyFile(sourceFile, destFile);
		} catch (IOException e) {
			throw new SAXException("Could not make copy of duplicate HTML document referenced in the TOC: " + sourceDocId, e);
		}
	}
	
	/**
	 * Emits the table of contents to the configured {@link ContentHandler}.
	 * 
	 * @throws SAXException the data could not be written.
	 */
	protected void writeTableOfContents() throws SAXException {
		if (tableOfContents.getChildren().size() > 0){
			super.startElement(URI, TOC_ELEMENT, TOC_ELEMENT, EMPTY_ATTRIBUTES);
			for (TocNode child : tableOfContents.getChildren()){
				writeTocNode(child);
			}
			super.endElement(URI, TOC_ELEMENT, TOC_ELEMENT);
		}
	}
	
	/**
	 * Writes an individual toc node.
	 * @throws SAXException
	 */
	private void writeTocNode(TocNode node) throws SAXException {
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
	 * Determines which parent to add a node to by comparing the depth of the last added node to the current depth.
	 * 
	 * @return the parent to which the current node should be added.
	 * @throws SAXException if an error occurs.
	 */
	protected TocNode determineParent() throws SAXException {
		if (currentDepth > previousDepth) { //the node we are adding is a child, so add it to the current node.
//			LOG.debug("Determined parent of " + currentNode + " to be " + previousNode);
			return previousNode;
		}
		else if ( currentDepth == previousDepth ) {
//			LOG.debug("Determined parent of " + currentNode + " to be " + previousNode.getParent());
			return previousNode.getParent();
		}
		else if (currentDepth < previousDepth) {
			return searchForParentInAncestryOfNode(previousNode, currentDepth - 1);
		}
		else {
			String message = "Could not determine parent when adding node: " + currentNode;
			LOG.error(message);
			throw new SAXException(message);
		}
	}
	
	/**
	 * Travels up a node's ancestry until reaching the desired depth.
	 * 
	 * @param node the node whose ancestry to interrogate.
	 * @param desiredDepth the depth to terminate the search. This could eventually be changed, if needed, to be a node ID if all nodes know who their immediate parent is (by identifier).
	 */
	protected TocNode searchForParentInAncestryOfNode(TocNode node, int desiredDepth) throws SAXException {
		if (node.getDepth() == desiredDepth) {
//			LOG.debug("Found parent in the ancestry: " + node);
			return node;
		}
		else if (node.getDepth() < desiredDepth) {
			String message = "Failed to identify a parent for node: " + node + " because of possibly bad depth assignment.  This is very likely a programming error in the TitleManifestFilter.";
			LOG.error(message);
			throw new SAXException(message); //could not find the parent, so bail.  This may need to be an exception case because it means that the way we are assigning depth to nodes is probably not implemented correctly.
		}
		else {
			return searchForParentInAncestryOfNode(node.getParent(), desiredDepth);
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
		writeDocuments();
		writeISBN();
		super.endElement(URI, TITLE_ELEMENT, TITLE_ELEMENT);
		super.endDocument();
	}

	/**
	 * Returns a list of TOC GUIDs that need to be cascaded into the generated
	 * missing document. 
	 * 
	 * @param node document level node for which anchors need to be generated
	 * @return list of TOC GUIDs for which Anchors need to be generated
	 */
	protected List<String> getAnchorsToBeGenerated(TocNode node)
	{
		String docGuid = node.getDocumentGuid();
		List<String> anchors = new ArrayList<String>();
		node = node.getParent();
		while(node != null)
		{
			if (StringUtils.isBlank(node.getDocumentGuid()) || docGuid.equals(node.getDocumentGuid()))
			{
				anchors.add(node.getTocGuid());
			}
			else
			{
				break;
			}
			node = node.getParent();
		}
		return anchors;
	}
	
	/**
	 * Pushes document guids up through the ancestry into headings that didn't contain document references in order to satisfy ProView's TOC to text linking requirement.
	 * 
	 * @param toc the table of contents to operate on.
	 */
	protected void cascadeAnchors() {
		for (TocNode document : nodesContainingDocuments) {
			if (StringUtils.isBlank(document.getParent().getDocumentGuid())) {
				cascadeAnchorUpwards(document.getParent(), document.getDocumentGuid());
			}
		}
	}
	
	/**
	 * Assigns a document uuid to this node and cascades recursively up the ancestry until a null node is found.
	 * 
	 * Exit conditions: stops cascading upwards if a document uuid exists in a parent or if a node does not have a parent.
	 * 
	 * @param node the current node to be interrogated.
	 * @param documentUuid the document uuid to assign to the node if, and only if, it doesn't have one already.
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
	 * Writes the ordered list of documents to the configured {@link ContentHandler}.
	 * 
	 * @throws SAXException if the data could not be written.
	 */
	protected void writeDocuments() throws SAXException {
		if (orderedDocuments.size() > 0) {
			super.startElement(URI, DOCS_ELEMENT, DOCS_ELEMENT, EMPTY_ATTRIBUTES);
			for (Doc document : orderedDocuments) {
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
	protected void writeISBN() throws SAXException {
			super.startElement(URI, ISBN_ELEMENT, ISBN_ELEMENT, EMPTY_ATTRIBUTES);
			super.characters(titleMetadata.getIsbn().toCharArray(), 0, titleMetadata.getIsbn().length());
			super.endElement(URI, ISBN_ELEMENT, ISBN_ELEMENT);
	}

	/**
	 * Writes an authors block to the {@link ContentHandler}.
	 * 
	 * @throws SAXException if data could not be written.
	 */
	protected void writeAuthors() throws SAXException {
		super.startElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT,EMPTY_ATTRIBUTES);
		for (String authorName : titleMetadata.getAuthorNames()){
			super.startElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT, EMPTY_ATTRIBUTES);
			super.characters(authorName.toCharArray(), 0, authorName.length());
			super.endElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT);
		}
		//if (titleMetadata.getAuthors().size() == 0)
		super.endElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT);
	}
	
	/**
	 * Writes an assets block to the {@link ContentHandler}.
	 * 
	 * @throws SAXException if data could not be written.
	 */
	protected void writeAssets() throws SAXException {
		super.startElement(URI, ASSETS_ELEMENT, ASSETS_ELEMENT, EMPTY_ATTRIBUTES);
		for (Asset asset : titleMetadata.getAssets()) {
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
	protected void writeKeywords() throws SAXException {
		super.startElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT,EMPTY_ATTRIBUTES);
		for (Keyword keyword : titleMetadata.getKeywords()){
			super.startElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT, getAttributes(keyword));
			String text = keyword.getText();
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
	protected Attributes getAttributes(Asset asset) {
		AttributesImpl attributes = new AttributesImpl();
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
	protected Attributes getAttributes(Keyword keyword) {
		AttributesImpl attributes = new AttributesImpl();
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
	protected Attributes getAttributes(Doc doc) {
		AttributesImpl attributes = new AttributesImpl();
		String docGuid = doc.getId();
		attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, docGuid);
				
		if (titleMetadata.getIsPilotBook())
		{
    
		    String altId = altIdMap.get(docGuid);
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
	protected void writeCopyright() throws SAXException {
		super.startElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT, EMPTY_ATTRIBUTES);
		String copyright = titleMetadata.getCopyright();
		if (copyright!=null)
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
	protected void startManifest() throws SAXException {
		super.startElement(URI, TITLE_ELEMENT, TITLE_ELEMENT, getTitleAttributes());
	}

	/**
	 * Returns the attributes for the title manifest.
	 * @return an {@link Attributes} instance.
	 */
	protected Attributes getTitleAttributes() {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, APIVERSION_ATTRIBUTE, APIVERSION_ATTRIBUTE, CDATA, titleMetadata.getApiVersion());
		attributes.addAttribute(URI, TITLEVERSION_ATTRIBUTE, TITLEVERSION_ATTRIBUTE, CDATA, titleMetadata.getTitleVersion());
		attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, titleMetadata.getTitleId());
		attributes.addAttribute(URI, LASTUPDATED_ATTRIBUTE, LASTUPDATED_ATTRIBUTE, CDATA, titleMetadata.getLastUpdated());
		attributes.addAttribute(URI, LANGUAGE_ATTRIBUTE, LANGUAGE_ATTRIBUTE, CDATA, titleMetadata.getLanguage());
		attributes.addAttribute(URI, STATUS_ATTRIBUTE, STATUS_ATTRIBUTE, CDATA, titleMetadata.getStatus());
		attributes.addAttribute(URI, ONLINEEXPIRATION_ATTRIBUTE, ONLINEEXPIRATION_ATTRIBUTE, CDATA, titleMetadata.getOnlineexpiration());		
		return attributes;
	}

	/**
	 * Writes the displayname to the configured {@link ContentHandler}.
	 * @throws SAXException if data could not be written.
	 */
	protected void writeDisplayName() throws SAXException {
		super.startElement(URI, NAME_ELEMENT, NAME_ELEMENT, EMPTY_ATTRIBUTES);
		String displayName = titleMetadata.getDisplayName();
		super.characters(displayName.toCharArray(), 0, displayName.length());
		super.endElement(URI,  NAME_ELEMENT, NAME_ELEMENT);
		
	}

	/**
	 * Writes a features block to the configured {@link ContentHandler}.
	 * @throws SAXException if data could not be written.
	 */
	protected void writeFeatures() throws SAXException {
		super.startElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT, EMPTY_ATTRIBUTES);
		for (Feature feature : titleMetadata.getProviewFeatures()){
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
	protected Attributes getAttributes(Feature feature) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, NAME_ELEMENT, NAME_ELEMENT, CDATA, feature.getName());
		if (StringUtils.isNotBlank(feature.getValue())){
			attributes.addAttribute(URI, VALUE_ATTRIBUTE, VALUE_ATTRIBUTE, CDATA, feature.getValue());
		}
		return attributes;
	}

	/**
	 * Writes a material id block to the configured {@link ContentHandler}.
	 * @throws SAXException if data could not be written.
	 */
	protected void writeMaterialId() throws SAXException {
		super.startElement(URI, MATERIAL_ELEMENT, MATERIAL_ELEMENT, EMPTY_ATTRIBUTES);
		String materialId = titleMetadata.getMaterialId();
		if (StringUtils.isNotBlank(materialId)) {
			super.characters(materialId.toCharArray(), 0, materialId.length());
		}
		super.endElement(URI, MATERIAL_ELEMENT, MATERIAL_ELEMENT);
	}

	/**
	 * Writes an artwork element to the configured {@link ContentHandler}.
	 * @throws SAXException if the data could not be written.
	 */
	protected void writeCoverArt() throws SAXException {
		super.startElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT, getAttributes(titleMetadata.getArtwork()));
		super.endElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT);
		
	}

	/**
	 * Retrieves the attributes for a given artwork object.
	 * @param artwork the artwork to create attributes for.
	 * @return the {@link Attributes} for the artwork. 
	 */
	protected Attributes getAttributes(Artwork artwork) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, artwork.getSrc());
		attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, artwork.getType());
		return attributes;
	}
	
	protected void setTableOfContents(final TableOfContents tableOfContents){
		this.tableOfContents = tableOfContents;
	}
	
}
