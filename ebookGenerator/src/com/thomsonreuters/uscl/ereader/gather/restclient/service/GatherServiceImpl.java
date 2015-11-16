/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.restclient.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;

public class GatherServiceImpl implements GatherService {

	public static String TOC_URL_PATTERN = "{context}/toc";
	public static String NORT_URL_PATTERN = "{context}/nort";
	public static String DOC_URL_PATTERN = "{context}/doc";
	public static String IMG_URL_PATTERN = "{context}/img";
	
	private RestTemplate restTemplate;
	private URL gatherAppContextUrl;

	@Override
	public GatherResponse getToc(GatherTocRequest tocRequest) {
		GatherResponse response = restTemplate.postForObject(TOC_URL_PATTERN, tocRequest,
				GatherResponse.class, gatherAppContextUrl.toString());
		return response;	
	}

	@Override
	public GatherResponse getNort(GatherNortRequest nortRequest) {
		GatherResponse response = restTemplate.postForObject(NORT_URL_PATTERN, nortRequest,
				GatherResponse.class, gatherAppContextUrl.toString());
		return response;	
	}
	
	@Override
	public GatherResponse getDoc(GatherDocRequest docRequest) {
		GatherResponse response = restTemplate.postForObject(DOC_URL_PATTERN, docRequest,
				GatherResponse.class, gatherAppContextUrl.toString());
		return response;	
	}
	
	@Override
	public GatherResponse getImg(GatherImgRequest imgRequest) {
		GatherResponse response = restTemplate.postForObject(IMG_URL_PATTERN, imgRequest,
				GatherResponse.class, gatherAppContextUrl.toString());
		return response;	
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
