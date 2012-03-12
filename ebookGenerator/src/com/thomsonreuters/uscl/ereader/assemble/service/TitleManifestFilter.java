/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Author;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;


/**
 * A SAX event handler responsible for parsing a gathered TOC file and producing a ProView title manifest based on the TOC and {@link TitleMetadata} from the book definition.
 * 
 * <p>Responsible for:
 * <ul>
 * <li>Identifying and resolving duplicate documents. Where dupes are found, a new GUID will be generated. The duplicate HTML file itself will be copied and assigned the new GUID.</li>
 * <li>Generating anchor references that correspond to the document the anchor(s) are contained in.</li>
 * <li>Replacing document guids with family guids.</li>
 * </ul>
 * </p>
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
class TitleManifestFilter extends XMLFilterImpl {

	private static final Logger LOG = Logger.getLogger(TitleManifestFilter.class);
	private FileUtilsFacade fileUtilsFacade;
	private File documentsDirectory;
	private TitleMetadata titleMetadata;
	private UuidGenerator uuidGenerator;
	private Map<String, String> familyGuidMap;
	private Set<String> uniqueDocumentIds = new HashSet<String>();
	private Set<String> uniqueFamilyGuids = new HashSet<String>();
	private List<Doc> orderedDocuments = new ArrayList<Doc>();
	private Set<String> uniqueTocGuids = new HashSet<String>();
	private List<TocEntry> anchorCascadeBuffer = new ArrayList<TocEntry>();
	private StringBuilder tocGuid = new StringBuilder();
	private StringBuilder docGuid = new StringBuilder();
	private StringBuilder textBuffer = new StringBuilder();
	private boolean bufferingTocGuid = false;
	private boolean bufferingDocGuid = false;
	private boolean bufferingText = false;
	private boolean buffersAreFull = false;
	
	//Element names that correspond to the toc.xml to title.xml transformation
	private static final String URI = "";
	private static final String CDATA = "CDATA";
	private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
	private static final String BACKSLASH = "/";
	private static final String EBOOK = "EBook";
	private static final String EBOOK_TOC = "EBookToc";
	private static final String NAME = "Name";
	private static final String TOC_GUID = "Guid";
	private static final String DOCUMENT_GUID = "DocumentGuid";
	private static final String ENTRY = "entry";
	private static final String TEXT = "text";
	private static final String ANCHOR_REFERENCE = "s";
	private static final String HTML_EXTENSION = ".html";
	
	//ProView element and attribute constants
	private static final String ARTWORK_ELEMENT = "artwork";
	private static final String MATERIAL_ELEMENT = "material";
	private static final String VALUE_ATTRIBUTE = "value";
	private static final String FEATURE_ELEMENT = "feature";
	private static final String FEATURES_ELEMENT = "features";
	private static final String NAME_ELEMENT = "name";
	private static final String STATUS_ATTRIBUTE = "status";
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
	
	public TitleManifestFilter(final TitleMetadata titleMetadata, final Map<String, String> familyGuidMap, final UuidGenerator uuidGenerator, final File documentsDirectory, final FileUtilsFacade fileUtilsFacade) {
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
		validateTitleMetadata(titleMetadata);
		this.titleMetadata = titleMetadata;
		this.familyGuidMap = familyGuidMap;
		this.uuidGenerator = uuidGenerator;
		this.documentsDirectory = documentsDirectory;
		this.fileUtilsFacade = fileUtilsFacade;
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

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (EBOOK.equals(qName)){
			super.startElement(URI, TOC_ELEMENT, TOC_ELEMENT, EMPTY_ATTRIBUTES);
		}
		else if (EBOOK_TOC.equals(qName)) { //we've reached the next element, time to add the buffered fields to the TocEntry buffer.
			bufferTocEntryEvent();
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
	}

	/**
	 * Adds a toc entry to the anchorCascadeBuffer.
	 * 
	 * <p>TocEntries added to the anchor cascade buffer will only contain TOC GUIDs as their anchor references at this point.  
	 * They will later be updated with the corresponding doc guid when emitted to the content handler.</p>
	 */
	private void bufferTocEntryEvent(){
		if (buffersAreFull) {
			String anchorReference = tocGuid.toString();
			TocEntry tocEntry = new TocEntry(anchorReference, textBuffer.toString());
			anchorCascadeBuffer.add(tocEntry);
			clearBuffers();
		}
	}

	/**
	 * Resets buffer state to allocate room for the next Toc Entry, if any.
	 * <p><em>Does not reset the docGuid buffer because it may not have been emitted to the Content Handler yet.</em></p>
	 */
	private void clearBuffers() {
		buffersAreFull = Boolean.FALSE;
		tocGuid = new StringBuilder();
		textBuffer = new StringBuilder();
		
	}

	/**
	 * Returns an anchor reference for a given toc entry.
	 * 
	 * @param documentGuid the document uuid to include in the anchor reference.
	 * @param tocEntry the toc entry containing a toc node guid.
	 * @return Attributes the anchor reference in doc_guid/toc_guid format.
	 */
	protected Attributes getAttributes(String documentGuid, TocEntry tocEntry) {
		AttributesImpl attributes = new AttributesImpl();
		String anchorReference = documentGuid + BACKSLASH + tocEntry.getAnchorReference();
		attributes.addAttribute(URI, ANCHOR_REFERENCE, ANCHOR_REFERENCE, CDATA, anchorReference);
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
			uniqueTocGuids.add(tocGuid.toString());
			buffersAreFull = Boolean.TRUE; //Set flag to trigger buffering this TocEntry on next pass.
		}
		else if (DOCUMENT_GUID.equals(qName)) {
			bufferingDocGuid = Boolean.FALSE;
			handleDuplicates();
			bufferTocEntryEvent();
			spewCascadedBufferedEntries(docGuid.toString());
			docGuid = new StringBuilder(); //clear the doc guid buffer independently of the other buffers.
			
		}
		else if (NAME.equals(qName)) {
			bufferingText = Boolean.FALSE;
		}
		else if (EBOOK_TOC.equals(qName)){
			//bufferTocEntryEvent(); //we reached the end of an entry.  If this entry has no children, we need to spew the buffer.
			super.endElement(URI, ENTRY, ENTRY);
		}
		else if (EBOOK.equals(qName)){
			super.endElement(URI, TOC_ELEMENT, TOC_ELEMENT);
		}
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
			orderedDocuments.add(new Doc(uniqueGuid, uniqueGuid + HTML_EXTENSION));
		}
		else {
			//Replace docGuid with the corresponding family Guid.
			if (familyGuidMap.containsKey(documentGuid)) {
				docGuid = new StringBuilder();
				String familyGuid = familyGuidMap.get(documentGuid);
				if (uniqueFamilyGuids.contains(familyGuid)) { //Have we already come across this family GUID?
					LOG.debug("Duplicate family GUID " + familyGuid + ", generating new uuid.");
					familyGuid = uuidGenerator.generateUuid();
					orderedDocuments.add(new Doc(familyGuid, familyGuid + HTML_EXTENSION));
					copyHtmlDocument(documentGuid, familyGuid);
				}
				else {
					orderedDocuments.add(new Doc(familyGuid, documentGuid + HTML_EXTENSION));
				}
				docGuid.append(familyGuid); //perform the replacement
				uniqueFamilyGuids.add(familyGuid);
			}
			else {
				uniqueDocumentIds.add(docGuid.toString());
				orderedDocuments.add(new Doc(documentGuid, documentGuid + HTML_EXTENSION));
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
	 * Emits each buffered toc entry from the anchor cascade buffer to the configured Content Handler.
	 * <p>This method, when used standalone, does <strong>NOT</strong> produce well-formed XML.</p>
	 * 
	 * @param documentGuid the documentGuid to embed into the anchor references.
	 * @throws SAXException if the data could not be written.
	 */
	private void spewCascadedBufferedEntries(String documentGuid) throws SAXException {
		//TODO: If we determine that we need to cascade the TOC GUIDs into the xhtml files here, create a list of the
		//TOC guids and append each entry to it, then inject them into each document via the existing SAX filter.
		for (TocEntry tocEntry : anchorCascadeBuffer) {
			super.startElement(URI, ENTRY, ENTRY, getAttributes(documentGuid, tocEntry));
			super.startElement(URI, TEXT, TEXT, EMPTY_ATTRIBUTES);
			String text = tocEntry.getText();
			super.characters(text.toCharArray(), 0, text.length());
			super.endElement(URI, TEXT, TEXT);
		}
		anchorCascadeBuffer.clear();
	}

	/**
	 * Writes the documents block to the owning Content Handler and ends the title manifest.
	 * 
	 * @throws SAXException if the data could not be written.
	 */
	@Override
	public void endDocument() throws SAXException {
		writeDocuments();
		super.endElement(URI, TITLE_ELEMENT, TITLE_ELEMENT);
		super.endDocument();
	}

	/**
	 * Writes the ordered list of documents to the configured Content Handler.
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
	 * Writes an authors block to the Content Handler.
	 * 
	 * @throws SAXException if data could not be written.
	 */
	protected void writeAuthors() throws SAXException {
		super.startElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT,EMPTY_ATTRIBUTES);
		for (Author author : titleMetadata.getAuthors()){
			super.startElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT, EMPTY_ATTRIBUTES);
			String authorName = author.getName();
			super.characters(authorName.toCharArray(), 0, authorName.length());
			super.endElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT);
		}
		super.endElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT);
	}
	
	/**
	 * Writes an assets block to the Content Handler.
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
	 * Writes an authors block to the Content Handler.
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
	 */	
	protected Attributes getAttributes(Doc doc) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, doc.getId());
		attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, doc.getSrc());
		return attributes;
	}

	/**
	 * Writes a copyright block to the configured Content Handler.
	 * 
	 * @throws SAXException if data could not be written.
	 */
	protected void writeCopyright() throws SAXException {
		super.startElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT, EMPTY_ATTRIBUTES);
		String copyright = titleMetadata.getCopyright();
		super.characters(copyright.toCharArray(), 0, copyright.length());
		super.endElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT);
		
	}

	/**
	 * Writes the root of the title manifest to the configured Content Handler.
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
		return attributes;
	}

	/**
	 * Writes the displayname to the configured content handler.
	 * @throws SAXException if data could not be written.
	 */
	protected void writeDisplayName() throws SAXException {
		super.startElement(URI, NAME_ELEMENT, NAME_ELEMENT, EMPTY_ATTRIBUTES);
		String displayName = titleMetadata.getDisplayName();
		super.characters(displayName.toCharArray(), 0, displayName.length());
		super.endElement(URI,  NAME_ELEMENT, NAME_ELEMENT);
		
	}

	/**
	 * Writes a features block to the configured content handler.
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
	 * Writes a material id block to the configured Content Handler.
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
	 * Writes an artwork element to the configured Content Handler.
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
}
