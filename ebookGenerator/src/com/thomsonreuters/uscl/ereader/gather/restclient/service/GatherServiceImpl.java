package com.thomsonreuters.uscl.ereader.gather.restclient.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;

public class GatherServiceImpl implements GatherService {

	public static String TOC_URL_PATTERN = "{context}/toc";

	public static String DOC_URL_PATTERN = "{context}/doc";
	
	private RestTemplate restTemplate;
	
	private URL gatherAppContextUrl;

	@Override
	public GatherResponse getToc(GatherTocRequest gatherTocRequest) 
	{
		GatherResponse response = restTemplate.postForObject(TOC_URL_PATTERN,gatherTocRequest,
				GatherResponse.class,gatherAppContextUrl.toString());
		return response;	
	}

	@Override
	public GatherResponse getDoc(GatherDocRequest gatherDocRequest) {
		
		return null;
	}

	@Required
	public void setRestTemplate(RestTemplate restTemplate) {
	
		this.restTemplate = restTemplate;
	}
	
	@Required
	public void setGatherAppContextUrl(URL gatherAppContextUrl) {
		this.gatherAppContextUrl = gatherAppContextUrl;
	}

}
