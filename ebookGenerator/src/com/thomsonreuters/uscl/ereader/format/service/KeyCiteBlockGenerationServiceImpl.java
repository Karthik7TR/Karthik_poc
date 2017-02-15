package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class KeyCiteBlockGenerationServiceImpl implements KeyCiteBlockGenerationService
{
    private static final Logger LOG = LogManager.getLogger(KeyCiteBlockGenerationServiceImpl.class);
    private DocMetadataService docMetadataService;
    private String hostname;
    private String mudParamRS;
    private String mudParamVR;
    private String KEYCITE_QUERY = "/Search/Results.html?query=kc%3A";
    private static String KEYCITE_DOC_PARAM = "/Link/RelatedInformation/Flag?";
    private static String ORIGINATION_CONTEXT = "ebookBuilder";

    @Override
    public InputStream getKeyCiteInfo(final String titleId, final long jobId, final String docGuid)
        throws EBookFormatException
    {
        String url = null;
        final DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, jobId, docGuid);

        if (docMetadata == null)
        {
            final String message = "Document metadata could not be found for given guid ="
                + docGuid
                + " and title Id ="
                + titleId
                + " and jobInstanceId ="
                + jobId;
            LOG.error(message);

            throw new EBookFormatException(message);
        }

        final String normalizedCite = docMetadata.getNormalizedFirstlineCite();
        final String firstlineCite = docMetadata.getFirstlineCite();
        final String secondlineCite = docMetadata.getSecondlineCite();

        if (normalizedCite != null || firstlineCite != null || secondlineCite != null)
        {
            final StringBuilder buffer = new StringBuilder();
            try
            {
                if (firstlineCite != null)
                {
                    final String normalizedString = normalizeCitation(firstlineCite);
                    buffer.append(URLEncoder.encode(normalizedString + ";", "UTF-8"));
                }
                if (secondlineCite != null)
                {
                    final String normalizedString = normalizeCitation(secondlineCite);
                    buffer.append(URLEncoder.encode(normalizedString + ";", "UTF-8"));
                }
                if (normalizedCite != null)
                {
                    final String normalizedString = normalizeCitation(normalizedCite);
                    buffer.append(URLEncoder.encode(normalizedString + ";", "UTF-8"));
                }
            }
            catch (final UnsupportedEncodingException e)
            {
                final String message =
                    "Encountered an Encoding issues while trying to encode the normalized cite for the KC URL.";
                LOG.error(message, e);
                throw new EBookFormatException(message, e);
            }

            url = hostname
                + KEYCITE_QUERY
                + buffer.toString()
                + "&amp;jurisdiction=ALLCASES&amp;contentType=ALL&amp;startIndex=1&amp;transitionType=Search&amp;contextData=(sc.Default)"
                + "&amp;rs="
                + mudParamRS
                + "&amp;vr="
                + mudParamVR;
        }
        else
        {
            url = hostname
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

        return buildImageBlock(url);
    }

    private String normalizeCitation(String cite)
    {
        String normalizedCite = "";
        if (StringUtils.isNotBlank(cite))
        {
            cite = cite.replaceAll("\\p{javaSpaceChar}", " ");
            normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(cite);
        }
        return normalizedCite;
    }

    @Required
    public void setDocMetadataService(final DocMetadataService docMetadataService)
    {
        this.docMetadataService = docMetadataService;
    }

    public void setHostname(final String hostname)
    {
        this.hostname = hostname;
    }

    public void setMudparamrs(final String mudParamaRS)
    {
        mudParamRS = mudParamaRS;
    }

    public void setMudparamvr(final String mudParamVR)
    {
        this.mudParamVR = mudParamVR;
    }

    /**
     * Builds Image block using passed in Url info .
     *
     * @param Url
     * @param cite
     *
     * @return
     */
    private InputStream buildImageBlock(final String URL)
    {
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
