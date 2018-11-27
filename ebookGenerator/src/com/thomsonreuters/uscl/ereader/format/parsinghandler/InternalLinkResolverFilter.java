package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.format.FormatConstants;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import com.thomsonreuters.uscl.ereader.util.UrlParsingUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * A SAX Filter that detects whether or not an anchor points to a resource included within an
 * eBook.<p>If there is a match, the URL is converted to ProView format &lt;a
 * href="er:#id"&gt;anchor text&lt;/a&gt;.</p>
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class InternalLinkResolverFilter extends XMLFilterImpl {
    private static final Logger LOG = LogManager.getLogger(InternalLinkResolverFilter.class);
    private static final String ANCHOR_ELEMENT = "a";
    private static final String HREF = "href";
    private static final Long PUB_NOT_PRESENT = Long.MIN_VALUE;
    private DocumentMetadataAuthority documentMetadataAuthority;
    private File docsGuidFile;
    private PaceMetadataService paceMetadataService;
    private Long jobId;
    private String docGuid;
    private String version;

    public InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority) {
        this.documentMetadataAuthority = Optional.ofNullable(documentMetadataAuthority)
            .orElseThrow(() -> new IllegalArgumentException(
                "Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority"));
    }

    public InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority,
                                      final File docsGuidFile,
                                      final PaceMetadataService paceMetadataService,
                                      final Long jobId,
                                      final String docGuid,
                                      final String version) {
        this(documentMetadataAuthority);
        this.docsGuidFile = docsGuidFile;
        this.paceMetadataService = paceMetadataService;
        this.jobId = jobId;
        this.docGuid = docGuid;
        this.version = version;
    }

    @Override
    public void startElement(final String uri, final String localname, final String qName, final Attributes attributes)
        throws SAXException {
        final String resourceUrl = attributes.getValue(HREF);

        if (StringUtils.isNotEmpty(resourceUrl) && resourceUrl.contains("http") && ANCHOR_ELEMENT.equals(qName)) {
            final Map<String, String> urlContents = UrlParsingUtil.parseUrlContents(resourceUrl);
            final Attributes resolvedAttributes = resolveResourceUrlReference(urlContents, attributes);
            super.startElement(uri, localname, qName, resolvedAttributes);
        } else {
            super.startElement(uri, localname, qName, attributes);
        }
    }

    /**
     *
     * @param urlContents
     *
     * @return
     */
    private DocMetadata getDocMetadata(final Map<String, String> urlContents) {
        DocMetadata docMetadata = null;
        final String cite = urlContents.get("cite");
        final String documentUuid = urlContents.get("documentUuid");
        final String serialNum = urlContents.get("serNum");

        if (StringUtils.isNotEmpty(cite)) {
            final String pubName = urlContents.get("pubNum");
            Long pubId = PUB_NOT_PRESENT;
            if (pubName != null && !pubName.trim().isEmpty()) {
                try {
                    pubId = Long.parseLong(pubName.trim());
                } catch (final NumberFormatException nfe) {
                    //not a valid serial number
                    LOG.debug("Encountered a pubName: " + pubName + " which is not a valid number.", nfe);
                }
            }

            docMetadata = getNormalizedCiteDocMetadata(cite, pubId, jobId);
        } else if (StringUtils.isNotEmpty(documentUuid)) {
            docMetadata = documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid().get(documentUuid);
        } else if (StringUtils.isNotEmpty(serialNum)) {
            try {
                final Long serNum = Long.valueOf(serialNum.trim());

                docMetadata = documentMetadataAuthority.getDocMetadataKeyedBySerialNumber().get(serNum);
            } catch (final NumberFormatException nfe) {
                //not a valid serial number
                LOG.debug("Encountered a serNum: " + serialNum + " which is not a valid number.", nfe);
            }
        }

        return docMetadata;
    }

    /**
     *
     * @param cite
     * @param pubId
     *
     * @return docMetadata DocumentMetadata
     */
    private DocMetadata getNormalizedCiteDocMetadata(final String cite, final Long pubId, final Long jobId) {
        DocMetadata docMetadata = Optional.ofNullable(documentMetadataAuthority.getDocMetadataByCite(cite, docGuid))
            .orElseGet(() -> documentMetadataAuthority.getDocMetadataByCite(cite.replaceAll("\\s", ""), docGuid));

        if (docMetadata == null) {
            if (!pubId.equals(PUB_NOT_PRESENT)) {
                final List<PaceMetadata> paceMetadataInfo = paceMetadataService.findAllPaceMetadataForPubCode(pubId);

                if (paceMetadataInfo != null && !paceMetadataInfo.isEmpty()) {
                    final PaceMetadata paceData = paceMetadataInfo.get(0);
                    final String stdPubName = paceData.getStdPubName();

                    if (cite.contains(stdPubName)) {
                        String pubNameCite = cite.replace(stdPubName, paceData.getPublicationName());
                        pubNameCite = NormalizationRulesUtil.applyCitationNormalizationRules(pubNameCite);
                        docMetadata = documentMetadataAuthority.getDocMetadataByCite(pubNameCite, docGuid);

                        if (docMetadata == null) {
                            // look for pubId and pubpage match (this will fix multiple volumes) Bug #33426
                            final String[] splitCite = cite.split(stdPubName);

                            if (splitCite.length > 0) {
                                String pubpage = splitCite[splitCite.length - 1];
                                pubpage = NormalizationRulesUtil.pubPageNormalizationRules(pubpage);
                                docMetadata =
                                    documentMetadataAuthority.getDocMetadataKeyedByPubIdAndPubPage().get(pubId + pubpage);
                            }
                        }
                    }
                }
            } else {
                final String normilizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(cite);
                if (normilizedCite.startsWith("LK(")) {
                    docMetadata = getRefsAnnosPage(normilizedCite);
                }
            }
        }
        return docMetadata;
    }

    /**
     *
     * @param docGuid
     *
     * @return tocGuid TocGuid as a string for corresponding document guid.
     *
     * @throws EBookFormatException
     */
    private String getTitleXMLTOCFilter(final String docGuid) {
        String tocGuid = "";

        if ((docsGuidFile == null) || !docsGuidFile.exists()) {
            throw new IllegalArgumentException(
                "File passed into InternalLinkResolverFilter constructor must be a valid file.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(docsGuidFile))) {
            String input = reader.readLine();

            while (input != null) {
                final String[] line = input.split(",", -1);

                if (line[0].equals(docGuid)) {
                    final String[] tocGuids = line[1].split("\\|");
                    tocGuid = tocGuids[0];

                    break;
                }

                input = reader.readLine();
            }

            if ("".equals(tocGuid)) {
                final String message = "Please verify that each document GUID in the following file has "
                    + "at least one TOC guid associated with it: "
                    + docsGuidFile.getAbsolutePath();
                LOG.error(message);
            }
        } catch (final IOException e) {
            final String message =
                "Could not read the DOC guid to TOC guid map file: " + docsGuidFile.getAbsolutePath();
            LOG.error(message, e);
        }

        return tocGuid;
    }

    /**
     * Resolves a normalized cite reference to a WLN resource to an internal book
     * reference, provided it exists in the normalizedCiteAuthority.
     *
     * @param urlContents the normalizedCite to match against the keys in the
     *        normalizedCiteAuthority.
     * @param attributes
     *
     * @return the updated URL in ProView format.
     */
    private Attributes resolveResourceUrlReference(final Map<String, String> urlContents, final Attributes attributes)
        throws SAXException {
        final DocMetadata docMetadata = getDocMetadata(urlContents);

        final AttributesImpl resolvedAttributes = new AttributesImpl(attributes);

        if (docMetadata == null) {
            return resolvedAttributes;
        }
        //For Rutter titles external link should be changed to internal links
        //RefType TS is specific to Rutter titles.
        //We are adding RefType TS as an attribute here to avoid modification to href value containing _sp_ in HTMLAnchorFilter
        final String refType = urlContents.get("refType");
        final String externalLinkClass = resolvedAttributes.getValue("class");
        if (refType != null
            && refType.equalsIgnoreCase("TS")
            && externalLinkClass.equalsIgnoreCase("co_link co_drag ui-draggable")) {
            final int classIndex = resolvedAttributes.getIndex("class");
            resolvedAttributes.setValue(classIndex, "co_internalLink");
            resolvedAttributes.addAttribute("", "refType", "refType", "CDATA", "TS");
        }

        final StringBuilder ebookResourceIdentifier = new StringBuilder();

        final String splitTitleId =
            documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid().get(docGuid).getSplitBookTitle();

        if (splitTitleId != null
            && docMetadata.getSplitBookTitle() != null
            && !splitTitleId.equalsIgnoreCase(docMetadata.getSplitBookTitle())) {
            ebookResourceIdentifier.append(docMetadata.getSplitBookTitle() + "/v" + version);
        }

        ebookResourceIdentifier.append("#");
        ebookResourceIdentifier.append(docMetadata.getProViewId());

        final String reference = urlContents.get("reference");

        if (StringUtils.isNotEmpty(reference)) {
            ebookResourceIdentifier.append("/" + reference);
        } else {
            final String tocGuid = getTitleXMLTOCFilter(docMetadata.getDocUuid());

            if (StringUtils.isEmpty(tocGuid)) {
                throw new SAXException("Could not find TOC guid for " + docMetadata.getDocUuid() + " document.");
            }

            ebookResourceIdentifier.append("/" + tocGuid);
        }

        final int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
        resolvedAttributes.setValue(
            anchorReferenceIndex,
            FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT + ebookResourceIdentifier); //add the new value

        return resolvedAttributes;
    }

    /**
     * Attempts to locate the page of a Refs and Annos citations that is in one of the following formats:
     * lk(TXPRD)+lk(TXPRR)+lk(TXAGD)+lk(TXALD)+lk(TXBCD)+...
     * lk(TXPRD) lk(TXPRR) lk(TXAGD) lk(TXALD) lk(TXBCD)
     *
     * @param cite Refs and Annos citation
     *
     * @return null if document is not identified in book otherwise the document metadata object of the Refs and Annos page.
     */
    private DocMetadata getRefsAnnosPage(final String cite) {
        DocMetadata docMetadata = null;

        final String[] citations = cite.split("LK\\(");

        //try and find the correct reference page from the list of citations
        for (String citation : citations) {
            citation = citation.replace(")", "").replace("+", "").trim();

            if (citation.endsWith("R")) {
                //Only look at the citations that end with R, presumably stands for References
                docMetadata = documentMetadataAuthority.getDocMetadataByCite(citation, docGuid);

                if (docMetadata != null) {
                    return docMetadata;
                }
            }
        }

        //if a reference page cannot be located, locate to the first found document in the cite list
        for (String citation : citations) {
            citation = citation.replace(")", "").replace("+", "").trim();

            docMetadata = documentMetadataAuthority.getDocMetadataByCite(citation, docGuid);

            if (docMetadata != null) {
                return docMetadata;
            }
        }

        return docMetadata;
    }
}
