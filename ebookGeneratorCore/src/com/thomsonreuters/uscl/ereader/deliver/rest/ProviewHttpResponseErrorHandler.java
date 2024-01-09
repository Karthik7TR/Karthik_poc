package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Logs error responses from the underlying HTTP client library used by Spring's REST Template.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
@Slf4j
public class ProviewHttpResponseErrorHandler implements ResponseErrorHandler {

    /* (non-Javadoc)
     * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
     */
    @Override
    public void handleError(final ClientHttpResponse clientHttpResponse) throws IOException {
        final String statusCode = clientHttpResponse.getStatusCode().toString();
        final String statusPhrase = clientHttpResponse.getStatusCode().getReasonPhrase();
        final String responseBody = IOUtils.toString(clientHttpResponse.getBody());
        log.error("HTTP HEADERS: " + clientHttpResponse.getHeaders().toString());
        log.error("HTTP STATUS: " + statusCode);
        log.error("HTTP BODY: " + responseBody);
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
    public boolean hasError(final ClientHttpResponse clientHttpResponse) throws IOException {
        if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
            return false;
        } else if (clientHttpResponse.getStatusCode() == HttpStatus.CREATED) {
            return false;
        }
        return true;
    }
}
