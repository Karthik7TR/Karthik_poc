package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

public class SimpleProviewResponseExtractor implements ResponseExtractor<ClientHttpResponse> {
    private static final Logger LOG = LogManager.getLogger(SimpleProviewResponseExtractor.class);

    @Override
    public ClientHttpResponse extractData(final ClientHttpResponse clientHttpResponse) throws IOException {
        final String statusCode = clientHttpResponse.getStatusCode().toString();
        final String statusPhrase = clientHttpResponse.getStatusCode().getReasonPhrase();
        final String responseBody = IOUtils.toString(clientHttpResponse.getBody(), "UTF-8");

        LOG.debug("HTTP HEADERS: " + clientHttpResponse.getHeaders().toString());
        LOG.debug("HTTP STATUS: " + statusCode + ", Reason: " + statusPhrase);
        LOG.debug("HTTP BODY: " + responseBody);

        return clientHttpResponse;
    }
}
