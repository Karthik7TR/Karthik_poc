package com.thomsonreuters.uscl.ereader.proview.rest;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.AbstractMiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.rest.BasicAuthenticationHttpClientFactory;

public class BasicAuthenticationHttpClientFactoryTest {

	private BasicAuthenticationHttpClientFactory httpClientFactory;
	private InetAddress mockHost;
	
	@Before
	public void setUp() throws Exception {
		mockHost = InetAddress.getLocalHost();
		httpClientFactory = new BasicAuthenticationHttpClientFactory(mockHost.getHostName(), "captain", "j4ck_sp4rr0w");
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
//	@Test
//	public void testFactoryIsCorrectlyConfiguredUponInstantiation() throws Exception {
//		DefaultHttpClient httpClient = (DefaultHttpClient) httpClientFactory.getBasicAuthenticationHttpClient();
//		Credentials credentials = httpClient.getCredentialsProvider().getCredentials(new AuthScope("pirate.com", AuthScope.ANY_PORT));
//		assertTrue("Configured password should match, but didn't.", credentials.getPassword().equals("j4ck_sp4rr0w"));
//	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenHostIsNull() throws Exception {
		BasicAuthenticationHttpClientFactory httpClientFactory = new BasicAuthenticationHttpClientFactory(null, "captain", "j4ck_sp4rr0w");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenUsernameIsNull() throws Exception {
		BasicAuthenticationHttpClientFactory httpClientFactory = new BasicAuthenticationHttpClientFactory(mockHost.getHostName(), null, "j4ck_sp4rr0w");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenUsernameIsBlank() throws Exception {
		BasicAuthenticationHttpClientFactory httpClientFactory = new BasicAuthenticationHttpClientFactory(mockHost.getHostName(), "", "j4ck_sp4rr0w");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenPasswordIsNull() throws Exception {
		BasicAuthenticationHttpClientFactory httpClientFactory = new BasicAuthenticationHttpClientFactory(mockHost.getHostName(), "captain", null);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenPasswordIsBlank() throws Exception {
		BasicAuthenticationHttpClientFactory httpClientFactory = new BasicAuthenticationHttpClientFactory(mockHost.getHostName(), "captain", "");
	}
}
