package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;

public class ProviewXMLRequestCallback  implements RequestCallback {
	private static final Logger LOG = Logger.getLogger(ProviewXMLRequestCallback.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String HTTP_BASIC_CREDENTIALS = "Basic cHVibGlzaGVyOmY5Ul96QnEzN2E=";
	private InputStream requestInputStream;
	

	public void setRequestInputStream(InputStream requestInputStream) {
		this.requestInputStream = requestInputStream;
	}

	@Override
	public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
		clientHttpRequest.getHeaders().add("Content-type", MediaType.APPLICATION_XML_VALUE);
		
		clientHttpRequest.getHeaders().add(AUTHORIZATION_HEADER, HTTP_BASIC_CREDENTIALS);
		if (requestInputStream != null) {
			long startTime = System.currentTimeMillis();
			//IOUtils.copy(requestInputStream, clientHttpRequest.getBody());		
			((StreamingHttpOutputMessage) clientHttpRequest).setBody(new StreamingHttpOutputMessage.Body() {
			    @Override
			    public void writeTo(final OutputStream outputStream) throws IOException {
			      IOUtils.copy(requestInputStream, outputStream);
			    }
			  });
			long duration = System.currentTimeMillis() - startTime;
			LOG.debug("Created Proview Group in " + duration + " milliseconds.");
		}
		LOG.debug("ProView URI: " + clientHttpRequest.getURI().getPath());
		LOG.debug("ProView HTTP Request Headers: " + clientHttpRequest.getHeaders());
	}
}
