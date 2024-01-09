package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

/**
 * A ResponseExtractor for ProView responses.  Returns the response body as a String to be interpreted by the caller.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
@Slf4j
public class ProviewResponseExtractor implements ResponseExtractor<String> {
    private static final int MAX_RESPONSE_LENGTH = 10000;

    /**
     * Logs the ProView HTTP response for publishing operations and returns the response body as a unicode string.
     */
    @Override
    public String extractData(final ClientHttpResponse clientHttpResponse) throws IOException {
        final String statusCode = clientHttpResponse.getStatusCode().toString();
        final String statusPhrase = clientHttpResponse.getStatusCode().getReasonPhrase();
        final String responseBody = IOUtils.toString(clientHttpResponse.getBody(), "UTF-8");

        log.debug("HTTP HEADERS: " + clientHttpResponse.getHeaders().toString());
        log.debug("HTTP STATUS: " + statusCode + ", Reason: " + statusPhrase);
        if (responseBody.length() < MAX_RESPONSE_LENGTH) {
            log.debug("HTTP BODY: " + responseBody);
        } else {
            log.debug("HTTP BODY: response is too long to log");
        }

        return responseBody;
    }
}
