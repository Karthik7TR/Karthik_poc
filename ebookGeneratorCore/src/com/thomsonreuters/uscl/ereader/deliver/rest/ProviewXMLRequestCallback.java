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

@Slf4j
public class ProviewXMLRequestCallback implements RequestCallback {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String HTTP_BASIC_CREDENTIALS = "Basic cHVibGlzaGVyOmY5Ul96QnEzN2E=";
    private InputStream requestInputStream;

    public void setRequestInputStream(final InputStream requestInputStream) {
        this.requestInputStream = requestInputStream;
    }

    @Override
    public void doWithRequest(final ClientHttpRequest clientHttpRequest) throws IOException {
        clientHttpRequest.getHeaders().add("Content-type", MediaType.APPLICATION_XML_VALUE + "; charset=UTF-8");

        clientHttpRequest.getHeaders().add(AUTHORIZATION_HEADER, HTTP_BASIC_CREDENTIALS);
        if (requestInputStream != null) {
            final long startTime = System.currentTimeMillis();
            //IOUtils.copy(requestInputStream, clientHttpRequest.getBody());
            ((StreamingHttpOutputMessage) clientHttpRequest).setBody(new StreamingHttpOutputMessage.Body() {
                @Override
                public void writeTo(final OutputStream outputStream) throws IOException {
                    IOUtils.copy(requestInputStream, outputStream);
                }
            });
            final long duration = System.currentTimeMillis() - startTime;
            log.debug("Created Proview Group in " + duration + " milliseconds.");
        }
        log.debug("ProView URI: " + clientHttpRequest.getURI().getPath());
        log.debug("ProView HTTP Request Headers: " + clientHttpRequest.getHeaders());
    }
}
