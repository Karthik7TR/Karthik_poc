package com.thomsonreuters.uscl.ereader.gather.parsinghandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class DocMetaDataXMLParser extends DefaultHandler {
    private static final Logger LOG = LogManager.getLogger(DocMetaDataXMLParser.class);

    private static final String MD_ROOT_ID = "n-metadata";
    private static final String MD_UUID = "md.uuid";
    private static final String MD_DOC_FAMILY_UUID = "md.doc.family.uuid";
    private static final String MD_NORMALIZED_CITE = "md.normalizedcite";
    private static final String MD_FIRSTLINE_CITE = "md.first.line.cite";
    private static final String MD_SECONDLINE_CITE = "md.second.line.cite";
    private static final String MD_THIRDLINE_CITE = "md.third.line.cite";
    private static final String MD_LEGACY_ID = "md.legacy.id";
    private static final String MD_DMS_SERIAL = "md.dmsserial";
    private static final String MD_DOC_TYPE_NAME = "md.doctype.name";
    private static final String MD_PUB_ID = "md.pubid";
    private static final String MD_PUB_PAGE = "md.pubpage";
    private static final String MD_START_EFFECTIVE = "md.starteffective";
    private static final String MD_END_EFFECTIVE = "md.endeffective";

    public static final String CAN_MD_TOPIC_KEY = "can.md.topic.key";
    private static final String CAN_MD_DIGEST_CLASSIFNUM = "can.md.digest.classifnum";
    private static final String CAN_MD_DIGEST_CLASSIFICATION = "can.md.digest.classification";
    private static final String CAN_MD_DIGEST = "can.md.digest";

    private StringBuffer tempValBuffer;

    // to maintain context
    private DocMetadata docMetadata;
    private String titleId;
    private Long jobInstanceId;
    private String docUuid;
    private String collectionName;
    private boolean processedPubPage;
    private boolean processedFirstPubId;
    private boolean processedSecondPubId;
    private boolean processedThirdPubId;

    private CanadianDigest canadianDigest;

    /**
     * Create factory method to defend against it being created as a Spring bean, which it should not be since it is not thread safe.
     */
    public static DocMetaDataXMLParser create() {
        return new DocMetaDataXMLParser();
    }

    private DocMetaDataXMLParser() {
        docMetadata = new DocMetadata();
    }

    public DocMetadata parseDocument(
        final String titleId,
        final Long jobInstanceId,
        final String collectionName,
        final File metadataFile) throws Exception {
        // get a factory
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        try (InputStream inputStream = new FileInputStream(metadataFile)) {
            // get a new instance of parser
            final SAXParser sp = spf.newSAXParser();

            this.titleId = titleId;
            this.jobInstanceId = jobInstanceId;
            this.collectionName = collectionName;

            final Reader reader = new InputStreamReader(inputStream, "UTF-8");
            final InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            sp.parse(is, this);
            // printData();
        }
        return docMetadata;
    }

    // Event Handlers
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException {
        // reset
        tempValBuffer = new StringBuffer();
        if (qName.equalsIgnoreCase(MD_ROOT_ID)) {
            // create a new instance of doc metadata
            docMetadata = new DocMetadata();
            docMetadata.setTitleId(titleId);
            docMetadata.setJobInstanceId(Long.valueOf(jobInstanceId));
            docMetadata.setDocUuid(docUuid);
            docMetadata.setCollectionName(collectionName);
        } else if (CAN_MD_DIGEST.equalsIgnoreCase(qName)) {
            canadianDigest = new CanadianDigest();
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        tempValBuffer.append(new String(ch, start, length));
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        String tempVal = tempValBuffer.toString();

        if (qName.equalsIgnoreCase(MD_NORMALIZED_CITE)) {
            // add normalized first line cite

            // Fix for Bug 339843 replace a hyphen with a dash
            // so that the normalized cite matches with one form the
            // document
            tempVal = tempVal.replaceAll("\u2013", "\u002D");
            docMetadata.setNormalizedFirstlineCite(tempVal);
        } else if (qName.equalsIgnoreCase(MD_FIRSTLINE_CITE)) {
            docMetadata.setFirstlineCite(tempVal);
        } else if (qName.equalsIgnoreCase(MD_SECONDLINE_CITE)) {
            docMetadata.setSecondlineCite(tempVal);
        } else if (qName.equalsIgnoreCase(MD_THIRDLINE_CITE)) {
            docMetadata.setThirdlineCite(tempVal);
        } else if (qName.equalsIgnoreCase(MD_UUID)) {
            docMetadata.setDocUuid(tempVal);
        } else if (qName.equalsIgnoreCase(MD_DOC_FAMILY_UUID)) {
            docMetadata.setDocFamilyUuid(tempVal);
        } else if (qName.equalsIgnoreCase(MD_DOC_TYPE_NAME)) {
            docMetadata.setDocType(tempVal);
        } else if (qName.equalsIgnoreCase(MD_LEGACY_ID)) {
            docMetadata.setFindOrig(tempVal);
        } else if (qName.equalsIgnoreCase(MD_DMS_SERIAL)) {
            docMetadata.setSerialNumber(Long.valueOf(tempVal));
        } else if (qName.equalsIgnoreCase(MD_PUB_PAGE) && !processedPubPage) {
            final String normalizedPubpage = NormalizationRulesUtil.pubPageNormalizationRules(tempVal);
            docMetadata.setFirstlineCitePubpage(normalizedPubpage);
            processedPubPage = true;
        } else if (qName.equalsIgnoreCase(MD_PUB_ID)) {
            try {
                final Long publicationCode = Long.parseLong(tempVal);

                if (!processedFirstPubId) {
                    docMetadata.setFirstlineCitePubId(publicationCode);
                    processedFirstPubId = true;
                } else if (!processedSecondPubId) {
                    docMetadata.setSecondlineCitePubId(publicationCode);
                    processedSecondPubId = true;
                } else if (!processedThirdPubId) {
                    docMetadata.setThirdlineCitePubId(publicationCode);
                    processedThirdPubId = true;
                }
            } catch (final NumberFormatException e) {
                //not a valid serial number
                LOG.debug("Encountered a publicationCode: " + tempVal + " which is not a valid number.", e);
            }
        } else if (MD_START_EFFECTIVE.equalsIgnoreCase(qName)) {
            docMetadata.setStartEffectiveDate(tempVal);
        } else if (MD_END_EFFECTIVE.equalsIgnoreCase(qName)) {
            docMetadata.setEndEffectiveDate(tempVal);
        } else if (CAN_MD_DIGEST_CLASSIFICATION.equalsIgnoreCase(qName)) {
            canadianDigest.setClassification(tempVal);
        } else if (CAN_MD_DIGEST_CLASSIFNUM.equalsIgnoreCase(qName)) {
            canadianDigest.setClassifnum(tempVal);
        } else if (CAN_MD_DIGEST.equalsIgnoreCase(qName)) {
            docMetadata.addDigest(canadianDigest);
        } else if (CAN_MD_TOPIC_KEY.equalsIgnoreCase(qName)) {
            CanadianTopicCode canadianTopicCode = new CanadianTopicCode();
            canadianTopicCode.setTopicKey(tempVal);
            docMetadata.addTopicCode(canadianTopicCode);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        docMetadata.setLastUpdated(new Date());
    }
}
