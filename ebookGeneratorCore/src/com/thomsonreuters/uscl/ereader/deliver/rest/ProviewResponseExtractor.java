/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

/**
 * This class returns the response body from ProView as a unicode string.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
public class ProviewResponseExtractor implements ResponseExtractor<String> {

	@Override
	public String extractData(ClientHttpResponse response)
			throws IOException {
		return IOUtils.toString(response.getBody(), "UTF-8");
	}

}
