package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;

/**
 * A custom callback to set the request header to accept application/xml for all Proview REST responses.
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
@Slf4j
public class ProviewRequestCallback implements RequestCallback {
    private static final String ACCEPT_HEADER = "Accept";
    private static final String APPLICATION_XML_MIMETYPE = "application/xml";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String HTTP_BASIC_CREDENTIALS = "Basic cHVibGlzaGVyOmY5Ul96QnEzN2E=";
    private InputStream ebookInputStream;

    public void setEbookInputStream(final InputStream ebookInputStream) {
        this.ebookInputStream = ebookInputStream;
    }

    @Override
    public void doWithRequest(final ClientHttpRequest clientHttpRequest) throws IOException {
        //clientHttpRequest.getHeaders().add(ACCEPT_HEADER, APPLICATION_XML_MIMETYPE);
        clientHttpRequest.getHeaders().add("Content-type", MediaType.APPLICATION_OCTET_STREAM_VALUE);

        /*
         * TODO: Determine why the Authorization header is not being set/used prior to this point.
         * Once the root cause is identified remove this workaround. It is possible that registering a callback
         * with the RestTemplate prevents the underlying, concrete HttpClient headers (if any are present) to be ignored.
         */
        clientHttpRequest.getHeaders().add(AUTHORIZATION_HEADER, HTTP_BASIC_CREDENTIALS);
        if (ebookInputStream != null) {
            final long startTime = System.currentTimeMillis();
            //IOUtils.copy(ebookInputStream, clientHttpRequest.getBody());

            ((StreamingHttpOutputMessage) clientHttpRequest).setBody(new StreamingHttpOutputMessage.Body() {
                @Override
                public void writeTo(final OutputStream outputStream) throws IOException {
                    IOUtils.copy(ebookInputStream, outputStream);
                }
            });

            final long duration = System.currentTimeMillis() - startTime;
            log.debug("Wrote ebook to HTTP Request Body in " + duration + " milliseconds.");
        }
        log.debug("ProView HTTP Request Headers: " + clientHttpRequest.getHeaders());
    }
}
