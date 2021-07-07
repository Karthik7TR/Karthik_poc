package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.FormatConstants;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import com.thomsonreuters.uscl.ereader.util.UrlParsingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class InternalLinkResolverService {
    private static final String HREF = "href";
    private static final Long PUB_NOT_PRESENT = Long.MIN_VALUE;
    private static final String LK = "LK(";
    private static final String TS = "TS";
    private static final String CLASS = "class";
    private static final String LINK_CLASSES = "co_link co_drag ui-draggable";
    private static final String REF_TYPE = "refType";
    private static final String CO_INTERNAL_LINK = "co_internalLink";
    private static final String CDATA = "CDATA";

    @Autowired
    private PaceMetadataService paceMetadataService;

    /**
     * Resolves a normalized cite reference to a WLN resource to an internal book
     * reference, provided it exists in the normalizedCiteAuthority.
     *
     * @param resourceUrl needs to contain the normalizedCite to match against the keys in the
     *        normalizedCiteAuthority.
     * @param attributes
     *
     * @return the updated URL in ProView format.
     */
    public Attributes resolveResourceUrlReference(final DocumentMetadataAuthority documentMetadataAuthority,
                                                  final String resourceUrl,
                                                  final Attributes attributes,
                                                  final File docsGuidFile,
                                                  final String docGuid,
                                                  final String version) {
        final Map<String, String> urlContents = UrlParsingUtil.parseUrlContents(resourceUrl);
        final String ebookResourceIdentifier = resolveResourceIdentifier(documentMetadataAuthority, docsGuidFile, docGuid, version, urlContents);
        final AttributesImpl resolvedAttributes = new AttributesImpl(attributes);

        if (ebookResourceIdentifier == null) {
            return resolvedAttributes;
        }
        //For Rutter titles external link should be changed to internal links
        //RefType TS is specific to Rutter titles.
        //We are adding RefType TS as an attribute here to avoid modification to href value containing _sp_ in HTMLAnchorFilter
        final String refType = urlContents.get(REF_TYPE);
        final String externalLinkClass = resolvedAttributes.getValue(CLASS);
        if (refType != null
                && refType.equalsIgnoreCase(TS)
                && externalLinkClass.equalsIgnoreCase(LINK_CLASSES)) {
            final int classIndex = resolvedAttributes.getIndex(CLASS);
            resolvedAttributes.setValue(classIndex, CO_INTERNAL_LINK);
            resolvedAttributes.addAttribute(StringUtils.EMPTY, REF_TYPE, REF_TYPE, CDATA, TS);
        }

        final int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
        resolvedAttributes.setValue(
                anchorReferenceIndex,
                ebookResourceIdentifier); //add the new value

        return resolvedAttributes;
    }

    private String resolveResourceIdentifier(final DocumentMetadataAuthority documentMetadataAuthority,
                                                    final File docsGuidFile,
                                                    final String docGuid,
                                                    final String version,
                                                    final Map<String, String> urlContents) {
        final DocMetadata docMetadata = getDocMetadata(documentMetadataAuthority, urlContents, docGuid);
        if (docMetadata == null) {
            return null;
        }

        final StringBuilder ebookResourceIdentifier = new StringBuilder();

        final String splitTitleId = Optional.ofNullable(documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid().get(docGuid))
                .map(DocMetadata::getSplitBookTitle).orElse(null);

        if (docMetadata.getSplitBookTitle() != null
                && !docMetadata.getSplitBookTitle().equalsIgnoreCase(splitTitleId)) {
            ebookResourceIdentifier.append(docMetadata.getSplitBookTitle() + "/v" + version);
        }

        ebookResourceIdentifier.append("#");
        ebookResourceIdentifier.append(docMetadata.getProViewId());

        final String reference = urlContents.get("reference");

        if (StringUtils.isNotEmpty(reference)) {
            ebookResourceIdentifier.append("/" + reference);
        } else {
            final String tocGuid = getTitleXMLTOCFilter(docsGuidFile, docMetadata.getDocUuid());

            if (StringUtils.isEmpty(tocGuid)) {
                throw new EBookException("Could not find TOC guid for " + docMetadata.getDocUuid() + " document.");
            }

            ebookResourceIdentifier.append("/" + tocGuid);
        }
        return FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT + ebookResourceIdentifier;
    }

    private DocMetadata getDocMetadata(final DocumentMetadataAuthority documentMetadataAuthority,
                                       final Map<String, String> urlContents, final String docGuid) {
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
                    log.debug("Encountered a pubName: " + pubName + " which is not a valid number.", nfe);
                }
            }

            docMetadata = getNormalizedCiteDocMetadata(documentMetadataAuthority, cite, pubId, docGuid);
        } else if (StringUtils.isNotEmpty(documentUuid)) {
            docMetadata = documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid().get(documentUuid);
        } else if (StringUtils.isNotEmpty(serialNum)) {
            try {
                final Long serNum = Long.valueOf(serialNum.trim());

                docMetadata = documentMetadataAuthority.getDocMetadataKeyedBySerialNumber().get(serNum);
            } catch (final NumberFormatException nfe) {
                //not a valid serial number
                log.debug("Encountered a serNum: " + serialNum + " which is not a valid number.", nfe);
            }
        }

        return docMetadata;
    }

    /**
     * @param docGuid
     *
     * @return tocGuid TocGuid as a string for corresponding document guid.
     */
    private String getTitleXMLTOCFilter(final File docsGuidFile, final String docGuid) {
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
                log.error(message);
            }
        } catch (final IOException e) {
            final String message =
                    "Could not read the DOC guid to TOC guid map file: " + docsGuidFile.getAbsolutePath();
            log.error(message, e);
        }

        return tocGuid;
    }

    private DocMetadata getNormalizedCiteDocMetadata(final DocumentMetadataAuthority documentMetadataAuthority,
                                                     final String cite, final Long pubId, final String docGuid) {
        DocMetadata docMetadata = documentMetadataAuthority.getDocMetadataByCite(cite, docGuid);

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
                if (normilizedCite.startsWith(LK)) {
                    docMetadata = getRefsAnnosPage(documentMetadataAuthority, normilizedCite, docGuid);
                }
            }
        }
        return docMetadata;
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
    private DocMetadata getRefsAnnosPage(final DocumentMetadataAuthority documentMetadataAuthority,
                                         final String cite, final String docGuid) {
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
