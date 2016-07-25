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
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;

/**
 * Logs error responses from the underlying HTTP client library used by Spring's REST Template.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class ProviewHttpResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger LOG = LogManager.getLogger(ProviewHttpResponseErrorHandler.class);
	
	/* (non-Javadoc)
	 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
	 */
	@Override
	public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
		String statusCode = clientHttpResponse.getStatusCode().toString();
		String statusPhrase = clientHttpResponse.getStatusCode().getReasonPhrase();
		String responseBody = IOUtils.toString(clientHttpResponse.getBody());
		LOG.error("HTTP HEADERS: " + clientHttpResponse.getHeaders().toString());
		LOG.error("HTTP STATUS: " + statusCode);
		LOG.error("HTTP BODY: " + responseBody);		
		throw new ProviewRuntimeException(statusCode, responseBody);
	}

	/**
	 * If we receive a non-200 response then the ResponseHandler
	 * <p>Eventually we will want to get
	 * fancier about how we go about determining error status.  For example, ProView could respond with an HTTP 200 OK, but
	 * send us a warning message that we deem should halt our publishing process.</p>
	 * 
	 * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.springframework.http.client.ClientHttpResponse)
	 */
	@Override
	public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
		if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
			return false;
		} else if (clientHttpResponse.getStatusCode() == HttpStatus.CREATED) {
			return false;
		}
		return true;
	}

}
