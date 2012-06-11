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
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.springframework.util.Assert;

/**
 * Serves up configured instances of {@link DefaultHttpClient} with the provided credentials.
 * 
 * 
 * <p>This factory is immutable once created. The {@link AuthScope} is set to {@link AuthScope.ANY_PORT}</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class BasicAuthenticationHttpClientFactory {

	private InetAddress host;
	private String username;
	private String password;
	
	public BasicAuthenticationHttpClientFactory(String hostname,
					String username, String password) throws Exception {
		Assert.isTrue((hostname != null), "hostname must not be null");
		Assert.isTrue(StringUtils.isNotBlank(username), "username must not be null");
		Assert.isTrue(StringUtils.isNotBlank(password), "password must not be null");
		this.host = InetAddress.getByName(hostname);
		this.username = username;
		this.password = password;
	}
	
	public HttpClient getBasicAuthenticationHttpClient(){
		// Get the changeable host name from the configuration
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
		defaultHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(host.getHostName(), AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		return defaultHttpClient;
	}
	
	/**
	 * The allows for the dynamic change of the 
	 * @param host
	 */
	public void setHost(InetAddress host) {
		this.host = host;
	}
}
