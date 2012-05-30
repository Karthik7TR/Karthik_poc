package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.util.CitationNormalizationRulesUtil;


/**
 * 
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class KeyCiteBlockGenerationServiceImpl implements KeyCiteBlockGenerationService
{
    private static final Logger LOG = Logger.getLogger(KeyCiteBlockGenerationServiceImpl.class);
    private DocMetadataService docMetadataService;
    private String hostname;
    private String mudParamRS;
    private String mudParamVR;
    private String KEYCITE_QUERY="/Search/Results.html?query=kc%3A";
    private static String KEYCITE_DOC_PARAM = "/Link/RelatedInformation/Flag?";
    private static String ORIGINATION_CONTEXT ="ebookBuilder";

    @Override
    public InputStream getKeyCiteInfo(String titleId, long jobId, String docGuid)
        throws EBookFormatException
    {
        String url = null;
        DocMetadata docMetadata =
            docMetadataService.findDocMetadataByPrimaryKey(titleId, jobId, docGuid);

        if (docMetadata == null)
        {
            String message =
                "Document metadata could not be found for given guid =" + docGuid
                + " and title Id =" + titleId + " and jobInstanceId =" + jobId;
            LOG.error(message);

            throw new EBookFormatException(message);
        }

        String normalizedCite = docMetadata.getNormalizedFirstlineCite();
        String firstlineCite = docMetadata.getFirstlineCite();
        String secondlineCite = docMetadata.getSecondlineCite();

        if (normalizedCite != null || firstlineCite != null || secondlineCite != null)
        {
        	String query = "";
            try
            {            	
            	if (firstlineCite != null)
            	{
            		query = query + URLEncoder.encode(firstlineCite + ";", "UTF-8");
            	}
            	if (secondlineCite != null)
            	{
            		query = query + URLEncoder.encode(secondlineCite + ";", "UTF-8");
            	}
            	if (normalizedCite != null)
            	{
            		query = query + URLEncoder.encode(normalizedCite + ";", "UTF-8");
            	}
            }
            catch (UnsupportedEncodingException e)
            {
            	String message = "Encountered an Encoding issues while trying to encode the normalized cite for the KC URL.";
            	LOG.error(message, e);
            	throw new EBookFormatException(message, e);
            }
            
            query = CitationNormalizationRulesUtil.applyNormalizationRules(query);
        	
        	url = hostname + KEYCITE_QUERY + query + "&amp;jurisdiction=ALLCASES&amp;contentType=ALL&amp;startIndex=1&amp;transitionType=Search&amp;contextData=(sc.Default)"
        			+ "&amp;rs=" + mudParamRS + "&amp;vr=" + mudParamVR;     
        }
        else
        {
            url = hostname + KEYCITE_DOC_PARAM + "docGuid=" + docMetadata.getDocUuid()
            		+ "&amp;originationContext=" + ORIGINATION_CONTEXT + "&amp;transitionType=NegativeTreatment&amp;contextData=(sc.UserEnteredCitation)&amp;rs=" + mudParamRS
                    + "&amp;vr=" + mudParamVR;
        }

        return buildImageBlock(url);  
    }

    @Required
    public void setDocMetadataService(DocMetadataService docMetadataService)
    {
        this.docMetadataService = docMetadataService;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public void setMudparamrs(String mudParamaRS)
    {
        this.mudParamRS = mudParamaRS;
    }

    public void setMudparamvr(String mudParamVR)
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
    private InputStream buildImageBlock(String URL)
    {
        StringBuffer keyCiteElement = new StringBuffer();
        keyCiteElement.append("<div id=\"ebookGeneratorKeyciteInfo\" class=\"co_flush x_introPara\">");
        keyCiteElement.append("<a href=\"");
        keyCiteElement.append(URL);
        keyCiteElement.append("\">");
        keyCiteElement.append("<img src=\"er:#keycite\" alt=\"KeyCite This Document\"/>");
        keyCiteElement.append("</a>");
        keyCiteElement.append("</div>");

        return  new ByteArrayInputStream(keyCiteElement.toString().getBytes());
    }
}
