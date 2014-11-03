/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.net.InetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.Assert;

/**
 * Serves up configured instances of {@link CloseableHttpClient} with the provided credentials.
 * 
 * 
 * <p>This factory is immutable once created. The {@link AuthScope} is set to {@link AuthScope.ANY_PORT}</p>
 * 
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 *
 */
public class CloseableAuthenticationHttpClientFactory {

	private InetAddress host;
	private String username;
	private String password;
	
	public CloseableAuthenticationHttpClientFactory(String hostname,
					String username, String password) throws Exception {
		Assert.isTrue((hostname != null), "hostname must not be null");
		Assert.isTrue(StringUtils.isNotBlank(username), "username must not be null");
		Assert.isTrue(StringUtils.isNotBlank(password), "password must not be null");
		this.host = InetAddress.getByName(hostname);
		this.username = username;
		this.password = password;
	}
	
	public HttpClient getCloseableAuthenticationHttpClient(){
		// Get the changeable host name from the configuration
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(host.getHostName(), AuthScope.ANY_PORT), 
				new UsernamePasswordCredentials(username, password));

		CloseableHttpClient  closeableHttpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credentialsProvider).build();

		return closeableHttpClient;
	}
	
	/**
	 * The allows for the dynamic change of the 
	 * @param host
	 */
	public void setHost(InetAddress host) {
		this.host = host;
	}
}
