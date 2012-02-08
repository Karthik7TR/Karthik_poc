/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Interceptor that is injected into the ImageVerticalRestTemplate in order to set the
 * Accept header value on the HTTP request to the desired content type for
 * returned images from the Image Vertical REST service.
 */
public class ImageVerticalHttpRequestInterceptor implements
									ClientHttpRequestInterceptor {
	private static Logger log = Logger.getLogger(ImageVerticalHttpRequestInterceptor.class);
	private MediaType desiredImageMediaType;	// like "image/png"
	
	public ImageVerticalHttpRequestInterceptor(MediaType desiredMediaType) {
		this.desiredImageMediaType = desiredMediaType;
	}
	
	/**
	 * Sets the "Accept" header value on the out-bound Image Vertical REST service HTTP request to the media type
	 * of the image we expect to be returned.  Examples: "image/png" or (say) "application/pdf".
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution)
										throws IOException {
		HttpHeaders httpHeaders = httpRequest.getHeaders();
log.debug("Setting HTTP request header: Accept="+desiredImageMediaType);
		httpHeaders.add("Accept", desiredImageMediaType.toString());
		return execution.execute(httpRequest, body);
	}
}
