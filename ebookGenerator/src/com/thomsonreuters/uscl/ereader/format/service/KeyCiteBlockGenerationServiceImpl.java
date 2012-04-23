package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;


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
    private String KEYCITE_QUERY="/Search/Results.html?query=KC%3A%20";
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

        if (normalizedCite != null)
        {
        	normalizedCite = normalizedCite.replace(String.valueOf((char) 0xA7),"s"); // TODO  need to check other special characters
        	
        	url = hostname + KEYCITE_QUERY + normalizedCite + "&amp;jurisdiction=ALLCASES&amp;contentType=ALL&amp;startIndex=1&amp;transitionType=Search&amp;contextData=(sc.Default)"
        			+ "&amp;rs=" + mudParamRS + "&amp;vr=" + mudParamVR  
        			+ "&amp;searchPostBody=%7B%7D&amp;searchUri=%2FSearch%2Fv3%2Fsearch%2Fstart%3Fjurisdiction%3DALLCASES%26" 
        			+ "query%3DKC%253A%2520" + normalizedCite;            
       
        }
        else
        {
            url = hostname + KEYCITE_DOC_PARAM + "docGuid=" + docMetadata.getDocUuid()
                + "&amp;originationContext= " + ORIGINATION_CONTEXT + "&amp;transitionType=NegativeTreatment&amp;contextData=%28sc.UserEnteredCitation%29" + "&amp;rs=" + mudParamRS
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
        keyCiteElement.append("<img src=\"er:#keyCite\" alt=\"KeyCite This Document\"/>");
        keyCiteElement.append("</a>");
        keyCiteElement.append("</div>");

        return  new ByteArrayInputStream(keyCiteElement.toString().getBytes());
    }
}
