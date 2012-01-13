/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
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

	private String host;
	private String username;
	private String password;
	
	public BasicAuthenticationHttpClientFactory(String host, String username, String password) {
		Assert.isTrue(StringUtils.isNotBlank(host), "host must not be null");
		Assert.isTrue(StringUtils.isNotBlank(username), "username must not be null");
		Assert.isTrue(StringUtils.isNotBlank(password), "password must not be null");
		this.host = host;
		this.username = username;
		this.password = password;
	}
	
	public HttpClient getBasicAuthenticationHttpClient(){
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(host, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		return defaultHttpClient;
	}
}
