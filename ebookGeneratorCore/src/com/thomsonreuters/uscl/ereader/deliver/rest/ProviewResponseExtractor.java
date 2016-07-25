/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

/**
 * A ResponseExtractor for ProView responses.  Returns the response body as a String to be interpreted by the caller.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
public class ProviewResponseExtractor implements ResponseExtractor<String> {
	private static final Logger LOG = LogManager.getLogger(ProviewResponseExtractor.class);
	
	/**
	 * Logs the ProView HTTP response for publishing operations and returns the response body as a unicode string.
	 */
	@Override
	public String extractData(ClientHttpResponse clientHttpResponse) throws IOException {
		String statusCode = clientHttpResponse.getStatusCode().toString();
		String statusPhrase = clientHttpResponse.getStatusCode().getReasonPhrase();
		String responseBody = IOUtils.toString(clientHttpResponse.getBody(), "UTF-8");
		
		LOG.debug("HTTP HEADERS: " + clientHttpResponse.getHeaders().toString());
		LOG.debug("HTTP STATUS: " + statusCode + ", Reason: " + statusPhrase);
		LOG.debug("HTTP BODY: " + responseBody);
		
		return responseBody;
	}
}
