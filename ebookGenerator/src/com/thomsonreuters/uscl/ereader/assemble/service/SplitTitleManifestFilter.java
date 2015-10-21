/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.IOException;
import java.net.ContentHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocNode;

public class SplitTitleManifestFilter extends XMLFilterImpl {

	// private static final Logger LOG =
	// Logger.getLogger(SplitTitleManifestFilter.class);
	private TitleMetadata titleMetadata;
	Map<String, String> altIdMap = new HashMap<String, String>();

	private TableOfContents tableOfContents = new TableOfContents();
	// Element names that correspond to the toc.xml to title.xml transformation
	private static final String URI = "";
	private static final String CDATA = "CDATA";
	private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
	private static final String ENTRY = "entry";
	private static final String TEXT = "text";
	private static final String ANCHOR_REFERENCE = "s";

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
	private List<Doc> documentsList;

	public SplitTitleManifestFilter(final TitleMetadata titleMetadata, List<Doc> docList,
			final Map<String, String> altIdMap) {
		if (titleMetadata == null) {
			throw new IllegalArgumentException(
					"Cannot instantiate SplitTitleManifestFilter without initialized TitleMetadata");
		}

		validateTitleMetadata(titleMetadata);
		this.titleMetadata = titleMetadata;
		this.documentsList = docList;

		if (titleMetadata.getIsPilotBook()) {
			this.altIdMap = altIdMap;
		}
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

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (!TITLE_ELEMENT.equals(qName)) {
			super.startElement(uri, localName, qName, atts);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
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
		for (Doc document : this.documentsList) {
			super.startElement(URI, DOC_ELEMENT, DOC_ELEMENT, getAttributes(document));
			super.endElement(URI, DOC_ELEMENT, DOC_ELEMENT);
		}
		super.endElement(URI, DOCS_ELEMENT, DOCS_ELEMENT);
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
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void writeAuthors() throws SAXException {
		super.startElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT, EMPTY_ATTRIBUTES);
		for (String authorName : titleMetadata.getAuthorNames()) {
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
	 * @throws SAXException
	 *             if data could not be written.
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
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void writeKeywords() throws SAXException {
		super.startElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT, EMPTY_ATTRIBUTES);
		for (Keyword keyword : titleMetadata.getKeywords()) {
			super.startElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT, getAttributes(keyword));
			String text = keyword.getText();
			super.characters(text.toCharArray(), 0, text.length());
			super.endElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT);
		}
		super.endElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT);
	}

	/**
	 * Returns a new instance of {@link Attributes} to be used within an asset
	 * element.
	 * 
	 * @param asset
	 *            the asset to create attributes from.
	 * @return the attributes for the asset.
	 */
	protected Attributes getAttributes(Asset asset) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, asset.getId());
		attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, asset.getSrc());
		return attributes;
	}

	/**
	 * Returns a new instance of {@link Attributes} to be used within a keyword
	 * element.
	 * 
	 * @param asset
	 *            the asset to create attributes from.
	 * @return the attributes for the asset.
	 */
	protected Attributes getAttributes(Keyword keyword) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, keyword.getType());
		return attributes;
	}

	/**
	 * Returns a new instance of {@link Attributes} to be used within a doc
	 * element.
	 * 
	 * @param doc
	 *            the doc to create attributes from.
	 * @return the attributes for the doc.
	 * @throws IOException
	 */
	protected Attributes getAttributes(Doc doc) {
		AttributesImpl attributes = new AttributesImpl();
		String docGuid = doc.getId();
		attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, docGuid);

		if (titleMetadata.getIsPilotBook()) {

			String altId = altIdMap.get(docGuid);
			if (altId != null) {
				attributes.addAttribute(URI, ALT_ID_ATTRIBUTE, ALT_ID_ATTRIBUTE, CDATA, altId);
			}
		}

		attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, doc.getSrc());
		return attributes;
	}

	/**
	 * Writes a copyright block to the configured {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void writeCopyright() throws SAXException {
		super.startElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT, EMPTY_ATTRIBUTES);
		String copyright = titleMetadata.getCopyright();
		if (copyright != null) {
			copyright = copyright.replace("\r\n", " ");
		}
		super.characters(copyright.toCharArray(), 0, copyright.length());
		super.endElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT);
	}

	/**
	 * Writes the root of the title manifest to the configured
	 * {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void startManifest() throws SAXException {
		super.startElement(URI, TITLE_ELEMENT, TITLE_ELEMENT, getTitleAttributes());
	}

	/**
	 * Returns the attributes for the title manifest.
	 * 
	 * @return an {@link Attributes} instance.
	 */
	protected Attributes getTitleAttributes() {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, APIVERSION_ATTRIBUTE, APIVERSION_ATTRIBUTE, CDATA, titleMetadata.getApiVersion());
		attributes.addAttribute(URI, TITLEVERSION_ATTRIBUTE, TITLEVERSION_ATTRIBUTE, CDATA,
				titleMetadata.getTitleVersion());
		attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, titleMetadata.getTitleId());
		attributes.addAttribute(URI, LASTUPDATED_ATTRIBUTE, LASTUPDATED_ATTRIBUTE, CDATA,
				titleMetadata.getLastUpdated());
		attributes.addAttribute(URI, LANGUAGE_ATTRIBUTE, LANGUAGE_ATTRIBUTE, CDATA, titleMetadata.getLanguage());
		attributes.addAttribute(URI, STATUS_ATTRIBUTE, STATUS_ATTRIBUTE, CDATA, titleMetadata.getStatus());
		attributes.addAttribute(URI, ONLINEEXPIRATION_ATTRIBUTE, ONLINEEXPIRATION_ATTRIBUTE, CDATA,
				titleMetadata.getOnlineexpiration());
		return attributes;
	}

	/**
	 * Writes the displayname to the configured {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void writeDisplayName() throws SAXException {
		super.startElement(URI, NAME_ELEMENT, NAME_ELEMENT, EMPTY_ATTRIBUTES);
		String displayName = titleMetadata.getDisplayName();
		super.characters(displayName.toCharArray(), 0, displayName.length());
		super.endElement(URI, NAME_ELEMENT, NAME_ELEMENT);

	}

	/**
	 * Writes a features block to the configured {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if data could not be written.
	 */
	protected void writeFeatures() throws SAXException {
		super.startElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT, EMPTY_ATTRIBUTES);
		for (Feature feature : titleMetadata.getProviewFeatures()) {
			super.startElement(URI, FEATURE_ELEMENT, FEATURE_ELEMENT, getAttributes(feature));
			super.endElement(URI, FEATURE_ELEMENT, FEATURE_ELEMENT);
		}
		super.endElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT);

	}

	/**
	 * Returns the attributes associated to a given feature element.
	 * 
	 * @param feature
	 *            the feature whose attributes to get.
	 * @return the attributes of the feature.
	 */
	protected Attributes getAttributes(Feature feature) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, NAME_ELEMENT, NAME_ELEMENT, CDATA, feature.getName());
		if (StringUtils.isNotBlank(feature.getValue())) {
			attributes.addAttribute(URI, VALUE_ATTRIBUTE, VALUE_ATTRIBUTE, CDATA, feature.getValue());
		}
		return attributes;
	}

	/**
	 * Writes a material id block to the configured {@link ContentHandler}.
	 * 
	 * @throws SAXException
	 *             if data could not be written.
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
	 * 
	 * @throws SAXException
	 *             if the data could not be written.
	 */
	protected void writeCoverArt() throws SAXException {
		super.startElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT, getAttributes(titleMetadata.getArtwork()));
		super.endElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT);

	}

	/**
	 * Retrieves the attributes for a given artwork object.
	 * 
	 * @param artwork
	 *            the artwork to create attributes for.
	 * @return the {@link Attributes} for the artwork.
	 */
	protected Attributes getAttributes(Artwork artwork) {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, artwork.getSrc());
		attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, artwork.getType());
		return attributes;
	}

	protected void setTableOfContents(final TableOfContents tableOfContents) {
		this.tableOfContents = tableOfContents;
	}

}
