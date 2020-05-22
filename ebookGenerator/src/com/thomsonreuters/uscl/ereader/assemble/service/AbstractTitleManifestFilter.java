package com.thomsonreuters.uscl.ereader.assemble.service;

import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import com.thomsonreuters.uscl.ereader.proview.InfoField;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.PUBLISHED_DATE_DATEFIELD_NAME;
import static org.apache.commons.lang3.StringUtils.LF;

abstract class AbstractTitleManifestFilter extends AbstractTocManifestFilter {
    protected static final String SRC_ATTRIBUTE = "src";
    protected static final String ID_ATTRIBUTE = "id";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String NAME_ELEMENT = "name";

    private static final String ISBN_ELEMENT = "isbn";
    private static final String STATUS_ATTRIBUTE = "status";
    private static final String ONLINEEXPIRATION_ATTRIBUTE = "onlineexpiration";
    private static final String LANGUAGE_ATTRIBUTE = "language";
    private static final String LASTUPDATED_ATTRIBUTE = "lastupdated";
    private static final String TITLEVERSION_ATTRIBUTE = "titleversion";
    private static final String APIVERSION_ATTRIBUTE = "apiversion";
    private static final String COPYRIGHT_ELEMENT = "copyright";
    private static final String ASSET_ELEMENT = "asset";
    private static final String ASSETS_ELEMENT = "assets";
    private static final String AUTHOR_ELEMENT = "author";
    private static final String AUTHORS_ELEMENT = "authors";
    private static final String KEYWORD_ELEMENT = "keyword";
    private static final String KEYWORDS_ELEMENT = "keywords";
    private static final String ARTWORK_ELEMENT = "artwork";
    private static final String LIBFIELDS_ELEMENT = "libfields";
    private static final String DATEFIELD_ELEMENT = "datefield";
    private static final String DATE_ATTRIBUTE = "date";
    private static final String INFOFIELD_ELEMENT = "infofield";
    private static final String HEADER_ELEMENT = "header";
    private static final String NOTE_ELEMENT = "note";
    private static final String MATERIAL_ELEMENT = "material";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String FEATURE_ELEMENT = "feature";
    private static final String FEATURES_ELEMENT = "features";
    private static final String ALT_ID_ATTRIBUTE = "altid";
    private static final String ENTRY = "entry";
    private static final String TEXT = "text";
    private static final String HTML_LINE_BREAK = "<br/>";
    private static final String CRLF = "\r\n";

    protected TableOfContents tableOfContents = new TableOfContents();
    protected Map<String, String> altIdMap = new HashMap<>();

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        startManifest();
        writeFeatures();
        writeMaterialId();
        writeCoverArt();
        writeAssets();
        writeDisplayName();
        writeLibfields();
        writeAuthors();
        writeKeywords();
        writeCopyright();
    }

    protected void startManifest() throws SAXException {
        super.startElement(URI, TITLE_ELEMENT, TITLE_ELEMENT, getTitleAttributes());
    }

    protected void writeFeatures() throws SAXException {
        super.startElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Feature feature : titleMetadata.getProviewFeatures()) {
            super.startElement(URI, FEATURE_ELEMENT, FEATURE_ELEMENT, getAttributes(feature));
            super.endElement(URI, FEATURE_ELEMENT, FEATURE_ELEMENT);
        }
        super.endElement(URI, FEATURES_ELEMENT, FEATURES_ELEMENT);
    }

    protected Attributes getAttributes(final Feature feature) {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, NAME_ELEMENT, NAME_ELEMENT, CDATA, feature.getName());
        if (StringUtils.isNotBlank(feature.getValue())) {
            attributes.addAttribute(URI, VALUE_ATTRIBUTE, VALUE_ATTRIBUTE, CDATA, feature.getValue());
        }
        return attributes;
    }

    protected void writeMaterialId() throws SAXException {
        super.startElement(URI, MATERIAL_ELEMENT, MATERIAL_ELEMENT, EMPTY_ATTRIBUTES);
        final String materialId = titleMetadata.getMaterialId();
        if (StringUtils.isNotBlank(materialId)) {
            super.characters(materialId.toCharArray(), 0, materialId.length());
        }
        super.endElement(URI, MATERIAL_ELEMENT, MATERIAL_ELEMENT);
    }

    protected void writeCoverArt() throws SAXException {
        super.startElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT, getAttributes(titleMetadata.getArtwork()));
        super.endElement(URI, ARTWORK_ELEMENT, ARTWORK_ELEMENT);
    }

    protected Attributes getAttributes(final Artwork artwork) {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, artwork.getSrc());
        attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, artwork.getType());
        return attributes;
    }

    protected void writeAssets() throws SAXException {
        super.startElement(URI, ASSETS_ELEMENT, ASSETS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Asset asset : titleMetadata.getAssets()) {
            super.startElement(URI, ASSET_ELEMENT, ASSET_ELEMENT, getAttributes(asset));
            super.endElement(URI, ASSET_ELEMENT, ASSET_ELEMENT);
        }
        super.endElement(URI, ASSETS_ELEMENT, ASSETS_ELEMENT);
    }

    protected Attributes getAttributes(final Asset asset) {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, asset.getId());
        attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, asset.getSrc());
        return attributes;
    }

    protected void writeDisplayName() throws SAXException {
        super.startElement(URI, NAME_ELEMENT, NAME_ELEMENT, EMPTY_ATTRIBUTES);
        final String displayName = titleMetadata.getDisplayName();
        super.characters(displayName.toCharArray(), 0, displayName.length());
        super.endElement(URI, NAME_ELEMENT, NAME_ELEMENT);
    }

    protected void writeLibfields() throws SAXException {
        if (isElooseLeafsMetadataExists()) {
            super.startElement(URI, LIBFIELDS_ELEMENT, LIBFIELDS_ELEMENT, EMPTY_ATTRIBUTES);
            writePublishedDate(titleMetadata.getPublishedDate());
            writeInfoFields(titleMetadata.getInfoFields());
            super.endElement(URI, LIBFIELDS_ELEMENT, LIBFIELDS_ELEMENT);
        }
    }

    private boolean isElooseLeafsMetadataExists() {
        List<InfoField> infoFields = titleMetadata.getInfoFields();
        return titleMetadata.isElooseleafsEnabled() && (StringUtils.isNotBlank(titleMetadata.getPublishedDate())
                || CollectionUtils.isNotEmpty(infoFields));
    }

    protected void writePublishedDate(final String publishedDate) throws SAXException {
        if (StringUtils.isNotBlank(publishedDate)) {
            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(URI, DATE_ATTRIBUTE, DATE_ATTRIBUTE, CDATA, publishedDate);
            super.startElement(URI, DATEFIELD_ELEMENT, DATEFIELD_ELEMENT, attributes);
            super.characters(PUBLISHED_DATE_DATEFIELD_NAME.toCharArray(), 0, PUBLISHED_DATE_DATEFIELD_NAME.length());
            super.endElement(URI, DATEFIELD_ELEMENT, DATEFIELD_ELEMENT);
        }
    }

    protected void writeInfoFields(final List<InfoField> infoFields) throws SAXException {
        if (CollectionUtils.isNotEmpty(infoFields)) {
            for (final InfoField infoField : infoFields) {
                super.startElement(URI, INFOFIELD_ELEMENT, INFOFIELD_ELEMENT, EMPTY_ATTRIBUTES);
                writeInfoFieldHeader(infoField.getHeader());
                writeInfoFieldNote(infoField.getNote());
                super.endElement(URI, INFOFIELD_ELEMENT, INFOFIELD_ELEMENT);
            }
        }
    }

    protected void writeInfoFieldHeader(String header) throws SAXException {
        super.startElement(URI, HEADER_ELEMENT, HEADER_ELEMENT, EMPTY_ATTRIBUTES);
        super.characters(header.toCharArray(), 0, header.length());
        super.endElement(URI, HEADER_ELEMENT, HEADER_ELEMENT);
    }

    protected void writeInfoFieldNote(String note) throws SAXException {
        super.startElement(URI, NOTE_ELEMENT, NOTE_ELEMENT, EMPTY_ATTRIBUTES);
        note = note.replaceAll(" +", " ")
                .replace(CRLF, HTML_LINE_BREAK)
                .replace(LF, HTML_LINE_BREAK);
        super.characters(note.toCharArray(), 0, note.length());
        super.endElement(URI, NOTE_ELEMENT, NOTE_ELEMENT);
    }

    protected void writeAuthors() throws SAXException {
        super.startElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final String authorName : titleMetadata.getAuthorNames()) {
            super.startElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT, EMPTY_ATTRIBUTES);
            super.characters(authorName.toCharArray(), 0, authorName.length());
            super.endElement(URI, AUTHOR_ELEMENT, AUTHOR_ELEMENT);
        }
        super.endElement(URI, AUTHORS_ELEMENT, AUTHORS_ELEMENT);
    }

    protected void writeKeywords() throws SAXException {
        super.startElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT, EMPTY_ATTRIBUTES);
        for (final Keyword keyword : titleMetadata.getKeywords()) {
            super.startElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT, getAttributes(keyword));
            final String text = keyword.getText();
            super.characters(text.toCharArray(), 0, text.length());
            super.endElement(URI, KEYWORD_ELEMENT, KEYWORD_ELEMENT);
        }
        super.endElement(URI, KEYWORDS_ELEMENT, KEYWORDS_ELEMENT);
    }

    protected Attributes getAttributes(final Keyword keyword) {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(URI, TYPE_ATTRIBUTE, TYPE_ATTRIBUTE, CDATA, keyword.getType());
        return attributes;
    }

    protected void writeCopyright() throws SAXException {
        String copyright = titleMetadata.getCopyright();
        if (copyright != null) {
            copyright = copyright.replace("\r\n", " ");
            super.startElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT, EMPTY_ATTRIBUTES);
            super.characters(copyright.toCharArray(), 0, copyright.length());
            super.endElement(URI, COPYRIGHT_ELEMENT, COPYRIGHT_ELEMENT);
        }
    }

    protected void writeISBN() throws SAXException {
        super.startElement(URI, ISBN_ELEMENT, ISBN_ELEMENT, EMPTY_ATTRIBUTES);
        super.characters(titleMetadata.getIsbn().toCharArray(), 0, titleMetadata.getIsbn().length());
        super.endElement(URI, ISBN_ELEMENT, ISBN_ELEMENT);
    }

    protected void writeTocNode(final TocNode node) throws SAXException {
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

    protected Attributes getTitleAttributes() {
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

    protected Attributes getAttributes(final Doc doc) {
        final AttributesImpl attributes = new AttributesImpl();
        final String guid = doc.getId();
        attributes.addAttribute(URI, ID_ATTRIBUTE, ID_ATTRIBUTE, CDATA, guid);

        if (titleMetadata.getIsPilotBook()) {
            final String altId = altIdMap.get(guid);
            if (altId != null) {
                attributes.addAttribute(URI, ALT_ID_ATTRIBUTE, ALT_ID_ATTRIBUTE, CDATA, altId);
            }
        }

        attributes.addAttribute(URI, SRC_ATTRIBUTE, SRC_ATTRIBUTE, CDATA, doc.getSrc());
        return attributes;
    }

    protected void setTableOfContents(final TableOfContents tableOfContents) {
        this.tableOfContents = tableOfContents;
    }
}
