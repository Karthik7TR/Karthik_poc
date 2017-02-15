package com.thomsonreuters.uscl.ereader.gather.restclient.service;

import java.net.URL;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

public class GatherServiceImpl implements GatherService
{
    public static final String TOC_URL_PATTERN = "{context}/toc";
    public static final String NORT_URL_PATTERN = "{context}/nort";
    public static final String DOC_URL_PATTERN = "{context}/doc";
    public static final String IMG_URL_PATTERN = "{context}/img";

    private RestTemplate restTemplate;
    private URL gatherAppContextUrl;

    @Override
    public GatherResponse getToc(final GatherTocRequest tocRequest)
    {
        final GatherResponse response = restTemplate
            .postForObject(TOC_URL_PATTERN, tocRequest, GatherResponse.class, gatherAppContextUrl.toString());
        return response;
    }

    @Override
    public GatherResponse getNort(final GatherNortRequest nortRequest)
    {
        final GatherResponse response = restTemplate
            .postForObject(NORT_URL_PATTERN, nortRequest, GatherResponse.class, gatherAppContextUrl.toString());
        return response;
    }

    @Override
    public GatherResponse getDoc(final GatherDocRequest docRequest)
    {
        final GatherResponse response = restTemplate
            .postForObject(DOC_URL_PATTERN, docRequest, GatherResponse.class, gatherAppContextUrl.toString());
        return response;
    }

    @Override
    public GatherResponse getImg(final GatherImgRequest imgRequest)
    {
        final GatherResponse response = restTemplate
            .postForObject(IMG_URL_PATTERN, imgRequest, GatherResponse.class, gatherAppContextUrl.toString());
        return response;
    }

    @Required
    public void setRestTemplate(final RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    @Required
    public void setGatherAppContextUrl(final URL gatherAppContextUrl)
    {
        this.gatherAppContextUrl = gatherAppContextUrl;
    }
}
