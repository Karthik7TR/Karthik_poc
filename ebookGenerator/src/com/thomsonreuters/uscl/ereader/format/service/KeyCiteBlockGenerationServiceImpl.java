package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */

@Slf4j
@Setter
public class KeyCiteBlockGenerationServiceImpl implements KeyCiteBlockGenerationService {
    private DocMetadataService docMetadataService;
    private CitationNormalizer citationNormalizer;
    private String hostname;
    private String mudParamRS;
    private String mudParamVR;
    private static String KEYCITE_QUERY = "/Search/Results.html?query=kc%3A";
    private static String KEYCITE_DOC_PARAM = "/Link/RelatedInformation/Flag?";
    private static String ORIGINATION_CONTEXT = "ebookBuilder";

    @Override
    public InputStream getKeyCiteInfo(final String titleId, final long jobId, final String docGuid)
        throws EBookFormatException {
        final DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, jobId, docGuid);

        if (docMetadata == null) {
            final String message = "Document metadata could not be found for given guid ="
                + docGuid
                + " and title Id ="
                + titleId
                + " and jobInstanceId ="
                + jobId;
            log.error(message);

            throw new EBookFormatException(message);
        }

        final String url = Optional.ofNullable(getUrl(docMetadata))
                .orElse(getDefaultUrl(docMetadata));

        return buildImageBlock(url);
    }

    private String getDefaultUrl(final DocMetadata docMetadata) {
        return hostname
                + KEYCITE_DOC_PARAM
                + "docGuid="
                + docMetadata.getDocUuid()
                + "&amp;originationContext="
                + ORIGINATION_CONTEXT
                + "&amp;transitionType=NegativeTreatment&amp;contextData=(sc.UserEnteredCitation)&amp;rs="
                + mudParamRS
                + "&amp;vr="
                + mudParamVR;
    }

    private String getUrl(final DocMetadata docMetadata) throws EBookFormatException {
        final String normalizedCite = docMetadata.getNormalizedFirstlineCite();
        final String firstlineCite = docMetadata.getFirstlineCite();
        final String secondlineCite = docMetadata.getSecondlineCite();

        if (normalizedCite == null || firstlineCite == null || secondlineCite == null) {
            return null;
        }

        try {
            final String query = Stream.of(firstlineCite, secondlineCite, normalizedCite)
                    .map(citationNormalizer::normalizeCitation)
                    .map(this::encode)
                    .collect(Collectors.joining());
            return hostname
                    + KEYCITE_QUERY
                    + query
                    + "&amp;jurisdiction=ALLCASES&amp;contentType=ALL&amp;startIndex=1&amp;transitionType=Search&amp;contextData=(sc.Default)"
                    + "&amp;rs="
                    + mudParamRS
                    + "&amp;vr="
                    + mudParamVR;
        } catch (final RuntimeException e) {
            final String message =
                    "Encountered an Encoding issues while trying to encode the normalized cite for the KC URL.";
            log.error(message, e);
            throw new EBookFormatException(message, e);
        }
    }

    private String encode(final String cite) {
        try {
            return URLEncoder.encode(cite + ";", "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds Image block using passed in Url info .
     *
     * @param Url
     *
     * @return
     */
    private InputStream buildImageBlock(final String URL) {
        final StringBuffer keyCiteElement = new StringBuffer();
        keyCiteElement.append("<div id=\"ebookGeneratorKeyciteInfo\" class=\"co_flush x_introPara\">");
        keyCiteElement.append("<a href=\"");
        keyCiteElement.append(URL);
        keyCiteElement.append("\">");
        keyCiteElement.append("<img src=\"er:#keycite\" alt=\"KeyCite This Document\"/>");
        keyCiteElement.append("</a>");
        keyCiteElement.append("</div>");

        return new ByteArrayInputStream(keyCiteElement.toString().getBytes());
    }
}
