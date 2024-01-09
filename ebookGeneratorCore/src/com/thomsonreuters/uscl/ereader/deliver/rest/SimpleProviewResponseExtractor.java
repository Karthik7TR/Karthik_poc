package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

@Slf4j
public class SimpleProviewResponseExtractor implements ResponseExtractor<ClientHttpResponse> {

    @Override
    public ClientHttpResponse extractData(final ClientHttpResponse clientHttpResponse) throws IOException {
        final String statusCode = clientHttpResponse.getStatusCode().toString();
        final String statusPhrase = clientHttpResponse.getStatusCode().getReasonPhrase();
        final String responseBody = IOUtils.toString(clientHttpResponse.getBody(), "UTF-8");

        log.debug("HTTP HEADERS: " + clientHttpResponse.getHeaders().toString());
        log.debug("HTTP STATUS: " + statusCode + ", Reason: " + statusPhrase);
        log.debug("HTTP BODY: " + responseBody);

        return clientHttpResponse;
    }
}
