/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;

/**
 * A custom callback to set the request header to accept application/xml for all Proview REST responses.
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
public class ProviewRequestCallback implements RequestCallback {
	private static final Logger LOG = Logger.getLogger(ProviewRequestCallback.class);
	private static final String ACCEPT_HEADER = "Accept";
	private static final String APPLICATION_XML_MIMETYPE = "application/xml";

	@Override
	public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
		clientHttpRequest.getHeaders().add(ACCEPT_HEADER, APPLICATION_XML_MIMETYPE);
		/* 
		 * TODO: Determine why the Authorization header is not being set/used prior to this point.
		 * Once the root cause is identified remove this workaround. It is possible that registering a callback
		 * with the RestTemplate prevents the underlying, concrete HttpClient headers (if any are present) to be ignored.
		 */
		clientHttpRequest.getHeaders().add("Authorization", "Basic cHVibGlzaGVyOmY5Ul96QnEzN2E=");
	}
}